import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;

/**
 * Created by pontu on 2018-03-20.
 */
/*
GUI class for the display and functionality of the MicrophoneSlides.
 */
public class MicrophoneSlider extends VBox {
    private Slider microphoneSlider;
    private PhoneGuiFX phoneGuiFX;
    private MicrophoneButton microphoneButton;
    private float savedValue;

    /*
    Standard constructor for the microphone slider.
    Sets the size, padding and style of the slider.
     */
    public MicrophoneSlider(PhoneGuiFX gui) {
        phoneGuiFX = gui;
        microphoneSlider = new Slider(0,100,100);
        microphoneSlider.getStyleClass().add("slider");
        microphoneSlider.setOrientation(Orientation.VERTICAL);
        microphoneSlider.setMaxSize(10,100);
        microphoneSlider.setStyle("-fx-background-color: #202020;" + "-fx-border-color: #202020");
        microphoneSlider.setPadding(new Insets(0,0,0,0));

        /*
        ChangeListener for the slider.
        When the user slides the slider the changed() function is called.
        Sets the volume of the microphone to the new slider value selected by user.
         */
        microphoneSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> value, Number oldValue, Number newValue) {
                if(!microphoneButton.getTurnedOn() && newValue.floatValue() > 0) {
                    microphoneButton.setAsTurnedOn();
                }else if(microphoneButton.turnedOn && newValue.floatValue() == 0) {
                    microphoneButton.setAsTurnedOff();
                }
                gui.changeMicrophoneVolume(newValue.intValue());
            }
        });

        microphoneSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
            double value = microphoneSlider.getValue() ;
            return setSliderStyle(value);
        }, microphoneSlider.valueProperty()));

        microphoneButton = new MicrophoneButton();

        this.setPrefWidth(40);
        this.setMinWidth(40);
        this.setMaxWidth(40);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #202020");
        this.getChildren().addAll(microphoneSlider, microphoneButton);
    }

    /*
    Getter method for the savedValue.
     */
    private float getSavedValue() {
        return savedValue;
    }

    /*
    Set method for the savedValue
     */
    private void setSavedValue(float value) {
        savedValue = value;
    }

    /*
    Method to set the slider style.
     */
    private String setSliderStyle(double value) {
        String gradient = "-slider-track-color: linear-gradient(to top, lawngreen 0.0%, green " + value + "%, derive(-fx-control-inner-background, -5%) " + value + "%); ";

        return gradient;
    }

    /*
    GUI class for the microphone button.
    Takes care of the display and functionality of the button.
    Lets the user mute or un-mute the microphone by clicking the button.
    If the user un-mutes the microphone is going back to the most recent volume setting used which is stored
    in the savedValue.
    */
    public class MicrophoneButton extends HBox {
        private Button microphoneButton;
        private boolean turnedOn = true;
        private ImageView imageView;

        /*
        Basic constructor for the microphone button.
        Sets the size, image and style of the button.
        Sets the functionality of the button.
         */
        public MicrophoneButton() {
            microphoneButton = new Button();

            Image image = new Image(new File("microphoneTurnedOn.png").toURI().toString());
            imageView = new ImageView(image);
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            imageView.setPreserveRatio(true);
            microphoneButton.setGraphic(imageView);

            /*
            Event handler for when the user clicks the microphone button.
            If microphone is on, it gets muted.
            If microphone is off the volume is set to the most recently used volume stored in the saveValue
            variable.
             */
            microphoneButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(turnedOn) {
                        setSavedValue((float)microphoneSlider.getValue());
                        microphoneSlider.setValue(0);
                    }else {
                        microphoneSlider.setValue(getSavedValue());
                    }
                }
            });

            microphoneButton.setStyle("-fx-background-color: transparent");
            this.setStyle("-fx-background-color: transparent");
            this.getChildren().addAll(microphoneButton);
        }

        /*
        Method used to change the image of the microphone when its turned off.
         */
        private void setAsTurnedOff() {
            Image image = new Image(new File("microphoneTurnedOff.png").toURI().toString());
            imageView.setImage(image);
            turnedOn = false;
        }

        /*
        Method used to change the image of the microphone when its turned on.
         */
        private void setAsTurnedOn() {
            Image image = new Image(new File("microphoneTurnedOn.png").toURI().toString());
            imageView.setImage(image);
            turnedOn = true;
        }
        /*
        Method used to get the turned on boolean.
        If true it means the microphone is on.
        If false it means the microphone is muted.
         */
        private boolean getTurnedOn() {
            return turnedOn;
        }
    }
}
