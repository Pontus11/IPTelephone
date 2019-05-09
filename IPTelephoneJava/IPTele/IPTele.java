import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import sun.awt.Symbol;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by pontu on 2018-02-24.
 */

/*
Primary class for all the functional communication between different classes
Takes care of the the sockets and streams, starting the message and audio receivers, audio recorder and sending messages to other clients.
Also the class responsible for communication with the server.
*/
public class IPTele {
    private String username;
    private String password;
    private int myPort;
    private int otherPort;
    private String otherIP;
    private PhoneGuiFX phoneGuiFX;
    private boolean inPhoneCall = false;
    private boolean isBeingCalled = false;
    private AudioReceiver audioReceiver;
    private AudioRecorder audioRecorder;
    private InetAddress callingAddress;
    private Socket socket;
    private ObjectOutputStream oos;
    private float savedVolume = 100;
    private int savedMicrophoneVolume = 100;

    /*
    Standard constructor.
    Calls the setupServerConnection() function to get the server connection.
    Sets up the CallReceiver class which is responsible for receiving messages from other clients.
     */
    public IPTele(PhoneGuiFX pg, String server, int port) {
        phoneGuiFX = pg;
        setupServerConnection(server, port);
        CallReceiver callReceiver = new CallReceiver(this, 3011);
        try {
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method for setting up the connection with the server.
    Sets up the output and the sockets.
    Starts initiates a ServerReply so that the client can receive messages from the server.
     */
    private void setupServerConnection(String server, int port){
        setUpSocket(server, port);
        setUpOutput();
        ServerReply serverReply = new ServerReply(socket, this);
    }

    /*
    Method for setting up the sockets.
     */
    public void setUpSocket(String ip, int port) {
        try {
            InetAddress host = InetAddress.getByName(ip);
            socket = new Socket(host, port);
            System.out.println("connected to IP: " + host.getHostAddress() + " at port: " + port);
        } catch (Exception ex) {
            System.out.println("Socket could not be created");
        }
    }

    /*
    Method for setting up the objectOutputStream.
     */
    public void setUpOutput() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            System.out.println("Could not setup input and output streams");
        }
    }

    /*
    Method called if login attempt to server was successful.
    updates the GUI and saves password and username as variables.
     */
    public void loginSuccessful(String username, String password, byte[] profilePictureData) {
        this.username = username;
        this.password = password;
        phoneGuiFX.loginSuccuessful();
        phoneGuiFX.setProfileDisplay(username, profilePictureData);
    }
    /*
    Method for notifying gui about unsuccessful login attempt.
     */
    public void loginUnsuccessful() {
        phoneGuiFX.loginUnsuccessful();
    }

