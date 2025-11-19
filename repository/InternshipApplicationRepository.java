package repository;

import entity.InternshipApplication;

/**
 * A specialized repository for managing {@link InternshipApplication} entities.
 * It handles the specific file path and provides functionality for generating
 * sequential, unique IDs for new applications.
 */
public class InternshipApplicationRepository extends Repository<InternshipApplication> {
    public static final String PREFIX = "APP";
    private int nextId;

    /**
     * Constructs the InternshipApplicationRepository.
     * It sets the file path and initializes the {@code nextId} by computing the
     * maximum existing numeric ID and incrementing it.
     */
    public InternshipApplicationRepository() {
        super("./SC2002_OOP_Assignment/data/internshipApplications.ser");
        nextId = computeMaxNumericId(PREFIX) + 1;
    }

    /**
     * Generates a new, unique, sequential ID for a new Internship Application.
     * The ID is formatted as "APP" followed by a 4-digit zero-padded number (e.g., "APP0001").
     *
     * @return The newly generated unique ID string.
     */
    public String generateNextId() {
        return String.format(PREFIX + "%04d", nextId++);
    }
}