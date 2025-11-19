package entity;

import java.util.ArrayList;
import java.util.List;

import entity.Notification;  

import entity.enums.Major;

/**
 * Represents a Student user in the system, extending the base {@link User} class.
 * <p>
 * A Student has attributes specific to their academic status, such as year of study
 * and major, and tracks their internship applications and notifications.
 * </p>
 */
public class Student extends User {

    private int yearOfStudy;
    private Major major;
    private ArrayList<InternshipApplication> appliedInternships = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();

    /**
     * Constructs a new Student object.
     *
     * @param userId The unique ID of the student.
     * @param fullName The full name of the student.
     * @param password The password for the student account.
     * @param yearOfStudy The current year of study (e.g., 1, 2, 3, 4).
     * @param major The {@link Major} of the student.
     */
    public Student(String userId, String fullName, String password, int yearOfStudy, Major major){
        super(userId, fullName, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    /**
     * Gets the student's current year of study.
     *
     * @return The year of study.
     */    
    public int getYearOfStudy(){return this.yearOfStudy;}
    
    /**
     * Sets the student's current year of study.
     *
     * @param yearOfStudy The new year of study.
     */
    public void setYearOfStudy(int yearOfStudy){this.yearOfStudy = yearOfStudy;}
    
    /**
     * Gets the student's academic major.
     *
     * @return The student's {@link Major}.
     */
    public Major getMajor(){return this.major;}
    

    /**
     * Sets the student's academic major.
     *
     * @param major The new {@link Major}.
     */
    public void setMajor(Major major){this.major = major;}
    
    /**
     * Gets the list of internship applications submitted by the student.
     *
     * @return An {@code ArrayList} of {@link InternshipApplication} objects.
     */
    public ArrayList<InternshipApplication> getAppliedInternships(){return this.appliedInternships;}


    /**
     * Implements the {@link Notifiable} interface method to receive a notification.
     * The notification is added to the student's local list.
     *
     * @param notification The {@link Notification} to be received.
     */
    @Override
    public void receiveNotification(Notification notification) {
        notifications.add(notification);
        // optionally print to console/log
    }

    /**
     * Implements the {@link Notifiable} interface method to retrieve all notifications.
     *
     * @return A {@code List} of {@link Notification} objects.
     */
    @Override
    public List<Notification> getNotifications() {
        return notifications;
    }
}