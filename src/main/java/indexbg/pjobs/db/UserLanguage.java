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
 * Entity за Чужд език: <br>
 * <ul>
 * 		<li>USER_ID : ID на потребител</li>
 * 		<li>LANGUAGE : Език</li>
 * 		<li>LEVEL : Ниво</li>
 * </ul>
 * */
@Entity
@Table(name = "JOBS_USERS_LANGUAGES")
public class UserLanguage implements PersistentEntity{

	private static final long serialVersionUID = -5532731927945377773L;

	static final Logger LOGGER = LoggerFactory.getLogger(UserLanguage.class);
	
	@SequenceGenerator(name = "UsersLanguage", sequenceName = "SEQ_JOBS_USERS_LANGUAGES", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersLanguage")
	@Column(name = "ID", nullable = false, precision = 10, scale = 0)
	private Long id;
	
	@Column(name = "USER_ID", insertable = false, updatable = false)
	private Long userId;
	
	@Column(name = "LANGUAGE", nullable = false, precision = 10, scale = 0)
	private Long language;
	
	@Column(name = "LEVEL", nullable = false, precision = 10, scale = 0)
	private Long level;
	
	public UserLanguage() {
		
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

	public Long getLanguage() {
		return language;
	}

	public void setLanguage(Long language) {
		this.language = language;
	}

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

}
