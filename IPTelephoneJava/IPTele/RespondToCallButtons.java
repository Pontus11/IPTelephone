import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

/**
 * Created by pontu on 2019-02-07.
 */
/*
Gui class for respond to call buttons.
Lets the user click accept or decline when there is an incoming call so that a call can be started or declined.
*/
public class RespondToCallButtons extends FlowPane {
    private final String hoverColor = "-fx-background-color: #898686;";
    private final String centerColor = "-fx-background-color: #222222;";

    private Button acceptButton;
    private Button declineButton;
    private PhoneGuiFX phoneGuiFX;

    /*
    Standard constructor for RespondToCallButtons.
    Creates an button with "Accept" on it and one with "Decline".
    Sets their styling and EventHandlers.
     */
    RespondToCallButtons(PhoneGuiFX phoneGuiFX) {
        this.phoneGuiFX = phoneGuiFX;
        acceptButton = new Button("Accept");
        declineButton = new Button("Decline");

        /*
        Calls PhoneGuiFX.stopIncomingCall() with "true" as the parameter which accepts the incoming call by
        stopping the incoming call animations and functionality and starting a new call.
         */
        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                phoneGuiFX.stopIncomingCall(true);
            }
        });

        /*
        Event handler that calls PhoneGuiFX.stopIncomingCall() with "false" as the parameter. Stops the incoming
        call and does not start a call with the user that is calling.
         */
        declineButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                phoneGuiFX.stopIncomingCall(false);
            }
        });

        acceptButton.setStyle(centerColor);
        acceptButton.setTextFill(Color.WHITE);

        /*
        Event handler for when the user is hovering the accept button.
         */
        acceptButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                acceptButton.setStyle(hoverColor);
                phoneGuiFX.getCallButton().stopIncomingFlasher();
                phoneGuiFX.getCallButton().circleStyle("-fx-background-color: lawngreen;");
            }
        });

        /*
        Event handler for when the user stops hovering the accept button.
         */
        acceptButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                acceptButton.setStyle(centerColor);
                if (phoneGuiFX.getPhoneState() == PhoneState.INCOMINGCALL) {
                    phoneGuiFX.getCallButton().startIncomingFlasher();
                }
            }
        });
        acceptButton.setOnMousePressed(e -> acceptButton.setTextFill(Color.BLACK));
        acceptButton.setOnMouseReleased(e -> acceptButton.setTextFill(Color.WHITE));

        declineButton.setStyle(centerColor);
        declineButton.setTextFill(Color.WHITE);

        /*
        Event handler for when the user starts hovering the decline button.
         */
        declineButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                declineButton.setStyle(hoverColor);
                phoneGuiFX.getCallButton().stopIncomingFlasher();
                phoneGuiFX.getCallButton().circleStyle("-fx-background-color: red;");
            }
        });

        /*
        Event handler for when the user stops hovering the decline button.
         */
        declineButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                declineButton.setStyle(centerColor);
                if (phoneGuiFX.getPhoneState() == PhoneState.INCOMINGCALL) {
                    phoneGuiFX.getCallButton().startIncomingFlasher();
                }
            }
        });
        declineButton.setOnMousePressed(e -> declineButton.setTextFill(Color.BLACK));
        declineButton.setOnMouseReleased(e -> declineButton.setTextFill(Color.WHITE));
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(acceptButton);
        this.getChildren().add(declineButton);
        this.setMaxWidth(200);
    }
}

