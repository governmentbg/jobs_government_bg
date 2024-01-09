package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.db.dao.PracticeLiceDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


/**
 * @author idineva
 *
 */
@Named
@ViewScoped
public class ReportUniversity extends PJobsBean{	
	

	private static final long serialVersionUID = -6096151774416920082L;

	static final Logger LOGGER = LoggerFactory.getLogger(ReportUniversity.class);
	
    private CampaignDAO campDao;
    private PracticeLiceDAO plDAO;
    private List<SelectItem> campList = new ArrayList<SelectItem>();
    private Long idCamp;
    private List<SelectItem> sprList = new ArrayList<SelectItem>();
    private Long typeSpr;
	private Long userId;
	private Long year;
	
	private List<Object[]> result = new ArrayList<Object[]>();
	
	/**
	 * Инициализира променливите в класа
	 */
	@PostConstruct
	public void initData(){
		
			try {
				
				String yearStr =JSFUtils.getRequestParameter("year");
				if(yearStr!=null && ValidationUtils.isNumber(yearStr)) {				
					year = Long.valueOf(yearStr) ;										
				}else {
					year = (long) Calendar.getInstance().get(Calendar.YEAR);
				}
				
				this.userId = getUserData().getUserId();				
				campDao = new CampaignDAO(userId);
				plDAO = new PracticeLiceDAO(userId);
				
				String idcampStr =JSFUtils.getRequestParameter("idcamp");							
				if (idcampStr!=null && ValidationUtils.isNumber(idcampStr)) {		
					idCamp = Long.valueOf(idcampStr) ;											
				}else {
					idCamp = campDao.findLastCampaign();
				}
															
				campList = campDao.findActiveCampaigns(false);
				sprList.add(new SelectItem(1,getMessageResourceString("labels", "reportUniversity.countStudents")));
				sprList.add(new SelectItem(2,getMessageResourceString("labels", "reportUniversity.countStudentsArea")));
				sprList.add(new SelectItem(3,getMessageResourceString("labels", "reportUniversity.countPossitionsArea")));
				//sprList.add(new SelectItem(4,getMessageResourceString("labels", "reportUniversity.countPossitionsStruct")));
				
				String typeSprStr =JSFUtils.getRequestParameter("type");	
				if(typeSprStr!=null && !"".equals(typeSprStr)) {
					
						typeSpr =Long.valueOf(typeSprStr);						
					
					
					actionSearch();
				}
				
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			} finally {
				JPA.getUtil().closeConnection();
			}	
	}
	
	
	/**
	 * Метод за изпълнение на справката
	 */
	public void actionSearch(){
		
		try {
			if(typeSpr.equals(1L)) {
				result = plDAO.reportStrudentUniversity(idCamp,year);
			}else if(typeSpr.equals(2L)) {
				result = plDAO.reportStrudentEducationArea(idCamp,year);
			}else if(typeSpr.equals(3L)) {
				result = new PracticeDAO(userId).reportPossitionsByArea(idCamp,year);
			}
													
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на брой кандидати по университети! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	/**
	 * Зачистване на зададените от потребителя стойности за търсене
	 */
	public void actionClear(){
 		idCamp = null;
 		typeSpr = Constants.CODE_ZNACHENIE_DA;
 		result = new ArrayList<Object[]>();
 			
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


	public List<SelectItem> getCampList() {
		return campList;
	}


	public void setCampList(List<SelectItem> campList) {
		this.campList = campList;
	}


	public List<SelectItem> getSprList() {
		return sprList;
	}


	public void setSprList(List<SelectItem> sprList) {
		this.sprList = sprList;
	}

	public List<Object[]> getResult() {
		return result;
	}

	public void setResult(List<Object[]> result) {
		this.result = result;
	}

	public Long getIdCamp() {
		return idCamp;
	}

	public void setIdCamp(Long idCamp) {
		this.idCamp = idCamp;
	}

	public Long getTypeSpr() {
		return typeSpr;
	}

	public void setTypeSpr(Long typeSpr) {
		this.typeSpr = typeSpr;
	}


	public Long getYear() {
		return year;
	}


	public void setYear(Long year) {
		this.year = year;
	}
		
}
