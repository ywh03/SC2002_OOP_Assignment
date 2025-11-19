package control;

import java.util.ArrayList;
import java.util.Date;
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

/**
 * Controller responsible for all business logic related to internship postings.
 *
 * <p>This includes:</p>
 * <ul>
 *     <li>Creating, editing and managing internship postings</li>
 *     <li>Approving and rejecting postings (Career Centre Staff)</li>
 *     <li>Toggling listing visibility (Company Reps)</li>
 *     <li>Generating filtered internship reports</li>
 *     <li>Fetching internship listings for students or company reps</li>
 * </ul>
 */
public class InternshipController {
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    /**
     * Constructs the InternshipController with required repositories injected.
     *
     * @param internshipRepo repository for storing internship objects
     * @param userRepo repository for retrieving company representatives
     */
    public InternshipController(InternshipRepository internshipRepo, UserRepository userRepo){
        this.internshipRepository = internshipRepo;
        this.userRepository = userRepo;
        notificationManager = NotificationManager.getInstance();
    }

    /**
     * Toggles the visibility of an internship posting.
     *
     * <p>Only the company representative who owns the posting may toggle it.
     * When visibility is turned OFF, all students who applied are notified.</p>
     *
     * @param companyRep the company representative performing the action
     * @param internshipId ID of the internship to toggle
     * @return true if the visibility was toggled successfully
     */
    public boolean toggleVisibility(CompanyRep companyRep, String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        if (internship == null) return false;

        if (!internship.getCompRepIC().getId().equals(companyRep.getId())) return false;

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

    /**
     * Approves an internship posting (performed by Career Centre Staff).
     *
     * <p>Sets status to APPROVED and notifies the posting's company representative.</p>
     *
     * @param internshipId ID of the internship
     * @return true if approval succeeds
     */
    public boolean approveInternship(String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        if (internship == null) return false;

        internship.setInternshipStatus(InternshipStatus.APPROVED);
        internshipRepository.save(internship);
        notificationManager.sendNotification(internship.getCompRepIC().getId(),"Your internship \"" + internship.getInternshipTitle() + "\" was approved.");

        return true;
    }

    /**
     * Rejects an internship posting.
     *
     * <p>Sets status to REJECTED and sends a notification to the company rep.</p>
     *
     * @param internshipId ID of the internship to reject
     * @return true if rejection succeeds
     */
    public boolean rejectInternship(String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        if (internship == null) return false;

        internship.setInternshipStatus(InternshipStatus.REJECTED);
        internshipRepository.save(internship);
        notificationManager.sendNotification(internship.getCompRepIC().getId(),"Your internship \"" + internship.getInternshipTitle() + "\" was rejected.");

        return true;
    }

    /**
     * Creates a new internship posting.
     *
     * <p>Company representatives may only create up to 5 postings.
     * The method generates a new internship ID and persists the posting.</p>
     *
     * @param title internship title
     * @param description description of the internship
     * @param level required internship level
     * @param preferredMajor required major
     * @param appOpenDate application open date
     * @param appCloseDate application close date
     * @param companyName name of the company
     * @param compRepIC the company representative in charge
     * @param numOfSlots number of available slots
     * @return true if created successfully, false if posting limits exceeded
     */
    public boolean createInternship(String title, String description, InternshipLevel level, Major preferredMajor, Date appOpenDate, Date appCloseDate, String companyName, CompanyRep compRepIC, int numOfSlots){
        long existingCount = internshipRepository.findAll().stream().filter(i -> i.getCompRepIC().getId().equals(compRepIC.getId())).count();

        if (existingCount >= 5) {
            return false; // cannot create more than 5 internships
        }
        
        String internshipId = internshipRepository.generateNextId();
        Internship internship = new Internship(internshipId, title, description, level, preferredMajor, appOpenDate, appCloseDate, InternshipStatus.PENDING, companyName, compRepIC, numOfSlots);
        internshipRepository.save(internship);
        return true;
    }

    /**
     * Edits an existing internship posting.
     *
     * <p>Only PENDING internships may be modified. Updates all editable fields and persists the changes.</p>
     *
     * @param internshipId ID of the internship being edited
     * @return true if updated successfully
     */
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

    /**
     * Generates a filtered internship report.
     *
     * <p>Uses the Filter interface to ensure Open-Closed Principle:
     * adding new filter types requires no change to this method.</p>
     *
     * @param filters list of filter conditions
     * @return list of internships matching all filters
     */
    public ArrayList<Internship> generateReport(ArrayList<Filter> filters){
        ArrayList<Internship> internships = this.internshipRepository.findAll();

        if (filters == null || filters.isEmpty()){
            return internships;
        }

        return (ArrayList<Internship>) internships.stream()
                .filter(internship -> filters.stream().allMatch(f -> f.matches(internship)))
                .collect(Collectors.toCollection(ArrayList::new));

    }

    /**
     * Retrieves all internships available to a given student.
     *
     * <p>Filters based on:</p>
     * <ul>
     *     <li>Internship is APPROVED</li>
     *     <li>Internship is visible</li>
     *     <li>Application window is open</li>
     *     <li>Student major matches preferred major</li>
     *     <li>Student year matches level requirement</li>
     * </ul>
     *
     * @param student student requesting the listings
     * @return list of internships the student may apply for
     */
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

    /**
     * Retrieves all internship postings created by a specific company representative.
     *
     * @param compRepId company representative ID
     * @return list of internships created by the representative
     */
    public ArrayList<Internship> getInternshipListings(String compRepId) {
        return internshipRepository.findAll().stream()
                .filter(internship -> internship.getCompRepIC().getId().equals(compRepId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves a single internship by its ID.
     *
     * @param id internship ID
     * @return Internship object, or null if not found
     */
    public Internship getInternshipById(String id) {
        return internshipRepository.findById(id);
    }

    /**
     * Retrieves a single internship by its ID.
     *
     * @param internshipId internship ID
     * @return Internship object, or null if not found
     */
    public ArrayList<InternshipApplication> getApplications(String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        if (internship == null) return null;

        return internship.getInternshipApplications();
    }

    /**
     * Retrieves all internship postings whose status is PENDING. Method is for Career Center Staff.
     *
     * @return list of PENDING internship postings
     */
    public ArrayList<Internship> getPendingInternships() {
        List<Internship> internships = internshipRepository.findAll();    
        List<Internship> pendingInternships = internships.stream()        
            .filter(internship -> internship.getInternshipStatus() == InternshipStatus.PENDING)            
            .toList();    
        return new ArrayList<>(pendingInternships);
    }

    /**
     * Retrieves all internship postings whose status is PENDING. Method is for Company Reps.
     *
     * @return list of PENDING internship postings
     */
    public ArrayList<Internship> getPendingInternships(String compRepId) {
        return internshipRepository.findAll().stream()
                .filter(i -> i.getCompRepIC().getId().equals(compRepId))
                .filter(i -> i.getInternshipStatus() == InternshipStatus.PENDING)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
