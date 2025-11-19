package entity;

import java.util.ArrayList;
import java.util.List;

import entity.Notification;  

import entity.enums.Major;

public class Student extends User {
    private int yearOfStudy;
    private Major major;
    private ArrayList<InternshipApplication> appliedInternships = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();

    // constructor
    public Student(String userId, String fullName, String password, int yearOfStudy, Major major){
        super(userId, fullName, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    // getters & setters
    public int getYearOfStudy(){return this.yearOfStudy;}
    public void setYearOfStudy(int yearOfStudy){this.yearOfStudy = yearOfStudy;}
    public Major getMajor(){return this.major;}
    public void setMajor(Major major){this.major = major;}
    public ArrayList<InternshipApplication> getAppliedInternships(){return this.appliedInternships;}

    @Override
    public void receiveNotification(Notification notification) {
        notifications.add(notification);
        // optionally print to console/log
    }

    @Override
    public List<Notification> getNotifications() {
        return notifications;
    }
}