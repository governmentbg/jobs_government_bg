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
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import indexbg.pjobs.system.Constants;

import com.indexbg.system.db.AuditExt;
import com.indexbg.system.db.JournalAttr;
import com.indexbg.system.db.TrackableEntity;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;



@Entity
@Table(name = "jobs_practice")
@XmlRootElement
public class Practice extends TrackableEntity implements AuditExt {


	/** Основен java Entity клас на обяви за стаж в ДА
	 * 
	 */

	private static final long serialVersionUID = 8820780634885244707L;

	static final Logger LOGGER = LoggerFactory.getLogger(Practice.class);
	
	@SequenceGenerator(name = "Practice", sequenceName = "seq_jobs_practice", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Practice")
	@Column(name = "id", nullable=false, precision = 10, scale = 0)
	private Long id; 
		
	@Column(name="campaign_id", nullable=false, precision = 10, scale = 0)	
	private Long idCampaign; 	
		
	@Column(name="region", nullable = false ,precision = 10, scale = 0)	
	@JournalAttr(label = "region", defaultText = "Област", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long region; 	
	
	@Column(name="municipality", nullable = false ,precision = 10, scale = 0)
	@JournalAttr(label = "municipality", defaultText = "Община", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long municipality; 	
	
	@Column(name="town", nullable = false ,precision = 10, scale = 0)	
	@JournalAttr(label = "town", defaultText = "Населено място", classifID = "" + Constants.CODE_SYSCLASS_EKATTE)
	private Long town; 	
	
	@Column(name="administration", nullable = false ,precision = 10, scale = 0)	
	@JournalAttr(label = "administration", defaultText = "Административна структура", classifID = "" + Constants.CODE_SYSCLASS_ADM_REGISTER)
	private Long administration; 	
	
	@Column(name="unit", nullable = false ,precision = 10, scale = 0)	
	@JournalAttr(label = "unit", defaultText = "Звено", classifID = "" + Constants.CODE_SYSCLASS_ADM_FLAT)
	private Long unit; 	
	
	@Column(name="education_area", nullable = false ,precision = 10, scale = 0)	
	@JournalAttr(label = "education_area", defaultText = "Област на висше образование", classifID = "" + Constants.CODE_SYSCLASS_SUBJECT)
	private Long educationArea; 	
	
	@Column(name="practice_title", nullable = false, length= 2000)	
	@JournalAttr(label = "practice_title", defaultText = "Заглавие")
	private String practiceTitle; 
	 		
	@Temporal(TemporalType.DATE)
	@Column(name = "date_from", length = 7)
	@JournalAttr(label = "date_from", defaultText = "Начална дата на обявата")
	private Date dateFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_to", length = 7, nullable= true )
	@JournalAttr(label = "date_to", defaultText = "Крайна дата на обявата")
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
	@Column(name = "practice_date_from", length = 7)
	@JournalAttr(label = "practice_date_from", defaultText = "Начална дата на стаж")
	private Date practiceDateFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "practice_date_to", length = 7, nullable= true )
	@JournalAttr(label = "practice_date_to", defaultText = "Крайна дата на стаж")
	private Date practiceDateTo;	
	
	@Column(name="description", nullable = false, length= 10000)	
	@JournalAttr(label = "description", defaultText = "Описание на позиция")
	private String description; 
	
	@Column(name="num", nullable = false ,precision = 10, scale = 0)	
	@JournalAttr(label = "num", defaultText = "Брой места")
	private Long num;
		
	
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

	@Temporal(TemporalType.DATE)
	@Column(name = "structure_date_from", length = 7)
	@JournalAttr(label = "structure_date_from", defaultText = "Начална дата за въвеждане на стаж")
	private Date structureDateFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "structure_date_to", length = 7, nullable= true )
	@JournalAttr(label = "structure_date_to", defaultText = "Крайна дата за въвеждане на стаж")
	private Date structureDateTo;
	
	public Practice () {
		
	}
	
	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECT_PRACTICE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdCampaign() {
		return idCampaign;
	}

	public void setIdCampaign(Long idCampaign) {
		this.idCampaign = idCampaign;
	}

	public Long getRegion() {
		return region;
	}

	public void setRegion(Long region) {
		this.region = region;
	}

	public Long getMunicipality() {
		return municipality;
	}

	public void setMunicipality(Long municipality) {
		this.municipality = municipality;
	}

	public Long getTown() {
		return town;
	}

	public void setTown(Long town) {
		this.town = town;
	}

	public Long getAdministration() {
		return administration;
	}

	public void setAdministration(Long administration) {
		this.administration = administration;
	}

	public Long getUnit() {
		return unit;
	}

	public void setUnit(Long unit) {
		this.unit = unit;
	}

	public Long getEducationArea() {
		return educationArea;
	}

	public void setEducationArea(Long educationArea) {
		this.educationArea = educationArea;
	}

	public String getPracticeTitle() {
		return practiceTitle;
	}

	public void setPracticeTitle(String practiceTitle) {
		this.practiceTitle = practiceTitle;
	}

	public Date getPracticeDateFrom() {
		return practiceDateFrom;
	}

	public void setPracticeDateFrom(Date practiceDateFrom) {
		this.practiceDateFrom = practiceDateFrom;
	}

	public Date getPracticeDateTo() {
		return practiceDateTo;
	}

	public void setPracticeDateTo(Date practiceDateTo) {
		this.practiceDateTo = practiceDateTo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
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
		
		return sj;
	}
	

}
