
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompetitionStatusesEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CompetitionStatusesEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Confirmed"/&gt;
 *     &lt;enumeration value="Published"/&gt;
 *     &lt;enumeration value="WaitingResult"/&gt;
 *     &lt;enumeration value="TerminatedNoCandidates"/&gt;
 *     &lt;enumeration value="TerminatedNoAdmittedCandidate"/&gt;
 *     &lt;enumeration value="TerminatedDueObjection"/&gt;
 *     &lt;enumeration value="Terminated"/&gt;
 *     &lt;enumeration value="CompletedWithRecruitment"/&gt;
 *     &lt;enumeration value="CompletedWithoutRanking"/&gt;
 *     &lt;enumeration value="CompletedWithoutRecruitment"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CompetitionStatusesEnum")
@XmlEnum
public enum CompetitionStatusesEnum {

    @XmlEnumValue("Confirmed")
    CONFIRMED("Confirmed"),
    @XmlEnumValue("Published")
    PUBLISHED("Published"),
    @XmlEnumValue("WaitingResult")
    WAITING_RESULT("WaitingResult"),
    @XmlEnumValue("TerminatedNoCandidates")
    TERMINATED_NO_CANDIDATES("TerminatedNoCandidates"),
    @XmlEnumValue("TerminatedNoAdmittedCandidate")
    TERMINATED_NO_ADMITTED_CANDIDATE("TerminatedNoAdmittedCandidate"),
    @XmlEnumValue("TerminatedDueObjection")
    TERMINATED_DUE_OBJECTION("TerminatedDueObjection"),
    @XmlEnumValue("Terminated")
    TERMINATED("Terminated"),
    @XmlEnumValue("CompletedWithRecruitment")
    COMPLETED_WITH_RECRUITMENT("CompletedWithRecruitment"),
    @XmlEnumValue("CompletedWithoutRanking")
    COMPLETED_WITHOUT_RANKING("CompletedWithoutRanking"),
    @XmlEnumValue("CompletedWithoutRecruitment")
    COMPLETED_WITHOUT_RECRUITMENT("CompletedWithoutRecruitment");
    private final String value;

    CompetitionStatusesEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CompetitionStatusesEnum fromValue(String v) {
        for (CompetitionStatusesEnum c: CompetitionStatusesEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
