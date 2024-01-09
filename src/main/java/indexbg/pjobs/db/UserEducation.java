package indexbg.pjobs.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.PersistentEntity;

import indexbg.pjobs.system.Constants;

/**
 * Entity за Образование на потребител: <br>
 * <ul>
 * 		<li>USER_ID : ID на потребител</li>
 * 		<li>EDUCATION_TYPE : Тип</li>
 * 		<li>EDUCATION_DEGREE : Степен</li>
 * 		<li>SUBJECT : Специалност</li>
 * 		<li>CATEGORY : Категория (проф. направление)</li>
 * </ul>
 * */
@Entity
@Table(name = "JOBS_USERS_EDUCATION")
public class UserEducation implements PersistentEntity{
	
	private static final long serialVersionUID = 969469062891459622L;

	static final Logger LOGGER = LoggerFactory.getLogger(UserEducation.class);
	
	@SequenceGenerator(name = "UsersEducation", sequenceName = "SEQ_JOBS_USERS_EDUCATION", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersEducation")
	@Column(name = "ID", nullable = false, precision = 10, scale = 0)
	private Long id;
	
	@Column(name = "USER_ID", insertable = false, updatable = false)
	private Long userId;
	
	@Column(name = "EDUCATION_TYPE", nullable = false, precision = 10, scale = 0)
	private Long educationType;
	
	@Column(name = "EDUCATION_DEGREE", nullable = false, precision = 10, scale = 0)
	private Long educationDegree;
	
	@Column(name = "SUBJECT", nullable = false, precision = 10, scale = 0)
	private Long subject;
	
	@Column(name = "CATEGORY", nullable = false, precision = 10, scale = 0)
	private Long category;
	
	public UserEducation() {
		
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEducationType() {
		return educationType;
	}

	public void setEducationType(Long educationType) {
		this.educationType = educationType;
	}

	public Long getEducationDegree() {
		return educationDegree;
	}

	public void setEducationDegree(Long educationDegree) {
		this.educationDegree = educationDegree;
	}

	public Long getSubject() {
		return subject;
	}

	public void setSubject(Long subject) {
		this.subject = subject;
	}

	public Long getCategory() {
		return category;
	}

	public void setCategory(Long category) {
		this.category = category;
	}
	
}
