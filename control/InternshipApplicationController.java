package control;

import entity.InternshipApplication;
import entity.Internship;
import entity.enums.ApplicationStatus;
import repository.InternshipApplicationRepository;
import repository.InternshipRepository;
import repository.UserRepository;
import entity.Student;
import entity.User;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;
import entity.enums.Major;
import manager.NotificationManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for all business logic related to internship applications.
 *
 * <p>This includes handling:</p>
 * <ul>
 *     <li>Student internship applications</li>
 *     <li>Offer approval/rejection by company representatives</li>
 *     <li>Student acceptance/rejection of internship offers</li>
 *     <li>Withdrawal request processing and approval</li>
 *     <li>Eligibility checking and application limits</li>
 * </ul>
 */
public class InternshipApplicationController {

    private InternshipApplicationRepository internshipApplicationRepository;
    private InternshipRepository internshipRepository;
    private UserRepository userRepository;
    private NotificationManager notificationManager;

    private String careerCenterStaffId;

    /**
     * Creates the controller with required repositories and managers injected.
     *
     * @param internshipApplicationRepository repository for storing internship applications
     * @param internshipRepository repository for internships
     * @param userRepository repository for user data (students and company reps)
     */
    public InternshipApplicationController(InternshipApplicationRepository internshipApplicationRepository, InternshipRepository internshipRepository, UserRepository userRepository) { // empty constructor, dont create a new repo lol

        this.internshipApplicationRepository = internshipApplicationRepository;
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
        this.notificationManager = NotificationManager.getInstance();

    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void setInternshipApplicationRepository(InternshipApplicationRepository repo) {
        this.internshipApplicationRepository = repo;
    }
    public void setInternshipRepository(InternshipRepository repo) {
        this.internshipRepository = repo;
    }
    public void setNotificationManager(NotificationManager manager) {
        this.notificationManager = manager;
    }
    public void setCareerCenterStaffId(String id) {
        this.careerCenterStaffId = id;
    }

    /**
     * Prints all internship applications (debug/admin use only).
     * Reads from the internshipApplicationRepository and prints details.
     */
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

    /**
     * Allows a student to accept an internship offer.
     *
     * <p>When a student accepts an offer:</p>
     * <ul>
     *     <li>Offer flag is updated and saved</li>
     *     <li>All other applications by the same student are withdrawn</li>
     *     <li>Internship slot availability is updated and may mark internship as FILLED</li>
     * </ul>
     *
     * @param intApp the internship application being accepted
     */
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

    /**
     * Allows a student to reject an internship offer.
     *
     * <p>This sets the application status to UNSUCCESSFUL.</p>
     *
     * @param internshipAppId ID of the internship application
     */
    public void rejectOffer(String internshipAppId) {
    InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null && intApp.getApplicationStatus() == ApplicationStatus.SUCCESSFUL) {
            intApp.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            internshipApplicationRepository.save(intApp);
        }
    }

