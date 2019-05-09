import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Created by pontu on 2019-02-07.
 */
/*
Gui class for the add button and its functionality.
The AddButton is used for sending a friend request to another user.
This class also takes care of displaying instructions for the user regarding the status of the friendRequest.
*/
public class AddButton extends HBox {
    private final String hoverColor = "-fx-background-color: #898686;";
    private final String centerColor = "-fx-background-color: #222222;";
    private boolean addCooldown = false;
    private Button addButton;
    private PhoneGuiFX phoneGuiFX;
    private IPTele ipTele;

    /*
    Standard constructor for the AddButton class.
    Sets the size and color of the AddButton.
    Adds an onAction event which calls IPTele.sendFriendRequest() in order to send a friend request if the user input
    was valid.
     */
    AddButton(PhoneGuiFX phoneGuiFX, IPTele ipTele) {
        this.phoneGuiFX = phoneGuiFX;
        this.ipTele = ipTele;
        addButton = new Button("Send Friend Request");
        addButton.setStyle(centerColor);
        addButton.setPrefWidth(130);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if (!addCooldown) {
                        String nameString = phoneGuiFX.getFriendNameField().getText();
                        nameString.trim();
                        if (nameString != null && !nameString.equals("")) {
                            boolean friendAlreadyExists = phoneGuiFX.getContacts().containsKey(nameString);
                            if (!friendAlreadyExists) {
                                ipTele.sendFriendRequest(nameString);
                                showLabel("Request sent!", Color.LAWNGREEN);
                            } else {
                                showLabel("Already added", Color.DARKRED);
                            }
                        } else {
                            showLabel("Enter username", Color.DARKRED);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        addButton.setOnMousePressed(e -> addButton.setTextFill(Color.BLACK));
        addButton.setOnMouseReleased(e -> addButton.setTextFill(Color.WHITE));
        addButton.setOnMouseEntered(e -> addButton.setStyle(hoverColor));
        addButton.setOnMouseExited(e -> addButton.setStyle(centerColor));
        addButton.setTextFill(Color.WHITE);
        this.setPrefWidth(250);
        this.getChildren().add(addButton);
    }

    /*
    Method used to display the status of the friend request as a label if the user presses the AddButton.
    Label is only displayed for 3 seconds.
     */
    private void showLabel(String message, Color color) {
        Label tmpLabel = new Label(message);
        tmpLabel.setStyle(centerColor);
        tmpLabel.setTextFill(color);

        Timeline showDuration = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(0.0), e -> {
                    addCooldown = true;
                    this.getChildren().add(tmpLabel);
                }), new KeyFrame(javafx.util.Duration.seconds(3.0), e -> {
            this.getChildren().remove(tmpLabel);
            addCooldown = false;
        }));

        showDuration.playFromStart();
    }
}