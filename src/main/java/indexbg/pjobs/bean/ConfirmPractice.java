package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.Practice;
import indexbg.pjobs.db.PracticeLice;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.db.dao.PracticeLiceDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named
@ViewScoped
public class ConfirmPractice extends PJobsBean{
	
	private static final long serialVersionUID = 8614463026818156909L;
	static final Logger LOGGER = LoggerFactory.getLogger(ConfirmPractice.class);

	private Long userId;
	//обявите за стаж,за които е кандидатсвал
	private List<Object[]> practiceList =  new ArrayList<Object[]>();
	private PracticeLiceDAO practLiceDao;
	private boolean active = true;
	private Practice selectedPractice = new Practice();
	
	@PostConstruct
	public void init() {
			
		try {
			this.userId = getUserData().getUserId();
			practLiceDao = new PracticeLiceDAO(userId);
			practiceList = practLiceDao.findPracticesByUserAplly(userId, null, null,active ,true);
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));					
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		} finally {
			JPA.getUtil().closeConnection();
		}	
	}

	
	//потребителят потвърждава участието си в стаж,за който е одобрен на първо или второ класиране
		public void actionConfPractice() {
			try {
				String idObj =JSFUtils.getRequestParameter("idObj");			
				PracticeLice practLice = new PracticeLice();
				if (idObj!=null) {
					if (ValidationUtils.isNumber(idObj)) {
						practLice = practLiceDao.findById(Long.valueOf(idObj));
						if(practLice!=null) {
							practLice.setStatus(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED);
							practLice.setAcceptDate(getToday());
							practLice.setStatusDate(getToday());
							
							JPA.getUtil().begin();
							practLiceDao.save(practLice);										
							JPA.getUtil().commit();								
							JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "practiceConfirm.sucsessConf"));	
							practiceList = practLiceDao.findPracticesByUserAplly(userId, null, null,active ,true);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			} finally {
				JPA.getUtil().closeConnection();
			}	
	}
		

    public void actionSeachPractices() {
    	try {
			practiceList = practLiceDao.findPracticesByUserAplly(userId, null, null,active,true);
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при работа с базата данни!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		} 
    }
    
    public void onRowToggle(ToggleEvent event) {
 	   Object row []  = (Object[]) event.getData();
   
 	   if(event.getVisibility() == Visibility.VISIBLE && row!=null) {
 		   try {
 			   
 			   selectedPractice = new PracticeDAO(userId).findById(SearchUtils.asLong(row[0]));
 			  			 
 		   } catch (DbErrorException e) {
 				LOGGER.error("Грешка при търсене на заявления! ", e);
 				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
 			} catch (Exception e) {
 				LOGGER.error("Грешка при работа със системата!!!", e);	
 				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
 			
 			} finally {
 				JPA.getUtil().closeConnection();
 			}
 	   } 
 	}
    
	public List<Object[]> getPracticeList() {
		return practiceList;
	}

	public void setPracticeList(List<Object[]> practiceList) {
		this.practiceList = practiceList;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getToday(){
		return new Date();
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public Practice getSelectedPractice() {
		return selectedPractice;
	}


	public void setSelectedPractice(Practice selectedPractice) {
		this.selectedPractice = selectedPractice;
	}
	
}
