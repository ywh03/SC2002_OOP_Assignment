package util.filter;

import entity.Internship;

/**
 * A functional interface for defining a filter condition on an {@link Internship} object.
 * This interface establishes the contract for all specific filter strategies
 * (e.g., filtering by Major, Level, or Status) used within the application.
 *
 * It is marked {@code @FunctionalInterface} as it defines a single abstract method.
 */
@FunctionalInterface
public interface Filter {
    /**
     * Checks if the given {@link Internship} object matches the filter's criteria.
     * <p>
     * Implementations of this method define the specific logic for matching,
     * such as checking if a major matches or if a status is approved.
     *
     * @param internship The Internship object to check against the filter criteria.
     * @return {@code true} if the Internship matches the filter condition, {@code false} otherwise.
     */
    boolean matches(Internship internship);
}
