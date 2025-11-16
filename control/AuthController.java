package control;

import entity.User;

public class AuthController{
    private User user;
    private boolean loggedIn;

    public AuthController(User user) {
        this.user = user;
        this.loggedIn = false; // dafault
    }
    
    public boolean login(String userID, String password) {
        if (user.getUserID().equals(userID) && user.getPassword().equals(password)) {
            loggedIn = true;
            System.out.println(user.getFullName() + " has logged in.");
            return true;
        } else {
            System.out.println("Wrong password.");
            return false;
        }
    }

    public void logout() {
        if (loggedIn) {
            loggedIn = false;
            System.out.println(user.getFullName() + " has logged out.");
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    public void changePassword(String newPassword) {
        if (loggedIn) {
            user.setPassword(newPassword);
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("No used is logged in.");
        }
    }
}