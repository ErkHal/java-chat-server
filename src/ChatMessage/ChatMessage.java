package ChatMessage;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ErkHal
 * ChatMessage class for the Chat server application. A message has a sender, a timestamp and a message itself
 */
public class ChatMessage {

    String message;
    String sender;
    Date sendTime;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM 'at' HH:mm");

    public ChatMessage(String msg, String username, Date timeStamp) {

        this.message = msg;
        this.sender = username;
        this.sendTime = timeStamp;
    }

    @Override
    public String toString() {

        return dateFormat.format(sendTime) + "|" + sender + " : " + this.message;

    }
}
