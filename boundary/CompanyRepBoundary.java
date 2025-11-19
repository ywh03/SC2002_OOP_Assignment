package boundary;

import control.CompanyRepController;
import control.InternshipApplicationController;
import control.InternshipController;
import entity.CompanyRep;
import entity.Internship;
import entity.InternshipApplication;
import entity.Notification;
import util.ConsoleUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;
import entity.enums.Major;


import java.util.List;

import manager.NotificationManager;

/**
 * Boundary class responsible for all interactions with a Company Representative user.
 *
 * <p>This class handles the user interface and flow control for CompanyRep-related actions,
 * including:</p>
 * <ul>
 *     <li>Creating, editing, and updating internship postings</li>
 *     <li>Toggling visibility of postings</li>
 *     <li>Viewing interns' applications for a posting</li>
 *     <li>Accepting or rejecting internship applications</li>
 *     <li>Viewing personal notifications</li>
 *     <li>Displaying tables of internship postings</li>
 * </ul>
 */
public class CompanyRepBoundary {

    private final CompanyRepController companyRepController;
    private final InternshipController internshipController;
    private final InternshipApplicationController internshipApplicationController;
    private final LoginBoundary loginBoundary;
    private final ConsoleUtil console;

    /**
     * Constructs a boundary class for a Company Representative.
     *
     * @param companyRepController controller for CompanyRep registration & management
     * @param internshipController controller for internship posting operations
     * @param internshipApplicationController controller for application processing
     * @param console utility helper for console input
     */
    public CompanyRepBoundary(CompanyRepController companyRepController,  InternshipController internshipController, InternshipApplicationController internshipApplicationController, ConsoleUtil console, LoginBoundary loginBoundary) {
        this.companyRepController = companyRepController;
        this.internshipController = internshipController;
        this.internshipApplicationController = internshipApplicationController;
        this.loginBoundary = loginBoundary;
        this.console = console;
    }

