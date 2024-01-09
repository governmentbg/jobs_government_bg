
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TopicsEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TopicsEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Political"/&gt;
 *     &lt;enumeration value="Economic"/&gt;
 *     &lt;enumeration value="Cultural"/&gt;
 *     &lt;enumeration value="Social"/&gt;
 *     &lt;enumeration value="Management"/&gt;
 *     &lt;enumeration value="Other"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TopicsEnum")
@XmlEnum
public enum TopicsEnum {

    @XmlEnumValue("Political")
    POLITICAL("Political"),
    @XmlEnumValue("Economic")
    ECONOMIC("Economic"),
    @XmlEnumValue("Cultural")
    CULTURAL("Cultural"),
    @XmlEnumValue("Social")
    SOCIAL("Social"),
    @XmlEnumValue("Management")
    MANAGEMENT("Management"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    TopicsEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TopicsEnum fromValue(String v) {
        for (TopicsEnum c: TopicsEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
