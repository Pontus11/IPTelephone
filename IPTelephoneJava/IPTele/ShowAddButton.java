/**
 * Created by pontu on 2018-03-17.
 */
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/*
GUI class for the button that makes the top panel for adding new friends visible.
 */
public class ShowAddButton extends HBox {
    private Button showAddButton;
    private PhoneGuiFX phoneGuiFX;

    private final String hoverColor = "-fx-background-color: #898686;";
    private final String toolbarColor = "-fx-background-color: #171717;";

    /*
    Standard constructor for the ShowAddButton class.
    Sets the EventHandler to show or hide the add friend panel depending on its current visibility status.
     */
    ShowAddButton(String buttonText, PhoneGuiFX gui) {
        phoneGuiFX = gui;
        showAddButton = new Button(buttonText);
        showAddButton.setStyle(toolbarColor);
        showAddButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!phoneGuiFX.isAddPanelVisible()) {
                    phoneGuiFX.addAddPanel();
                }else {
                    phoneGuiFX.removeAddPanel();
                }
            }
        });
        showAddButton.setOnMousePressed(e -> showAddButton.setTextFill(Color.BLACK));
        showAddButton.setOnMouseReleased(e -> showAddButton.setTextFill(Color.WHITE));
        showAddButton.setOnMouseEntered(e -> showAddButton.setStyle(hoverColor));
        showAddButton.setOnMouseExited(e -> showAddButton.setStyle(toolbarColor));
        showAddButton.setTextFill(Color.WHITE);
        this.getChildren().add(showAddButton);
    }
}