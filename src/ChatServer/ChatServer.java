package ChatServer;

import CommandInterpreter.CommandInterpreter;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author ErkHal
 * ChatServer class for the chat server project. Initializes threads for sockets and accepts incoming connections.
 */
public class ChatServer {

    public ChatServer() {}

    public void serve() {

        try {

            ServerSocket server = new ServerSocket(0, 3);
            ChatConsole chatConsole = new ChatConsole();
            chatConsole.register();

            while(true) {

                System.out.println("Listening to port " + server.getLocalPort());
                Socket connectedUser = server.accept();
                System.out.println("New connection established from " + connectedUser.getInetAddress().toString());

                //Gets the input and output streams from the connected user's socket.
                CommandInterpreter CI = new CommandInterpreter(connectedUser.getInputStream(),
                        new PrintStream(connectedUser.getOutputStream(), true));
                Thread userThread = new Thread(CI);

                //Initializes new thread for the connected user.
                userThread.start();

            }

        } catch(IOException ioError) {

            ioError.printStackTrace();
        }

    }
}
