package repository;

import entity.CompanyRep;
import entity.User;

import java.util.ArrayList;

public class UserRepository extends Repository<User> {

    public UserRepository() {
        super("../data/users.ser");
    }

    public ArrayList<CompanyRep> getAllCompanyReps() {
        return new ArrayList<>(
            entities.stream()
                .filter(user -> user instanceof CompanyRep)
                .map(user -> (CompanyRep) user)
                .toList()
        );
    }

}