package indexbg.pjobs.bean;

import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.UserUtils;

import indexbg.pjobs.db.AdmUser;
import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class AdmSettings extends PJobsBean {	

	/** Клас за промяна на парола и добавяне на сертификат за администратори
	 * 
	 * 
	 */
	private static final long serialVersionUID = -4172876657020898361L;

	static final Logger LOGGER = LoggerFactory.getLogger(AdmSettings.class);
	
	private Long userId;
	private AdmUser user;	
	private AdmUserDAO userDao; 
	
	private String[] password = new String[3];	
	
	@PostConstruct
	public void initData() {
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
			this.userId = getUserData().getUserId();
			this.userDao = new AdmUserDAO(this.userId);			
			this.user = userDao.findById(this.userId);

		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	public void actionChangePassword() {

		if(validatePasswords() && validateNewPassword()) {
			String passOld = Base64.getEncoder().encodeToString(this.password[0].getBytes());
			String passNew = Base64.getEncoder().encodeToString(this.password[1].getBytes());
			
			try {
				JPA.getUtil().begin();
				boolean success = this.userDao.changePassword(this.userId, passOld, passNew);
				if(success) {
					JPA.getUtil().commit();
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages","pass.changed"));
				} else {
					JSFUtils.addMessage("formProfile:pass",FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","pass.wrong"));
				}
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(), e);
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(), e);
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
		
	}
	
	/**
	 * Проверява дали полетата са попълнени и дали двете нови пароли си съвпадат 
	 * @return true, ако всичко е валидно
	 */
	private boolean validatePasswords() {
		
		boolean valid = true;
		
		if(this.password[0] == null ||this.password[0].trim().equals("")) { // ПАРОЛА
			JSFUtils.addMessage("formAdmSet:oldPass",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.pass")));
			valid = false;
		}
		
		if(this.password[1] == null ||this.password[1].trim().equals("")) { // НОВА ПАРОЛА
			JSFUtils.addMessage("formAdmSet:newPass",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.passNew")));
			valid = false;
		}
		
		if(this.password[2] == null ||this.password[2].trim().equals("")) { // НОВА ПАРОЛА 2
			JSFUtils.addMessage("formAdmSet:newPassRep",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.pass1")));
			valid = false;
		}
		
		if(this.password[1] != null && this.password[2] != null && !this.password[1].equals(this.password[2])) { // ПАРОЛИТЕ ДА СЪВПАДАТ
			JSFUtils.addMessage("formAdmSet:newPassRep",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","pass.noMatch"));
			valid = false;
		}
		
		return valid;
	}

	
	/**
	 * Проверява дали новата парола отговаря на критериите за парола от RegistrationService.register в AAServer
	 * 	<ul>
	 * 		<li>да е поне 6 символа</li>
	 * 		<li>да не съдържа №\\\",.;:'%(){}[]^=<>|/`~ и шпации</li>
	 *  </ul>
	 * @return true, ако паролата е валидна
	 */
	private boolean validateNewPassword() {

		boolean valid = true;
		
		if(this.password[1].length() < 6) { // паролата е под 6 символа
			JSFUtils.addMessage("formAdmSet:newPass",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","pass.short"));
			valid = false;
		}
		if(!(new UserUtils()).isValidSimboli("№\\\",.;:'%(){}[]^=<>|/`~ ", this.password[1])) {
			JSFUtils.addMessage("formAdmSet:newPass",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","pass.symbols"));
			valid = false;
		}
		
		return valid;
	}
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */	
	
	public AdmUser getUser() {
		return user;
	}

	public void setUser(AdmUser user) {
		this.user = user;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String[] getPassword() {
		return password;
	}

	public void setPassword(String[] password) {
		this.password = password;
	}
	
	// ************************************************* END GET & SET ***************************************************** //	
	
}
