import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/*
GUI class used for the display and functionality of the contact display box.
The contactDisplay is responsible for the display of a contact, such as the name, picture and online/offline label.
also stores the current chat history for the contact.
*/
public class ContactDisplay extends HBox {
    private Circle contactPictureCircle;
    private Label contactNameLabel;
    private HBox spacerBox;
    private ArrayList<Message> chatHistory;
    private Label onlineLabel;

    protected ContactDisplay contactDisplay;
    protected User contactUser;
    protected PhoneGuiFX phoneGuiFX;
    protected HBox contactPictureBox;

    /*
    Standard constructor.
    Sets the size and padding etc for the contact name, picture and online label.
     */
    public ContactDisplay(User user, PhoneGuiFX gui) {
        contactDisplay = this;
        contactUser = user;
        phoneGuiFX = gui;
        chatHistory = new ArrayList<>();

        contactNameLabel = new Label(contactUser.getUsername());
        contactNameLabel.setTextFill(Color.WHITE);
        contactNameLabel.getStyleClass().add("contactLabelStyle");

        contactPictureCircle = new Circle();
        contactPictureCircle.setRadius(12);
        contactPictureCircle.setFill(new ImagePattern(getContactImage()));
        contactPictureBox = new HBox();
        contactPictureBox.setPadding(new Insets(2,0,0,0));
        contactPictureBox.getChildren().add(contactPictureCircle);

        onlineLabel = new Label("");
        onlineLabel.setTextFill(Color.WHITE);
        onlineLabel.getStyleClass().add("contactLabelStyle");

        spacerBox = new HBox();
        HBox.setHgrow(spacerBox, Priority.ALWAYS);

        if(user.getLastKnownIp() != null) {
            setOnlineLabel();
        }else{
            setOfflineLabel();
        }

        this.getStyleClass().add("contactDisplay");
        this.getChildren().addAll(contactPictureBox, contactNameLabel, spacerBox, onlineLabel);
    }

    /*
    Overriden toString() method for the contactDisplay. Returns the contacts username.
     */
    @Override
    public String toString() {
        return contactUser.getUsername();
    }

    /*
    Method used to set the onlineLabel to show that a contact is online.
     */
    public void setOnlineLabel() {
        onlineLabel.setTextFill(Color.LAWNGREEN);
        onlineLabel.setText("Online");
    }
    /*
     Method used to set the onlineLabel to show that a contact is offline.
    */
    public void setOfflineLabel() {
        onlineLabel.setTextFill(Color.WHITE);
        onlineLabel.setText("Offline");
    }

    /*
    Method used to remove the online label.
     */
    protected void removeOnlineLabel() {
        this.getChildren().remove(onlineLabel);
    }

    /*
    Method used to get a contactDisplays username.
     */
    public String getName() {
        return contactUser.getUsername();
    }

    /*
    Method used to get the image of a contactDisplay.
     */
    public Image getContactImage() {
        byte[] imgData = contactUser.getProfilePicture();
        ByteArrayInputStream bais = new ByteArrayInputStream(imgData);
        Image image = new Image(bais);
        return image;
    }

    /*
    Method used to remove the contactDisplayClicked style from a contactDisplay when selection has been removed.
    Then adds the normal contactDisplay style back to the contactDisplay.
     */
    public void removeSelection() {
        contactDisplay.getStyleClass().remove("contactDisplayClicked");
        contactDisplay.getStyleClass().add("contactDisplay");
    }

    /*
    Method used to add the contactDisplayClicked style to a contactDisplay when its been selected by user.
     */
    public void selectContact() {
        contactDisplay.getStyleClass().add("contactDisplayClicked");
    }

    /*
    Method used to get User.
     */
    public User getUser() {
        return contactUser;
    }

    /*
    Method used to set the contactDisplays chat history.
     */
    public void setChatHistory(ArrayList<Message> chatHistory) {
        this.chatHistory = chatHistory;
    }

    /*
    Method used to add a message to a contactDisplays chat history.
     */
    public void addMessageToChatHistory(Message message) {
        chatHistory.add(message);
    }

    /*
    Method used to get the chat history of the contact display.
     */
    public ArrayList<Message> getChatHistory() {
        return chatHistory;
    }
}