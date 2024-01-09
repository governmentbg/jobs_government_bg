package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.db.dao.PracticeLiceDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class ReportsVacantPositions extends PJobsBean {	
	
	/** Справки за класиране на кандидати за стаж
	 * 
	 */
	private static final long serialVersionUID = -6304116479883770105L;

	static final Logger LOGGER = LoggerFactory.getLogger(ReportsVacantPositions.class);	
	
	private Long userId;
	private PracticeLiceDAO practiceLiceDao;
	private CampaignDAO campDao;	
	
	private Long adminStruc;
	private String adminStrucText;
	private Long idCamp;
	private Long numPractice;
	
	private List<SelectItem> campaignsList;
	
	private List<Object[]> reportsList;	
	
	public ReportsVacantPositions() {
		
	}
	
	/** Иницира първоначалните стойности
	 * 
	 * 
	 */
	@PostConstruct
	public void init() {
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.practiceLiceDao = new PracticeLiceDAO(userId);
			this.campDao = new CampaignDAO(userId);
			
			this.campaignsList = new ArrayList<>();
			
			this.campaignsList = this.campDao.findAllCampaigns();	
			this.idCamp = this.campDao.findLastCampaign();
			
			this.reportsList = new ArrayList<>();
			
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
	
	/** Метод за търсене на класиране на кандидати за стаж
	 * 
	 * 
	 */
	public void actionSearch(){
		
		try {			
						
			this.reportsList = practiceLiceDao.findVacantPositions(this.adminStruc, this.idCamp, this.numPractice);
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на класирани кандидати за стаж! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		} finally {
			JPA.getUtil().closeConnection();
		}		
		
	}
	
	/**
	 * Зачистване на зададените от потребителя стойности за търсене
	 */
	public void actionClear(){
		
		try {
       
			this.adminStruc = null;
			this.adminStrucText = "";
			this.idCamp = this.campDao.findLastCampaign();
			this.numPractice = null;
			
			this.reportsList = new ArrayList<>();			
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при тнамиране на последната активна кампания! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		} finally {
			JPA.getUtil().closeConnection();
		}	
	}
	
	
	// ************************************************* GET & SET ***************************************************** //

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getAdminStruc() {
		return adminStruc;
	}

	public void setAdminStruc(Long adminStruc) {
		this.adminStruc = adminStruc;
	}

	public String getAdminStrucText() {
		return adminStrucText;
	}

	public void setAdminStrucText(String adminStrucText) {
		this.adminStrucText = adminStrucText;
	}

	public Long getIdCamp() {
		return idCamp;
	}

	public void setIdCamp(Long idCamp) {
		this.idCamp = idCamp;
	}

	public Long getNumPractice() {
		return numPractice;
	}

	public void setNumPractice(Long numPractice) {
		this.numPractice = numPractice;
	}

	public List<SelectItem> getCampaignsList() {
		return campaignsList;
	}

	public void setCampaignsList(List<SelectItem> campaignsList) {
		this.campaignsList = campaignsList;
	}

	public List<Object[]> getReportsList() {
		return reportsList;
	}

	public void setReportsList(List<Object[]> reportsList) {
		this.reportsList = reportsList;
	}

	
	
	// ************************************************* END GET & SET ***************************************************** //	
	
}
