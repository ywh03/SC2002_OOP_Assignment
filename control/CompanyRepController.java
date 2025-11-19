package control;

import entity.CompanyRep;
import repository.UserRepository;

import java.util.ArrayList;

import manager.NotificationManager;

/**
 * Controller responsible for managing Company Representative–related business logic.
 *
 * <p>This includes:</p>
 * <ul>
 *     <li>Approving or rejecting Company Representative registrations</li>
 *     <li>Retrieving approved or pending CompanyRep accounts</li>
 *     <li>Sending notifications when appropriate</li>
 * </ul>
 *
 * <p>This controller does not handle user interface, input/output, or printing.
 * It focuses strictly on enforcing business rules and interacting with the
 * UserRepository and NotificationManager. This aligns with the BCE architecture
 * and the Single Responsibility Principle.</p>
 */
public class CompanyRepController {
    private final UserRepository userRepository;

    /**
     * Creates a CompanyRepController with access to the user repository.
     *
     * @param userRepo the UserRepository used to retrieve and persist CompanyRep data
     */
    public CompanyRepController(UserRepository userRepo){
        this.userRepository = userRepo;
    }

    /**
     * Approves a Company Representative registration.
     *
     * <p>If the given ID corresponds to an existing CompanyRep, the system marks the account as approved and persists
     * the change. A notification is also sent to the representative informing them of the approval.</p>
     *
     * @param companyRepId the ID of the CompanyRep to approve
     * @return true if approval succeeds, false if the ID does not exist
     */
    public boolean authoriseCompanyRep(String companyRepId){
        CompanyRep companyRep = (CompanyRep) userRepository.findById(companyRepId);
        if (companyRep == null) return false;

        companyRep.setApproved(true);
        userRepository.save(companyRep);
        NotificationManager.getInstance().sendNotification(companyRepId,"Your company representative registration has been approved.");

        return true;
    }

    /**
     * Rejects a Company Representative registration.
     *
     * <p>If the CompanyRep exists, their account remains unapproved and the change
     * is persisted. This method may be extended to send notifications as well.</p>
     *
     * @param companyRepId the ID of the CompanyRep to reject
     * @return true if rejection succeeds, false if the ID does not exist
     */
    public boolean rejectCompanyRep(String companyRepId){
        CompanyRep companyRep = (CompanyRep) userRepository.findById(companyRepId);
        if (companyRep == null) return false;
        userRepository.save(companyRep);
        companyRep.setApproved(false);
        NotificationManager.getInstance().sendNotification(companyRepId,"Your company representative registration has been rejected.");
        return true;
    }

    /**
     * Returns all Company Representatives whose registrations have been approved.
     *
     * @return a list of approved CompanyRep accounts
     */
    public ArrayList<CompanyRep> approvedCompanyReps(){
        ArrayList<CompanyRep> companyReps = this.userRepository.getAllCompanyReps();
        return new ArrayList<>(companyReps.stream().filter(CompanyRep::getApproved).toList());
    }

    /**
     * Returns all Company Representatives whose registrations are still pending approval.
     *
     * @return a list of unapproved CompanyRep accounts
     */
    public ArrayList<CompanyRep> getPendingRegistrations(){
        ArrayList<CompanyRep> companyReps = this.userRepository.getAllCompanyReps();
        return new ArrayList<>(companyReps.stream().filter(c -> !c.getApproved()).toList());
    }
}
