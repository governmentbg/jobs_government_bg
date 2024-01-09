
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
 *         &lt;element name="SearchCompetitionsResult" type="{http://iisda.government.bg/Competitons/IntegrationServices}ArrayOfCompetitionType" minOccurs="0"/&gt;
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
    "searchCompetitionsResult"
})
@XmlRootElement(name = "SearchCompetitionsResponse", namespace = "http://iisda.government.bg/Competitons/IntegrationServices")
public class SearchCompetitionsResponse {

    @XmlElement(name = "SearchCompetitionsResult", namespace = "http://iisda.government.bg/Competitons/IntegrationServices")
    protected ArrayOfCompetitionType searchCompetitionsResult;

    /**
     * Gets the value of the searchCompetitionsResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCompetitionType }
     *     
     */
    public ArrayOfCompetitionType getSearchCompetitionsResult() {
        return searchCompetitionsResult;
    }

    /**
     * Sets the value of the searchCompetitionsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCompetitionType }
     *     
     */
    public void setSearchCompetitionsResult(ArrayOfCompetitionType value) {
        this.searchCompetitionsResult = value;
    }

}
