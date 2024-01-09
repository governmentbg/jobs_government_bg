
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
 *         &lt;element name="fileID" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
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
    "fileID"
})
@XmlRootElement(name = "GetFileContent", namespace = "http://iisda.government.bg/Competitons/IntegrationServices")
public class GetFileContent {

    @XmlElement(namespace = "http://iisda.government.bg/Competitons/IntegrationServices", required = true, type = Long.class, nillable = true)
    protected Long fileID;

    /**
     * Gets the value of the fileID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFileID() {
        return fileID;
    }

    /**
     * Sets the value of the fileID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFileID(Long value) {
        this.fileID = value;
    }

}
