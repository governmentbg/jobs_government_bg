
package indexbg.pjobs.wsclient.stJobs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JobClassificationRowType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="JobClassificationRowType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LEVEL"/>
 *     &lt;enumeration value="JOB"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "JobClassificationRowType", namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common")
@XmlEnum
public enum JobClassificationRowType {

    LEVEL,
    JOB;

    public String value() {
        return name();
    }

    public static JobClassificationRowType fromValue(String v) {
        return valueOf(v);
    }

}
