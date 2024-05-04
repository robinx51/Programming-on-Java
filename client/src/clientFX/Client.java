package clientFX;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application {
    ClientFXMLController fxml;
    ClientObject client;
    
    public void MessageBox(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientFXML.fxml"));
        Parent root = loader.load();
        
        fxml = loader.getController();
        client = new ClientObject(fxml);
        client.ConnectToServer();
        
        Scene scene = new Scene(root);
        stage.setTitle("Messenger");
        stage.setScene(scene);
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Closing app...");
                fxml.statusApp = false;
                client.CloseClient();
            }
        });
        
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
