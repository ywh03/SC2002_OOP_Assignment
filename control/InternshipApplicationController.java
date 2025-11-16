package control;

import entity.InternshipApplication;
import entity.Internship;
import entity.ApplicationStatus;
import entity.Student;

import java.util.ArrayList;
import java.util.List;

public class InternshipApplicationController{
    private List<InternshipApplication> applications;

    public InternshipApplicationController() {
        applications = new ArrayList<>();
    }

    public void printApplication() {
        if (applications.isEmpty()) {
            System.out.println("No applications were submitted.");
        }
        for (InternshipApplication app : applications) {
            System.out.println("Internship: " + app.getInternship().getInternshipTitle() + ", Student: " + app.getStudent().getFullName() + ", Status: " + app.getApplicationStatus() + ", Offer Accepted: " + app.getOfferAccepted());
        }
    }

    // student accepts the offer
    public void accept(InternshipApplication app) { 
        if (applications.contains(app) && app.getApplicationStatus() == ApplicationStatus.SUCCESSFUL) {
            app.setOfferAccepted(true);
            System.out.println(app.getStudent().getFullName() + " has accepted the internship offer.");
        } else {
            System.out.println("Cannot accept offer for this application.");
        }
    }

    // student withdraws their application
    public boolean withdraw(InternshipApplication app) {
        if (applications.contains(app) && !app.getOfferAccepted()) {
            applications.remove(app);
            app.getInternship().getInternshipApplications().remove(app);
            System.out.println(app.getStudent().getFullName() + " has withdrawn the application.");
            return true;
        } else {
            System.out.println("Cannot withdraw this application (already accepted or not found).");
            return false;
        }
    }

    // student applies for an internship
    public boolean apply(Internship internship, Student student) {
        // if they already applied, do not accept
        for (InternshipApplication app : applications) {
            if (app.getInternship() == internship && app.getStudent() == student) {
                System.out.println(student.getFullName() + " has already applied for " + internship.getInternshipTitle());
                return false;
            }
        }

        InternshipApplication newApp = new InternshipApplication(internship, student);
        applications.add(newApp);
        internship.getInternshipApplications().add(newApp); // also add to internship's list
        System.out.println(student.getFullName() + " applied for " + internship.getInternshipTitle());
        return true;
    }

    // means application status is successful -> give offer
    public void approveInternshipApplication(InternshipApplication app) {
        if (applications.contains(app)) {
            app.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
            System.out.println("Application approved for " + app.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    // company rejects application -> unsuccessful
    public void rejectInternshipApplication(InternshipApplication app) {
        if (applications.contains(app)) {
            app.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected for " + app.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    public void approveApplicationWithdrawal(InternshipApplication app) {
        if (applications.contains(app)) {
            applications.remove(app);
            app.getInternship().getInternshipApplications().remove(app);
            System.out.println("Withdrawal approved for " + app.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    public void rejectApplicationWithdrawal(InternshipApplication app) {
        if (applications.contains(app)) {
            // Do nothing to the application lols... just inform
            System.out.println("Withdrawal rejected for " + app.getStudent().getFullName() + ". Application remains " + app.getApplicationStatus());
        } else {
            System.out.println("Application not found.");
        }
    }
}
