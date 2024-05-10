package clientFX.LoginForm;

import clientFX.Client;
import clientFX.ClientObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class LoginFormController implements Initializable {
    public boolean statusApp;
    private ClientObject client;
    private Client mainThread;

    @FXML
    private Button LoginButton; @FXML
    private TextField LoginFieldLog; @FXML
    private TextField PasswordFieldLog; @FXML
    private Button LoginPanelButton; @FXML
    private Button RegPanelButton; @FXML
    private TextField NameFieldReg; @FXML
    private TextField LoginFieldReg; @FXML
    private PasswordField PasswordFieldReg; @FXML
    private PasswordField ConfirmPasswordFieldReg; @FXML
    private Button RegButton; @FXML
    private AnchorPane LoginPanel; @FXML
    private AnchorPane RegPanel; @FXML
    private Label ConnStatusLabel; @FXML
    private Button ReconnButton;
    
    public void SetClient(ClientObject client, Client mainTh) {
        this.client = client;
        mainThread = mainTh;
    }
    public void SetClient(ClientObject client) {
        this.client = client;
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
    
    public void ClearLoginPanel() {
        LoginFieldLog.setText("");
        PasswordFieldLog.setText("");
    }
    
    public void SuccessfulReg() {
        Platform.runLater(() -> {
            LoginFieldLog.setText(LoginFieldReg.getText());
            LoginPanelButton.fire();
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
        //Tooltip tooltip = new Tooltip("Это кнопка для выполнения какого-то действия");
    }
    
    @FXML
    private void HandleTextBoxLog(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            LoginButton.fire();
    }
    @FXML
    private void HandleLoginButton(ActionEvent event) {
        String login = LoginFieldLog.getText();
        String pass = PasswordFieldLog.getText();
        if (login.length() > 2 && pass.length() > 2) {
            String message = "#log|" + login + ' ' + pass;
            client.SendMessage(message);
        } else {
            MessageBox("Ошибка ввода","Минимальная длина каждого поля - 3 символа","warning");
        }
    }
    @FXML
    private void HandleTextBoxReg(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            RegButton.fire();
    }
    @FXML
    private void HandleRegButton(ActionEvent event) {
        String login = LoginFieldReg.getText();
        String name = NameFieldReg.getText();
        String pass = PasswordFieldReg.getText();
        String confPass = ConfirmPasswordFieldReg.getText();
        
        if (login.length() > 2 && name.length() >= 2 && pass.length() > 2 && confPass.length() > 2 ) {
            if (!login.matches(".*\\W+.*") && name.matches("[a-zA-Zа-яА-Я]+")) {
                if (pass.equals(confPass)) {
                    String message = "#reg|" + name + ' ' + login + ' ' + pass;
                    client.SendMessage(message);
                } else {
                    MessageBox("Ошибка ввода","Пароль не совпадает","warning");
                }
            } else {
                MessageBox("Ошибка ввода","Имя и Логин должны содержать только буквы и цифры","warning");
            }
        } else {
            MessageBox("Ошибка ввода","Минимальная длина каждого поля - 3 символа","warning");
        }
    }
    
    @FXML
    private void HandleLogPanelButton(ActionEvent event) {
        LoginPanel.toFront();
        NameFieldReg.setText("");
        LoginFieldReg.setText("");
        PasswordFieldReg.setText("");
        ConfirmPasswordFieldReg.setText("");
}
    @FXML
    private void HandleRegPanelButton(ActionEvent event) {
        RegPanel.toFront();
        ClearLoginPanel();
    }

    @FXML
    private void HandleReconnButton(ActionEvent event) {
        mainThread.ConnectToServer();
    }
    
    @FXML
    private void HandleFastLogin(ActionEvent event) {
        String message = "#log|robinx51 123";
        client.SendMessage(message);
    } @FXML
    private void HandleMeffsLogin(ActionEvent event) {
        String message = "#log|Meffs 123";
        client.SendMessage(message);
    }
    
}
