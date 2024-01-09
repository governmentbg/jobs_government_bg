
package indexbg.pjobs.wsclient.stJobs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JobClassification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JobClassification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClassificationCode" type="{https://eisuchrda.egov.bg/IISDAIntegrationServices/Common}JobClassifierCode" minOccurs="0"/>
 *         &lt;element name="JobCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JobLevelId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="JobLevelCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JobLevelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JobId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="JobName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JObShortName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MinimumQualification" type="{https://eisuchrda.egov.bg/IISDAIntegrationServices/Common}EducationLevels" minOccurs="0"/>
 *         &lt;element name="MinimumRang" type="{http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models}MinimumRang" minOccurs="0"/>
 *         &lt;element name="Category" type="{http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models}ArrayOfCategory" minOccurs="0"/>
 *         &lt;element name="MinimumProfExperience" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServiceType" type="{https://eisuchrda.egov.bg/IISDAIntegrationServices/Common}ContractType" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ParentId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="JobClassificationRowType" type="{https://eisuchrda.egov.bg/IISDAIntegrationServices/Common}JobClassificationRowType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JobClassification", propOrder = {
    "classificationCode",
    "jobCode",
    "jobLevelId",
    "jobLevelCode",
    "jobLevelName",
    "jobId",
    "jobName",
    "jObShortName",
    "minimumQualification",
    "minimumRang",
    "category",
    "minimumProfExperience",
    "serviceType",
    "status",
    "parentId",
    "jobClassificationRowType"
})
public class JobClassification {

    @XmlElement(name = "ClassificationCode")
    protected JobClassifierCode classificationCode;
    @XmlElementRef(name = "JobCode", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jobCode;
    @XmlElementRef(name = "JobLevelId", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> jobLevelId;
    @XmlElementRef(name = "JobLevelCode", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jobLevelCode;
    @XmlElementRef(name = "JobLevelName", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jobLevelName;
    @XmlElementRef(name = "JobId", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> jobId;
    @XmlElementRef(name = "JobName", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jobName;
    @XmlElementRef(name = "JObShortName", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<String> jObShortName;
    @XmlElementRef(name = "MinimumQualification", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<EducationLevels> minimumQualification;
    @XmlElementRef(name = "MinimumRang", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<MinimumRang> minimumRang;
    @XmlElementRef(name = "Category", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfCategory> category;
    @XmlElementRef(name = "MinimumProfExperience", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<String> minimumProfExperience;
    @XmlElementRef(name = "ServiceType", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<ContractType> serviceType;
    @XmlElement(name = "Status")
	public Boolean status;
    @XmlElementRef(name = "ParentId", namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> parentId;
    @XmlElement(name = "JobClassificationRowType")
    protected JobClassificationRowType jobClassificationRowType;

    /**
     * Gets the value of the classificationCode property.
     * 
     * @return
     *     possible object is
     *     {@link JobClassifierCode }
     *     
     */
    public JobClassifierCode getClassificationCode() {
        return classificationCode;
    }

    /**
     * Sets the value of the classificationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobClassifierCode }
     *     
     */
    public void setClassificationCode(JobClassifierCode value) {
        this.classificationCode = value;
    }

    /**
     * Gets the value of the jobCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getJobCode() {
        return jobCode;
    }

    /**
     * Sets the value of the jobCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setJobCode(JAXBElement<String> value) {
        this.jobCode = value;
    }

    /**
     * Gets the value of the jobLevelId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getJobLevelId() {
        return jobLevelId;
    }

    /**
     * Sets the value of the jobLevelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setJobLevelId(JAXBElement<Long> value) {
        this.jobLevelId = value;
    }

    /**
     * Gets the value of the jobLevelCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getJobLevelCode() {
        return jobLevelCode;
    }

    /**
     * Sets the value of the jobLevelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setJobLevelCode(JAXBElement<String> value) {
        this.jobLevelCode = value;
    }

    /**
     * Gets the value of the jobLevelName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getJobLevelName() {
        return jobLevelName;
    }

    /**
     * Sets the value of the jobLevelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setJobLevelName(JAXBElement<String> value) {
        this.jobLevelName = value;
    }

    /**
     * Gets the value of the jobId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getJobId() {
        return jobId;
    }

    /**
     * Sets the value of the jobId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setJobId(JAXBElement<Long> value) {
        this.jobId = value;
    }

    /**
     * Gets the value of the jobName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getJobName() {
        return jobName;
    }

    /**
     * Sets the value of the jobName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setJobName(JAXBElement<String> value) {
        this.jobName = value;
    }

    /**
     * Gets the value of the jObShortName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getJObShortName() {
        return jObShortName;
    }

    /**
     * Sets the value of the jObShortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setJObShortName(JAXBElement<String> value) {
        this.jObShortName = value;
    }

    /**
     * Gets the value of the minimumQualification property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EducationLevels }{@code >}
     *     
     */
    public JAXBElement<EducationLevels> getMinimumQualification() {
        return minimumQualification;
    }

    /**
     * Sets the value of the minimumQualification property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EducationLevels }{@code >}
     *     
     */
    public void setMinimumQualification(JAXBElement<EducationLevels> value) {
        this.minimumQualification = value;
    }

    /**
     * Gets the value of the minimumRang property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link MinimumRang }{@code >}
     *     
     */
    public JAXBElement<MinimumRang> getMinimumRang() {
        return minimumRang;
    }

    /**
     * Sets the value of the minimumRang property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link MinimumRang }{@code >}
     *     
     */
    public void setMinimumRang(JAXBElement<MinimumRang> value) {
        this.minimumRang = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCategory }{@code >}
     *     
     */
    public JAXBElement<ArrayOfCategory> getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCategory }{@code >}
     *     
     */
    public void setCategory(JAXBElement<ArrayOfCategory> value) {
        this.category = value;
    }

    /**
     * Gets the value of the minimumProfExperience property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMinimumProfExperience() {
        return minimumProfExperience;
    }

    /**
     * Sets the value of the minimumProfExperience property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMinimumProfExperience(JAXBElement<String> value) {
        this.minimumProfExperience = value;
    }

    /**
     * Gets the value of the serviceType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ContractType }{@code >}
     *     
     */
    public JAXBElement<ContractType> getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ContractType }{@code >}
     *     
     */
    public void setServiceType(JAXBElement<ContractType> value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStatus(Boolean value) {
        this.status = value;
    }

    /**
     * Gets the value of the parentId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getParentId() {
        return parentId;
    }

    /**
     * Sets the value of the parentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setParentId(JAXBElement<Long> value) {
        this.parentId = value;
    }

    /**
     * Gets the value of the jobClassificationRowType property.
     * 
     * @return
     *     possible object is
     *     {@link JobClassificationRowType }
     *     
     */
    public JobClassificationRowType getJobClassificationRowType() {
        return jobClassificationRowType;
    }

    /**
     * Sets the value of the jobClassificationRowType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobClassificationRowType }
     *     
     */
    public void setJobClassificationRowType(JobClassificationRowType value) {
        this.jobClassificationRowType = value;
    }

}
