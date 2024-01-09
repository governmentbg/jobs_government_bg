package indexbg.pjobs.db;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.PersistentEntity;


@Entity
@Table(name = "ADM_USER_ROLES")
public class AdmUserRoles implements PersistentEntity {

	private static final long serialVersionUID = -545631164407428931L;
	static final Logger LOGGER = LoggerFactory.getLogger(AdmUserRoles.class);	

	// properties
	@SequenceGenerator(name = "UserRoles", sequenceName = "SEQ_ADM_USER_ROLES", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "UserRoles")
	@Column(name = "ROLE_ID", nullable=false, precision = 10, scale = 0)
	private Long  id;      // id
	
	@Column(name="USER_ID",insertable = false, updatable = false)	
	private Long userId;      // id User
	
	@Column(name = "CODE_CLASSIF", nullable=false, precision = 10, scale = 0)
	private Long  codeClassif;     // Код класификация
	@Column(name = "CODE_ROLE", nullable=false, precision = 10, scale = 0)
	private  Long codeRole;        // Код на ролята от класификацията

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
