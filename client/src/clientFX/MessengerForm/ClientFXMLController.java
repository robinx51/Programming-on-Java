package clientFX.MessengerForm;

import clientFX.ClientObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class ClientFXMLController implements Initializable {
    public boolean statusApp;
    private ClientObject client;

    @FXML
    private Label ConnStatusLabel;
    @FXML
    private Button ReconnButton;
    
    public void SetClient(ClientObject client) {
        this.client = client;
    }
    
    public void SetConn(boolean status) {
        Platform.runLater(() -> {
            if (status) {
                statusApp = true;
                ConnStatusLabel.setText("Подключение к серверу установлено");
                ConnStatusLabel.setStyle("-fx-text-fill: #37da7e;");
                ReconnButton.setVisible(false);
            } else {
                statusApp = false;
                ConnStatusLabel.setText("Подключение к серверу не установлено");
                ConnStatusLabel.setStyle("-fx-text-fill: red;");
                ReconnButton.setVisible(true);
            }
        });
    }
    
    public void MessageBox(String title, String message, String type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            switch (type){
                case "info":
                    alert.setAlertType(AlertType.INFORMATION);
                    break;
                case "confirm":
                    alert.setAlertType(AlertType.CONFIRMATION);
                    break;
                case "warning":
                    alert.setAlertType(AlertType.WARNING);
                    break;
                case "error":
                    alert.setAlertType(AlertType.ERROR);
                    break;
                case "none":
                    alert.setAlertType(AlertType.NONE);
                    break;
            }
            alert.setTitle(title);
            alert.setHeaderText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Tooltip tooltip = new Tooltip("Это кнопка для выполнения какого-то действия");
    }
    
    @FXML
    private void HandleReconnButton(ActionEvent event) {
        //ClientObject client = new ClientObject(this);
        client.ConnectToServer();
    }
    
}
