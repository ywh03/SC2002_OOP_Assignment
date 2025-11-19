package util.filter;

import entity.Internship;
import entity.enums.Major;

public class MajorFilter implements Filter {

    private final Major major;

    public MajorFilter(Major major) {
        this.major = major;
    }

    @Override
    public boolean matches(Internship internship) {
        return internship.getPreferredMajor().equals(major);
    }
}
