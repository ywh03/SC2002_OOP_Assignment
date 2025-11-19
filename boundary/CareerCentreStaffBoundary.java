package boundary;

import control.CompanyRepController;
import control.InternshipApplicationController;
import control.InternshipController;
import entity.CareerCentreStaff;
import entity.CompanyRep;
import entity.Internship;
import entity.InternshipApplication;
import entity.Student;
import entity.enums.ApplicationStatus;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;
import entity.enums.Major;
import entity.Notification;
import util.ConsoleUtil;
import util.filter.Filter;
import util.filter.LevelFilter;
import util.filter.MajorFilter;
import util.filter.StatusFilter;

import java.util.ArrayList;
import java.util.List;

import manager.NotificationManager;

/**
 * Boundary class responsible for all Career Centre Staff interactions.
 * <p>
 * This class handles:
 * <ul>
 *     <li>Viewing, approving and rejecting pending CompanyRep registrations</li>
 *     <li>Viewing, approving and rejecting internship postings</li>
 *     <li>Processing student withdrawal requests</li>
 *     <li>Generating internship reports using filters</li>
 *     <li>Displaying notifications sent to Career Centre Staff</li>
 * </ul>
 */
public class CareerCentreStaffBoundary {

    private final InternshipController internshipController;
    private final CompanyRepController companyRepController;
    private final InternshipApplicationController internshipApplicationController;
    private final ConsoleUtil console;

    /**
     * Creates a CareerCentreStaffBoundary with the required controllers.
     *
     * @param internshipController controller handling internship posting logic
     * @param companyRepController controller handling CompanyRep registration logic
     * @param internshipApplicationController controller handling internship applications & withdrawals
     */
    public CareerCentreStaffBoundary(InternshipController internshipController,  CompanyRepController companyRepController, InternshipApplicationController internshipApplicationController) {
        this.internshipController = internshipController;
        this.companyRepController = companyRepController;
        this.internshipApplicationController = internshipApplicationController;
        this.console = new ConsoleUtil();
    }

