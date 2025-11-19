package control;

import entity.CompanyRep;
import repository.UserRepository;

import java.util.ArrayList;

public class CompanyRepController {
    private final UserRepository userRepository;

    public CompanyRepController(UserRepository userRepo){
        this.userRepository = userRepo;
    }

    public void authoriseCompanyRep(String companyRepId){
        CompanyRep companyRep = (CompanyRep) userRepository.findById(companyRepId);
        companyRep.setApproved(true);
    }

    public void rejectCompanyRep(String companyRepId){
        CompanyRep companyRep = (CompanyRep) userRepository.findById(companyRepId);
        companyRep.setApproved(false);
    }

    public ArrayList<CompanyRep> approvedCompanyReps(){
        ArrayList<CompanyRep> companyReps = this.userRepository.getAllCompanyReps();
        return new ArrayList<>(companyReps.stream().filter(CompanyRep::getApproved).toList());
    }

    public ArrayList<CompanyRep> getPendingRegistrations(){
        ArrayList<CompanyRep> companyReps = this.userRepository.getAllCompanyReps();
        return new ArrayList<>(companyReps.stream().filter(c -> !c.getApproved()).toList());
    }
}
