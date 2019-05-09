import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by pontu on 2018-03-18.
 */

/*
User class for holding user data.
 */
public class User implements Serializable {
    private String username;
    private InetAddress lastKnownIp;
    private byte[] profilePicture;

    public User(String name) {
        username = name;
    }
    public User(String name, InetAddress ip) {
        username = name;
        lastKnownIp = ip;
    }
    public User(String name, byte[] pic) {
        username = name;
        profilePicture = pic;
    }
    public User(String name, InetAddress ip, byte[] pic) {
        username = name;
        lastKnownIp = ip;
        profilePicture = pic;
    }

    public String getUsername() {
        return username;
    }
    public InetAddress getLastKnownIp() {
        return lastKnownIp;
    }
    public byte[] getProfilePicture() {
        return profilePicture;
    }
    public void setUsername(String name) {
        username = name;
    }
    public void setLastKnownIp(InetAddress ip) {
        lastKnownIp = ip;
    }
    public void setProfilePicture(byte[] pic) {
        profilePicture = pic;
    }
}
