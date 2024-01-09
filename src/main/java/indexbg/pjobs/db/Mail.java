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

import com.indexbg.system.db.PersistentEntity;

@Entity
@Table(name = "jobs_mail")
public class Mail implements PersistentEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6207905144179576288L;

	@SequenceGenerator(name = "Mail", sequenceName = "seq_mail", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Mail")
	@Column(name = "id", nullable = false, precision = 10, scale = 0)
	private Long id;
	
	@Column(name = "id_user", nullable = true, precision = 10, scale = 0)
	private Long idUser;
	
	@Column(name = "administration", nullable = true, precision = 10, scale = 0)
	private Long administration;
	
	@Column(name = "email", nullable = false, length = 400)
	private String email;
	
	@Column(name = "name_lice", length = 400)
	private String nameLice;
	
	@Column(name = "subject", nullable = false, length = 1000)
	private String subject;
	
	@Column(name = "msg",  nullable = false)
	private String msg;	
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_mail", nullable = false, length = 7)
	private Date dateEmail;
	
	@Column(name = "error")
	private String error;	
	
	@Column(name = "user_reg", nullable = true, precision = 10, scale = 0)
	private Long userReg;
	
	@Column(name = "code_object", nullable = true, precision = 10, scale = 0)
	private Long codeObject;
	
	@Column(name = "status", nullable = true, precision = 10, scale = 0)
	private Long status;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "status_date", nullable = false, length = 7)
	private Date statusDate;
	
	public Mail() {
		
	}

	public Mail(Long idUser, Long administration, String email, String nameLice, String subject, String msg, Date dateEmail, String error, Long userReg) {
	
		this.idUser = idUser;
		this.administration = administration;
		this.email = email;
		this.nameLice = nameLice;
		this.subject = subject;
		this.msg = msg;
		this.dateEmail = dateEmail;
		this.error = error;
		this.userReg = userReg;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public Long getAdministration() {
		return administration;
	}

	public void setAdministration(Long administration) {
		this.administration = administration;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNameLice() {
		return nameLice;
	}

	public void setNameLice(String nameLice) {
		this.nameLice = nameLice;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Date getDateEmail() {
		return dateEmail;
	}

	public void setDateEmail(Date dateEmail) {
		this.dateEmail = dateEmail;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}	

	public Long getUserReg() {
		return userReg;
	}

	public void setUserReg(Long userReg) {
		this.userReg = userReg;
	}
	

	public Long getCodeObject() {
		return codeObject;
	}

	public void setCodeObject(Long codeObject) {
		this.codeObject = codeObject;
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
	
	
	@Override
	public Long getCodeMainObject() {
		// TODO Auto-generated method stub
		return null;
	}

}
