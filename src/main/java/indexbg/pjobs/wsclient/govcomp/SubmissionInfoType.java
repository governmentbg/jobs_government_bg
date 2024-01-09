
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SubmissionInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubmissionInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EkatteAddress" type="{http://iisda.government.bg/Competitions/}EkatteAddressType" minOccurs="0"/&gt;
 *         &lt;element name="ContactInfo" type="{http://iisda.government.bg/Competitions/}ContactInfoType" minOccurs="0"/&gt;
 *         &lt;element name="InformationInfoType" type="{http://iisda.government.bg/Competitions/}InformationInfoType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="DocumentsUnitID" use="required" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="AddressText" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PostCode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PublishedDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *       &lt;attribute name="DeadlineDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubmissionInfoType", propOrder = {
    "ekatteAddress",
    "contactInfo",
    "informationInfoType"
})
public class SubmissionInfoType {

    @XmlElement(name = "EkatteAddress")
    protected EkatteAddressType ekatteAddress;
    @XmlElement(name = "ContactInfo")
    protected ContactInfoType contactInfo;
    @XmlElement(name = "InformationInfoType")
    protected InformationInfoType informationInfoType;
    @XmlAttribute(name = "DocumentsUnitID", required = true)
    protected long documentsUnitID;
    @XmlAttribute(name = "AddressText")
    protected String addressText;
    @XmlAttribute(name = "PostCode")
    protected String postCode;
    @XmlAttribute(name = "PublishedDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar publishedDate;
    @XmlAttribute(name = "DeadlineDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar deadlineDate;

    /**
     * Gets the value of the ekatteAddress property.
     * 
     * @return
     *     possible object is
     *     {@link EkatteAddressType }
     *     
     */
    public EkatteAddressType getEkatteAddress() {
        return ekatteAddress;
    }

    /**
     * Sets the value of the ekatteAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link EkatteAddressType }
     *     
     */
    public void setEkatteAddress(EkatteAddressType value) {
        this.ekatteAddress = value;
    }

    /**
     * Gets the value of the contactInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ContactInfoType }
     *     
     */
    public ContactInfoType getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the value of the contactInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactInfoType }
     *     
     */
    public void setContactInfo(ContactInfoType value) {
        this.contactInfo = value;
    }

    /**
     * Gets the value of the informationInfoType property.
     * 
     * @return
     *     possible object is
     *     {@link InformationInfoType }
     *     
     */
    public InformationInfoType getInformationInfoType() {
        return informationInfoType;
    }

    /**
     * Sets the value of the informationInfoType property.
     * 
     * @param value
     *     allowed object is
     *     {@link InformationInfoType }
     *     
     */
    public void setInformationInfoType(InformationInfoType value) {
        this.informationInfoType = value;
    }

    /**
     * Gets the value of the documentsUnitID property.
     * 
     */
    public long getDocumentsUnitID() {
        return documentsUnitID;
    }

    /**
     * Sets the value of the documentsUnitID property.
     * 
     */
    public void setDocumentsUnitID(long value) {
        this.documentsUnitID = value;
    }

    /**
     * Gets the value of the addressText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressText() {
        return addressText;
    }

    /**
     * Sets the value of the addressText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressText(String value) {
        this.addressText = value;
    }

    /**
     * Gets the value of the postCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Sets the value of the postCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCode(String value) {
        this.postCode = value;
    }

    /**
     * Gets the value of the publishedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPublishedDate() {
        return publishedDate;
    }

    /**
     * Sets the value of the publishedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPublishedDate(XMLGregorianCalendar value) {
        this.publishedDate = value;
    }

    /**
     * Gets the value of the deadlineDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeadlineDate() {
        return deadlineDate;
    }

    /**
     * Sets the value of the deadlineDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeadlineDate(XMLGregorianCalendar value) {
        this.deadlineDate = value;
    }

}
