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
import com.indexbg.system.db.TrackableEntity;

import indexbg.pjobs.system.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Entity
@Table(name = "JOBS_PUBLICATION")
public class Publication extends TrackableEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4357503241275885208L;

	/**
	 * Основен java Entity клас на публикациите 
	 */
	

	static final Logger LOGGER = LoggerFactory.getLogger(Publication.class);
	
	@SequenceGenerator(name = "Publication", sequenceName = "SEQ_JOBS_PUBLICATION", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Publication")
	@Column(name = "ID", nullable=false, precision = 10, scale = 0)  // ид.
	private Long id;                      // id
	

	@Column(name = "SECTION", nullable=false, precision = 10, scale = 0)
	private Long section;      //Код на секция        
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_FROM", nullable=false, length = 7)
	private Date dateFrom;   // Нач. дата на публикацията
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_TO", length = 7)
	private Date dateTo;  // Крайна дата на публикацията

	@Column(name = "IMAGE_PUB")
	private byte[] 	imagePub;     // Контент на изображение към публикация 
	
	@Column(name = "NOTE", length = 2500)
	private String  note;  // Други коментари към публикацията
	
	/** Текстови атрибути на публикацията/информац.материал - езикови версии*/
	/*@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true )
	@JoinColumn(name="PUBLICATION_ID", nullable = false, referencedColumnName = "ID", insertable = true, updatable = true)
	private List<PublicationLang> pubLang=new ArrayList<PublicationLang>(0);*/
	
	@Column(name = "TYPE_PUB", nullable=false, precision = 10, scale = 0)
	private Long typePub;      //тип на публикацията (видео ,изображения ,статии)
		
	@Override
	public Long getCodeMainObject() {
		// TODO Auto-generated method stub
		return Constants.CODE_OBJECT_PUBLICATION;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSection() {
		return section;
	}

	public void setSection(Long section) {
		this.section = section;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}


	public byte[] getImagePub() {
		return imagePub;
	}

	public void setImagePub(byte[] imagePub) {
		this.imagePub = imagePub;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/*public List<PublicationLang> getPubLang() {
		return pubLang;
	}

	public void setPubLang(List<PublicationLang> pubLang) {
		this.pubLang = pubLang;
	}*/
	

	public Long getTypePub() {
		return typePub;
	}

	public void setTypePub(Long typePub) {
		this.typePub = typePub;
	}

	public Publication() {
		
	}
}
