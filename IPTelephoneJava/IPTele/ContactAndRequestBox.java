import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by pontu on 2018-03-17.
 */
/*
GUI class used to take care of the display and functionality of the contact request box.
This class functions as a display box for all the contacts, contact requests and the contact button and request button.
Lets the user click the contacts or requests button to change between seeing current contacts and their online/offline
status or all current contact requests.
*/
public class ContactAndRequestBox extends HBox {
    private ContactButton contactButton;
    private FriendRequestButton requestButton;
    private PhoneGuiFX phoneGuiFX;

    /*
    Standard constructor.
    Initiates the Contact Button and Friend request button and then adds them to the contactAndRequestBox.
     */
    public ContactAndRequestBox(PhoneGuiFX gui) {
        phoneGuiFX = gui;
        contactButton = new ContactButton(phoneGuiFX);
        requestButton = new FriendRequestButton(phoneGuiFX);

        this.getChildren().addAll(contactButton, requestButton);
    }

    /*
    Sets the friend request count to be displayed on the request button.
     */
    public void setFriendRequestCount(int count) {
        requestButton.setText(count + " Requests");
    }


    /*
    Gui class for displaying the friend request button.
    Initiates a button and sets its text, color and style.
    Also sets the buttons action event for when the user clicks the button.
    Finally sets the display for the button for when the user clicks or hovers the button.
     */
    public class FriendRequestButton extends HBox {
        private Button friendRequestButton;
        private PhoneGuiFX phoneGuiFX;

        private final String hoverColor = "-fx-background-color: #898686;";
        private final String centerColor = "-fx-background-color: #222222;";

        FriendRequestButton(PhoneGuiFX gui) {
            phoneGuiFX = gui;
            friendRequestButton = new Button("Requests");
            friendRequestButton.setStyle(centerColor);
            friendRequestButton.getStyleClass().add("contactRequestButton");
            friendRequestButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gui.changeContactViewToRequests();
                    friendRequestButton.setStyle(hoverColor);
                    contactButton.setContactButtonStyle();
                }
            });
            friendRequestButton.setOnMousePressed(e -> friendRequestButton.setTextFill(Color.BLACK));
            friendRequestButton.setOnMouseReleased(e -> friendRequestButton.setTextFill(Color.WHITE));
            friendRequestButton.setOnMouseEntered(e -> friendRequestButton.setStyle(hoverColor));
            friendRequestButton.setOnMouseExited(e -> friendRequestButton.setStyle(centerColor));
            friendRequestButton.setTextFill(Color.WHITE);
            this.getChildren().add(friendRequestButton);
        }
        public void setFriendRequestButtonStyle() {
            friendRequestButton.setStyle(centerColor);
        }
        public void setText(String text) {
            Platform.runLater(()-> friendRequestButton.setText(text));
        }
    }

    /*
    Gui class for the contact button.
    Initiates a button and sets its text, color and style.
    Sets the action event for when the user clicks the button.
    Finally sets the display of the button when the user clicks or hovers the button.
     */
    public class ContactButton extends HBox {
        private Button contactButton;
        private PhoneGuiFX phoneGuiFX;

        private final String hoverColor = "-fx-background-color: #898686;";
        private final String centerColor = "-fx-background-color: #222222;";

        ContactButton(PhoneGuiFX gui) {
            phoneGuiFX = gui;
            contactButton = new Button("Contacts");
            contactButton.setStyle(centerColor);
            contactButton.getStyleClass().add("contactRequestButton");
            contactButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gui.changeContactViewToContacts();
                    contactButton.setStyle(hoverColor);
                    requestButton.setFriendRequestButtonStyle();
                }
            });
            contactButton.setOnMousePressed(e -> contactButton.setTextFill(Color.BLACK));
            contactButton.setOnMouseReleased(e -> contactButton.setTextFill(Color.WHITE));
            contactButton.setOnMouseEntered(e -> contactButton.setStyle(hoverColor));
            contactButton.setOnMouseExited(e -> contactButton.setStyle(centerColor));
            contactButton.setTextFill(Color.WHITE);
            this.getChildren().add(contactButton);
        }
        /*
        Method to set the style for the contact button.
         */
        public void setContactButtonStyle() {
            contactButton.setStyle(centerColor);
        }
    }
}
