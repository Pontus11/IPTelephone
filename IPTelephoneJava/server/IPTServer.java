import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pontu on 2018-03-08.
 */
/*
Server class that takes care of the the server functionality.
It creates the database, queries the database and sends messages to the clients that
are using the iptelephones.
 */
public class IPTServer {
    private ServerSocket host;
    private ArrayList<ClientConnection> connections = new ArrayList<>();
    private HashMap<String, ClientConnection> onlineConnections = new HashMap<>();
    private Connection dbConnect;
    /*
    Standard constructor that creates all the database tables and calls the
    run() method.
     */
    IPTServer(String port) {
        createDBConnection();
        try {
            createUserTable();
            createMessageTable();
            createFriendRequestQueTable();
            createFriendRelationTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            host = new ServerSocket(19999);
        }catch(Exception e) {
            e.printStackTrace();
        }
        run();
    }

    /*
    The run method is constantly looking for new client connections
     */
    public void run() {
        while(true) {
            try {
                Socket clientSocket = host.accept();
                ClientConnection con = new ClientConnection(this, clientSocket);
                System.out.println("New connection: " + clientSocket.getInetAddress());
                connections.add(con);
                Thread.sleep(50);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Method to create db connection
     */
    private void createDBConnection() {
        try {
            String url = "jdbc:sqlite:users.db";
            String username = "narren";
            String password = "narren112asd";
            dbConnect = DriverManager.getConnection(url, username, password);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    /*
    Method to create users table in the database
     */
    private void createUserTable() throws Exception {
        try {
            PreparedStatement createSQL = dbConnect.prepareStatement("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY, timestamp DATETIME, username varchar(255), password varchar(255), profilePicture BLOB)");
            createSQL.executeUpdate();
            System.out.println("created user table");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    /*
    Method to create messages table in the database
     */
    private void createMessageTable() throws Exception {
        try {
            PreparedStatement createSQL = dbConnect.prepareStatement("CREATE TABLE IF NOT EXISTS message(id INTEGER PRIMARY KEY, timestamp DATETIME, sender varchar(255), recipient varchar(255), messageText varchar(255))");
            createSQL.executeUpdate();
            System.out.println("created message table");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method to create the friend request que table in the database
     */
    private void createFriendRequestQueTable() throws Exception {
        try {
            PreparedStatement createSQL = dbConnect.prepareStatement("CREATE TABLE IF NOT EXISTS friendRequestQue(id INTEGER PRIMARY KEY, timestamp DATETIME, sender varchar(255), recipient varchar(255))");
            createSQL.executeUpdate();
            System.out.println("created friendRequestQue table");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method to create friend relations table in the database
     */
    private void createFriendRelationTable() throws Exception {
        try {
            PreparedStatement createSQL = dbConnect.prepareStatement("CREATE TABLE IF NOT EXISTS friendship(id INTEGER PRIMARY KEY, timestamp DATETIME, friend1 varchar(255), friend2 varchar(255))");
            createSQL.executeUpdate();
            System.out.println("created friends table");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method to check if a certain user is online
     */
    private boolean isUserOnline(String username) {
        if(onlineConnections.get(username) != null) {
            return true;
        }
        else {
            return false;
        }
    }

    /*
    Method to send notification to a client that one of their friends are online
     */
    private void sendOnlineNotifications(String friend, String username) {
        if(isUserOnline(friend) && isUserOnline(username)) {
            try {
                ClientConnection conFriend = onlineConnections.get(friend);
                ClientConnection conUsername = onlineConnections.get(username);

                ServerMessage serverMessage1 = new ServerMessage("ONLINE", new User(username, onlineConnections.get(username).getSocket().getInetAddress(), getUserPictureFromDB(username)));
                ServerMessage serverMessage2 = new ServerMessage("ONLINE", new User(friend, onlineConnections.get(friend).getSocket().getInetAddress(), getUserPictureFromDB(friend)));

                conFriend.getOos().writeObject(serverMessage1);
                conUsername.getOos().writeObject(serverMessage2);

                conFriend.getOos().flush();
                conUsername.getOos().flush();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Method for handling an accepted friend request.
    Adds it into the database
     */
    public synchronized void acceptFriendship(String username, String password, String friend) {
        if(validateUser(username,password) && friendRequestExists(friend,username) && !lookupFriendship(friend, username)) {
            insertFriendshipToDB(username,friend);
            insertFriendshipToDB(friend, username);
            removeFriendRequest(friend,username);
            sendOnlineNotifications(friend, username);
        }
    }
    /*
    Method to check if friend request exists in the database.
     */
    private synchronized boolean friendRequestExists(String from, String to) {
        try {
            PreparedStatement statement = dbConnect.prepareStatement("SELECT recipient FROM friendRequestQue WHERE sender = ? AND recipient = ?");
            statement.setString(1,from);
            statement.setString(2,to);
            ResultSet result  = statement.executeQuery();
            if(result.next()) {
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    Method to remove a friend request if a response has been given.
     */
    private synchronized void removeFriendRequest(String from, String to) {
        try {
            PreparedStatement statement = dbConnect.prepareStatement("DELETE FROM friendRequestQue WHERE sender = ? AND recipient = ?");
            statement.setString(1,from);
            statement.setString(2,to);
            System.out.println("removed request");
            statement.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method called if a friend request was declined.
     */
    public synchronized void declineFriendRequest(String username, String password, String from) {
        if(validateUser(username,password)) {
            removeFriendRequest(from, username);
        }
    }

    /*
    Method used to insert a new friendship into db
     */
    private synchronized void insertFriendshipToDB(String username, String friend){
        try {
            PreparedStatement insertSQL = dbConnect.prepareStatement("INSERT INTO friendship(timestamp, friend1, friend2) VALUES (?,?,?)");

            insertSQL.setString(1, LocalDateTime.now().toString());
            insertSQL.setString(2, username);
            insertSQL.setString(3, friend);

            System.out.println("added friendship between " + username + " and " + friend);

            insertSQL.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to send a list of friends to a user.
     */
    public synchronized void sendUserFriends(ObjectOutputStream oos, String username, String password) {
        if(validateUser(username,password)) {
            try {
                PreparedStatement statement = dbConnect.prepareStatement("SELECT friend2 FROM friendship WHERE friend1 = ?");
                statement.setString(1, username);
                ResultSet queryResult = statement.executeQuery();
                ArrayList<User> friends = new ArrayList<>();
                while (queryResult.next()) {
                    String friendUsername = queryResult.getString("friend2");
                    if(onlineConnections.get(friendUsername) != null) {
                        InetAddress ipAddress = onlineConnections.get(queryResult.getString("friend2")).getSocket().getInetAddress();
                        friends.add(new User(friendUsername,ipAddress,getUserPictureFromDB(friendUsername)));
                    }else{
                        friends.add(new User(friendUsername, getUserPictureFromDB(friendUsername)));
                    }
                }
                ServerMessage serverMessage = new ServerMessage("FRIENDS", friends);
                oos.writeObject(serverMessage);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Method used to lookup if a friendship exists between two users in db.
     */
    private synchronized boolean lookupFriendship(String username, String target) {
        try {
            PreparedStatement statement = dbConnect.prepareStatement("SELECT friend1 FROM friendship WHERE friend1 = ? AND friend2 = ?");
            statement.setString(1, username);
            statement.setString(2, target);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    method used to remove a friendship.
     */
    public synchronized void removeFriendship(String username, String password, String target) {
        if(validateUser(username,password)) {
            try {
                PreparedStatement statement = dbConnect.prepareStatement("DELETE FROM friendship WHERE friend1 = ? AND friend2 = ?");
                statement.setString(1,username);
                statement.setString(2,target);
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Method used to insert a new friendship into db
     */
    private void insertUserToDB(String username, String password) {
        String currentTimeDate = LocalDateTime.now().toString();

        byte[] imgData = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            File file = new File("noProfilePic.png");
            BufferedImage img = ImageIO.read(file);
            ImageIO.write(img, "png", baos);
            imgData = baos.toByteArray();
        }catch(Exception e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement insertSQL = dbConnect.prepareStatement("INSERT INTO users(username, timestamp, password, profilePicture) VALUES (?,?,?,?)");

            insertSQL.setString(1, username);
            insertSQL.setString(2, currentTimeDate);
            insertSQL.setString(3, password);
            insertSQL.setBytes(4,imgData);

            insertSQL.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to check for illegal injections.
     */
    private String checkForIllegalInjection(String s) {
        Pattern pattern = Pattern.compile("<.*>");
        Matcher matcher = pattern.matcher(s);
        Boolean matchFound = matcher.matches();
        if(matchFound) {
            return "Censur";
        }else {
            return s;
        }
    }

    /*
    Method used to check if a user exists in the db.
     */
    public synchronized boolean userExistsDBLookup(String username) {
        try {
            PreparedStatement selectSQL = dbConnect.prepareStatement("SELECT id, timestamp, username, password FROM users");
            ResultSet queryResult = selectSQL.executeQuery();
            while(queryResult.next()) {
                if(queryResult.getString("username").equals(username)) {
                    return true;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    Method used to retrieve a users picture from the DB
     */
    private synchronized byte[] getUserPictureFromDB(String username) {
        try {
            PreparedStatement statement = dbConnect.prepareStatement("SELECT profilePicture FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet queryResult = statement.executeQuery();
            while(queryResult.next()) {
                return queryResult.getBytes("profilePicture");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    Method used to take care of a userLogin attempt
    checks if username and password is valid.
    then sends a response.
     */
    public synchronized void userLogin(ClientConnection connection, String username, String password) {
        if(validateUser(username,password)) {
            onlineConnections.put(username,connection);
            //ta bort pass?
            byte[] imageByte = getUserPictureFromDB(username);
            System.out.println(imageByte);
            ServerMessage sm = new ServerMessage("LOGIN SUCCESSFUL", username, password, getUserPictureFromDB(username));
            try {
                connection.getOos().writeObject(sm);
                connection.getOos().flush();
            }catch(Exception e) {
                e.printStackTrace();
            }
            sendUserFriendRequests(connection.getOos(),username,password);
            usersOnline(new User(username, connection.getSocket().getInetAddress()));
        }else {
            ServerMessage sm = new ServerMessage("LOGIN UNSUCCESSFUL");
            try {
                connection.getOos().writeObject(sm);
                connection.getOos().flush();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Method used to send a user a friend request.
     */
    public synchronized void sendUserFriendRequests(ObjectOutputStream oos, String username, String password) {
        try {
            ArrayList<User> friendRequests = getQuedFriendRequests(username,password);
            ServerMessage serverMessage = new ServerMessage("FRIEND REQUESTS", friendRequests);
            oos.writeObject(serverMessage);
            oos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to notify a user of another users online status.
     */
    private synchronized void usersOnline(User user) {
        for (Map.Entry<String, ClientConnection> entry : onlineConnections.entrySet()) {
            String username = entry.getKey();
            ClientConnection clientConnection = entry.getValue();
            if(lookupFriendship(username,user.getUsername())) {
                try {
                    ServerMessage serverMessage = new ServerMessage("ONLINE", user);
                    clientConnection.getOos().writeObject(serverMessage);
                    clientConnection.getOos().flush();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    Method used to send offline friends to user.
     */
    private synchronized void usersOffline(User user) {
        for (Map.Entry<String, ClientConnection> entry : onlineConnections.entrySet()) {
            String username = entry.getKey();
            ClientConnection clientConnection = entry.getValue();
            if(lookupFriendship(username,user.getUsername())) {
                try {
                    ServerMessage serverMessage = new ServerMessage("OFFLINE", user);
                    clientConnection.getOos().writeObject(serverMessage);
                    clientConnection.getOos().flush();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /*
    Method used to perform a new account creation.
    Then notifies the user if the account creation was successful or not.
     */
    public synchronized void accountCreation(ObjectOutputStream oos, String username, String password) {
        if(!userExistsDBLookup(username)) {
            insertUserToDB(username, password);
            ServerMessage serverMessage = new ServerMessage("CREATION SUCCESSFUL");
            try {
                oos.writeObject(serverMessage);
                oos.flush();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            ServerMessage serverMessage = new ServerMessage("CREATION UNSUCCESSFUL");
            try {
                oos.writeObject(serverMessage);
                oos.flush();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /*
    Method used to insert a new message into the db
     */
    public synchronized void insertMessageToDB(String from, String to, String message) {
        try {
            PreparedStatement insertSQL = dbConnect.prepareStatement("INSERT INTO message(timestamp, sender, recipient, messageText) VALUES (?,?,?,?)");

            insertSQL.setString(1, LocalDateTime.now().toString());
            insertSQL.setString(2, from);
            insertSQL.setString(3, to);
            insertSQL.setString(4, message);

            insertSQL.executeUpdate();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to send a text message from one user to another through the server.
     */
    public synchronized void sendMessageToRecipient(String username, String password, String recipient, String message) {
        if(validateUser(username,password) && userExistsDBLookup(recipient)) {
            try {
                insertMessageToDB(username, recipient, message);
                if(onlineConnections.get(recipient) != null) {
                    ObjectOutputStream oos = onlineConnections.get(recipient).getOos();
                    ServerMessage serverMessage = new ServerMessage("MESSAGE", username, null, recipient, message);
                    oos.writeObject(serverMessage);
                    oos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Method used to validate a user and check if password and username was correct.
     */
    private synchronized boolean validateUser(String username, String password) {
        try {
            PreparedStatement statement = dbConnect.prepareStatement("SELECT username FROM users WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet queryResult = statement.executeQuery();
            return queryResult.next();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /*
    Method used to prepare a friendship request.
     */
    public synchronized void prepareFriendRequest(String username, String password, String recipient) {
        if(validateUser(username,password) && userExistsDBLookup(recipient) && !friendRequestExists(username,recipient)) {
            if (onlineConnections.get(recipient) != null) {
                ClientConnection connection = onlineConnections.get(recipient);
                addFriendRequestToDB(username, recipient);
                sendFriendRequests(username, connection.getOos());
            }else{
                addFriendRequestToDB(username, recipient);
            }
        }
    }

    /*
    Method used to send a friendship request.
     */
    private synchronized void sendFriendRequests(String username, ObjectOutputStream oos) {
        try {
            ServerMessage serverMessage = new ServerMessage("FRIEND REQUEST", new User(username, getUserPictureFromDB(username)));
            oos.writeObject(serverMessage);
            oos.flush();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to add a friendRequest to the db
     */
    private synchronized void addFriendRequestToDB(String sender, String recipient) {
        try {
            PreparedStatement insertSQL = dbConnect.prepareStatement("INSERT INTO friendRequestQue(timestamp, sender, recipient) VALUES (?,?,?)");
            insertSQL.setString(1, LocalDateTime.now().toString());
            insertSQL.setString(2, sender);
            insertSQL.setString(3, recipient);
            insertSQL.executeUpdate();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method used to send a user all their que'd friend requests when they log online.
     */
    public synchronized ArrayList<User> getQuedFriendRequests(String username, String password) {
        if(validateUser(username, password)) {
            try {
                PreparedStatement statement = dbConnect.prepareStatement("SELECT sender FROM friendRequestQue WHERE recipient = ?");
                statement.setString(1, username);
                ResultSet queryResult = statement.executeQuery();
                ArrayList<User> friendRequests = new ArrayList<>();
                while (queryResult.next()) {
                    String sender = (queryResult.getString("sender"));
                    byte[] profilePicture = getUserPictureFromDB(sender);
                    User user = new User(sender, profilePicture);
                    friendRequests.add(user);
                }
                return friendRequests;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    method used to retrieve a conversation between two users from db.
     */
    public synchronized ArrayList<String> getConversationFromDB(ObjectOutputStream oos, String username, String password, String recipient) {
        if(validateUser(username,password)) {
            try {
                PreparedStatement statement = dbConnect.prepareStatement("SELECT sender, recipient, messageText FROM message WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?)");
                statement.setString(1, username);
                statement.setString(2, recipient);
                statement.setString(3, recipient);
                statement.setString(4, username);

                ResultSet queryResult = statement.executeQuery();

                ArrayList<Message> messages = new ArrayList<>();
                while (queryResult.next()) {
                    String from = queryResult.getString("sender");
                    String to = queryResult.getString("recipient");
                    String message = queryResult.getString("messageText");
                    messages.add(new Message(from, to, message));
                }
                ServerMessage serverMessage = new ServerMessage("CONVERSATION",recipient, messages);
                oos.writeObject(serverMessage);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    Method used to update users picture in db.
     */
    public synchronized void updateUserPictureInDB(String username, String password, byte[] pictureData){
        if(validateUser(username,password)) {
            try {
                System.out.println("updating picture");
                PreparedStatement statement = dbConnect.prepareStatement("UPDATE users SET profilePicture = ? WHERE username = ?");
                System.out.println(pictureData);
                statement.setBytes(1, pictureData);
                statement.setString(2, username);
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Method used to logout the user.
     */
    public synchronized void logoutUser(ClientConnection connection, String username, String passwordd) {
        if(validateUser(username,passwordd)) {
            usersOffline(new User(username, connection.getSocket().getInetAddress()));
            onlineConnections.remove(username);
        }
    }


    /*
    Method used to take care of killing a client connection if user goes offline.
     */
    public synchronized void killThread(ClientConnection con) {
        String usernameToRemove = null;
        for (Map.Entry<String, ClientConnection> entry : onlineConnections.entrySet()) {
            String username = entry.getKey();
            ClientConnection clientConnection = entry.getValue();
            if(clientConnection == con) {
                usersOffline(new User(username, clientConnection.getSocket().getInetAddress()));
                usernameToRemove = username;
            }
        }
        if(usernameToRemove != null) {
            onlineConnections.remove(usernameToRemove);
        }
        connections.remove(con);
    }

    /*
    Method used to get connections.
     */
    public synchronized ArrayList<ClientConnection> getConnections() {
        return connections;
    }
    /*
    Method used to get onlineConnections.
     */
    public synchronized HashMap<String, ClientConnection> getOnlineConnections() {
        return onlineConnections;
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
        } catch(Exception e) {
            System.out.println(e);
        }
        if(args.length > 0) {
            IPTServer iptServer = new IPTServer(args[0]);
        }else{
            System.out.println("hey");
            IPTServer iptServer = new IPTServer("19999");
        }
    }
}
