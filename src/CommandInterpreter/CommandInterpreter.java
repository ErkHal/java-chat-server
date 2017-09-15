package CommandInterpreter;

import ChatHistory.ChatHistory;
import ChatHistoryInterfaces.ChatHistoryObserver;
import ChatMessage.ChatMessage;
import User.User;
import UsersList.UsersList;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * @author ErkHal
 * CommandInterpreter package for chatserver project
 * Interprets the users inputs and treats them as commands or messages
 */

public class CommandInterpreter implements Runnable, ChatHistoryObserver {

    private InputStream inputStream;
    private PrintStream printStream;
    private Scanner scanner;
    private HashSet<String> commandSet;
    private User user;

    //Command recognition symbol. A command has to start and end with this symbol.
    private static final char COMMAND_SYMBOL = '%';

    //Username reserved for server broadcast
    private static final String SERVER_USERNAME = "SYSTEM BROADCAST";

    //Maximum user name length
    private static final int MAX_USERNAME_LENGTH = 12;

    /**
     * Constructor for CommandInterpreter. Declares a null user first before setting name. Assigns default channel first.
     * @param inputStream
     * @param printStream
     */
    public CommandInterpreter(InputStream inputStream, PrintStream printStream) {

        this.inputStream = inputStream;
        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);
        this.commandSet = loadCommandSet();
        this.user = new User("", "Lobby");

    }

    /**
     * Listens for commands of the user and handles it correctly
     */
    public void run() {

        boolean running = true;
        boolean helpDisplayed = false;

        try {

            while (running) {

                if (!helpDisplayed) {
                    printStream.println("Connection established ! ");
                    printStream.println("Type " + COMMAND_SYMBOL + "help for help \n");
                    helpDisplayed = true;
                }

                String message;
                message = scanner.nextLine();
                message = message.trim();

                //Checks if given message is a command
                if (commandSet.contains(message)) {

                    if (message.equals(COMMAND_SYMBOL + "help")) {

                        printStream.println("\n        All commands and their explanations: \r\n" +
                                "------------------------------------------------------------------\r\n" +
                                COMMAND_SYMBOL + "help - Displays this list of commands\r\n" +
                                COMMAND_SYMBOL + "user - Sets username. Must be set before messaging !\r\n" +
                                COMMAND_SYMBOL + "history - Displays message history for this channel\r\n" +
                                COMMAND_SYMBOL + "channel - Displays your current channel and all users in this channel\r\n" +
                                COMMAND_SYMBOL + "join -  Join another channel with this command\r\n" +
                                COMMAND_SYMBOL + "online - Displays every user online\r\n" +
                                COMMAND_SYMBOL + "quit - Disconnects user from the chat \r\n");
                    }

                    //Quit cmd functionality
                    if (message.equals(COMMAND_SYMBOL + "quit")) {

                        printStream.println("Goodbye " + this.user.getUserName() + "!");
                        inputStream.close();
                    }

                    //History cmd functionality. Cannot see history without username.
                    if (message.equals(COMMAND_SYMBOL + "history")) {

                        if(!this.user.getUserName().equals("")) {
                            printStream.println("Chat history: ");
                            printStream.println(ChatHistory.getInstance().toString(user.getCurrentChannel()));

                        } else {

                            printStream.println("Cannot see history without username !");
                        }
                    }

                    //User cmd functionality. Allows user to set a username. Cannot see or send messages to chat without a username
                    if (message.equals(COMMAND_SYMBOL + "user")) {

                        boolean userNameSet = false;
                        while (!userNameSet) {

                            printStream.println("Give desired username, has to be at least two characters long" +
                                    " and maximum length " + MAX_USERNAME_LENGTH + "characters");
                            String input = scanner.nextLine();
                            String possibleUserName = input.trim();
                            user.setUserName(possibleUserName);

                            if (possibleUserName.length() >= 2 && possibleUserName.length() <= MAX_USERNAME_LENGTH && !possibleUserName.equals(SERVER_USERNAME)) {

                                if (!(UsersList.checkIfUserNameExists(user.getUserName()))) {

                                    while (true) {

                                        printStream.println("Selected username: " + possibleUserName + " is available, use Y/N ?");
                                        String choice = scanner.nextLine();

                                        if (choice.equals("Y") || choice.equals("y")) {

                                            user.setUserName(possibleUserName);
                                            UsersList.addUser(user);
                                            printStream.println("Username set as " + user.getUserName() + ", start chatting !");
                                            printStream.println("You are now chatting in: " + user.getCurrentChannel());
                                            userNameSet = true;

                                            //Adds this user to the chat history observers
                                            ChatHistory.getInstance().addObserver(this);

                                            break;

                                        }

                                        if (choice.equals("N") || choice.equals("n")) {

                                            printStream.println("Username not selected !");
                                            break;

                                        } else if (!choice.equals("n") && !choice.equals("N") && !choice.equals("Y") && !choice.equals("Y")) {

                                            printStream.println("Invalid input, answer either Y or N !");
                                        }
                                    }

                                } else {

                                    printStream.println("This username is already in use ! Here's a suggestion: " + possibleUserName + "22");

                                }

                            } else {

                                printStream.println("Username too short or too long, please choose another !");
                            }
                        }
                    }

                    //Channel cmd functionality. Shows current channel and all users in it.
                    if(message.equals(COMMAND_SYMBOL + "channel")) {

                        printStream.println("You are now chatting in: " + user.getCurrentChannel());

                        ArrayList<User> channelUsers = UsersList.getChannelUsersInList(user.getCurrentChannel());
                        printStream.println("---- Users in channel " + user.getCurrentChannel() + " ----");
                        for (User user : channelUsers) {

                            printStream.println(user.getUserName());
                        }

                    }

                    //Join cmd functionality. Lists all channels and allows user to join another channel
                    if(message.equals(COMMAND_SYMBOL + "join")) {
                        //TODO: add join command functionality
                    }

                    //Online cmd functionality. Shows all users online
                    if(message.equals(COMMAND_SYMBOL + "online")) {

                        printStream.println("---- Users online ----");

                        for(User usr : UsersList.getAllUsersInList()) {

                            printStream.println(usr.getUserName() + " @ " + usr.getCurrentChannel());
                        }
                    }

                        //Sends message to server
                    } else if (!user.getUserName().equals("")) {

                        if(!message.equals("")) {

                            Calendar calendar = Calendar.getInstance();
                            Date dateTime = calendar.getTime();
                            ChatMessage msg = new ChatMessage(message, user.getUserName(), dateTime, user.getCurrentChannel());
                            ChatHistory.getInstance().insertMessage(msg);

                        } else {

                            printStream.println("Cannot send an empty message");
                        }

                    } else {

                        printStream.println("You don't have a username ! Please set one with the " + COMMAND_SYMBOL + "user command.");
                    }
                }

            } catch (Exception exception) {

            UsersList.removeUser(user);
            ChatHistory.getInstance().removeObserver(this);
            System.out.println("User " + user.getUserName() + " disconnected");
        }
    }

    /**
     * Loads the commandset with commands
     * @return commandList
     */
    private HashSet<String> loadCommandSet() {

        HashSet<String> commandSet = new HashSet<>();

        commandSet.add(COMMAND_SYMBOL + "help");
        commandSet.add(COMMAND_SYMBOL + "user");
        commandSet.add(COMMAND_SYMBOL + "history");
        commandSet.add(COMMAND_SYMBOL + "channel");
        commandSet.add(COMMAND_SYMBOL + "join");
        commandSet.add(COMMAND_SYMBOL + "online");
        commandSet.add(COMMAND_SYMBOL + "quit");

        return commandSet;
    }

    /**
     * Updates the client's chat history
     * @param chatMessage a ChatMessage object. Contains sender, timestamp, channel and message.
     */
    @Override
    public void update(ChatMessage chatMessage) {

        if(chatMessage.getChannel().equals(user.getCurrentChannel()) || chatMessage.getChannel().equals("SERVER")){
            printStream.println(chatMessage);
        }
    }
}
