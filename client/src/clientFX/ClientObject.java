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
    
    private final LoginFormController LoginForm;
    private MessengerFormController MessengerForm;
    public final Client mainThread;
    private boolean IsLogined;
    
    public ClientObject(LoginFormController fxml, Client client) {
        this.LoginForm = fxml;
        this.mainThread = client;
        IsLogined = false;
    }
    public void SetForm(MessengerFormController form) {
        MessengerForm = form;
    }
    public void SetAuth(boolean IsLogined) {
        this.IsLogined = IsLogined;
        if (!IsLogined) MessengerForm = null;
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
                out.println("/close");
                socket.close();
                MessengerForm = null;
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
            
            LoginForm.SetConn(true);
            LoginForm.SetClient(this);
            
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
                switch (options.substring(0, 1)) {
                    case "&" -> {
                        options = options.substring(1);
                        int TargetId = Integer.parseInt(options);
                        MessengerForm.NewMessage(text, TargetId, true);
                    }
                    case "@" -> { // P: "@to:from|message"
                        options = options.substring(1);
                        int TargetId, FromId;
                        {
                            String[] data = options.split(":");
                            TargetId = Integer.parseInt(data[0]);
                            FromId = Integer.parseInt(data[1]); 
                        }
                        text = MessengerForm.GetName(FromId) + ": " + text;
                        MessengerForm.NewMessage(text, TargetId, true);
                    }
                    case "#" -> {
                        options = options.substring(1);
                        if (null != options) switch (options) {
                            case "log" -> {
                                switch (text) {
                                    case "online" -> LoginForm.MessageBox("Получен ответ от сервера", "Пользователь с таким логином уже в сети", "warning");
                                    case "reject" -> LoginForm.MessageBox("Получен ответ от сервера", "Ошибка входа", "error");
                                    default -> mainThread.OpenMessenger(text);
                                }
                            }
                            case "reg" -> {
                                if ("accept".equals(text)) {
                                    LoginForm.MessageBox("Получен ответ от сервера", "Успешная регистрация!", "confirm");
                                    LoginForm.SuccessfulReg();
                                }
                                else if ("reject".equals(text))
                                    LoginForm.MessageBox("Получен ответ от сервера", "Ошибка регистрации", "error");
                            }
                            case "new" -> {
                                String[] data = text.split(":");
                                MessengerForm.NewUser(data[0], Integer.parseInt(data[1]), true);
                            }
                            case "leave" -> MessengerForm.LeaveUser(Integer.parseInt(text));
                            default -> {
                                
                            }
                        }
                    }
                    case "/" -> {
                        options = options.substring(1);
                        if ("online".equals(options)) {
                            if (!"null".equals(text)) {
                                String[] data = text.split(":");
                                String[] names = data[0].split(",");
                                String[] ids = data[1].split(",");
                                for (int i = 0; i < names.length; i++) {
                                    MessengerForm.NewUser(names[i],Integer.parseInt(ids[i]),true);
                                }
                            }
                        }
                    }
                }
            } else if (message.startsWith("%")) {
                LoginForm.MessageBox("Сообщение от сервера", message.substring(1), "info");
            } else 
                System.err.println("Unprocessed request: " + message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run()
    {
        try {
            OUTER:
            while (!socket.isClosed()) {
                String msg = in.readLine();
                if (null == msg) {
                    break;
                } else {
                    switch (msg) {
                        case "/close" -> {
                            break OUTER;
                        }
                        default -> HandleMessage(msg);
                    }
                }
                System.out.println("Server: " + msg);
            }
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (LoginForm.statusApp) LoginForm.SetConn(false);
            if (MessengerForm != null ) mainThread.ClosedServer();
            try{
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}