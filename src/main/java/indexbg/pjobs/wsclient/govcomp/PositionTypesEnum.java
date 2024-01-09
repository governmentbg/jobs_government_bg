
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PositionTypesEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PositionTypesEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="KDA"/&gt;
 *     &lt;enumeration value="KDD"/&gt;
 *     &lt;enumeration value="KMVR"/&gt;
 *     &lt;enumeration value="KDANS"/&gt;
 *     &lt;enumeration value="NKPD"/&gt;
 *     &lt;enumeration value="KACADEMIC"/&gt;
 *     &lt;enumeration value="KGDIN"/&gt;
 *     &lt;enumeration value="KGDO"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PositionTypesEnum")
@XmlEnum
public enum PositionTypesEnum {

    KDA,
    KDD,
    KMVR,
    KDANS,
    NKPD,
    KACADEMIC,
    KGDIN,
    KGDO;

    public String value() {
        return name();
    }

    public static PositionTypesEnum fromValue(String v) {
        return valueOf(v);
    }

}
