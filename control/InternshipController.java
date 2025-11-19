package control;

import java.util.ArrayList;
<<<<<<< HEAD
// import java.util.Date;
=======
import java.util.Date;
import java.util.HashSet;
>>>>>>> 5ecdb3a8c3970eb292913008a00b7798fbcbf876

import entity.CompanyRep;
import entity.Internship;
import entity.InternshipApplication;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;
import entity.enums.Major;

public class InternshipController {
    private InternshipRepository internshipRepository;
    private UserRepository userRepository;

    public InternshipController(InternshipRepository internshipRepo, UserRepositroy userRepo){
        this.internshipRepository = internshipRepo;
        this.userRepository = userRepo;
    }
    public boolean toggleVisibility(Internship internship){
        internship.setVisibility(!internship.getVisibility());
    }

    public void approveInternship(Internship internship){
        internship.setInternshipStatus(InternshipStatus.APPROVED);
    }

    public void rejectInternship(Internship internship){
        internship.setInternshipStatus(InternshipStatus.REJECTED);
    }

    public boolean createInternship(String internshipId, String title, String description, InternshipLevel level, Major preferredMajor, String appOpenDate, String appCloseDate, String companyName, String compRepIC, int numOfSlots){
        Internship(internshipId, title, description, level, preferredMajor, appOpenDate, appCloseDate, InternshipStatus.PENDING, companyName, compRepIC, numOfSlots);
        return true;
    }

    public void editInternship(String internshipId, String title, String description, InternshipLevel level, Major preferredMajor, String appOpenDate, String appCloseDate, String companyName, String compRepIC, int numOfSlots){
        internship = internshipRepository.findById(internshipId);
        internship.setInternshipTitle(title);
        internship.setDescription(description);
        internship.setLevel(level);
        internship.setPreferredMajor(preferredMajor);
        internship.setOpenDate(appOpenDate);
        internship.setCloseDate(appCloseDate);
        internship.setCompanyName(companyName);
        internship.setCompRepIC(compRepIC);
        internship.setNumOfSlots(numOfSlots);
    }

    public HashSet<Internship> generateReport(ArrayList<ArrayList<String>> filters){
        HashSet<Internship> filteredInternships = new HashSet<>();
        ArrayList<Internship> internships = this.internshipRepository.findAll();

        for (Internship internship : internships) {
            boolean matchesMajor = filters.get(0).isEmpty() ||
                    filters.get(0).stream().anyMatch(f -> new MajorFilter(f).matches(internship));

            boolean matchesLevel = filters.get(1).isEmpty() ||
                    filters.get(1).stream().anyMatch(f -> new LevelFilter(f).matches(internship));

            boolean matchesStatus = filters.get(2).isEmpty() ||
                    filters.get(2).stream().anyMatch(f -> new StatusFilter(f).matches(internship));

            if (matchesMajor && matchesLevel && matchesStatus) {
                filteredInternships.add(internship);
            }
        }
        return filteredInternships;
    }

    public ArrayList<Internship> getInternshipListings(String compRepId){
        CompanyRep companyRep = this.userRepository.findById(compRepId);
        return companyRep.getInternshipInfo();
    }

    public ArrayList<InternshipApplication> getApplications(String internshipId){
        Internship internship = this.internshipRepository.findById(internshipId);
        return internship.getInternshipApplications();
    }

    public ArrayList<Internship> getPendingInternships(String compRepId){
        CompanyRep companyRep = this.userRepository.findById(compRepId);
        ArrayList<Internship> pendingInternships = new ArrayList<>(companyRep.getInternshipInfo().stream().filter(i -> i.getInternshipStatus == PENDING));
        return pendingInternships;
    }
}
