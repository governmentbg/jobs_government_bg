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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.PersistentEntity;

import indexbg.pjobs.system.Constants;

/**
 * Entity за Данни за студент: <br>
 * <ul>
 * 		<li>USER_ID : ID на потребител</li>
 * 		<li>EDUCATION_DEGREE : Образователно-квалификационна степен</li>
 * 		<li>COURSE : Завършен курс към момента</li>
 * 		<li>SUBJECT : Специалност</li>
 * 		<li>STATUS : Статус на студент</li>
 * 		<li>STATUS_DATE : Дата на статус на студент</li>
 * </ul>
 * */
@Entity
@Table(name = "JOBS_USERS_STUDENT")
public class UserStudent implements PersistentEntity {
	
	private static final long serialVersionUID = 4830765332062213608L;

	static final Logger LOGGER = LoggerFactory.getLogger(UserStudent.class);
	
	@SequenceGenerator(name = "UsersStudent", sequenceName = "SEQ_JOBS_USERS_STUDENT", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersStudent")
	@Column(name = "ID", nullable = false, precision = 10, scale = 0)
	private Long id;
	
	@OneToOne
	@JoinColumn(name="USER_ID", nullable = false)
	private AdmUser user;
	
	@Column(name = "EDUCATION_DEGREE", nullable = false, precision = 10, scale = 0)
	private Long educationDegree;
	
	@Column(name = "COURSE", nullable = false, precision = 10, scale = 0)
	private Long course;
	
	@Column(name = "SUBJECT", nullable = false, precision = 10, scale = 0)
	private Long subject;
	
	@Column(name = "STATUS", nullable = false, precision = 10, scale = 0)
	private Long studentStatus;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_DATE", nullable = false)
	private Date studentStatusDate;
	
	
	@Column(name = "EDUCATION_AREA", precision = 10, scale = 0)
	private Long educationArea;
	
	@Column(name = "UNIVERSITY", precision = 10, scale = 0)
	private Long university;
	
	
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

	public AdmUser getUser() {
		return user;
	}

	public void setUser(AdmUser user) {
		this.user = user;
	}

	public Long getEducationDegree() {
		return educationDegree;
	}

	public void setEducationDegree(Long educationDegree) {
		this.educationDegree = educationDegree;
	}

	public Long getCourse() {
		return course;
	}

	public void setCourse(Long course) {
		this.course = course;
	}

	public Long getSubject() {
		return subject;
	}

	public void setSubject(Long subject) {
	//	System.out.println("User: "+subject);
		this.subject = subject;
	}

	public Long getStudentStatus() {
		return studentStatus;
	}

	public void setStudentStatus(Long studentStatus) {
		this.studentStatus = studentStatus;
	}

	public Date getStudentStatusDate() {
		return studentStatusDate;
	}

	public void setStudentStatusDate(Date studentStatusDate) {
		this.studentStatusDate = studentStatusDate;
	}

	public Long getEducationArea() {
		return educationArea;
	}

	public void setEducationArea(Long educationArea) {
		this.educationArea = educationArea;
	}

	public Long getUniversity() {
		return university;
	}

	public void setUniversity(Long university) {
		this.university = university;
	}

}
