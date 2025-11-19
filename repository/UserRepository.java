package repository;

import entity.CompanyRep;
import entity.User;

import java.util.ArrayList;


/**
 * A specialized repository for managing {@link User} entities.
 * It extends the generic {@link Repository} and handles the specific file
 * path for user data, as well as providing a utility method for
 * retrieving only {@link CompanyRep} instances.
 */
public class UserRepository extends Repository<User> {

    /**
     * Constructs the UserRepository, specifying the file path for user data.
     * Entities are loaded upon instantiation.
     */
    public UserRepository() {
        super("data/users.ser");
    }


    
    /**
     * Retrieves all entities that are instances of {@link CompanyRep}.
     * This is necessary because the repository stores the base {@link User} type,
     * and this method filters the collection to return only the subclass.
     *
     * @return An {@code ArrayList} of {@link CompanyRep} objects.
     */
    public ArrayList<CompanyRep> getAllCompanyReps() {
        return new ArrayList<>(
            entities.stream()
                .filter(user -> user instanceof CompanyRep)
                .map(user -> (CompanyRep) user)
                .toList()
        );
    }

}