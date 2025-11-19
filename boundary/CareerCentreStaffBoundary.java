package boundary;

import control.CompanyRepController;
import control.InternshipApplicationController;
import control.InternshipController;
import entity.CareerCentreStaff;
import entity.CompanyRep;
import entity.Internship;
import entity.InternshipApplication;
import entity.Student;
import util.ConsoleUtil;

import java.util.ArrayList;
import java.util.List;

public class CareerCentreStaffBoundary {

    private InternshipController internshipController;
    private CompanyRepController companyRepController;
    private InternshipApplicationController internshipApplicationController;
    private ConsoleUtil console;

    public CareerCentreStaffBoundary(InternshipController internshipController,  CompanyRepController companyRepController, InternshipApplicationController internshipApplicationController) {
        this.internshipController = internshipController;
        this.companyRepController = companyRepController;
        this.internshipApplicationController = internshipApplicationController;
        this.console = new ConsoleUtil();
    }

    public void displayMenu(CareerCentreStaff careerCentreStaff) {
        while (true) {
            System.out.println("\n=== Career Centre Staff Menu ===");
            System.out.println("1. View / Approve / Reject Company Rep Registrations");
            System.out.println("2. View / Approve / Reject Internship Postings");
            System.out.println("3. Handle Withdrawal Requests");
            System.out.println("4. Generate Internship Report");
            System.out.println("5. Logout");

            String choice = console.readLine("Enter your choice: ");

            switch (choice) {
                case "1" -> displayPendingRegistrations(staff);
                case "2" -> displayPendingInternships(staff);
                case "3" -> displayReport();
                case "4" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void displayPendingRegistrations(CareerCentreStaff careerCentreStaff) {
        List<CompanyRep> pendingCompanyReps = companyRepController.getPendingCompanyReps();

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

    private void displayPendingWithdrawals(CareerCentreStaff careerCentreStaff) {
        List<InternshipApplication> pendingWithdrawals = internshipApplicationController.getPendingWithdrawals();

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

    private void displayInternship(){
        System.out.println("Internship: " + internship.getInternshipTitle() + " (" + internship.getInternshipId() + ")");
        System.out.println("Company: " + internship.getCompanyName() + " | Contact: " + internship.getCompRepIC().getName());
        System.out.println("Level: " + internship.getLevel() + " | Preferred Major: " + internship.getPreferredMajor() + " | Slots: " + internship.getNumOfSlots());
        System.out.println("Application Dates: " + internship.getAppOpenDate() + " - " + internship.getAppCloseDate() + " | Status: " + internship.getInternshipStatus());
        System.out.println("Applications:");
        for (InternshipApplication app : internship.getInternshipApplications()) {
            Student s = app.getStudent();
            System.out.print("- Student: " + s.getName() + ", Major: " + s.getMajor() + ", Year: " + s.getYearOfStudy() +
                            ", Status: " + app.getApplicationStatus());
            if (app.getApplicationStatus() == SUCCESSFUL){
                System.out.println(", Offer Accepted: " + app.isOfferAccepted());
            } else {System.out.println();}
        }
        System.out.println("---------------------------------------------------");
    }

    public void displayReport(){
        ArrayList<ArrayList<String>> filters = this.getFilters();
        HashSet<Internship> report = InternshipController.generaterReport(filters);
        for (Internship internship : report){
            internship.displayInternship();
        }
    }

    public ArrayList<ArrayList<String>> getFilters(){
        ArrayList<ArrayList<String>> filters = new ArrayList<>();
        filters.add(new ArrayList<>());
        filters.add(new ArrayList<>());
        filters.add(new ArrayList<>());

        do {
            System.out.println("Choose report filters and input parameters:");
            System.out.println("1. Add major filter");
            System.out.println("2. Add level filter");
            System.out.println("3. Add status filter");
            System.out.println("4. Generate Report");
            int choice = console.readInt("Please enter a number: ");

            
            switch(choice){
                case 1:
                    String majorFilter = console.readLine("Enter a major: ");
                    filters.get(0).add(majorFilter.toUpperCase());
                    break;
                case 2:
                    String levelFilter = console.readLine("Enter a level (BASIC, INTERMEDIATE, ADVANCED): ");
                    filters.get(0).add(levelFilter.toUpperCase());
                    break;
                case 3:
                    String statusFilter = console.readLine("Enter a status (PENDING, APPROVED, REJECTED, FILLED): ");
                    filters.get(0).add(levelFilter.toUpperCase());
                    break;
                case 4:
                    System.out.println("Generating report...");
                    break;
                default:
                    System.out.println("Please enter a valid number: ");
            }
        } while (choice != 4);

        return filters;
    }

}
