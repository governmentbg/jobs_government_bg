
package indexbg.pjobs.wsclient.stJobs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MinimumRang complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MinimumRang">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MinimumRangId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="MinimumRangName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinimumRang", propOrder = {
    "minimumRangId",
    "minimumRangName"
})
public class MinimumRang {

    @XmlElement(name = "MinimumRangId")
    protected Integer minimumRangId;
    @XmlElementRef(name = "MinimumRangName", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<String> minimumRangName;

    /**
     * Gets the value of the minimumRangId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinimumRangId() {
        return minimumRangId;
    }

    /**
     * Sets the value of the minimumRangId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinimumRangId(Integer value) {
        this.minimumRangId = value;
    }

    /**
     * Gets the value of the minimumRangName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMinimumRangName() {
        return minimumRangName;
    }

    /**
     * Sets the value of the minimumRangName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMinimumRangName(JAXBElement<String> value) {
        this.minimumRangName = value;
    }

}
