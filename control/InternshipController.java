public class InternshipController {
    public boolean toggleVisibility(Internship internship){
        internship.setVisibility(!internship.getVisibility());
    }

    public void approveInternship(Internship internship){
        internship.setInternshipStatus(InternshipStatus.APPROVED);
    }

    public void rejectInternship(Internship internship){
        internship.setInternshipStatus(InternshipStatus.REJECTED);
    }
}
