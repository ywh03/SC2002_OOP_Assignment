package control;

import entity.InternshipApplication;
import entity.CareerCentreStaff;
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

public class InternshipApplicationController {

    private InternshipApplicationRepository internshipApplicationRepository;
    private InternshipRepository internshipRepository;
    private UserRepository userRepository;
    private NotificationManager notificationManager;

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

    // student accepts the offer
    public boolean acceptOffer(InternshipApplication intApp) { 
        if (intApp.getApplicationStatus() == ApplicationStatus.SUCCESSFUL && !intApp.getOfferAccepted()) {
            intApp.setOfferAccepted(true);
            internshipApplicationRepository.save(intApp); //check if update = save

            // after student accepts one internship offer, they will withdraw all their other applications
            Student student = intApp.getStudent();
            List<InternshipApplication> allApps = internshipApplicationRepository.findAll()
            .stream()
            .filter(a -> a.getStudent().getId().equals(student.getId()))
            .toList();

            for (InternshipApplication other : allApps) {
                if (!other.getId().equals(intApp.getId())) {

                    other.setApplicationStatus(ApplicationStatus.WITHDRAWN);
                    other.setOfferAccepted(false); // Just to be safe

                    internshipApplicationRepository.save(other);

                    // Update application list inside the internship
                    Internship otherInternship = other.getInternship();
                    // No removal — just update status inside the same object
                    internshipRepository.save(otherInternship);
                }
            }

            // check if internshipstatus goes to filled:
            Internship internship = intApp.getInternship();
            Long acceptedOffersCount = internship.getInternshipApplications().stream().filter(a -> a.getOfferAccepted()).count();
            if (acceptedOffersCount >= internship.getNumOfSlots()) {
                internship.setInternshipStatus(InternshipStatus.FILLED);
            }
            internshipRepository.save(internship);
            return true;
        }
        return false;
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

            return false;
        }
        
        if (!canApply(studentId)) {
            return false;
        }

        if (!isEligible(studentId, internshipId)) {
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

    // means application status is successful -> give offer
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

    // company rejects application -> unsuccessful
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

    // student requests withdrawal
    public boolean requestWithdrawal(String internshipAppId, Student student) {
        InternshipApplication intApp = internshipApplicationRepository.findById(internshipAppId);
        if (intApp == null) return false;

        if (!intApp.getStudent().getId().equals(student.getId())) return false;

        if (intApp.getApplicationStatus() == ApplicationStatus.PENDING || intApp.getApplicationStatus() == ApplicationStatus.SUCCESSFUL) {
            intApp.setApplicationStatus(ApplicationStatus.PENDING_WITHDRAWAL);
            internshipApplicationRepository.save(intApp);

            List<String> careerStaffIds = userRepository.findAll().stream()
                                                    .filter(user -> user instanceof CareerCentreStaff) // or check role
                                                    .map(User::getId)
                                                    .toList();
            for (String staffId : careerStaffIds) {
                notificationManager.sendNotification(staffId, "A withdrawal request has been submitted for internship \"" + intApp.getInternship().getInternshipTitle() + "\".");
            }

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
            intApp.getInternship().getInternshipApplications().removeIf(a -> a.getId().equals(intApp.getId()));
            internshipRepository.save(intApp.getInternship());

            notificationManager.sendNotification(intApp.getInternship().getCompRepIC().getId(), "Student (" + intApp.getStudent().getId() + ") has withdrawn for your internship: " + intApp.getInternship().getInternshipTitle());

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

    public boolean canApply(String studentId) {
        User user = userRepository.findById(studentId);
        if (!(user instanceof Student student)) {
            return false; // not a student or doesn't exist
        }

        // case 1: student has already accepted an offer
        boolean hasAcceptedOffer = internshipApplicationRepository.findAll().stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .anyMatch(InternshipApplication::getOfferAccepted);
        if (hasAcceptedOffer) {
            return false;
        }
        // case 2: student's current applications = 3 already (not incl of unsuccessful ones)
        long currentIntAppCount = internshipApplicationRepository.findAll().stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .filter(a -> switch (a.getApplicationStatus()) {
                    case PENDING, PENDING_WITHDRAWAL, SUCCESSFUL -> !a.getOfferAccepted();
                    default -> false; // UNSUCCESSFUL and WITHDRAWN are not active
                })
                .count();
        return currentIntAppCount < 3; // allow up to 3 active applications
    }

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
