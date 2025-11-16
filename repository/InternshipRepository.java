package repository;

import entity.Internship;

public class InternshipRepository extends Repository<Internship> {

    public InternshipRepository() {
        super("../data/internships.ser");
    }

    @Override
    protected String getId(Internship internship) {
        return internship.getId();
    }

    public Internship findById(int id) {
        return entities.stream()
                .filter(internship -> internship.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}