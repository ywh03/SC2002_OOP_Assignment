package control;

import entity.CompanyRep;
import repository.UserRepository;

import java.util.ArrayList;

import manager.NotificationManager;

public class CompanyRepController {
    private final UserRepository userRepository;

    public CompanyRepController(UserRepository userRepo){
        this.userRepository = userRepo;
    }

    public boolean authoriseCompanyRep(String companyRepId){
        CompanyRep companyRep = (CompanyRep) userRepository.findById(companyRepId);
        if (companyRep == null) return false;

        companyRep.setApproved(true);
        userRepository.save(companyRep);
        NotificationManager.getInstance().sendNotification(companyRepId,"Your company representative registration has been approved.");

        return true;
    }

    public boolean rejectCompanyRep(String companyRepId){
        CompanyRep companyRep = (CompanyRep) userRepository.findById(companyRepId);
        if (companyRep == null) return false;
        userRepository.save(companyRep);
        companyRep.setApproved(false);
        return true;
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
