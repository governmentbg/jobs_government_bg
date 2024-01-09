
package indexbg.pjobs.wsclient.stJobs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="classificationCode" type="{https://eisuchrda.egov.bg/IISDAIntegrationServices/Common}JobClassifierCode" minOccurs="0"/>
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
    "classificationCode"
})
@XmlRootElement(name = "GetJobsClassification", namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common")
public class GetJobsClassification {

    @XmlElement(namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common")
    protected JobClassifierCode classificationCode;

    /**
     * Gets the value of the classificationCode property.
     * 
     * @return
     *     possible object is
     *     {@link JobClassifierCode }
     *     
     */
    public JobClassifierCode getClassificationCode() {
        return classificationCode;
    }

    /**
     * Sets the value of the classificationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobClassifierCode }
     *     
     */
    public void setClassificationCode(JobClassifierCode value) {
        this.classificationCode = value;
    }

}
