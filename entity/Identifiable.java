package entity;

/**
 * A minimal interface representing an entity that possesses a unique identifier.
 *
 * <p>This interface is implemented by all persistent entity classes in the system
 * (such as {@code User}, {@code Internship}, and {@code InternshipApplication})
 * to ensure that each object provides a consistent {@code getId()} method.</p>
 *
 * <p>The {@code Repository<T>} classes rely on this interface to perform save,
 * update, and lookup operations without depending on concrete class types.
 * This supports both the Interface Segregation Principle (ISP) and the
 * Dependency Inversion Principle (DIP).</p>
 *
 * <p>As a functional interface, it contains exactly one abstract method,
 * enabling concise usage in lambda expressions where appropriate.</p>
 */
@FunctionalInterface
public interface Identifiable {
    String getId();
}
