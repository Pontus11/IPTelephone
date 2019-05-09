import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Created by pontu on 2019-02-07.
 */
 /*
 Gui class for the close button and its functionality.
 Takes care of displaying the close button in the top right corner so that the user can
 close the program.
 */
public class CloseButton extends HBox {
    private final String toolbarColor = "-fx-background-color: #171717;";
    private final String hoverColor = "-fx-background-color: #898686;";

    private boolean canExit = false;
    private PhoneGuiFX phoneGuiFX;
    private IPTele ipTele;

    /*
    Standard constructor for initiating the close button "X" and color.
    Also sets the action event if the user clicks to button to make sure any ongoing phone calls
    or out going call requests are canceled or incoming calls are declined.
    Also takes care of the display of the button when the user hovers or clicks the button.
     */
    CloseButton(PhoneGuiFX phoneGuiFX, IPTele ipTele) {
        this.phoneGuiFX = phoneGuiFX;
        this.ipTele = ipTele;

        Button closeButton = new Button("X");
        closeButton.setStyle(toolbarColor);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (phoneGuiFX.getPhoneState() == PhoneState.INCALL) {
                    phoneGuiFX.stopInCall();
                } else if (phoneGuiFX.getPhoneState() == PhoneState.INCOMINGCALL) {
                    ipTele.respondToCall(false);
                }else if(phoneGuiFX.getPhoneState() == PhoneState.CALLING) {
                    phoneGuiFX.stopCalling();
                }
                Platform.exit();
                System.exit(0);
            }
        });
        closeButton.setOnMousePressed(e -> closeButton.setTextFill(Color.BLACK));
        closeButton.setOnMouseReleased(e -> closeButton.setTextFill(Color.WHITE));
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(hoverColor));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(toolbarColor));
        closeButton.setTextFill(Color.WHITE);
        this.getChildren().add(closeButton);
    }
}