    /**
     * Displays the main Company Representative menu and routes user choices to the appropriate boundary methods.
     *
     * @param companyRep the logged-in company representative
     */
    public void displayMenu(CompanyRep companyRep) {
        while (true) {
            System.out.println("\n=== Company Representative Menu ===");
            System.out.println("1. Create Internship Posting");
            System.out.println("2. Edit Internship Posting");
            System.out.println("3. Toggle Posting Visibility");
            System.out.println("4. View Applicants for a Posting");
            System.out.println("5. Accept/Reject Applicant");
            System.out.println("6. View My Postings");
            System.out.println("7. View Notifications");
            System.out.println("8: Change Password");
            System.out.println("9. Logout");

            String choice = console.readLine("Choose an option: ");

            switch (choice) {
                case "1" -> createInternship(companyRep);
                case "2" -> editInternship(companyRep);
                case "3" -> toggleVisibility(companyRep);
                case "4" -> displayApplications(companyRep);
                case "5" -> processApplication(companyRep);
                case "6" -> displayInternshipListings(companyRep);
                case "7" -> displayNotifications(companyRep);
                case "8" -> loginBoundary.handlePasswordChange(companyRep);
                case "9" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Handles user input to create a new internship posting.
     * Collects fields such as title, description, level, major, application dates and slots, then delegates creation to the controller.
     *
     * @param companyRep the CompanyRep creating the posting
     */
    private void createInternship(CompanyRep companyRep) {
        System.out.println("\n=== Create Internship Posting ===");
        String title = console.readLine("Title: ");
        String description = console.readLine("Description: ");

        InternshipLevel internshipLevel = null;
        while (internshipLevel == null){
            try {
                String levelInput = console.readLine("Level (BASIC, INTERMEDIATE, ADVANCED): ");
                internshipLevel = InternshipLevel.valueOf(levelInput.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level. Please try again.");
            }
        }

        Major preferredMajor = null;
        while (preferredMajor == null){
            try {
                String majorInput = console.readLine("Preferred Major: ");
                preferredMajor = Major.valueOf(majorInput.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid major. Please try again.");
            }
        }

        Date appOpenDate = readValidatedLegacyDate("Application Open Date (DD/MM/YYYY): ");
        Date appCloseDate = readValidatedLegacyDate("Application Close Date (DD/MM/YYYY): ");
        String companyName = companyRep.getCompanyName();

        int numOfSlots = 0;
        while (numOfSlots == 0) {
            try {
                numOfSlots = Integer.parseInt(console.readLine("Number of Slots Available: "));
                if (numOfSlots <= 0 || numOfSlots > 10) {
                    System.out.println("Number of slots must be between 1 and 10. Please try again.");
                    numOfSlots = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Input not a number. Please try again.");
            }
        }
        
        boolean success = internshipController.createInternship(title, description, internshipLevel, preferredMajor, appOpenDate, appCloseDate, companyName, companyRep, numOfSlots);
        
        if (success) {
            System.out.println("Internship created.");
        } else {
            System.out.println("Failed to create internship.");
        }
    }

    /**
     * Allows a CompanyRep to edit one of their pending internship postings.
     * Displays all editable postings, validates selection, prompts for updated fields, and delegates update logic to the controller.
     *
     * @param companyRep the CompanyRep performing the edit
     */
    private void editInternship(CompanyRep companyRep) {
        System.out.println("\n=== Edit Internship Posting ===");
        // display all the postings 
        ArrayList<Internship> pendingInternships = internshipController.getPendingInternships(companyRep.getId()); // i think this method changed
        if (pendingInternships.isEmpty()) {
            System.out.println("You have no internship postings available for editing (only pending status can be modified).");
            return;
        }

        System.out.println("\n=== Editable Internship Postings ===");

        printInternshipTable(pendingInternships);

        String internshipId = console.readLine("Enter ID of posting: ");

        Internship internshipToEdit = internshipController.getInternshipById(internshipId);

        if (internshipToEdit == null) {
            System.out.println("Invalid internship ID. Please choose a valid posting.");
            return;
        }

        if (!internshipToEdit.getCompRepIC().getId().equals(companyRep.getId())) {
            System.out.println("You are not authorized to edit this internship.");
            return;
        }

        if (internshipToEdit.getInternshipStatus() != InternshipStatus.PENDING){
            System.out.println("You can only edit PENDING internships.");
            return;
        }

        // enter the details 
        String title = console.readLine("Title: ");
        String description = console.readLine("Description: ");

        InternshipLevel internshipLevel = null;
        while (internshipLevel == null){
            try {
                String levelInput = console.readLine("Level (BASIC, INTERMEDIATE, ADVANCED): ");
                internshipLevel = InternshipLevel.valueOf(levelInput.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level. Please try again.");
            }
        }

        Major preferredMajor = null;
        while (preferredMajor == null){
            try {
                String majorInput = console.readLine("Preferred Major: ");
                preferredMajor = Major.valueOf(majorInput.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid major. Please try again.");
            }
        }

        Date appOpenDate = readValidatedLegacyDate("Application Open Date (DD/MM/YYYY): ");
        Date appCloseDate = readValidatedLegacyDate("Application Close Date (DD/MM/YYYY): ");

        // boolean datesValid = false;
        // while (!datesValid) {
        //     Date appOpenDate = readValidatedLegacyDate("Application Open Date (DD/MM/YYYY): ");
        //     Date appCloseDate = readValidatedLegacyDate("Application Close Date (DD/MM/YYYY): ");
        //     if (appOpenDate.compareTo(appCloseDate) > 0) {
        //         System.out.println("Error: The Application Closing Date cannot be earlier than the Application Opening Date. Please re-enter both dates.");
        //     } else {
        //         datesValid = true;
        //     }
        // }
        
        String companyName = companyRep.getCompanyName();
        int numOfSlots = Integer.parseInt(console.readLine("Number of Slots Available: "));
        
        boolean success = internshipController.editInternship(internshipId,title, description, internshipLevel, preferredMajor, appOpenDate, appCloseDate, companyName, companyRep, numOfSlots);
        
        if (success) {
            System.out.println("Internship created.");
        } else {
            System.out.println("Failed to create internship.");
        }
    }

    // Helper functions

    /**
     * Shortens a string to a maximum length, appending "..." if truncated.
     */
    private String truncate(String s, int max) {
        return (s.length() <= max) ? s : s.substring(0, max - 3) + "...";
    }

    /**
     * Formats a Date into DD/MM/YYYY or returns "-" if null.
     */
    private String formatDate(Date d) {
        if (d == null) return "-";
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(d);
    }

    /**
     * Prints a formatted table of internship postings, including fields such as
     * ID, title, level, major, dates, slots, visibility and status.
     *
     * @param internships list of internships to print
     */
    private void printInternshipTable(List<Internship> internships) {
        System.out.printf(
                "%-8s | %-25s | %-15s | %-30s | %-12s | %-12s | %-6s | %-10s | %-20s | %s\n",
                "ID",
                "Title",
                "Level",
                "Major",
                "Open Date",
                "Close Date",
                "Slots",
                "Visible",
                "Status",
                "Company"
        );

        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Internship i : internships) {
            System.out.printf(
                    "%-8s | %-25s | %-15s | %-30s | %-12s | %-12s | %-6s | %-10s | %-20s | %s\n",
                    i.getId(),
                    truncate(i.getInternshipTitle(), 25),
                    i.getLevel(),
                    i.getPreferredMajor(),
                    formatDate(i.getAppOpenDate()),
                    formatDate(i.getAppCloseDate()),
                    i.getNumOfSlots(),
                    i.getVisibility() ? "YES" : "NO",
                    i.getInternshipStatus(),
                    i.getCompanyName()
            );
        }

        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Displays all internship postings created by the logged-in CompanyRep in a formatted table view.
     *
     * @param companyRep the posting owner whose listings are displayed
     */
    private void displayInternshipListings(CompanyRep companyRep) {
        List<Internship> internships = internshipController.getInternshipListings(companyRep.getId());

        if (internships.isEmpty()) {
            System.out.println("You have no internship postings.");
            return;
        }

        System.out.println("\n=== My Internship Postings ===");
        
        printInternshipTable(internships);


    }

    /**
     * Toggles the visibility of a CompanyRep-owned internship posting.
     * Only the owner of the posting may modify its visibility.
     *
     * @param companyRep the posting owner performing the action
     */
    private void toggleVisibility(CompanyRep companyRep) {
        String internshipId = console.readLine("Internship ID: ");

        boolean success = internshipController.toggleVisibility(companyRep, internshipId);

        if (success) {
            System.out.println("Internship visibility toggled.");
        } else {
            System.out.println("Failed to toggle Internship visibility. Choose a valid ID.");
        }

    }

    /**
     * Displays all internship applications for a selected internship posting.
     * Validates internship existence and ownership before listing applications.
     *
     * @param companyRep the CompanyRep viewing applications
     * @return true if applications were displayed, false if posting was invalid or empty
     */
    private boolean displayApplications(CompanyRep companyRep) {

        String internshipId = console.readLine("Internship ID: ");
        List<InternshipApplication> internshipApplications = internshipController.getApplications(internshipId);

        if (internshipApplications == null) {
            System.out.println("Internship not found.");
            return false;
        }
        if (internshipApplications.isEmpty()) {
            System.out.println("There are no applications.");
            return false;
        }
        System.out.println("\n=== Applications ===");
        for (InternshipApplication internshipApplication: internshipApplications) {
            System.out.println(internshipApplication.getId() + " | Student: " + internshipApplication.getStudent().getId() + " | Status: " + internshipApplication.getApplicationStatus());
        }
        return true;
    }

    /**
     * Processes a single internship application by accepting or rejecting it.
     * This is called only after displaying available applications.
     *
     * @param companyRep the owner of the internship posting
     */
    private void processApplication(CompanyRep companyRep) {
        if (!displayApplications(companyRep)) return;
        
        String internshipApplicationId = console.readLine("Internship Application ID: ");
        String choice = console.readLine("Accept or Reject (A/R)").toUpperCase();

        boolean success;

        if (choice.equals("A")) {
            success = internshipApplicationController.approveInternshipApplication(internshipApplicationId);
        } else if (choice.equals("R")) {
            success = internshipApplicationController.rejectInternshipApplication(internshipApplicationId);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (success) {
            System.out.println("Internship application updated.");
        } else {
            System.out.println("Failed to update Internship application.");
        }

    }

    /**
     * Reads and validates a date from user input. Only accepts strict DD/MM/YYYY format.
     * Re-prompts the user until a valid date is provided.
     *
     * @param prompt the message shown to the user
     * @return the validated Date object
     */
    private Date readValidatedLegacyDate(String prompt) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setLenient(false); // don't accept invalid dates

        Date date = null;

        while (date == null) {
            String input = console.readLine(prompt);
            try {
                date = formatter.parse(input);
                
            } catch (ParseException e) {
                System.out.println("Invalid date format or date. Please use DD/MM/YYYY");
            }
        }
        return date;
    }

    /**
     * Displays all notifications for the logged-in CompanyRep.
     * Unread notifications are marked, and all messages are marked as read after they are displayed.
     *
     * @param companyRep the user whose notifications are shown
     */
    private void displayNotifications(CompanyRep companyRep) {
        List<Notification> notifs = NotificationManager.getInstance().getNotifications(companyRep.getId());

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
        NotificationManager.getInstance().markAllAsRead(companyRep.getId());
    }



}
