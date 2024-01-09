
package indexbg.pjobs.wsclient.stJobs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JobClassifierCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="JobClassifierCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="KDA"/>
 *     &lt;enumeration value="KDD"/>
 *     &lt;enumeration value="KMVR"/>
 *     &lt;enumeration value="KDANS"/>
 *     &lt;enumeration value="NKPD"/>
 *     &lt;enumeration value="KACADEMIC"/>
 *     &lt;enumeration value="KMODL"/>
 *     &lt;enumeration value="KGDIN"/>
 *     &lt;enumeration value="KGDO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "JobClassifierCode", namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common")
@XmlEnum
public enum JobClassifierCode {

    KDA,
    KDD,
    KMVR,
    KDANS,
    NKPD,
    KACADEMIC,
    KMODL,
    KGDIN,
    KGDO;

    public String value() {
        return name();
    }

    public static JobClassifierCode fromValue(String v) {
        return valueOf(v);
    }

}
