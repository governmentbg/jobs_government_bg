
package indexbg.pjobs.wsclient.govcomp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCompetitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCompetitionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CompetitionType" type="{http://iisda.government.bg/Competitions/}CompetitionType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCompetitionType", namespace = "http://iisda.government.bg/Competitons/IntegrationServices", propOrder = {
    "competitionType"
})
public class ArrayOfCompetitionType {

    @XmlElement(name = "CompetitionType", nillable = true)
    protected List<CompetitionType> competitionType;

    /**
     * Gets the value of the competitionType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the competitionType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompetitionType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompetitionType }
     * 
     * 
     */
    public List<CompetitionType> getCompetitionType() {
        if (competitionType == null) {
            competitionType = new ArrayList<CompetitionType>();
        }
        return this.competitionType;
    }

}
