
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentTypesEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentTypesEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Sample"/&gt;
 *     &lt;enumeration value="Document"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DocumentTypesEnum")
@XmlEnum
public enum DocumentTypesEnum {

    @XmlEnumValue("Sample")
    SAMPLE("Sample"),
    @XmlEnumValue("Document")
    DOCUMENT("Document");
    private final String value;

    DocumentTypesEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DocumentTypesEnum fromValue(String v) {
        for (DocumentTypesEnum c: DocumentTypesEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
