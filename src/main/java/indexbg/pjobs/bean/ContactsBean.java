package indexbg.pjobs.bean;

import java.util.List;

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

import indexbg.pjobs.db.Contacts;
import indexbg.pjobs.db.dao.ContactsDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named ("contactsBean")
@ViewScoped
public class ContactsBean extends PJobsBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2324768526029212577L;
	static final Logger LOGGER = LoggerFactory.getLogger(ContactsBean.class);
	private Contacts contacts;
	private Long userId;
	private ContactsDAO cDao;
	


	public ContactsBean() {
		
	}
	
	@PostConstruct
	public void init() {
		contacts = new Contacts();
		
		try {
			this.userId = Constants.PORTAL_USER;
			cDao =  new ContactsDAO(userId);	
			List<Contacts> contactsList = cDao.findAll();
			if(contactsList!=null && contactsList.size()>0) {
				contacts = cDao.findAll().get(0);
			}else {
				contacts = new Contacts();//защото може някой да изтрие записа				
			}
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			this.userId = -1L;
		} finally {
			JPA.getUtil().closeConnection();
		}	
	}
    private boolean checkData() {
		
		boolean flagSave = false;
		if(contacts.getEmails()==null || "".equals(contacts.getEmails().trim())){
			JSFUtils.addMessage("contactsForm:email",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.email")));	
	
			flagSave=true;
		}
		
		if(contacts.getPhones()==null || "".equals(contacts.getPhones().trim())){
			JSFUtils.addMessage("contactsForm:telefon",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.telefon")));	
	
			flagSave=true;
		}
		
		if(contacts.getFax()==null || "".equals(contacts.getFax().trim())){
			JSFUtils.addMessage("contactsForm:fax",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.fax")));	
	
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
			cDao.save(contacts);
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

	public Contacts getContacts() {
		return contacts;
	}

	public void setContacts(Contacts contacts) {
		this.contacts = contacts;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
