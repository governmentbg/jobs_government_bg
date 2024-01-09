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
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("delUserData")
@ViewScoped
public class DeleteUserDataBean extends PJobsBean {

	static final Logger LOGGER = LoggerFactory.getLogger(DeleteUserDataBean.class);
	
	private Long userId;
	private AdmUserDAO usersDAO;
	private LazyDataModelSQL2Array usersList = null;
	private String username;
	private String names;
	private String pin;
	
	@PostConstruct
	public void initData() {
		try {
			this.userId = getUserData().getUserId();
			this.usersDAO = new AdmUserDAO(userId);
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
	
	public void actionSearch() {
		try {
			SelectMetadata smd = this.usersDAO.searchUser(this.username, this.names, this.pin);
			this.usersList = new LazyDataModelSQL2Array(smd, "names");
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на потребители! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
		}
	}

	public void actionClear() {
		this.username = null;
		this.names = null;
		this.pin = null;
	}
	
	public LazyDataModelSQL2Array getUsersList() {
		return usersList;
	}

	public void setUsersList(LazyDataModelSQL2Array usersList) {
		this.usersList = usersList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}
