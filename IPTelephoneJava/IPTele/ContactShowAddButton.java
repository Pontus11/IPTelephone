import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
/*
GUI class for the display and functionality of the contactShowAddButton
 */
public class ContactShowAddButton extends HBox {
    private Button showAddButton;
    private PhoneGuiFX phoneGuiFX;

    private final String hoverColor = "-fx-background-color: #898686;";
    private final String centerColor = "-fx-background-color: #222222;";

    ContactShowAddButton(String buttonText, PhoneGuiFX gui) {
        phoneGuiFX = gui;
        showAddButton = new Button(buttonText);
        showAddButton.setStyle(centerColor);
        showAddButton.getStyleClass().add("showAddButton");
        showAddButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!phoneGuiFX.isAddPanelVisible()) {
                    phoneGuiFX.addAddPanel();
                    System.out.println("added");
                }else {
                    phoneGuiFX.removeAddPanel();
                    System.out.println("Removed");
                }
            }
        });
        showAddButton.setOnMousePressed(e -> showAddButton.setTextFill(Color.BLACK));
        showAddButton.setOnMouseReleased(e -> showAddButton.setTextFill(Color.WHITE));
        showAddButton.setOnMouseEntered(e -> showAddButton.setStyle(hoverColor));
        showAddButton.setOnMouseExited(e -> showAddButton.setStyle(centerColor));
        showAddButton.setTextFill(Color.WHITE);
        this.getChildren().add(showAddButton);
    }
}