package clientFX;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ClientFXMLController implements Initializable {
    public boolean statusApp;

    @FXML
    private Button LoginButton;
    @FXML
    private TextField LoginFieldLog;
    @FXML
    private TextField PasswordFieldLog;
    @FXML
    private Button LoginPanelButton;
    @FXML
    private Button RegPanelButton;
    @FXML
    private TextField NameFieldReg;
    @FXML
    private TextField LoginFieldReg;
    @FXML
    private PasswordField PasswordFieldReg;
    @FXML
    private Button RegButton;
    @FXML
    private AnchorPane LoginPanel;
    @FXML
    private AnchorPane RegPanel;
    @FXML
    private Label ConnStatusLabel;
    @FXML
    private Button ReconnButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusApp = true;
        // TODO
    }
    
    public void SetConn(boolean status) {
        if (status) {
            ConnStatusLabel.setText("Подключение к серверу установлено");
            ConnStatusLabel.setStyle("-fx-text-fill: #37da7e;");
            ReconnButton.setVisible(false);
        } else {
            ConnStatusLabel.setText("Подключение к серверу не установлено");
            ConnStatusLabel.setStyle("-fx-text-fill: orange red;");
            ReconnButton.setVisible(true);
        }
    }

    @FXML
    private void HandleLoginButton(ActionEvent event) {
        
    }

    @FXML
    private void HandleLogPanelButton(ActionEvent event) {
        LoginPanel.toFront();
        LoginPanelButton.setStyle("-fx-background-color: #37da7e;");
        RegPanelButton.setStyle("-fx-background-color: #778899;");
}

    @FXML
    private void HandleRegPanelButton(ActionEvent event) {
        RegPanel.toFront();
        RegPanelButton.setStyle("-fx-background-color: #37da7e;");
        LoginPanelButton.setStyle("-fx-background-color: #778899;");
    }

    @FXML
    private void HandleReconnButton(ActionEvent event) {
        ClientObject client = new ClientObject(this);
        client.ConnectToServer();
    }
    
}
