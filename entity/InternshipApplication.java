package entity;

import entity.enums.ApplicationStatus;

import java.io.Serializable;

public class InternshipApplication implements Serializable, Identifiable {
    private String internshipApplicationId;
    private Internship internship;
    private ApplicationStatus applicationStatus; // successful, pending, rejected
    private boolean offerAccepted; 
    private Student student;
    private ApplicationStatus preWithdrawalStatus;

    public InternshipApplication(String internshipApplicationId, Internship internship, Student student) {
        this.internshipApplicationId = internshipApplicationId;
        this.internship = internship;
        this.student = student;
        this.applicationStatus = ApplicationStatus.PENDING; //default is set to pending
        this.offerAccepted = false; // default is false first
        this.preWithdrawalStatus = null;
    }

    @Override
    public String getId() {
        return this.internshipApplicationId;
    }

    public Internship getInternship() {return internship;}
    public void setInternship(Internship internship) {this.internship = internship;}
    public ApplicationStatus getApplicationStatus() {return applicationStatus;}
    public void setApplicationStatus(ApplicationStatus applicationStatus) {this.applicationStatus = applicationStatus;}
    public boolean getOfferAccepted() {return offerAccepted;}
    public void setOfferAccepted(boolean offerAccepted) {this.offerAccepted = offerAccepted;}
    public Student getStudent() {return student;}
    public void setStudent(Student student) {this.student = student;}
    public ApplicationStatus getPreWithdrawalStatus() {return preWithdrawalStatus;}
    public void setPreWithdrawalStatus(ApplicationStatus preWithdrawalStatus) {this.preWithdrawalStatus = preWithdrawalStatus}

    public void requestWithdrawal() {
    if (applicationStatus == ApplicationStatus.PENDING) { 
        preWithdrawalStatus = applicationStatus;          
        applicationStatus = ApplicationStatus.PENDING_WITHDRAWAL; 
    }
}

}