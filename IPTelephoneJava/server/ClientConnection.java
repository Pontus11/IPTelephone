import com.sun.deploy.util.SessionState;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by pontu on 2018-03-08.
 */
/*
Class used for connection with client.
Waits for messages from client and redirects them to the correct method in the IPTServer.
 */
public class ClientConnection extends Thread {
    private boolean alive = true;
    private boolean active = true;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private IPTServer server;
    public ClientConnection(IPTServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        }catch(Exception e) {
            e.printStackTrace();
        }
        start();
    }

    /*
    Run method that constantly just waits for new messages from
    client. Once a message is received the handleRequest() method is called.
     */
    public void run() {
        ServerMessage request;
        while(alive) {
            while(active) {
                try {
                    Thread.sleep(100);
                    while((request = (ServerMessage) ois.readObject()) != null) {
                        System.out.println("Recieved: " + request + " from client");
                        handleRequest(request);
                    }
                }catch(Exception e) {
                    System.out.println("Connection lost");
                }
                try {
                    active = false;
                    alive = false;
                    socket.close();
                    server.killThread(this);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(25);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Help method used to redirect the server message to the correct method depending on the
    request type.
     */
    public void handleRequest(ServerMessage serverMessage) {
        System.out.println(serverMessage.getRequest());
        if (serverMessage.getRequest().equals("LOGIN")) {
            System.out.println(serverMessage.getUsername() + serverMessage.getPassword());
            server.userLogin(this, serverMessage.getUsername(), serverMessage.getPassword());
        }
        if (serverMessage.getRequest().equals("CREATE")) {
            server.accountCreation(oos, serverMessage.getUsername(), serverMessage.getPassword());
        }
        if (serverMessage.getRequest().equals("MESSAGE")) {
            server.sendMessageToRecipient(serverMessage.getUsername(), serverMessage.getPassword(), serverMessage.getRecipient(), serverMessage.getMessage());
        }
        if (serverMessage.getRequest().equals("CONVERSATION")) {
            server.getConversationFromDB(oos, serverMessage.getUsername(), serverMessage.getPassword(), serverMessage.getRecipient());
        }
        if(serverMessage.getRequest().equals("FRIEND REQUEST")) {
            server.prepareFriendRequest(serverMessage.getUsername(), serverMessage.getPassword(), serverMessage.getRecipient());
            System.out.println(serverMessage.getUsername() + serverMessage.getPassword() + serverMessage.getRecipient());
        }
        if(serverMessage.getRequest().equals("ACCEPT REQUEST")) {
            server.acceptFriendship(serverMessage.getUsername(),serverMessage.getPassword(),serverMessage.getRecipient());
        }
        if(serverMessage.getRequest().equals("DECLINE REQUEST")) {
            server.declineFriendRequest(serverMessage.getUsername(),serverMessage.getPassword(),serverMessage.getRecipient());
        }
        if(serverMessage.getRequest().equals("UPDATE PICTURE")) {
            server.updateUserPictureInDB(serverMessage.getUsername(),serverMessage.getPassword(), serverMessage.getData());
        }
        if(serverMessage.getRequest().equals("LOGOUT")) {
            server.logoutUser(this, serverMessage.getUsername(),serverMessage.getPassword());
        }
        if(serverMessage.getRequest().equals("FRIENDS")) {
            server.sendUserFriends(oos, serverMessage.getUsername(),serverMessage.getPassword());
        }
    }

    /*
    Method used to get the object output stream oos.
     */
    public ObjectOutputStream getOos() {
        return oos;
    }

    /*
    Method used to get the socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /*
    Method used to set the alive status of the thread.
     */
    public void setAlive(boolean a) {
        active = a;
        alive = a;
    }

    /*
    Method used to set the active status of the thread.
     */
    public void setActive(boolean a) {
        active = a;
    }
}
