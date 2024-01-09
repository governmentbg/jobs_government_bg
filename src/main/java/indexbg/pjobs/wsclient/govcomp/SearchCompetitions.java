
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="competitonID" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="batchIdentificationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="batchUIC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DateAt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "competitonID",
    "batchIdentificationNumber",
    "batchUIC",
    "dateAt"
})
@XmlRootElement(name = "SearchCompetitions", namespace = "http://iisda.government.bg/Competitons/IntegrationServices")
public class SearchCompetitions {

    @XmlElement(namespace = "http://iisda.government.bg/Competitons/IntegrationServices", required = true, type = Long.class, nillable = true)
    protected Long competitonID;
    @XmlElement(namespace = "http://iisda.government.bg/Competitons/IntegrationServices")
    protected String batchIdentificationNumber;
    @XmlElement(namespace = "http://iisda.government.bg/Competitons/IntegrationServices")
    protected String batchUIC;
    @XmlElement(name = "DateAt", namespace = "http://iisda.government.bg/Competitons/IntegrationServices", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateAt;

    /**
     * Gets the value of the competitonID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCompetitonID() {
        return competitonID;
    }

    /**
     * Sets the value of the competitonID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCompetitonID(Long value) {
        this.competitonID = value;
    }

    /**
     * Gets the value of the batchIdentificationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchIdentificationNumber() {
        return batchIdentificationNumber;
    }

    /**
     * Sets the value of the batchIdentificationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchIdentificationNumber(String value) {
        this.batchIdentificationNumber = value;
    }

    /**
     * Gets the value of the batchUIC property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchUIC() {
        return batchUIC;
    }

    /**
     * Sets the value of the batchUIC property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchUIC(String value) {
        this.batchUIC = value;
    }

    /**
     * Gets the value of the dateAt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateAt() {
        return dateAt;
    }

    /**
     * Sets the value of the dateAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateAt(XMLGregorianCalendar value) {
        this.dateAt = value;
    }

}
