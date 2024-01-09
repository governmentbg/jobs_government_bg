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
@Table(name = "jobs_administration_structures")
public class OrgStructure implements PersistentEntity{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2592936752386136454L;

	@SequenceGenerator(name = "OrgStructure", sequenceName = "seq_jobs_administration", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OrgStructure")
	@Column(name = "id", nullable=false, precision = 10, scale = 0)
	private Long id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_from", length = 7)
	private Date datFrom;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_to", length = 7)
	private Date datTo;
	
	@Column(name="str_name", nullable = true, length= 255)	
	private String strName;
	
	@Column(name="id_parent", precision = 10, scale = 0)
	private Long idParent;
	
	@Column(name="id_subject", precision = 10, scale = 0)
	private Long idSubject;
	
	@Column(name="region", nullable = true)	
	private Long region; 	
	
	@Column(name="municipality", nullable = true)	
	private Long municipality; 	
	
	@Column(name="town", nullable = true)
	private Long town; 
	
	@Column(name="address", length= 100 , nullable = true)	
	private String address; 
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDatFrom() {
		return datFrom;
	}
	public void setDatFrom(Date datFrom) {
		this.datFrom = datFrom;
	}
	public Date getDatTo() {
		return datTo;
	}
	public void setDatTo(Date datTo) {
		this.datTo = datTo;
	}
	public String getStrName() {
		return strName;
	}
	public void setStrName(String strName) {
		this.strName = strName;
	}
	public Long getIdParent() {
		return idParent;
	}
	public void setIdParent(Long idParent) {
		this.idParent = idParent;
	}
	public Long getIdSubject() {
		return idSubject;
	}
	public void setIdSubject(Long idSubject) {
		this.idSubject = idSubject;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public Long getCodeMainObject() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

}
