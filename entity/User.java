package entity;

public abstract class User {
    private String userID;
    private String name;
    private String password;

    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
        this.password = "default";
    }

    public boolean login(String userID, String password) {
        return this.userID.equals(userID) && this.password.equals(password);
    }

    public void logout() {
        // return to main page if we have one ?????????
        System.out.println(this.name + " has logged out.");
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        System.out.println("Password changed successfully.");
    }

    public String getUserID() {return userID;}
    public void setUserID(String userID) {this.userID = userID;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}


}