package indexbg.pjobs.db;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.indexbg.system.db.PersistentEntity;



@Entity
@Table(name = "ADM_GROUP_ROLES")
public class AdmGroupRoles implements PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2342551115626418279L;
	
	// properties
	@SequenceGenerator(name = "AdmGroupRoles", sequenceName = "SEQ_ADM_GROUP_ROLES", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "AdmGroupRoles")
	@Column(name = "ROLE_ID", nullable = false, precision = 10, scale = 0)
	private Long id; 	
	
	@Column(name="GROUP_ID",nullable = false, insertable = false, updatable = false)	
	private Long groupId; 
	
	@Column(name = "CODE_CLASSIF", nullable = false, precision = 10, scale = 0)	
	private Long codeClassif; 
	
	@Column(name = "CODE_ROLE", nullable = false, precision = 10, scale = 0)	
	private Long codeRole; 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getCodeClassif() {
		return codeClassif;
	}

	public void setCodeClassif(Long codeClassif) {
		this.codeClassif = codeClassif;
	}

	public Long getCodeRole() {
		return codeRole;
	}

	public void setCodeRole(Long codeRole) {
		this.codeRole = codeRole;
	}

	@Override
	public Long getCodeMainObject() {
		return null; // записват се през парент обект
	}	

}
