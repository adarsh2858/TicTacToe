package adarsh.helloworld.tictactoe;

public class User {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private static User singleInstance = null;

    public static User getInstance() {
        return singleInstance;
    }

    public static void setSingleInstance(String email, String fullName, String phone) {
        if (singleInstance == null)
            singleInstance = new User(email, fullName, phone);
    }

    public static void setSingleInstance(User nullValue) {
        singleInstance = nullValue;
    }

    public User(String email) {
        this.email = email;
    }

    public User(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    public User(String email, String fullName, String phone) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
    }

    public User(String email, String fullName, String phone, String password) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
