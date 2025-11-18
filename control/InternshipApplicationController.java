package control;

import entity.InternshipApplication;
import entity.Internship;
import entity.enums.ApplicationStatus;
import repository.InternshipApplicationRepository;
import repository.InternshipRepository;
import entity.Student;

public class InternshipApplicationController{
    private InternshipApplicationRepository internshipApplicationRepository;
    private InternshipRepository internshipRepository;

    public InternshipApplicationController() { // constructor???
        internshipApplicationRepository = new InternshipApplicationRepository();
        internshipRepository = new InternshipRepository();
    }

    public void printApplication() {
        if (internshipApplicationRepository.getAll().isEmpty()) {
            System.out.println("No applications were submitted.");
            return;
        }

        for (InternshipApplication intApp : internshipApplicationRepository.getAll()) {
            System.out.println("Internship: " + intApp.getInternship().getInternshipTitle() + ", Student: " + intApp.getStudent().getFullName()
                                + ", Status: " + intApp.getApplicationStatus() + ", Offer Accepted: " + intApp.getOfferAccepted());
        }
    }

    // student accepts the offer
    public void accept(InternshipApplication intApp) { 
        if (intApp.getApplicationStatus() == ApplicationStatus.SUCCESSFUL && !intApp.getOfferAccepted()) {
            intApp.setOfferAccepted(true);
            internshipApplicationRepository.save(intApp); //check if update = save
            System.out.println(intApp.getStudent().getFullName() + " has accepted the internship offer.");
        } else {
            System.out.println("Cannot accept offer for this application.");
        }
    }

    // student withdraws their application
    public boolean withdraw(InternshipApplication intApp) {
        if (!intApp.getOfferAccepted()) {
            internshipApplicationRepository.delete(intApp);
            Internship internship = intApp.getInternship();
            internship.getInternshipApplications().remove(intApp);
            internshipRepository.save(internship);
            System.out.println(intApp.getStudent().getFullName() + " has withdrawn the application.");
            return true;
        } else {
            System.out.println("Cannot withdraw this application (application has already been accepted or not found).");
            return false;
        }
    }

    // student applies for an internship
    public boolean apply(Internship internship, Student student) {
        // if they already applied, do not accept
        boolean alreadyApplied = internshipApplicationRepository.getAll().stream().anyMatch(a -> a.getInternship() == internship && a.getStudent() == student);
            if (alreadyApplied) {
                System.out.println(student.getFullName() + " has already applied for " + internship.getInternshipTitle());
                return false;
            }

        String newId = internshipApplicationRepository.generateNextId();
        InternshipApplication newIntApp = new InternshipApplication(newId, internship, student);
        internshipApplicationRepository.save(newIntApp);
        internship.getInternshipApplications().add(newIntApp); // also add to internship's list
        internshipRepository.save(internship);
        System.out.println(student.getFullName() + " applied for " + internship.getInternshipTitle());
        return true;
    }

    // means application status is successful -> give offer
    public void approveInternshipApplication(InternshipApplication intApp) {
        if (internshipApplicationRepository.getAll().contains(intApp)) {
            intApp.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
            System.out.println("Application approved for " + intApp.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    // company rejects application -> unsuccessful
    public void rejectInternshipApplication(InternshipApplication intApp) {
        if (internshipApplicationRepository.getAll().contains(intApp)) {
            intApp.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected for " + intApp.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    public void approveApplicationWithdrawal(InternshipApplication intApp) {
        if (internshipApplicationRepository.getAll().contains(intApp)) {
            internshipApplicationRepository.delete(intApp);
            intApp.getInternship().getInternshipApplications().remove(intApp);
            System.out.println("Withdrawal approved for " + intApp.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    public void rejectApplicationWithdrawal(InternshipApplication app) {
        if (internshipApplicationRepository.getAll().contains(app)) {
            System.out.println("Withdrawal rejected for " + app.getStudent().getFullName() + ". Application remains " + app.getApplicationStatus());
        } else {
            System.out.println("Application not found.");
        }
    }
}
