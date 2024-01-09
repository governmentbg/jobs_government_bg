package indexbg.pjobs.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.AuditExt;
import com.indexbg.system.db.JournalAttr;
import com.indexbg.system.db.TrackableEntity;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;

import indexbg.pjobs.system.Constants;

@Entity
@Table(name = "jobs_advertisement")
@XmlRootElement
public class Advertisement extends TrackableEntity implements AuditExt {

	/** Основен java Entity клас на обявите за работа / мобилност
	 * 
	 */
	private static final long serialVersionUID = 6776238744778786512L;

	static final Logger LOGGER = LoggerFactory.getLogger(Advertisement.class);
	
	@SequenceGenerator(name = "Advertisement", sequenceName = "seq_jobs_advertisement", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Advertisement")
	@Column(name = "id", nullable = false, precision = 10, scale = 0)
	private Long id; // id
	
	@Column(name = "adv_type", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "advType", defaultText = "Вид обява" , classifID = "" + Constants.CODE_SYSCLASS_TYPE_ADVERTISEMENT)	
	private Long advType; // Вид обява
	
	@Column(name = "administration", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "administration", defaultText = "Административна структура", classifID = "" + Constants.CODE_SYSCLASS_ADM_REGISTER)
	private Long administration; // Административна структура
	
	@Column(name = "unit", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "unit", defaultText = "Звено", classifID = "" + Constants.CODE_SYSCLASS_ADM_FLAT)
	private Long unit; // Звено
	
	@Column(name = "position", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "position", defaultText = "Длъжност", classifID = "" + Constants.CODE_SYSCLASS_JOBS)
	private Long position; // Длъжност
	
	@Column(name = "description", nullable = false, length = 10000)
	@JournalAttr(label="description", defaultText = "Кратко описание на длъжността по длъжностна характеристика")
	private String description; // Кратко описание на длъжността по длъжностна характеристика
	
	@Column(name = "professional_field", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "professionalField", defaultText = "Професионално направление", classifID = "" + Constants.CODE_SYSCLASS_PROFESSIONAL_FIELD)
	private Long professionalField; // Професионално направление
	
	@Column(name = "mobility_type", precision = 10, scale = 0)
	@JournalAttr(label = "mobilityType", defaultText = "Тип мобилност", classifID = "" + Constants.CODE_SYSCLASS_MOBILITY_TYPE)
	private Long mobilityType; // Тип мобилност
	
	@Column(name = "level", precision = 10, scale = 0)
	@JournalAttr(label = "level", defaultText = "Длъжностно ниво", classifID = "" + Constants.CODE_SYSCLASS_POSITION_LEVEL)
	private Long level; // Длъжностно ниво	
	
	@Column(name = "education_degree", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "educationDegree", defaultText = "Минимална степен на завършено образование", classifID = "" + Constants.CODE_SYSCLASS_EDUCATION_DEGREE)
	private Long educationDegree; // Минимална степен на завършено образование
	
	@Column(name = "experience", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "experience", defaultText = "Минимален професионален опит", classifID = "" + Constants.CODE_SYSCLASS_EXPERIENCE)	
	private Long experience; // Минимален професионален опит
	
	@Column(name = "rank", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "rank", defaultText = "Ранг", classifID = "" + Constants.CODE_SYSCLASS_RANK)	
	private Long rank; // Ранг
	
	@Column(name = "requirements", length = 10000)
	@JournalAttr(label="requirements", defaultText = "Специфични изисквания за длъжността")
	private String requirements; //  Специфични изисквания за длъжността
	
	@Column(name = "salary", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label="salary", defaultText = "Минимален размер на основната заплата")
	private Long salary; // Минимален размер на основната заплата
		
	@Column(name = "num", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label="num", defaultText = "Брой места")
	private Long num; // Брой места
	
	@Column(name = "region", precision = 10, scale = 0)
	@JournalAttr(label = "region", defaultText = "Област", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long region; // Област
	
	@Column(name = "municipality", precision = 10, scale = 0)
	@JournalAttr(label = "municipality", defaultText = "Община", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long municipality; // Община
	
	@Column(name = "town", precision = 10, scale = 0)
	@JournalAttr(label = "town", defaultText = "Населено място", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long town; // Населено място
	
	@Column(name = "address", length = 100)
	@JournalAttr(label="address", defaultText = "Адрес")
	private String address; // Квартал, Улица, №, вх. ет. ап. 
	
	@Column(name = "adm_unit", precision = 10, scale = 0)
	@JournalAttr(label = "admUnit", defaultText = "Административно звено", classifID = "" + Constants.CODE_SYSCLASS_ADM_FLAT)
	private Long admUnit; // Административно звено
	
	@Column(name = "contact_person", length = 255)
	@JournalAttr(label="contactPerson", defaultText = "Лице за контакт - име")
	private String contactPerson; // Лице за контакт - име

	@Column(name = "contact_phone", length = 50)
	@JournalAttr(label="contactPhone", defaultText = "Лице за контакт - телефон")
	private String contactPhone; // Лице за контакт - телефон
	
	@Column(name = "contact_email", length = 100)
	@JournalAttr(label="contactEmail", defaultText = "Адрес на електронна поща за подаване на документи")
	private String contactEmail; // Адрес на електронна поща за подаване на документи
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_from", nullable = false, length = 7)
	@JournalAttr(label = "dateFrom", defaultText = "Дата на публикуване", dateMask = "dd.MM.yyyy")
	private Date dateFrom; // Дата на публикуване
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_to", nullable = false, length = 7)
	@JournalAttr(label = "dateTo", defaultText = "Краен срок за подаване на документи", dateMask = "dd.MM.yyyy")
	private Date dateTo; // Краен срок за подаване на документи	
	
	@Column(name = "add_info", length = 10000)
	@JournalAttr(label="addInfo", defaultText = "Допълнителна информация")
	private String addInfo; // Допълнителна информация
	
	@Column(name="status", nullable = false, precision = 10, scale = 0)	
	private Long status; // Статус
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date", nullable = false)
	private Date statusDate; // Дата на статуса
	
	@Column(name = "location_region", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "locationRegion", defaultText = "Област на локацията на звеното", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long locationRegion; // Област на локацията на звеното
	
	@Column(name = "location_municipality", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "locationMunicipality", defaultText = "Община на локацията на звеното", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long locationMunicipality; // Община на локацията на звеното
	
	@Column(name = "location_town", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "locationTown", defaultText = "Населено място на локацията на звеното", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long locationTown; // Населено място на локацията на звеното
	
	@Column(name = "location_email", length = 255)
	@JournalAttr(label="locationEmail", defaultText = "Адрес на електронна поща за подаване на документи")
	private String locationEmail; // Адрес на електронна поща за подаване на документи
	
	@Column(name = "add_info_new", length = 10000)
	@JournalAttr(label="addInfoNew", defaultText = "Допълнителни изисквания за заемане на длъжността")
	private String addInfoNew; // Допълнителни изисквания за заемане на длъжността 
	
	@Column(name = "selection_mode", length = 5000)
	@JournalAttr(label="selectionMode", defaultText = "Начин за провеждане на подбора ")
	private String selectionMode; // Начин за провеждане на подбора 
	
	/** default constructor */
	public Advertisement() {
		
	}	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getAdvType() {
		return advType;
	}

	public void setAdvType(Long advType) {
		this.advType = advType;
	}

	public Long getAdministration() {
		return administration;
	}

	public void setAdministration(Long administration) {
		this.administration = administration;
	}

	public Long getUnit() {
		return unit;
	}

	public void setUnit(Long unit) {
		this.unit = unit;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getProfessionalField() {
		return professionalField;
	}

	public void setProfessionalField(Long professionalField) {
		this.professionalField = professionalField;
	}

	public Long getMobilityType() {
		return mobilityType;
	}

	public void setMobilityType(Long mobilityType) {
		this.mobilityType = mobilityType;
	}

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getEducationDegree() {
		return educationDegree;
	}

	public void setEducationDegree(Long educationDegree) {
		this.educationDegree = educationDegree;
	}

	public Long getExperience() {
		return experience;
	}

	public void setExperience(Long experience) {
		this.experience = experience;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}

	public Long getSalary() {
		return salary;
	}

	public void setSalary(Long salary) {
		this.salary = salary;
	}
	
	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	public Long getRegion() {
		return region;
	}

	public void setRegion(Long region) {
		this.region = region;
	}

	public Long getMunicipality() {
		return municipality;
	}

	public void setMunicipality(Long municipality) {
		this.municipality = municipality;
	}

	public Long getTown() {
		return town;
	}

	public void setTown(Long town) {
		this.town = town;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getAdmUnit() {
		return admUnit;
	}

	public void setAdmUnit(Long admUnit) {
		this.admUnit = admUnit;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	
	public String getAddInfo() {
		return addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Long getLocationRegion() {
		return locationRegion;
	}

	public void setLocationRegion(Long locationRegion) {
		this.locationRegion = locationRegion;
	}

	public Long getLocationMunicipality() {
		return locationMunicipality;
	}

	public void setLocationMunicipality(Long locationMunicipality) {
		this.locationMunicipality = locationMunicipality;
	}

	public Long getLocationTown() {
		return locationTown;
	}

	public void setLocationTown(Long locationTown) {
		this.locationTown = locationTown;
	}

	@Override
	public Long getUserReg() {
		// TODO Auto-generated method stub
		return super.getUserReg();
	}
	
	@Override
	public Date getDateReg() {
		// TODO Auto-generated method stub
		return super.getDateReg();
	}
	
	@Override
	public Long getUserLastMod() {
		// TODO Auto-generated method stub
		return super.getUserLastMod();
	}
	
	@Override
	public Date getDateLastMod() {
		// TODO Auto-generated method stub
		return super.getDateLastMod();
	}
	
	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECT_ADVERTISEMENT; 
	}

	public String getIdentInfo() {
		return "Обява за мобилност: " + this.id;
	}

	public String getLocationEmail() {
		return locationEmail;
	}

	public void setLocationEmail(String locationEmail) {
		this.locationEmail = locationEmail;
	}

	public String getAddInfoNew() {
		return addInfoNew;
	}

	public void setAddInfoNew(String addInfoNew) {
		this.addInfoNew = addInfoNew;
	}

	public String getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}
	
	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal sj = new  SystemJournal();				
		sj.setCodeObject(getCodeMainObject());
		sj.setIdObject(getId());
		sj.setIdentObject(getIdentInfo());
		
		return sj;
	}
}
