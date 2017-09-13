package EH.com.company;

import ChatServer.ChatServer;

/**
 * @author ErkHal
 * Main class for the chatserver project.
 */
public class Main {

    public static void main(String[] args) {

        ChatServer server = new ChatServer();
        server.serve();
    }
}
