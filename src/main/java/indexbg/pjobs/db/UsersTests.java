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

import com.indexbg.system.db.TrackableEntity;

import indexbg.pjobs.system.Constants;

/**
 * Entity за тестове на потребител: 
 * */

@Entity
@Table(name = "jobs_users_tests")
public class UsersTests extends TrackableEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1689271303804558859L;

	static final Logger LOGGER = LoggerFactory.getLogger(UsersTests.class);
	
	@SequenceGenerator(name = "UsersTests", sequenceName = "seq_jobs_users_tests", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersTests")
	@Column(name = "id", nullable = false, precision = 10, scale = 0)
	private Long id;
	

	@Column(name="user_id", nullable = false, precision = 10, scale = 0)
	private Long userId;
	
	@Column(name = "test_level", nullable = false, precision = 10, scale = 0)
	private Long testLevel;
	
	@Column(name = "test_location", nullable = false, precision = 10, scale = 0)
	private Long testLocation;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "test_date", nullable = false)
	private Date testDate;
	
	@Temporal(TemporalType.TIME)
	@Column(name = "test_time", nullable = false)
	private Date testТime;
	
	@Column(name = "administration", nullable = false, precision = 10, scale = 0)
	private Long administration;
	
	@Column(name = "test_group_id", nullable = false, precision = 10, scale = 0)
	private Long testGroupId;
	
	@Column(name = "status", nullable = false, precision = 10, scale = 0)
	private Long status;
	
	@Column(name="deactiv_reason",  length= 1000)	
	private String deactivReason; 
	
	@Column(name="administration_name",  length= 1000)	
	private String administrationName; 
	
	@Column(name="deactiv_code", precision = 10, scale = 0)	
	private Long deactivCode; 		

	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECT_USERS_TESTS;
	}
	
	public UsersTests() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTestLevel() {
		return testLevel;
	}

	public void setTestLevel(Long testLevel) {
		this.testLevel = testLevel;
	}

	public Long getTestLocation() {
		return testLocation;
	}

	public void setTestLocation(Long testLocation) {
		this.testLocation = testLocation;
	}

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	public Date getTestТime() {
		return testТime;
	}

	public void setTestТime(Date testТime) {
		this.testТime = testТime;
	}

	public Long getAdministration() {
		return administration;
	}

	public void setAdministration(Long administration) {
		this.administration = administration;
	}

	public Long getTestGroupId() {
		return testGroupId;
	}

	public void setTestGroupId(Long testGroupId) {
		this.testGroupId = testGroupId;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getDeactivReason() {
		return deactivReason;
	}

	public void setDeactivReason(String deactivReason) {
		this.deactivReason = deactivReason;
	}
	
	public String getAdministrationName() {
		return administrationName;
	}

	public void setAdministrationName(String administrationName) {
		this.administrationName = administrationName;
	}

	public Long getDeactivCode() {
		return deactivCode;
	}

	public void setDeactivCode(Long deactivCode) {
		this.deactivCode = deactivCode;
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

}
