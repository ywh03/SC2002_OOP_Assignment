package repository;

import entity.Internship;

/**
 * A specialized repository for managing {@link Internship} entities.
 * It handles the specific file path and provides functionality for generating
 * sequential, unique IDs for new internships.
 */
public class InternshipRepository extends Repository<Internship> {
    /** The prefix used for all Internship IDs. */
    public static final String PREFIX = "INT";

    private int nextId;


    /**
     * Constructs the InternshipRepository.
     * It sets the file path and initializes the {@code nextId} by computing the
     * maximum existing numeric ID and incrementing it.
     */
    public InternshipRepository() {
        super("./SC2002_OOP_Assignment/data/internships.ser");
        nextId = computeMaxNumericId(PREFIX) + 1;
    }

    
    /**
     * Generates a new, unique, sequential ID for a new Internship.
     * The ID is formatted as "INT" followed by a 4-digit zero-padded number (e.g., "INT0001").
     *
     * @return The newly generated unique ID string.
     */
    public String generateNextId() {
        return String.format(PREFIX + "%04d", nextId++);
    }
}