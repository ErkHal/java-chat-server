package CommandInterpreter;

import ChatHistory.ChatHistory;
import ChatHistoryInterfaces.ChatHistoryObserver;
import ChatMessage.ChatMessage;
import UserNameList.UserNameList;

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
    private String userName;

    //Command recognition symbol. A command has to start and end with this symbol.
    private static final char COMMAND_SYMBOL = '%';

    //Username reserved for server broadcast
    private static final String SERVER_USERNAME = "SYSTEM BROADCAST";

    //Maximum user name length
    private static final int MAX_USERNAME_LENGTH = 12;

    public CommandInterpreter(InputStream inputStream, PrintStream printStream) {

        this.inputStream = inputStream;
        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);
        this.commandSet = loadCommandSet();
        this.userName = null;

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
                    printStream.println("Type " + COMMAND_SYMBOL + "help" + COMMAND_SYMBOL + " for help \r\n");
                    helpDisplayed = true;
                }

                String message;
                message = scanner.nextLine();

                //Checks if given message is a command
                if (commandSet.contains(message)) {

                    if (message.substring(1, message.length() - 1).equals("help")) {

                        printStream.println("\r\n        All commands and their explanations: \r\n" +
                                "------------------------------------------------------------------\r\n" +
                                COMMAND_SYMBOL + "help" + COMMAND_SYMBOL + " - Displays this list of commands" + "\r\n" +
                                COMMAND_SYMBOL + "user" + COMMAND_SYMBOL + " - Sets username. Must be set before messaging and can only be set once per session." + "\r\n" +
                                COMMAND_SYMBOL + "history" + COMMAND_SYMBOL + " - Displays message history" + "\r\n" +
                                COMMAND_SYMBOL + "quit" + COMMAND_SYMBOL + " - Disconnects user from the chat" + "\r\n");
                    }

                    //Quit cmd functionality
                    if (message.substring(1, message.length() - 1).equals("quit")) {

                        printStream.println("Goodbye " + this.userName + "!");
                        inputStream.close();
                    }

                    //History cmd functionality. Cannot see history without username.
                    if (message.substring(1, message.length() - 1).equals("history")) {

                        if(!this.userName.equals("")) {
                            printStream.println("Chat history: ");
                            printStream.println(ChatHistory.getInstance().toString());

                        } else {

                            printStream.println("Cannot see history without username !");
                        }
                    }

                    //User cmd functionality. Allows user to set a username. Cannot see or send messages to chat without a username
                    if (message.substring(1, message.length() - 1).equals("user")) {

                        boolean userNameSet = false;
                        while (!userNameSet) {

                            printStream.println("Give desired username, has to be at least two characters long" +
                                    " and maximum length " + MAX_USERNAME_LENGTH + "characters");
                            String input = scanner.nextLine();
                            String possibleUserName = input.trim();

                            if (possibleUserName.length() >= 2 && possibleUserName.length() <= MAX_USERNAME_LENGTH && !possibleUserName.equals(SERVER_USERNAME)) {

                                printStream.println(possibleUserName.length());

                                if (!(UserNameList.checkIfUserExists(possibleUserName))) {

                                    while (true) {

                                        printStream.println("Selected username: " + possibleUserName + " is available, use Y/N ?");
                                        String choice = scanner.nextLine();

                                        if (choice.equals("Y") || choice.equals("y")) {

                                            this.userName = possibleUserName;
                                            UserNameList.addUser(possibleUserName);
                                            printStream.println("Username set as " + possibleUserName + ", start chatting !");
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

                        //Sends message to server
                    } else if (this.userName != null) {

                        Calendar calendar = Calendar.getInstance();
                        Date dateTime = calendar.getTime();
                        ChatMessage msg = new ChatMessage(message, this.userName, dateTime);
                        ChatHistory.getInstance().insertMessage(msg);

                        System.out.println(msg.toString());

                    } else {

                        printStream.println("You don't have a username ! Please set one with the " + COMMAND_SYMBOL +
                                "user" + COMMAND_SYMBOL + " command.");
                    }
                }

            } catch (Exception exception) {

            UserNameList.removeUser(this.userName);
            ChatHistory.getInstance().removeObserver(this);
            System.out.println("User " + this.userName + " disconnected");
        }
    }

    /**
     * Loads the commandset with commands
     * @return
     */
    private HashSet<String> loadCommandSet() {

        HashSet<String> tempList = new HashSet<String>();

        tempList.add(COMMAND_SYMBOL + "help" + COMMAND_SYMBOL);
        tempList.add(COMMAND_SYMBOL + "user" + COMMAND_SYMBOL);
        tempList.add(COMMAND_SYMBOL + "history" + COMMAND_SYMBOL);
        tempList.add(COMMAND_SYMBOL + "quit" + COMMAND_SYMBOL);

        return tempList;
    }

    /**
     * Updates the client's chat history
     * @param chatMessage
     */
    @Override
    public void update(ChatMessage chatMessage) {

        printStream.println(chatMessage);
    }
}
