package clientFX.MessengerForm;

import clientFX.ClientObject;
import java.net.URL;
import java.util.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class MessengerFormController implements Initializable {
    public boolean statusApp;
    private ClientObject client;
    private int activeFriendId = -1;
    private final SortedMap<Integer, UserProfile> FriendMap = new TreeMap<>();
    
    @FXML
    private Button BackButton; @FXML 
    private Label HelloLabel;
    
    @FXML
    private AnchorPane noChatPanel; @FXML
    private AnchorPane ChatPanel; @FXML
    private Label NameFriend; @FXML
    private Label FriendImg; @FXML
    private ScrollPane MessagesScrollPane; @FXML
    private VBox MessagesList; @FXML
    private TextField MessageField; @FXML
    private Button SendButton;
    
    @FXML
    private VBox FriendList;
    
    private final class UserProfile extends Pane {
        private final String name;
        private final int id;
        private final boolean isOnline;
        private int newMessagesCount;
        
        private ObservableList<Node> messages;

        private Button button;
        private Label status;
        private final Label nameLabel;
        private Label newMessagesLabel;
        
        private final class Message extends Pane {
            Label messageLabel;

            public Message(String message, boolean isDerived) {
                this.setId("MessagePane");
                HBox msgBox = new HBox();
                messageLabel = new Label(message);
                messageLabel.setWrapText(true);
                msgBox.getChildren().add(messageLabel);
                if (isDerived) {
                    messageLabel.setId("DerivedMessage");
                } else {
                    messageLabel.setId("SendedMessage");
                    msgBox.setAlignment(Pos.CENTER_RIGHT);
                    msgBox.setPrefWidth(381);
                }
                getChildren().add(msgBox);
            }
        }
        
        public UserProfile(String name, int id, boolean isOnline) {
            this.name = name;
            this.id = id;
            this.isOnline = isOnline;
            newMessagesCount = 0;
            
            messages = FXCollections.observableArrayList();
            
            button = new Button("");
            status = new Label("");
            nameLabel = new Label(name);
            newMessagesLabel = new Label("" + newMessagesCount);
            
            this.setId("UserProfile");
            button.setId("UserProfileButton");
            if (id == 0) status.setId("UserProfileGroup");
            else SetStatus(this.isOnline);
            nameLabel.setId("UserProfileName");
            newMessagesLabel.setId("UserProfileCount");
            newMessagesLabel.setVisible(false);
            
            newMessagesLabel.setLayoutX(210);
            newMessagesLabel.setLayoutY(3);
            status.setLayoutY(3);
            
            getChildren().addAll(status, nameLabel, newMessagesLabel, button);           
            button.setOnAction((ActionEvent event) -> {
                SetActive(name, id, status, messages);
                ChatPanel.toFront();
                newMessagesLabel.setVisible(false);
                newMessagesCount = 0;
                button.setId("UserProfileActiveButton");
                //button.setDisable(true);
            }); 
        }
        
        public void SetInactive() {
            button.setId("UserProfileButton");
            button.setDisable(false);
        }
        public void SetStatus(boolean IsOnline) {
            if (IsOnline) status.setId("UserProfileOnline");
            else status.setId("UserProfileOffline");
        }
        public boolean GetStatus() { return isOnline; }
        public int GetId() { return id; }
        public String GetName() { return name; }
        
        public Node NewMessage(String strMessage, boolean isDerived) {
            Message msg = new Message(strMessage, isDerived);
            if (isDerived && activeFriendId != id) {
                newMessagesLabel.setText("" + ++newMessagesCount);
                newMessagesLabel.setVisible(true);
            }
            messages.add(msg);
            
            return msg;
        }
    }
    
    public String GetName(int id) {
        return FriendMap.get(id).GetName();
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
    public void NewMessage(String message, int id, boolean isDerived) {
        Platform.runLater(() -> { 
            Node Message = FriendMap.get(id).NewMessage(message, isDerived);
            if (activeFriendId == id) MessagesList.getChildren().add(Message);
        });
    }
    private void SetActive(String name, int id, Label Status, ObservableList<Node> messages) {
        if (activeFriendId != -1) SetInactive(activeFriendId);
        activeFriendId = id;
        NameFriend.setText(name);
        FriendImg.setId(Status.getId());
        MessagesList.getChildren().setAll(messages);
    }
    private void SetInactive(int id) {
        FriendMap.get(id).SetInactive();
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
        
        NewUser("Общий чат", 0, true);
        
        MessagesList.heightProperty().addListener((observable, oldValue, newValue) -> {
            MessagesScrollPane.setVvalue(1.0);
        });
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
            MessagesList.getChildren().clear();
            SetInactive(activeFriendId);
            activeFriendId = -1;
        }
    } @FXML
    private void HandleSendButton(ActionEvent event) {
        if (MessageField.getText().length() >= 1) {
            String message = MessageField.getText();
            try {
                client.SendMessage("@" + activeFriendId + "|" + message);
                NewMessage(message, activeFriendId, false);
                MessageField.setText("");
                SendButton.setId("sendButton");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } 
        }
    }
    
}
