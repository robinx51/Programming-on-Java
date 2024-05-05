package clientFX;

import clientFX.LoginForm.ClientFXMLController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application {
    ClientFXMLController fxml;
    ClientObject client;
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginForm/ClientFXML.fxml"));
        Parent root = loader.load();
        
        fxml = loader.getController();
        client = new ClientObject(fxml);
        client.ConnectToServer();
        fxml.SetClient(client);
        
        Scene scene = new Scene(root);
        stage.setTitle("Messenger");
        String icon = this.getClass().getResource("icons/messenger.png").toExternalForm();
        stage.getIcons().add(new Image(icon));
        stage.setResizable(false);
        stage.setScene(scene);
        
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
