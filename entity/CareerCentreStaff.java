package entity;

import java.util.ArrayList;
import java.util.List;

import entity.Notification;

public class CareerCentreStaff extends User implements Notifiable{
    private String staffDepartment;
    private List<Notification> notifications = new ArrayList<>();

    // constructor
    public CareerCentreStaff(String userId, String fullName, String password, String staffDepartment){
        super(userId, fullName, password);
        this.staffDepartment = staffDepartment;
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
    public String getStaffDepartment(){return this.staffDepartment;}

    public void setStaffDepartment(String staffDepartment){this.staffDepartment = staffDepartment;}


}