
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FileType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FileType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="FileID" use="required" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="FileName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="MimeType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FileType")
public class FileType {

    @XmlAttribute(name = "FileID", required = true)
    protected long fileID;
    @XmlAttribute(name = "FileName")
    protected String fileName;
    @XmlAttribute(name = "MimeType")
    protected String mimeType;

    /**
     * Gets the value of the fileID property.
     * 
     */
    public long getFileID() {
        return fileID;
    }

    /**
     * Sets the value of the fileID property.
     * 
     */
    public void setFileID(long value) {
        this.fileID = value;
    }

    /**
     * Gets the value of the fileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

}
