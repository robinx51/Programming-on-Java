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
        
        while (!server.s.isClosed())
        {
            String str = console.nextLine();
            if(str.equals("stop") || str.equals("стоп"))
            { 
                server.StopServer();
                break;
            }
            else if (str.equals("start")|| str.equals("старт"))
                StartServer();
            //else server.HandleMessage("");
        }
    }
    
}
