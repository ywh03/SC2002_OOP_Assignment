package entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * An abstract base class representing a generic user in the system.
 * <p>
 * This class provides common user properties like ID, full name, and password,
 * and implements {@link Serializable}, {@link Identifiable}, and {@link Notifiable}.
 * Specific user types (e.g., Student, CompanyRep) should extend this class.
 * </p>
 */
public abstract class User implements Serializable, Identifiable, Notifiable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String fullName;
    private String password;


    /**
     * Constructs a new User object.
     *
     * @param userId The unique ID of the user.
     * @param fullName The full name of the user.
     * @param password The password for the user account.
     */
    public User(String userId, String fullName, String password) {
        this.userId = userId;
        this.fullName = fullName;
        this.password = password;
    }

    /**
     * Gets the unique ID of the user.
     *
     * @return The user ID string.
     */
    @Override
    public String getId() {return userId;}

    /**
     * Sets the unique ID of the user.
     *
     * @param userId The new user ID string.
     */
    public void setUserID(String userId) {this.userId = userId;}
    
    /**
     * Gets the full name of the user.
     *
     * @return The full name string.
     */
    public String getFullName() {return fullName;}
    
    /**
     * Sets the full name of the user.
     *
     * @param fullName The new full name string.
     */
    public void setFullName(String fullName) {this.fullName = fullName;}
    
    /**
     * Gets the password of the user.
     *
     * @return The password string.
     */
    public String getPassword() {return password;}
    
    /**
     * Sets the password of the user.
     *
     * @param password The new password string.
     */
    public void setPassword(String password) {this.password = password;}
    
}