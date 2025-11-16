package repository;

import entity.InternshipApplication;

public class InternshipApplicationRepository extends Repository<InternshipApplication> {

    public InternshipApplicationRepository() {
        super("../data/internshipApplications.ser");
    }

    @Override
    protected String getId(InternshipApplication internshipApplication) {
        return internshipApplication.getId();
    }

    public InternshipApplication findById(int id) {
        return entities.stream()
                .filter(internshipApplication -> internshipApplication.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}