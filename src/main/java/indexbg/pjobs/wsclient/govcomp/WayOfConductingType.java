
package indexbg.pjobs.wsclient.govcomp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WayOfConductingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WayOfConductingType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CompetitionStages" type="{http://iisda.government.bg/Competitions/}CompetitionStageType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="AdditionalInfoHowToProceed" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WayOfConductingType", propOrder = {
    "competitionStages"
})
public class WayOfConductingType {

    @XmlElement(name = "CompetitionStages")
    protected List<CompetitionStageType> competitionStages;
    @XmlAttribute(name = "AdditionalInfoHowToProceed")
    protected String additionalInfoHowToProceed;

    /**
     * Gets the value of the competitionStages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the competitionStages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompetitionStages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompetitionStageType }
     * 
     * 
     */
    public List<CompetitionStageType> getCompetitionStages() {
        if (competitionStages == null) {
            competitionStages = new ArrayList<CompetitionStageType>();
        }
        return this.competitionStages;
    }

    /**
     * Gets the value of the additionalInfoHowToProceed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalInfoHowToProceed() {
        return additionalInfoHowToProceed;
    }

    /**
     * Sets the value of the additionalInfoHowToProceed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalInfoHowToProceed(String value) {
        this.additionalInfoHowToProceed = value;
    }

}
