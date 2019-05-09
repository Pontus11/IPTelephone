/**
 * Created by pontu on 2018-03-09.
 */
import java.io.*;
import java.net.*;
/*
Class for dealing with server communication.
sets up sockets and object input stream to read server messages.
 */
public class ServerReply extends Thread{
    private Socket socket;
    private BufferedReader br;
    private boolean alive = true;
    private boolean active = true;
    private IPTele ipTele;
    private ObjectInputStream ois;
    private ServerMessage serverMessage;
    private BufferedReader in;

    ServerReply(Socket skt, IPTele ipt){
        socket = skt;
        ipTele = ipt;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        }catch(Exception e) {
            e.printStackTrace();
        }
        start();
    }
    /*
    Method that is constantly waiting for a message from the server.
    Once a message is received the handleReply() method is called.
     */
    public void run() {
        while(alive) {
            while(active) {
                try {
                    Thread.sleep(100);
                    ServerMessage serverMessage;
                    while((serverMessage = (ServerMessage) ois.readObject()) != null) {
                        if(serverMessage.getData() != null) {
                            this.serverMessage = serverMessage;

                        }
                        handleReply(serverMessage);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("Not connected to server");
                    active = false;
                    alive = false;
                }
            }
        }
    }
    /*
    Method to redirect message to the correct method in the IPTele class.
     */
    private void handleReply(ServerMessage serverMessage) {
        if(serverMessage.getRequest().equals("LOGIN SUCCESSFUL") && serverMessage.getUsername() != null && serverMessage.getPassword() != null) {
            ipTele.loginSuccessful(serverMessage.getUsername(), serverMessage.getPassword(), serverMessage.getData());
        }else if(serverMessage.getRequest().equals("LOGIN UNSUCCESSFUL")) {
            ipTele.loginUnsuccessful();
        }else if(serverMessage.getRequest().equals("CREATION UNSUCCESSFUL")) {
            ipTele.accountCreationServerResponse(false);
        }else if(serverMessage.getRequest().equals("CREATION SUCCESSFUL")) {
            ipTele.accountCreationServerResponse(true);
        }else if(serverMessage.getRequest().equals("MESSAGE")) {
            ipTele.sendMessageToGUI(serverMessage.getUsername(), serverMessage.getMessage());
        }else if(serverMessage.getRequest().equals("CONVERSATION")) {
            ipTele.sendConversationToGui(serverMessage.getUsername(), serverMessage.getMessagesList());
        }else if(serverMessage.getRequest().equals("FRIEND REQUESTS")) {
            ipTele.displayFriendRequestsInGUI(serverMessage.getUserList());
        }else if(serverMessage.getRequest().equals("FRIEND REQUEST")) {
            ipTele.addFriendRequestToGui(serverMessage.getUser());
        }else if(serverMessage.getRequest().equals("FRIENDS")) {
            ipTele.sendFriendsToGui(serverMessage.getUserList());
        }else if(serverMessage.getRequest().equals("ONLINE")) {
            ipTele.friendIsOnline(serverMessage.getUser());
        }else if(serverMessage.getRequest().equals("OFFLINE")) {
            ipTele.friendIsOffline(serverMessage.getUser());
        }
    }
    public ServerMessage getStored() {
        return serverMessage;
    }
    public void setAlive(boolean a) {
        active = a;
        alive = a;
    }
    public void setActive(boolean a) {
        active = a;
    }
}
