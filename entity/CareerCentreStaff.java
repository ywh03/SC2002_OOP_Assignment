package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Career Centre Staff member within the Internship Management System.
 *
 * <p>Career Centre Staff are responsible for reviewing and approving company
 * representative registrations, internship postings, and withdrawal requests.
 * They may also receive system notifications related to these actions.</p>
 *
 * <p>This class extends {@link User}, inheriting common user attributes such as
 * user ID, full name, and password. It also maintains its own list of
 * {@link Notification} objects, which are delivered through the system's
 * {@code NotificationManager}.</p>
 */
public class CareerCentreStaff extends User {
    private String staffDepartment;
    private List<Notification> notifications = new ArrayList<>();

    /**
     * Constructs a new CareerCentreStaff user with the specified details.
     *
     * @param userId          unique identifier for the staff member
     * @param fullName        full name of the staff member
     * @param password        login password for the account
     * @param staffDepartment department the staff member is associated with
     */
    public CareerCentreStaff(String userId, String fullName, String password, String staffDepartment){
        super(userId, fullName, password);
        this.staffDepartment = staffDepartment;
    }

    /**
     * Receives a notification and stores it in the staff member's notification list.
     *
     * @param notification the notification to be added
     */
    @Override
    public void receiveNotification(Notification notification) {
        notifications.add(notification);
        // optionally print to console/log
    }

    /**
     * Returns the list of notifications received by this staff member.
     *
     * @return list of notifications
     */
    @Override
    public List<Notification> getNotifications() {
        return notifications;
    }

    /**
     * Returns the department this staff member belongs to.
     *
     * @return the staff member's department
     */
    public String getStaffDepartment(){return this.staffDepartment;}

    /**
     * Updates the staff member's department.
     *
     * @param staffDepartment new department name
     */
    public void setStaffDepartment(String staffDepartment){this.staffDepartment = staffDepartment;}


}