    /**
     * Allows a student to submit a new internship application.
     *
     * <p>This method validates:</p>
     * <ul>
     *     <li>Student exists</li>
     *     <li>Internship exists</li>
     *     <li>Student has not already applied</li>
     *     <li>Student is eligible for the internship</li>
     *     <li>Student has not exceeded the maximum number of applications</li>
     * </ul>
     *
     * <p>When valid, creates and saves a new application, updates the internship,
     * and sends a notification to the company representative.</p>
     *
     * @param internshipId ID of the internship being applied to
     * @param studentId ID of the applying student
     * @return true if the application succeeds, false otherwise
     */
    public boolean apply(String internshipId, String studentId) {

        Internship internship = internshipRepository.findById(internshipId);
        User user = userRepository.findById(studentId);

        if (!(user instanceof Student student)) return false;
        if (internship == null) return false;

        // if they already applied, do not accept
        boolean alreadyApplied = internshipApplicationRepository.findAll().stream()
                .anyMatch(a -> a.getInternship().getId().equals(internshipId)
                && a.getStudent().getId().equals(studentId));

        if (alreadyApplied) {
            System.out.println(student.getFullName() + " has already applied for " + internship.getInternshipTitle());
            return false;
        }
        
        if (!canApply(studentId)) {
            System.out.println("You cannot apply for any more internships.");
            return false;
        }

        if (!isEligible(studentId, internshipId)) {
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

        notificationManager.sendNotification(internship.getCompRepIC().getId(), "A student has applied for your internship: " + internship.getInternshipTitle());

        return true;
    }

    /**
     * Approves an internship application (offer creation).
     *
     * <p>Sets the application status to SUCCESSFUL and notifies the student.</p>
     *
     * @param internshipAppId the ID of the application
     * @return true if updated successfully
     */
    public boolean approveInternshipApplication(String internshipAppId) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null) {
            intApp.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
            internshipApplicationRepository.save(intApp);

            notificationManager.sendNotification(intApp.getStudent().getId(),"Your application for \"" + intApp.getInternship().getInternshipTitle() + "\" was approved.");

            return true;
        }
        return false;
    }

    /**
     * Rejects an internship application.
     *
     * <p>Sets the application status to UNSUCCESSFUL and notifies the student.</p>
     *
     * @param internshipAppId the ID of the application
     * @return true if updated successfully
     */
    public boolean rejectInternshipApplication(String internshipAppId) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null) {
            intApp.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            internshipApplicationRepository.save(intApp);

            notificationManager.sendNotification(intApp.getStudent().getId(),"Your application for \"" + intApp.getInternship().getInternshipTitle() + "\" was rejected.");

            return true;
        }
        return false;
    }

    /**
     * Allows a student to request withdrawal from a PENDING internship application.
     *
     * <p>Only PENDING applications may be withdrawn. A PENDING_WITHDRAWAL status is set,
     * and a notification is sent to Career Centre Staff if configured.</p>
     *
     * @param internshipAppId ID of the application being withdrawn
     * @param student the student requesting the withdrawal
     * @return true if withdrawal is requested successfully
     */
    public boolean requestWithdrawal(String internshipAppId, Student student) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp == null) return false;

        if (!intApp.getStudent().getId().equals(student.getId())) return false;

        if (intApp.getApplicationStatus() == ApplicationStatus.PENDING) {
            intApp.setApplicationStatus(ApplicationStatus.PENDING_WITHDRAWAL);
            internshipApplicationRepository.save(intApp);

            if (careerCenterStaffId != null) {
                notificationManager.sendNotification(careerCenterStaffId,"A withdrawal request has been submitted for internship \"" + intApp.getInternship().getInternshipTitle() + "\".");
            }
            return true;
        }
        // System.out.println("Cannot request withdrawal for this application.");
        return false;
    }

    /**
     * Approves a student's withdrawal request.
     *
     * <p>This method:</p>
     * <ul>
     *     <li>Sets status to WITHDRAWN</li>
     *     <li>Removes the application from the internship's internal list</li>
     *     <li>Persists all changes</li>
     * </ul>
     *
     * @param internshipAppId ID of the application
     * @return true if withdrawal approved
     */
    public boolean approveAppWithdrawal(String internshipAppId) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp != null  && intApp.getApplicationStatus() == ApplicationStatus.PENDING_WITHDRAWAL) {
            intApp.setApplicationStatus(ApplicationStatus.WITHDRAWN);
            internshipApplicationRepository.save(intApp);
            intApp.getInternship().getInternshipApplications().removeIf(a -> a.getId().equals(intApp.getId()));
            internshipRepository.save(intApp.getInternship());
            return true;
        // } else {
        //     System.out.println("Application not found.");
        }
        return false;
    }

    /**
     * Rejects a withdrawal request.
     *
     * <p>Reverts status from PENDING_WITHDRAWAL back to PENDING.</p>
     *
     * @param internshipAppId ID of the application
     * @return true if withdrawal rejected successfully
     */
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

    /**
     * Checks whether a student may apply for an internship.
     *
     * <p>A student cannot apply if:</p>
     * <ul>
     *     <li>They have already accepted an internship</li>
     *     <li>They have 3 or more active applications</li>
     * </ul>
     *
     * @param studentId ID of the student
     * @return true if the student may apply
     */
    public boolean canApply(String studentId) {
        User user = userRepository.findById(studentId);
        if (!(user instanceof Student student)) {
            return false; // not a student or doesn't exist
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

    /**
     * Validates whether a student is eligible to apply for a given internship.
     *
     * <p>Checks include:</p>
     * <ul>
     *     <li>Internship exists and is APPROVED</li>
     *     <li>Internship is visible</li>
     *     <li>Application window is open</li>
     *     <li>Student's major matches</li>
     *     <li>Student's year matches level requirements</li>
     *     <li>Internship is not already filled</li>
     * </ul>
     *
     * @param studentId ID of the student applying
     * @param internshipId ID of the internship
     * @return true if the student is eligible
     */
    public boolean isEligible(String studentId, String internshipId) {
        User user = userRepository.findById(studentId);
        if (!(user instanceof Student student)) return false;

        Internship internship = internshipRepository.findById(internshipId);
        if (internship == null) return false;

        // case 0: student don't exist:
        if (student == null || internship == null) {
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

    /**
     * Retrieves all applications with status PENDING_WITHDRAWAL.
     *
     * @return list of withdrawal requests
     */
    public ArrayList<InternshipApplication> getWithdrawalRequests() {

        List<InternshipApplication> result = internshipApplicationRepository.findAll().stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.PENDING_WITHDRAWAL)
                .toList();

        return new ArrayList<>(result);

    }

    /**
     * Retrieves all internship applications for a specific internship.
     *
     * @param internshipId ID of the internship
     * @return list of InternshipApplication for that internship
     */
    public ArrayList<InternshipApplication> companyRepGetInternshipApplications(String internshipId) {

        List<InternshipApplication> result = internshipApplicationRepository.findAll().stream()
                .filter(internshipApplication -> internshipApplication.getInternship().getId().equals(internshipId))
                .toList();

        return new ArrayList<>(result);

    }

    /**
     * Retrieves all internship applications submitted by a specific student.
     *
     * @param userId student ID
     * @return list of internship applications belonging to the student
     */
    public ArrayList<InternshipApplication> studentGetInternshipApplications(String userId) {

        List<InternshipApplication> result = internshipApplicationRepository.findAll().stream()
                .filter(internshipApplication -> internshipApplication.getStudent().getId().equals(userId))
                .toList();

        return new ArrayList<>(result);

    }
}
