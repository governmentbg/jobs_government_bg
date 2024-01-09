package indexbg.pjobs.bean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.exceptions.BaseException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.PageText;
import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.db.dao.PageTextDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named
@ViewScoped
public class UpdateSalaryTable extends PJobsBean {	
	
	/** Основен java клас за въвеждане/актуализация на статични текстове към различни страници
	 * 
	 * 
	 */
	private static final long serialVersionUID = -6221008129476410947L;
	static final Logger LOGGER = LoggerFactory.getLogger(UpdateSalaryTable.class);
	
	private Long userId;
	
	//Текста на инструкциите
	private String txtInstructions;	
	
	/** Иницира първоначалните стойности на обекта
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.txtInstructions = "";
			
			PageText txtPages = new PageTextDAO(getUserData().getUserId()).loadPageTextByCodeSectLang(59L, getCurrentLang());
			
			if(txtPages.isVisibleOnSite()) {
				txtInstructions = txtPages.getPageText();
			}
		
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;	
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));		
		} 	
	}	

	/** Избор на файлове за прикачване към избраната страница
	 * 
	 * 
	 * @param event
	 */
	public void uploadFileListener(FileUploadEvent event){		
		
		try {
			
			UploadedFile upFile = event.getFile();			
					
			new AdministrationDAO(userId, getSystemData()).updateSalaryTables(upFile.getContent());
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "updateSalary.succesUpdate"));			
		
			
		} catch (BaseException e) {
			LOGGER.error("Exception: " + e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		
		} catch (Exception e) {
			LOGGER.error("Exception: " + e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		} 
	}		
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTxtInstructions() {
		return txtInstructions;
	}

	public void setTxtInstructions(String txtInstructions) {
		this.txtInstructions = txtInstructions;
	}	
	
	// ************************************************* END GET & SET ***************************************************** //	
		
}
