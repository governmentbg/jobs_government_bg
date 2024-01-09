
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LevalsOfEducationEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LevalsOfEducationEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Primary"/&gt;
 *     &lt;enumeration value="Secondary"/&gt;
 *     &lt;enumeration value="SecondarySpecial"/&gt;
 *     &lt;enumeration value="Bachelor"/&gt;
 *     &lt;enumeration value="ProfessionalBachelor"/&gt;
 *     &lt;enumeration value="Master"/&gt;
 *     &lt;enumeration value="PhD"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LevalsOfEducationEnum")
@XmlEnum
public enum LevalsOfEducationEnum {

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
    PH_D("PhD");
    private final String value;

    LevalsOfEducationEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LevalsOfEducationEnum fromValue(String v) {
        for (LevalsOfEducationEnum c: LevalsOfEducationEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
