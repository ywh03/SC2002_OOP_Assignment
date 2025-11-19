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

    public MainApp() {
        userRepository = new UserRepository();
        internshipRepository = new InternshipRepository();
        internshipApplicationRepository = new InternshipApplicationRepository();
        console = new ConsoleUtil();

        authController = new AuthController(userRepository);
        internshipController = new InternshipController(internshipRepository, userRepository);
        internshipApplicationController = new InternshipApplicationController();
        companyRepController = new CompanyRepController(userRepository);

        loginBoundary = new LoginBoundary(authController);
        studentBoundary = new StudentBoundary(internshipController, internshipApplicationController, console);
        companyRepBoundary = new CompanyRepBoundary(companyRepController, internshipController, internshipApplicationController, console);
        careerCentreStaffBoundary = new CareerCentreStaffBoundary(internshipController, companyRepController, internshipApplicationController);
    }

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

    public static void main(String[] args) {
        new MainApp().start();
    }

}
