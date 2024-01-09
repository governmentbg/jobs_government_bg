package indexbg.pjobs.db;

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.indexbg.system.db.AuditExt;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.JournalAttr;
import com.indexbg.system.db.PersistentEntity;
import com.indexbg.system.db.TrackableEntity;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;

import indexbg.pjobs.system.Constants;

@Entity
@Table(name = "ADM_GROUPS")
@XmlRootElement
public class AdmGroups extends TrackableEntity implements Cloneable, AuditExt {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8328718883642909104L;

	// properties for object AdmGroups	
	@SequenceGenerator(name = "AdmGroups", sequenceName = "SEQ_ADM_GROUPS", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "AdmGroups")
	@Column(name = "GROUP_ID", nullable = false, precision = 10, scale = 0)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Long id;
	
	@Column(name = "GROUP_NAME", nullable = false, length = 100)
	@JournalAttr(label = "groupName", defaultText = "Име на група")
	private String groupName;
	
	@Column(name = "GROUP_COMMENT", length = 255)
	@JournalAttr(label = "groupComment", defaultText = "Описание на група")
	private String groupComment;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_OT", nullable = false, length = 7)
	@JournalAttr(label = "dateOt", defaultText = "Дата от", dateMask = "dd.MM.yyyy")
	private Date dateOt;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_DO", length = 7)
	@JournalAttr(label = "dateDo", defaultText = "Дата до", dateMask = "dd.MM.yyyy")
	private Date dateDo;
	
	/** Зададени роли на групи */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name="GROUP_ID", nullable = false, referencedColumnName = "GROUP_ID", insertable = true, updatable = true)
	private List<AdmGroupRoles> groupRoles = new ArrayList<AdmGroupRoles>();

	/** Зададени потребители за групите */
	
	@ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinTable(name="ADM_USER_GROUP",
		 joinColumns= { @JoinColumn(name="GROUP_ID", referencedColumnName="GROUP_ID", nullable = false, updatable = false) },
         inverseJoinColumns= { @JoinColumn(name="USER_ID", referencedColumnName="USER_ID", nullable = false, updatable = false)  } )
	private List<AdmUsers> groupUsers = new ArrayList<AdmUsers>();
	
	@Transient
	@JournalAttr(label = "xmlUsers", defaultText = "Участници", classifID = "" + Constants.CODE_OBJECTS_USERS)
	private List<Long> xmlUsers;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getGroupComment() {
		return groupComment;
	}
	
	public void setGroupComment(String groupComment) {
		this.groupComment = groupComment;
	}
	
	public Date getDateOt() {
		return dateOt;
	}
	
	public void setDateOt(Date dateOt) {
		this.dateOt = dateOt;
	}
	
	public Date getDateDo() {
		return dateDo;
	}
	
	public void setDateDo(Date dateDo) {
		this.dateDo = dateDo;
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
	
	public List<AdmGroupRoles> getGroupRoles() {
		return groupRoles;
	}
	
	public void setGroupRoles(List<AdmGroupRoles> groupRoles) {
		this.groupRoles = groupRoles;
	}
	
	@XmlTransient
	public List<AdmUsers> getGroupUsers() {
		return groupUsers;
	}
	
	public void setGroupUsers(List<AdmUsers> groupUsers) {
		this.groupUsers = groupUsers;
	}		
	
	public List<Long> getXmlUsers() {
		this.xmlUsers = null;
		if (this.groupUsers != null && !this.groupUsers.isEmpty()) {
			this.xmlUsers = new ArrayList<>();

			for (AdmUsers user : this.groupUsers) {
				this.xmlUsers.add(user.getId());
			}
		}
		return this.xmlUsers;
	}

	public void setXmlUsers(List<Long> xmlUsers) {
		this.xmlUsers = xmlUsers;
	}	
	
	/** */
	@Override
	public AdmGroups clone() throws CloneNotSupportedException {
		return (AdmGroups) super.clone();
	}
	
	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECTS_GROUPUSER;
	}
	
	/** */
	@Override
	public PersistentEntity toAuditSerializeObject(String unitName) {
		AdmGroups obj;
		try {
			obj = clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}

		JPA jpa = JPA.getUtil(unitName);

		obj.groupRoles = this.groupRoles != null && jpa.isLoaded(this.groupRoles) ? new ArrayList<>(this.groupRoles) : null;
		obj.groupUsers = this.groupUsers != null && jpa.isLoaded(this.groupUsers) ? new ArrayList<>(this.groupUsers) : null;

		return obj;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal dj = new SystemJournal();
		dj.setCodeObject(getCodeMainObject());
		dj.setIdObject(getId());
		dj.setIdentObject(getIdentInfo());
		return dj;
	}
	
}
