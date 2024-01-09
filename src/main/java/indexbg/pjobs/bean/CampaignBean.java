package indexbg.pjobs.bean;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.ValidationUtils;

import java.util.List;
import indexbg.pjobs.db.Campaign;
import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named
@ViewScoped
public class CampaignBean extends PJobsBean{
	
	private static final long serialVersionUID = 8614463026818156909L;
	static final Logger LOGGER = LoggerFactory.getLogger(CampaignBean.class);
	private Campaign camp;
	private Long userId;
	private CampaignDAO campDao;
	private Long statusTmp;//за актуализация на датата на статуса
	private boolean disabledDates = false; // ако вече има въведени стаове,да не могат да се редактират датите,които се записват в стажовете 
	@PostConstruct
	public void init() {
			
		try {
			this.userId = getUserData().getUserId();
			
			campDao = new CampaignDAO(userId);
			
			String idObj =JSFUtils.getRequestParameter("idObj");			
			
			if (idObj!=null) {
				if (ValidationUtils.isNumber(idObj)) {
					camp = campDao.findById(Long.valueOf(idObj));
					if(camp!=null) {
						statusTmp = camp.getStatus();
						 List<Object[]> practices = new PracticeDAO(userId).findPracticesByCamp(camp.getId());
						 if(practices!=null && !practices.isEmpty()) {
							 disabledDates = true;
						 }
					}else {
						redirectExternal("pageNotFound.jsf");
					}				
				} else {
					redirectExternal("pageNotFound.jsf");				
				}
			} else {
				actionNew();			
			}
		
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
    private boolean checkData() {
		
		boolean flagSave = false;
		
		if(camp.getTypeCamp()==null){
			JSFUtils.addMessage("campForm:type",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.vid")));		
			flagSave=true;
		}
		
		if(camp.getNameCamp()==null || "".equals(camp.getNameCamp())){
			JSFUtils.addMessage("campForm:name",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.name")));		
			flagSave=true;
		}
		
		if(camp.getStatus()==null){
			JSFUtils.addMessage("campForm:status",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.status")));		
			flagSave=true;
		}
					
//		if(camp.getStatusDate()==null){
//			JSFUtils.addMessage("campForm:dateStatus",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "userEdit.dateStat")));		
//			flagSave=true;
//		}else if(camp.getId()==null && DateUtils.startDate(camp.getStatusDate()).before(DateUtils.startDate(getToday()))) {
//			JSFUtils.addMessage("campForm:dateStatus",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.dateStatusBeforeToday"));				
//			flagSave=true;
//		}
		if(camp.getTypeCamp()!=null && camp.getTypeCamp().equals(Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR)) { /// задължителни са само при централизирана кампания
			
			if(camp.getStructureDateFrom()==null){
				JSFUtils.addMessage("campForm:structureDateFrom",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campagn.dateBegInputPractice")));	
		
				flagSave=true;
			} 
			
			if(camp.getStructureDateTo()==null){
				JSFUtils.addMessage("campForm:structureDateTo",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.endDateInputPractice")));	
		
				flagSave=true;
			}
			
			if(camp.getRankingDate()==null){
				JSFUtils.addMessage("campForm:rankingDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.rankingDate")));	
		
				flagSave=true;
			}
			
			if(camp.getConfirmDateFrom()==null){
				JSFUtils.addMessage("campForm:begDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.beginDateConf")));	
		
				flagSave=true;
			} 
			
			if(camp.getConfirmDateTo()==null){
				JSFUtils.addMessage("campForm:endDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.endDateConf")));	
		
				flagSave=true;
			}
			
			if(camp.getRankingDate2()==null){
				JSFUtils.addMessage("campForm:rankingDate2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.rankingDate2")));	
		
				flagSave=true;
			}
			
			if(camp.getConfirmDateFrom2()==null){
				JSFUtils.addMessage("campForm:begDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.beginDateConf2")));	
		
