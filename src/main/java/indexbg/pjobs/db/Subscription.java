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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.PersistentEntity;

@Entity
@Table(name = "JOBS_SUBSCRIPTION")
public class Subscription implements PersistentEntity {

	static final Logger LOGGER = LoggerFactory.getLogger(Subscription.class);
	
	@SequenceGenerator(name = "Subscriptions", sequenceName = "SEQ_JOBS_SUBSCRIPTION", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Subscriptions")
	@Column(name="ID", nullable = false, precision = 10, scale = 0)
	private Long id;
	
	@Column(name="TYPE", nullable = false, precision = 10, scale = 0)
	private Long type;
	
	@Column(name="ADMINISTRATION", nullable = false, precision = 10, scale = 0)
	private Long administration;
	
	@Column(name="POSITION", nullable = false, precision = 10, scale = 0)
	private Long position;
	
	@Column(name = "professional_field", nullable = false, precision = 10, scale = 0)
	private Long professionalField; // Професионално направление
	
	@Column(name="USER_ID", nullable = false, precision = 10, scale = 0)
	private Long userId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_FROM", nullable = false)
	private Date dateFrom;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_REG", nullable = false)
	private Date dateReg;

	public Subscription() {
		
	}
	
	public Subscription(Long userId) {
		this.userId = userId;
	}
	
	@Override
	public Long getCodeMainObject() {
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public Long getAdministration() {
		return administration;
	}

	public void setAdministration(Long administration) {
		this.administration = administration;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateReg() {
		return dateReg;
	}

	public void setDateReg(Date dateReg) {
		this.dateReg = dateReg;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public Long getProfessionalField() {
		return professionalField;
	}

	public void setProfessionalField(Long professionalField) {
		this.professionalField = professionalField;
	}
}
