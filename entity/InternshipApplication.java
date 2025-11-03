package entity;

public class InternshipApplication {
    private Internship internship;
    private String appStatus; // successful, pending, rejected
    private boolean acceptedStatus; //i dont rly rmb what this was lols kiv

    public InternshipApplication(Internship internship, String appStatus, boolean acceptedStatus) {
        this.internship = internship;
        this.appStatus = appStatus;
        this.acceptedStatus = acceptedStatus;
    }

    public Internship getInternship() {return internship;}
    public void setInternship(Internship internship) {this.internship = internship;}
    public String getAppStatus() {return appStatus;}
    public void setAppStatus(String appStatus) {this.appStatus = appStatus;}
    public boolean isAcceptedStatus() {return acceptedStatus;}
    public void setAcceptedStatus(boolean acceptedStatus) {this.acceptedStatus = acceptedStatus;}
}