package api.event.random;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Account {


    private String username;
    private String password;
    private int pin;

    public Account(String username, String password) {
        this(username, password, 0000);
    }

    public Account(String username, String password, int pin) {
        this.username = username;
        this.password = password;
        this.pin = pin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
