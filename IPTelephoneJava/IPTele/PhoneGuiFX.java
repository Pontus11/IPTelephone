import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.InetAddress;
import java.util.*;


/**
 * Created by pontu on 2018-02-26.
 */
/*
Main GUI class for the IP telephone.
Takes care of creating all the different GUI elements.
Class is used by other GUI classes in order to communicate with IPTele which in turn takes
care of server and other client communication.
*/
public class PhoneGuiFX extends Application {
    private IPTele ipt;

    private double xCordinate;
    private double yCordinate;

    private final String toolbarColor = "-fx-background-color: #171717;";
    private final String centerColor = "-fx-background-color: #222222;";
    private final String centerBottomColor = "-fx-background-color: #202020;";
    private final String textFieldColor = "-fx-background-color: #FFFFFF;";

    private Scene ipTeleScene;

    private BorderPane mainLayout;
    private FlowPane loginLayout;
    private LoginOrCreatePaneGUI loginOrCreatePane;
    private BorderPane centerPane;
    private FlowPane topPane;
    private HBox bottomPane;
    private VBox contactPane;
    private VBox callPane;
    private VBox chatPane;

    private TextField friendNameField;
    private ContactView contactView;
    private ContactAndRequestBox contactAndRequestBox;

    private ProfileDisplay profileDisplay;
    private String username;
    private Image profileImage;

    private ChatWindow chatWindow;

    private RespondToCallButtons respondToCallButtons;
    private CallingLabel callingLabel;
    private CallButton callButton;

    private PhoneState state = PhoneState.UNAVAILABLE;

    private InetAddress callingAddress;
    private InetAddress incomingCallAddress;
    private HashMap<String, ContactDisplay> contacts = new HashMap<>();
    private HashMap<String, ContactDisplay> friendRequests = new HashMap<>();

    private AudioPlayer ringingPlayer;
    private AudioPlayer incomingCallPlayer;

    public PhoneGuiFX() {
        ipt = new IPTele(this, "192.168.1.74", 19999);
    }

    /*
    Initiates the css files and calls methods that
    initiate GUI elements.
     */
    @Override
    public void start(Stage primaryStage) {
        mainLayout = new BorderPane();
        ipTeleScene = new Scene(mainLayout, 900, 700);
        File sliderFile = new File("Slider.css");
        File profileDisplayFile = new File( "ProfileDisplay.css");
        File contactViewFile = new File("ContactView.css");
        File chatWindowFile = new File("ChatWindow.css");

        ipTeleScene.getStylesheets().addAll("file:///" + sliderFile.getAbsolutePath().replace("\\", "/"),"file:///" + profileDisplayFile.getAbsolutePath().replace("\\", "/"),"file:///" + contactViewFile.getAbsolutePath().replace("\\", "/"),"file:///" + chatWindowFile.getAbsolutePath().replace("\\", "/") );
        primaryStage.setTitle("IP Telephone");

        initiateAndAddToolbar(primaryStage);
        initiateAndAddLoginLayout();

        mainLayout.setStyle(centerColor);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(ipTeleScene);
        primaryStage.show();
    }

