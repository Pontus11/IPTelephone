import java.io.Serializable;

/**
 * Created by pontu on 2018-03-19.
 */
/*
Holding class for message data.
 */
public class Message implements Serializable{
    private String from;
    private String to;
    private String message;

    public Message(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    /*
    Getter and setter methods for all the data.
     */
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
