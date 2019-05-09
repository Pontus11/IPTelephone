import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.concurrent.TimeUnit;

/**
 * Created by pontu on 2019-02-07.
 */

/*
Gui class for the calling label and its functionality.
This class takes care of the display of the label which shows the user the status of their phone calls.
Displays different labels and animations for the user if they are calling, being called or are in a phone call.
 */
public class CallingLabel extends HBox {
    private Label callingLabel;
    private AudioPlayer ap;
    private Timeline callingAnimation;
    private Timeline inCallAnimation;
    private int secondsPassed;
    private Insets padding = new Insets(10, 0, 0, 0);

    /*
    Standard constructor for the CallingLabel.
    Creates the callingAnimation, sets the alignment and padding.
     */
    CallingLabel() {
        callingLabel = new Label("Calling");
        callingLabel.setTextFill(Color.WHITE);

        callingAnimation = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0.5), e -> {
            callingLabel.setText("Calling.");
        }), new KeyFrame(javafx.util.Duration.seconds(1.0), e -> {
            callingLabel.setText("Calling..");
        }), new KeyFrame(javafx.util.Duration.seconds(1.5), e -> {
            callingLabel.setText("Calling...");
        })
        );
        callingAnimation.setCycleCount(Animation.INDEFINITE);
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(callingLabel);
        callingLabel.setPadding(padding);
    }

    /*
    Method for starting the calling animation.
     */
    public void startCallingAnimation() {
        callingAnimation.playFromStart();
    }

    /*
    Method for stopping the calling animation.
     */
    public void stopCallingAnimation() {
        callingAnimation.stop();
    }

    /*
    Method for starting the in call animation.
    Displays how long the user has currently been in a phone call for.
     */
    public void startInCallAnimation() {
        secondsPassed = 0;
        inCallAnimation = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1.0), e -> {
            secondsPassed++;
            long hours = TimeUnit.SECONDS.toHours(secondsPassed);
            long minutes = TimeUnit.SECONDS.toMinutes(secondsPassed) - TimeUnit.HOURS.toMinutes(hours);
            System.out.println(secondsPassed);
            String s = String.format("%dh %dm %ds",
                    hours,
                    minutes,
                    secondsPassed - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours));

            callingLabel.setText(s);
        }));
        inCallAnimation.setCycleCount(Animation.INDEFINITE);
        inCallAnimation.playFromStart();
    }

    /*
    Method to stop the in call animation
     */
    public void stopInCallAnimation() {
        secondsPassed = 0;
        inCallAnimation.stop();
    }

    /*
    Method to start the incoming call animation.
    Displays who is calling the user.
     */
    public void startIncomingCallAnimation(String addressString) {
        callingLabel.setText("Incoming call from: " + addressString);
        callingLabel.setPadding(new Insets(10, 0, 0, 0));
    }

    /*
    Method to stop the incoming call animation.
     */
    public void stopIncomingCallAnimation() {
        callingLabel.setText("");
        callingLabel.setPadding(padding);
    }
}
