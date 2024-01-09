
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContactInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContactInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="PersonFirstName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PersonLastName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="Email" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactInfoType")
public class ContactInfoType {

    @XmlAttribute(name = "PersonFirstName")
    protected String personFirstName;
    @XmlAttribute(name = "PersonLastName")
    protected String personLastName;
    @XmlAttribute(name = "Phone")
    protected String phone;
    @XmlAttribute(name = "Email")
    protected String email;

    /**
     * Gets the value of the personFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonFirstName() {
        return personFirstName;
    }

    /**
     * Sets the value of the personFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonFirstName(String value) {
        this.personFirstName = value;
    }

    /**
     * Gets the value of the personLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonLastName() {
        return personLastName;
    }

    /**
     * Sets the value of the personLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonLastName(String value) {
        this.personLastName = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

}
