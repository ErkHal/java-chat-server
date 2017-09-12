package ChatServer;

import ChatHistory.ChatHistory;
import ChatHistoryInterfaces.ChatHistoryObserver;
import ChatMessage.ChatMessage;

/**
 * @Author ErkHal
 * ChatConsole class for the chat server application.
 */
public class ChatConsole implements ChatHistoryObserver {

    public ChatConsole() {}

    public void register() {

        ChatHistory.getInstance().addObserver(this);
    }

    @Override
    public void update(ChatMessage chatMessage) {

        System.out.println(chatMessage.toString());
    }
}
