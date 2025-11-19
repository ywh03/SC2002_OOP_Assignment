package boundary;

import control.InternshipApplicationController;
import control.InternshipController;
import entity.Internship;
import entity.Notification;
import entity.InternshipApplication;
import entity.Student;
import util.ConsoleUtil;
import entity.enums.ApplicationStatus;

import java.util.List;

import manager.NotificationManager;

/**
 * Boundary class responsible for all interactions with a Student user.
 *
 * <p>This class handles user interface concerns such as:</p>
 * <ul>
 *     <li>Viewing available internships</li>
 *     <li>Submitting internship applications</li>
 *     <li>Viewing and withdrawing applications</li>
 *     <li>Responding to internship offers</li>
 *     <li>Displaying student notifications</li>
 * </ul>
 */
public class StudentBoundary {

    private final InternshipController internshipController;
    private final InternshipApplicationController internshipApplicationController;
    private final LoginBoundary loginBoundary;
    private final ConsoleUtil console;

    /**
     * Constructs the StudentBoundary with the required controllers.
     *
     * @param internshipController controller responsible for retrieving internship listings
     * @param internshipApplicationController controller responsible for managing applications
     * @param console helper class for safe console input
     */
    public StudentBoundary(InternshipController internshipController, InternshipApplicationController internshipApplicationController, ConsoleUtil console, LoginBoundary loginBoundary) {
        this.internshipController = internshipController;
        this.internshipApplicationController = internshipApplicationController;
        this.loginBoundary = loginBoundary;
        this.console = console;
    }

