package server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
        
public class ServerObject extends Thread {
    private final SortedMap<Integer, ClientObject> onlineList;
    private final SortedMap<Integer, ClientObject> offlineList;
    public ServerSocket serverSocket;
    private Connection ConDB;
    
    public ServerObject() {
        onlineList = new TreeMap<>();
        offlineList = new TreeMap<>();
        ConnectToDB();
    }
    
    public void HandleMessage(String message) {
        try {
            if (message.contains("|")) {
                String options, text;
                {
                    String[] strings = message.split("\\|");
                    options = strings[0];
                    text = strings[1];
                }
                if (options.startsWith("@")) {
                    options = options.substring(1);
                    int TargetId = Integer.parseInt(options);
                    switch (TargetId) {
                        case 0 -> {
                            text = "&0|" + 0 + ':' + text;
                            BroadcastMessage(0, text);
                        } default -> {
                            text = "%" + text;
                            SendMessage(TargetId, text);
                        }
                    }
                } else if ("/close".equals(options) && text.matches("[0-9]+")) {
                    int TargetId = Integer.parseInt(text);
                    CloseClient(TargetId);
                }
            } else 
                System.out.println("Необработанный запрос: " + message);
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public String GetOnlineClients(int SenderId){
        if(onlineList.size() < 2)
            return("/online|null");
        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (Map.Entry<Integer, ClientObject> client : onlineList.entrySet()) {
            int id = client.getKey();
            if (id != SenderId && id > 0) {
                String name = client.getValue().GetName();
                ids.add("" + id);
                names.add(name);
            }
        }
        String message = "/online|" + String.join(",", names) + ":" + String.join(",", ids);
        return message;
    }
    
    private void ConnectToDB() {
        try
        {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/messenger";
            
            Properties authorization = new Properties();
            authorization.put("user", "postgres");
            authorization.put("password", "123");

            ConDB = DriverManager.getConnection(url, authorization);
            
            System.out.println("Соединение с базой данных установлено");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error accessing database!");
        }
    }
    private Boolean IsLoginExist(String login) {
        String query = "select is_login_exist(?)";
        try{
            PreparedStatement statement = ConDB.prepareStatement(query);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
        } catch (SQLException e)
        {   System.err.println("Error accessing database!"); }
        return false;
    }
    public Boolean RegClient(String message) {
        String[] data = message.split(" "); // "name login password"
        if (IsLoginExist(data[1]))
            return false;
        
        String query = "insert into users(name, login, password) values(?, ?, ?)";
        try{
            PreparedStatement statement = ConDB.prepareStatement(query);
            statement.setString(1, data[0]);
            statement.setString(2, data[1]);
            statement.setString(3, data[2]);
            statement.executeUpdate();
            
            System.out.println("Пользователь " + data[0] + " успешно зарегистрирован.");
            return true;
        } catch (SQLException e)
        {   System.err.println("Error accessing database!"); }
        return false;
    }
    public Boolean LogClient(String message, ClientObject client) {
        String[] data = message.split(" "); // "login password"
        String query = "select * from check_login(?, ?)";
        int id = 0;
        try {
            PreparedStatement statement = ConDB.prepareStatement(query);
            statement.setString(1, data[0]);
            statement.setString(2, data[1]);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt(1); // Чтение значения из первого столбца результата
                String name = resultSet.getString(2);
                if (onlineList.containsKey(id)) {
                    SendMessage(client.GetId(), "#log|online");
                    return false;
                } else if (id != 0) {
                    offlineList.remove(client.GetId());
                    ChangeIdClient(client, id);
                    client.SetName(name);
                    onlineList.put(id, client);
                    SendMessage(id, "#log|" + name);
                    if (onlineList.size() > 1)
                        BroadcastMessage(id, "#new|" + name + ":" + id);
                } else { 
                    SendMessage(client.GetId(), "#log|reject");
                    id = 0;
                }
            }
        } catch (SQLException e)
        {   System.err.println("Error accessing database!"); }
        return id != 0;
    }
    
    public void StopServer() {
        try {
            for (Map.Entry<Integer, ClientObject> client : onlineList.entrySet()) {
                ClientObject clientObj = client.getValue();
                clientObj.socket.close();
            } for (Map.Entry<Integer, ClientObject> client : offlineList.entrySet()) {
                ClientObject clientObj = client.getValue();
                clientObj.socket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
            this.interrupt();
        } catch(IOException ex) {
            System.err.println("Ошибка при закрытии сокета сервера");
        }
    }
    public void CloseClient(int id) {
        ClientObject client;
        if (onlineList.containsKey(id)) {
            client = onlineList.get(id);
            onlineList.remove(id);
        } else {
            client = offlineList.get(id);
            offlineList.remove(id);
        }
        try {
            if (!client.socket.isClosed()) {
                client.socket.close();
                BroadcastMessage(id, "#leave|" + id);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    public void LeaveClient(int id) {
        BroadcastMessage(id, "#leave|" + id);
        ClientObject client = onlineList.get(id);
        onlineList.remove(id);
        
        int port = client.socket.getPort();
        ChangeIdClient(client, port);
        offlineList.put(port, client);
    }
    public void ChangeIdClient (ClientObject client, int newId) {
        int oldId = client.GetId();
        client.SetId(newId);
        System.out.println("Смена id клиента " + oldId + " -> " + newId);
    }

    public void SendMessage(int id, String message) {
        try {
            if (onlineList.containsKey(id)) onlineList.get(id).out.println(message);
            else offlineList.get(id).out.println(message);
            System.out.println("@" + id + ": " + message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
    }
    public void BroadcastMessage(int SenderId, String message) {
        try {
            for (Map.Entry<Integer, ClientObject> client : onlineList.entrySet()) {
                int id = client.getKey();
                if (id != SenderId && id > 0) {
                    ClientObject clientObj = client.getValue();
                    clientObj.out.println(message);
                    System.out.println("@" + id + ": " + message);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    @Override
    public void run()
    {
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
            System.out.println("Сервер запущен");
            
            while (!this.isInterrupted()) {
                Socket socket = serverSocket.accept();
                try {
                    int port = socket.getPort();
                    ClientObject client = new ClientObject(socket, port, this);
                    client.start();
                    System.out.println("Новый клиент, порт: " + port );
                    offlineList.put(port, client);
                }
                catch (IOException e) {
                    System.out.println("Ошибка создания ClientObject внутри цикла в ServerObject");
                    socket.close();
                }
            }
        } catch (SocketException e) {
            System.out.println("Сокет сервера был закрыт");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        finally {
            StopServer();
            System.out.println("Сервер был остановлен");
        }
    }
}