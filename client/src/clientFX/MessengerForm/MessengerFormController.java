package clientFX.MessengerForm;

import clientFX.ClientObject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.SortedMap;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class MessengerFormController implements Initializable {
    public boolean statusApp;
    private ClientObject client;
    private int activeFriendId = 0;
    private SortedMap<Integer, String> list;
    
    @FXML
    private Button BackButton; @FXML 
    private Label HelloLabel; @FXML
    private Label ConnStatusLabel; @FXML
    private Button ReconnButton;
    
    @FXML
    private AnchorPane noChatPanel; @FXML
    private AnchorPane ChatPanel; @FXML
    private TextField MessageField; @FXML
    private Button SendButton;
    
    @FXML
    private AnchorPane NoUsersPanel;
    
    public void SetClient(ClientObject client, String name) {
        this.client = client;
        HelloLabel.setText("Привет, " + name + '!');
    }
    
    public void SetConn(boolean status) {
        Platform.runLater(() -> {
            if (status) {
                statusApp = true;
                ConnStatusLabel.setVisible(false);
                ReconnButton.setVisible(false);
            } else {
                statusApp = false;
                ConnStatusLabel.setVisible(true);
                ReconnButton.setVisible(true);
            }
        });
    }
    
    public void MessageBox(String title, String message, String type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            switch (type){
                case "info" -> alert.setAlertType(AlertType.INFORMATION);
                case "confirm" -> alert.setAlertType(AlertType.CONFIRMATION);
                case "warning" -> alert.setAlertType(AlertType.WARNING);
                case "error" -> alert.setAlertType(AlertType.ERROR);
                case "none" -> alert.setAlertType(AlertType.NONE);
            }
            alert.setTitle(title);
            alert.setHeaderText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tooltip tooltip = new Tooltip("Отправить сообщение");
        SendButton.setTooltip(tooltip);
        tooltip = new Tooltip("Вернуться на страницу авторизации");
        BackButton.setTooltip(tooltip);
    }
    
    @FXML
    private void HandleBackButton(ActionEvent event) {
        client.mainThread.OpenAuth();
    } @FXML
    private void HandleReconnButton(ActionEvent event) {
        client.ConnectToServer();
    } @FXML
    private void HandleMessageField(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            SendButton.fire();
    } @FXML
    private void HandleSendButton(ActionEvent event) { 
        //MessageBox("Нажал на кнопку","Отправишь сообщение потом","info");
        if (MessageField.getText().length() > 1) {
            String message = MessageField.getText();
            try {
                client.SendMessage("@" + activeFriendId + "|" + message);
                MessageField.setText("");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } 
        }
    }
    
}
