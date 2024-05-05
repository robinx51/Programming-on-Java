package server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
        
public class ServerObject extends Thread {
    private final SortedMap<Integer, ClientObject> list;
    public ServerSocket s;
    Connection connection;
    
    public ServerObject()
    {
        list = new TreeMap<>();
        ConnectToDB();
    }
    
    public void HandleMessage(String message) {
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
                    }
                    default -> {
                        text = "%" + text;
                        SendMessage(TargetId, text);
                    }
                }
            } else if ("/close".equals(options) && text.matches("[0-9]+")) {
                int TargetId = Integer.parseInt(text);
                CloseClient(TargetId);
            }
        }
        else 
            System.out.println("Необработанный запрос: " + message);
    }
    
    public String GetOnlineClients(int SenderId){
        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if(list.size() == 1)
            return("/online|null");
        for (Map.Entry<Integer, ClientObject> client : list.entrySet()) {
            int id = client.getKey();
            if (id != SenderId) {
                String name = client.getValue().getName();
                ids.add("" + id);
                names.add(name);
            }
        }
        String message = String.join(",", ids) + ":" + String.join(",", names);
        return message;
    }
    
    private void ConnectToDB()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/messenger";
            
            // Создание свойств соединения с базой данных
            Properties authorization = new Properties();
            authorization.put("user", "postgres");
            authorization.put("password", "123");

            // Создание соединения с базой данных
            connection = DriverManager.getConnection(url, authorization);
            
            System.out.println("Соединение с базой данных установлено");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error accessing database!");
        }
    }
    private Boolean IsLoginExist(String login)
    {
        String query = "select is_login_exist(?)";
        try{
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
        } catch (SQLException e)
        {   System.err.println("Error accessing database!"); }
        return false;
    }
    public boolean RegClient(String message)
    {
        String[] data = message.split(" "); // "name login password"
        if (IsLoginExist(data[1]))
            return false;
        
        String query = "insert into users(name, login, password) values(?, ?, ?)";
        try{
            PreparedStatement statement = connection.prepareStatement(query);
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
    public boolean LogClient(String message, ClientObject client)
    {
        String[] data = message.split(" "); // "login password"
        String query = "select * from check_login(?, ?)";
        int id = 0;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, data[0]);
            statement.setString(2, data[1]);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt(1); // Чтение значения из первого столбца результата
                String name = resultSet.getString(2);
                if (id != 0) {
                    list.remove(client.GetId());
                    System.out.println("Смена id клиента " + client.GetId() + " -> " + id);
                    client.SetId(id);
                    client.SetName(name);
                    list.put(id, client);
                    if (list.size() > 1)
                        BroadcastMessage(id, "#new|" + name + ":" + id);
                }
            }
        } catch (SQLException e)
        {   System.err.println("Error accessing database!"); }
        return id != 0;
    }
    
    public void StopServer() 
    {
        try {
            for (Map.Entry<Integer, ClientObject> client : list.entrySet()) {
                ClientObject clientObj = client.getValue();
                clientObj.socket.close();
            }
            if (s != null && !s.isClosed())
                s.close();
            this.interrupt(); // Прерываем поток
        } catch(IOException ex) {
            System.err.println("Ошибка при закрытии сокета сервера");
        }
    }
    private void CloseClient(int id) {
        ClientObject client = list.get(id);
        try{
            
            if (!client.socket.isClosed()) {
                client.socket.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        
    }

    public void SendMessage(int id, String message)
    {
        list.get(id).out.println(message);
    }
    public void BroadcastMessage(int SenderId, String message)
    {
        for (Map.Entry<Integer, ClientObject> client : list.entrySet()) {
            int id = client.getKey();
            if (id != SenderId) {
                ClientObject clientObj = client.getValue();
                clientObj.out.println(message);
            }
        }
    }
    
    public void LeaveClient(int id)
    {
        try{
            if (!list.get(id).socket.isClosed())
                list.get(id).socket.close();
            BroadcastMessage(id, "#leave|" + id);
            list.remove(id);
        } catch (IOException ex) {
            System.err.println("Ошибка закрытия клиента");
        }
    }
    
    @Override
    public void run()
    {
        s = null;
        try {
            s = new ServerSocket(8080);
            System.out.println("Сервер запущен");
            
            while (!this.isInterrupted()) {
                Socket socket = s.accept();
                try {
                    int port = socket.getPort();
                    ClientObject client = new ClientObject(socket, port, this);
                    client.start();
                    System.out.println("Новый клиент " + port );
                    list.put(port, client);
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