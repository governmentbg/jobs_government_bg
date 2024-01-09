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

import com.indexbg.system.db.PersistentEntity;

@Entity
@Table(name = "jobs_jobs")
public class Jobs implements PersistentEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9094236105281090470L;

	@SequenceGenerator(name = "Jobs", sequenceName = "seq_jobs_jobs", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Jobs")
	@Column(name = "id", nullable=false, precision = 10, scale = 0)
	private Long id;
	
	@Column(name="job_name", nullable = true, length= 4000)
	private String jobName;
	
	@Column(name="job_shortname", nullable = true, length= 1000)
	private String jobShortName;
	
	@Column(name="min_exp", nullable = true, length= 1000)
	private String minProofExperience;
	
	@Column(name="min_qual", nullable = true, length= 1000)
	private String minQualification;
	
	@Column(name="min_qual_sc", nullable = true)
	private Long minQualificationSc;
	
	
	
	@Column(name="min_rang", nullable = true, length= 1000)
	private String minRang;
	
	@Column(name="min_rang_sc", nullable = true)
	private Long minRangnSc;
	
	
	@Column(name="min_rang_code", nullable = true, length= 1000)
	private Long minRangCode;
	
	@Column(name="level_code", nullable = true, length= 1000)
	private String levelCode;
	
	@Column(name="level_name", nullable = true, length= 1000)
	private String levelName;
	
	@Column(name="level_name_sc", nullable = true)
	private Long levelNameSc;
	
	
	
	@Column(name="status", nullable = true, length= 1000)
	private boolean status;
	
	@Column(name="job_id", nullable = true)
	private Long jobId;
	
	@Column(name="parent_id", nullable = true)
	private Long parentId;
	
	@Column(name="pravo", nullable = true, length= 1000)
	private String pravo;
	
	@Column(name="pravo_sc", nullable = true)
	private Long pravoSc;
	
	@Column(name="type_item", nullable = true, length= 1000)
	private String typeItem;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_from", nullable = true, length = 7)
	private Date dateFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_to", nullable = true, length = 7)
	private Date dateTo;
	
	
	
	
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
	public Long getPravoSc() {
		return pravoSc;
	}
	public void setPravoSc(Long pravoSc) {
		this.pravoSc = pravoSc;
	}
	public Long getMinQualificationSc() {
		return minQualificationSc;
	}
	public void setMinQualificationSc(Long minQualificationSc) {
		this.minQualificationSc = minQualificationSc;
	}
	public Long getMinRangnSc() {
		return minRangnSc;
	}
	public void setMinRangnSc(Long minRangnSc) {
		this.minRangnSc = minRangnSc;
	}
	public Long getLevelNameSc() {
		return levelNameSc;
	}
	public void setLevelNameSc(Long levelNameSc) {
		this.levelNameSc = levelNameSc;
	}
	public Long getMinRangCode() {
		return minRangCode;
	}
	public void setMinRangCode(Long minRangCode) {
		this.minRangCode = minRangCode;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public String getPravo() {
		return pravo;
	}
	public void setPravo(String pravo) {
		this.pravo = pravo;
	}
	public String getTypeItem() {
		return typeItem;
	}
	public void setTypeItem(String typeItem) {
		this.typeItem = typeItem;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Column(name="job_code", nullable = true)
	private String jobCode;
	
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobShortName() {
		return jobShortName;
	}
	public void setJobShortName(String jobShortName) {
		this.jobShortName = jobShortName;
	}
	public String getMinProofExperience() {
		return minProofExperience;
	}
	public void setMinProofExperience(String minProofExperience) {
		this.minProofExperience = minProofExperience;
	}
	public String getMinQualification() {
		return minQualification;
	}
	public void setMinQualification(String minQualification) {
		this.minQualification = minQualification;
	}
	public String getMinRang() {
		return minRang;
	}
	public void setMinRang(String minRang) {
		this.minRang = minRang;
	}
	public String getLevelCode() {
		return levelCode;
	}
	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public Long getJobId() {
		return jobId;
	}
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	public String getJobCode() {
		return jobCode;
	}
	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Long getCodeMainObject() {
		// TODO Auto-generated method stub
		return -99L;
	}

	
	
	
}
