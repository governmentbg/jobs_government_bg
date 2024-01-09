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

import com.indexbg.system.db.JournalAttr;
import com.indexbg.system.db.AuditExt;
import com.indexbg.system.db.TrackableEntity;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;


@Entity
@Table(name = "jobs_campaign")
public class Campaign extends TrackableEntity implements AuditExt{


	private static final long serialVersionUID = 8334194425597177367L;

	/** Основен java Entity клас на кампанни за стаж в ДА
	 * 
	 */

	static final Logger LOGGER = LoggerFactory.getLogger(Campaign.class);
	
	@SequenceGenerator(name = "Campaign", sequenceName = "seq_jobs_campaign", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Campaign")
	@Column(name = "id", nullable=false, precision = 10, scale = 0)
	private Long id; 
	
	@Column(name="name", nullable = false, length= 255)	
	@JournalAttr(label = "name", defaultText = "Наименование")
	private String nameCamp; 
	
	@Column(name="type", nullable=false, precision = 10, scale = 0)	
	@JournalAttr(label = "type", defaultText = "Тип на кампанията", classifID = "" + Constants.CODE_SYSCLASS_CAMPAIGN_TYPE)
	private Long typeCamp; 	
		
	@Column(name="year", nullable = false ,precision = 10, scale = 0)	
	@JournalAttr(label = "year", defaultText = "Година")
	private Long year; 	
	 	
		
	@Temporal(TemporalType.DATE)
	@Column(name = "date_from", length = 7)
	@JournalAttr(label = "date_from", defaultText = "Начална дата за кандидатстване")
	private Date dateFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_to", length = 7, nullable= true )
	@JournalAttr(label = "date_to", defaultText = "Крайна дата за кандидатстване")
	private Date dateTo;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "ranking_date", length = 7, nullable= true )
	@JournalAttr(label = "ranking_date", defaultText = "Дата на първа класация")
	private Date rankingDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "confirm_date_from", length = 7)
	@JournalAttr(label = "confirm_date_from", defaultText = "Начална дата на потвърждаване")
	private Date confirmDateFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "confirm_date_to", length = 7, nullable= true )
	@JournalAttr(label = "confirm_date_to", defaultText = "Крайна дата на потвърждаване")
	private Date confirmDateTo;	
	
	@Temporal(TemporalType.DATE)
	@Column(name = "ranking_date2", length = 7, nullable= true )
	@JournalAttr(label = "ranking_date2", defaultText = "Дата за втора класация")
	private Date rankingDate2;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "confirm_date_from2", length = 7)
	@JournalAttr(label = "confirm_date_from2", defaultText = "Дата от за потвърждение след втора класация")
	private Date confirmDateFrom2;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "confirm_date_to2", length = 7, nullable= true )
	@JournalAttr(label = "confirm_date_to2", defaultText = "Дата до за потвърждение след втора класация")
	private Date confirmDateTo2;	
	
	@Column(name="status", precision = 10, scale = 0)	
	@JournalAttr(label = "status", defaultText = "Статус", classifID = "" + Constants.CODE_SYSCLASS_CAMPAIGN_STATUS)
	private Long status; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date", length = 7)
	@JournalAttr(label = "status_date", defaultText = "Дата на статуса")
	private Date statusDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "structure_date_from", length = 7)
	@JournalAttr(label = "structure_date_from", defaultText = "Начална дата за въвеждане на обява")
	private Date structureDateFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "structure_date_to", length = 7, nullable= true )
	@JournalAttr(label = "structure_date_to", defaultText = "Крайна дата за въвеждане на обява")
	private Date structureDateTo;
	

	public Campaign () {
		
	}
	
	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECT_CAMPAIGN;
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
	
	public String getNameCamp() {
		return nameCamp;
	}

	public void setNameCamp(String name) {
		this.nameCamp = name;
	}

	public Long getTypeCamp() {
		return typeCamp;
	}

	public void setTypeCamp(Long type) {
		this.typeCamp = type;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	public Date getConfirmDateFrom() {
		return confirmDateFrom;
	}

	public void setConfirmDateFrom(Date confirmDateFrom) {
		this.confirmDateFrom = confirmDateFrom;
	}

	public Date getConfirmDateTo() {
		return confirmDateTo;
	}

	public void setConfirmDateTo(Date confirmDateTo) {
		this.confirmDateTo = confirmDateTo;
	}

	public Date getRankingDate() {
		return rankingDate;
	}

	public void setRankingDate(Date rankingDate) {
		this.rankingDate = rankingDate;
	}

	public Date getRankingDate2() {
		return rankingDate2;
	}

	public void setRankingDate2(Date rankingDate2) {
		this.rankingDate2 = rankingDate2;
	}

	public Date getConfirmDateFrom2() {
		return confirmDateFrom2;
	}

	public void setConfirmDateFrom2(Date confirmDateFrom2) {
		this.confirmDateFrom2 = confirmDateFrom2;
	}

	public Date getConfirmDateTo2() {
		return confirmDateTo2;
	}

	public void setConfirmDateTo2(Date confirmDateTo2) {
		this.confirmDateTo2 = confirmDateTo2;
	}

	public Date getStructureDateFrom() {
		return structureDateFrom;
	}

	public void setStructureDateFrom(Date structureDateFrom) {
		this.structureDateFrom = structureDateFrom;
	}

	public Date getStructureDateTo() {
		return structureDateTo;
	}

	public void setStructureDateTo(Date structureDateTo) {
		this.structureDateTo = structureDateTo;
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
