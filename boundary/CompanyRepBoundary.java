package boundary;

import control.CompanyRepController;
import control.InternshipApplicationController;
import control.InternshipController;
import entity.CompanyRep;
import entity.Internship;
import entity.InternshipApplication;
import entity.Student;
import entity.Notification;
import util.ConsoleUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import entity.enums.InternshipLevel;
import entity.enums.Major;


import java.util.List;

import manager.NotificationManager;

public class CompanyRepBoundary {

    private final CompanyRepController companyRepController;
    private final InternshipController internshipController;
    private final InternshipApplicationController internshipApplicationController;
    private final ConsoleUtil console;

    public CompanyRepBoundary(CompanyRepController companyRepController,  InternshipController internshipController, InternshipApplicationController internshipApplicationController, ConsoleUtil console) {
        this.companyRepController = companyRepController;
        this.internshipController = internshipController;
        this.internshipApplicationController = internshipApplicationController;
        this.console = console;
    }

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

            System.out.println("8. Logout");

            String choice = console.readLine("Choose an option: ");

            switch (choice) {
                case "1" -> createInternship(companyRep);
                case "2" -> editInternship(companyRep);
                case "3" -> toggleVisibility(companyRep);
                case "4" -> displayApplications(companyRep);
                case "5" -> processApplication(companyRep);
                case "6" -> displayInternshipListings(companyRep);
                case "7" -> displayNotifications(companyRep);
                case "8" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

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

    private void editInternship(CompanyRep companyRep) {
        System.out.println("\n=== Edit Internship Posting ===");
        // display all the postings 
        ArrayList<Internship> pendingPostings = internshipController.getPendingInternships(companyRep.getId()); // i think this method changed
        if (pendingPostings.isEmpty()) {
            System.out.println("You have no internship postings available for editing (only pending status can be modified).");
            return;
        }
        
        System.out.println("\n=== Editable Internship Postings ===");
        for (Internship internship : pendingPostings) {
            System.out.println(internship.getId() + " | " + internship.getInternshipTitle() + " | Visible: " + internship.getVisibility());
        }

        String internshipId = console.readLine("Enter ID of posting: ");
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
        String companyName = companyRep.getCompanyName();
        int numOfSlots = Integer.parseInt(console.readLine("Number of Slots Available: "));
        
        boolean success = internshipController.editInternship(internshipId,title, description, internshipLevel, preferredMajor, appOpenDate, appCloseDate, companyName, companyRep, numOfSlots);
        
        if (success) {
            System.out.println("Internship created.");
        } else {
            System.out.println("Failed to create internship.");
        }
    }

    private void displayInternshipListings(CompanyRep companyRep) {
        List<Internship> internships = internshipController.getInternshipListings(companyRep.getId());

        if (internships.isEmpty()) {
            System.out.println("You have no internship postings.");
            return;
        }

        System.out.println("\n=== My Internship Postings ===");

        System.out.printf("%-8s | %-30s | %-10s | %-15s | %-10s | %s\n",
                "ID", "Title", "Visible", "Status", "Slots", "Major");
        System.out.println("-----------------------------------------------------------------------------------------------");

        for (Internship i : internships) {
            System.out.printf("%-8s | %-30s | %-10s | %-15s | %-10d | %s\n",
                    i.getId(),
                    i.getInternshipTitle(),
                    i.getVisibility() ? "YES" : "NO",
                    i.getInternshipStatus(),
                    i.getNumOfSlots(),
                    i.getPreferredMajor());
        }

        System.out.println("-----------------------------------------------------------------------------------------------");
    }

    private void toggleVisibility(CompanyRep companyRep) {
        String internshipId = console.readLine("Internship ID: ");

        boolean success = internshipController.toggleVisibility(companyRep, internshipId);

        if (success) {
            System.out.println("Internship visibility toggled.");
        } else {
            System.out.println("Failed to toggle Internship visibility. Choose a valid ID.");
        }

    }

    private void displayApplications(CompanyRep companyRep) {
        String internshipId = console.readLine("Internship ID: ");
        List<InternshipApplication> internshipApplications = internshipController.getApplications(internshipId);

        if (internshipApplications == null) {
            System.out.println("Internship not found.");
            return;
        }
        if (internshipApplications.isEmpty()) {
            System.out.println("There are no applications.");
            return;
        }
        System.out.println("\n=== Applications ===");
        for (InternshipApplication internshipApplication: internshipApplications) {
            System.out.println(internshipApplication.getId() + " | Student: " + internshipApplication.getStudent().getId() + " | Status: " + internshipApplication.getApplicationStatus());
        }
    }

    private void processApplication(CompanyRep companyRep) {
        displayApplications(companyRep);
        
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
