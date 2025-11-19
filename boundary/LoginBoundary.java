package boundary;

import java.util.List;

import control.AuthController;
import entity.CareerCentreStaff;
import entity.CompanyRep;
import entity.User;
import util.ConsoleUtil;

/**
 * Boundary class responsible for handling all login- and registration-related
 * user interactions. This class displays prompts, gathers input from the console,
 * and delegates authentication logic to the AuthController.
 */
public class LoginBoundary {

    private final AuthController authController;
    private final ConsoleUtil console;

    /**
     * Creates the LoginBoundary with the required authentication controller.
     *
     * @param authController controller responsible for authentication and registration logic
     */
    public LoginBoundary(AuthController authController) {
        this.authController = authController;
        this.console = new ConsoleUtil();
    }

    /**
     * Handles the login flow for all user types.
     * Prompts the user for credentials, delegates validation to the AuthController, and prints the appropriate login feedback.
     *
     * @return the authenticated User object, or null if login failed
     */
    public User handleLogin() {

        System.out.println("=== Login ===");
        String userId = console.readLine("User ID: ");
        String password = console.readLine("Password: ");

        User user = authController.login(userId, password);

        if (user == null) {
            System.out.println("Invalid credentials or account not approved.");
        } else {
            System.out.println("Welcome, " + user.getFullName());
        }
        return user;

    }

    /**
     * Handles registration for a new Company Representative.
     * Collects required user information, constructs a CompanyRep entity, and delegates validation and persistence to the AuthController.
     *
     * <p>Note: Only Company Representatives can self-register; other user types are assumed to be created by the school.</p>
     */
    public void handleRegistration() {
        System.out.println("=== Company Rep Registration ===");
        String userId = console.readLine("Enter Company Rep ID: ");
        String fullName = console.readLine("Enter Company Rep Full Name: ");
        String password = console.readLine("Enter Company Rep Password: ");
        String companyName = console.readLine("Enter Company Rep Company Name: ");
        String department = console.readLine("Enter Company Rep Department: ");
        String position = console.readLine("Enter Company Rep Position: ");

        CompanyRep companyRep = new CompanyRep(userId, fullName, password, companyName, department, position);
        boolean success = authController.registerCompanyRep(companyRep);

        if (success) {
            System.out.println("Registration submitted, awaiting staff approval");
        } else {
            System.out.println("Company Rep already exists or registration has been submitted (pending approval).");
        }
    }

    /**
     * Allows a logged-in user to change their password.
     * Validates that a user is logged in before prompting for a new password,
     * then delegates update logic to the AuthController.
     *
     * @param user the currently logged-in user requesting a password change
     */
    public void handlePasswordChange(User user) {
        if (user == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        System.out.println("=== Change Password ===");
        String oldPassword = console.readLine("Current Password: ");
        String newPassword = console.readLine("New Password: ");

        boolean changePasswordSuccess = authController.changePassword(user, oldPassword, newPassword);

        if (changePasswordSuccess) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Password change failed.");
        }
    }
}