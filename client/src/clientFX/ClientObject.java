package clientFX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientObject extends Thread {
        private static Socket socket;
        private static BufferedReader in;
        private static PrintWriter out;
        ClientFXMLController fxml;
        
        public ClientObject(ClientFXMLController fxml) {
            this.fxml = fxml;
        }
        
        public void CloseClient() {
            try{
                if (socket != null &&!socket.isClosed()) {
                    //out.println("/close");
                    socket.close();
                    this.interrupt(); 
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void ConnectToServer() {
            try{
                // Передаем null в getByName()
                InetAddress addr = InetAddress.getByName("localhost");

                System.out.println("addr = " + addr);
                socket = new Socket(addr, 8080);
                System.out.println("socket = " + socket);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Вывод автоматически Output выталкивается PrintWriter'ом.
                out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
                fxml.SetConn(true);
            } catch (UnknownHostException e){
                System.out.println(e.getMessage());
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            
            start();
        }
        
        private void HandleMessage() {
            
        }
        
        @Override
        public void run()
        {
            try{
                while(!socket.isClosed())
                {
                    String msg = in.readLine();
                    if ("/close".equals(msg)) 
                        break;
                    else HandleMessage();
                    System.out.println("Сообщение от сервера: " + msg);
                }
            } catch(IOException ex) {
                System.out.println(ex.getMessage());
            } finally {
                if (fxml.statusApp) fxml.SetConn(false);
                try{
                    if (!socket.isClosed())
                        socket.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }