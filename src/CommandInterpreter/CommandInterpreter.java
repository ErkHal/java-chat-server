package CommandInterpreter;

import ChatHistory.ChatHistory;
import ChatHistoryInterfaces.ChatHistoryObserver;
import ChatMessage.ChatMessage;
import ChatServer.ChatServer;
import User.User;
import UsersList.UsersList;

import java.io.IOException;
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
    private ChatServer currentServer;
    private boolean running;

    //Command recognition symbol. A command has to start and end with this symbol.
    private static final char COMMAND_SYMBOL = '!';

    //Username reserved for server broadcast
    private static final String SERVER_USERNAME = "SYSTEM BROADCAST";

    //Maximum user name length
    private static final int MAX_USERNAME_LENGTH = 12;

    /**
     * Constructor for CommandInterpreter. Declares a null user first before setting name. Assigns default channel first.
     * @param inputStream
     * @param printStream
     */
    public CommandInterpreter(InputStream inputStream, PrintStream printStream, String defaultChannel, ChatServer currentServer) {

        this.inputStream = inputStream;
        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);
        this.commandSet = loadCommandSet();
        this.user = new User("", defaultChannel);
        this.currentServer = currentServer;
        this.running = true;

    }

    /**
     * Listens for commands of the user and handles it correctly
     */
    public void run() {

        boolean helpDisplayed = false;

        try {

            while (running) {

                if (!helpDisplayed) {
                    printStream.println("Connection established ! ");
                    printStream.println("Type " + COMMAND_SYMBOL + "help for help \r\n");
                    helpDisplayed = true;
                }

                String message;
                message = scanner.nextLine();
                message = message.trim();

                //Checks if given message is a command
                if (commandSet.contains(message)) {

                    if (message.equals(COMMAND_SYMBOL + "help")) {

                        printStream.println("\r\n        All commands and their explanations: \r\n" +
                                "------------------------------------------------------------------\r\n" +
                                COMMAND_SYMBOL + "help - Displays this list of commands\r\n" +
                                COMMAND_SYMBOL + "user - Sets username. Must be set before messaging !\r\n" +
                                COMMAND_SYMBOL + "history - Displays message history for this channel\r\n" +
                                COMMAND_SYMBOL + "channel - Displays your current channel and all users in this channel\r\n" +
                                COMMAND_SYMBOL + "join -  Join another channel with this command\r\n" +
                                COMMAND_SYMBOL + "create - Create a new channel and connect to that one\r\n" +
                                COMMAND_SYMBOL + "login - Login as administrator\r\n" +
                                COMMAND_SYMBOL + "online - Displays every user online\r\n" +
                                COMMAND_SYMBOL + "quit - Disconnects user from the chat");

                        //admin access commands
                        if(this.user.isAdminAccess()) {

                            printStream.println("---- ADMIN MENU ----");
                            printStream.println(
                                    COMMAND_SYMBOL + "remove - remove channels.\r\n" +
                                    COMMAND_SYMBOL + "kick - kick user");
                                    //COMMAND_SYMBOL + "mute - prevent user from sending messages");

                        }
                    }

                    //quit cmd functionality
                    if (message.equals(COMMAND_SYMBOL + "quit")) {

                        printStream.println("Goodbye " + this.user.getUserName() + "!");
                        inputStream.close();
                    }

                    //history cmd functionality. Cannot see history without username.
                    if (message.equals(COMMAND_SYMBOL + "history")) {

                        if (!this.user.getUserName().equals("")) {
                            printStream.println("Chat history: ");
                            printStream.println(ChatHistory.getInstance().toString(user.getCurrentChannel()));

                        } else {

                            printStream.println("Cannot see history without username !");
                        }
                    }

                    //user cmd functionality. Allows user to set a username. Cannot see or send messages to chat without a username
                    if (message.equals(COMMAND_SYMBOL + "user")) {

                        boolean userNameSet = false;
                        while (!userNameSet) {

                            printStream.println("Give desired username, has to be at least two characters long" +
                                    " and maximum length " + MAX_USERNAME_LENGTH + "characters");
                            String input = scanner.nextLine();
                            String possibleUserName = input.trim();

                            if (possibleUserName.length() >= 2 && possibleUserName.length() <= MAX_USERNAME_LENGTH && !possibleUserName.equals(SERVER_USERNAME)) {

                                if (!(UsersList.getInstance().checkIfUserNameExists(possibleUserName))) {

                                    while (true) {

                                        printStream.println("Selected username: " + possibleUserName + " is available, use Y/N ?");
                                        String choice = scanner.nextLine();

                                        if (choice.equals("Y") || choice.equals("y")) {

                                            user.setUserName(possibleUserName);
                                            UsersList.getInstance().addUser(this);
                                            printStream.println("Username set as " + this.user.getUserName() + ", start chatting !");
                                            printStream.println("You are now chatting in: " + this.user.getCurrentChannel());
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

                    //channel cmd functionality. Shows current channel and all users in it.
                    if (message.equals(COMMAND_SYMBOL + "channel")) {

                        printStream.println("You are now chatting in: " + this.user.getCurrentChannel());

                        ArrayList<CommandInterpreter> channelUsers = UsersList.getInstance().getChannelUsersInList(this.user.getCurrentChannel());
                        printStream.println("---- Users in channel " + this.user.getCurrentChannel() + " ----");
                        for (CommandInterpreter user : channelUsers) {

                            printStream.println(user.getUser().getUserName());
                        }

                    }

                    //join cmd functionality. Lists all channels and allows user to join another channel
                    if (message.equals(COMMAND_SYMBOL + "join")) {
                        ArrayList<String> channels = currentServer.getChannels();

                        printStream.println("---- CHANNELS ----");
                        for (String channel : channels) {
                            printStream.println(channel);
                        }
                        printStream.println("------------------");

                        boolean commandRunning = true;
                        while (commandRunning) {

                            printStream.println("Type the name of a channel to connect to that one, or type " + COMMAND_SYMBOL
                                    + " to stay in this channel");

                            String temp = scanner.nextLine();
                            String input = temp.trim();

                            // Join a new channel if input is valid, otherwise print out message
                            if (!input.equals(""+COMMAND_SYMBOL)) {

                                if (channels.contains(input)) {
                                    this.user.setCurrentChannel(input);
                                    printStream.println("Switched channel to " + this.user.getCurrentChannel());
                                    commandRunning = false;
                                } else {
                                    printStream.println("That channel doesn't exist !");
                                }
                            } else {

                                printStream.println("Staying in channel " + this.user.getCurrentChannel());
                                commandRunning = false;
                            }
                        }
                    }

                    //create cmd functionality. Allows users to create new channels
                    if(message.equals(COMMAND_SYMBOL + "create")) {

                        boolean commandRunning = true;
                        while(commandRunning) {

                            printStream.println("Type the name of the new channel, at least three characters long and maximum of" +
                                                " 20 characters");
                            String temp = scanner.nextLine();
                            String possibleChannelName = temp.trim();

                            if(possibleChannelName.length() >= 3 && possibleChannelName.length() <= 20 && !this.currentServer.channelAlreadyExists(possibleChannelName)) {

                                printStream.println("Create channel " + possibleChannelName + " ? Y/N");
                                String temp1 = scanner.nextLine();
                                String choice = temp1.trim();
                                if (choice.equals("Y") || choice.equals("y")) {

                                    this.currentServer.addChannel(possibleChannelName);
                                    printStream.println("Channel " + possibleChannelName + " created.");
                                    commandRunning = false;
                                }

                                if (choice.equals("N") || choice.equals("n")) {

                                    printStream.println("Exiting command interface");
                                    commandRunning = false;
                            }

                            } else {

                                printStream.println("Invalid channel name ! Try another one");
                            }
                        }
                    }

                    //## ADMIN ## remove cmd functionality. Allows users to remove channels. ONLY WITH ADMIN PRIVILEGES.
                    if(message.equals(COMMAND_SYMBOL + "remove")) {

                        if(this.user.isAdminAccess()) {

                            printStream.println("Which channel do you want to remove ?");

                            boolean commandRunning = true;
                            while(commandRunning) {
                                ArrayList<String> channels = this.currentServer.getChannels();

                                for (String channel : channels) {

                                    printStream.println(channel);
                                }

                                String temp = scanner.nextLine();
                                String channelToBeRemoved = temp.trim();

                                if (channels.contains(channelToBeRemoved)) {

                                    ArrayList<CommandInterpreter> users = new ArrayList<CommandInterpreter>
                                                                            (UsersList.getInstance().getChannelUsersInList(channelToBeRemoved));

                                    for(CommandInterpreter channelUser : users) {

                                        //Transfers all users in removed channel to the default channel
                                        channelUser.getUser().setCurrentChannel(this.currentServer.getChannels().get(0));

                                    }

                                    this.currentServer.removeChannel(channelToBeRemoved);
                                    ChatHistory.getInstance().broadcastServerMessage("Channel " + channelToBeRemoved + " removed. All users transferred to " +
                                                                                    this.currentServer.getChannels().get(0));
                                    printStream.println("Channel " + channelToBeRemoved + " removed !");
                                    commandRunning = false;

                                } else {

                                    printStream.println("Invalid choice");
                                }
                            }
                        } else {

                            printStream.println("Access denied !");
                        }
                    }

                    //## ADMIN ## kick cmd functionality. Allows admins to kick users from the server. ONLY WITH ADMIN PRIVILEGES
                    if(message.equals(COMMAND_SYMBOL + "kick")) {

                        if(this.user.isAdminAccess()) {

                            ArrayList<CommandInterpreter> usersList = UsersList.getInstance().getAllUsersInList();

                            boolean commandRunning = true;

                            while (commandRunning) {

                                printStream.println("Select user by index to be kicked or type: " + COMMAND_SYMBOL + " to cancel\r\n" +
                                        "---- USERS ----");

                                int index = 0;
                                for (CommandInterpreter userInList : usersList) {

                                    User user = userInList.getUser();
                                    if (!this.user.equals(user)) {
                                        printStream.println(" " + index + " : " + user.getUserName() + " @ "
                                                                                + user.getCurrentChannel());
                                    }
                                    index++;
                                }

                                int kickThisUser = -1;

                                //Convert input to index. If fails, cancels kicking
                                try {

                                    String temp = scanner.nextLine();
                                    kickThisUser = Integer.parseInt(temp);

                                } catch(Exception e) {
                                    printStream.println("Invalid input !");
                                }

                                boolean userKicked = false;

                                if(kickThisUser == -1) {
                                    printStream.println("Kicking canceled");
                                    break;

                                } else {

                                    System.out.println("#### USER ####" + usersList.get(kickThisUser).getUser().getUserName());
                                    userKicked = UsersList.getInstance().kickUser(usersList.get(kickThisUser));

                                }
                                if (userKicked) {
                                    printStream.println("User was kicked !");
                                    commandRunning = false;

                                } else {

                                    printStream.println("User not found");
                                }
                            }
                        } else {
                            printStream.println("Access denied");
                        }
                    }

                    //## ADMIN ## login cmd functionality. Allows user to login as admin if they are provided credentials for it
                    if(message.equals(COMMAND_SYMBOL + "login")) {

                        printStream.println("Username: ");
                        String userName = scanner.nextLine();

                        printStream.println("Password: ");
                        String passwd = scanner.nextLine();

                        if(this.currentServer.tryAdminLogin(userName, passwd)) {

                            printStream.println("Administrator access granted.");
                            this.user.setAdminAccess(true);

                        } else {

                            printStream.println("Invalid username and password !");
                        }
                    }

                    //Online cmd functionality. Shows all users online and their channels
                    if (message.equals(COMMAND_SYMBOL + "online")) {

                        printStream.println("---- Users online ----");

                        for (CommandInterpreter usr : UsersList.getInstance().getAllUsersInList()) {

                            printStream.println(usr.getUser().getUserName() + " @ " + usr.getUser().getCurrentChannel());
                        }
                    }

                    //Sends message to channel if user wasn't trying to input a command
                } else if (!user.getUserName().equals("")) {

                    if (message.contains("" + COMMAND_SYMBOL)) {

                        printStream.println("Were you trying to input a command ? Commands start with the " + COMMAND_SYMBOL + " character.");

                    } else {

                        //Send message
                        if (!message.equals("") && !this.user.getIsMuted()) {

                            Calendar calendar = Calendar.getInstance();
                            Date dateTime = calendar.getTime();
                            ChatMessage msg = new ChatMessage(message, this.user.getUserName(), dateTime, this.user.getCurrentChannel());
                            ChatHistory.getInstance().insertMessage(msg);

                        } else {

                            if(this.user.getIsMuted()) {
                                printStream.println("You are muted by the administrator");
                            } else {
                                printStream.println("Cannot send an empty message");
                            }
                        }
                    }

                    } else {

                        printStream.println("You don't have a username ! Please set one with the " + COMMAND_SYMBOL + "user command.");
                    }
                }

            } catch (Exception exception) {

            this.quit();
            System.out.println("User " + this.user.getUserName() + " disconnected");
            //this.user = null;
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
        commandSet.add(COMMAND_SYMBOL + "create");
        commandSet.add(COMMAND_SYMBOL + "remove");
        commandSet.add(COMMAND_SYMBOL + "kick");
        commandSet.add(COMMAND_SYMBOL + "login");
        commandSet.add(COMMAND_SYMBOL + "online");
        commandSet.add(COMMAND_SYMBOL + "quit");
        //commandSet.add(COMMAND_SYMBOL + "mute");

        return commandSet;
    }

    /**
     * Returns this CommandInterpreter's user
     * @return User object
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Updates the client's chat history
     * @param chatMessage a ChatMessage object. Contains sender, timestamp, channel and message.
     */
    @Override
    public void update(ChatMessage chatMessage) {

        if(chatMessage.getChannel().equals(user.getCurrentChannel()) || chatMessage.getChannel().equals("SERVER")){
            if(chatMessage.getSender().equals(this.user.getUserName())) {
                chatMessage.setSender("OWN_MESSAGE");
            }
        }
    }

    public void quit() {

        UsersList.getInstance().removeUser(this);
        ChatHistory.getInstance().removeObserver(this);
        this.running = false;
        try {
            this.inputStream.close();
        } catch(IOException io) {
            System.out.println("Closed input stream of " + this.getUser().getUserName());
        }
        this.printStream.close();

    }
}
