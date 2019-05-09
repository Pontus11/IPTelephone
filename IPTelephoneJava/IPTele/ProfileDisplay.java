import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Created by pontu on 2018-03-15.
 */
/*
GUI class for the profile display.
 */
public class ProfileDisplay extends VBox {
    private final String toolbarColor = "-fx-background-color: #171717;";
    private final String hoverColor = "-fx-background-color: #898686;";
    private final String centerColor = "-fx-background-color: #222222;";
    private final String centerBottomColor = "-fx-background-color: #202020;";
    private final String textFieldColor = "-fx-background-color: #FFFFFF;";
    private final String bottomColor = "-fx-background-color: #222222";
    private PhoneGuiFX phoneGuiFX;
    private ImageView imageView;
    private Label nameLabel;
    private Circle profileCircle;
    private StackPane imageStackPane;
    private ColorAdjust lightupEffect;

    /*
    Standard constructor for the ProfileDisplay.
    Sets the profile image, sizes and the name label.
    Sets the style sheet.
    Also sets the handlers for the drag and drop functionality that allows the
    user to change the profile picture by dropping a new image there.
     */
    public ProfileDisplay(PhoneGuiFX gui, String name, Image img) {
        phoneGuiFX = gui;

        nameLabel = new Label(name);
        nameLabel.getStyleClass().add("labelText");

        lightupEffect = new ColorAdjust();

        imageView = new ImageView(img);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        profileCircle = new Circle();
        profileCircle.setRadius(50);
        profileCircle.setCenterX(50);
        profileCircle.setCenterY(50);
        imageView.setClip(profileCircle);

        Label changePictureLabel = new Label("Drag and\ndrop image to \nchange picture");
        changePictureLabel.textAlignmentProperty().setValue(TextAlignment.CENTER);
        changePictureLabel.setMouseTransparent(true);
        changePictureLabel.setTextFill(Color.WHITE);
        changePictureLabel.setVisible(false);

        imageView.getStyleClass().add("profilePicture");

        imageView.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lightupImage();
                changePictureLabel.setVisible(true);
            }
        });
        imageView.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                darkenImage();
                changePictureLabel.setVisible(false);
            }
        });

        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        this.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                boolean success = false;
                if (event.getDragboard().hasFiles()) {
                    setPictureFromFile(event.getDragboard().getFiles().get(0).getAbsolutePath());
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });


        imageStackPane = new StackPane();
        imageStackPane.getChildren().addAll(imageView,changePictureLabel);
        StackPane.setAlignment(imageView,Pos.CENTER);

        this.setPadding(new Insets(0,0,0,20));
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(nameLabel, imageStackPane, new LogoutButton());
    }

    /*
    Method used to increase the brightness of the image.
     */
    public void lightupImage() {
        lightupEffect.setBrightness(0.3);
        imageView.setEffect(lightupEffect);
    }
    /*
    Method used to darken the image.
     */
    public void darkenImage() {
        lightupEffect.setBrightness(0.0);
        imageView.setEffect(lightupEffect);
    }

    /*
    Method used to set the picture.
     */
    public void setPicture(byte[] imageData) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            Image image = new Image(bais);
            imageView.setImage(image);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method to set the picture from a file.
     */
    private void setPictureFromFile(String imageURL) {
        try {
            System.out.println("trying to set picture" + imageURL);
            File file = new File(imageURL);
            if(file.exists()){
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                phoneGuiFX.sendUpdatedPictureToServer(image);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to set the name label.
     */
    public void setName(String username) {
        nameLabel.setText(username);
    }

    /*
    Gui class for the logout button.
     */
    public class LogoutButton extends VBox{
        /*
        Standard constructor for the logout button.
        Sets the functionality by setting the eventHandler that calls PhoneGuiFX.logout() when pressed.
         */
        LogoutButton() {
            Button logoutButton = new Button("Log out");
            logoutButton.setStyle(bottomColor);
            logoutButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    phoneGuiFX.logout();
                }
            });
            logoutButton.setOnMousePressed(e -> logoutButton.setTextFill(Color.BLACK));
            logoutButton.setOnMouseReleased(e -> logoutButton.setTextFill(Color.WHITE));
            logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(hoverColor));
            logoutButton.setOnMouseExited(e -> logoutButton.setStyle(bottomColor));
            logoutButton.setTextFill(Color.WHITE);
            this.getChildren().add(logoutButton);
            this.setAlignment(Pos.CENTER);
        }
    }
}
