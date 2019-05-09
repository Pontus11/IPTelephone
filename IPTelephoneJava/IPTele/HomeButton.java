import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Created by pontu on 2019-02-07.
 */
    /*
    Gui class for the homeButton and its functionality.
    The home button exists for a potential expansion of the software but does currently not
    do anything if pressed by the user.
     */
public class HomeButton extends HBox {
    private final String toolbarColor = "-fx-background-color: #171717;";
    private final String hoverColor = "-fx-background-color: #898686;";

    /*
    Standard constructor for the home button.
    Creates a button with the text home.
    Sets the colors of the button and the hover and press style changes.
     */
    HomeButton() {
        Button homeButton = new Button("Home");
        homeButton.setStyle(toolbarColor);
        homeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        homeButton.setOnMousePressed(e -> homeButton.setTextFill(Color.BLACK));
        homeButton.setOnMouseReleased(e -> homeButton.setTextFill(Color.WHITE));
        homeButton.setOnMouseEntered(e -> homeButton.setStyle(hoverColor));
        homeButton.setOnMouseExited(e -> homeButton.setStyle(toolbarColor));
        homeButton.setTextFill(Color.WHITE);
        this.getChildren().add(homeButton);
    }
}