    /**
     * Displays the main Student menu and routes the student's selected actions.
     *
     * @param student the logged-in student whose actions are being handled
     */
    public void displayMenu(Student student) {
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Available Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Withdraw Application");
            System.out.println("5. View Notifications");
            System.out.println("6. Accept / Reject Internship Offers");
            System.out.println("7. Change Password");
            System.out.println("8. Logout");

            String choice = console.readLine("Enter choice: ");

            switch (choice) {
                case "1" -> displayInternships(student);
                case "2" -> applyInternship(student);
                case "3" -> displayAllApplications(student);
                case "4" -> withdrawRequest(student);
                case "5" -> displayNotifications(student);
                case "6" -> handleInternshipOffers(student);
                case "7" -> loginBoundary.handlePasswordChange(student);
                case "8" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Displays all internships available to the logged-in student.
     * The availability logic (e.g., date checks, major matching, level restrictions) is delegated to the InternshipController.
     *
     * @param student the student requesting to view available internships
     * @return true if internships were displayed, false if none available
     */
    private boolean displayInternships(Student student) {
        // Need to retroactively apply filters
        List<Internship> internships = internshipController.getAvailableInternships(student);

        if (internships.isEmpty()) {
            System.out.println("There are no available internships.");
            return false;
        }

        System.out.println("\n=== Available Internships ===");
        for (Internship internship : internships) {
            System.out.println(internship.getId() + " | " + internship.getInternshipTitle() + " | "
                    + internship.getDescription() + " | " + internship.getCompanyName());
        }
        return true;
    }

    /**
     * Allows the student to apply for an internship after displaying available internships. Delegates application validation and creation
     * to the InternshipApplicationController.
     *
     * @param student the student submitting the application
     */
    private void applyInternship(Student student) {
        if (!displayInternships(student)) return;

        System.out.println();
        String internshipId = console.readLine("Enter internship ID to apply for: ");

        boolean success = internshipApplicationController.apply(internshipId, student.getId());

        if (success) {
            System.out.println("Application submitted");
        } else {
            System.out.println("Could not apply. Check eligibility or ID");
        }
    }

    /**
     * Displays all internship applications submitted by the student.
     *
     * @param student the student whose applications are viewed
     * @return true if applications exist, false otherwise
     */
    private boolean displayAllApplications(Student student) {
        List<InternshipApplication> myInternshipApplications = internshipApplicationController.studentGetInternshipApplications(student.getId());
        if (myInternshipApplications.isEmpty()) {
            System.out.println("You have not applied for any internships.");
            return false;
        }
        System.out.println("\n=== My Applications ===");
        for (InternshipApplication internshipApplication : myInternshipApplications) {
            System.out.println(internshipApplication.getId() + " | Internship: " + internshipApplication.getInternship().getInternshipTitle()
                    + " | Company: " + internshipApplication.getInternship().getCompanyName() + " | Status: " + internshipApplication.getApplicationStatus());
        }
        return true;
    }

    /**
     * Initiates a withdrawal request for one of the student's applications.
     * Only applications eligible for withdrawal may be submitted.
     *
     * @param student the student requesting a withdrawal
     */
    private void withdrawRequest(Student student) {
        if (!displayAllApplications(student)) return;
        
        String applicationId = console.readLine("Enter application ID to withdraw: ");

        boolean success = internshipApplicationController.requestWithdrawal(applicationId, student);

        if (success) {
            System.out.println("Withdrawal request submitted");
        } else {
            System.out.println("Could not withdraw. Check eligibility or ID");
        }
    }

    /**
     * Displays all system notifications addressed to the student.
     * Notifications are retrieved from the NotificationManager and marked as read after display.
     *
     * @param student the student viewing notifications
     */
    private void displayNotifications(Student student) {
        List<Notification> notifs = NotificationManager.getInstance().getNotifications(student.getId());

        if (notifs.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }

        System.out.println("=== Notifications ===");
        for (Notification n : notifs) {
            // example: mark read/unread and message
            System.out.println((n.isRead() ? "[READ] " : "[UNREAD] ") + n.getMessage()
                    + " | " + n.getTimestamp());
        }

        // Optional: mark all as read once displayed
        NotificationManager.getInstance().markAllAsRead(student.getId());
    }

    /**
     * Allows the student to respond to internship offers.
     * Only applications with status SUCCESSFUL and that have not yet been accepted or rejected are shown.
     *
     * <p>The student may choose to accept or reject an offer, and the
     * application state is updated accordingly.</p>
     *
     * @param student the student responding to internship offers
     */
    private void handleInternshipOffers(Student student){
        // Get all internship applications with status SUCCESSFUL (offer made)
        List<InternshipApplication> offers = internshipApplicationController.studentGetInternshipApplications(student.getId()).stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.SUCCESSFUL
                            && !app.getOfferAccepted()) // only unaccepted offers
                .toList();
    
        if (offers.isEmpty()) {
            System.out.println("No internship offers to respond to.");
            return;
        }
    
        System.out.println("=== Internship Offers ===");
        for (int i = 0; i < offers.size(); i++) {
            InternshipApplication app = offers.get(i);
            Internship internship = app.getInternship();
            System.out.println((i + 1) + ". " + internship.getInternshipTitle() +
                    " at " + internship.getCompanyName() +
                    " | Status: " + app.getApplicationStatus());
        }
    
        int choice = console.readInt("Enter the number of the internship you want to respond to (or 0 to go back): ");
        if (choice == 0) return;
    
        if (choice < 1 || choice > offers.size()) {
            System.out.println("Invalid choice.");
            return;
        }
    
        InternshipApplication selectedApp = offers.get(choice - 1);
        String decision = console.readLine("Accept or Reject this offer? (A/R): ").toUpperCase();
    
        switch (decision) {
            case "A" -> {
                boolean success = internshipApplicationController.acceptOffer(selectedApp);
                if (success) {
                    System.out.println("You have accepted the offer for " + selectedApp.getInternship().getInternshipTitle());
                } else {
                    System.out.println("Cannot accept this offer.");
                }
            }
            case "R" -> {
                internshipApplicationController.rejectOffer(selectedApp.getId());
                System.out.println("You have rejected the offer for " + selectedApp.getInternship().getInternshipTitle());
            }
            default -> System.out.println("Invalid input. Offer not changed.");

        }
        
    }

}
