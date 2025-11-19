package control;

import java.util.ArrayList;
import java.util.stream.Collectors;

import repository.UserRepository;
import entity.CompanyRep;
import entity.User;

public class CompanyRepController {
    private UserRepository userRepository;

    public CompanyRepController(UserRepository userRepo){
        this.userRepository = userRepo;
    }

    public void authoriseCompanyRep(String companyRepId){
        User user = userRepository.findById(companyRepId);
        if (user instanceof CompanyRep companyRep) {
            companyRep.setApproved(true);
            userRepository.save(companyRep);
        }
    }

    public void rejectCompanyRep(String companyRepId){
        User user = userRepository.findById(companyRepId);
        if (user instanceof CompanyRep companyRep) {
            companyRep.setApproved(false);
            userRepository.save(companyRep);
        }
    }

    public ArrayList<CompanyRep> approvedCompanyReps(){
        return userRepository.findAll().stream()
            .filter(user -> user instanceof CompanyRep compRep && compRep.getApproved())
            .map(user -> (CompanyRep) user) //cast to comprep
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<CompanyRep> getPendingRegistrations(){
        return userRepository.findAll().stream()
            .filter(user -> user instanceof CompanyRep compRep && !compRep.getApproved()) 
            .map(user -> (CompanyRep) user) //cast to comprep
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
