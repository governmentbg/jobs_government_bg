
package indexbg.pjobs.wsclient.stJobs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetJobsClassificationResult" type="{http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models}ArrayOfJobClassification" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getJobsClassificationResult"
})
@XmlRootElement(name = "GetJobsClassificationResponse", namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common")
public class GetJobsClassificationResponse {

    @XmlElementRef(name = "GetJobsClassificationResult", namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfJobClassification> getJobsClassificationResult;

    /**
     * Gets the value of the getJobsClassificationResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfJobClassification }{@code >}
     *     
     */
    public JAXBElement<ArrayOfJobClassification> getGetJobsClassificationResult() {
        return getJobsClassificationResult;
    }

    /**
     * Sets the value of the getJobsClassificationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfJobClassification }{@code >}
     *     
     */
    public void setGetJobsClassificationResult(JAXBElement<ArrayOfJobClassification> value) {
        this.getJobsClassificationResult = value;
    }

}
