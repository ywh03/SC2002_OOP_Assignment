package control;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import entity.CompanyRep;
import entity.Internship;
import entity.Student;
import entity.InternshipApplication;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;
import entity.enums.Major;
import manager.NotificationManager;
import repository.InternshipRepository;
import repository.UserRepository;
import util.filter.Filter;
import util.filter.LevelFilter;
import util.filter.MajorFilter;
import util.filter.StatusFilter;

public class InternshipController {
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    public InternshipController(InternshipRepository internshipRepo, UserRepository userRepo){
        this.internshipRepository = internshipRepo;
        this.userRepository = userRepo;
        notificationManager = new NotificationManager();
    }
    public boolean toggleVisibility(CompanyRep companyRep, String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        if (internship == null) return false;

        if (!internship.getCompRepIC().equals(companyRep)) return false;

        internship.setVisibility(!internship.getVisibility());
        internshipRepository.save(internship);

        if (!internship.getVisibility()) {
            for (InternshipApplication app : internship.getInternshipApplications()) {
                notificationManager.sendNotification(app.getStudent().getId(),
                    "The internship \"" + internship.getInternshipTitle() + "\" is no longer visible."
                );
            }
        }

        return true;
    }

    public boolean approveInternship(String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        if (internship == null) return false;

        internship.setInternshipStatus(InternshipStatus.APPROVED);
        internshipRepository.save(internship);
        notificationManager.sendNotification(internship.getCompRepIC().getId(),"Your internship \"" + internship.getInternshipTitle() + "\" was approved.");

        return true;
    }

    public boolean rejectInternship(String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        if (internship == null) return false;

        internship.setInternshipStatus(InternshipStatus.REJECTED);
        internshipRepository.save(internship);
        notificationManager.sendNotification(internship.getCompRepIC().getId(),"Your internship \"" + internship.getInternshipTitle() + "\" was rejected.");

        return true;
    }

    public boolean createInternship(String internshipId, String title, String description, InternshipLevel level, Major preferredMajor, Date appOpenDate, Date appCloseDate, String companyName, CompanyRep compRepIC, int numOfSlots){
        Internship internship = new Internship(internshipId, title, description, level, preferredMajor, appOpenDate, appCloseDate, InternshipStatus.PENDING, companyName, compRepIC, numOfSlots);
        internshipRepository.save(internship);
        return true;
    }

    public boolean editInternship(String internshipId, String title, String description, InternshipLevel level, Major preferredMajor, Date appOpenDate, Date appCloseDate, String companyName, CompanyRep compRepIC, int numOfSlots){
        Internship internship = internshipRepository.findById(internshipId);
        internship.setInternshipTitle(title);
        internship.setDescription(description);
        internship.setLevel(level);
        internship.setPreferredMajor(preferredMajor);
        internship.setAppOpenDate(appOpenDate);
        internship.setAppCloseDate(appCloseDate);
        internship.setCompanyName(companyName);
        internship.setCompRepIC(compRepIC);
        internship.setNumOfSlots(numOfSlots);

        internshipRepository.save(internship);
        return true;
    }

    public ArrayList<Internship> generateReport(ArrayList<Filter> filters){
        HashSet<Internship> filteredInternships = new HashSet<>();
        ArrayList<Internship> internships = this.internshipRepository.findAll();

        if (filters == null || filters.isEmpty()){
            return internships;
        }

        return (ArrayList<Internship>) internships.stream()
                .filter(internship -> filters.stream()
                        .allMatch((filter -> filter.matches(internship)))
                )
                .toList();

    }

    // get Available Internships 
    public ArrayList<Internship> getAvailableInternships(Student student){
        List<Internship> allInternships = this.internshipRepository.findAll();
        Date currentDate = new Date(); // Get the current system date/time for comparison
        int studentYear = student.getYearOfStudy();

        List<Internship> availableInternships = allInternships.stream()
            .filter(internship -> internship.getInternshipStatus() == InternshipStatus.APPROVED) // approved internships
            .filter(Internship::getVisibility) // visible internships
            .filter(internship -> { // internship is still open
                Date openDate = internship.getAppOpenDate();
                Date closeDate = internship.getAppCloseDate();               
                boolean isAfterOpen = !currentDate.before(openDate);                
                boolean isBeforeClose = !currentDate.after(closeDate); 
                return isAfterOpen && isBeforeClose;
            })
            .filter(internship -> internship.getPreferredMajor().equals(student.getMajor())) // preferred major
            .filter(internship -> { // within the same level
                if (studentYear <= 2) {
                    return internship.getLevel() == InternshipLevel.BASIC;
                } else {
                    return true;
                }
            })
            .toList();
        return new ArrayList<>(availableInternships);
    }

    // 

    public ArrayList<Internship> getInternshipListings(String compRepId){
        CompanyRep companyRep = (CompanyRep) this.userRepository.findById(compRepId);
        return companyRep.getInternshipInfo();
    }

    public ArrayList<InternshipApplication> getApplications(String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        return internship.getInternshipApplications();
    }

    public ArrayList<Internship> getPendingInternships() {
        List<Internship> internships = internshipRepository.findAll();    
        List<Internship> pendingInternships = internships.stream()        
            .filter(internship -> internship.getInternshipStatus() == InternshipStatus.PENDING)            
            .toList();    
        return new ArrayList<>(pendingInternships);
    }


    public ArrayList<Internship> getPendingInternships(String compRepId){
        CompanyRep companyRep = (CompanyRep) this.userRepository.findById(compRepId);

        List<Internship> pendingInternships = companyRep.getInternshipInfo().stream()
                .filter(internship -> internship.getInternshipStatus() == InternshipStatus.PENDING)
                .toList();

        return new ArrayList<>(pendingInternships);
    }
}