package com.robinx.lab6_server;

import java.io.*;
import java.net.*;

public class ClientObject extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    public final PrintWriter out;
    private final int ClientId;
    private final Lab6 form;
    private final ServerObject server;
    
    public ClientObject(Socket s, int num, Lab6 form, ServerObject server) throws IOException {
        socket = s;
        ClientId = num;
        this.form = form;
        this.server = server;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Включаем автоматическое выталкивание:
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        start(); // вызываем run()
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                String str = in.readLine();
                if (str.equals("END"))
                    break;
                form.NewMessage("Client " + ClientId + ": " + str);
                //out.println(str);
            }
        }
        catch (IOException e) {
            form.NewMessage("IO Exception from Client " + ClientId);
        }
        finally {
            try {
                form.NewMessage("Closing client " + ClientId + "...");
                socket.close();
                form.LeaveClient(--server.ClientCounter);
            }
            catch (IOException e) {
                form.NewMessage("Socket not closed from Client" + ClientId);
            } 
        } 
    }
}