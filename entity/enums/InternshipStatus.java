package entity.enums;


/**
 * Represents the approval and availability state of an internship posting.
 *
 * <p>This enumeration is used by the {@code Internship} entity and is managed
 * primarily by the {@code InternshipController} and Career Centre Staff boundary.</p>
 *
 * <p>The values indicate the lifecycle of an internship posting:</p>
 *
 * <ul>
 *     <li><b>PENDING</b> – The internship has been created by a Company Representative
 *     and is awaiting review by Career Centre Staff. It cannot be applied for until approved.</li>
 *
 *     <li><b>APPROVED</b> – The internship has passed validation and is available for
 *     students to view and apply for, subject to visibility and application date rules.</li>
 *
 *     <li><b>REJECTED</b> – The internship posting has been reviewed and rejected by
 *     Career Centre Staff. It will not be shown to students or accessible for applications.</li>
 *
 *     <li><b>FILLED</b> – All available slots have been taken by students who accepted offers.
 *     Once FILLED, no further applications may be submitted.</li>
 * </ul>
 */
public enum InternshipStatus {
    PENDING,
    APPROVED,
    REJECTED,
    FILLED
}