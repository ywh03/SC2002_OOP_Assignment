public class CompanyRepController {
    public void authoriseCompanyRep(CompanyRep companyRep){
        companyRep.setApproved(true);
    }

    public void rejectCompanyRep(CompanyRep companyRep){
        companyRep.setApproved(false);
    }

    public boolean register(String userId, String fullName, String password, String companyName,
    String department,String position){
        CompanyRep(userId, fullName, password, companyName, department,position);
        return True;
    }
}
