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
@Table(name = "jobs_page_text")
public class PageText extends TrackableEntity {

	/** Основен java Entity клас на статичните текстове в различните страници
	 * 
	 */
	private static final long serialVersionUID = -8987212043175932208L;

	static final Logger LOGGER = LoggerFactory.getLogger(PageText.class);
	
	@SequenceGenerator(name = "PageText", sequenceName = "seq_jobs_page_text", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PageText")
	@Column(name = "id", nullable = false, precision = 10, scale = 0)
	private Long id; // id
	
	@Column(name = "code_page", nullable = false, precision = 10, scale = 0)
	private Long codePage; // Код на страницата
	
	@Column(name = "LANG", nullable = false, precision = 10, scale = 0) 
	private Long lang;  // Код на езика - от класификация
	
	@Column(name = "page_text", nullable = false)
	private String 	pageText; // Текст на страницата
	
	@Column(name = "visible_on_site")
	private boolean visibleOnSite; // Да е видим ли текста на страницата

	/** default constructor */
	public PageText() {
		
	}	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCodePage() {
		return codePage;
	}

	public void setCodePage(Long codePage) {
		this.codePage = codePage;
	}

	public Long getLang() {
		return lang;
	}

	public void setLang(Long lang) {
		this.lang = lang;
	}

	public String getPageText() {
		return pageText;
	}

	public void setPageText(String pageText) {
		this.pageText = pageText;
	}

	public boolean isVisibleOnSite() {
		return visibleOnSite;
	}

	public void setVisibleOnSite(boolean visibleOnSite) {
		this.visibleOnSite = visibleOnSite;
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
	
	@Override
	public Long getCodeMainObject() {
		return Constants.CODE_OBJECT_STATIC_TEXTS; 
	}

	public String getIdentInfo() {
		return "";
	}
}
