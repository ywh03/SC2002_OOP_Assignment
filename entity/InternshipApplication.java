package entity;

import entity.enums.ApplicationStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a student's application for a specific internship.
 * This entity tracks the application details, status, and whether a potential
 * offer has been accepted.
 * It implements {@code Serializable} for file persistence and {@code Identifiable}
 * to be managed by a repository.
 */
public class InternshipApplication implements Serializable, Identifiable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier for this internship application (e.g., "APP0001").
     */
    private String internshipApplicationId;
    private Internship internship;
    
    /**
     * The current status of the application, such as PENDING, SUCCESSFUL, or UNSUCCESSFUL.
     */
    private ApplicationStatus applicationStatus; // successful, pending, rejected
    private boolean offerAccepted; 

    /**
     * The {@link Student} who submitted this application.
     */
    private Student student;


    /**
     * Constructs a new InternshipApplication with an initial status of {@link ApplicationStatus#PENDING}.
     * The offer is defaulted to not accepted.
     *
     * @param internshipApplicationId The unique ID for the application.
     * @param internship The internship listing being applied for.
     * @param student The student submitting the application.
     */
    public InternshipApplication(String internshipApplicationId, Internship internship, Student student) {
        this.internshipApplicationId = internshipApplicationId;
        this.internship = internship;
        this.student = student;
        this.applicationStatus = ApplicationStatus.PENDING;
        this.offerAccepted = false;
    }

    /**
     * Retrieves the unique identifier of the internship application.
     *
     * @return The application ID string.
     */
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

}
