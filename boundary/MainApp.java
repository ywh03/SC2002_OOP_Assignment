package boundary;

import control.AuthController;
import control.CompanyRepController;
import control.InternshipApplicationController;
import control.InternshipController;
import entity.CareerCentreStaff;
import entity.CompanyRep;
import entity.Student;
import entity.User;
import repository.InternshipApplicationRepository;
import repository.InternshipRepository;
import repository.UserRepository;
import util.ConsoleUtil;

/**
 * Entry point and application coordinator for the Internship Placement Management System.
 *
 * <p>This class is responsible for system bootstrapping, wiring together repositories, controllers and boundary classes,
 * and routing authenticated users to the correct
 * interface based on their role (Student, CompanyRep, CareerCentreStaff).</p>
 */
public class MainApp {

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipApplicationRepository internshipApplicationRepository;
    private final ConsoleUtil console;

    private final AuthController authController;
    private final InternshipController internshipController;
    private final InternshipApplicationController internshipApplicationController;
    private final CompanyRepController companyRepController;

    private final LoginBoundary loginBoundary;
    private final StudentBoundary studentBoundary;
    private final CompanyRepBoundary companyRepBoundary;
    private final CareerCentreStaffBoundary careerCentreStaffBoundary;

    /**
     * Initializes the application by creating repositories, controllers, and
     * boundary classes, and wiring all required dependencies.
     *
     * <p>This constructor acts as a simple dependency injector, and assembles all major modules of the system before user interaction begins.</p>
     */
    public MainApp() {
        userRepository = new UserRepository();
        internshipRepository = new InternshipRepository();
        internshipApplicationRepository = new InternshipApplicationRepository();
        console = new ConsoleUtil();

        authController = new AuthController(userRepository);
        internshipController = new InternshipController(internshipRepository, userRepository);
        internshipApplicationController = new InternshipApplicationController(internshipApplicationRepository, internshipRepository, userRepository);
        companyRepController = new CompanyRepController(userRepository);

        loginBoundary = new LoginBoundary(authController);
        studentBoundary = new StudentBoundary(internshipController, internshipApplicationController, console, loginBoundary);
        companyRepBoundary = new CompanyRepBoundary(companyRepController, internshipController, internshipApplicationController, console, loginBoundary);
        careerCentreStaffBoundary = new CareerCentreStaffBoundary(internshipController, companyRepController, internshipApplicationController, loginBoundary);
    }

    /**
     * Starts the main application loop and displays the main menu.
     * Supports:
     * <ul>
     *     <li>User login</li>
     *     <li>CompanyRep registration</li>
     *     <li>Application exit</li>
     * </ul>
     *
     * <p>Delegates login handling to LoginBoundary and routes authenticated
     * users to their respective boundaries using routeUser().</p>
     */
    public void start() {
        System.out.println("Welcome to the Internship Placement Management System");

        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Login");
            System.out.println("2. Register as Company Rep");
            System.out.println("3. Exit");

            String choice = console.readLine("Enter choice: ");

            switch (choice) {
                case "1" -> {
                    User user = loginBoundary.handleLogin();
                    if (user != null) {
                        routeUser(user);
                    }
                }
                case "2" -> loginBoundary.handleRegistration();
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Routes an authenticated user to the appropriate boundary menu based on their role.
     *
     * <p>The method uses runtime polymorphism to determine whether the user is a
     * Student, CompanyRep, or CareerCentreStaff. Company Representatives must also be
     * approved before they are permitted to access system functions.</p>
     *
     * @param user the authenticated user to route
     */
    private void routeUser(User user) {

        if (user instanceof Student) {
            studentBoundary.displayMenu((Student) user);
        } else if (user instanceof CompanyRep) {
            if (!((CompanyRep) user).getApproved()) {
                System.out.println("Your account has not been approved yet.");
                return;
            }
            companyRepBoundary.displayMenu((CompanyRep) user);
        } else if (user instanceof CareerCentreStaff) {
            careerCentreStaffBoundary.displayMenu((CareerCentreStaff) user);
        }

    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new MainApp().start();
    }

}
