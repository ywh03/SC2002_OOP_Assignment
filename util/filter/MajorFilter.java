package util.filter;

import entity.Internship;
import entity.enums.InternshipLevel;
import entity.enums.Major;

/**
 * Implements the {@link Filter} interface to check if an {@link Internship}'s
 * required Major matches a specific {@link InternshipMajor}.
 */
public class MajorFilter implements Filter {

    private final Major major;

    
    /**
     * Constructs a MajorFilter with the required internship major.
     *
     * @param internshipMajor The Internship major that the Internship must match.
     */
    public MajorFilter(Major major) {
        this.major = major;
    }


    /**
     * Checks if the given internship's major is equal to the major specified
     * in this filter.
     *
     * @param internship The Internship object to check.
     * @return true if the internship's major matches, false otherwise.
     */
    @Override
    public boolean matches(Internship internship) {
        return internship.getPreferredMajor().equals(major);
    }
}
