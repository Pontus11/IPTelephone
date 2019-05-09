import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;

/**
 * Created by pontu on 2019-02-07.
 */
  /*
  GUI class for the call button and its functionality
  */
public class CallButton extends VBox {
    private final String toolbarColor = "-fx-background-color: #171717;";

    private Button callButton;
    private Timeline callingFlasher;
    private Timeline incomingCallFlasher;
    private ScaleTransition scaleAnimation;

    private PhoneGuiFX phoneGuiFX;

    CallButton(PhoneGuiFX phoneGuiFX) {
        this.phoneGuiFX = phoneGuiFX;
        callButton = new Button();
        callButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (phoneGuiFX.getPhoneState() != PhoneState.AVAILABLE) {
                    if (phoneGuiFX.getPhoneState() == PhoneState.INCALL) {
                        phoneGuiFX.stopInCall();
                    }
                    if (phoneGuiFX.getPhoneState() == PhoneState.CALLING) {
                        phoneGuiFX.stopCalling();
                    }
                    if (phoneGuiFX.getPhoneState() == PhoneState.INCOMINGCALL) {
                        phoneGuiFX.stopIncomingCall(true);
                    }
                } else if (phoneGuiFX.getPhoneState() == PhoneState.AVAILABLE) {
                    phoneGuiFX.startCalling();
                }
            }
        });
        callButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (phoneGuiFX.getPhoneState() == PhoneState.CALLING) {
                    circleStyle("-fx-background-color: darkred;");
                } else if (phoneGuiFX.getPhoneState() == PhoneState.INCALL) {
                    circleStyle("-fx-background-color: darkred");
                } else if (phoneGuiFX.getPhoneState() == PhoneState.INCOMINGCALL) {
                    circleStyle("-fx-background-color: darkgreen");
                } else if (phoneGuiFX.getPhoneState() == PhoneState.AVAILABLE) {
                    circleStyle("-fx-background-color: darkgreen");
                }
            }
        });

        callButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (phoneGuiFX.getPhoneState() == PhoneState.CALLING) {
                    callingFlasher.pause();
                    circleStyle("-fx-background-color: red;");
                } else if (phoneGuiFX.getPhoneState() == PhoneState.INCALL) {
                    circleStyle("-fx-background-color: red;");
                } else if (phoneGuiFX.getPhoneState() == PhoneState.INCOMINGCALL) {
                    incomingCallFlasher.pause();
                    circleStyle("-fx-background-color: lawngreen");
                } else if (phoneGuiFX.getPhoneState() == PhoneState.AVAILABLE) {
                    circleStyle("-fx-background-color: lawngreen;");
                }
            }
        });
        callButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (phoneGuiFX.getPhoneState() == PhoneState.INCALL) {
                    circleStyle("-fx-background-color: blue");
                } else if (phoneGuiFX.getPhoneState() == PhoneState.CALLING) {
                    callingFlasher.play();
                } else if (phoneGuiFX.getPhoneState() == PhoneState.INCOMINGCALL) {
                    incomingCallFlasher.play();
                } else if (phoneGuiFX.getPhoneState() == PhoneState.AVAILABLE) {
                    circleStyle(toolbarColor);
                }
            }
        });

        circleStyle(toolbarColor);
        this.getChildren().add(callButton);
        this.setPadding(new Insets(20, 0, 0, 0));
        this.setAlignment(Pos.CENTER);
        Image image = new Image(new File("tele3.png").toURI().toString());
        ImageView iv = new ImageView(image);
        iv.setFitHeight(100);
        iv.setFitWidth(100);
        iv.setPreserveRatio(true);
        callButton.setGraphic(iv);

        scaleAnimation = new ScaleTransition();
        scaleAnimation.setNode(callButton);
        scaleAnimation.setDuration(Duration.seconds(2));
        scaleAnimation.setToX(0.85);
        scaleAnimation.setToY(0.85);
        scaleAnimation.setAutoReverse(true);
        scaleAnimation.setCycleCount(Animation.INDEFINITE);

        callingFlasher = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0.2), e -> {
            circleStyle("-fx-background-color: #0D9649;");
        }), new KeyFrame(javafx.util.Duration.seconds(0.4), e -> {
            circleStyle("-fx-background-color: #0D9654;");
        }), new KeyFrame(javafx.util.Duration.seconds(0.6), e -> {
            circleStyle("-fx-background-color: #0D965A;");
        }), new KeyFrame(javafx.util.Duration.seconds(0.8), e -> {
            circleStyle("-fx-background-color: #0D9665;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.0), e -> {
            circleStyle("-fx-background-color: #0D966B;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.2), e -> {
            circleStyle("-fx-background-color: #0D9673;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.4), e -> {
            circleStyle("-fx-background-color: #0D966B;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.6), e -> {
            circleStyle("-fx-background-color: #0D9665;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.8), e -> {
            circleStyle("-fx-background-color: #0D965A;");
        }), new KeyFrame(javafx.util.Duration.seconds(2.0), e -> {
            circleStyle("-fx-background-color: #0D9654;");
        }));
        callingFlasher.setCycleCount(Animation.INDEFINITE);

        incomingCallFlasher = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0.2), e -> {
            circleStyle("-fx-background-color: #441B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(0.4), e -> {
            circleStyle("-fx-background-color: #451B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(0.6), e -> {
            circleStyle("-fx-background-color: #461B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(0.8), e -> {
            circleStyle("-fx-background-color: #471B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.0), e -> {
            circleStyle("-fx-background-color: #481B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.2), e -> {
            circleStyle("-fx-background-color: #491B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.4), e -> {
            circleStyle("-fx-background-color: #481B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.6), e -> {
            circleStyle("-fx-background-color: #471B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(1.8), e -> {
            circleStyle("-fx-background-color: #461B8F;");
        }), new KeyFrame(javafx.util.Duration.seconds(2.0), e -> {
            circleStyle("-fx-background-color: #451B8F;");
        }));
        incomingCallFlasher.setCycleCount(Animation.INDEFINITE);
    }

    /*
    Method used to stop the calling animation
     */
    public void stopCallingAnimation() {
        callingFlasher.pause();
        scaleAnimation.pause();
        circleStyle(toolbarColor);
    }

    /*
    Method used to start calling animation
     */
    public void startCallingAnimation() {
        scaleAnimation.play();
        callingFlasher.play();
    }

    /*
    Method used to start animation for being in a call
     */
    public void startInCallAnimation() {
        circleStyle("-fx-background-color: blue");
    }

    /*
    Method for stopping in call animation
     */
    public void stopInCallAnimation() {
        circleStyle(toolbarColor);
    }

    /*
    Method to start incoming call animation
     */
    public void startIncomingCallAnimation() {
        scaleAnimation.play();
        incomingCallFlasher.play();
    }

    /*
    method to stop incoming call animation
     */
    public void stopIncomingCallAnimation() {
        scaleAnimation.pause();
        System.out.println("stopped flasher");
        incomingCallFlasher.pause();
        circleStyle(toolbarColor);
    }

    /*
    Method used to stop incoming call flasher
     */
    public void stopIncomingFlasher() {
        incomingCallFlasher.pause();
    }

    /*
    Method used to start incoming call flasher
     */
    public void startIncomingFlasher() {
        incomingCallFlasher.play();
    }

    /*
    Method used to set the size of the phone image circle and its color.
     */
    public void circleStyle(String backgroundColor) {
        double sizeRadius = 200;
        double width = 108;
        callButton.setStyle(
                "-fx-background-radius: " + sizeRadius + "em; " +
                        "-fx-min-width: " + width + "px; " +
                        "-fx-min-height: " + width + "px; " +
                        "-fx-max-width: " + width + "px; " +
                        "-fx-max-height: " + width + "px;" +
                        backgroundColor
        );
    }
}