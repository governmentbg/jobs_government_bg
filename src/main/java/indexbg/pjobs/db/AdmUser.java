package indexbg.pjobs.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.TrackableEntity;

import indexbg.pjobs.system.Constants;

/**
 * Entity за данни на външен потребител на портала: <br>
 * <ul>
 * 		<li>PASSWORD: Парола</li>
 * 		<li>NAMES: Имена</li>
 * 		<li>EMAIL: Имейл</li>
 * 		<li>PHONE: Телефон</li>
 * 		<li>STATUS, STATUS_DATE: Статус и дата на статус на кандидат</li>
 * 		<li>STATUS_EXPLAIN: Причина за статуса</li>
 * </ul>
 * */
@Entity
@Table(name = "ADM_USERS")
public class AdmUser extends TrackableEntity {
	
	private static final long serialVersionUID = 7261592675614554470L;

	static final Logger LOGGER = LoggerFactory.getLogger(AdmUser.class);
	
	@SequenceGenerator(name = "Users", sequenceName = "SEQ_ADM_USERS", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Users")
	@Column(name = "USER_ID", nullable = false, precision = 10, scale = 0)
	private Long id;
	
	@Column(name="USERNAME", length = 50, nullable = false)
	private String username;
	
	@Column(name="PASSWORD", length = 50, nullable = false)
	private String password;
	
	@Column(name="USER_TYPE", nullable = false, precision = 10, scale = 0)
	private Long userType;
	
	@Column(name="NAMES", length = 255, nullable = false)
	private String names;
	
	@Column(name="LANG", nullable = false, precision = 10, scale = 0)
	private Long lang;
	
	@Column(name="EMAIL", length = 100)
	private String email;
	
	@Column(name="PHONE", length = 50)
	private String phone;
	
	@Column(name="STATUS", nullable = false, precision = 10, scale = 0)
	private Long status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="STATUS_DATE", nullable = false)
	private Date statusDate;
	
	@Column(name="STATUS_EXPLAIN", length = 255)
	private String statusExplain;
	
	@Column(name="PASS_IS_NEW", precision = 1, scale = 0)
	private int passNew;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private UserAdd userAdd;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private UserStudent userStudent;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "USER_ID", nullable = false, referencedColumnName = "USER_ID", insertable = true, updatable = true)
    private List<UserEducation> userEducation = new ArrayList<UserEducation>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "USER_ID", nullable = false, referencedColumnName = "USER_ID", insertable = true, updatable = true)
    private List<UserExperience> userExperience = new ArrayList<UserExperience>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "USER_ID", nullable = false, referencedColumnName = "USER_ID", insertable = true, updatable = true)
    private List<UserLanguage> userLanguages = new ArrayList<UserLanguage>();
	
	public AdmUser() {
		 
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getUserType() {
		return userType;
	}

	public void setUserType(Long userType) {
		this.userType = userType;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}
	
	public Long getLang() {
		return lang;
	}

	public void setLang(Long lang) {
		this.lang = lang;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getStatusExplain() {
		return statusExplain;
	}

	public void setStatusExplain(String statusExplain) {
		this.statusExplain = statusExplain;
	}

	public int getPassNew() {
		return passNew;
	}

	public void setPassNew(int passNew) {
		this.passNew = passNew;
	}

	public UserAdd getUserAdd() {
		return userAdd;
	}

	public void setUserAdd(UserAdd userAdd) {
		this.userAdd = userAdd;
		if(userAdd != null) {
			userAdd.setUser(this);
		}
	}

	public UserStudent getUserStudent() {
		return userStudent;
	}

	public void setUserStudent(UserStudent userStudent) {
		this.userStudent = userStudent;
		if(userStudent != null) {
			userStudent.setUser(this);
		}
	}
	
	public List<UserEducation> getUserEducation() {
		return userEducation;
	}

	public void setUserEducation(List<UserEducation> userEducation) {
		this.userEducation = userEducation;
	}
	
	public void addEducation(UserEducation education) {
		this.userEducation.add(education);
		
	}
	
	public List<UserExperience> getUserExperience() {
		return userExperience;
	}

	public void setUserExperience(List<UserExperience> userExperience) {
		this.userExperience = userExperience;
	}
	
	public void addUserExperience(UserExperience experience) {
		this.userExperience.add(experience);
	}

	public List<UserLanguage> getUserLanguages() {
		return userLanguages;
	}

	public void setUserLanguages(List<UserLanguage> userLanguages) {
		this.userLanguages = userLanguages;
	}

	public void addLanguage(UserLanguage language) {
		this.userLanguages.add(language);
	}
}
