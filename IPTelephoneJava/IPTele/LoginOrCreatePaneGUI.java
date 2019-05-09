import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pontu on 2019-02-07.
 */
    /*
    GUI class for the login or create new user pane.
    Takes care of creating all the input labels and input fields as well as the login and create buttons.
    Creates action handelers for the buttons and basic input validation before sending it to server.
     */
public class LoginOrCreatePaneGUI extends TilePane {
    private Label loginLabel;
    private Label loginNameLabel;
    private TextField loginNameField;
    private Label loginNameResponseLabel;
    private Label loginPasswordLabel;
    private PasswordField loginPasswordField;
    private Label loginPasswordResponseLabel;
    private Button loginButton;
    private Label loginResponseLabel;
    private Label creationLabel;
    private Label creationNameLabel;
    private TextField creationNameField;
    private Label creationNameResponseLabel;
    private Label creationPasswordLabel;
    private PasswordField creationPasswordField;
    private Label creationPasswordResponseLabel;
    private Label creationConfirmationLabel;
    private PasswordField creationConfirmationField;
    private Label creationConfirmationResponseLabel;
    private Button createAccountButton;
    private Label createAccountResponseLabel;
    private IPTele ipTele;

    /*
    Standard constructor for the login or account creation pane.
    Initiates all the labels and input fields, sets their sizes, padding and texts.

     */
    LoginOrCreatePaneGUI(IPTele ipTele) {
        this.ipTele = ipTele;

        loginLabel = new Label("Login");
        loginNameLabel = new Label("Username: ");
        loginNameField = new TextField();
        loginNameResponseLabel = new Label();
        loginPasswordLabel = new Label("Password: ");
        loginPasswordField = new PasswordField();
        loginPasswordResponseLabel = new Label();
        loginButton = new Button("Login");
        loginResponseLabel = new Label();
        creationLabel = new Label("Create account");
        creationNameLabel = new Label("Username: ");
        creationNameField = new TextField();
        creationNameResponseLabel = new Label();
        creationPasswordLabel = new Label("Password: ");
        creationPasswordField = new PasswordField();
        creationPasswordResponseLabel = new Label();
        creationConfirmationLabel = new Label("Password: ");
        creationConfirmationField = new PasswordField();
        creationConfirmationResponseLabel = new Label();
        createAccountButton = new Button("Create account");
        createAccountResponseLabel = new Label();
        loginNameLabel.setPadding(new Insets(0, 0, 0, 60));
        loginNameLabel.setTextFill(Color.WHITE);
        loginPasswordLabel.setPadding(new Insets(0, 0, 0, 60));
        loginPasswordLabel.setTextFill(Color.WHITE);
        creationNameLabel.setPadding(new Insets(0, 0, 0, 60));
        creationNameLabel.setTextFill(Color.WHITE);
        creationPasswordLabel.setPadding(new Insets(0, 0, 0, 60));
        creationPasswordLabel.setTextFill(Color.WHITE);
        creationConfirmationLabel.setPadding(new Insets(0, 0, 0, 60));
        creationConfirmationLabel.setTextFill(Color.WHITE);
        loginLabel.setTextFill(Color.WHITE);
        creationLabel.setTextFill(Color.WHITE);
        loginNameResponseLabel.setTextFill(Color.DARKRED);
        loginPasswordResponseLabel.setTextFill(Color.DARKRED);
        creationNameResponseLabel.setTextFill(Color.DARKRED);
        creationPasswordResponseLabel.setTextFill(Color.DARKRED);
        creationConfirmationResponseLabel.setTextFill(Color.DARKRED);

        /*
        Event handler for when the user clicks on the login button.
        Performs basic input validation and if accepted sends the login request to the IPTele which in turn sends it
        to the server.
         */
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username = loginNameField.getText();
                String password = loginPasswordField.getText();
                boolean nameValidated = inputValidation(username);
                boolean passwordValidated = inputValidation(password);
                boolean usernameAccepted = false;
                boolean passwordAccpeted = false;
                if (!nameValidated) {
                    responseLabelAnimation(loginNameResponseLabel, "a-Z & 0-9 characters only");
                } else if (username.length() > 15) {
                    responseLabelAnimation(loginNameResponseLabel, "Max 15 characters");
                } else {
                    usernameAccepted = true;
                }
                if (!passwordValidated) {
                    responseLabelAnimation(loginPasswordResponseLabel, "a-Z & 0-9 characters only");
                } else if (password.length() > 20) {
                    responseLabelAnimation(loginPasswordResponseLabel, "Max 20 character");
                } else {
                    passwordAccpeted = true;
                }
                if (usernameAccepted && passwordAccpeted) {
                    ipTele.sendLoginRequestToServer(username, password);
                }
            }
        });
        /*
        Event handler for when the user clicks the create account button.
        performs basic input validation and if accepted sends creation request to IPTele which in turn
        sends the request to the server.
        */
        createAccountButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username = creationNameField.getText();
                String password = creationPasswordField.getText();
                String confirmation = creationConfirmationField.getText();
                boolean nameValidated = inputValidation(username);
                boolean passwordValidated = inputValidation(password);
                boolean confirmationValidated = inputValidation(confirmation);
                boolean usernameAccepted = false;
                boolean passwordAccpeted = false;
                boolean confirmationAccepted = false;
                if (!nameValidated) {
                    responseLabelAnimation(creationNameResponseLabel, "a-Z & 0-9 characters only");
                } else if (username.length() > 15) {
                    responseLabelAnimation(creationNameResponseLabel, "Max 15 characters");
                } else {
                    usernameAccepted = true;
                }
                if (!passwordValidated) {
                    responseLabelAnimation(creationPasswordResponseLabel, "a-Z & 0-9 characters only");
                } else if (password.length() > 20) {
                    responseLabelAnimation(creationPasswordResponseLabel, "Max 20 character");
                } else {
                    passwordAccpeted = true;
                }
                if (!confirmationValidated) {
                    responseLabelAnimation(creationConfirmationResponseLabel, "a-Z & 0-9 characters only");
                } else if (confirmation.length() > 20) {
                    responseLabelAnimation(creationConfirmationResponseLabel, "Max 20 character");
                } else if (!confirmation.equals(password)) {
                    responseLabelAnimation(creationConfirmationResponseLabel, "Not the same");
                } else {
                    confirmationAccepted = true;
                }
                if (usernameAccepted && passwordAccpeted && confirmationAccepted) {
                    ipTele.sendCreationRequestToServer(username, password);
                }
            }
        });

        this.setPrefTileWidth(USE_COMPUTED_SIZE);
        this.setPrefColumns(3);
        this.setVgap(1);
        this.setMaxSize(500, 600);
        this.getChildren().addAll(new HBox(), loginLabel, new HBox(), loginNameLabel, loginNameField, loginNameResponseLabel, loginPasswordLabel, loginPasswordField, loginPasswordResponseLabel, new HBox(), loginButton, new HBox(),
                new HBox(), loginResponseLabel, new HBox(), new HBox(), creationLabel, new HBox(), creationNameLabel, creationNameField, creationNameResponseLabel, creationPasswordLabel,
                creationPasswordField, creationPasswordResponseLabel, creationConfirmationLabel, creationConfirmationField, creationConfirmationResponseLabel, new HBox(), createAccountButton,
                new HBox(), new HBox(), createAccountResponseLabel, new HBox());
    }

    /*
    Method called if login attempt was unsuccessful.
    Displays a label for the user informing them the account details were incorrect.
     */
    public void loginFailed() {
        loginResponseLabel.setTextFill(Color.DARKRED);
        responseLabelAnimation(loginResponseLabel, "Incorrect account details");
    }

    /*
    Method called when response has been received regarding account creation.
    If creation was successful the "Account created" label is displayed to the user, if not the
    "Username already exists" label is displayed.
     */
    public void accountCreationServerResonponse(boolean created) {
        if (!created) {
            createAccountResponseLabel.setTextFill(Color.DARKRED);
            responseLabelAnimation(createAccountResponseLabel, "Username already exists");
        } else {
            createAccountResponseLabel.setTextFill(Color.LAWNGREEN);
            responseLabelAnimation(createAccountResponseLabel, "Account created");
        }
    }

    /*
    Method for displaying the response label.
    Displays the label fully for 3 seconds then slowly fades it out.
     */
    private void responseLabelAnimation(Label label, String message) {
        Timeline labelAnimation = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0.0), e -> {
            label.setText(message);
        }), new KeyFrame(javafx.util.Duration.seconds(3.0), e -> {
            label.setOpacity(0.9);
        }), new KeyFrame(javafx.util.Duration.seconds(3.05), e -> {
            label.setOpacity(0.8);
        }), new KeyFrame(javafx.util.Duration.seconds(3.1), e -> {
            label.setOpacity(0.7);
        }), new KeyFrame(javafx.util.Duration.seconds(3.15), e -> {
            label.setOpacity(0.6);
        }), new KeyFrame(javafx.util.Duration.seconds(3.2), e -> {
            label.setOpacity(0.5);
        }), new KeyFrame(javafx.util.Duration.seconds(3.25), e -> {
            label.setOpacity(0.4);
        }), new KeyFrame(javafx.util.Duration.seconds(3.3), e -> {
            label.setOpacity(0.3);
        }), new KeyFrame(javafx.util.Duration.seconds(3.35), e -> {
            label.setOpacity(0.2);
        }), new KeyFrame(javafx.util.Duration.seconds(3.4), e -> {
            label.setText("");
            label.setOpacity(1.0);
        }));
        labelAnimation.playFromStart();
    }

    /*
    Method used for basic input validation.
    Returns true if the input string only contains a-z letters, A-Z letters or 0-9 numerals.
     */
    private boolean inputValidation(String s) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
        Matcher matcher = pattern.matcher(s);
        Boolean matchFound = matcher.matches();
        if (matchFound) {
            return true;
        } else {
            return false;
        }
    }
}