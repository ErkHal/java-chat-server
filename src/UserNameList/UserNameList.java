package UserNameList;

import ChatHistory.ChatHistory;
import Users.Users;

import java.util.HashSet;

/**
 * @author ErkHal
 * Contains methods for checking usernames and adding them into the users set
 */
public class UserNameList {

    /**
     * Checks if given username already exists
     * @param userName
     * @return
     */
    public static boolean checkIfUserExists(String userName) {

        Users users = Users.getInstance();
        if(users.getList().contains(userName)) {

            return true;

        } else {

            return false;
        }
    }

    /**
     * Adds new user to the list of users
     * @param userName
     */
    public static void addUser(String userName) {

        Users users = Users.getInstance();
        users.getList().add(userName);
        ChatHistory.getInstance().broadcastServerMessage("User " + userName + " connected !");
    }

    /**
     * Removes user from the list. Executed when user disconnects.
     * @param userName
     */
    public static void removeUser(String userName) {

        Users users = Users.getInstance();
        users.getList().remove(userName);
        ChatHistory.getInstance().broadcastServerMessage("User " + userName + " disconnected");
    }
}
