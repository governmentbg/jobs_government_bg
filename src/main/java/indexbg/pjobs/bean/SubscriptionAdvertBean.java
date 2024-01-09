package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Date;
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
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.Subscription;
import indexbg.pjobs.db.dao.SubscriptionDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("subAdvBean")
@ViewScoped
public class SubscriptionAdvertBean extends PJobsBean {

	private static final long serialVersionUID = 3891346664385955682L;

	static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionAdvertBean.class);
	
	private Long userId;
	private SubscriptionDAO subDao;
	private List<Subscription> subscriptions = new ArrayList<Subscription>();
	private Subscription newSubscription;
	private String newSubStrucText;
//	private LazyDataModelSQL2Array competitions;
	private LazyDataModelSQL2Array obyaviMobilnost;
	
	private String positionText;
	
	@PostConstruct
	public void initData() {
		
		LOGGER.debug("PostConstruct!");
		
		try {
			userId = getUserData().getUserId();
			subDao = new SubscriptionDAO();
			subscriptions = subDao.findByUserId(userId);
			newSubscription = new Subscription(userId);
			newSubscription.setType(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_ADVERTISEMENT_MOBILITY));
			
		//	competitions = subDao.getCompetitionsByUserId(userId);
			obyaviMobilnost = subDao.getAdvertSubsByUserId(userId);
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			userId = -1L;
		} catch (DbErrorException e) {
			e.printStackTrace();	
			userId = -1L;
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			userId = -1L;
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	/**
	 * Записва нов абонамент
	 */
	public void subscribe() {
		if(!validateFields()) {
			return;
		}
		
		if(!validateSubscription()) {
			clearForm();
			return;
		}
			
		this.newSubscription.setDateReg(new Date());
		this.newSubscription.setDateFrom(new Date());
		
		try {
			JPA.getUtil().begin();
			subDao.save(this.newSubscription);
			JPA.getUtil().commit();
			
			this.subscriptions = this.subDao.findByUserId(this.userId);
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error(e.getMessage(), e);
		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error(e.getMessage(), e);
		} finally {
			JPA.getUtil().closeConnection();
		}
		
		clearForm();
	}
	
	/**
	 * Изтрива абонамент
	 * @param index подава се от таблицата
	 */
	public void unsubscribe(int index) {
		try {
			JPA.getUtil().begin();
			subDao.deleteById(this.subscriptions.get(index).getId());
			JPA.getUtil().commit();
			
			this.subscriptions = this.subDao.findByUserId(this.userId);
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
	
	/**
	 * Проверява дали полетата са попълнени
	 * @return false, ако нещо не е попълнено вярно
	 */
	private boolean validateFields() {
		boolean valid = true;
		
		if(this.newSubscription.getType() == null) { // ВИД НА ОБЯВАТА
			JSFUtils.addMessage("formSubsc:newSubType",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "advertisement.vidObiava")));
			valid = false;
		}
		if(this.newSubscription.getAdministration() == null) { // АДМИНИСТРАЦИЯ
			JSFUtils.addMessage("formSubsc:newAdminStruc",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.admStruc")));
			valid = false;
		}
		
		return valid;
	}
	
	/**
	 * Проверява дали потребителят иска да се абонира повторно за едно и също нещо
	 * @return true, ако всичко е наред 
	 */
	private boolean validateSubscription() {
		for(Subscription s : this.subscriptions) {
			if(newSubscription.getAdministration().equals(s.getAdministration()) && newSubscription.getType().equals(s.getType())) {
				
				if(newSubscription.getType().longValue() == Constants.CODE_ZNACHENIE_TYPE_ADVERTISEMENT_JOB) {
				
					if(newSubscription.getPosition()== null || s.getPosition() == null) {
						JSFUtils.addMessage("formProfile:name",FacesMessage.SEVERITY_ERROR,"Вече сте абониран");
						clearForm();
						return false;
					} else if (newSubscription.getPosition().equals(s.getPosition())) {
					
						JSFUtils.addMessage("formProfile:name",FacesMessage.SEVERITY_ERROR,"Вече сте абониран");
						clearForm();
						return false;
					}
				
				} else {
					if( (newSubscription.getPosition()== null || s.getPosition() == null) && (newSubscription.getProfessionalField() == null || s.getProfessionalField() == null) )  { // ne sym siguren è e prawilno
						JSFUtils.addMessage("formProfile:name",FacesMessage.SEVERITY_ERROR,"Вече сте абониран");
						clearForm();
						return false;
					} else if (newSubscription.getPosition().equals(s.getPosition()) && newSubscription.getProfessionalField().equals(s.getProfessionalField())) {
					
						JSFUtils.addMessage("formProfile:name",FacesMessage.SEVERITY_ERROR,"Вече сте абониран");
						clearForm();
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public void actionChangeSubType() {
		newSubscription.setProfessionalField(null);
	}
	
	private void clearForm() {
		newSubscription = new Subscription(this.userId);
		newSubscription.setType(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_ADVERTISEMENT_MOBILITY));
		
		newSubStrucText = null;
		positionText = null;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Subscription getNewSubscription() {
		return newSubscription;
	}

	public void setNewSubscription(Subscription newSubscription) {
		this.newSubscription = newSubscription;
	}

	public String getNewSubStrucText() {
		return newSubStrucText;
	}

	public void setNewSubStrucText(String newSubStrucText) {
		this.newSubStrucText = newSubStrucText;
	}
//
//	public LazyDataModelSQL2Array getCompetitions() {
//		return competitions;
//	}
//
//	public void setCompetitions(LazyDataModelSQL2Array competitions) {
//		this.competitions = competitions;
//	}

	public LazyDataModelSQL2Array getObyaviMobilnost() {
		return obyaviMobilnost;
	}

	public void setObyaviMobilnost(LazyDataModelSQL2Array obyaviMobilnost) {
		this.obyaviMobilnost = obyaviMobilnost;
	}

	public String getPositionText() {
		return positionText;
	}

	public void setPositionText(String positionText) {
		this.positionText = positionText;
	}
}
