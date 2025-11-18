package boundary;

import control.AuthController;
import entity.User;
import util.ConsoleUtil;

public class LoginBoundary {

    private final AuthController authController;
    private final ConsoleUtil console;

    public LoginBoundary(AuthController authController) {
        this.authController = authController;
        this.console = new ConsoleUtil();
    }

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

    public void handleRegistration() {
        System.out.println("=== Company Rep Registration ===");
        String userId = console.readLine("Enter Company Rep ID (Email): ");
        String fullName = console.readLine("Enter Company Rep Full Name: ");
        String password = console.readLine("Enter Company Rep Password: ");
        String companyName = console.readLine("Enter Company Rep Company Name: ");
        String department = console.readLine("Enter Company Rep Department: ");
        String position = console.readLine("Enter Company Rep Position: ");

        boolean success = authController.registerCompanyRep(userId, password, fullName, companyName, department, position);

        System.out.println("Registration submitted, awaiting staff approval");
    }

    public void handlePasswordChange(User user) {
        if (user == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        System.out.println("=== Change Password ===");
        String newPassword = console.readLine("New Password: ");

        boolean changePasswordSuccess = authController.changePassword(newPassword);
        if (changePasswordSuccess) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Password change failed.");
        }

    }

}