package control;

import entity.InternshipApplication;
import entity.Internship;
import entity.enums.ApplicationStatus;
import repository.InternshipApplicationRepository;
import repository.InternshipRepository;
import entity.Student;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;
import entity.enums.Major;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    // student rejects the offer
    public void rejectOffer(String internshipAppId) {
    InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null && intApp.getApplicationStatus() == ApplicationStatus.SUCCESSFUL) {
            intApp.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            internshipApplicationRepository.save(intApp);
        }
    }

    // student applies for an internship
    public boolean apply(String internshipId, Student student) {

        Internship internship = internshipRepository.findById(internshipId);

        if (internship == null) return false;

        // if they already applied, do not accept
        boolean alreadyApplied = internshipApplicationRepository.findAll().stream()
                .anyMatch(a -> a.getInternship()
                .getId()
                .equals(internshipId) && a.getStudent() == student);

        if (alreadyApplied) {
            System.out.println(student.getFullName() + " has already applied for " + internship.getInternshipTitle());
            return false;
        }
        
        if (!canApply(student)) {
            System.out.println("You cannot apply for any more internships.");
            return false;
        }

        if (!isEligible(student, internship)) {
            System.out.println("You are not eligible to apply for this internship.");
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
    public boolean approveInternshipApplication(String internshipAppId) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null) {
            intApp.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
            internshipApplicationRepository.save(intApp);
            return true;
        }
        return false;
    }

    // company rejects application -> unsuccessful
    public boolean rejectInternshipApplication(String internshipAppId) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null) {
            intApp.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            internshipApplicationRepository.save(intApp);
            return true;
        }
        return false;
    }

    // student requests withdrawal
    public boolean requestWithdrawal(String internshipAppId, Student student) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp == null) return false;

        if (!intApp.getStudent().getId().equals(student.getId())) return false;

        if (intApp.getApplicationStatus() == ApplicationStatus.PENDING) {
            intApp.setApplicationStatus(ApplicationStatus.PENDING_WITHDRAWAL);
            internshipApplicationRepository.save(intApp);
            return true;
        }
        // System.out.println("Cannot request withdrawal for this application.");
        return false;
    }

    public boolean approveAppWithdrawal(String internshipAppId) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null  && intApp.getApplicationStatus() == ApplicationStatus.PENDING_WITHDRAWAL) {
            intApp.setApplicationStatus(ApplicationStatus.WITHDRAWN);
            internshipApplicationRepository.save(intApp);
            intApp.getInternship().getInternshipApplications().remove(intApp);
            internshipRepository.save(intApp.getInternship());
            return true;
        // } else {
        //     System.out.println("Application not found.");
        }
        return false;
    }

    public boolean rejectAppWithdrawal(String internshipAppId) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null && intApp.getApplicationStatus() == ApplicationStatus.PENDING_WITHDRAWAL) {
            intApp.setApplicationStatus(ApplicationStatus.PENDING);
            internshipApplicationRepository.save(intApp);
            return true;
        // } else {
        //     System.out.println("Application not found.");
        }
        return false;
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
        Major preferredMajor = internship.getPreferredMajor();
        if (preferredMajor != null) {
            if (student.getMajor() == null) return false;
            if (student.getMajor() != preferredMajor) return false;
        }

        // rej 5: internship level don't match the year (not sure if this one we incl here or not as well)
        if (student.getYearOfStudy() <= 2) {
            if (internship.getLevel() != InternshipLevel.BASIC) return false;
        }

        // rej 6: internship is filled
        if (internship.getInternshipStatus() == InternshipStatus.FILLED) return false;

        return true;
    }

    public ArrayList<InternshipApplication> getWithdrawalRequests() {

        List<InternshipApplication> result = internshipApplicationRepository.findAll().stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.PENDING_WITHDRAWAL)
                .toList();

        return new ArrayList<>(result);

    }

    public ArrayList<InternshipApplication> companyRepGetInternshipApplications(String internshipId) {

        List<InternshipApplication> result = internshipApplicationRepository.findAll().stream()
                .filter(internshipApplication -> internshipApplication.getInternship().getId().equals(internshipId))
                .toList();

        return new ArrayList<>(result);

    }

    public ArrayList<InternshipApplication> studentGetInternshipApplications(String userId) {

        List<InternshipApplication> result = internshipApplicationRepository.findAll().stream()
                .filter(internshipApplication -> internshipApplication.getStudent().getId().equals(userId))
                .toList();

        return new ArrayList<>(result);

    }
}
