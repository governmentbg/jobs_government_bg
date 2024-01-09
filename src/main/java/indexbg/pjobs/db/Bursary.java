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

import indexbg.pjobs.system.Constants;

import com.indexbg.system.db.AuditExt;
import com.indexbg.system.db.JournalAttr;
import com.indexbg.system.db.TrackableEntity;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;



@Entity
@Table(name = "jobs_bursary")
public class Bursary extends TrackableEntity implements AuditExt{


	private static final long serialVersionUID = 8334194425597177367L;

	/** Основен java Entity клас на стипендиантски програми
	 * 
	 */

	static final Logger LOGGER = LoggerFactory.getLogger(Bursary.class);
	
	@SequenceGenerator(name = "Bursary", sequenceName = "seq_jobs_bursary", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Bursary")
	@Column(name = "id", nullable=false, precision = 10, scale = 0)
	private Long id; 
	
	@Column(name="administration", nullable=false, precision = 10, scale = 0)	
	@JournalAttr(label = "administration", defaultText = "Административна структура", classifID = "" + Constants.CODE_SYSCLASS_ADM_REGISTER)
	private Long administration; 	
	
	@Column(name="subject", nullable = false, length= 255)	
	@JournalAttr(label = "subject", defaultText = "Специалност")
	private String subject; 
	
	@Column(name="bursary", nullable = false ,precision = 5, scale = 0)	
	@JournalAttr(label = "bursary", defaultText = "Размер")
	private Long bursary; 	
	
	@Column(name="num", nullable = false,precision = 5, scale = 0)	
	@JournalAttr(label = "num", defaultText = "Брой стипендии")
	private Long num; 	
		
	@Temporal(TemporalType.DATE)
	@Column(name = "date_from", length = 7)
	@JournalAttr(label = "date_from", defaultText = "Валидност от")
	private Date dateFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_to", length = 7, nullable= true )
	@JournalAttr(label = "date_to", defaultText = "Валидност до")
	private Date dateTo;
	
	@Column(name="add_info", nullable = true)	
	@JournalAttr(label = "AddInfo", defaultText = "Допълнителна информация")
	private String AddInfo; 	
	
	@Column(name="status", precision = 10, scale = 0)	
	
	@JournalAttr(label = "status", defaultText = "Статус", classifID = "" + Constants.CODE_SYSCLASS_STATUS_BURSARY)
	private Long status; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date", length = 7)
	@JournalAttr(label = "status_date", defaultText = "Дата на статус")
	private Date statusDate;
	

	public Bursary () {
		
	}
	
	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECT_BURSARY;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getIdentInfo() {
		return "";
	}

	public Long getAdministration() {
		return administration;
	}

	public void setAdministration(Long administration) {
		this.administration = administration;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Long getBursary() {
		return bursary;
	}

	public void setBursary(Long bursary) {
		this.bursary = bursary;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	public String getAddInfo() {
		return AddInfo;
	}

	public void setAddInfo(String addInfo) {
		AddInfo = addInfo;
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
