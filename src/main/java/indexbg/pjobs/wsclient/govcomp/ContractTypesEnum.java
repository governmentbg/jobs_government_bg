
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContractTypesEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ContractTypesEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SPO"/&gt;
 *     &lt;enumeration value="TPO"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ContractTypesEnum")
@XmlEnum
public enum ContractTypesEnum {

    SPO,
    TPO;

    public String value() {
        return name();
    }

    public static ContractTypesEnum fromValue(String v) {
        return valueOf(v);
    }

}
