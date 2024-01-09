
package indexbg.pjobs.wsclient.stJobs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContractType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ContractType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TPO"/>
 *     &lt;enumeration value="SPO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ContractType", namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common")
@XmlEnum
public enum ContractType {

    TPO,
    SPO;

    public String value() {
        return name();
    }

    public static ContractType fromValue(String v) {
        return valueOf(v);
    }

}
