package indexbg.pjobs.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.AuditExt;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.JournalAttr;
import com.indexbg.system.db.PersistentEntity;
import com.indexbg.system.db.TrackableEntity;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;

import indexbg.pjobs.db.dao.AdmGroupsDAO;
import indexbg.pjobs.system.Constants;

@Entity
@Table(name = "ADM_USERS")
@XmlRootElement
public class AdmUsers  extends TrackableEntity implements Cloneable, AuditExt {
	
	/** */
	private static final long serialVersionUID = -545631164407428931L;
	static final Logger LOGGER = LoggerFactory.getLogger(AdmUsers.class);
	
	// properties for AdmUsers
	@SequenceGenerator(name = "AdmUsers", sequenceName = "SEQ_ADM_USERS", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AdmUsers")
	@Column(name = "USER_ID", nullable=false, precision = 10, scale = 0)
	private Long id;                      // id
	
	@Column(name = "USERNAME", nullable=false, length = 50)
	@JournalAttr(label = "username", defaultText = "Потребителско име")
	private String username;              // logid
	
	@Column(name = "PASSWORD", nullable=false, length = 50)
	private String password;              // pass
	
	@Column(name = "USER_TYPE", nullable=false, precision = 10, scale = 0)
	@JournalAttr(label = "userType", defaultText = "Тип потребител", classifID = "" + Constants.CODE_CLASSIF_TIP_POTREB)
	private Long userType;                 // Тип на потребител
	
	@Column(name = "NAMES", length = 255)
	@JournalAttr(label = "names", defaultText = "Имена")
	private String  names;                  // Трите имена
	
	@Column(name = "LANG", nullable=false, precision = 10, scale = 0)
	@JournalAttr(label = "lang", defaultText = "Език", classifID = "" + Constants.CODE_CLASSIF_LANG)
	private Long lang;                         // Език за работа  
	
	@Column(name = "EMAIL",  length = 100)
	@JournalAttr(label = "email", defaultText = "Електронна поща")
	private String email;                      // e-mail
	
	@Column(name = "PHONE",  length = 50)
	@JournalAttr(label = "phone", defaultText = "Телефон")
	private String phone;                    // Тел. / факс
	
	@Column(name = "STATUS", precision = 10, scale = 0)
	@JournalAttr(label = "status", defaultText = "Статус", classifID = "" + Constants.CODE_CLASSIF_STATUS_POTREB)
	private Long status;                      // Статус - актуален / не
	
	@Temporal(TemporalType.DATE)
	@Column(name = "STATUS_DATE", length = 7)
	@JournalAttr(label = "statusDate", defaultText = "Дата на статус")
	private Date statusDate;              // Дата на установяване на статуса неактуален
	
	@Column(name = "STATUS_EXPLAIN",  length = 255)
	@JournalAttr(label = "statusExplain", defaultText = "Статус - доп.информация")
	private String statusExplain;            // Причини за промяна на статуса
	
	@Column(name = "ORG_ID", precision = 10, scale = 0)
	@JournalAttr(label = "orgId", defaultText = "ИД на организация")
	private Long orgId;                 // Организация
	
	@Column(name = "ORG_CODE", precision = 10, scale = 0)
	private Long orgCode;            // от Административната структура
	
	@Column(name = "ORG_TEXT",  length = 255)
	@JournalAttr(label = "orgText", defaultText = "Име на организация")
	private String orgText;            // Име на организация
	
	@Column(name = "POSITION", precision = 10, scale = 0)
	@JournalAttr(label = "position", defaultText = "Длъжност", classifID = "" + Constants.CODE_CLASSIF_DLAJNOSTI)
	private Long position;             // Длъжност
	
	@Column(name = "POSITION_TEXT",  length = 255)
	@JournalAttr(label = "positionText", defaultText = "Наименование на длъжност")
	private String positionText;     // Наименование на длъжност
	
	@Column(name = "MAIL_FORWARD", precision = 1, scale = 0)
	private Long  mailForward;
	
	@Column(name = "MAIL_USER",  length = 50)
	private String mailUser;
	
	@Column(name = "MAIL_PASS",  length = 50)
	private String mailPass;
	
	@Column(name = "MAIL_EMAIL",  length = 100)
	private String mailEmail;
	
	@Column(name = "MAIL_IMAP_SERVER",  length = 50)
	private String mailImapServer;
	
	@Column(name = "MAIL_SMTP_SERVER",  length = 50)
	private String mailSmtpServer;
	
	// Брой опити за влизане	
	@Column(name = "LOGIN_ATTEMPTS", precision = 10, scale = 0)
	@JournalAttr(label = "loginAttempts", defaultText = "Брой опити")
	private Long loginAttempts;             
	
	// Дата на последна смяна на парола	
	@Column(name = "PASS_LAST_CHANGE", length=7)
	@JournalAttr(label = "passLastChange", defaultText = "Дата на последна смяна на парола")
	private Date passLastChange;             
	
	@Column(name = "PASS_IS_NEW",  precision = 10, scale = 0)
	private Long passIsNew;            
	
	@Column(name = "ORG_ZVENO", precision = 10, scale = 0)
	@JournalAttr(label = "zveno", defaultText = "Организационно звено", classifID = "" + Constants.CODE_SYSCLASS_ADM_FLAT)
	private Long zveno;            // Организационно звено - от Административната структура
	
	/** Зададени роли на потребителя */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JournalAttr(label = "userRoles", defaultText = "Право")
	@JoinColumn(name="USER_ID", nullable = false, referencedColumnName = "USER_ID", insertable = true, updatable = true)
	private List<AdmUserRoles> userRoles;

	/** Зададени групи на потребителя */
	@ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinTable(name="ADM_USER_GROUP",
		 joinColumns= { @JoinColumn(name="USER_ID", referencedColumnName="USER_ID", nullable = false, updatable = false) },
         inverseJoinColumns= { @JoinColumn(name="GROUP_ID", referencedColumnName="GROUP_ID", nullable = false, updatable = false)  } )
	private List<AdmGroups> userGroups;	
	
	@Transient
	@JournalAttr(label = "xmlGroups", defaultText = "Групи", codeObject = (int) Constants.CODE_OBJECTS_GROUPUSER)
	private List<Long> xmlGroups;
	
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

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public Long getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(Long orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgText() {
		return orgText;
	}

	public void setOrgText(String orgText) {
		this.orgText = orgText;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public String getPositionText() {
		return positionText;
	}

	public void setPositionText(String positionText) {
		this.positionText = positionText;
	}

	public Long getMailForward() {
		return mailForward;
	}

	public void setMailForward(Long mailForward) {
		this.mailForward = mailForward;
	}

	public String getMailUser() {
		return mailUser;
	}

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public String getMailPass() {
		return mailPass;
	}

	public void setMailPass(String mailPass) {
		this.mailPass = mailPass;
	}

	public String getMailEmail() {
		return mailEmail;
	}

	public void setMailEmail(String mailEmail) {
		this.mailEmail = mailEmail;
	}

	public String getMailImapServer() {
		return mailImapServer;
	}

	public void setMailImapServer(String mailImapServer) {
		this.mailImapServer = mailImapServer;
	}

	public String getMailSmtpServer() {
		return mailSmtpServer;
	}

	public void setMailSmtpServer(String mailSmtpServer) {
		this.mailSmtpServer = mailSmtpServer;
	}
	
	public List<AdmUserRoles> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<AdmUserRoles> userRoles) {
		this.userRoles = userRoles;
	}
	
	@XmlTransient
	public List<AdmGroups> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<AdmGroups> userGroups) {
		this.userGroups = userGroups;
	}	

	public List<Long> getXmlGroups() {
		this.xmlGroups = null;
		if (this.userGroups != null && !this.userGroups.isEmpty()) {
			this.xmlGroups = new ArrayList<>();

			for (AdmGroups group : this.userGroups) {
				this.xmlGroups.add(group.getId());
			}
		}
		return this.xmlGroups;
	}


	public void setXmlGroups(List<Long> xmlGroups) {
		this.xmlGroups = xmlGroups;
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
		return Constants.CODE_OBJECTS_USERS;
	}
	
	

	/**
	 * Връща текущите стойности за колекция "Роли" за конкретна  класификация.
	 * 
	 * @return Роли
	 */
	public ArrayList<Long> getSelectedRoles(Long codeClassif) {
           if (codeClassif == null)  return null;
		ArrayList<Long> rez = new ArrayList<Long>();
	
		if (this.userRoles != null) {
			for (AdmUserRoles role : this.userRoles) {
				if (role.getCodeClassif() != null && role.getCodeClassif().longValue() == codeClassif.longValue())
				    rez.add(role.getCodeRole());
			}
		}

		return rez;
	}

	/**
	 * Зарежда  текущите стойности на колекция "Роли" за конкретна класификация.
	 * 
	 *  
	 */
	public void setSelectedRoles(ArrayList<Long> roles, Long codeClassif) {

		if (this.getUserRoles() == null)
			this.setUserRoles(new ArrayList<AdmUserRoles>());
		else {
			// Първо изтриване на всички въведени роли за класификацията
		 	List <AdmUserRoles> rolsN = new ArrayList<AdmUserRoles> ();
			for (AdmUserRoles rol: this.getUserRoles()) {
				if (rol.getCodeClassif() != null && rol.getCodeClassif().longValue() == codeClassif.longValue() )
					continue;
				rolsN.add(rol);
			}	
			 this.getUserRoles().clear();
			 this.getUserRoles().addAll(rolsN);
		
		}

		if (roles != null && !roles.isEmpty()) {
			for (Long code : roles) {
				AdmUserRoles ur = new  AdmUserRoles();
				ur.setCodeClassif(codeClassif);
				ur.setCodeRole(code);
				this.getUserRoles().add(ur);
			}
		}
	}

	/**
	 * Връща текущите стойности за колекция "Групи на потребителя".
	 * 
	 * @return Групи
	 */
	public ArrayList<String> getSelectedGroups() {

		ArrayList<String> rez = new ArrayList<String>();
		if (this.userGroups != null) {
			for (AdmGroups group : this.userGroups) {
				rez.add(group.getId().toString());
			}
		}

		return rez;
	}

	/**
	 * Зарежда текущите  стойности  за  колекция "Групи на потребителя".
	 * 
	 *  
	 */
	public void setSelectedGroups(ArrayList<String> groups) {

		if (this.getUserGroups() == null)
			this.setUserGroups(new ArrayList<AdmGroups>());

		this.getUserGroups().clear();
		if (groups != null && !groups.isEmpty()) {
			for (String id_group : groups) {
				AdmGroups gr = null;
				try {

					gr = new AdmGroupsDAO(Long.valueOf(-1)).findById(Long.valueOf(id_group));

				} catch (DbErrorException e) {
					LOGGER.error("Грешка при четене данни за група! " + " - " + e.getMessage(), e);
					gr = null;

				} catch (Exception e) {
					LOGGER.error(
							"Грешка при работа със системата при четене данни за група!!!" + " - " + e.getMessage(), e);
					gr = null;
				} finally {
					JPA.getUtil().closeConnection();
				}

				if (gr != null)
					this.getUserGroups().add(gr);
			}
		}
	}

	public Long getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Long loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public Date getPassLastChange() {
		return passLastChange;
	}

	public void setPassLastChange(Date passLastChange) {
		this.passLastChange = passLastChange;
	}

	public Long getPassIsNew() {
		return passIsNew;
	}

	public void setPassIsNew(Long passIsNew) {
		this.passIsNew = passIsNew;
	}

	public Boolean getPassIsNewBoolean() {
		if (passIsNew!=null && passIsNew.equals(Constants.CODE_ZNACHENIE_DA)) {
			return true;
		}else{
			return false;
		}
	}

	public void setPassIsNewBoolean(Boolean passIsNewBoolean) {
		if (passIsNewBoolean) {
			this.passIsNew=Constants.CODE_ZNACHENIE_DA;
		}else{
			this.passIsNew=Constants.CODE_ZNACHENIE_NE;
		}
	}

	public Long getZveno() {
		return zveno;
	}

	public void setZveno(Long zveno) {
		this.zveno = zveno;
	}
	
	/** */
	@Override
	public AdmUsers clone() throws CloneNotSupportedException {
		return (AdmUsers) super.clone();
	}
	
	/** */
	@Override
	public PersistentEntity toAuditSerializeObject(String unitName) {
		AdmUsers obj;
		try {
			obj = clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}

		JPA jpa = JPA.getUtil(unitName);

		obj.userRoles = this.userRoles != null && jpa.isLoaded(this.userRoles) ? new ArrayList<>(this.userRoles) : null;
		obj.userGroups = this.userGroups != null && jpa.isLoaded(this.userGroups) ? new ArrayList<>(this.userGroups) : null;

		return obj;
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