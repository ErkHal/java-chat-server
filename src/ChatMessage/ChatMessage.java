package ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ErkHal
 * ChatMessage class for the Chat server application. A message has a sender, a timestamp and a message itself
 */
public class ChatMessage {

    private String message;
    private String sender;
    private Date sendTime;
    private String channel;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM 'at' HH:mm");

    public ChatMessage(String msg, String username, Date timeStamp, String channel) {

        this.message = msg;
        this.sender = username;
        this.sendTime = timeStamp;
        this.channel = channel;
    }

    @Override
    public String toString() {

        return dateFormat.format(sendTime) + "|" + sender + " : " + this.message;

    }

    public String getChannel() {

        return this.channel;
    }
}
