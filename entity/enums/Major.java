package entity.enums;

/**
 * Represents the academic major or field of study of a student.
 *
 * <p>This enumeration is used by the {@code Student} entity and is referenced
 * in various parts of the system, such as internship eligibility checks,
 * filtering mechanisms, and report generation. By using a predefined list of
 * majors, the system avoids inconsistent string inputs and ensures reliable
 * comparisons during internship matching.</p>
 *
 * <p>The majors are grouped into several broad academic domains:</p>
 *
 * <ul>
 *     <li><b>Engineering</b> – e.g., AEROSPACE_ENGINEERING, COMPUTER_ENGINEERING</li>
 *     <li><b>Computing / IT</b> – e.g., COMPUTER_SCIENCE, DATA_SCIENCE, CYBER_SECURITY</li>
 *     <li><b>Business & Management</b> – e.g., ACCOUNTING, FINANCE, BUSINESS_ANALYTICS</li>
 *     <li><b>Sciences</b> – e.g., CHEMISTRY, PHYSICS, MATHEMATICS, LIFE_SCIENCES</li>
 *     <li><b>Humanities & Social Sciences</b> – e.g., PSYCHOLOGY, SOCIOLOGY, HISTORY</li>
 *     <li><b>Design & Media</b> – e.g., ARCHITECTURE, GRAPHIC_DESIGN, GAME_DESIGN</li>
 *     <li><b>Health Sciences</b> – e.g., NURSING, BIOMEDICAL_SCIENCE, PUBLIC_HEALTH</li>
 *     <li><b>Others</b> – GENERAL_STUDIES, UNDECLARED</li>
 * </ul>
 */
public enum Major {
    // ========================
    // ENGINEERING
    // ========================
    AEROSPACE_ENGINEERING,
    BIOENGINEERING,
    CHEMICAL_ENGINEERING,
    CIVIL_ENGINEERING,
    COMPUTER_ENGINEERING,
    ELECTRICAL_ENGINEERING,
    ELECTRONIC_ENGINEERING,
    ENVIRONMENTAL_ENGINEERING,
    INDUSTRIAL_ENGINEERING,
    MATERIALS_ENGINEERING,
    MECHANICAL_ENGINEERING,
    MECHATRONICS_ENGINEERING,
    SYSTEMS_ENGINEERING,

    // ========================
    // COMPUTING / IT
    // ========================
    COMPUTER_SCIENCE,
    INFORMATION_SYSTEMS,
    DATA_SCIENCE,
    CYBER_SECURITY,
    SOFTWARE_ENGINEERING,
    ARTIFICIAL_INTELLIGENCE,

    // ========================
    // BUSINESS / MANAGEMENT
    // ========================
    ACCOUNTING,
    BUSINESS_ADMINISTRATION,
    BUSINESS_ANALYTICS,
    FINANCE,
    MARKETING,
    HUMAN_RESOURCE_MANAGEMENT,
    OPERATIONS_MANAGEMENT,
    SUPPLY_CHAIN_MANAGEMENT,
    ENTREPRENEURSHIP,
    ECONOMICS,

    // ========================
    // SCIENCES
    // ========================
    BIOLOGY,
    BIOTECHNOLOGY,
    CHEMISTRY,
    PHYSICS,
    MATHEMATICS,
    APPLIED_MATHEMATICS,
    STATISTICS,
    ENVIRONMENTAL_SCIENCE,
    LIFE_SCIENCES,

    // ========================
    // HUMANITIES & SOCIAL SCIENCES
    // ========================
    PSYCHOLOGY,
    SOCIOLOGY,
    POLITICAL_SCIENCE,
    PUBLIC_POLICY,
    COMMUNICATIONS,
    JOURNALISM,
    HISTORY,
    PHILOSOPHY,
    LINGUISTICS,

    // ========================
    // DESIGN / MEDIA
    // ========================
    ARCHITECTURE,
    INDUSTRIAL_DESIGN,
    GRAPHIC_DESIGN,
    DIGITAL_MEDIA,
    GAME_DESIGN,
    INTERACTIVE_MEDIA,

    // ========================
    // HEALTH SCIENCES
    // ========================
    NURSING,
    PHARMACY,
    BIOMEDICAL_SCIENCE,
    PUBLIC_HEALTH,
    NUTRITION_SCIENCE,

    // ========================
    // OTHERS
    // ========================
    GENERAL_STUDIES,
    UNDECLARED;
}
