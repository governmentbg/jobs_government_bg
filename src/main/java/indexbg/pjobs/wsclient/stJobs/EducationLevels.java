
package indexbg.pjobs.wsclient.stJobs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EducationLevels.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EducationLevels">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Primary"/>
 *     &lt;enumeration value="Secondary"/>
 *     &lt;enumeration value="SecondarySpecial"/>
 *     &lt;enumeration value="Bachelor"/>
 *     &lt;enumeration value="ProfessionalBachelor"/>
 *     &lt;enumeration value="Master"/>
 *     &lt;enumeration value="PhD"/>
 *     &lt;enumeration value="SecondaryAndQualification"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EducationLevels", namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common")
@XmlEnum
public enum EducationLevels {

    @XmlEnumValue("Primary")
    PRIMARY("Primary"),
    @XmlEnumValue("Secondary")
    SECONDARY("Secondary"),
    @XmlEnumValue("SecondarySpecial")
    SECONDARY_SPECIAL("SecondarySpecial"),
    @XmlEnumValue("Bachelor")
    BACHELOR("Bachelor"),
    @XmlEnumValue("ProfessionalBachelor")
    PROFESSIONAL_BACHELOR("ProfessionalBachelor"),
    @XmlEnumValue("Master")
    MASTER("Master"),
    @XmlEnumValue("PhD")
    PH_D("PhD"),
    @XmlEnumValue("SecondaryAndQualification")
    SECONDARY_AND_QUALIFICATION("SecondaryAndQualification");
    private final String value;

    EducationLevels(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EducationLevels fromValue(String v) {
        for (EducationLevels c: EducationLevels.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
