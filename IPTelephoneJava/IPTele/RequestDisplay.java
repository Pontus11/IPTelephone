import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/*
GUI class for the request display.
 */
public class RequestDisplay extends ContactDisplay {

    /*
    Standard constructor for the RequestDisplay.
    Creates a new AcceptOrDeclineButton and sets the padding and alignments.
     */
    public RequestDisplay(User user, PhoneGuiFX gui) {
        super(user, gui);
        AcceptOrDeclineButton button = new AcceptOrDeclineButton();
        contactPictureBox.setPadding(new Insets(12,0,0,0));
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(button);
    }

    /*
    GUI class for the accept or decline buttons.
     */
    public class AcceptOrDeclineButton extends HBox {
        private Button acceptFriendButton;
        private Button declineFriendButton;
        /*
        Standard constructor for the AcceptOrDeclineButton.
        creates new buttons and sets their style.
        Adds a spacer box.
        Sets the EventHandlers for when the user presses accept or decline.
        When user clicks the accept or decline button the
        PhoneGuiFX.acceptFriendRequest() or PhoneGuiFX.declineFriendRequest() is called.
         */
        AcceptOrDeclineButton() {
            acceptFriendButton = new Button("Accept");
            declineFriendButton = new Button("Decline");
            acceptFriendButton.getStyleClass().add("acceptOrDeclineButton");
            declineFriendButton.getStyleClass().add("acceptOrDeclineButton");

            HBox spacerBox2 = new HBox();
            spacerBox2.setMinWidth(10);
            spacerBox2.setMaxWidth(10);

            VBox acceptOrDeclineButton = new VBox();

            acceptOrDeclineButton.getChildren().addAll(acceptFriendButton,declineFriendButton);


            acceptFriendButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    phoneGuiFX.acceptFriendRequest(contactDisplay);
                }
            });
            declineFriendButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    phoneGuiFX.declineFriendRequest(contactUser.getUsername());
                }
            });
            acceptFriendButton.setTextFill(Color.WHITE);
            acceptFriendButton.setOnMousePressed(e -> acceptFriendButton.setTextFill(Color.BLACK));
            acceptFriendButton.setOnMouseReleased(e -> acceptFriendButton.setTextFill(Color.WHITE));

            declineFriendButton.setTextFill(Color.WHITE);
            declineFriendButton.setOnMousePressed(e -> declineFriendButton.setTextFill(Color.BLACK));
            declineFriendButton.setOnMouseReleased(e -> declineFriendButton.setTextFill(Color.WHITE));

            removeOnlineLabel();
            this.getChildren().addAll(acceptOrDeclineButton, spacerBox2);
        }
    }
}