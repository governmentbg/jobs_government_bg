
package indexbg.pjobs.wsclient.govcomp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Documents" type="{http://iisda.government.bg/Competitions/}DocumentType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="IsPersonalSubmissionOfDocs" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentsType", propOrder = {
    "documents"
})
public class DocumentsType {

    @XmlElement(name = "Documents")
    protected List<DocumentType> documents;
    @XmlAttribute(name = "IsPersonalSubmissionOfDocs", required = true)
    protected boolean isPersonalSubmissionOfDocs;

    /**
     * Gets the value of the documents property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documents property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocuments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentType }
     * 
     * 
     */
    public List<DocumentType> getDocuments() {
        if (documents == null) {
            documents = new ArrayList<DocumentType>();
        }
        return this.documents;
    }

    /**
     * Gets the value of the isPersonalSubmissionOfDocs property.
     * 
     */
    public boolean isIsPersonalSubmissionOfDocs() {
        return isPersonalSubmissionOfDocs;
    }

    /**
     * Sets the value of the isPersonalSubmissionOfDocs property.
     * 
     */
    public void setIsPersonalSubmissionOfDocs(boolean value) {
        this.isPersonalSubmissionOfDocs = value;
    }

}