    /**
     * Displays the main menu for Career Centre Staff and routes user actions.
     *
     * @param careerCentreStaff the logged-in staff member
     */
    public void displayMenu(CareerCentreStaff careerCentreStaff) {
        while (true) {
            System.out.println("\n=== Career Centre Staff Menu ===");
            System.out.println("1. View / Approve / Reject Company Rep Registrations");
            System.out.println("2. View / Approve / Reject Internship Postings");
            System.out.println("3. Handle Withdrawal Requests");
            System.out.println("4. Generate Internship Report");
            System.out.println("5. View Notifications");
            System.out.println("6. Logout");

            String choice = console.readLine("Enter your choice: ");

            switch (choice) {
                case "1" -> displayPendingRegistrations(careerCentreStaff);
                case "2" -> displayPendingInternships(careerCentreStaff);
                case "3" -> displayPendingWithdrawals(careerCentreStaff);
                case "4" -> displayReport();
                case "5" -> displayNotifications(careerCentreStaff);
                case "6" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Displays all pending CompanyRep registrations and allows staff to approve or reject selected representatives.
     *
     * @param careerCentreStaff the staff performing the action
     */
    private void displayPendingRegistrations(CareerCentreStaff careerCentreStaff) {
        List<CompanyRep> pendingCompanyReps = companyRepController.getPendingRegistrations();

        if (pendingCompanyReps.isEmpty()) {
            System.out.println("No pending company representative registrations.");
            return;
        }

        System.out.println("\n=== Pending CompanyRep Registrations ===");
        for (CompanyRep rep : pendingCompanyReps) {
            System.out.println(rep.getId() + " | " + rep.getFullName()
                    + " | Company: " + rep.getCompanyName());
        }

        String companyRepId = console.readLine("Enter Rep ID to approve/reject (or ENTER to go back): ");
        if (companyRepId.isEmpty()) return;

        String choice = console.readLine("Approve or Reject? (A/R): ");

        boolean success;

        if (choice.equals("A")) {
            success = companyRepController.authoriseCompanyRep(companyRepId);
        } else if (choice.equals("R")) {
            success = companyRepController.rejectCompanyRep(companyRepId);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (success) {
            System.out.println("Registration updated.");
        } else {
            System.out.println("Failed to update Registration.");
        }
    }

    /**
     * Displays internship postings that are still pending approval, and allows staff to approve or reject them.
     *
     * @param careerCentreStaff the staff performing the action
     */
    private void displayPendingInternships(CareerCentreStaff careerCentreStaff) {
        List<Internship> pendingInternships = internshipController.getPendingInternships();

        if (pendingInternships.isEmpty()) {
            System.out.println("No pending internships.");
        }

        System.out.println("\n=== Pending internships ===");
        for (Internship internship : pendingInternships) {
            System.out.println(internship.getId() + " | " + internship.getInternshipTitle()
                    + " | CompanyName: " + internship.getCompanyName());
        }

        String internshipId = console.readLine("Enter Internship ID to approve / reject (or ENTER to go back): ");
        if (internshipId.isEmpty()) return;

        String choice =  console.readLine("Approve or Reject? (A/R): ");

        boolean success;

        if (choice.equals("A")) {
            success = internshipController.approveInternship(internshipId);
        } else if (choice.equals("R")) {
            success = internshipController.rejectInternship(internshipId);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (success) {
            System.out.println("Internship updated.");
        } else {
            System.out.println("Failed to update Internship.");
        }

    }

    /**
     * Displays all internship applications currently in PENDING_WITHDRAWAL state and allows staff to approve or reject each withdrawal request.
     *
     * @param careerCentreStaff the staff performing the action
     */
    private void displayPendingWithdrawals(CareerCentreStaff careerCentreStaff) {
        List<InternshipApplication> pendingWithdrawals = internshipApplicationController.getWithdrawalRequests();

        if (pendingWithdrawals.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            return;
        }

        System.out.println("\n=== Pending Withdrawal Requests ===");
        for (InternshipApplication internshipApplication : pendingWithdrawals) {
            System.out.println(internshipApplication.getId() + " | Student: " + internshipApplication.getStudent().getId()
                    + " | Company: " + internshipApplication.getInternship().getCompanyName() + " | Internship: " + internshipApplication.getInternship().getInternshipTitle());
        }

        String applicationid = console.readLine("Enter Application ID to process: ");

        String choice =  console.readLine("Approve or Reject? (A/R): ");

        boolean success;

        if (choice.equals("A")) {
            success = internshipApplicationController.approveAppWithdrawal(applicationid);
        } else if (choice.equals("R")) {
            success = internshipApplicationController.rejectAppWithdrawal(applicationid);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (success) {
            System.out.println("Withdrawal request updated.");
        } else {
            System.out.println("Failed to update Withdrawal request.");
        }
    }

    /**
     * Displays a detailed, formatted breakdown of an internship posting, including metadata, applicant list, status and application outcomes.
     *
     * @param internship the internship to display
     */
    private void displayInternship(Internship internship){
        System.out.println("Internship: " + internship.getInternshipTitle() + " (" + internship.getId() + ")");
        System.out.println("Company: " + internship.getCompanyName() + " | Contact: " + internship.getCompRepIC().getFullName());
        System.out.println("Level: " + internship.getLevel() + " | Preferred Major: " + internship.getPreferredMajor() + " | Slots: " + internship.getNumOfSlots());
        System.out.println("Application Dates: " + internship.getAppOpenDate() + " - " + internship.getAppCloseDate() + " | Status: " + internship.getInternshipStatus());
        System.out.println("Applications:");
        for (InternshipApplication app : internship.getInternshipApplications()) {
            Student s = app.getStudent();
            System.out.print("- Student: " + s.getFullName() + ", Major: " + s.getMajor() + ", Year: " + s.getYearOfStudy() +
                            ", Status: " + app.getApplicationStatus());
            if (app.getApplicationStatus() == ApplicationStatus.SUCCESSFUL){
                System.out.println(", Offer Accepted: " + app.getOfferAccepted());
            } else {System.out.println();}
        }
        System.out.println("---------------------------------------------------");
    }

    /**
     * Generates and displays an internship report based on selected filters.
     * The filtering logic is performed by InternshipController.
     */
    public void displayReport(){
        List<Filter> filters = this.getFilters();
        ArrayList<Internship> report = internshipController.generateReport((ArrayList<Filter>) filters);
        for (Internship internship : report){
            displayInternship(internship);
        }
    }

    /**
     * Prompts the staff to select and add filtering criteria for the internship report.
     * Supported filters:
     * <ul>
     *     <li>Major</li>
     *     <li>Internship level</li>
     *     <li>Internship status</li>
     * </ul>
     *
     * @return a list of Filter objects to be applied by the controller
     */
    public List<Filter> getFilters() {
        List<Filter> filters = new ArrayList<>();
        int choice;

        do {
            System.out.println("\nChoose report filters:");
            System.out.println("1. Add Major filter");
            System.out.println("2. Add Level filter");
            System.out.println("3. Add Status filter");
            System.out.println("4. Generate Report");

            choice = console.readInt("Enter choice: ");

            switch (choice) {

                case 1 -> {
                    String input = console.readLine("Enter major (e.g. COMPUTER_SCIENCE): ").trim().toUpperCase();
                    try {
                        Major major = Major.valueOf(input);
                        filters.add(new MajorFilter(major));
                        System.out.println("Major filter added.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid major.");
                    }
                }

                case 2 -> {
                    String input = console.readLine("Enter level (e.g. BASIC, INTERMEDIATE, ADVANCED): ").trim().toUpperCase();
                    try {
                        InternshipLevel level = InternshipLevel.valueOf(input);
                        filters.add(new LevelFilter(level));
                        System.out.println("Level filter added.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid level.");
                    }
                }

                case 3 -> {
                    String input = console.readLine("Enter status (PENDING, APPROVED, REJECTED, FILLED): ").trim().toUpperCase();
                    try {
                        InternshipStatus status = InternshipStatus.valueOf(input);
                        filters.add(new StatusFilter(status));
                        System.out.println("Status filter added.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid status.");
                    }
                }

                case 4 -> System.out.println("Generating report...");

                default -> System.out.println("Invalid choice.");
            }

        } while (choice != 4);

        return filters;
    }

    /**
     * Displays all notifications addressed to the logged-in staff member.
     * Notifications are retrieved from the NotificationManager.
     * After displaying, all notifications are marked as read.
     *
     * @param careerCentreStaff the staff member whose notifications are shown
     */
    private void displayNotifications(CareerCentreStaff careerCentreStaff) {
        List<Notification> notifs = NotificationManager.getInstance().getNotifications(careerCentreStaff.getId());

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
        NotificationManager.getInstance().markAllAsRead(careerCentreStaff.getId());
    }


}
