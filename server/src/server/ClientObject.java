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
    
    public void SetId(int id)       { ClientId = id; }
    public void SetName(String name){ ClientName = name; }
    public int GetId()              { return ClientId; }
    public String GetName()         { return ClientName; }
    
    public void HandleMessage(String message) {
        if (message.contains("|")) {
            String options, text;
            {
                String[] strings = message.split("\\|");
                options = strings[0];
                text = strings[1];
            }
            switch (options.substring(0, 1)) {
                case "@" -> {
                    options = options.substring(1);
                    //if (options.contains(",")) {} // Нереализованные беседы
                    int TargetId = Integer.parseInt(options);
                    switch (TargetId) {
                        case 0 -> {
                            text = "@0:" + ClientId + "|" + text;
                            server.BroadcastMessage(ClientId, text);
                        } default -> {
                            text = "&" + ClientId + "|" + text;
                            server.SendMessage(TargetId, text);
                        }
                    }
                } 
                case "#" -> {
                    options = options.substring(1);
                    switch (options) {
                        case "log" -> server.LogClient(text, this);
                        case "reg" -> {
                            if(server.RegClient(text))
                                server.SendMessage(ClientId, "#reg|accept");
                            else server.SendMessage(ClientId, "#reg|reject");
                        } default -> System.err.println("Unprocessed request: " + text);
                    }
                }
                default -> System.err.println("Unprocessed request: " + message);
            }
        } else if ("/online".equals(message)) {
            server.SendMessage(ClientId, server.GetOnlineClients(ClientId));
        } else {
            System.err.println("Unprocessed request: " + message);
        }
    }
    
    @Override
    public void run() {
        try {
            OUTER:
            while (!socket.isClosed() && !server.serverSocket.isClosed()) {
                String message = in.readLine();
                System.out.println("Клиент " + ClientId + ": " + message);
                if (message == null) {
                    break;
                } else {
                    switch (message) {
                        case "/online" -> {
                            server.SendMessage(ClientId, server.GetOnlineClients(ClientId) );
                        } case "/close" -> {
                            break OUTER;
                        } case "#leave" -> {
                            server.LeaveClient(ClientId);
                        } default -> HandleMessage(message);
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("IOException from client " + ClientId);
        }
        finally {
            System.out.println("Закрытие клиента " + ClientId + "...");
            server.CloseClient(ClientId);
        }
    }
}