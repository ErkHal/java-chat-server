package User;

/**
 * @author ErkHal
 *
 * An User class to create User objects. User know their current channel.
 */
public class User {

    private String userName;
    private String currentChannel;
    private boolean adminAccess;

    public User (String userId, String channel){

        this.userName = userId;
        this.currentChannel = channel;
        this.adminAccess = false;
    }

    /**
     * Checks if user has logon as adminAccess
     * @return True if has adminAccess priviledges, otherwise false
     */
    public boolean isAdminAccess() {
        return this.adminAccess;
    }

    public void setAdminAccess(boolean adminAccess) {
        this.adminAccess = adminAccess;
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    /**
     * Return current channel
     * @return Current channel the user is on
     */
    public String getCurrentChannel() {

        return this.currentChannel;
    }

    /**
     * Sets user's channel
     * @param channel sets the channel user is on
     */
    public void setCurrentChannel(String channel) {

        this.currentChannel = channel;
    }

    /**
     * Returns username of the User
     * @return
     */
    public String getUserName() {

        return this.userName;
    }

    /**
     * Sets the user's username.
     * @param userName new username
     */
    public void setUserName(String userName) {

        this.userName = userName;
    }
}
