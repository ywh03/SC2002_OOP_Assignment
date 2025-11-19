package util.filter;

import entity.Internship;
import entity.enums.InternshipStatus;

public class StatusFilter implements  Filter{

    private final InternshipStatus internshipStatus;

    public StatusFilter(InternshipStatus internshipStatus) {
        this.internshipStatus = internshipStatus;
    }

    @Override
    public boolean matches(Internship internship) {
        return internship.getInternshipStatus().equals(internshipStatus);
    }
}
