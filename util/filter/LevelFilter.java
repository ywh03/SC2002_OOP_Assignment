package util.filter;

import entity.Internship;
import entity.enums.InternshipLevel;


/**
 * Implements the {@link Filter} interface to check if an {@link Internship}'s
 * required level matches a specific {@link InternshipLevel}.
 */
public class LevelFilter implements Filter {

    private final InternshipLevel internshipLevel;

    /**
     * Constructs a LevelFilter with the required internship level.
     *
     * @param internshipLevel The InternshipLevel that the Internship must match.
     */
    public LevelFilter(InternshipLevel internshipLevel) {
        this.internshipLevel = internshipLevel;
    }

    /**
     * Checks if the given internship's level is equal to the level specified
     * in this filter.
     *
     * @param internship The Internship object to check.
     * @return true if the internship's level matches, false otherwise.
     */
    @Override
    public boolean matches(Internship internship) {
        return internship.getLevel().equals(internshipLevel);
    }
}
