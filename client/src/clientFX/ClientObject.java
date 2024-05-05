package clientFX;

import clientFX.LoginForm.LoginFormController;
import clientFX.MessengerForm.MessengerFormController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Platform;

public class ClientObject extends Thread {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    private final LoginFormController fxml;
    private MessengerFormController MessengerForm;
    private final Client mainThread;

    public ClientObject(LoginFormController fxml, Client client) {
        this.fxml = fxml;
        this.mainThread = client;
    }
    public void setForm(MessengerFormController form) {
        MessengerForm = form;
    }
    public void SendMessage(String message) {
        Platform.runLater(() -> {
            if (!socket.isClosed())
                out.println(message);
        });
    }

    public void CloseClient() {
        try{
            if (socket != null && !socket.isClosed()) {
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

            socket = new Socket(addr, 8080);
            System.out.println("socket = " + socket);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Вывод автоматически Output выталкивается PrintWriter'ом.
            out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), true);
            fxml.SetConn(true);
            start();
        } catch (UnknownHostException e){
            System.out.println(e.getMessage());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    private void HandleMessage(String message) {
        try{
            if (message.contains("|")) {
                String options, text;
                {
                    String[] strings = message.split("\\|");
                    options = strings[0];
                    text = strings[1];
                }
                if (options.startsWith("&")) {
                    options = options.substring(1);
                    int TargetId = Integer.parseInt(options);
                    switch (TargetId) {
                        case 0 -> {
                            text = "&0|" + 0 + ':' + text;
                            // Общий диалог
                        }
                        default -> {
                            text = "&server|" + text;
                            // Личное сообщение
                        }
                    }
                } else if (options.startsWith("#")) {
                    options = options.substring(1);
                    if ("log".equals(options)) {
                        if ("reject".equals(text))
                            fxml.MessageBox("Получен ответ от сервера", "Ошибка входа", "error");
                        else
                            mainThread.OpenMessenger(text);
                    } else if ("reg".equals(options)) {
                        if ("accept".equals(text)) {
                            fxml.MessageBox("Получен ответ от сервера", "Успешная регистрация!", "confirm");
                            fxml.SuccessfulReg();
                        }
                        else if ("reject".equals(text))
                            fxml.MessageBox("Получен ответ от сервера", "Ошибка регистрации", "error");
                    }
                }
            } else if (message.startsWith("%")) {
                fxml.MessageBox("Сообщение от сервера", message.substring(1), "info");
            }
            else 
                System.err.println("Unprocessed request: " + message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run()
    {
        try{
            while(!socket.isClosed())
            {
                String msg = in.readLine();
                if (msg == null)
                    break;
                else if ("/close".equals(msg)) 
                    break;
                else HandleMessage(msg);
                System.out.println("Server: " + msg);
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