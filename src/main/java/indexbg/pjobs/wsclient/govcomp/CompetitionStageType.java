
package indexbg.pjobs.wsclient.govcomp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompetitionStageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompetitionStageType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Topics" type="{http://iisda.government.bg/Competitions/}TopicsEnum" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Stage" use="required" type="{http://iisda.government.bg/Competitions/}CompetitionStagesEnum" /&gt;
 *       &lt;attribute name="\u0422heme" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompetitionStageType", propOrder = {
    "topics"
})
public class CompetitionStageType {

    @XmlElement(name = "Topics")
    @XmlSchemaType(name = "string")
    protected List<TopicsEnum> topics;
    @XmlAttribute(name = "Stage", required = true)
    protected CompetitionStagesEnum stage;
    @XmlAttribute(name = "\u0422heme")
    protected String \u0442heme;

    /**
     * Gets the value of the topics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the topics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTopics().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TopicsEnum }
     * 
     * 
     */
    public List<TopicsEnum> getTopics() {
        if (topics == null) {
            topics = new ArrayList<TopicsEnum>();
        }
        return this.topics;
    }

    /**
     * Gets the value of the stage property.
     * 
     * @return
     *     possible object is
     *     {@link CompetitionStagesEnum }
     *     
     */
    public CompetitionStagesEnum getStage() {
        return stage;
    }

    /**
     * Sets the value of the stage property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompetitionStagesEnum }
     *     
     */
    public void setStage(CompetitionStagesEnum value) {
        this.stage = value;
    }

    /**
     * Gets the value of the \u0442heme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String get\u0422heme() {
        return \u0442heme;
    }

    /**
     * Sets the value of the \u0442heme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void set\u0422heme(String value) {
        this.\u0442heme = value;
    }

}
