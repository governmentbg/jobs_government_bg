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
 * Entity за Професионален опит: <br>
 * <ul>
 * 		<li>USER_ID : ID на потребител</li>
 * 		<li>POSITION : Длъжност</li>
 * 		<li>YEARS : Брой години</li>
 * 		<li>ADD_INFO : Описание</li>
 * </ul>
 * */
@Entity
@Table(name = "JOBS_USERS_EXPERIENCE")
public class UserExperience implements PersistentEntity{
	
	private static final long serialVersionUID = 4645621841135869695L;

	static final Logger LOGGER = LoggerFactory.getLogger(UserExperience.class);
	
	@SequenceGenerator(name = "UsersExperience", sequenceName = "SEQ_JOBS_USERS_EXPERIENCE", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersExperience")
	@Column(name = "ID", nullable = false, precision = 10, scale = 0)
	private Long id;
	
	@Column(name = "USER_ID", insertable = false, updatable = false)
	private Long userId;
	
	@Column(name = "POSITION", nullable = false, precision = 10, scale = 0)
	private Long position;
	
	@Column(name = "YEARS", nullable = false, precision = 2, scale = 0)
	private Integer years;
	
	@Column(name = "ADD_INFO", nullable = true, length = 500)
	private String addInfo;
	
	public UserExperience() {
		
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

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

	public String getAddInfo() {
		return addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}
	
}
