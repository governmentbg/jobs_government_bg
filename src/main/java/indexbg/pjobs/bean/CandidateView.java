package indexbg.pjobs.bean;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.AdmUser;
import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.UserAdd;
import indexbg.pjobs.db.UserEducation;
import indexbg.pjobs.db.UserExperience;
import indexbg.pjobs.db.UserLanguage;
import indexbg.pjobs.db.UserStudent;
import indexbg.pjobs.db.UsersBursar;
import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.UsersBursarDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * @author dkarapetrova
 *
 */
@Named
@ViewScoped
public class CandidateView extends PJobsBean {	

	/** Основен java клас за разглеждане на профила на подходящ кандидат
	 * 
	 * 
	 */
	private static final long serialVersionUID = 117018898717590666L;
	
	static final Logger LOGGER = LoggerFactory.getLogger(CandidateView.class);
	
	private Long userId;
	private AdmUser user;
	private UserAdd userAdd;
	private UserStudent userStudent;
	private UserEducation newEducation = new UserEducation();
	private UserExperience newExperience = new UserExperience();
	private UserLanguage newLanguage = new UserLanguage();
	private List<UserEducation> userEducation;
	private List<UserExperience> userExperience;
	private List<UserLanguage> userLanguages;
	private UsersBursar userBursar;
	
	// Сертификати	
	private FilesDAO filesDAO;
	private List<Files> filesList = new ArrayList<>();
	
	private Long idPrac;
		
	/** Иницира първоначалните стойности на обекта
	 * 
	 * Зарежда обекта по подаден параметър от списъка с подходящи кандидати
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.filesDAO = new FilesDAO(this.userId);
			
			Long idObj = null;
			
			if (JSFUtils.getRequestParameter("idObj") != null && !JSFUtils.getRequestParameter("idObj").equals("")) {
				idObj = Long.valueOf(JSFUtils.getRequestParameter("idObj"));
				
				user = new AdmUserDAO(this.userId).findById(idObj);
				userAdd = user.getUserAdd();
				
				this.filesList = this.filesDAO.findByCodeObjAndIdObj(this.user.getCodeMainObject(), this.user.getId()); 
				
				if(user.getUserStudent() == null) {
					user.setUserStudent(new UserStudent());
				}
				userStudent = user.getUserStudent();
				user.getUserEducation().size();
				user.getUserExperience().size();
				user.getUserLanguages().size();
				userEducation = user.getUserEducation();
				userExperience = user.getUserExperience();
				userLanguages = user.getUserLanguages();
				userBursar = new UsersBursarDAO(this.userId).findByIdUser(idObj);
			}	
			
			if (JSFUtils.getRequestParameter("idPrac") != null && !JSFUtils.getRequestParameter("idPrac").equals("")) {				
				this.idPrac = Long.valueOf(JSFUtils.getRequestParameter("idPrac"));				
				
			} else {
				this.idPrac = null;
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

	public UserAdd getUserAdd() {
		return userAdd;
	}

	public void setUserAdd(UserAdd userAdd) {
		this.userAdd = userAdd;
	}

	public AdmUser getUser() {
		return user;
	}

	public void setUser(AdmUser user) {
		this.user = user;
	}

	public UserStudent getUserStudent() {
		return userStudent;
	}

	public void setUserStudent(UserStudent userStudent) {
		this.userStudent = userStudent;
	}	

	public UserEducation getNewEducation() {
		return newEducation;
	}

	public void setNewEducation(UserEducation newEducation) {
		this.newEducation = newEducation;
	}

	public UserExperience getNewExperience() {
		return newExperience;
	}

	public void setNewExperience(UserExperience newExperience) {
		this.newExperience = newExperience;
	}

	public UserLanguage getNewLanguage() {
		return newLanguage;
	}

	public void setNewLanguage(UserLanguage newLanguage) {
		this.newLanguage = newLanguage;
	}

	public List<UserEducation> getUserEducation() {
		return userEducation;
	}

	public void setUserEducation(List<UserEducation> userEducation) {
		this.userEducation = userEducation;
	}

	public List<UserExperience> getUserExperience() {
		return userExperience;
	}

	public void setUserExperience(List<UserExperience> userExperience) {
		this.userExperience = userExperience;
	}

	public List<UserLanguage> getUserLanguages() {
		return userLanguages;
	}

	public void setUserLanguages(List<UserLanguage> userLanguages) {
		this.userLanguages = userLanguages;
	}

	public UsersBursar getUserBursar() {
		return userBursar;
	}

	public void setUserBursar(UsersBursar userBursar) {
		this.userBursar = userBursar;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}
	
	public Long getIdPrac() {
		return idPrac;
	}

	public void setIdPrac(Long idPrac) {
		this.idPrac = idPrac;
	}
	
	// ************************************************* END GET & SET ***************************************************** //	
	
	// ************************************************* FILES ************************************************************* //

	/** Извличане от БД на запазените файлове към профила на подходящ кандидат
	 * 
	 * 
	 * @param file
	 */
	public void download(Files file){ 
		
		boolean ok = true;
		
		if(file.getContent() == null && file.getId() != null) {
			
			try {					
				
				file = this.filesDAO.findById(file.getId());
				
				if(file.getPath() != null && !file.getPath().isEmpty()){
					Path path = Paths.get(file.getPath());
					file.setContent(java.nio.file.Files.readAllBytes(path));
				}
			
			} catch (DbErrorException e) {
				LOGGER.error("DbErrorException: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
			
			} catch (IOException e) {
				LOGGER.error("IOException: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			
			} catch (Exception e) {
				LOGGER.error("Exception: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
			
			}  finally {
				JPA.getUtil().closeConnection();
			}
		}
		
		if(ok){
			
			try {
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
			    ExternalContext externalContext = facesContext.getExternalContext();
			    externalContext.setResponseHeader("Content-Type", "application/x-download");
			    externalContext.setResponseHeader("Content-Length", file.getContent().length + "");
			    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + file.getFilename() + "\"");
				externalContext.getResponseOutputStream().write(file.getContent());
				facesContext.responseComplete();
			
			} catch (IOException e) {
				LOGGER.error("IOException: " + e.getMessage());
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			
			} catch (Exception e) {
				LOGGER.error("Exception: " + e.getMessage());
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
			} 
		}
		
	}			
	
	// ************************************************* END FILES ************************************************************* //
			
}
