package repository;

import entity.User;

public class UserRepository extends Repository<User> {

    public UserRepository() {
        super("../data/users.ser");
    }

}