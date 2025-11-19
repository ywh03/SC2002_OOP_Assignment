package entity;
import entity.enums.ApplicationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import entity.enums.InternshipStatus;
import entity.enums.InternshipLevel;
import entity.enums.Major;

/**
 * Represents an internship posting created by a Company Representative in the
 * Internship Management System.
 *
 * <p>An Internship contains all relevant posting information such as title,
 * description, level, preferred major, application dates, and company details.
 * It also maintains a list of all {@link InternshipApplication} objects
 * submitted for this posting.</p>
 *
 * <p>This class implements {@link Identifiable} to ensure consistent ID handling
 * through the repository layer, and {@link Serializable} to allow the object to
 * be persisted to disk.</p>
 */
public class Internship implements Serializable, Identifiable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String internshipId;
    private int numOfSlots;
    private String internshipTitle;
    private String description;
    private InternshipLevel level;
    private Major preferredMajor;
    private Date appOpenDate;
    private Date appCloseDate; 
    private String companyName;
    private CompanyRep compRepIC;
    private InternshipStatus internshipStatus; 
    private ArrayList<InternshipApplication> internshipApplications; // contains the internship applications JUST for this internship
    private boolean visibility;

    /**
     * Constructs a new Internship posting with the specified details.
     *
     * @param internshipId       unique identifier for the internship
     * @param internshipTitle    title of the internship posting
     * @param description        description of duties and requirements
     * @param level              difficulty/experience level required
     * @param preferredMajor     preferred major for applicants
     * @param appOpenDate        date when applications open
     * @param appCloseDate       date when applications close
     * @param internshipStatus   posting approval status
     * @param companyName        name of the hiring company
     * @param compRepIC          company representative in charge
     * @param numOfSlots         number of available internship slots
     */
    public Internship(String internshipId, String internshipTitle, String description, InternshipLevel level, Major preferredMajor, Date appOpenDate, Date appCloseDate, InternshipStatus internshipStatus, String companyName, CompanyRep compRepIC, int numOfSlots) {
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

    /**
     * Returns the unique identifier of this internship.
     *
     * @return internship ID
     */
    @Override
    public String getId() {
        return this.internshipId;
    }

    public String getInternshipTitle() {return internshipTitle;}
    public void setInternshipTitle(String internshipTitle) {this.internshipTitle = internshipTitle;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public InternshipLevel getLevel() {return level;}
    public void setLevel(InternshipLevel level) {this.level = level;}
    public Major getPreferredMajor() {return preferredMajor;}
    public void setPreferredMajor(Major preferredMajor) {this.preferredMajor = preferredMajor;}
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

    /**
     * Returns all internship applications that have been accepted
     * (i.e., SUCCESSFUL status).
     *
     * @return list of accepted applications
     */
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
