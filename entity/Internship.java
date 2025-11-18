package entity;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import entity.enums.InternshipStatus;
import entity.enums.InternshipLevel;
import entity.enums.ApplicationStatus;

public class Internship implements Serializable {
    private String internshipId;
    private int numOfSlots;
    private String internshipTitle;
    private String description;
    private InternshipLevel level;
    private String preferredMajor;
    private Date appOpenDate;
    private Date appCloseDate; 
    private String companyName;
    private CompanyRep compRepIC;
    private InternshipStatus internshipStatus; 
    private ArrayList<InternshipApplication> internshipApplications; // contains the internship applications JUST for this internship
    private boolean visibility;

    public Internship(String internshipId, String internshipTitle, String description, InternshipLevel level, String preferredMajor, Date appOpenDate, Date appCloseDate, InternshipStatus internshipStatus, String companyName, CompanyRep compRepIC, int numOfSlots) {
        this.internshipId = internshipId;
        this.internshipTitle = internshipTitle;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.appOpenDate = appOpenDate;
        this.appCloseDate = appCloseDate;
        this.internshipStatus = internshipStatus;
        this.companyName = companyName;
        this.compRepIC = compRepIC;
        this.numOfSlots = numOfSlots;
        this.internshipApplications = new ArrayList<>();
        this.visibility = false; // default off so the staff can toggle on later
    }

    @Override
    public String getId() {
        return this.internshipID;
    }

    public String getInternshipTitle() {return internshipTitle;}
    public void setInternshipTitle(String internshipTitle) {this.internshipTitle = internshipTitle;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public InternshipLevel getLevel() {return level;}
    public void setLevel(InternshipLevel level) {this.level = level;}
    public String getPreferredMajor() {return preferredMajor;}
    public void setPreferredMajor(String preferredMajor) {this.preferredMajor = preferredMajor;}
    public Date getAppOpenDate() {return appOpenDate;}
    public void setAppOpenDate(Date appOpenDate) {this.appOpenDate = appOpenDate;}
    public Date getAppCloseDate() {return appCloseDate;}
    public void setAppCloseDate(Date appCloseDate) {this.appCloseDate = appCloseDate;}
    public InternshipStatus getInternshipStatus() {return internshipStatus;}
    public void setInternshipStatus(InternshipStatus internshipStatus) {this.internshipStatus = internshipStatus;}
    public String getCompanyName() {return companyName;}
    public void setCompanyName(String companyName) {this.companyName = companyName;}
    public CompanyRep getCompRepIC() {return compRepIC;}
    public void setCompRepIC(CompanyRep compRepIC) {this.compRepIC = compRepIC;}
    public int getNumOfSlots() {return numOfSlots;}
    public void setNumOfSlots(int numOfSlots) {this.numOfSlots = numOfSlots;}
    public boolean getVisibility() {return this.visibility;}
    public void setVisibility(boolean visibility) {this.visibility = visibility;}

    public ArrayList<InternshipApplication> getInternshipApplications() {
        return internshipApplications;
    }

    public ArrayList<InternshipApplication> getAcceptedApps() { 
        ArrayList<InternshipApplication> acceptedApps = new ArrayList<>();
        for (InternshipApplication app: internshipApplications) {
            if (app.getApplicationStatus() == ApplicationStatus.SUCCESSFUL) {
                acceptedApps.add(app);
            }
        }
        return acceptedApps;
    }
}
