package indexbg.pjobs.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.indexbg.system.db.TrackableEntity;
import indexbg.pjobs.system.Constants;


@Entity
@Table(name = "JOBS_PUBLICATION_LANG")
public class PublicationLang  extends TrackableEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4439647306940449702L;

	/**
	 * java Entity клас на езиковите версии на публикациите 
	 */

	@SequenceGenerator(name = "PublicationLang", sequenceName = "SEQ_JOBS_PUBLICATION_LANG", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PublicationLang")
	@Column(name = "ID", nullable=false, precision = 10, scale = 0)  // ид.
	private Long id;                      // id
	

	/*@Column(name = "PUBLICATION_ID", nullable=false, precision = 10, scale = 0, insertable = false, updatable = false)  // ид. на публикацията root
*/	@Column(name = "PUBLICATION_ID", nullable=false, precision = 10, scale = 0)  // ид. на публикацията root	
	private Long pubId;                      
	
	@Column(name = "LANG", nullable=false, precision = 10, scale = 0)  // Код на езика - от класификация
	private Long lang; 
	
	@Column(name = "TITLE", length = 250)
	private String title;         // Заглавие    
	
	@Column(name = "ANNOTATION", length = 2500)
	private String  annotation; // Анотация

	@Column(name = "FULL_TEXT")
	private String 	fullText;    // Пълен текст на публикацията

	@Column(name = "OTHER_INFO", length = 2500)
	private String  otherInfo;  // Други коментари към публикацията

	@Column(name = "URL_PUB", length = 125)
	private String  urlPub;  // URL Publication

	@Override
	public Long getCodeMainObject() {
		// TODO Auto-generated method stub
		return Constants.CODE_OBJECT_PUBL_LANG;
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public String getUrlPub() {
		return urlPub;
	}

	public void setUrlPub(String urlPub) {
		this.urlPub = urlPub;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getLang() {
		return lang;
	}

	public void setLang(Long lang) {
		this.lang = lang;
	}

	public Long getPubId() {
		return pubId;
	}

	public void setPubId(Long pubId) {
		this.pubId = pubId;
	}

	


}