    /*
    Method used to initiate the toolbar.
    Sets the event handlers for when user presses the mouse and drags the mouse.
     */
    private void initiateAndAddToolbar(Stage primaryStage) {
        HBox space = new HBox();
        HBox.setHgrow(space, Priority.ALWAYS);
        space.setMinWidth(10);

        ToolBar toolBar = new ToolBar();
        toolBar.setPrefHeight(25);
        toolBar.setMinHeight(25);
        toolBar.setMaxHeight(25);
        toolBar.getItems().addAll(new HomeButton(), new ShowAddButton("Add Contacts", this), space, new CloseButton(this, ipt));
        toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                xCordinate = primaryStage.getX() - mouseEvent.getScreenX();
                yCordinate = primaryStage.getY() - mouseEvent.getScreenY();
            }
        });
        toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                primaryStage.setX(mouseEvent.getScreenX() + xCordinate);
                primaryStage.setY(mouseEvent.getScreenY() + yCordinate);
            }
        });
        toolBar.setStyle(toolbarColor);
        mainLayout.setTop(toolBar);
    }
    /*
    Method used to initiate the login layout.
     */
    private void initiateAndAddLoginLayout() {
        loginLayout = new FlowPane();
        loginOrCreatePane = new LoginOrCreatePaneGUI(ipt);
        loginLayout.getChildren().add(loginOrCreatePane);
        loginLayout.setAlignment(Pos.CENTER);

        mainLayout.setCenter(loginLayout);
    }

    /*
    Method for starting ringing sound
     */
    private void startRingingSound() {
        if (ringingPlayer != null) {
            ringingPlayer.setAlive(false);
        }
        ringingPlayer = new AudioPlayer("beep.wav");
    }

    /*
    Method for stopping ringing sound
     */
    private void stopRingingSound() {
        if (ringingPlayer != null) {
            ringingPlayer.setAlive(false);
        }
    }

    /*
    method for starting incoming call sound
     */
    private void startIncomingCallSound() {
        if (incomingCallPlayer != null) {
            incomingCallPlayer.setAlive(false);
        }
        incomingCallPlayer = new AudioPlayer("beep2.wav");
    }

    /*
    method for stopping incoming call sound
     */
    private void stopIncomingCallSound() {
        if (incomingCallPlayer != null) {
            incomingCallPlayer.setAlive(false);
        }
    }

    /*
    Method to start the calling functionality.
    Sets the PhoneState to CALLING.
    If the callingAddress is not null the call is initiated and a
    call request is sent to the other target client by calling
    IPTele.callRequest().
     */
    public void startCalling() {
        state = PhoneState.CALLING;

        String selectedName = (String) contactView.getSelectionModel().getSelectedItem().getName();
        InetAddress address = contacts.get(selectedName).getUser().getLastKnownIp();
        if (address != null) {
            callingAddress = address;
            callingLabel.startCallingAnimation();
            callingLabel.setVisible(true);
            callButton.startCallingAnimation();
            startRingingSound();

            ipt.callRequest(callingAddress);
        } else {
            state = PhoneState.AVAILABLE;
        }
    }

    /*
    Method to stop calling
     */
    public void stopCalling() {
        stopCallingAnimation();
        ipt.stopCall();
    }

    /*
    Method to stop calling animation
     */
    public void stopCallingAnimation() {
        state = PhoneState.AVAILABLE;

        callingLabel.stopCallingAnimation();
        callingLabel.setVisible(false);
        callButton.stopCallingAnimation();
        stopRingingSound();
        callingAddress = null;
    }

    /*
    method to start incoming call functionality.
     */
    public void startIncomingCall(InetAddress address) {
        state = PhoneState.INCOMINGCALL;

        incomingCallAddress = address;
        callingLabel.setVisible(true);
        callButton.startIncomingCallAnimation();

        String callerName = "";
        for(Map.Entry<String, ContactDisplay> entry: contacts.entrySet()) {
            System.out.println(entry.getValue().getUser().getLastKnownIp() + " " + address);
            if(entry.getValue().getUser().getLastKnownIp() != null && entry.getValue().getUser().getLastKnownIp().getHostAddress().equals(address.getHostAddress())) {
                System.out.println("here");
                callerName = entry.getValue().getUser().getUsername();
            }
        }
        final String finalCallerName = callerName;
        System.out.println("name is: " + finalCallerName);
        Platform.runLater(()-> callingLabel.startIncomingCallAnimation(finalCallerName));

        respondToCallButtons.setVisible(true);
        startIncomingCallSound();
    }

    /*
    Method to stop incoming call
     */
    public void stopIncomingCall(boolean response) {
        stopIncomingCall();
        ipt.respondToCall(response);
    }

    /*
    Method to stop incoming call
     */
    public void stopIncomingCall() {
        state = PhoneState.AVAILABLE;

        incomingCallAddress = null;
        callingLabel.setVisible(false);
        Platform.runLater(() -> {
            callingLabel.stopIncomingCallAnimation();
        });
        System.out.println("about to stop incoming animation");
        callButton.stopIncomingCallAnimation();
        respondToCallButtons.setVisible(false);
        stopIncomingCallSound();
    }

    /*
    method to set in call functionality.
    Sets the PhoneState to INCALL.
     */
    public void startInCall() {
        state = PhoneState.INCALL;
        System.out.println("incall");
        callingLabel.startInCallAnimation();
        callingLabel.setVisible(true);
        callButton.startInCallAnimation();
    }

    /*
    Method to stop call
     */
    public void stopInCall() {
        stopInCallAnimation();
        ipt.stopCall();
    }

    /*
    method to stop call animation
     */
    public void stopInCallAnimation() {
        state = PhoneState.AVAILABLE;

        callButton.stopInCallAnimation();
        callingLabel.stopInCallAnimation();
        callingLabel.setVisible(false);
    }

    /*
    Method used to update the GUI so that the user can be notified about an unsuccessful login
    attempt.
     */
    public void loginUnsuccessful() {
        loginOrCreatePane.loginFailed();
    }

    /*
    Method used to update the GUI with an account creation response so it can be displayed to user.
     */
    public void accountCreationServerResponse(boolean created) {
        loginOrCreatePane.accountCreationServerResonponse(created);
    }

    /*
    Method used to set the profile display so it can be displayed to the user.
     */
    public void setProfileDisplay(String username, byte[] profilePictureData) {
        this.username = username;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(profilePictureData);
            Image image = new Image(bais);
            profileImage = image;
        }catch(Exception e) {
            e.printStackTrace();
        }
        profileDisplay.setPicture(profilePictureData);
        profileDisplay.setName(username);
    }
    /*
    method to get user image.
     */
    public Image getUserImage() {
        return profileImage;
    }
    /*
    Method to get username.
     */
    public String getUsername() {
        return username;
    }

    /*
    Method used to get the current phone state.
     */
    public PhoneState getPhoneState() {
        return state;
    }

    /*
    Method used to get a HashMap<String, ContactDisplay> of all the users contacts.
     */
    public HashMap<String, ContactDisplay> getContacts() {
        return contacts;
    }

    /*
    Method used to send a message to the GUI so that it can be updated and display a text message to
    the user.
     */
    public void sendMessageToGui(String from, String message) {
        ContactDisplay contactDisplay = contactView.getSelectionModel().getSelectedItem();
        if(contactDisplay != null && contactDisplay.getName().equals(from)) {
            chatWindow.addMessageToChatWindow(message, contactDisplay.getContactImage(), from);
        }
    }

    /*
    method to make IPTele send a text message to server for another user.
     */
    public void sendMessageToRecipient(String message) {
        ipt.sendMessageToServer(contactView.getSelectionModel().getSelectedItem().getName(), message);
    }

    /*
    Method used to check if the topPane is visible.
     */
    public boolean isAddPanelVisible() {
        return topPane.isVisible();
    }

    /*
    Method used to make the topPane visible and add it to the centerPane.
     */
    public void addAddPanel() {
        topPane.setVisible(true);
        centerPane.setTop(topPane);
    }

    /*
    Method used to make the topPane invisible and set
    the center top pane to null.
     */
    public void removeAddPanel() {
        topPane.setVisible(false);
        centerPane.setTop(null);
    }

    /*
    Method used to change the call volume by calling IPTele.changeCallVolume().
     */
    public void changeCallVolume(float value) {
        ipt.changeCallVolume(value);
    }

    /*
    Method used to change the microphone volume by calling IPTele.changeMicrophoneVolume().
     */
    public void changeMicrophoneVolume(int value) {
        ipt.changeMicrophoneVolume(value);
    }

    /*
    Method used to sort the list of contacts.
     */
    private SortedList<ContactDisplay> sortList(Collection<ContactDisplay> unsorted) {
        SortedList<ContactDisplay> sorted = new SortedList<ContactDisplay>(FXCollections.observableArrayList(unsorted));
        Comparator<ContactDisplay> contactDisplayComperator = new Comparator<ContactDisplay>() {
            @Override
            public int compare(ContactDisplay o1, ContactDisplay o2) {
                int online = 0;
                if(o1.getUser().getLastKnownIp() != null && o2.getUser().getLastKnownIp() == null) {
                    System.out.println("found ip");
                    online = -1000;
                }else if(o2.getUser().getLastKnownIp() != null && o1.getUser().getLastKnownIp() == null) {
                    online = 1000;
            }
                System.out.println("sorting value" + (online + o1.getName().compareTo(o2.getName())));
                return online + o1.getName().compareToIgnoreCase(o2.getName());
            }
        };
        sorted.setComparator(contactDisplayComperator);
        return sorted.sorted(contactDisplayComperator);
    }

    /*
    Method used to change the contact view to show friend requests instead.
     */
    public void changeContactViewToRequests() {
        contactView.setItems(FXCollections.observableArrayList(friendRequests.values()));
        contactView.getSelectionModel().select(0);
    }

    /*
    Method used to change the contact view to display all current contacts.
     */
    public void changeContactViewToContacts() {
        SortedList<ContactDisplay> sorted = sortList(contacts.values());
        contactView.setItems(sorted);
        contactView.getSelectionModel().select(0);
    }

    /*
    Method to set the request map.
     */
    public void setRequestsMap(ArrayList<User> requests) {
        friendRequests.clear();
        for(User user: requests) {
            RequestDisplay contactDisplay = new RequestDisplay(user, this);
            friendRequests.put(user.getUsername(), contactDisplay);
        }
    }
    /*
    Method to set the contacts map.
     */
    public void setContactsMap(ArrayList<User> contactsList) {
        contacts.clear();
        for(User user: contactsList) {
            ContactDisplay contactDisplay = new ContactDisplay(user, this);
            contacts.put(user.getUsername(), contactDisplay);
        }
    }
    /*
    Method to display friend requests.
     */
    public void displayFriendRequests(ArrayList<User> friendRequests) {
        setRequestsMap(friendRequests);
        contactAndRequestBox.setFriendRequestCount(friendRequests.size());
    }

    /*
    Method used to add a friend request.
     */
    public void addFriendRequest(User user) {
        RequestDisplay contactDisplay = new RequestDisplay(user, this);
        friendRequests.put(user.getUsername(), contactDisplay);
        contactAndRequestBox.setFriendRequestCount(friendRequests.size());
    }

    /*
    Method used to set the contacts map and then change the contact view to
    display contacts.
     */
    public void displayContacts(ArrayList<User> contacts) {
        setContactsMap(contacts);
        changeContactViewToContacts();
    }

    /*
    Method used to set a contact as online.
    re-sorts the contacts map by calling resortContactView().
     */
    public void setContactAsOnline(User user) {
        if(contacts.get(user.getUsername()) == null) {
            contacts.put(user.getUsername(), new ContactDisplay(user,this));
        }
        ContactDisplay contactDisplay = contacts.get(user.getUsername());
        contactDisplay.getUser().setLastKnownIp(user.getLastKnownIp());
        Platform.runLater(()->contactDisplay.setOnlineLabel());
        resortContactView();
    }

    /*
    Method used to set a contact as offline.
     */
    public void setContactAsOffline(User user) {
        ContactDisplay contactDisplay = contacts.get(user.getUsername());
        contactDisplay.getUser().setLastKnownIp(null);
        Platform.runLater(()->contactDisplay.setOfflineLabel());
        resortContactView();
    }

    /*
    Method used to re-sort the contacts map.
     */
    private void resortContactView() {
        Platform.runLater(()-> {
            SortedList<ContactDisplay> sorted = sortList(contacts.values());
            contactView.setItems(sorted);
        });
    }

    /*
    Method used to accept a friend request.
    calls IPTele.acceptFriendRequest() to notify server that the request has been accepted.
    Removes the request from requests and adds the friend to contacts.
     */
    public void acceptFriendRequest(ContactDisplay contactDisplay) {
        ipt.acceptFriendRequest(contactDisplay.getUser().getUsername());
        removeFromRequest(contactDisplay.getUser().getUsername());
        contacts.put(contactDisplay.getUser().getUsername(), new ContactDisplay(contactDisplay.getUser(),this));
    }

    /*
    Method used to decline a friend request.
    Calls IPTele.declineFriendRequest() to notify server.
    Removes friend request from requests.
     */
    public void declineFriendRequest(String requestName) {
        ipt.declineFriendRequest(requestName);
        removeFromRequest(requestName);
    }

    /*
    Method used to remove a request.
     */
    private void removeFromRequest(String requestName) {
        friendRequests.remove(requestName);
        changeContactViewToRequests();
        contactAndRequestBox.setFriendRequestCount(friendRequests.size());
    }

    /*
    Method used to clear the chat window.
     */
    public void clearChatWindow() {
        chatWindow.clearChat();
    }

    /*
    Method used to load the chat window.
    Loads the chat window for the selected contact.
    If chat history has not already been received it is requested
    from server by calling IPTele.requestConversationFromServer() which in turn
    contacts server.
     */
    public void loadChatWindow(ContactDisplay contact) {
        chatWindow.setContactDisplay(contact);
        if(contact.getChatHistory().size() == 0) {
            ipt.requestConversationFromServer(contact.getName());
        }else {
            chatWindow.loadChatHistory();
        }
    }

    /*
    Message to update contact conversation
     */
    public void updateContactConversation(String from, ArrayList<Message> chatHistory){
        System.out.println("Recieved history from serv");
        if(contacts.get(from) != null) {
            System.out.println("updating chathistory");
            contacts.get(from).setChatHistory(chatHistory);
        }else if(friendRequests.get(from) != null) {
            friendRequests.get(from).setChatHistory(chatHistory);
        }
        chatWindow.loadChatHistory();
    }

    /*
    Method used to send a new updated profile picture to the server by calling
    IPTele.sendUserPictureToServer().
     */
    public void sendUpdatedPictureToServer(Image image) {
        ipt.sendUserPictureToServer(image);
    }

    /*
    Method to logout.
    Sets the PhoneState to UNAVAILABLE, clears all the maps and lists.
    sets the mainLayout to the loginLayout.
    Calls IPTele.sendLogoutToServer() in order to notify server that user has logged out.
     */
    public void logout() {
        state = PhoneState.UNAVAILABLE;

        contacts.clear();
        friendRequests.clear();
        contactView.setItems(null);
        friendNameField.clear();

        mainLayout.setCenter(loginLayout);
        ipt.sendLogoutToServer();
    }

    /*
    Initiation method to initiate the contact pane.
     */
    private void initiateContactPane() {
        contactPane = new VBox();
        contactAndRequestBox = new ContactAndRequestBox(this);
        contactView = new ContactView(this);

        contactPane.getChildren().addAll(contactAndRequestBox, contactView);
    }

    /*
    Initiation method to initiate the call pane.
     */
    private void initiateCallPane() {
        callPane = new VBox();

        callingLabel = new CallingLabel();
        callButton = new CallButton(this);
        callingLabel.setVisible(false);
        respondToCallButtons = new RespondToCallButtons(this);
        respondToCallButtons.setVisible(false);

        callPane.setMinWidth(300);
        callPane.setMaxWidth(300);

        callPane.setStyle(centerBottomColor);
        callPane.setAlignment(Pos.CENTER);
        callPane.getChildren().addAll(callButton, callingLabel, respondToCallButtons);
    }
    /*
    Initiation method to initiate the chat pane.
     */
    private void initiateChatPane() {
        chatPane = new VBox();
        chatPane.setStyle("-fx-background-color: Transparent");
        chatPane.setAlignment(Pos.CENTER);
        chatPane.setMinWidth(300);

        chatWindow = new ChatWindow(this);
        chatPane.getChildren().add(chatWindow);
    }
    /*
    Initiation method to initiate the top pane.
     */
    private void initiateTopPane() {
        topPane = new FlowPane();

        Label friendNameLabel = new Label("Username:");
        friendNameLabel.setTextFill(Color.WHITE);
        friendNameField = new TextField();
        friendNameField.setStyle(textFieldColor);

        topPane.setHgap(10);
        topPane.setAlignment(Pos.CENTER);
        topPane.getChildren().addAll(friendNameLabel, friendNameField, new AddButton(this, ipt));
        topPane.setVisible(false);
    }
    /*
    Initiation method to initiate the bottom pane.
     */
    private void initiateBottomPane() {
        bottomPane = new HBox();

        Image img = new Image(new File("noProfilePic.png").toURI().toString());
        profileDisplay = new ProfileDisplay(this, "test", img);

        Pane spacer2 = new Pane();
        spacer2.setPrefWidth(9999);

        Pane spacer3 = new Pane();
        spacer3.setPrefWidth(9999);

        Pane spacer4 = new Pane();
        spacer4.setPrefWidth(160);
        spacer4.setMinWidth(160);

        VolumeSlider volumeSlider = new VolumeSlider(this);
        MicrophoneSlider microphoneSlider = new MicrophoneSlider(this);

        bottomPane.setStyle(centerBottomColor);
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.getChildren().addAll(profileDisplay, spacer2, volumeSlider, callPane, microphoneSlider, spacer3, spacer4);

    }
    /*
    Initiation method to initiate the center pane.
     */
    private void initiateCenterPane() {
        HBox spacerPane = new HBox();
        spacerPane.setPrefWidth(200);

        centerPane = new BorderPane();
        centerPane.setCenter(chatPane);
        centerPane.setLeft(contactPane);
        centerPane.setRight(spacerPane);
        centerPane.setBottom(bottomPane);
        centerPane.setStyle(centerColor);
        Platform.runLater(()->mainLayout.setCenter(centerPane));
    }

    /*
    Method to remove loginView.
    Sets the PhoneState to AVAILABLE.
     */
    public void removeLoginView() {
        state = PhoneState.AVAILABLE;
        Platform.runLater(() -> {
            mainLayout.setCenter(centerPane);
        });
    }

    /*
    method for successful login.
    Initiates all panes.
     */
    public void loginSuccuessful() {
        initiateContactPane();
        initiateChatPane();
        initiateCallPane();
        initiateBottomPane();
        initiateTopPane();
        initiateCenterPane();
        removeLoginView();
        contactView.getSelectionModel().select(null);
        ipt.requestFriendsFromServer();
    }

    /*
    Method to get friendNameField.
     */
    public TextField getFriendNameField() {
        return friendNameField;
    }

    /*
    Method to get call Button.
     */
    public CallButton getCallButton() {
        return callButton;
    }
    /*
    Main class.
     */
    public static void main(String[] args){
        launch(args);
    }
}
