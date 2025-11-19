package control;

import java.util.ArrayList;

public class CompanyRepController {
    private UserRepository userRepository;

    public CompanyRepController(UserRepository userRepo){
        this.userRepository = userRepo;
    }
    
    public void authoriseCompanyRep(CompanyRep companyRep){
        companyRep.setApproved(true);
    }

    public void rejectCompanyRep(CompanyRep companyRep){
        companyRep.setApproved(false);
    }

    public ArrayList<CompanyRep> approvedCompanyReps(){
        ArrayList<CompanyRep> companyReps = this.userRepository.findAll();
        ArrayList<CompanyRep> approvedCompanyReps = new ArrayList<>(companyReps.stream()
                                                                                .filter(CompanyRep::getApproved).toList());
        return approvedCompanyReps;
    }

    public ArrayList<CompanyRep> getPendingRegistrations(){
        ArrayList<CompanyRep> companyReps = this.userRepository.findAll();
        ArrayList<CompanyRep> pendingRegistrations = new ArrayList<>(companyReps.stream().filter(c -> !c.getApproved()).toList());
        return pendingRegistrations;
    }
}
