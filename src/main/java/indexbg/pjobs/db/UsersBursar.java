package indexbg.pjobs.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.TrackableEntity;

import indexbg.pjobs.system.Constants;



@Entity
@Table(name = "jobs_users_bursar")
public class UsersBursar extends TrackableEntity {



	private static final long serialVersionUID = 893283041651962652L;


	static final Logger LOGGER = LoggerFactory.getLogger(UsersBursar.class);
	
	@SequenceGenerator(name = "UsersBursar", sequenceName = "seq_jobs_users_burser", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersBursar")
	@Column(name = "id", nullable=false, precision = 10, scale = 0)
	private Long id; 
	
	@Column(name="user_id", nullable=false, precision = 10, scale = 0)	
	private Long userId; 	
	
	@Column(name="bursary_term",nullable = false , precision = 4, scale = 0)	
	private Long bursaryTerm; 
	
	@Column(name="graduation_term", nullable = false ,precision = 4, scale = 0)	
	private Long graduationTerm; 	
	
	@Column(name="test_term", nullable = true,precision = 4, scale = 0)	
	private Long testTerm; 	
	
	@Column(name="job_term", nullable = false,precision = 4, scale = 0)	
	private Long jobTerm; 	
	
	@Column(name="administration", nullable = false)	
	private Long administration; 	
		

	public UsersBursar () {
		
	}
		
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdentInfo() {
		return "";
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getBursaryTerm() {
		return bursaryTerm;
	}

	public void setBursaryTerm(Long bursaryTerm) {
		this.bursaryTerm = bursaryTerm;
	}

	public Long getGraduationTerm() {
		return graduationTerm;
	}

	public void setGraduationTerm(Long graduationTerm) {
		this.graduationTerm = graduationTerm;
	}

	public Long getTestTerm() {
		return testTerm;
	}

	public void setTestTerm(Long testTerm) {
		this.testTerm = testTerm;
	}

	public Long getJobTerm() {
		return jobTerm;
	}

	public void setJobTerm(Long jobTerm) {
		this.jobTerm = jobTerm;
	}
	
	public Long getAdministration() {
		return administration;
	}

	public void setAdministration(Long administration) {
		this.administration = administration;
	}

	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECT_BURSAR;
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
