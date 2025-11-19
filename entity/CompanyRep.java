package entity;

import java.util.ArrayList;
import java.util.List;

import entity.Notification;

public class CompanyRep extends User implements Notifiable{
    private boolean approved = false;
    private int numOfInternships = 0;
    private String companyName;
    private ArrayList<Internship> internshipInfo = new ArrayList<>();
    private String department;
    private String position;
    private boolean registered = false;
    private List<Notification> notifications = new ArrayList<>();

    // constructor
    public CompanyRep(String userId, String fullName, String password, String companyName,
                     String department,String position) {
        super(userId, fullName, password);   // call the User constructor
        this.companyName = companyName;
        this.department = department;
        this.position = position;
    }

    @Override
    public void receiveNotification(Notification notification) {
        notifications.add(notification);
        // optionally print to console/log
    }

    @Override
    public List<Notification> getNotifications() {
        return notifications;
    }

    // getters & setters
    public boolean getApproved(){return this.approved;}
    public void setApproved(boolean approved){this.approved = approved;}
    public int getNumOfInternships(){return this.numOfInternships;}
    public void setNumOfInternships(int numOfInternships){this.numOfInternships = numOfInternships;}
    public String getCompanyName(){return this.companyName;}
    public void setCompanyName(String companyName){this.companyName = companyName;}
    public ArrayList<Internship> getInternshipInfo(){return this.internshipInfo;}
    public String getDepartment(){return this.department;}
    public void setDepartment(String department){this.department = department;}
    public String getPosition(){return this.position;}
    public void setPosition(String position){this.position = position;}

    public void register(){
        this.registered = true;
    }

    public void addInternship(Internship internship){
        internshipInfo.add(internship);
    }
}