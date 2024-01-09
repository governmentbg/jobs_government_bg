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

import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class UserMailMessages extends PJobsBean{	
	
	/** Изкарва списък с всички съобщения, които са изпратени до потребителя.
	 * 
	 * 
	 */
	private static final long serialVersionUID = 4731408936701532164L;

	static final Logger LOGGER = LoggerFactory.getLogger(UserMailMessages.class);	
	
	private MailDAO mailDAO;
	private LazyDataModelSQL2Array userMailsList = null;
	private Long userId;
	private String email;
	
	/** Иницира първоначалните стойности на списъка със съобщения
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.mailDAO = new MailDAO(this.userId); 
			this.email = getUserData().getEmailUser();
			
			actionSearch();
		
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
	
	
	/** Метод за търсене на на обяви за мобилност
	 * 
	 * 
	 */
	public void actionSearch(){
		
		try {
		
			SelectMetadata smd = this.mailDAO.findMailsByUser(userId, email);
			String defaultSortColumn = "A4";	
			this.userMailsList = new LazyDataModelSQL2Array(smd, defaultSortColumn);
		
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на обяви за мобилност! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	
	/** Метод за зачистване на зададените стойности за търсене
	 * 
	 * 
	 */
	public void actionClear(){
		
		this.userMailsList = null;
		
	}
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */
	
	public LazyDataModelSQL2Array getUserMailsList() {
		return userMailsList;
	}


	public void setUserMailsList(LazyDataModelSQL2Array userMailsList) {
		this.userMailsList = userMailsList;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}	
	
	// ************************************************* END GET & SET ***************************************************** //	
	
}
