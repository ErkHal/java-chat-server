package ChatHistoryInterfaces;

import ChatMessage.ChatMessage;

/**
 * @author ErkHal
 * Observation pattern implementation for the Chat history.
 */
public interface ChatHistoryObservable {

    void notifyObservers(ChatMessage chatMessage);
    void addObserver(ChatHistoryObserver observer);
    void removeObserver(ChatHistoryObserver observer);
}
