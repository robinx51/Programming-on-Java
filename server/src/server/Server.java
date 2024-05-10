package server;

import java.util.Scanner;

public class Server {

    private static final ServerObject server = new ServerObject();   
    
    private static void StartServer()
    {
        if (server.isAlive() )
            System.out.println("Сервер уже запущен");
        else server.start();
    }
    
    public static void main(String[] args) {
        StartServer();
        Scanner console = new Scanner(System.in);
        
        OUTER:
        while (!server.s.isClosed()) {
            String str = console.nextLine();
            switch (str) {
                case "stop", "стоп" -> {
                    server.StopServer();
                    break OUTER;
                }
                case "start", "старт" -> StartServer();
                default -> server.HandleMessage(str);
            }
        }
    }
    
}
