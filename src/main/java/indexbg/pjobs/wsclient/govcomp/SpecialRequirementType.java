
package indexbg.pjobs.wsclient.govcomp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SpecialRequirementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecialRequirementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="Requirement" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="SgAct" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpecialRequirementType")
public class SpecialRequirementType {

    @XmlAttribute(name = "Requirement")
    protected String requirement;
    @XmlAttribute(name = "SgAct")
    protected String sgAct;

    /**
     * Gets the value of the requirement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequirement() {
        return requirement;
    }

    /**
     * Sets the value of the requirement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequirement(String value) {
        this.requirement = value;
    }

    /**
     * Gets the value of the sgAct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSgAct() {
        return sgAct;
    }

    /**
     * Sets the value of the sgAct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSgAct(String value) {
        this.sgAct = value;
    }

}
