package clientFX;

import clientFX.LoginForm.LoginFormController;
import clientFX.MessengerForm.MessengerFormController; 
import java.io.IOException;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application {
    private LoginFormController LoginForm;
    private MessengerFormController MessengerForm;
    private ClientObject client;
    private Stage authStage;
    private Stage MessengerStage;
    
    public void ConnectToServer() {
        client = new ClientObject(LoginForm, this);
        client.ConnectToServer();
    }
    
    public void OpenMessenger(String name) {
        Platform.runLater(() -> {
            MessengerStage = new Stage();
            
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MessengerForm/MessengerForm.fxml"));
                AnchorPane root = loader.load();

                MessengerForm = loader.getController();
                client.SetForm(MessengerForm);
                MessengerForm.SetClient(client, name);
                
                MessengerStage.setTitle("Messenger");
                String icon = this.getClass().getResource("icons/messenger.png").toExternalForm();
                MessengerStage.getIcons().add(new Image(icon));
                MessengerStage.setResizable(false);
                MessengerStage.setScene(new Scene(root));
                
                MessengerStage.setOnCloseRequest((WindowEvent event) -> {
                    System.out.println("Closing app...");
                    client.CloseClient();
                });

                authStage.hide();
                client.SetAuth(true);
                client.SendMessage("/online");
                MessengerStage.show();
                LoginForm.ClearLoginPanel();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });
    }
    
    public void OpenAuth() {
        Platform.runLater(() -> {
            MessengerStage.close();
            client.SendMessage("#leave");
            client.SetAuth(false);
            authStage.show();
        });
    }
    public void ClosedServer() {
        Platform.runLater(() -> {
            MessengerForm.MessageBox("Потеряно соединение с сервером", "Пожалуйста, подключитесь заново и войдите в профиль", "error");
            MessengerStage.close();
            
            client.SetAuth(false);
            authStage.show();
        });
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginForm/LoginForm.fxml"));
        Parent root = loader.load();
        authStage = stage;
        
        LoginForm = loader.getController();
        ConnectToServer();
        LoginForm.SetClient(client, this);
        
        stage.setTitle("Messenger");
        String icon = this.getClass().getResource("icons/messenger.png").toExternalForm();
        stage.getIcons().add(new Image(icon));
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            System.out.println("Closing app...");
            client.CloseClient();
        });
        
        stage.show();
    }
        
    public static void main(String[] args) {
        launch(args);
    }
}
