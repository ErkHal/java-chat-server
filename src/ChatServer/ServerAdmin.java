package ChatServer;

public class ServerAdmin {

    private String userName;
    private String passWord;

    public ServerAdmin(String usrName, String passwd) {

        this.userName = usrName;
        this.passWord = passwd;
    }

    /**
     * Checks users credentials to login as admin
     * @param usrName
     * @param passWrd
     * @return True if username and password match, otherwise false.
     */
    public boolean checkCredentials(String usrName, String passWrd) {

        return this.userName.equals(usrName) && this.passWord.equals(passWrd);
    }
}
