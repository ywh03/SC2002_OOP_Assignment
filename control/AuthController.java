package control;

import entity.User;

public class AuthController{
    private User user;
    private boolean loggedIn;

    public AuthController(User user) {
        this.user = user;
        this.loggedIn = false; // dafault
    }
    
    public User login(String userID, String password) {
        if (user.getUserID().equals(userID) && user.getPassword().equals(password)) {
            loggedIn = true;
            return user;
        } else {
            return null;
        }
    }

    public boolean logout() {
        if (loggedIn) {
            loggedIn = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean changePassword(String newPassword) {
        if (loggedIn) {
            user.setPassword(newPassword);
            return true;
        } else {
            return false;
        }
    }
}