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
import entity.CareerCentreStaff;
import entity.CompanyRep;
import entity.Student;
import entity.enums.Major;
import entity.enums.RegistrationStatus;

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
        initializeTestData(); 
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
            companyRepBoundary.displayMenu((CompanyRep) user);
        } else if (user instanceof CareerCentreStaff) {
            careerCentreStaffBoundary.displayMenu((CareerCentreStaff) user);
        }

    }


    private void initializeTestData() {
        // Only add data if the user list is empty (fresh start)
        if (userRepository.findAll().isEmpty()) {
            System.out.println("--- Initializing Rich Test Data ---");

            CareerCentreStaff s1 = new CareerCentreStaff("STAFF0001", "Alice Tan", "password1", "CCDS");
            CareerCentreStaff s2 = new CareerCentreStaff("STAFF0002", "Benjamin Lee", "password2", "COE");
            CareerCentreStaff s3 = new CareerCentreStaff("STAFF0003", "Cheryl Ng", "password3", "SOH");
            
            userRepository.save(s1);
            userRepository.save(s2);
            userRepository.save(s3);

            // 1. Approved Rep (Google)
            CompanyRep rep1 = new CompanyRep("REP001", "John Lim", "rep123", "Google", "Engineering", "Recruiter");
            rep1.setRegistrationStatus(RegistrationStatus.APPROVED);
            userRepository.save(rep1);

            // 2. Approved Rep (Shopee)
            CompanyRep rep2 = new CompanyRep("REP002", "Sarah Tan", "rep234", "Shopee", "Talent Acquisition", "Hiring Manager");
            rep2.setRegistrationStatus(RegistrationStatus.APPROVED);
            userRepository.save(rep2);

            // 3. PENDING Rep (ByteDance)
            CompanyRep rep3 = new CompanyRep("REP003", "Marcus Lee", "rep345", "ByteDance", "HR", "Campus Recruiter");
            rep3.setRegistrationStatus(RegistrationStatus.PENDING); 
            userRepository.save(rep3);

            // 4. Approved Rep (NCS)
            CompanyRep rep4 = new CompanyRep("REP004", "Daphne Koh", "rep456", "NCS", "Tech Hiring", "Recruiter");
            rep4.setRegistrationStatus(RegistrationStatus.APPROVED);
            userRepository.save(rep4);

            CompanyRep rep5 = new CompanyRep("REP005", "Keith Wong", "rep567", "GovTech", "Internship Office", "Coordinator");
            rep5.setRegistrationStatus(RegistrationStatus.PENDING);
            userRepository.save(rep5);

            Student st1 = new Student("U2410001A", "Tan Wei Ling", "pass001", 2, Major.COMPUTER_SCIENCE);
            Student st2 = new Student("U2310002B", "Ng Jia Hao", "pass002", 3, Major.DATA_SCIENCE);
            Student st3 = new Student("U2210003C", "Lim Yi Xuan", "pass003", 4, Major.COMPUTER_ENGINEERING);
            Student st4 = new Student("U2510004D", "Chong Zhi Hao", "pass004", 1, Major.MECHANICAL_ENGINEERING);
            Student st5 = new Student("U2310005E", "Wong Shu Hui", "pass005", 3, Major.COMPUTER_SCIENCE);

            userRepository.save(st1);
            userRepository.save(st2);
            userRepository.save(st3);
            userRepository.save(st4);
            userRepository.save(st5);
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
