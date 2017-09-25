package ChatServer;

import CommandInterpreter.CommandInterpreter;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @Author ErkHal
 * ChatServer class for the chat server project. Initializes threads for sockets and accepts incoming connections.
 */
public class ChatServer {

    //Set for storing server's channels
    private HashSet<String> channels;
    private ServerAdmin admin;

    public ChatServer() {

        this.channels = new HashSet<>();
        this.channels.add("Lobby");
        this.admin = new ServerAdmin("admin", "1234");
    }

    public void serve() {

        try {

            //Fixed server port for the sake of testing
            ServerSocket server = new ServerSocket(1337, 3);
            ChatConsole chatConsole = new ChatConsole();
            chatConsole.register();

            while(true) {

                System.out.println("Listening to port " + server.getLocalPort());
                Socket connectedUser = server.accept();
                System.out.println("New connection established from " + connectedUser.getInetAddress().toString());

                //Gets the input and output streams from the connected user's socket.
                CommandInterpreter CI = new CommandInterpreter(connectedUser.getInputStream(),
                        new PrintStream(connectedUser.getOutputStream(), true), getChannels().get(0), this);
                Thread userThread = new Thread(CI);

                //Initializes new thread for the connected user.
                userThread.start();

            }

        } catch(IOException ioError) {

            ioError.printStackTrace();
            System.out.println("Connection error !");
        }
    }

    /**
     * Checks if the channel exists already
     * @param channelId The name of the channel
     * @return True if channel exists, else false
     */
    public boolean channelAlreadyExists(String channelId) {

        return this.channels.contains(channelId);
    }

    /**
     * Adds a new channel into the channel list
     * @param channelId The name of the new channel
     */
    public void addChannel(String channelId) {

        this.channels.add(channelId);
    }

    /**
     * Removes a channel from the channel list
     * @param channelId The name of the channel to be removed
     */
    public void removeChannel(String channelId) {

        this.channels.remove(channelId);
    }

    /**
     * Returns channels in a ArrayList
     * @return List of channels.
     */
    public ArrayList<String> getChannels() {

        return new ArrayList<String>(this.channels);
    }

    public boolean tryAdminLogin(String usrName, String passwd) {

        return admin.checkCredentials(usrName, passwd);
    }
}
