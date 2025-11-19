package repository;

import entity.Internship;

public class InternshipRepository extends Repository<Internship> {
    public static final String PREFIX = "INT";

    private int nextId;

    public InternshipRepository() {
        super("data/internships.ser");
        nextId = computeMaxNumericId(PREFIX) + 1;
    }

    public String generateNextId() {
        return String.format(PREFIX + "%04d", nextId++);
    }
}