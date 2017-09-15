package UsersList;

import ChatHistory.ChatHistory;
import User.User;

import java.util.ArrayList;
import java.util.HashSet;

public class UsersList {

    private HashSet<User> usersList;

    private static UsersList ourInstance = new UsersList();

    public static UsersList getInstance() {
        return ourInstance;
    }

    private UsersList() {

        this.usersList = new HashSet<User>();
    }

    public static void addUser(User usr) {

        UsersList.getInstance().usersList.add(usr);
        ChatHistory.getInstance().broadcastServerMessage("User " + usr.getUserName() + " connected");
    }

    public static void removeUser(User usr) {

        UsersList.getInstance().usersList.remove(usr);
        ChatHistory.getInstance().broadcastServerMessage("User " + usr.getUserName() + " disconnected");
    }


    /**
     * Checks the existence of a user in the set.
     * @param user
     * @return

    public static boolean checkIfUserExists(User user) {

        if(UsersList.getInstance().usersList.contains(user)) {

            return true;

        } else {

            return false;
        }
    } */

    /**
     * Checks if userNameExists already. Prevents duplicate names.
     * @param userName
     * @return
     */
    public static boolean checkIfUserNameExists(String userName) {

        boolean exists = false;

        for(User user : UsersList.getAllUsersInList()) {

            if(user.getUserName().equals(userName)) {
                exists = true;
            }
        }

        return exists;
    }

    /**
     * Returns users in an arraylist for easier handling
     * @return All connected users
     */
    public static ArrayList<User> getAllUsersInList() {

        return new ArrayList<User>(UsersList.getInstance().usersList);
    }

    /**
     * Returns users in a particular channel
     * @param channel Channel to be searched with
     * @return ArrayList of users in this channel
     */
    public static ArrayList<User> getChannelUsersInList(String channel) {

        ArrayList<User> channelUsers = new ArrayList<User>();

        for(User user : getAllUsersInList()) {

            if(user.getCurrentChannel().equals(channel)) {
                channelUsers.add(user);
            }
        }
        return channelUsers;
    }
}
