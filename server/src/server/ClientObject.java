package server;

import java.io.*;
import java.net.*;

public class ClientObject extends Thread {
    private final ServerObject server;
    public  final Socket socket;
    private final BufferedReader in;
    public  final PrintWriter out;
    private int ClientId;
    private String ClientName;
    
    public ClientObject(Socket socket, int port, ServerObject server) throws IOException {
        this.server = server;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        ClientId = port;
        ClientName = "$NONAME$";
    }
    
    public void SetId(int id) {
        ClientId = id;
    }
    public void SetName(String name) {
        ClientName = name;
    }
    public String GetName(){
        return ClientName;
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
                        text = "&0|" + ClientId + ':' + text;
                        server.BroadcastMessage(ClientId, text);
                    }
                    default -> {
                        text = '&' + ClientId + '|' + text;
                        server.SendMessage(TargetId, text);
                    }
                }
            }
            else if (options.startsWith("#")) {
                options = options.substring(1);
                switch (options) {
                    case "log" -> {
                        if(server.LogClient(text, this))
                            server.SendMessage(ClientId, "#log|accept");
                        else server.SendMessage(ClientId, "#log|reject");
                    }
                    case "reg" -> {
                        if(server.RegClient(text))
                            server.SendMessage(ClientId, "#reg|accept");
                        else server.SendMessage(ClientId, "#reg|reject");
                    }
                    default -> System.out.println("Необработанный запрос: " + text);
                }
            }
            else {
                System.out.println("Необработанный запрос: " + message);
            }
        }
        else {
            System.out.println("Необработанный запрос: " + message);
        }
    }
    
    @Override
    public void run() {
        try {
            OUTER:
            while (!socket.isClosed() && !server.s.isClosed()) {
                String message = in.readLine();
                if (null == message) {
                    break;
                } else {
                    switch (message) {
                        case "/online" -> {
                            server.SendMessage(ClientId, server.GetOnlineClients(ClientId) );
                        }
                        case "/close" -> {
                            break OUTER;
                        }
                        default -> HandleMessage(message);
                    }
                }
                System.out.println("Клиент " + ClientId + ": " + message);
            }
        }
        catch (IOException e) {
            System.err.println("IOException from client " + ClientId);
        }
        finally {
            System.out.println("Закрытие клиента " + ClientId + "...");
            server.LeaveClient(ClientId);
        }
    }
}