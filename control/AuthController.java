package control;

import entity.CompanyRep;
import entity.User;
import repository.UserRepository;

public class AuthController{
    private User currentUser;
    private boolean loggedIn;
    private UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentUser = null;
        this.loggedIn = false; // dafault
    }
    
    public User login(String userId, String password) {
        User user = userRepository.findById(userId);
        if (user != null && user.getPassword().equals(password)) {
            loggedIn = true;
            currentUser = user;
            return currentUser;
        } else {
            return null;
        }
    }

    public boolean logout() {
        if (loggedIn) {
            loggedIn = false;
            currentUser = null;
            return true;
        } else {
            return false;
        }
    }

    public boolean changePassword(String newPassword) {
        if (loggedIn && currentUser != null) {
            currentUser.setPassword(newPassword);
            userRepository.save(currentUser); // update repo to save this new pw
            return true;
        } else {
            return false;
        }
    }

    public boolean registerCompanyRep(CompanyRep companyRep){
        if (userRepository.findById(companyRep.getId()) != null) {
            return false; // companyRep already exists, dont need to reregister
        }

        companyRep.setApproved(false);

        userRepository.save(companyRep);

        return true;
    }
}