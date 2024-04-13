package com.robinx.lab6_server;

import java.io.*;
import java.net.*;
import java.awt.Color;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JOptionPane;
        
public class ServerObject extends Thread {
    private static final int PORT = 8080;
    private final Lab6 form;
    public int ClientCounter = 0;
    //public ArrayList<ClientObject> list;
    
    private SortedMap<Integer, ClientObject> list;
    
    public ServerObject(Lab6 form)
    {
        this.form = form;
        list = new TreeMap<>();
    }
    
    public void SendMessage(int id, String message)
    {
        list.get(id).out.println(message); // ClientObject.PrintWriter.println(messsage;)
    }
    
    @Override
    public void run()
    {
        ServerSocket s = null;
        try {
            s = new ServerSocket(PORT);
            form.ServerStatusLabel.setText("Сервер запущен");
            form.ServerStatusLabel.setForeground(Color.green);
            
            int id = 0;
            while (true) {
                Socket socket = s.accept();
                try {
                    ClientCounter++;
                    ClientObject client = new ClientObject(socket, ++id, form, this);
                    form.NewClient(ClientCounter);
                    list.put(id, client);
                }
                catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Зашёл");
                    socket.close();
                    ClientCounter--;
                    form.LeaveClient(ClientCounter);
                }
            }
        } 
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Сервер выдал исключение");
            
            form.ServerStatusLabel.setText("Сервер не запущен");
            form.ServerStatusLabel.setForeground(Color.red);
        }
        finally {
            try{
                if (!s.isClosed())
                    s.close();
                JOptionPane.showMessageDialog(null, "Сервер отрубился");

                form.ServerStatusLabel.setText("Сервер не запущен");
                form.ServerStatusLabel.setForeground(Color.red);
            }
            catch(IOException ex)
            {
                JOptionPane.showMessageDialog(null, "Ошибка при закрытии сокета сервера");
            }
        }
    }
}
    /*public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        form = new Lab6();
        form.ServerStatusLabel.setText("Сервер запущен");
        form.ServerStatusLabel.setForeground(Color.green);
        form.setVisible(true);
        
        try {
            while (true) {
                Socket socket = s.accept();
                try {
                    new ClientObject(socket, ClientCounter);
                    ClientCounter++;
                    form.NewClient(ClientCounter);
                }
                catch (IOException e) {
                    socket.close();
                    ClientCounter--;
                }
            }
        }
        finally {
            s.close();
            form.ServerStatusLabel.setText("Сервер не запущен");
            form.ServerStatusLabel.setForeground(Color.red);
        }
    }*/
