/**
 * Created by pontu on 2018-03-08.
 */
import java.io.*;
import java.util.ArrayList;
/*
Holding class for serverMessage data.
 */
public class ServerMessage implements Serializable {
    private String request;
    private String username;
    private String password;
    private String recipient;
    private String message;
    private User user;
    private ArrayList<Message> messagesList;
    private ArrayList<User> userList;
    private byte[] data;

    public ServerMessage(String request) {
        this.request = request;
    }

    public ServerMessage(String request, String username, String password, ByteArrayOutputStream baos) {
        setData(baos);
        this.request = request;
        this.username = username;
        this.password = password;
    }

    public ServerMessage(String request, String username) {
        this.request = request;
        this.username = username;
    }
    public ServerMessage(String request, User user) {
        this.request = request;
        this.user = user;
    }
    public ServerMessage(String request, String username, byte[] data) {
        this.request = request;
        this.username = username;
        this.data = data;
    }

    public ServerMessage(String request, String username, String password) {
        this.request = request;
        this.username = username;
        this.password = password;
    }
    public ServerMessage(String request, String username, String password, String recipient) {
        this.request = request;
        this.username = username;
        this.password = password;
        this.recipient = recipient;
    }
    public ServerMessage(String request, String username, String password, String recipient, String message) {
        this.request = request;
        this.username = username;
        this.password = password;
        this.recipient = recipient;
        this.message = message;
    }
    public ServerMessage(String request, String username, ArrayList<Message> messagesList) {
        this.messagesList = messagesList;
        this.username = username;
        this.request = request;
    }

    public ServerMessage(String request, String username, String password, byte[] data) {
        setData(data);
        this.request = request;
        this.username = username;
        this.password = password;
    }
    public ServerMessage(String request, ArrayList<User> userList) {
        this.request = request;
        this.userList = userList;
    }

    public void setRequest(String requestString) {
        this.request = requestString;
    }

    public String getRequest() {
        return request;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setData(ByteArrayOutputStream baos) {
        data = baos.toByteArray();
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessagesList(ArrayList<Message> messagesList) {
        this.messagesList = messagesList;
    }

    public ArrayList<Message> getMessagesList() {
        return messagesList;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }
    public void setUserList(ArrayList<User>users) {
        userList = users;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }
}