
package indexbg.pjobs.wsclient.govcomp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CompetitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompetitionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SubmissionInfo" type="{http://iisda.government.bg/Competitions/}SubmissionInfoType" minOccurs="0"/&gt;
 *         &lt;element name="WayOfConducting" type="{http://iisda.government.bg/Competitions/}WayOfConductingType" minOccurs="0"/&gt;
 *         &lt;element name="Documents" type="{http://iisda.government.bg/Competitions/}DocumentsType" minOccurs="0"/&gt;
 *         &lt;element name="MinRequirements" type="{http://iisda.government.bg/Competitions/}MinRequirementsType" minOccurs="0"/&gt;
 *         &lt;element name="SpecialRequirements" type="{http://iisda.government.bg/Competitions/}SpecialRequirementType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="CompetitionID" use="required" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="ContractType" use="required" type="{http://iisda.government.bg/Competitions/}ContractTypesEnum" /&gt;
 *       &lt;attribute name="Status" use="required" type="{http://iisda.government.bg/Competitions/}CompetitionStatusesEnum" /&gt;
 *       &lt;attribute name="BatchIdentificationNumber" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="UnitID" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="PoliticalOfficeID" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="PositionID" use="required" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="PositionType" use="required" type="{http://iisda.government.bg/Competitions/}PositionTypesEnum" /&gt;
 *       &lt;attribute name="PositionCode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PositionName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="FreePositions" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="JobInfo" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="AdditionalInfoHowToProceed" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="AdditionalSkillsQualifications" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="WorkPlace" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="Salary" type="{http://www.w3.org/2001/XMLSchema}decimal" /&gt;
 *       &lt;attribute name="HiredNumber" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="ClosedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="UpdatedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompetitionType", propOrder = {
    "submissionInfo",
    "wayOfConducting",
    "documents",
    "minRequirements",
    "specialRequirements"
})
public class CompetitionType {

    @XmlElement(name = "SubmissionInfo")
    protected SubmissionInfoType submissionInfo;
    @XmlElement(name = "WayOfConducting")
    protected WayOfConductingType wayOfConducting;
    @XmlElement(name = "Documents")
    protected DocumentsType documents;
    @XmlElement(name = "MinRequirements")
    protected MinRequirementsType minRequirements;
    @XmlElement(name = "SpecialRequirements")
    protected List<SpecialRequirementType> specialRequirements;
    @XmlAttribute(name = "CompetitionID", required = true)
    protected long competitionID;
    @XmlAttribute(name = "ContractType", required = true)
    protected ContractTypesEnum contractType;
    @XmlAttribute(name = "Status", required = true)
    protected CompetitionStatusesEnum status;
    @XmlAttribute(name = "BatchIdentificationNumber")
    protected String batchIdentificationNumber;
    @XmlAttribute(name = "UnitID")
    protected Long unitID;
    @XmlAttribute(name = "PoliticalOfficeID")
    protected Long politicalOfficeID;
    @XmlAttribute(name = "PositionID", required = true)
    protected long positionID;
    @XmlAttribute(name = "PositionType", required = true)
    protected PositionTypesEnum positionType;
    @XmlAttribute(name = "PositionCode")
    protected String positionCode;
    @XmlAttribute(name = "PositionName")
    protected String positionName;
    @XmlAttribute(name = "FreePositions", required = true)
    protected int freePositions;
    @XmlAttribute(name = "JobInfo")
    protected String jobInfo;
    @XmlAttribute(name = "AdditionalInfoHowToProceed")
    protected String additionalInfoHowToProceed;
    @XmlAttribute(name = "AdditionalSkillsQualifications")
    protected String additionalSkillsQualifications;
    @XmlAttribute(name = "WorkPlace")
    protected String workPlace;
    @XmlAttribute(name = "Salary")
    protected BigDecimal salary;
    @XmlAttribute(name = "HiredNumber")
    protected Integer hiredNumber;
    @XmlAttribute(name = "ClosedOn")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar closedOn;
    @XmlAttribute(name = "UpdatedOn")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar updatedOn;

    /**
     * Gets the value of the submissionInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SubmissionInfoType }
     *     
     */
    public SubmissionInfoType getSubmissionInfo() {
        return submissionInfo;
    }

    /**
     * Sets the value of the submissionInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubmissionInfoType }
     *     
     */
    public void setSubmissionInfo(SubmissionInfoType value) {
        this.submissionInfo = value;
    }

    /**
     * Gets the value of the wayOfConducting property.
     * 
     * @return
     *     possible object is
     *     {@link WayOfConductingType }
     *     
     */
    public WayOfConductingType getWayOfConducting() {
        return wayOfConducting;
    }

    /**
     * Sets the value of the wayOfConducting property.
     * 
     * @param value
     *     allowed object is
     *     {@link WayOfConductingType }
     *     
     */
    public void setWayOfConducting(WayOfConductingType value) {
        this.wayOfConducting = value;
    }

    /**
     * Gets the value of the documents property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentsType }
     *     
     */
    public DocumentsType getDocuments() {
        return documents;
    }

    /**
     * Sets the value of the documents property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentsType }
     *     
     */
    public void setDocuments(DocumentsType value) {
        this.documents = value;
    }

    /**
     * Gets the value of the minRequirements property.
     * 
     * @return
     *     possible object is
     *     {@link MinRequirementsType }
     *     
     */
    public MinRequirementsType getMinRequirements() {
        return minRequirements;
    }

    /**
     * Sets the value of the minRequirements property.
     * 
     * @param value
     *     allowed object is
     *     {@link MinRequirementsType }
     *     
     */
    public void setMinRequirements(MinRequirementsType value) {
        this.minRequirements = value;
    }

    /**
     * Gets the value of the specialRequirements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specialRequirements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialRequirements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecialRequirementType }
     * 
     * 
     */
    public List<SpecialRequirementType> getSpecialRequirements() {
        if (specialRequirements == null) {
            specialRequirements = new ArrayList<SpecialRequirementType>();
        }
        return this.specialRequirements;
    }

    /**
     * Gets the value of the competitionID property.
     * 
     */
    public long getCompetitionID() {
        return competitionID;
    }

    /**
     * Sets the value of the competitionID property.
     * 
     */
    public void setCompetitionID(long value) {
        this.competitionID = value;
    }

    /**
     * Gets the value of the contractType property.
     * 
     * @return
     *     possible object is
     *     {@link ContractTypesEnum }
     *     
     */
    public ContractTypesEnum getContractType() {
        return contractType;
    }

    /**
     * Sets the value of the contractType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContractTypesEnum }
     *     
     */
    public void setContractType(ContractTypesEnum value) {
        this.contractType = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link CompetitionStatusesEnum }
     *     
     */
    public CompetitionStatusesEnum getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompetitionStatusesEnum }
     *     
     */
    public void setStatus(CompetitionStatusesEnum value) {
        this.status = value;
    }

    /**
     * Gets the value of the batchIdentificationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchIdentificationNumber() {
        return batchIdentificationNumber;
    }

    /**
     * Sets the value of the batchIdentificationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchIdentificationNumber(String value) {
        this.batchIdentificationNumber = value;
    }

    /**
     * Gets the value of the unitID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUnitID() {
        return unitID;
    }

    /**
     * Sets the value of the unitID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUnitID(Long value) {
        this.unitID = value;
    }

    /**
     * Gets the value of the politicalOfficeID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPoliticalOfficeID() {
        return politicalOfficeID;
    }

    /**
     * Sets the value of the politicalOfficeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPoliticalOfficeID(Long value) {
        this.politicalOfficeID = value;
    }

    /**
     * Gets the value of the positionID property.
     * 
     */
    public long getPositionID() {
        return positionID;
    }

    /**
     * Sets the value of the positionID property.
     * 
     */
    public void setPositionID(long value) {
        this.positionID = value;
    }

    /**
     * Gets the value of the positionType property.
     * 
     * @return
     *     possible object is
     *     {@link PositionTypesEnum }
     *     
     */
    public PositionTypesEnum getPositionType() {
        return positionType;
    }

    /**
     * Sets the value of the positionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionTypesEnum }
     *     
     */
    public void setPositionType(PositionTypesEnum value) {
        this.positionType = value;
    }

    /**
     * Gets the value of the positionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPositionCode() {
        return positionCode;
    }

    /**
     * Sets the value of the positionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPositionCode(String value) {
        this.positionCode = value;
    }

    /**
     * Gets the value of the positionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * Sets the value of the positionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPositionName(String value) {
        this.positionName = value;
    }

    /**
     * Gets the value of the freePositions property.
     * 
     */
    public int getFreePositions() {
        return freePositions;
    }

    /**
     * Sets the value of the freePositions property.
     * 
     */
    public void setFreePositions(int value) {
        this.freePositions = value;
    }

    /**
     * Gets the value of the jobInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobInfo() {
        return jobInfo;
    }

    /**
     * Sets the value of the jobInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobInfo(String value) {
        this.jobInfo = value;
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

    /**
     * Gets the value of the additionalSkillsQualifications property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalSkillsQualifications() {
        return additionalSkillsQualifications;
    }

    /**
     * Sets the value of the additionalSkillsQualifications property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalSkillsQualifications(String value) {
        this.additionalSkillsQualifications = value;
    }

    /**
     * Gets the value of the workPlace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkPlace() {
        return workPlace;
    }

    /**
     * Sets the value of the workPlace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkPlace(String value) {
        this.workPlace = value;
    }

    /**
     * Gets the value of the salary property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSalary() {
        return salary;
    }

    /**
     * Sets the value of the salary property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSalary(BigDecimal value) {
        this.salary = value;
    }

    /**
     * Gets the value of the hiredNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHiredNumber() {
        return hiredNumber;
    }

    /**
     * Sets the value of the hiredNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHiredNumber(Integer value) {
        this.hiredNumber = value;
    }

    /**
     * Gets the value of the closedOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getClosedOn() {
        return closedOn;
    }

    /**
     * Sets the value of the closedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setClosedOn(XMLGregorianCalendar value) {
        this.closedOn = value;
    }

    /**
     * Gets the value of the updatedOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Sets the value of the updatedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdatedOn(XMLGregorianCalendar value) {
        this.updatedOn = value;
    }

}
