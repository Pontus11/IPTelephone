import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Created by pontu on 2018-03-15.
 */
/*
Class used to display and take care of the functionality of the chatWindow.
This class takes care of displaying the input area, chat area and the scroll pane.
 */
public class ChatWindow extends VBox {
    private VBox chatArea;
    private PhoneGuiFX pgui;
    private TextArea inputArea;
    private Text textHolder;
    private ScrollPane scroll;
    private int messageStandardwidth = 425;
    private double inputAreaStandardSize = 30;
    private ContactDisplay contactDisplay;

    /*
    Standard constructor that takes care of initiating the chat area, input area, scroll pane, their respective sizes
    and the event handler for when the user sends a written message by pressing enter.
     */
    public ChatWindow(PhoneGuiFX phoneGuiFX) {
        pgui = phoneGuiFX;
        textHolder = new Text();
        chatArea = new VBox();
        chatArea.setPrefSize(500,395);
        chatArea.setMaxWidth(475);
        chatArea.getStyleClass().add("chatArea");


        inputArea = new TextArea();
        inputArea.setPrefSize(450, inputAreaStandardSize);
        inputArea.setMinHeight(inputAreaStandardSize);
        inputArea.setMaxWidth(500);
        inputArea.getStyleClass().add("inputArea");
        inputArea.setWrapText(true);

        scroll = new ScrollPane(chatArea);
        chatArea.heightProperty().addListener(
                (observable, oldValue, newValue) -> {
                    chatArea.layout();
                    scroll.setVvalue( 1.0d );
                }
        );
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("scrollPane");

        inputArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    String inputString = inputArea.getText();
                    addMessageToChatWindow(inputString, pgui.getUserImage(), pgui.getUsername());
                    contactDisplay.addMessageToChatHistory(new Message(pgui.getUsername(), contactDisplay.getName(), inputString));
                    phoneGuiFX.sendMessageToRecipient(inputString);
                    inputArea.clear();
                    event.consume();
                }
            }
        });
        textHolder.strokeWidthProperty();
        textHolder.textProperty().bind(inputArea.textProperty());
        textHolder.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                int rows = (inputArea.getText().length()-1)/73;
                inputArea.setPrefHeight(inputAreaStandardSize + (inputAreaStandardSize * rows));
            }
        });
        this.setMaxSize(500,450);
        this.setPadding(new Insets(10,0,0,0));
        this.getChildren().addAll(scroll, inputArea);
    }

    /*
    Method used to set the size of the chat area and input area.
     */
    public void setSize(int width, int height, int inputHeight) {
        chatArea.setPrefSize(width, height);
        inputArea.setPrefSize(width, inputHeight);
    }

    /*
    Method used to set the contact display.
     */
    public void setContactDisplay(ContactDisplay contactDisplay) {
        this.contactDisplay = contactDisplay;
    }

    /*
    Method used to get the contact display.
     */
    public ContactDisplay getContactDisplay() {
        return contactDisplay;
    }

    /*
    Method used to send a message to the chat area.
     */
    public void addMessageToChatWindow(String messageText, Image image, String username) {
        ChatMessageDisplay message = new ChatMessageDisplay(messageText, image, username);
        Platform.runLater(()-> chatArea.getChildren().add(message));
    }

    /*
    Method used to load the chat history between the user and the currently selected contact.
    Retrieves the chatHistory by calling ContactDisplay.getChatHistory().
    Adds all the messages to the chat area.
     */
    public void loadChatHistory() {
        ArrayList<Message> chatHistory = contactDisplay.getChatHistory();
        for(Message message: chatHistory) {
            System.out.println(message.getMessage());
            if(contactDisplay.getName().equals(message.getFrom())) {
                addMessageToChatWindow(message.getMessage(), contactDisplay.getContactImage(), contactDisplay.getUser().getUsername());
            }else {
                addMessageToChatWindow(message.getMessage(), pgui.getUserImage(), pgui.getUsername());
            }
        }
    }

    /*
    Method used to clear the chat area.
     */
    public void clearChat() {
        Platform.runLater(()-> chatArea.getChildren().clear());
    }

    /*
    Gui class for the chat messages displayed in the chat area.
    Takes care of how each message should be displayed.
    Sets the picture of the sender, name of sender, the message, sizes of the message, padding etc.
     */
    private class ChatMessageDisplay extends HBox {
        private Circle userPicture;
        private TextArea messageArea;
        private Label userNameLabel;
        private double height;
        public ChatMessageDisplay(String messageString, Image img, String username) {
            height = (messageString.length()/61) * 15 + inputAreaStandardSize;
            System.out.println(height + " " + messageString.length());
            userPicture = new Circle();
            userPicture.setRadius(25);
            userPicture.setFill(new ImagePattern(img));
            HBox userPictureBox = new HBox();
            userPictureBox.getChildren().add(userPicture);
            userPictureBox.setPadding(new Insets(5,0,0,0));

            messageArea = new TextArea(messageString);
            messageArea.setWrapText(true);
            messageArea.setEditable(false);
            messageArea.setPrefWidth(425);
            messageArea.setMaxWidth(425);
            messageArea.setPrefHeight(height);
            messageArea.setMinHeight(height);
            messageArea.getStyleClass().add("messageArea");

            VBox nameAndMessageBox = new VBox();
            HBox usernameBox = new HBox();
            HBox spacerBox = new HBox();
            spacerBox.setPrefWidth(50);
            usernameBox.setAlignment(Pos.CENTER);
            usernameBox.setPrefWidth(500);
            userNameLabel = new Label(username);
            userNameLabel.getStyleClass().add("usernameLabel");
            userNameLabel.setStyle("-fx-background-color: #303030");
            userNameLabel.setPadding(new Insets(0,15,0,15));
            usernameBox.getChildren().addAll(userNameLabel,spacerBox);
            nameAndMessageBox.getChildren().addAll(usernameBox, messageArea);
            this.getStyleClass().add("message");
            this.getChildren().addAll(userPictureBox, nameAndMessageBox);
        }
    }
}