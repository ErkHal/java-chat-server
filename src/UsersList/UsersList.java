package UsersList;

import ChatHistory.ChatHistory;
import CommandInterpreter.CommandInterpreter;

import java.util.ArrayList;
import java.util.HashSet;

public class UsersList {

    private HashSet<CommandInterpreter> usersList;

    private static UsersList ourInstance = new UsersList();

    public static UsersList getInstance() {
        return ourInstance;
    }

    private UsersList() {

        this.usersList = new HashSet<CommandInterpreter>();
    }

    public void addUser(CommandInterpreter usr) {

        UsersList.getInstance().usersList.add(usr);
        ChatHistory.getInstance().broadcastServerMessage("User " + usr.getUser().getUserName() + " connected");
    }

    public void removeUser(CommandInterpreter usr) {

        UsersList.getInstance().usersList.remove(usr);
        ChatHistory.getInstance().broadcastServerMessage("User " + usr.getUser().getUserName() + " disconnected");
    }

    /**
     * Checks if userNameExists already. Prevents duplicate names.
     *
     * @param userName
     * @return
     */
    public boolean checkIfUserNameExists(String userName) {

        boolean exists = false;

        for (CommandInterpreter user : getAllUsersInList()) {

            if (user.getUser().getUserName().equals(userName)) {
                exists = true;
            }
        }

        return exists;
    }

    /**
     * Returns users in an arraylist for easier handling
     *
     * @return All connected users
     */
    public ArrayList<CommandInterpreter> getAllUsersInList() {

        return new ArrayList<CommandInterpreter>(UsersList.getInstance().usersList);
    }

    /**
     * Returns users in a particular channel
     *
     * @param channel Channel to be searched with
     * @return ArrayList of users in this channel
     */
    public ArrayList<CommandInterpreter> getChannelUsersInList(String channel) {

        ArrayList<CommandInterpreter> channelUsers = new ArrayList<>();

        for (CommandInterpreter user : getAllUsersInList()) {

            if (user.getUser().getCurrentChannel().equals(channel)) {
                channelUsers.add(user);
            }
        }
        return channelUsers;
    }

    /**
     * Kicks the given user from the list
     * @param userToBeKicked
     * @return True if kicking
     */
    public boolean kickUser(CommandInterpreter userToBeKicked) {

        boolean kicked;

        ChatHistory.getInstance().broadcastServerMessage("User " + userToBeKicked.getUser().getUserName() + " was kicked from the server");

        if (this.usersList.contains(userToBeKicked)) {

            this.usersList.remove(userToBeKicked);
            ChatHistory.getInstance().removeObserver(userToBeKicked);
            kicked = true;
            userToBeKicked.quit();

        } else {
            kicked = false;
        }

        return kicked;
    }
}
