package repository;

import entity.User;

public class UserRepository extends Repository<User> {

    public UserRepository() {
        super("../data/users.ser");
    }

    @Override
    protected String getId(User user) {
        return user.getUserId();
    }

    public User findById(String id) {
        return entities.stream()
                .filter(user -> user.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

}