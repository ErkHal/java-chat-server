package ChatHistoryInterfaces;

import ChatMessage.ChatMessage;

/**
 * @author ErkHal
 * Observation pattern implementation for the Chat history.
 */
public interface ChatHistoryObserver {

    void update(ChatMessage chatMessage);
}
