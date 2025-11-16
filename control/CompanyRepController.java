public class CompanyRepController {
    public void authoriseCompanyRep(CompanyRep companyRep){
        companyRep.setApproved(true);
    }

    public void rejectCompanyRep(CompanyRep companyRep){
        companyRep.setApproved(false);
    }
}
