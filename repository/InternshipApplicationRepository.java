package repository;

import entity.InternshipApplication;

public class InternshipApplicationRepository extends Repository<InternshipApplication> {
    public static final String PREFIX = "APP";
    private int nextId;

    public InternshipApplicationRepository() {
        super("../data/internshipApplications.ser");
        nextId = computeMaxNumericId(PREFIX) + 1;
    }

    public String generateNextId() {
        return String.format(PREFIX + "%04d", nextId++);
    }
}