    /*
    Method called if the client has received an update from the server regarding an account creation being successful.
    Calls PhoneGuiFX.accountCreationServerResponse() to update the gui so that the user can see that the account creation was unsuccessful.
     */
    public void accountCreationServerResponse(boolean created) {
        phoneGuiFX.accountCreationServerResponse(created);
    }
    /*
    Method used to send a login attempt to the server.
     */
    public void sendLoginRequestToServer(String username, String password) {
        ServerMessage loginMessage = new ServerMessage("LOGIN", username, password);
        try {
            oos.writeObject(loginMessage);
            oos.flush();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    method used to send a account creation request to server. Sends password and username.
     */
    public void sendCreationRequestToServer(String username, String password) {
        ServerMessage creationMessage = new ServerMessage("CREATE", username, password);
        try {
            oos.writeObject(creationMessage);
            oos.flush();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method called after incoming call message is received. Setting necessary variables and calls
    PhoneGuiFX.startIncomingCall() to notify gui about incoming call.
     */
    public void displayIncomingCall(InetAddress address) {
        System.out.println("sending display command to gui");
        isBeingCalled = true;
        callingAddress = address;
        phoneGuiFX.startIncomingCall(address);
     }

     /*
     Method used to respond to a call.
     Sends an accept or decline message to other client depending on what user selected in GUI.
      */
     public void respondToCall(boolean reply) {
         if(reply) {
             System.out.println("Sending acce to: " + callingAddress);
             sendMessage("ACCE", callingAddress);
             startPhoneCall();
         }else {
             sendMessage("DECL", callingAddress);
         }
         isBeingCalled = false;
     }

     /*
     Method used to start a phoneCall
     Creates a AudioReceiver for receiving audio and an AudioRecorder to record and send audio.
     Sets the inPhoneCall boolean to true and then notifies the GUI so it can stop any ongoing animations
     and start the inCall animations.
      */
    public void startPhoneCall() {
        try {
            System.out.println("Starting phonecall");
            audioReceiver = new AudioReceiver(this, 3010, savedVolume);
            audioRecorder = new AudioRecorder(this, callingAddress, savedMicrophoneVolume);
            inPhoneCall = true;
            phoneGuiFX.stopCallingAnimation();
            phoneGuiFX.startInCall();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to send a call request to another client.
     */
    public void callRequest(InetAddress address) {
        try {
            callingAddress = address;
            sendMessage("CALL", callingAddress);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to stop a call
    notifies the gui so it can stop the in call animations displayed for the user.
    Then sends a cancel message to the contact that the user was in a phone call with to let them know that
    the phone call has ended.
    Sets the callingAddress to null.
     */
    public void stopCall(){
        if(inPhoneCall) {
            phoneGuiFX.stopInCallAnimation();
            audioRecorder.killThread(false);
            audioReceiver.killThread(false);
            inPhoneCall = false;
        }
        if(callingAddress != null) {
            phoneGuiFX.stopCallingAnimation();
            sendMessage("CANC", callingAddress);
            callingAddress = null;
        }
        try {
            Thread.sleep(100);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used if call was stopped by contact that user was in a call with.
    Notifies gui so it can stop in call animations.
     */
    public void callStoppedByOther() {
        System.out.println("call stopped by other" + inPhoneCall);
        if(inPhoneCall) {
            System.out.println("canceling both");
            phoneGuiFX.stopInCallAnimation();
            audioRecorder.killThread(false);
            audioReceiver.killThread(false);
            inPhoneCall = false;
        }else {
            phoneGuiFX.stopIncomingCall();
        }
        callingAddress = null;
    }

    /*
    Method used to send a message to another client.
     */
    public void sendMessage(String message, InetAddress address) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] stringArray = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(stringArray, stringArray.length, address, 3011);
            System.out.println("SENDING MESSAGE TO OTHER" + address + " " + 3011);
            socket.send(datagramPacket);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to send a message to the server.
     */
    public void sendMessageToServer(String recipient, String message) {
        try {
            ServerMessage serverMessage = new ServerMessage("MESSAGE", username, password, recipient, message);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to send a friend request to the server.
     */
    public void sendFriendRequest(String recipient) {
        try {
            ServerMessage serverMessage = new ServerMessage("FRIEND REQUEST", username, password, recipient);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to accept a friend request and send that message to the server.
     */
    public void acceptFriendRequest(String sender) {
        try {
            ServerMessage serverMessage = new ServerMessage("ACCEPT REQUEST", username, password, sender);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method to send a decline friendship message to server.
     */
    public void declineFriendRequest(String sender) {
        try {
            ServerMessage serverMessage = new ServerMessage("DECLINE REQUEST", username, password, sender);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    Method used to call PhoneGuiFX.displayFriendRequests() in order to display all the users
    friend requests in the GUI.
     */
    public void displayFriendRequestsInGUI(ArrayList<User> requests) {
        phoneGuiFX.displayFriendRequests(requests);
    }

    /*
    Method used to add a new friend request to the gui so it can be displayed to user.
     */
    public void addFriendRequestToGui(User user) {
        phoneGuiFX.addFriendRequest(user);
    }
    /*
    Method used to send all the users contacts to the GUI so they can be displayed to the user.
     */
    public void sendFriendsToGui(ArrayList<User> contacts) {
        phoneGuiFX.displayContacts(contacts);
    }
    /*
    Method used to change the call volume.
    Sets the call in an on going call by calling AudioReceiver.setVolumeValue().
    If not in an on going call the volume value is saved in savedVolume.
     */
    public void changeCallVolume(float value) {
        if(audioReceiver != null) {
            audioReceiver.setVolumeValue(value);
            savedVolume = value;
        }else{
            savedVolume = value;
        }
    }
    /*
    method used to change the microphone volume.
    If in an on going call the microphone volume is set immediately by calling AudioRecorder.setVolumeValue().
    If not the value is saved in savedMicrophoneVolume.
     */
    public void changeMicrophoneVolume(int value) {
        if(audioRecorder != null) {
            audioRecorder.setVolumeValue(value);
            savedMicrophoneVolume = value;
        }else {
            savedMicrophoneVolume = value;
        }
    }

    /*
    Method used to relay a text message from another user to be displayed in GUI
     */
    public void sendMessageToGUI(String from, String message) {
        phoneGuiFX.sendMessageToGui(from,message);
    }
    /*
    Method used when an entire conversation is received from server to be displayed in gui.
     */
    public void sendConversationToGui(String from, ArrayList<Message> conversation) {
        phoneGuiFX.updateContactConversation(from, conversation);
    }
    /*
    Method used to request a text conversation from server.
     */
    public void requestConversationFromServer(String target) {
        try {
            ServerMessage serverMessage = new ServerMessage("CONVERSATION", username, password, target);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to send a user picture to server.
     */
    public void sendUserPictureToServer(Image image) {
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", baos);
            byte[] imageData = baos.toByteArray();
            ServerMessage serverMessage = new ServerMessage("UPDATE PICTURE", username, password, imageData);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to request friends list from server.
     */
    public void requestFriendsFromServer() {
        try {
            ServerMessage serverMessage = new ServerMessage("FRIENDS", username,password);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    Method used to notify server about logout.
     */
    public void sendLogoutToServer() {
        try {
            ServerMessage serverMessage = new ServerMessage("LOGOUT", username, password);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch (Exception e) {
           e.printStackTrace();
        }
    }
    /*
    Method called when update is received from server that a friend has come online.
    Calls PhoneGuiFX.setContactAsOnline() to display friend as online in GUI.
     */
    public void friendIsOnline(User user) {
        phoneGuiFX.setContactAsOnline(user);
    }
    /*
    Method called when update is received from server that a friend has gone offline.
    Calls PhoneGuiFX.setContactAsOffline() to display friend is offline in GUI.
     */
    public void friendIsOffline(User user) {
        phoneGuiFX.setContactAsOffline(user);
    }

    /*
    Get method to retrieve isBeingCalled boolean.
     */
    public boolean isBeingCalled() {
        return isBeingCalled;
    }

    /*
    Method to set the callingAddress variable.
     */
    public void setCallingAddress(InetAddress address) {
        callingAddress = address;
    }

    /*
    Method to get the inPhoneCall boolean.
    Returns true if user is in a phoneCall.
     */
    public boolean isInPhoneCall(){
        return inPhoneCall;
    }

    /*
    Method used to get the current callingAddress
     */
    public InetAddress getCallingAddress() {
        return callingAddress;
    }
}
