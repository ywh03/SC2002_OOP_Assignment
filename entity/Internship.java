package entity;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Internship {
    private int numOfSlots;
    private String internshipTitle;
    private String description;
    private String level;
    private String prefMajor;
    private Date appOpenDate;
    private Date appCloseDate; 
    private String companyName;
    private String compRepIC;
    private String internshipStatus; 

    private List<InternshipApplication> applications; // all apps rather than just accepted ones

    public Internship(String internshipTitle, String description, String level, String prefMajor, Date appOpenDate, Date appCloseDate, String internshipStatus, String companyName, String compRepIC, int numOfSlots, boolean isVisible) {
        this.internshipTitle = internshipTitle;
        this.description = description;
        this.level = level;
        this.prefMajor = prefMajor;
        this.appOpenDate = appOpenDate;
        this.appCloseDate = appCloseDate;
        this.internshipStatus = internshipStatus;
        this.companyName = companyName;
        this.compRepIC = compRepIC;
        this.numOfSlots = numOfSlots;
        this.applications = new ArrayList<>();
    }

    public String getInternshipTitle() {return internshipTitle;}
    public void setInternshipTitle(String internshipTitle) {this.internshipTitle = internshipTitle;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String getLevel() {return level;}
    public void setLevel(String level) {this.level = level;}
    public String getPrefMajor() {return prefMajor;}
    public void setPrefMajor(String prefMajor) {this.prefMajor = prefMajor;}
    public Date getAppOpenDate() {return appOpenDate;}
    public void setAppOpenDate(Date appOpenDate) {this.appOpenDate = appOpenDate;}
    public Date getAppCloseDate() {return appCloseDate;}
    public void setAppCloseDate(Date appCloseDate) {this.appCloseDate = appCloseDate;}
    public String getInternshipStatus() {return internshipStatus;}
    public void setInternshipStatus(String internshipStatus) {this.internshipStatus = internshipStatus;}
    public String getCompanyName() {return companyName;}
    public void setCompanyName(String companyName) {this.companyName = companyName;}
    public String getCompRepIC() {return compRepIC;}
    public void setCompRepIC(String compRepIC) {this.compRepIC = compRepIC;}
    public int getNumOfSlots() {return numOfSlots;}
    public void setNumOfSlots(int numOfSlots) {this.numOfSlots = numOfSlots;}

    public List<InternshipApplication> getAcceptedApps() {
        List<InternshipApplication> acceptedApps = new ArrayList<>();
        for (InternshipApplication app: applications) {
            if ("successful".equalsIgnoreCase(app.getAppStatus())) {
                acceptedApps.add(app);
            }
        }
        return acceptedApps;
    }
}