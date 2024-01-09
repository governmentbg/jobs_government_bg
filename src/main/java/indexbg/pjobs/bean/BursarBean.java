package indexbg.pjobs.bean;

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

import indexbg.pjobs.db.UsersBursar;
import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.db.dao.UsersBursarDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named ("bursarBean")
@ViewScoped
public class BursarBean extends PJobsBean{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4841307410700023844L;
	static final Logger LOGGER = LoggerFactory.getLogger(BursarBean.class);
    
	private Long pin;
	private Object[] bursar;
	private AdmUserDAO userDao;
    private Long idUser;
    private UsersBursar usersBursar;
    private UsersBursarDAO bursarDao;
    private boolean visButtSave = false ;

	public BursarBean() {
		
	}
	
	@PostConstruct
	public void init() {
		try {
			idUser = getUserData().getUserId();
			usersBursar = new UsersBursar();
			bursarDao = new UsersBursarDAO(idUser);			
			userDao = new AdmUserDAO(idUser); 
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}		
	}
	
	public void actionSearch() {
		
		if(pin==null) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.insertParameters"));
			return;
		}
		try {
			bursar = userDao.getUserByPin(pin);
			if(bursar!=null && bursar[1]!=null) {
				usersBursar = bursarDao.findByIdUser(Long.valueOf(bursar[1].toString()));				
			}
			if(usersBursar != null) { 
				if(usersBursar.getAdministration()!=null &&usersBursar.getAdministration()!= getUserData().getCodeOrg()) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "bursar.insert"));
					visButtSave = false;
				}
			}else {
				visButtSave= true;
				usersBursar = new UsersBursar();
				usersBursar.setUserId(Long.valueOf(bursar[1].toString()));
				usersBursar.setAdministration(getUserData().getCodeOrg());
			}
			
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при извличане на данни за стипендиант по въведен ПИН! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
		}
	}
	
    private boolean checkData() {
		
		boolean flagSave = false;
		if(usersBursar.getBursaryTerm()==null){
			JSFUtils.addMessage("bursarForm:bursaryTerm",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.bursaryTerm")));	
	
			flagSave=true;
		}
		
		if(usersBursar.getGraduationTerm()==null){
			JSFUtils.addMessage("bursarForm:gradTerm",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.graduationTerm")));	
	
			flagSave=true;
		}
				
		// Отпада - кандидата няма да се явява задължително на тест
//		if(usersBursar.getTestTerm()==null){
//			JSFUtils.addMessage("bursarForm:testTerm",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.testTerm")));	
//	
//			flagSave=true;
//		}
		
		if(usersBursar.getJobTerm()==null){
			JSFUtils.addMessage("bursarForm:jobTerm",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.jobTerm")));	
	
			flagSave=true;
		}

		return flagSave;
	}


    public void actionSave() {
		if(checkData()){
			return;
		}
		try {
		
			JPA.getUtil().begin();
			bursarDao.save(usersBursar);
			JPA.getUtil().commit();
					
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));	
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при запис на задължен субект! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
		}
		
	}
    
    public void actionNew() {
		
	}

	public Long getPin() {
		return pin;
	}

	public void setPin(Long pin) {
		this.pin = pin;
	}

	public Object[] getBursar() {
		return bursar;
	}

	public void setBursar(Object[] bursar) {
		this.bursar = bursar;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public UsersBursar getUsersBursar() {
		return usersBursar;
	}

	public void setUsersBursar(UsersBursar usersBursar) {
		this.usersBursar = usersBursar;
	}


	public boolean isVisButtSave() {
		return visButtSave;
	}

	public void setVisButtSave(boolean visButtSave) {
		this.visButtSave = visButtSave;
	}



}
