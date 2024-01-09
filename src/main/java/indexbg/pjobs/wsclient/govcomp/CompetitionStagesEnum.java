
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompetitionStagesEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CompetitionStagesEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="StrategicManagementConcept"/&gt;
 *     &lt;enumeration value="PaperWork"/&gt;
 *     &lt;enumeration value="PracticalExamination"/&gt;
 *     &lt;enumeration value="Test"/&gt;
 *     &lt;enumeration value="PhysicalExam"/&gt;
 *     &lt;enumeration value="InvestigationOfIntellectualAbilityAndKnowledge"/&gt;
 *     &lt;enumeration value="PsychologicalTest"/&gt;
 *     &lt;enumeration value="Interview"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CompetitionStagesEnum")
@XmlEnum
public enum CompetitionStagesEnum {

    @XmlEnumValue("StrategicManagementConcept")
    STRATEGIC_MANAGEMENT_CONCEPT("StrategicManagementConcept"),
    @XmlEnumValue("PaperWork")
    PAPER_WORK("PaperWork"),
    @XmlEnumValue("PracticalExamination")
    PRACTICAL_EXAMINATION("PracticalExamination"),
    @XmlEnumValue("Test")
    TEST("Test"),
    @XmlEnumValue("PhysicalExam")
    PHYSICAL_EXAM("PhysicalExam"),
    @XmlEnumValue("InvestigationOfIntellectualAbilityAndKnowledge")
    INVESTIGATION_OF_INTELLECTUAL_ABILITY_AND_KNOWLEDGE("InvestigationOfIntellectualAbilityAndKnowledge"),
    @XmlEnumValue("PsychologicalTest")
    PSYCHOLOGICAL_TEST("PsychologicalTest"),
    @XmlEnumValue("Interview")
    INTERVIEW("Interview");
    private final String value;

    CompetitionStagesEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CompetitionStagesEnum fromValue(String v) {
        for (CompetitionStagesEnum c: CompetitionStagesEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
