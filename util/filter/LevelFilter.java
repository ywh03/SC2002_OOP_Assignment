package util.filter;

import entity.Internship;
import entity.enums.InternshipLevel;

public class LevelFilter implements Filter {

    private final InternshipLevel internshipLevel;

    public LevelFilter(InternshipLevel internshipLevel) {
        this.internshipLevel = internshipLevel;
    }

    @Override
    public boolean matches(Internship internship) {
        return internship.getLevel().equals(internshipLevel);
    }
}
