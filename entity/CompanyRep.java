package entity;

import entity.enums.RegistrationStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Company Representative (CompanyRep) user in the Internship Management System.
 *
 * <p>A CompanyRep is responsible for creating, editing, and managing internship postings,
 * as well as reviewing student applications. Their account must be approved by Career
 * Centre Staff before they may access the system's features.</p>
 *
 * <p>This class extends {@link User}, inheriting core user fields such as ID, name,
 * and password. Additional attributes are included to store company-specific
 * information and approval/registration state.</p>
 */
public class CompanyRep extends User {
    private RegistrationStatus registrationStatus;    private int numOfInternships = 0;
    private String companyName;
    private String department;
    private String position;
    private boolean registered = false;
    private List<Notification> notifications = new ArrayList<>();

    /**
     * Constructs a new CompanyRep object with the specified personal and company details.
     *
     * @param userId      the unique ID for this representative
     * @param fullName    the representative's full name
     * @param password    login password
     * @param companyName the company this representative belongs to
     * @param department  the department within the company
     * @param position    the representative's job role/position
     */
    public CompanyRep(String userId, String fullName, String password, String companyName,
                     String department,String position) {
        super(userId, fullName, password);   // call the User constructor
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.registrationStatus = RegistrationStatus.PENDING;
    }

    /**
     * Adds a notification to this representative's inbox.
     *
     * @param notification the notification object to add
     */
    @Override
    public void receiveNotification(Notification notification) {
        notifications.add(notification);
        // optionally print to console/log
    }

    /**
     * Returns all notifications received by this representative.
     *
     * @return list of notifications
     */
    @Override
    public List<Notification> getNotifications() {
        return notifications;
    }

    // getters & setters
    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public int getNumOfInternships(){return this.numOfInternships;}
    public void setNumOfInternships(int numOfInternships){this.numOfInternships = numOfInternships;}
    public String getCompanyName(){return this.companyName;}
    public void setCompanyName(String companyName){this.companyName = companyName;}
    public String getDepartment(){return this.department;}
    public void setDepartment(String department){this.department = department;}
    public String getPosition(){return this.position;}
    public void setPosition(String position){this.position = position;}

    // helper function
    public boolean isApproved() {
        return this.registrationStatus == RegistrationStatus.APPROVED;
    }


    public void register(){
        this.registered = true;
    }
}