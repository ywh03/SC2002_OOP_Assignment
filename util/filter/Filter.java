package util.filter;

import entity.Internship;

@FunctionalInterface
public interface Filter {
    boolean matches(Internship internship);
}
