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
    private boolean muted;

    public User (String userId, String channel){

        this.userName = userId;
        this.currentChannel = channel;
        this.adminAccess = false;
        this.muted = false;
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

    public void toggleMute() {

        this.muted = !muted;
    }

    public boolean getIsMuted() {

        return this.muted;
    }

    @Override
    public boolean equals(Object object) {

        User thatUser = (User) object;
        return this.userName.equals(thatUser.userName);
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
     * @return the username of the user
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
