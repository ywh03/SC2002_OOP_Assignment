package control;

import entity.CompanyRep;
import entity.User;
import repository.UserRepository;

/**
 * Controller responsible for handling authentication-related application logic.
 *
 * <p>This includes:</p>
 * <ul>
 *     <li>User login and logout</li>
 *     <li>Password changes</li>
 *     <li>Registration workflow for Company Representatives</li>
 * </ul>
 */
public class AuthController{
    private User currentUser;
    private boolean loggedIn;
    private UserRepository userRepository;

    /**
     * Creates an AuthController with access to the user repository.
     *
     * @param userRepository the repository used to retrieve and store user accounts
     */
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentUser = null;
        this.loggedIn = false; // dafault
    }

    /**
     * Attempts to authenticate a user with the given credentials.
     *
     * <p>If authentication succeeds, the user is marked as logged in and the
     * authenticated User object is returned.</p>
     *
     * @param userId   the user ID entered
     * @param password the password entered
     * @return the authenticated User, or null if credentials are invalid
     */
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

    /**
     * Logs out the currently authenticated user.
     *
     * @return true if logout succeeded, false if no user was logged in
     */
    public boolean logout() {
        if (loggedIn) {
            loggedIn = false;
            currentUser = null;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Changes the password of the currently logged-in user.
     * The updated password is saved through the UserRepository.
     *
     * @param newPassword the new password to set
     * @return true if the password was successfully changed, false otherwise
     */
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!user.getPassword().equals(oldPassword)) {
            return false;  // incorrect current password
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }


    /**
     * Registers a new Company Representative.
     *
     * <p>A CompanyRep may self-register, but is initially unapproved.
     * Approval must later be granted by a Career Centre Staff member.</p>
     *
     * <p>Registration fails if a user with the same ID already exists.</p>
     *
     * @param companyRep the CompanyRep entity containing registration information
     * @return true if registration succeeds, false if the ID already exists
     */
    public boolean registerCompanyRep(CompanyRep companyRep){
        if (userRepository.findById(companyRep.getId()) != null) {
            return false; // companyRep already exists, dont need to reregister
        }

        companyRep.setApproved(false);

        userRepository.save(companyRep);

        return true;
    }
}