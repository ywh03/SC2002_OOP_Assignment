package util.filter;

import entity.Internship;
import entity.enums.InternshipStatus;

/**
 * Implements the {@link Filter} interface to check if an {@link Internship}'s
 * current status matches a specific {@link InternshipStatus}.
 */
public class StatusFilter implements  Filter{

    private final InternshipStatus internshipStatus;

    /**
     * Constructs a StatusFilter with the required internship status.
     *
     * @param internshipStatus The status that the Internship must match.
     */
    public StatusFilter(InternshipStatus internshipStatus) {
        this.internshipStatus = internshipStatus;
    }


    /**
     * Checks if the given internship's status is equal to the status specified
     * in this filter.
     *
     * @param internship The Internship object to check.
     * @return true if the internship's status matches, false otherwise.
     */
    @Override
    public boolean matches(Internship internship) {
        return internship.getInternshipStatus().equals(internshipStatus);
    }
}
