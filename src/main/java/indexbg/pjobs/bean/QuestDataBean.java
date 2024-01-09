package indexbg.pjobs.bean;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.indexbg.indexui.navigation.Navigation;
import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import javax.faces.view.ViewScoped;
import indexbg.pjobs.db.Questions;
import indexbg.pjobs.db.dao.QuestionsDAO;
import indexbg.pjobs.system.PJobsBean;
import indexbg.pjobs.system.Constants;


@Named("questData")
@ViewScoped
public class QuestDataBean extends PJobsBean {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8494303783381577606L;


	/**
	 * Основен javaBean клас за въвеждане/актуализация на въпрос
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(QuestDataBean.class);
	
	
	private Questions question = new Questions();
	private Long idQuest = null;
	private String grupaText="";
	private Long codeGrupa=null;
	private Long idUser=null;

	/**Инициира/сетва първоначалните стойности на атрибутите на обектите. Чете предадените параметри от други екрани 
	 * 
	 */
	@PostConstruct
	public void initData(){
		LOGGER.debug("PostConstruct!");
		
		
		try{
			this.idUser=getUserData().getUserId();
		} catch (ObjectNotFoundException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.objectNotFound"), e.getMessage());
			LOGGER.error(e.getMessage(), e);
			this.idUser = -1L;
		}

		
		String par =JSFUtils.getRequestParameter("idQuest");
		
		actionClear();
		this.question.setLang(getCurrentLang());
		
		if (par != null && !par.trim().isEmpty()){
			this.setIdQuest(Long.valueOf(par));
			loadQuestById(this.getIdQuest());
		}

	}
	
	/**Изтрива стойностите на определени атрибути на обектите
	 * 
	 */
	public void actionClear(){
		this.question = new Questions();
		this.question.setLang(getCurrentLang());
		this.setGrupaText("");
		this.setCodeGrupa(null);
	}

	public Questions getQuestion() {
		return question;
	}

	public void setQuestion(Questions question) {
		this.question = question;
	}

	/** Записва в БД на въведените/актуализираните информационни обекти/въпроси
	 * 
	 */
	public void actionSave(){
		
		if (!checkData())
			return;
	

		try {
			
			JPA.getUtil().begin();
			new QuestionsDAO(this.idUser).save(this.question);
			
						
			JPA.getUtil().commit();
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
								
			
			} catch (DbErrorException e) {
			 	LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
				JPA.getUtil().rollback();
			/*} catch (ObjectInUseException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.objectInUse"), e.getMessage());	
				JPA.getUtil().rollback();*/
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());	
				JPA.getUtil().rollback();
			} finally {
				JPA.getUtil().closeConnection();
		} 
	
	}
	
	
	public boolean checkData(){
		boolean ver = true;
		// 1. Required
		
		if (null==this.question.getGrupa()) {
			JSFUtils.addMessage("formQuestData:idGrupaText", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","quest.nogrupa"));
			ver=false;
		}
		if (null==this.question.getLang()) {
			JSFUtils.addMessage("formQuestData:idLang", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","quest.noLang"));
			ver=false;
		}
		if (null==this.question.getStatus()) {
			JSFUtils.addMessage("formQuestData:idStatus", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","quest.nostatus"));
			ver=false;
		}
		
		return ver;		
	}
	
	
	/** Изтрива от БД на определен въпрос
	 * 
	 */
	public void actionDelete(){
		
		try {
			
			JPA.getUtil().begin();		

			if (null!=this.question.getId())
				new QuestionsDAO(this.idUser).deleteById(this.question.getId());
				
			JPA.getUtil().commit();			
				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));
			
			Navigation navHolder = new Navigation();			
		    int i = navHolder.getNavPath().size();
		    
		    if(i > 2) {	
		    	navHolder.goTo(String.valueOf(i-2));
		    } else if(i > 1) {	navHolder.goBack();   }
			
			
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"), e.getMessage());
			JPA.getUtil().rollback();
		/*} catch (ObjectInUseException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.objectInUse"), e.getMessage());
			JPA.getUtil().rollback();*/
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());	
			JPA.getUtil().rollback();
		} finally {
			JPA.getUtil().closeConnection();
		}
	
	}

	
	public Date getCurrentDate() {
		return new Date();
	}

	
	
	
	/** Извлича данни за определена публикация по ид.
	 * @param idPubl
	 */
	
	public void loadQuestById(Long idPubl){
		
		try {
			this.question = (Questions) new QuestionsDAO(this.idUser).findById(this.idQuest);

			this.setCodeGrupa(this.question.getGrupa());
			if(null!=this.getCodeGrupa()){
				this.setGrupaText(getSystemData().decodeItem(Constants.CODE_SYSCLASS_QUEST_GRUPA, this.getCodeGrupa(), getCurrentLang(), new Date(), this.idUser));
			}
			    		
						
			} catch (DbErrorException e) {
			 	LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
				
			} finally {
				JPA.getUtil().closeConnection();
			}

	}
	
	public Long getIdUser() {
		return idUser;
	}
	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}


	public Long getIdQuest() {
		return idQuest;
	}

	public void setIdQuest(Long idQuest) {
		this.idQuest = idQuest;
	}

	public String getGrupaText() {
		return grupaText;
	}

	public void setGrupaText(String grupaText) {
		this.grupaText = grupaText;
	}

	public Long getCodeGrupa() {
		return codeGrupa;
	}

	public void setCodeGrupa(Long codeGrupa) {
		this.codeGrupa = codeGrupa;
		this.question.setGrupa(codeGrupa);
	}

}