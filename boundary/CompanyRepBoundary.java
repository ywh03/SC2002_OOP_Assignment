package boundary;

import control.CompanyRepController;
import control.InternshipApplicationController;
import control.InternshipController;
import entity.CompanyRep;
import entity.Internship;
import entity.InternshipApplication;
import util.ConsoleUtil;

import java.util.ArrayList;
import java.util.Date;
import entity.enums.InternshipLevel;
import entity.enums.Major;


import java.util.List;

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

            System.out.println("7. Logout");

            String choice = console.readLine("Choose an option: ");

            switch (choice) {
                case "1" -> createInternship(rep);
                case "2" -> editInternship(rep);
                case "3" -> handleToggleVisibility(rep);
                case "4" -> handleViewApplicants(rep);
                case "5" -> handleApplicantDecision(rep);
                case "6" -> handleViewPostings(rep);
                case "7" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createInternship(CompanyRep companyRep) {
        String internshipId = ""; // how to get the internshipID


        System.out.println("\n=== Create Internship Posting ===");
        String title = console.readLine("Title: ");
        String description = console.readLine("Description: ");
        String levelInput = console.readLine("Level");
        // convert to InternshipLevel enum
        InternshipLevel level = InternshipLevel.valueOf(levelInput.toUpperCase()); 
        String preferredMajorInput = console.readLine("Preferred Major: ");
        // convert to Major enum
        Major preferredMajor = Major.valueOf(preferredMajorInput.toUpperCase()); 
        Date appOpenDate = console.readLine("Application Open Date (DD/MM/YYYY): ");
        Date appCloseDate = console.readLine("Application Close Date (DD/MM/YYYY): ");
        String companyName = companyRep.getCompanyName(); 
        String compRepIC = companyRep.getFullName(); 
        int numOfSlots = Integer.parseInt(console.readLine("Number of Slots Available: "));
        
        
        boolean success = internshipController.createInternship(internshipId,title, description, level, preferredMajor, appOpenDate, appCloseDate, companyName, compRepIC, numOfSlots);
        
        if (success) {
            System.out.println("Internship created.");
        } else {
            System.out.println("Failed to create internship.");
        }
    }

    private void editInternship(CompanyRep companyRep) {
        System.out.println("\n=== Edit Internship Posting ===");
        // display all the postings 
        ArrayList<Internship> pendingPostings = internshipController.getPendingInternships(companyRep.getId());
        if (pendingPostings.isEmpty()) {
            System.out.println("You have no internship postings available for editing (only pending status can be modified).");
        return;
        
        System.out.println("\n=== Editable Internship Postings ===");
        for (Internship internship : pendingPostings) {
            System.out.println(internship.getId() + " | " + internship.getInternshipTitle() + " | Visible: " + internship.getVisibility());
        }

        String internshipId = console.readLine("Enter ID of posting: ");
        // enter the details 
        String title = console.readLine("Title: ");
        String description = console.readLine("Description: ");
        String levelInput = console.readLine("Level");
        // convert to InternshipLevel enum
        InternshipLevel level = InternshipLevel.valueOf(levelInput.toUpperCase()); 
        String preferredMajorInput = console.readLine("Preferred Major: ");
        // convert to Major enum
        Major preferredMajor = Major.valueOf(preferredMajorInput.toUpperCase()); 
        Date appOpenDate = console.readLine("Application Open Date (DD/MM/YYYY): ");
        Date appCloseDate = console.readLine("Application Close Date (DD/MM/YYYY): ");
        String companyName = companyRep.getCompanyName(); 
        String compRepIC = companyRep.getFullName(); 
        int numOfSlots = Integer.parseInt(console.readLine("Number of Slots Available: "));
        
        boolean success = internshipController.editInternship(internshipId,title, description, level, preferredMajor, appOpenDate, appCloseDate, companyName, compRepIC, numOfSlots);
        
        if (success) {
            System.out.println("Internship created.");
        } else {
            System.out.println("Failed to create internship.");
        }
    }

    private void displayInternshipListings(CompanyRep companyRep) {
        List<Internship> internships = internshipController.displayInternshipListings(companyRep);

        if (internships.isEmpty()) {
            System.out.println("You have no Internship postings.");
            return;
        }

        System.out.println("\n=== My Internship Postings ===");
        for (Internship internship : internships) {
            System.out.println(internship.getId() + " | " + internship.getInternshipTitle() + " | Visible: " + internship.getVisibility());
        }

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
        List<InternshipApplication> internshipApplications = internshipController.displayApplications(internshipId);

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
            System.out.println(internshipApplication.getId() + " | Student: " + internshipApplication.getStudentId() + " | Status: " + internshipApplication.getStatus());
        }
    }

    private void processApplication(CompanyRep companyRep) {
        String internshipApplicationId = console.readLine("Internship Application ID: ");
        String choice = console.readLine("Accept or Reject (A/R)").toUpperCase();

        boolean success;

        if (choice.equals("A")) {
            success = internshipApplicationController.approveInternshipApplication(companyRep, internshipApplicationId);
        } else if (choice.equals("R")) {
            success = internshipApplicationController.rejectInternshipApplication(companyRep, internshipApplicationId);
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




}
