
package indexbg.pjobs.wsclient.govcomp;

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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GetFileContentResult" type="{http://schemas.microsoft.com/Message}StreamBody"/&gt;
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
    "getFileContentResult"
})
@XmlRootElement(name = "GetFileContentResponse", namespace = "http://iisda.government.bg/Competitons/IntegrationServices")
public class GetFileContentResponse {

    @XmlElement(name = "GetFileContentResult", namespace = "http://iisda.government.bg/Competitons/IntegrationServices", required = true)
    protected byte[] getFileContentResult;

    /**
     * Gets the value of the getFileContentResult property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetFileContentResult() {
        return getFileContentResult;
    }

    /**
     * Sets the value of the getFileContentResult property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetFileContentResult(byte[] value) {
        this.getFileContentResult = value;
    }

}
