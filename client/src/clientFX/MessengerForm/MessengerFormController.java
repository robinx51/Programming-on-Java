package clientFX.MessengerForm;

import clientFX.ClientObject;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MessengerFormController implements Initializable {
    public boolean statusApp;
    private ClientObject client;
    private int activeFriendId = -1;
    private SortedMap<Integer, UserProfile> FriendMap = new TreeMap<>();
    
    @FXML
    private Button BackButton; @FXML 
    private Label HelloLabel;
    
    @FXML
    private AnchorPane noChatPanel; @FXML
    private AnchorPane ChatPanel; @FXML
    private Label NameFriend; @FXML
    private Label FriendImg; @FXML
    private VBox MessagesList; @FXML
    private Label TestLabel; @FXML
    private TextField MessageField; @FXML
    private Button SendButton;
    
    @FXML
    private AnchorPane NoUsersPanel;@FXML
    private VBox FriendList;
    
    
    public final class UserProfile extends Pane {
        private final String name;
        private final int id;
        private final boolean isOnline;
        private int NewMessagesCount;
        
        private LinkedList<Label> Messages;

        private Button button;
        private Label Status;
        private final Label NameLabel;
        private Label NewMessagesLabel;
        
        public UserProfile(String name, int id, boolean isOnline) {
            this.name = name;
            this.id = id;
            this.isOnline = isOnline;
            NewMessagesCount = 0;
            
            Messages = new LinkedList<>();
            
            button = new Button("");
            Status = new Label("");
            NameLabel = new Label(name);
            NewMessagesLabel = new Label("" + NewMessagesCount);
            
            this.setId("UserProfile");
            button.setId("UserProfile");
            if (id == 0) Status.setId("UserProfileGroup");
            else SetStatus(this.isOnline);
            NameLabel.setId("UserProfileName");
            NewMessagesLabel.setId("UserProfileCount");
            NewMessagesLabel.setVisible(false);
            
            NewMessagesLabel.setLayoutX(210);
            NewMessagesLabel.setLayoutY(3);
            Status.setLayoutY(3);
            
            getChildren().addAll(Status, NameLabel, NewMessagesLabel, button);           
            button.setOnAction((ActionEvent event) -> {
                SetActive(name, id, Status, Messages);
                ChatPanel.toFront();
                NewMessagesLabel.setVisible(false);
                NewMessagesCount = 0;
            }); 
        }
        
        public void SetStatus(boolean IsOnline) {
            if (IsOnline) Status.setId("UserProfileOnline");
            else Status.setId("UserProfileOffline");
        }
        public boolean GetStatus() { return isOnline; }
        public int GetId() { return id; }
        
        public void NewMessage(String message) {
            NewMessagesLabel.setText("" + ++NewMessagesCount);
            NewMessagesLabel.setVisible(true);
            //Messages.add(DerivedMessage);
            
            Messages.add(new Label(name + ": " + message));
            
        }
    }
    
    public void NewUser(String name, int id, boolean IsOnline) {
        Platform.runLater(() -> {
            if (!FriendMap.containsKey(id)) {
                UserProfile Alex = new UserProfile(name, id, IsOnline);
                FriendMap.put(id, Alex);
                FriendList.getChildren().add(Alex);
            } else {
                FriendMap.get(id).SetStatus(true);
            }
        });
    }
    public void LeaveUser(int id) {
        Platform.runLater(() -> { FriendMap.get(id).SetStatus(false); });
    }
    public void NewMessage(String message, int id) {
        Platform.runLater(() -> { 
            FriendMap.get(id).NewMessage(message); 
        });
    }
    
    private void SetActive(String name, int id, Label Status, LinkedList<Label> messages) {
        activeFriendId = id;
        NameFriend.setText(name);
        FriendImg.setId(Status.getId());
        MessagesList.getChildren().addAll(messages);
    }
    public void SetClient(ClientObject client, String name) {
        this.client = client;
        HelloLabel.setText("Привет, " + name + '!');
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
        
        TestLabel.setWrapText(true);
        TestLabel.setId("MessageDerived");
        
        NewUser("Общий чат", 0, true);
    }
    
    @FXML
    private void HandleBackButton(ActionEvent event) {
        client.mainThread.OpenAuth();
    } @FXML
    private void HandleReconnButton(ActionEvent event) {
        client.ConnectToServer();
    } @FXML
    private void HandleMessageField(KeyEvent event) {
        if (MessageField.getText().length() >= 1) 
            SendButton.setId("sendButtonActive");
        else SendButton.setId("sendButton");
        if (event.getCode() == KeyCode.ENTER)
            SendButton.fire();
    } @FXML
    private void HandleMessageTypedField(KeyEvent event) {
        if (MessageField.getText().length() >= 1) 
            SendButton.setId("sendButtonActive");
        else SendButton.setId("sendButton");
    } @FXML
    private void HandleMessagePanel(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            noChatPanel.toFront();
            activeFriendId = -1;
        }
    } @FXML
    private void HandleSendButton(ActionEvent event) { 
        //MessageBox("Нажал на кнопку","Отправишь сообщение потом","info");
        if (MessageField.getText().length() >= 1) {
            String message = MessageField.getText();
            try {
                client.SendMessage("@" + activeFriendId + "|" + message);
                MessageField.setText("");
                SendButton.setId("sendButton");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } 
        }
    }
    
}
