package control;

import entity.CompanyRep;
import entity.User;
import repository.UserRepository;

public class AuthController{
    private User user;
    private boolean loggedIn;
    private UserRepository userRepository;

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

    public boolean registerCompanyRep(String userId, String fullName, String password, String companyName, String department,String position){

        User existingUser = userRepository.findById(userId);
        if (existingUser != null) {
            return false;
        }

        CompanyRep rep = new CompanyRep(userId, fullName, password, companyName, department, position);
        rep.setApproved(false);

        userRepository.save(rep);

        return true;
    }
}