package control;

import java.util.ArrayList;
import java.util.Date;

import entity.CompanyRep;
import entity.Internship;
import entity.enums.InternshipLevel;
import entity.enums.InternshipStatus;

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

    public boolean createInternship(String internshipId, String title, String description, InternshipLevel level, String preferredMajor, Date appOpenDate, Date appCloseDate, String companyName, CompanyRep compRepIC, int numOfSlots){
        Internship(internshipId, title, description, level, preferredMajor, appOpenDate, appCloseDate, internshipStatus, companyName, compRepIC, numOfSlots);
        return true;
    }

    public void editInternship(String internshipId, String title, String description, InternshipLevel level, String preferredMajor, Date appOpenDate, Date appCloseDate, String companyName, CompanyRep compRepIC, int numOfSlots){
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

    public ArrayList<Internship> generateReport(ArrayList<String> filter){

    }

    public ArrayList<Internship> getAvailableInternships(String filter){

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
