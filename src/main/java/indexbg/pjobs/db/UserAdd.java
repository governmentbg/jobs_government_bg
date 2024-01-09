package indexbg.pjobs.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.PersistentEntity;

import indexbg.pjobs.system.Constants;

/**
 * Entity за допълнителните данни на потребител: <br>
 * <ul>
 * 		<li>PIN : ПИН</li>
 * 		<li>APPLY_FOR : Кандидат за</li>
 * 		<li>REGION, MUNICIPALITY, TOWN, ADDRESS : Адрес</li>
 * 		<li>LINKEDIN : Профил в linkedIn</li>
 * 		<li>INTERESTS : Интереси</li>
 * 		<li>HEARING_PROBLEMS : Слухови проблеми</li>
 * 		<li>MOBILITY_PROBLEMS : Двигателни проблеми</li>
 * 		<li>SEARCH_AGREEMENT : Съгласие за участие в търсения</li>
 * 		<li>STATUS, STATUS_DATE : Статус и дата на статус на кандидат за ДСл</li>
 * 		<li>EGN : ЕГН</li>
 * 		<li>NAME, SURNAME, FAMILY : Име, презиме и фамилия</li>
 * 		<li>DYSLEXIA : Дислексия</li>
 * 		<li>CONDITIONS_AGREEMENT: Съгласие с условията на портала</li>
 * </ul>
 * */
@Entity
@Table(name = "JOBS_USERS_ADD")
public class UserAdd implements PersistentEntity {
	
	private static final long serialVersionUID = 6098191379627427887L;

	static final Logger LOGGER = LoggerFactory.getLogger(UserAdd.class);
	
	@SequenceGenerator(name = "UsersAdd", sequenceName = "SEQ_JOBS_USERS_ADD", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersAdd")
	@Column(name = "ID", nullable = false, precision = 10, scale = 0)
	private Long id;
	
	@OneToOne
	@JoinColumn(name="USER_ID", nullable = false)
	private AdmUser user;
	
	@Column(name = "PIN", nullable = false, precision = 10, scale = 0)
	private Long pin;
	
	@Column(name = "APPLY_FOR", nullable = false, precision = 10, scale = 0)
	private Long applyFor;
	
	@Column(name = "REGION", precision = 10, scale = 0)
	private Long region;
	
	@Column(name = "MUNICIPALITY", precision = 10, scale = 0)
	private Long municipality;
	
	@Column(name = "TOWN", precision = 10, scale = 0)
	private Long town;
	
	@Column(name = "ADDRESS", length = 100)
	private String address;
	
	@Column(name = "LINKEDIN", length = 50)
	private String linkedIn;
	
	@Column(name = "INTERESTS", length = 500)
	private String interests;
	
	@Column(name = "HEARING_PROBLEMS", nullable = false)
	private Boolean hearingProblems;
	
	@Column(name = "MOBILITY_PROBLEMS", nullable = false)
	private Boolean mobilityProblems;
	
	@Column(name = "SEARCH_AGREEMENT", nullable = false)
	private Boolean searchAgreement;
	
	@Column(name = "STATUS", nullable = false, precision = 10, scale = 0)
	private Long candidateStatus;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_DATE", nullable = false)
	private Date candidateStatusDate;
	
	@Column(name = "EGN", length = 100)
	private String egn;
	
	@Column(name = "NAME", length = 50, nullable = false)
	private String name;
	
	@Column(name = "SURNAME", length = 50)
	private String surname;
	
	@Column(name = "FAMILY", length = 100, nullable = false)
	private String family;
	
	@Column(name = "dyslexia", nullable = true)
	private Boolean dyslexia;
	
	@Column(name = "CONDITIONS_AGREEMENT", nullable = false)
	private Boolean conditionsAgreement;
	
	
	public UserAdd() {
		
	}
	
	@Override
	public Long getCodeMainObject() {
		
		return Constants.CODE_OBJECTS_USERS;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlTransient
	public AdmUser getUser() {
		return user;
	}

	public void setUser(AdmUser user) {
		this.user = user;
	}

	public Long getPin() {
		return pin;
	}

	public void setPin(Long pin) {
		this.pin = pin;
	}

	public Long getApplyFor() {
		return applyFor;
	}

	public void setApplyFor(Long applyFor) {
		this.applyFor = applyFor;
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

	public String getLinkedIn() {
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn) {
		this.linkedIn = linkedIn;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public Boolean getHearingProblems() {
		return hearingProblems;
	}

	public void setHearingProblems(Boolean hearingProblems) {
		this.hearingProblems = hearingProblems;
	}

	public Boolean getMobilityProblems() {
		return mobilityProblems;
	}

	public void setMobilityProblems(Boolean mobilityProblems) {
		this.mobilityProblems = mobilityProblems;
	}

	public Boolean getSearchAgreement() {
		return searchAgreement;
	}

	public void setSearchAgreement(Boolean searchAgreement) {
		this.searchAgreement = searchAgreement;
	}

	public Long getCandidateStatus() {
		return candidateStatus;
	}

	public void setCandidateStatus(Long candidateStatus) {
		this.candidateStatus = candidateStatus;
	}

	public Date getCandidateStatusDate() {
		return candidateStatusDate;
	}

	public void setCandidateStatusDate(Date candidateStatusDate) {
		this.candidateStatusDate = candidateStatusDate;
	}

	public String getEgn() {
		return egn;
	}

	public void setEgn(String egn) {
		this.egn = egn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public Boolean getDyslexia() {
		return dyslexia;
	}

	public void setDyslexia(Boolean dyslexia) {
		this.dyslexia = dyslexia;
	}

	public Boolean getConditionsAgreement() {
		return conditionsAgreement;
	}

	public void setConditionsAgreement(Boolean conditionsAgreement) {
		this.conditionsAgreement = conditionsAgreement;
	}	
}
