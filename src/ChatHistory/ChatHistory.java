package ChatHistory;


import ChatHistoryInterfaces.ChatHistoryObservable;
import ChatHistoryInterfaces.ChatHistoryObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import ChatMessage.ChatMessage;

/**
 * @author ErkHal
 * This is the singleton class for the chatserver's message history.
 */
public class ChatHistory implements ChatHistoryObservable {

    private static ChatHistory ourInstance = new ChatHistory();

    HashSet<ChatHistoryObserver> observers;

    ArrayList<ChatMessage> messageHistory;

    private ChatHistory() {

        this.messageHistory = new ArrayList<ChatMessage>();
        this.observers = new HashSet<>();
    }

    public static ChatHistory getInstance() {
        return ourInstance;
    }

    /**
     * Inserts message to chat history and updates chat
     * @param message
     */
    public void insertMessage(ChatMessage message) {

        messageHistory.add(message);
        notifyObservers(message);
    }

    /**
     * Prints out the whole chat history to the client
     * @return
     */
    @Override
    public String toString() {

        String wholeMessageHistory = "";
        wholeMessageHistory += "---- START OF CHAT HISTORY ----\r\n";

        for(ChatMessage msg : this.messageHistory) {

            wholeMessageHistory += msg.toString() + "\r\n";
        }

        wholeMessageHistory += "---- END OF CHAT HISTORY ----";
        return wholeMessageHistory;
    }

    /**
     * Tells all the users to update their chat with the newest message
     */
    @Override
    public void notifyObservers(ChatMessage chatMessage) {

        //System.out.println("Notifying all observers about the change in the chat history");

        for (ChatHistoryObserver obs : observers)
            obs.update(chatMessage);

    }

    /**
     * Adds a new user to observers
     * @param observer
     */
    @Override
    public void addObserver(ChatHistoryObserver observer) {

        this.observers.add(observer);

    }

    /**
     * Removes user from the observers list
     * @param observer
     */
    @Override
    public void removeObserver(ChatHistoryObserver observer) {

        this.observers.remove(observer);

    }

    /**
     * Broadcasts a server message to all users. Executed when a new user connects or disconnects
     */
    public void broadcastServerMessage(String message) {

        Calendar calendar = Calendar.getInstance();
        Date broadcastTime = calendar.getTime();

        ChatMessage serverMessage = new ChatMessage(message,"SERVER BROADCAST", broadcastTime);
        ChatHistory.getInstance().insertMessage(serverMessage);

    }
}