				flagSave=true;
			} 
			
			if(camp.getConfirmDateTo2()==null){
				JSFUtils.addMessage("campForm:endDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.endDateConf2")));	
		
				flagSave=true;
			}
		}
		
		if(camp.getDateFrom()==null){
			JSFUtils.addMessage("campForm:begDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.beginDateApply")));	
	
			flagSave=true;
		}
		
		if(camp.getDateTo()==null){
			JSFUtils.addMessage("campForm:endDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.endDateApply")));	
	
			flagSave=true;
		}
		
		if(camp.getId()==null) {
			if(camp.getDateFrom() !=null && DateUtils.startDate(camp.getDateFrom()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("campForm:begDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.begDateBeforeToday"));	
				
				flagSave=true;
			}
			
			if(camp.getRankingDate() != null && DateUtils.startDate(camp.getRankingDate()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("campForm:rankingDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.dateRankingBeforeToday"));	
				
				flagSave=true;
			}
			
			if(camp.getConfirmDateFrom()!=null && DateUtils.startDate(camp.getConfirmDateFrom()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("campForm:begDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.begDateConfBeforeToday"));	
				
				flagSave=true;
			}	
			
			if(camp.getRankingDate2() != null && DateUtils.startDate(camp.getRankingDate2()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("campForm:rankingDate2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.dateRankingBeforeToday2"));	
				
				flagSave=true;
			}
			
			if(camp.getConfirmDateFrom2()!=null && DateUtils.startDate(camp.getConfirmDateFrom2()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("campForm:begDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.begDateConfBeforeToday2"));	
				
				flagSave=true;
			}	
		}
		if(camp.getStructureDateFrom()!=null && camp.getStructureDateTo()!=null && camp.getStructureDateTo().before(camp.getStructureDateFrom())) {
			JSFUtils.addMessage("campForm:structureDateFrom",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaignBean.dateFromStructLessDateTo"));	
			
			flagSave=true;
		}
		
		if(camp.getDateFrom()!=null && camp.getDateTo()!=null && camp.getDateTo().before(camp.getDateFrom())) {
			JSFUtils.addMessage("campForm:begDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "general.dateFromLessDateTo"));	
			
			flagSave=true;
		}
		
		if(camp.getConfirmDateFrom()!=null && camp.getConfirmDateTo()!=null && camp.getConfirmDateTo().before(camp.getConfirmDateFrom())) {
			JSFUtils.addMessage("campForm:begDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateConfAfretEndDate"));	
			
			flagSave=true;
		}
		
		if(camp.getConfirmDateFrom2()!=null && camp.getConfirmDateTo2()!=null && camp.getConfirmDateTo2().before(camp.getConfirmDateFrom2())) {
			JSFUtils.addMessage("campForm:begDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateConfAfretEndDate2"));	
			
			flagSave=true;
		}
		
		if(camp.getStructureDateTo()!=null && camp.getDateFrom()!=null && (camp.getDateFrom().before(camp.getStructureDateTo()) || camp.getDateFrom().equals(camp.getStructureDateTo()) )) {
			JSFUtils.addMessage("campForm:begDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateBeforeStructureDate"));	
			
			flagSave=true;
		}
		
		if(camp.getDateTo()!=null && camp.getRankingDate()!=null && (camp.getRankingDate().before(camp.getDateTo()) ||camp.getRankingDate().equals(camp.getDateTo()) )) {
			JSFUtils.addMessage("campForm:rankingDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.rankingDateBeforeApplyDate"));	
			
			flagSave=true;
		}
		
		if(camp.getConfirmDateFrom()!=null && camp.getRankingDate()!=null && (camp.getConfirmDateFrom().before(camp.getRankingDate()) || camp.getConfirmDateFrom().equals(camp.getRankingDate()))) {
			JSFUtils.addMessage("campForm:begDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateConfBeforeRankingDate"));	
			
			flagSave=true;
		}
		
		if(camp.getConfirmDateTo()!=null && camp.getRankingDate2()!=null && (camp.getRankingDate2().before(camp.getConfirmDateTo()) || camp.getRankingDate2().equals(camp.getConfirmDateTo()) )) {
			JSFUtils.addMessage("campForm:rankingDate2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.rankingDate2BeforeConfirmDateTo"));	
			
			flagSave=true;
		}
		
		if(camp.getConfirmDateFrom2()!=null && camp.getRankingDate2()!=null && (camp.getConfirmDateFrom2().before(camp.getRankingDate2()) || camp.getConfirmDateFrom2().equals(camp.getRankingDate2()))) {
			JSFUtils.addMessage("campForm:begDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateConf2BeforeRankingDate2"));	
			
			flagSave=true;
		}

		return flagSave;
	}


    public void actionSave() {
		if(checkData()){
			return;
		}
		try {

		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(camp.getDateFrom());
		    camp.setYear(SearchUtils.asLong(calendar.get(Calendar.YEAR)));
	
		    if(statusTmp!=null && !statusTmp.equals(camp.getStatus())) {
		    	camp.setStatusDate(getToday());
		    }
			JPA.getUtil().begin();
			campDao.save(camp);		
					
			JPA.getUtil().commit();
					
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));	
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при запис на кампания! ", e);
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
		camp = new Campaign();	
		//camp.setTypeCamp(Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR);
		statusTmp = null;
		camp.setStatusDate(getToday());
	}

	
    public void actionDelete(){
		if(camp.getId()!=null){
			try {
				JPA.getUtil().begin();
				
				if(disabledDates) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "campaign.cannotDeleteCamp"));	
				}else{
					campDao.delete(camp);
					JPA.getUtil().commit();				
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));	
					actionNew();
				}
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при изтриване на кампания! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			}finally {
				JPA.getUtil().closeConnection();
			}
			
			
		}		
	}
    
    //Изчисляване на крайна дата на кампания спрямо началната дата и системната настройка  PJOBS.PERIOD.VALIDNOST.STAJ
    public void actionSetDateTo() {
    	if(camp.getDateFrom()!=null) {
    		String period = getSystemData().getSettingsValue("PJOBS.PERIOD.VALIDNOST.STAJ");
    		if(period!=null && !"".equals(period)&& ValidationUtils.isNumber(period)) {
    			  Calendar c = Calendar.getInstance();
    		      c.setTime(camp.getDateFrom());
    		      c.add(Calendar.DATE, Integer.valueOf(period));
    		      camp.setDateTo(c.getTime());
    		}
    	}
    }
    
  
    public void actionSetDateToConfirm() {
    	if(camp.getConfirmDateFrom()!=null) {
    		String period = getSystemData().getSettingsValue("PJOBS.PERIOD.POTVARJDAVANE.STAJ");
    		if(period!=null && !"".equals(period)&& ValidationUtils.isNumber(period)) {
    			  Calendar c = Calendar.getInstance();
    		      c.setTime(camp.getConfirmDateFrom());
    		      c.add(Calendar.DATE, Integer.valueOf(period));
    		      camp.setConfirmDateTo(c.getTime());
    		}
    	}
    }
    
    public void actionSetDateToConfirm2() {
    	if(camp.getConfirmDateFrom2()!=null) {
    		String period = getSystemData().getSettingsValue("PJOBS.PERIOD.POTVARJDAVANE.STAJ");
    		if(period!=null && !"".equals(period)&& ValidationUtils.isNumber(period)) {
    			  Calendar c = Calendar.getInstance();
    		      c.setTime(camp.getConfirmDateFrom2());
    		      c.add(Calendar.DATE, Integer.valueOf(period));
    		      camp.setConfirmDateTo2(c.getTime());
    		}
    	}
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
	public Campaign getCamp() {
		return camp;
	}
	public void setCamp(Campaign camp) {
		this.camp = camp;
	}

	public boolean isDisabledDates() {
		return disabledDates;
	}
	public void setDisabledDates(boolean disabledDates) {
		this.disabledDates = disabledDates;
	}

}
