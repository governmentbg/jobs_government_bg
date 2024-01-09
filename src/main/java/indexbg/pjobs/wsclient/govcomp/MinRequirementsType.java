
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MinRequirementsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MinRequirementsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="LevalOfEducation" use="required" type="{http://iisda.government.bg/Competitions/}LevalsOfEducationEnum" /&gt;
 *       &lt;attribute name="RankCategoryID" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="RankCategoryName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="MinExperience" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinRequirementsType")
public class MinRequirementsType {

    @XmlAttribute(name = "LevalOfEducation", required = true)
    protected LevalsOfEducationEnum levalOfEducation;
    @XmlAttribute(name = "RankCategoryID")
    protected Long rankCategoryID;
    @XmlAttribute(name = "RankCategoryName")
    protected String rankCategoryName;
    @XmlAttribute(name = "MinExperience")
    protected String minExperience;

    /**
     * Gets the value of the levalOfEducation property.
     * 
     * @return
     *     possible object is
     *     {@link LevalsOfEducationEnum }
     *     
     */
    public LevalsOfEducationEnum getLevalOfEducation() {
        return levalOfEducation;
    }

    /**
     * Sets the value of the levalOfEducation property.
     * 
     * @param value
     *     allowed object is
     *     {@link LevalsOfEducationEnum }
     *     
     */
    public void setLevalOfEducation(LevalsOfEducationEnum value) {
        this.levalOfEducation = value;
    }

    /**
     * Gets the value of the rankCategoryID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRankCategoryID() {
        return rankCategoryID;
    }

    /**
     * Sets the value of the rankCategoryID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRankCategoryID(Long value) {
        this.rankCategoryID = value;
    }

    /**
     * Gets the value of the rankCategoryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRankCategoryName() {
        return rankCategoryName;
    }

    /**
     * Sets the value of the rankCategoryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRankCategoryName(String value) {
        this.rankCategoryName = value;
    }

    /**
     * Gets the value of the minExperience property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinExperience() {
        return minExperience;
    }

    /**
     * Sets the value of the minExperience property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinExperience(String value) {
        this.minExperience = value;
    }

}
