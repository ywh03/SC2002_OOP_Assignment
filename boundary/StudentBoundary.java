package boundary;

import control.InternshipApplicationController;
import control.InternshipController;
import entity.Internship;
import entity.InternshipApplication;
import entity.Student;
import util.ConsoleUtil;

import java.util.List;

import Manager.NotificationManager;

public class StudentBoundary {

    private final InternshipController internshipController;
    private final InternshipApplicationController internshipApplicationController;
    private final ConsoleUtil console;

    public StudentBoundary(InternshipController internshipController, InternshipApplicationController internshipApplicationController, ConsoleUtil console) {
        this.internshipController = internshipController;
        this.internshipApplicationController = internshipApplicationController;
        this.console = console;
    }

    public void displayMenu(Student student) {
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Available Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Withdraw Application");
            System.out.println("5. View Notifications");
            System.out.println("6. Logout");

            String choice = console.readLine("Enter choice: ");

            switch (choice) {
                case "1" -> displayInternships(student);
                case "2" -> applyInternship(student);
                case "3" -> displayAllApplications(student);
                case "4" -> withdrawRequest(student);
                case "5" -> displayNotifications(student);
                case "6" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void displayInternships(Student student) {
        // Need to retroactively apply filters
        List<Internship> internships = internshipController.getAvailableInternships(student);

        if (internships.isEmpty()) {
            System.out.println("There are not available internships.");
            return;
        }

        System.out.println("\n=== Available Internships ===");
        for (Internship internship : internships) {
            System.out.println(internship.getId() + " | " + internship.getInternshipTitle() + " | "
                    + internship.getDescription() + " | " + internship.getCompanyName());
        }
    }

    private void applyInternship(Student student) {
        displayInternships(student);
        System.out.println();
        String internshipId = console.readLine("Enter internship ID to apply for: ");

        boolean success = internshipApplicationController.apply(internshipId, student);

        if (success) {
            System.out.println("Application submitted");
        } else {
            System.out.println("Could not apply. Check eligibility or ID");
        }
    }

    private void displayAllApplications(Student student) {
        List<InternshipApplication> myInternshipApplications = internshipApplicationController.studentGetInternshipApplications(student.getId());
        if (myInternshipApplications.isEmpty()) {
            System.out.println("You have not applied for any internships.");
            return;
        }
        System.out.println("\n=== My Applications ===");
        for (InternshipApplication internshipApplication : myInternshipApplications) {
            System.out.println(internshipApplication.getId() + " | Internship: " + internshipApplication.getInternship().getInternshipTitle()
                    + " | Company: " + internshipApplication.getInternship().getCompanyName() + " | Status: " + internshipApplication.getApplicationStatus());
        }
    }

    private void withdrawRequest(Student student) {
        displayAllApplications(student);
        
        String applicationId = console.readLine("Enter application ID to withdraw: ");

        boolean success = internshipApplicationController.requestWithdrawal(applicationId, student);

        if (success) {
            System.out.println("Withdrawal request submitted");
        } else {
            System.out.println("Could not withdraw. Check eligibility or ID");
        }
    }

    private void displayNotifications(Student student) {
        List<Notification> notifs = NotificationManager.getInstance().getNotifications(student.getUserId());

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
        NotificationManager.getInstance().markAllAsRead(student.getUserId());
    }


}
