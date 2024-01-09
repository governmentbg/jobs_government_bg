package indexbg.pjobs.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.TrackableEntity;

import indexbg.pjobs.system.Constants;



@Entity
@Table(name = "jobs_contacts")
public class Contacts extends TrackableEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6206814477620260717L;

	static final Logger LOGGER = LoggerFactory.getLogger(Contacts.class);
	
	
	@SequenceGenerator(name = "Contacts", sequenceName = "seq_jobs_contacts", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Contacts")
	@Column(name="id", nullable=false, precision = 10, scale = 0)	
	private Long id; 	
	
	@Column(name="phones", nullable = false, length= 255)	
	private String phones; 
	
	
	@Column(name="emails", nullable = false, length= 255)	
	private String emails; 
	
	@Column(name="fax", nullable = false, length= 255)	
	private String fax; 	
	
	@Column(name="add_info", nullable = false, length= 300)	
	private String addInfo; 	
	
	public Contacts () {
		
	}
	
	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECT_CONTACTS;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public String getIdentInfo() {
		return "";
	}
	
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAddInfo() {
		return addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	@Override
	public Long getUserReg() {
		// TODO Auto-generated method stub
		return super.getUserReg();
	}
	
	@Override
	public Date getDateReg() {
		// TODO Auto-generated method stub
		return super.getDateReg();
	}
	
	@Override
	public Long getUserLastMod() {
		// TODO Auto-generated method stub
		return super.getUserLastMod();
	}
	
	@Override
	public Date getDateLastMod() {
		// TODO Auto-generated method stub
		return super.getDateLastMod();
	}

}
