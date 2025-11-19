package util.filter;

import entity.Internship;

public class CompanyFilter implements Filter {

    private final String companyName;

    public CompanyFilter(String companyName) {
        this.companyName = companyName.toUpperCase();
    }

    @Override
    public boolean matches(Internship internship) {
        return internship.getCompanyName().toLowerCase().contains(companyName);
    }
}
