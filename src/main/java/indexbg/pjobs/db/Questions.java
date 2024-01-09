package indexbg.pjobs.db;

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
@Table(name = "JOBS_QUESTIONS")
public class Questions extends TrackableEntity {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8851854519492187869L;

	/**
	 * Основен java Entity клас на публикациите 
	 */
	

	static final Logger LOGGER = LoggerFactory.getLogger(Questions.class);
	
	@SequenceGenerator(name = "Questions", sequenceName = "SEQ_JOBS_QUESTIONS", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Questions")
	@Column(name = "ID", nullable=false, precision = 10, scale = 0)  // ид.
	private Long id;                      // id
	
	@Column(name = "LANG", nullable=false, precision = 10, scale = 0)  // ид.
	private Long lang;                      // код на езика - класификация

	@Column(name = "GRUPA", nullable=false, precision = 10, scale = 0)
	private Long grupa;      //Код на групата въпроси - класификация        
	
	@Column(name = "QUEST", length = 2500)
	private String  quest;  // Въпрос
	
	@Column(name = "ANSWER", length = 2500)
	private String  answer;  // Отговор
	
	@Column(name = "STATUS", nullable=false, precision = 10, scale = 0)  // Статус на въпроса - показване/непоказване
	private Long status;                  

	public Questions() {
		
	}
	
	@Override
	public Long getCodeMainObject() {
		// TODO Auto-generated method stub
		return Constants.CODE_OBJECT_QUESTIONS;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLang() {
		return lang;
	}

	public void setLang(Long lang) {
		this.lang = lang;
	}


	public String getQuest() {
		return quest;
	}

	public void setQuest(String quest) {
		this.quest = quest;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	
	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}
	
	
	public void setStatusB(Boolean status) {
		if (status) {
			this.status=Long.valueOf(Constants.CODE_ZNACHENIE_DA);
		}else{
			this.status=Long.valueOf(Constants.CODE_ZNACHENIE_NE);
		}		
	}

	public Boolean getStatusB() {
		if (this.status==null || this.status.equals(Long.valueOf(Constants.CODE_ZNACHENIE_NE))) {
			return false;
		}else{
			return true;
		}		
	}
	

	public Long getGrupa() {
		return grupa;
	}

	public void setGrupa(Long grupa) {
		this.grupa = grupa;
	}

	
}