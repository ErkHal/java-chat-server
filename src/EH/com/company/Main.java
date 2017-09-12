package EH.com.company;

import ChatServer.ChatServer;
import CommandInterpreter.CommandInterpreter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import static sun.java2d.cmm.ColorTransform.In;

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
