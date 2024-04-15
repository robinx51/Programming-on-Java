package com.robinx.lab6_client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Lab6_client extends Thread {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    
    public Lab6_client() throws IOException
    {
        // Передаем null в getByName()
        InetAddress addr = InetAddress.getByName("localhost");
        
        System.out.println("addr = " + addr);
        socket = new Socket(addr, 8080);
        System.out.println("socket = " + socket);
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Вывод автоматически Output выталкивается PrintWriter'ом.
        out = new PrintWriter(new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream())), true);
        
        start();
    }
    
    private double Calculate(double[] value)
    {
        double sum = 0;
        
        for (double x = value[0]; x <= value[1]; x += value[2]) {
            double f = Math.sqrt(x) + Math.sqrt(x + value[2]);     // f(x) + f(x+h)
            double area = f * (value[2] /2);                         // 1/2( f(x) + f(x+h) )
            sum += area;
            if (x + value[2] > value[1]) 
            {
                double edge = Math.sqrt(x) + Math.sqrt(value[1]);
                double edgeArea = (value[1] - x) * edge / 2;
                sum += edgeArea;
            }
        }
        return sum;
    }
    
    public static void main(String[] args) throws IOException {
        new Lab6_client();
//        try {
//            Scanner console = new Scanner(System.in);
//            System.out.println("Введите сообщения серверу(\"END\" завершает работу):");
//            while(!socket.isClosed()){
//                String str = console.nextLine();
//                if(str.equals("END"))
//                    break;
//                out.println(str);
//            }
//            out.println("END");
//        }
//        finally {
//            System.out.println("closing...");
//            if (!socket.isClosed())
//                socket.close();
//        }
    }
    
    @Override
    public void run()
    {
        try{
            while(true)
            {
                String msg = in.readLine();
                if ("END".equals(msg)) 
                    break;
                else if(msg.startsWith("$")) // protocol: "@id|$  LowerBound UpperBound Step  LowerBound..."
                {
                    // Обработка сообщения и вычисление функции
                    try{
                        msg = msg.replace(',','.');
                        String[] arr = msg.substring(3).split("  ");
                        String str_result = "$";
                        for (String str : arr)
                        {
                            String[] str_value = str.split(" ");
                            if (str_value.length != 3)
                                break;
                            double[] value = new double[3];
                            for (int i = 0; i < 3; i++)
                                value[i] = Double.parseDouble(str_value[i]);

                            str_result = String.join(" ",str_result, "" + Calculate(value));
                        }
                        if (!"".equals(str_result)) 
                        {
                            out.println(str_result);
                            System.out.println("Вычисления завершены с результатами:" + str_result);
                        }
                        else System.out.println("Вычисления не были выполнены");
                    } 
                    catch (Throwable  ex) { 
                        System.out.println("Обработка вычислений завершена с ошибкой: " + ex.getMessage() ); 
                    }
                }
                else System.out.println("Сообщение от сервера: " + msg);
            }
        } catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        } finally {
            try{
                if (!socket.isClosed())
                    socket.close();
            }
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
}
