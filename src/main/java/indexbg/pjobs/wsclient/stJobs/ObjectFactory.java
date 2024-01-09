
package indexbg.pjobs.wsclient.stJobs;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the indexbg.pjobs.wsclient.stJobs package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _JobClassification_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "JobClassification");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _JobClassificationRowType_QNAME = new QName("https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", "JobClassificationRowType");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _Category_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "Category");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _MinimumRang_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "MinimumRang");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _ArrayOfCategory_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "ArrayOfCategory");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _ArrayOfJobClassification_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "ArrayOfJobClassification");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _JobClassifierCode_QNAME = new QName("https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", "JobClassifierCode");
    private final static QName _EducationLevels_QNAME = new QName("https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", "EducationLevels");
    private final static QName _ContractType_QNAME = new QName("https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", "ContractType");
    private final static QName _MinimumRangMinimumRangName_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "MinimumRangName");
    private final static QName _GetJobsClassificationResponseGetJobsClassificationResult_QNAME = new QName("https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", "GetJobsClassificationResult");
    private final static QName _CategoryCategoryName_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "CategoryName");
    private final static QName _JobClassificationJobName_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "JobName");
    private final static QName _JobClassificationServiceType_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "ServiceType");
    private final static QName _JobClassificationJobCode_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "JobCode");
    private final static QName _JobClassificationMinimumQualification_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "MinimumQualification");
    private final static QName _JobClassificationParentId_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "ParentId");
    private final static QName _JobClassificationJobLevelId_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "JobLevelId");
    private final static QName _JobClassificationJObShortName_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "JObShortName");
    private final static QName _JobClassificationJobLevelCode_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "JobLevelCode");
    private final static QName _JobClassificationJobLevelName_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "JobLevelName");
    private final static QName _JobClassificationMinimumProfExperience_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "MinimumProfExperience");
    private final static QName _JobClassificationJobId_QNAME = new QName("http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", "JobId");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: indexbg.pjobs.wsclient.stJobs
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetJobsClassificationResponse }
     * 
     */
    public GetJobsClassificationResponse createGetJobsClassificationResponse() {
        return new GetJobsClassificationResponse();
    }

    /**
     * Create an instance of {@link ArrayOfJobClassification }
     * 
     */
    public ArrayOfJobClassification createArrayOfJobClassification() {
        return new ArrayOfJobClassification();
    }

    /**
     * Create an instance of {@link GetJobsClassification }
     * 
     */
    public GetJobsClassification createGetJobsClassification() {
        return new GetJobsClassification();
    }

    /**
     * Create an instance of {@link ArrayOfCategory }
     * 
     */
    public ArrayOfCategory createArrayOfCategory() {
        return new ArrayOfCategory();
    }

    /**
     * Create an instance of {@link Category }
     * 
     */
    public Category createCategory() {
        return new Category();
    }

    /**
     * Create an instance of {@link MinimumRang }
     * 
     */
    public MinimumRang createMinimumRang() {
        return new MinimumRang();
    }

    /**
     * Create an instance of {@link JobClassification }
     * 
     */
    public JobClassification createJobClassification() {
        return new JobClassification();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JobClassification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "JobClassification")
    public JAXBElement<JobClassification> createJobClassification(JobClassification value) {
        return new JAXBElement<JobClassification>(_JobClassification_QNAME, JobClassification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JobClassificationRowType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", name = "JobClassificationRowType")
    public JAXBElement<JobClassificationRowType> createJobClassificationRowType(JobClassificationRowType value) {
        return new JAXBElement<JobClassificationRowType>(_JobClassificationRowType_QNAME, JobClassificationRowType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Category }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "Category")
    public JAXBElement<Category> createCategory(Category value) {
        return new JAXBElement<Category>(_Category_QNAME, Category.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MinimumRang }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "MinimumRang")
    public JAXBElement<MinimumRang> createMinimumRang(MinimumRang value) {
        return new JAXBElement<MinimumRang>(_MinimumRang_QNAME, MinimumRang.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "ArrayOfCategory")
    public JAXBElement<ArrayOfCategory> createArrayOfCategory(ArrayOfCategory value) {
        return new JAXBElement<ArrayOfCategory>(_ArrayOfCategory_QNAME, ArrayOfCategory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfJobClassification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "ArrayOfJobClassification")
    public JAXBElement<ArrayOfJobClassification> createArrayOfJobClassification(ArrayOfJobClassification value) {
        return new JAXBElement<ArrayOfJobClassification>(_ArrayOfJobClassification_QNAME, ArrayOfJobClassification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JobClassifierCode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", name = "JobClassifierCode")
    public JAXBElement<JobClassifierCode> createJobClassifierCode(JobClassifierCode value) {
        return new JAXBElement<JobClassifierCode>(_JobClassifierCode_QNAME, JobClassifierCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EducationLevels }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", name = "EducationLevels")
    public JAXBElement<EducationLevels> createEducationLevels(EducationLevels value) {
        return new JAXBElement<EducationLevels>(_EducationLevels_QNAME, EducationLevels.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContractType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", name = "ContractType")
    public JAXBElement<ContractType> createContractType(ContractType value) {
        return new JAXBElement<ContractType>(_ContractType_QNAME, ContractType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "MinimumRangName", scope = MinimumRang.class)
    public JAXBElement<String> createMinimumRangMinimumRangName(String value) {
        return new JAXBElement<String>(_MinimumRangMinimumRangName_QNAME, String.class, MinimumRang.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfJobClassification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", name = "GetJobsClassificationResult", scope = GetJobsClassificationResponse.class)
    public JAXBElement<ArrayOfJobClassification> createGetJobsClassificationResponseGetJobsClassificationResult(ArrayOfJobClassification value) {
        return new JAXBElement<ArrayOfJobClassification>(_GetJobsClassificationResponseGetJobsClassificationResult_QNAME, ArrayOfJobClassification.class, GetJobsClassificationResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "CategoryName", scope = Category.class)
    public JAXBElement<String> createCategoryCategoryName(String value) {
        return new JAXBElement<String>(_CategoryCategoryName_QNAME, String.class, Category.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "JobName", scope = JobClassification.class)
    public JAXBElement<String> createJobClassificationJobName(String value) {
        return new JAXBElement<String>(_JobClassificationJobName_QNAME, String.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContractType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "ServiceType", scope = JobClassification.class)
    public JAXBElement<ContractType> createJobClassificationServiceType(ContractType value) {
        return new JAXBElement<ContractType>(_JobClassificationServiceType_QNAME, ContractType.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "JobCode", scope = JobClassification.class)
    public JAXBElement<String> createJobClassificationJobCode(String value) {
        return new JAXBElement<String>(_JobClassificationJobCode_QNAME, String.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EducationLevels }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "MinimumQualification", scope = JobClassification.class)
    public JAXBElement<EducationLevels> createJobClassificationMinimumQualification(EducationLevels value) {
        return new JAXBElement<EducationLevels>(_JobClassificationMinimumQualification_QNAME, EducationLevels.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "ParentId", scope = JobClassification.class)
    public JAXBElement<Long> createJobClassificationParentId(Long value) {
        return new JAXBElement<Long>(_JobClassificationParentId_QNAME, Long.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "JobLevelId", scope = JobClassification.class)
    public JAXBElement<Long> createJobClassificationJobLevelId(Long value) {
        return new JAXBElement<Long>(_JobClassificationJobLevelId_QNAME, Long.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "JObShortName", scope = JobClassification.class)
    public JAXBElement<String> createJobClassificationJObShortName(String value) {
        return new JAXBElement<String>(_JobClassificationJObShortName_QNAME, String.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "JobLevelCode", scope = JobClassification.class)
    public JAXBElement<String> createJobClassificationJobLevelCode(String value) {
        return new JAXBElement<String>(_JobClassificationJobLevelCode_QNAME, String.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "JobLevelName", scope = JobClassification.class)
    public JAXBElement<String> createJobClassificationJobLevelName(String value) {
        return new JAXBElement<String>(_JobClassificationJobLevelName_QNAME, String.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "Category", scope = JobClassification.class)
    public JAXBElement<ArrayOfCategory> createJobClassificationCategory(ArrayOfCategory value) {
        return new JAXBElement<ArrayOfCategory>(_Category_QNAME, ArrayOfCategory.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "MinimumProfExperience", scope = JobClassification.class)
    public JAXBElement<String> createJobClassificationMinimumProfExperience(String value) {
        return new JAXBElement<String>(_JobClassificationMinimumProfExperience_QNAME, String.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "JobId", scope = JobClassification.class)
    public JAXBElement<Long> createJobClassificationJobId(Long value) {
        return new JAXBElement<Long>(_JobClassificationJobId_QNAME, Long.class, JobClassification.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MinimumRang }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/IisdaIntergrationServices.Models", name = "MinimumRang", scope = JobClassification.class)
    public JAXBElement<MinimumRang> createJobClassificationMinimumRang(MinimumRang value) {
        return new JAXBElement<MinimumRang>(_MinimumRang_QNAME, MinimumRang.class, JobClassification.class, value);
    }

}
