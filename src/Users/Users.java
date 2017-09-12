package Users;

import java.util.HashSet;

/**
 * @author ErkHal
 * Contains a set for storing usernames
 */
public class Users {

    private static Users ourInstance = new Users();

    HashSet<String> userList;

    private Users() {
        this.userList = new HashSet<String>();
    }

    public HashSet<String> getList() {
        return this.userList;
    }

    public void setNewUser(String newUser) {
        this.userList.add(newUser);
    }

    public static Users getInstance() {
        return ourInstance;
    }
}
