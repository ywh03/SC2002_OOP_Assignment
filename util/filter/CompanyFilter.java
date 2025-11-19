package util.filter;

import entity.Internship;


/**
 * Implements the {@link Filter} interface to check if an {@link Internship}'s
 * company name matches (contains) a specific search term. The search is case-insensitive.
 */
public class CompanyFilter implements Filter {

    private final String companyName;



    /**
     * Constructs a CompanyFilter with the specified company name search term.
     * The search term is converted to uppercase internally for case-insensitive matching.
     *
     * @param companyName The company name (or part of the name) to search for.
     */
    public CompanyFilter(String companyName) {
        this.companyName = companyName.toUpperCase();
    }

    /**
     * Checks if the given internship's company name contains the search term
     * specified in this filter, ignoring case.
     *
     * @param internship The Internship object to check.
     * @return true if the company name contains the filter term, false otherwise.
     */
    @Override
    public boolean matches(Internship internship) {
        return internship.getCompanyName().toLowerCase().contains(companyName);
    }
}
