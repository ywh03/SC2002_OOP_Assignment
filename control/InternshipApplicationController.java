package control;

import entity.InternshipApplication;
import entity.Internship;
import entity.enums.ApplicationStatus;
import repository.InternshipApplicationRepository;
import repository.InternshipRepository;
import entity.Student;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;
import java.util.Date;
import java.util.stream.Collectors;

public class InternshipApplicationController{
    private InternshipApplicationRepository internshipApplicationRepository;
    private InternshipRepository internshipRepository;

    public InternshipApplicationController() { 
        internshipApplicationRepository = new InternshipApplicationRepository();
        internshipRepository = new InternshipRepository();
    }

    public void printApplication() {
        if (internshipApplicationRepository.findAll().isEmpty()) {
            System.out.println("No applications were submitted.");
            return;
        }

        for (InternshipApplication intApp : internshipApplicationRepository.findAll()) {
            System.out.println("Internship: " + intApp.getInternship().getInternshipTitle() + ", Student: " + intApp.getStudent().getFullName()
                                + ", Status: " + intApp.getApplicationStatus() + ", Offer Accepted: " + intApp.getOfferAccepted());
        }
    }

    // student accepts the offer
    public void acceptOffer(InternshipApplication intApp) { 
        if (intApp.getApplicationStatus() == ApplicationStatus.SUCCESSFUL && !intApp.getOfferAccepted()) {
            intApp.setOfferAccepted(true);
            internshipApplicationRepository.save(intApp); //check if update = save

            // after student accepts one internship offer, they will withdraw all their other applications
            Student student = intApp.getStudent();
            for (InternshipApplication otherIntApp : student.getAppliedInternships().stream().filter( a -> !a.getId().equals(intApp.getId())).collect((Collectors.toList()))) {
                internshipApplicationRepository.delete(otherIntApp);
                // remove from internship list as well
                Internship otherInternshipApplied = otherIntApp.getInternship();
                otherInternshipApplied.getInternshipApplications().removeIf(a -> a.getId().equals(otherIntApp.getId()));
                internshipRepository.save(otherInternshipApplied);
                // remove from student's list to update it as well
                student.getAppliedInternships().removeIf(a -> a.getId().equals(otherIntApp.getId()));
            }

            // check if internshipstatus goes to filled:
            Internship internship = intApp.getInternship();
            Long acceptedOffersCount = internship.getInternshipApplications().stream().filter(a -> a.getOfferAccepted()).count();
            if (acceptedOffersCount >= internship.getNumOfSlots()) {
                internship.setInternshipStatus(InternshipStatus.FILLED);
            }
            internshipRepository.save(internship);
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
        boolean alreadyApplied = internshipApplicationRepository.findAll().stream().anyMatch(a -> a.getInternship() == internship && a.getStudent() == student);
            if (alreadyApplied) {
                System.out.println(student.getFullName() + " has already applied for " + internship.getInternshipTitle());
                return false;
            }
        
        // student haven't apply yet -> generate a new id for them
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
        if (internshipApplicationRepository.findAll().contains(intApp)) {
            intApp.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
            System.out.println("Application approved for " + intApp.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    // company rejects application -> unsuccessful
    public void rejectInternshipApplication(InternshipApplication intApp) {
        if (internshipApplicationRepository.findAll().contains(intApp)) {
            intApp.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected for " + intApp.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    public void approveAppWithdrawal(InternshipApplication intApp) {
        if (internshipApplicationRepository.findAll().contains(intApp)) {
            internshipApplicationRepository.delete(intApp);
            intApp.getInternship().getInternshipApplications().remove(intApp);
            System.out.println("Withdrawal approved for " + intApp.getStudent().getFullName());
        } else {
            System.out.println("Application not found.");
        }
    }

    public void rejectAppWithdrawal(InternshipApplication app) {
        if (internshipApplicationRepository.findAll().contains(app)) {
            System.out.println("Withdrawal rejected for " + app.getStudent().getFullName() + ". Application remains " + app.getApplicationStatus());
        } else {
            System.out.println("Application not found.");
        }
    }

    public boolean canApply(Student student) {
        // case 0: student don't exist:
        if (student == null) {
            return false;
        }
        // case 1: student has already accepted an offer
        boolean hasAcceptedOffer = student.getAppliedInternships().stream().anyMatch(a -> a.getOfferAccepted());
        if (hasAcceptedOffer) {
            return false;
        }
        // case 2: student's current applications = 3 already (not incl of unsuccessful ones)
        long currentIntAppCount = student.getAppliedInternships().stream().filter(a -> a.getApplicationStatus() != ApplicationStatus.UNSUCCESSFUL).count();
        return currentIntAppCount < 3; // allow up to 3 active applications
    }

    public boolean isEligible(Student student, Internship internship) {
        // case 0: student don't exist:
        if (student == null | internship == null) {
            return false;
        }
        // rej 1: internship status (not sure if this is needed as well but put first lols -- depends on how we're toggling visibility imo)
        if (internship.getInternshipStatus() != InternshipStatus.APPROVED) {
            return false;
        }
        // rej 2: visbility
        if (!internship.getVisibility()) {
            return false;
        }
        // rej 3: check application dates (not sure if this one we incl here or not as well)
        Date today = new Date();
        Date open = internship.getAppOpenDate();
        Date close = internship.getAppCloseDate();
        if (open != null && today.before(open)) return false;
        if (close != null && today.after(close)) return false;

        // rej 4: check for preferred major (not sure if this one we incl here or not as well)
        String preferredMajor = internship.getPreferredMajor();
        if (preferredMajor != null && !preferredMajor.trim().isEmpty()) {
            if (student.getMajor() == null) return false;
            if (!preferredMajor.trim().equalsIgnoreCase(student.getMajor().trim())) return false;
        }

        // rej 5: internship level don't match the year (not sure if this one we incl here or not as well)
        if (student.getYearOfStudy() <= 2) {
            if (internship.getLevel() != InternshipLevel.BASIC) return false;
        }

        // rej 6: internship is filled
        if (internship.getInternshipStatus() == InternshipStatus.FILLED) return false;

        // rej 7: student applied for this specific internship already
        if (internship.getInternshipApplications().stream().anyMatch(a -> a.getInternship().getId().equals(internship.getId()))) {
            return false;
        }

        // rej 8: student can't apply for anymore internships
        if (!canApply(student)) return false;

        return true;
    }

    public String displayInternshipApplications(String InternshipId) {

    }
}
