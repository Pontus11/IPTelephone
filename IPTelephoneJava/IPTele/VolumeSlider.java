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
GUI Class for the volume slider takes care of its functionality
 */
public class VolumeSlider extends VBox {
    private Slider volumeSlider;
    private PhoneGuiFX phoneGuiFX;
    private VolumeButton volumeButton;
    private float savedValue;

    /*
    Standard constructor for the VolumeSlider.
    Sets the style, sizes and padding for the volume slider.
     */
    public VolumeSlider(PhoneGuiFX gui) {
        phoneGuiFX = gui;

        volumeSlider = new Slider(0,100,100);
        volumeSlider.getStyleClass().add("slider");
        volumeSlider.setOrientation(Orientation.VERTICAL);
        volumeSlider.setMaxSize(10,100);
        volumeSlider.setPadding(new Insets(0,0,0,0));

        /*
        The ChangeListener for the volume slider.
         */
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> value, Number oldValue, Number newValue) {
                if(!volumeButton.getTurnedOn() && newValue.floatValue() > 0) {
                    volumeButton.setAsTurnedOn();
                }else if(volumeButton.turnedOn && newValue.floatValue() == 0) {
                    volumeButton.setAsTurnedOff();
                }
                gui.changeCallVolume(newValue.floatValue());
            }
        });

        volumeSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
            double value = volumeSlider.getValue() ;
            return setSliderStyle(value);
        }, volumeSlider.valueProperty()));

        volumeButton = new VolumeButton();

        this.setPrefWidth(40);
        this.setMinWidth(40);
        this.setMaxWidth(40);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #202020");
        this.getChildren().addAll(volumeSlider, volumeButton);
    }

    /*
    Method to get the savedValue.
     */
    private float getSavedValue() {
        return savedValue;
    }

    /*
    Method to set the savedValue.
     */
    private void setSavedValue(float value) {
        savedValue = value;
    }

    /*
    Method to set the slider style. Depending on how far the user has moved the slider certain amounts of the slider is made green.
     */
    private String setSliderStyle(double value) {
        String gradient = "-slider-track-color: linear-gradient(to top, lawngreen 0.0%, green " + value + "%, derive(-fx-control-inner-background, -5%) " + value + "%); ";

        return gradient;
    }

    /*
    GUI class for the volume button.
    Sets the volume buttons size, image and style.
     */
    public class VolumeButton extends HBox {
        private Button volumeButton;
        private boolean turnedOn = true;
        private ImageView imageView;
        public VolumeButton() {
            volumeButton = new Button();

            Image image = new Image(new File("speakerTurnedOn.png").toURI().toString());
            imageView = new ImageView(image);
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            imageView.setPreserveRatio(true);
            volumeButton.setGraphic(imageView);

            /*
            Sets the EventHandler so that the user can set the volume to 0 or back to the last saved value.
             */
            volumeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(turnedOn) {
                        setSavedValue((float)volumeSlider.getValue());
                        volumeSlider.setValue(0);
                    }else {
                        volumeSlider.setValue(getSavedValue());
                    }
                }
            });

            volumeButton.setStyle("-fx-background-color: transparent");
            this.setStyle("-fx-background-color: transparent");
            this.getChildren().addAll(volumeButton);
        }

        /*
        Method used to set the speaker symbol to turned off.
         */
        private void setAsTurnedOff() {
            Image image = new Image(new File("speakerTurnedOff.png").toURI().toString());
            imageView.setImage(image);
            turnedOn = false;
        }

        /*
        Method used to set the volume image to turned on.
         */
        private void setAsTurnedOn() {
            Image image = new Image(new File("speakerTurnedOn.png").toURI().toString());
            imageView.setImage(image);
            turnedOn = true;
        }
        /*
        Method used to get the turnedOn boolean.
         */
        private boolean getTurnedOn() {
            return turnedOn;
        }
    }
}
