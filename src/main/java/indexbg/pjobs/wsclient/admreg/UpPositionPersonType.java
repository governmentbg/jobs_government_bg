
package indexbg.pjobs.wsclient.admreg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for UpPositionPersonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpPositionPersonType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PersonData" type="{http://iisda.government.bg/RAS/Common}PersonDataType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="UpPstnPersonID" use="required" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="VersionID" use="required" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="TakeOfficeDate" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *       &lt;attribute name="LeaveOfficeDate" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpPositionPersonType", propOrder = {
    "personData"
})
public class UpPositionPersonType {

    @XmlElement(name = "PersonData")
    protected PersonDataType personData;
    @XmlAttribute(name = "UpPstnPersonID", required = true)
    protected long upPstnPersonID;
    @XmlAttribute(name = "VersionID", required = true)
    protected long versionID;
    @XmlAttribute(name = "TakeOfficeDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar takeOfficeDate;
    @XmlAttribute(name = "LeaveOfficeDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar leaveOfficeDate;

    /**
     * Gets the value of the personData property.
     * 
     * @return
     *     possible object is
     *     {@link PersonDataType }
     *     
     */
    public PersonDataType getPersonData() {
        return personData;
    }

    /**
     * Sets the value of the personData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonDataType }
     *     
     */
    public void setPersonData(PersonDataType value) {
        this.personData = value;
    }

    /**
     * Gets the value of the upPstnPersonID property.
     * 
     */
    public long getUpPstnPersonID() {
        return upPstnPersonID;
    }

    /**
     * Sets the value of the upPstnPersonID property.
     * 
     */
    public void setUpPstnPersonID(long value) {
        this.upPstnPersonID = value;
    }

    /**
     * Gets the value of the versionID property.
     * 
     */
    public long getVersionID() {
        return versionID;
    }

    /**
     * Sets the value of the versionID property.
     * 
     */
    public void setVersionID(long value) {
        this.versionID = value;
    }

    /**
     * Gets the value of the takeOfficeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTakeOfficeDate() {
        return takeOfficeDate;
    }

    /**
     * Sets the value of the takeOfficeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTakeOfficeDate(XMLGregorianCalendar value) {
        this.takeOfficeDate = value;
    }

    /**
     * Gets the value of the leaveOfficeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLeaveOfficeDate() {
        return leaveOfficeDate;
    }

    /**
     * Sets the value of the leaveOfficeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLeaveOfficeDate(XMLGregorianCalendar value) {
        this.leaveOfficeDate = value;
    }

}
