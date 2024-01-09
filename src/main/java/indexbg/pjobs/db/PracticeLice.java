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

import com.indexbg.system.db.AuditExt;
import com.indexbg.system.db.JournalAttr;
import com.indexbg.system.db.TrackableEntity;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;

import indexbg.pjobs.system.Constants;


@Entity
@Table(name = "jobs_practice_lice")
public class PracticeLice extends TrackableEntity implements AuditExt{


	/** Кандидатсване за обява за стаж
	 * 
	 */

	private static final long serialVersionUID = 8820780634885244707L;

	static final Logger LOGGER = LoggerFactory.getLogger(PracticeLice.class);
	
	@SequenceGenerator(name = "PracticeLice", sequenceName = "seq_jobs_practice_lice", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PracticeLice")
	@Column(name = "id", nullable=false, precision = 10, scale = 0)
	private Long id; 
		
	@Column(name="practice_id", nullable=false, precision = 10, scale = 0)	
	private Long idPractice; 	
		
	@Column(name="user_id", nullable = false, precision = 10, scale = 0)	
	private Long idUser; 
	
	@Column(name="status", precision = 10, scale = 0)	
	@JournalAttr(label = "status", defaultText = "Статус", classifID = "" + Constants.CODE_SYSCLASS_STATUS_CANDIDATE)
	private Long status; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date", length = 7)
	@JournalAttr(label = "status_date", defaultText = "Дата на статус", dateMask = "dd.MM.yyyy HH:mm")
	private Date statusDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "interview_date", length = 7)
	@JournalAttr(label = "nterview_date", defaultText = "Дата на интервю", dateMask = "dd.MM.yyyy HH:mm")
	private Date dateInterview;
	
	@Column(name="interview_place", nullable = true, length= 2000)	
	@JournalAttr(label = "interview_place", defaultText = "Адрес на интервю")
	private String interviewPlace; 
	
	@Column(name="classification", nullable = true, precision = 10, scale = 0)
	@JournalAttr(label = "classification", defaultText = "Класиране")
	private Long classification; 	
	
	@Column(name="practice_result", nullable = true, precision = 10, scale = 0)	
	@JournalAttr(label = "practice_result", defaultText = "Резултат от стажа")
	private Long practiceResult; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "accept_date", length = 7)
	@JournalAttr(label = "accept_date", defaultText = "Дата на приемане на стаж", dateMask = "dd.MM.yyyy HH:mm")
	private Date acceptDate;
	
	@Column(name="ranked2", nullable = true, precision = 10, scale = 0)	
	@JournalAttr(label = "ranked2", defaultText = "Класиран на втора класация")
	private Long ranked2; 
			
	public PracticeLice () {
		
	}
	
	@Override
	public Long getCodeMainObject() { 
		return Constants.CODE_OBJECT_APPLY;
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
	
	public Long getIdPractice() {
		return idPractice;
	}

	public void setIdPractice(Long idPractice) {
		this.idPractice = idPractice;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public Date getDateInterview() {
		return dateInterview;
	}

	public void setDateInterview(Date dateInterview) {
		this.dateInterview = dateInterview;
	}

	public String getInterviewPlace() {
		return interviewPlace;
	}

	public void setInterviewPlace(String interviewPlace) {
		this.interviewPlace = interviewPlace;
	}

	public Long getClassification() {
		return classification;
	}

	public void setClassification(Long classification) {
		this.classification = classification;
	}

	public Long getPracticeResult() {
		return practiceResult;
	}

	public void setPracticeResult(Long practiceResult) {
		this.practiceResult = practiceResult;
	}

	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	public Long getRanked2() {
		return ranked2;
	}

	public void setRanked2(Long ranked2) {
		this.ranked2 = ranked2;
	}
	
	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal sj = new  SystemJournal();				
		sj.setCodeObject(getCodeMainObject());
		sj.setIdObject(getId());
		sj.setIdentObject(getIdentInfo());
//		sj.setJoinedCodeObject1(DocuConstants.CODE_ZNACHENIE_JOURNAL_DELO);
//		sj.setJoinedCodeObject2(DocuConstants.CODE_ZNACHENIE_JOURNAL_DELO);
//		sj.setJoinedIdObject1(idCampaign);
//		sj.setJoinedIdObject2(inputDeloId);
		return sj;
	}
}
