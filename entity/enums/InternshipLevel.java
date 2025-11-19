package entity.enums;


/**
 * Represents the difficulty or seniority level of an internship posting.
 *
 * <p>This enumeration is used by {@code Internship} to indicate the
 * expected level of knowledge and experience required from applicants.
 * Controllers such as {@code InternshipController} and
 * {@code InternshipApplicationController} use this information when
 * determining eligibility (e.g., Year 1–2 students may only apply for
 * BASIC-level internships).</p>
 *
 * <p>The levels are defined as:</p>
 * <ul>
 *     <li><b>BASIC</b> – Suitable for Year 1–2 students or applicants with
 *     introductory experience. Requirements are minimal.</li>
 *
 *     <li><b>INTERMEDIATE</b> – Intended for students with some foundational
 *     knowledge, typically Year 2–3, or applicants with moderate exposure.</li>
 *
 *     <li><b>ADVANCED</b> – Designed for more experienced or senior students,
 *     often Year 3–4, requiring strong technical ability or specialised skills.</li>
 * </ul>
 */
public enum InternshipLevel {
    BASIC,
    INTERMEDIATE,
    ADVANCED
}