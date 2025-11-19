package entity.enums;

/**
 * Represents the various states an internship application may be in throughout its lifecycle.
 *
 * <p>This enum is used by {@code InternshipApplication} to track the
 * status of each student's application, and is referenced by both
 * company representatives and career centre staff when processing,
 * approving or rejecting applications.</p>
 *
 * <p>Statuses include:</p>
 * <ul>
 *     <li><b>PENDING</b> – Application has been submitted and is awaiting review.</li>
 *     <li><b>SUCCESSFUL</b> – Application has been approved by the company representative.</li>
 *     <li><b>UNSUCCESSFUL</b> – Application has been rejected by the company representative.</li>
 *     <li><b>WITHDRAWN</b> – Withdrawal request approved by career centre staff.</li>
 *     <li><b>PENDING_WITHDRAWAL</b> – Student has requested to withdraw; awaiting staff review.</li>
 * </ul>
 */
public enum ApplicationStatus {
    PENDING,
    SUCCESSFUL,
    UNSUCCESSFUL,
    WITHDRAWN,
    PENDING_WITHDRAWAL,
}