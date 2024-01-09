package indexbg.pjobs.bean;

import java.text.SimpleDateFormat;
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
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.Campaign;
import indexbg.pjobs.db.Practice;
import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;
import indexbg.pjobs.system.UserData;


@Named
@ViewScoped
public class PracticeBean extends PJobsBean{
	

	private static final long serialVersionUID = -2586668251908064927L;
	static final Logger LOGGER = LoggerFactory.getLogger(PracticeBean.class);
	private Practice practice;
	private Long userId;
	private PracticeDAO practiceDao;
	private CampaignDAO campDao;
	private List<SelectItem> campList = new ArrayList<SelectItem>();
	UserData userData;
	// Звено
	private String unitText;
	private Long unit;
	//Админ.структура
	private Long adm;
	private String admText;
	// ЕКАТТЕ
	private List<SystemClassif> oblastList;
	private List<SystemClassif> obshtiniList = new ArrayList<SystemClassif>();
	private List<SystemClassif> nasMestoList = new ArrayList<SystemClassif>();

	//заради датите,които се извличат от кампанията. Ако ги има,не могада т да се редактират. Иначе се попълват нови
	private Campaign camp = new Campaign();
	//области на висше образование 
	private List<SystemClassif> educationAreaList = new ArrayList<SystemClassif>();
	
	private boolean edit = true; //ако е изтекъл периодът за въвеждане на обява,да не могат да редактират
	
	private boolean admin= false; //дали е администратор от МС 
		
	@PostConstruct
	public void init() {
			
		try {
			
			userData = getUserData();
			userId = userData.getUserId();
			campDao = new CampaignDAO(userId);
			practiceDao = new PracticeDAO(userId);
			if(userData.getTypeUser().longValue()==Constants.CODE_ZNACHENIE_TIP_POTR_VATR) {
				admin= true;										
			}
			String idObj =JSFUtils.getRequestParameter("idObj");			
			loadCategories();
			if (idObj!=null) {
				
				if (ValidationUtils.isNumber(idObj)) {
					practice = practiceDao.findById(Long.valueOf(idObj));
					if(practice==null) {
						redirectExternal("pageNotFound.jsf");
					}else { 
						//campList = campDao.findActiveCampaigns(false);	
						camp = campDao.findById(practice.getIdCampaign());
						filEkateList(obshtiniList, practice.getRegion());
						filEkateList(nasMestoList, practice.getMunicipality());
						unit = practice.getUnit();
						if(unit!=null) {
							unitText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, unit, getCurrentLang(), new Date(), this.userId);
						}
						adm = practice.getAdministration();
						if(adm!=null) {
							this.admText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, adm, getCurrentLang(), new Date(), userId);
						}
						//ако периодът е изтекъл
						if(practice.getStructureDateTo()!=null && DateUtils.startDate(practice.getStructureDateTo()).before(DateUtils.startDate(getToday()))  ) {
							edit = false;
							//JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "practice.inputPeriodEnd"));																		
						}
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
	
	public void loadCategories() {
		
		fillSysClassifList(this.educationAreaList, 0L, Constants.CODE_SYSCLASS_SUBJECT, false);		
		this.educationAreaList.sort((a, b) -> a.getTekst().compareTo(b.getTekst()));
	}
	
	/**
	 * @param list - списъкът, който да се напълни
	 * @param parentCode - код на родителския възел
	 * @param sysClassifCode - код на класификацията
	 * @param clearList - дали съдържанието на подадения списък да се изтрие или да се добавя
	 */
	public void fillSysClassifList(List<SystemClassif> list, Long parentCode, Long sysClassifCode, boolean clearList) {
		
		if(list == null) {
			list = new ArrayList<SystemClassif>();
		}
		
		if(clearList) {
			list.clear();
		}
		
		if(parentCode == null){
			return;
		}
		
		try {
			list.addAll(getSystemData().getChildrenOnNextLevel(sysClassifCode, parentCode.longValue(), new Date(), getCurrentLang(), userId));
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		} catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		} finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	
	public void actionSelectCamp() {
		if(practice.getIdCampaign()!=null) {
			
			try {				
				camp = campDao.findById(practice.getIdCampaign());
				//мантис 0016646 при децентрализирана кампяния да не се прехвърлят датите
				if(camp!=null && camp.getTypeCamp() == Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR) {
					practice.setDateFrom(camp.getDateFrom());
					practice.setDateTo(camp.getDateTo());
					practice.setConfirmDateFrom(camp.getConfirmDateFrom());
					practice.setConfirmDateTo(camp.getConfirmDateTo());
					practice.setRankingDate(camp.getRankingDate());
					practice.setConfirmDateFrom2(camp.getConfirmDateFrom2());
					practice.setConfirmDateTo2(camp.getConfirmDateTo2());
					practice.setRankingDate2(camp.getRankingDate2());
					practice.setStructureDateFrom(camp.getStructureDateFrom());
					practice.setStructureDateTo(camp.getStructureDateTo());
				}
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при работа с базата данни! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			}catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));			
			} finally {
				JPA.getUtil().closeConnection();
			}
		}else {
			practice.setDateFrom(null);
			practice.setDateTo(null);
			practice.setConfirmDateFrom(null);
			practice.setConfirmDateTo(null);
			practice.setRankingDate(null);
			practice.setConfirmDateFrom2(null);
			practice.setConfirmDateTo2(null);
			practice.setRankingDate2(null);
			practice.setStructureDateFrom(null);
			practice.setStructureDateTo(null);
		}
	}
	
    private boolean checkData() {
		
		boolean flagSave = false;
			
		if(practice.getIdCampaign()==null){
			JSFUtils.addMessage("practiceForm:idCampaign",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "practice.campaign")));		
			flagSave=true;
		} 
		
		if(practice.getNum()==null){
			JSFUtils.addMessage("practiceForm:num",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "advertisement.brMesta")));		
			flagSave=true;
		}
		
//		if(practice.getStatus()==null){
//			JSFUtils.addMessage("practiceForm:status",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.status")));		
//			flagSave=true;
//		}
		
//		if(practice.getStatusDate()==null){
//			JSFUtils.addMessage("practiceForm:dateStatus",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "userEdit.dateStat")));		
//			flagSave=true;
//		}else if(practice.getId()==null && DateUtils.startDate(practice.getStatusDate()).before(DateUtils.startDate(getToday()))) {
//			JSFUtils.addMessage("practiceForm:dateStatus",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.dateStatusBeforeToday"));				
//			flagSave=true;
//		}
		
		if(practice.getRegion()==null){
			JSFUtils.addMessage("practiceForm:oblast",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.oblast")));		
			flagSave=true;
		}
		
		if(practice.getMunicipality()==null){
			JSFUtils.addMessage("practiceForm:obshtina",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.obshtina")));		
			flagSave=true;
		}
		
		if(practice.getTown()==null){
			JSFUtils.addMessage("practiceForm:nasMesto",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.nasMiasto")));		
			flagSave=true;
		}
		
		if(practice.getEducationArea()==null ){
			JSFUtils.addMessage("practiceForm:eduArea",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "practice.educationArea")));		
			flagSave=true;
		}
		//ще се генерира автоматично
//		if(practice.getPracticeTitle()==null || "".equals(practice.getPracticeTitle())){
//			JSFUtils.addMessage("practiceForm:practiceTitle",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "publ.title")));		
//			flagSave=true;
//		}
									
		if(camp!=null && camp.getTypeCamp()==Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_DECENTR) {
			
			if(practice.getDateFrom()==null){
				JSFUtils.addMessage("practiceForm:begDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "practice.begDate")));	
		
				flagSave=true; 
				
			}else if(practice.getId()==null && DateUtils.startDate(practice.getDateFrom()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("practiceForm:begDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","practice.begDateAdBeforeToday"));	
				
				flagSave=true;
			}
			
			if(practice.getDateTo()==null){
				JSFUtils.addMessage("practiceForm:endDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "practice.endDate")));	
		
				flagSave=true;
			}

			if(practice.getDateFrom()!=null && practice.getDateTo()!=null && practice.getDateTo().before(practice.getDateFrom())) {
				JSFUtils.addMessage("practiceForm:begDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "general.dateFromLessDateTo"));	
				
				flagSave=true;
			}
			
			if(practice.getStructureDateTo()!=null && practice.getDateFrom()!=null && (practice.getDateFrom().before(practice.getStructureDateTo()) || practice.getDateFrom().equals(practice.getStructureDateTo()) )) {
				JSFUtils.addMessage("practiceForm:begDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "practice.begDatebeforeStructureDateTo"));	
				
				flagSave=true;
			}
			
			if(practice.getDateTo()!=null && practice.getRankingDate()!=null && (practice.getRankingDate().before(practice.getDateTo()) ||practice.getRankingDate().equals(practice.getDateTo()) )) {
				JSFUtils.addMessage("practiceForm:rankingDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.rankingDateBeforeApplyDate"));	
				
				flagSave=true;
			}
			
			if(practice.getConfirmDateFrom()!=null && practice.getRankingDate()!=null && (practice.getConfirmDateFrom().before(practice.getRankingDate()) || practice.getConfirmDateFrom().equals(practice.getRankingDate()))) {
				JSFUtils.addMessage("practiceForm:begDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateConfBeforeRankingDate"));	
				
				flagSave=true;
			}
			
			if(practice.getConfirmDateTo()!=null && practice.getRankingDate2()!=null && (practice.getRankingDate2().before(practice.getConfirmDateTo()) || practice.getRankingDate2().equals(practice.getConfirmDateTo()) )) {
				JSFUtils.addMessage("practiceForm:rankingDate2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.rankingDate2BeforeConfirmDateTo"));	
				
				flagSave=true;
			}
			
			if(practice.getConfirmDateFrom2()!=null && practice.getRankingDate2()!=null && (practice.getConfirmDateFrom2().before(practice.getRankingDate2()) || practice.getConfirmDateFrom2().equals(practice.getRankingDate2()))) {
				JSFUtils.addMessage("practiceForm:begDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateConf2BeforeRankingDate2"));	
				
				flagSave=true;
			}
					
			if(practice.getRankingDate()==null){
				JSFUtils.addMessage("practiceForm:rankingDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.rankingDate")));	
		
				flagSave=true;
				
			}else if(practice.getId()==null && DateUtils.startDate(practice.getRankingDate()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("practiceForm:rankingDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.dateRankingBeforeToday"));	
				
				flagSave=true;
			}
			
			if(practice.getConfirmDateFrom()==null){
				JSFUtils.addMessage("practiceForm:begDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.beginDateConf")));	
		
				flagSave=true;
				
			}else if(practice.getId()==null && DateUtils.startDate(practice.getConfirmDateFrom()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("practiceForm:begDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.begDateConfBeforeToday"));	
				
				flagSave=true;
			}
			
			if(practice.getConfirmDateTo()==null){
				JSFUtils.addMessage("practiceForm:endDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.endDateConf")));	
		
				flagSave=true;
			}

			if(practice.getConfirmDateFrom()!=null && practice.getConfirmDateTo()!=null && practice.getConfirmDateTo().before(practice.getConfirmDateFrom())) {
				JSFUtils.addMessage("practiceForm:begDateConf",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateConfAfretEndDate"));	
				
				flagSave=true;
			}
			
			if(practice.getRankingDate2()==null){
				JSFUtils.addMessage("practiceForm:rankingDate2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.rankingDate2")));	
		
				flagSave=true;
				
			}else if(practice.getId()==null && DateUtils.startDate(practice.getRankingDate2()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("practiceForm:rankingDate2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.dateRankingBeforeToday2"));	
				
				flagSave=true;
			}
			
			if(practice.getConfirmDateFrom2()==null){
				JSFUtils.addMessage("practiceForm:begDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.beginDateConf2")));	
		
				flagSave=true;
				
			}else if(practice.getId()==null && DateUtils.startDate(practice.getConfirmDateFrom2()).before(DateUtils.startDate(getToday()))) {
				JSFUtils.addMessage("practiceForm:begDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","campaign.begDateConfBeforeToday2"));	
				
				flagSave=true;
			}
			
			if(practice.getConfirmDateTo2()==null){
				JSFUtils.addMessage("practiceForm:endDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "campaign.endDateConf2")));	
		
				flagSave=true;
			}

			if(practice.getConfirmDateFrom2()!=null && practice.getConfirmDateTo2()!=null && practice.getConfirmDateTo2().before(practice.getConfirmDateFrom2())) {
				JSFUtils.addMessage("practiceForm:begDateConf2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "campaign.begDateConfAfretEndDate2"));	
				
				flagSave=true;
			}
			
			//проверка началната и крайна дата на обявата да не излизат от рамките на въведените в кампанията при децентрализирана кампания
			if(practice.getDateFrom()!=null && practice.getDateTo()!=null && camp.getDateFrom()!=null && camp.getDateTo()!=null && 
					(practice.getDateFrom().before(camp.getDateFrom()) || practice.getDateTo().after(camp.getDateTo()))	) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				JSFUtils.addMessage("practiceForm:begDateConf",
						FacesMessage.SEVERITY_ERROR,"Началната или крайната дата на обявата са извън въведения интервал на кампанията(" + sdf.format(camp.getDateFrom())+ "-"+ 
						sdf.format(camp.getDateTo())+")!");					
				flagSave=true;
			}			
		}
		
		
		if(practice.getPracticeDateFrom()==null){
			JSFUtils.addMessage("practiceForm:begDatePractice",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "practice.begDatePract")));	
	
			flagSave=true;
		}else if(practice.getId()==null && DateUtils.startDate(practice.getPracticeDateFrom()).before(DateUtils.startDate(getToday()))) {
			JSFUtils.addMessage("practiceForm:begDatePractice",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","practice.begDateBeforeToday"));	
			
			flagSave=true;
		}
		
		if(practice.getPracticeDateTo()==null){
			JSFUtils.addMessage("practiceForm:endDatePractice",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "practice.endDatePractice")));	
	
			flagSave=true;
		}

		if(practice.getPracticeDateFrom()!=null && practice.getPracticeDateTo()!=null && practice.getPracticeDateTo().before(practice.getPracticeDateFrom())) {
			JSFUtils.addMessage("practiceForm:begDatePractice",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "general.dateFromLessDateTo"));	
			
			flagSave=true;
		} 
		
		if(practice.getConfirmDateTo2()!=null && practice.getPracticeDateFrom()!=null && (practice.getPracticeDateFrom().before(practice.getConfirmDateTo2()) || practice.getConfirmDateTo2().equals(practice.getPracticeDateFrom()))) {
			JSFUtils.addMessage("practiceForm:begDatePractice",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages", "practice.begDatePracticeBeforeConfDate2"));	
			
			flagSave=true;
		}
		
		if(practice.getDescription()==null || "".equals(practice.getDescription())){
			JSFUtils.addMessage("practiceForm:description",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "practice.description")));		
			flagSave=true;
		}

		return flagSave;
	}


    private void generateTitle(){
    	practice.setPracticeTitle("");
    	if(practice.getEducationArea()!=null) {
    		try {
				practice.setPracticeTitle(getSystemData().decodeItem(Constants.CODE_SYSCLASS_SUBJECT, practice.getEducationArea(), getCurrentLang(), getToday(), userId));
				practice.setPracticeTitle(practice.getPracticeTitle() + ", ");
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при разкодиране на значение от класификация! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			}
    	}
 	
    	if(practice.getUnit()!=null) {
    		try {
				practice.setPracticeTitle(practice.getPracticeTitle() + getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, practice.getUnit(), getCurrentLang(), getToday(), userId));
				practice.setPracticeTitle(practice.getPracticeTitle() + ", ");
    		} catch (DbErrorException e) {
				LOGGER.error("Грешка при разкодиране на значение от класификация! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			}
    	}
    	  	
    	if(practice.getAdministration()!=null) {
    		try {
				practice.setPracticeTitle(practice.getPracticeTitle() + getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, practice.getAdministration(), getCurrentLang(), getToday(), userId));
				practice.setPracticeTitle(practice.getPracticeTitle() + ", ");
    		} catch (DbErrorException e) {
				LOGGER.error("Грешка при разкодиране на значение от класификация! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			}
    	}
    	 	
    	if(practice.getTown()!=null) {
    		try {
				practice.setPracticeTitle(practice.getPracticeTitle() + getSystemData().decodeItem(Constants.CODE_SYSCLASS_EKATTE, practice.getTown(), getCurrentLang(), getToday(), userId));
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при разкодиране на значение от класификация! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			}
    	}
    }
    
    public void actionSave() {
		if(checkData()){
			return;
		}
		try {

		    practice.setUnit(unit);
		    practice.setAdministration(adm);
		    practice.setAdministration(adm);
		   
		    generateTitle();
		    
			JPA.getUtil().begin();
			practiceDao.save(practice);							
			JPA.getUtil().commit();					
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));	
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при запис на обява за стаж! ", e);
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
    	
		practice = new Practice();
		practice.setAdministration(userData.getCodeOrg());
		practice.setUnit(userData.getZveno());
		unit = practice.getUnit();
		adm = userData.getCodeOrg();
		
		try {
			this.admText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, adm, getCurrentLang(), new Date(), userId);
			campList = campDao.findActiveCampaigns(true);
			if(unit!=null) {
				unitText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, unit, getCurrentLang(), new Date(), this.userId);
			}
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при разкодиране на значение! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		}
		
	
	}

	
    public void actionDelete(){
		if(practice.getId()!=null){
			try {
				JPA.getUtil().begin();
							
				practiceDao.delete(practice);
				JPA.getUtil().commit();	
		
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));	
				
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при изтриване на обява за стаж! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			}finally {
				JPA.getUtil().closeConnection();
			}
			
			actionNew();
		}		
	}
    
    /** Метод за измъкване на системната класификация и зареждането на списъците с област, община и населени места
	 * 
	 * 
	 * @param ekateList
	 * @param code
	 */
	public void filEkateList(List<SystemClassif> ekateList, Long code) {
		
		ekateList.clear();
		
		if(code == null){
			return;
		}
		
		try {
			
			ekateList.addAll(getSystemData().getChildrenOnNextLevel(Constants.CODE_SYSCLASS_EKATTE, code.longValue(), new Date(), getCurrentLang(), userId));
		
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		
		} finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	
	/** Метод за зареждане на общините при избор на област за адм. звено
	 * 
	 * 
	 */
	public void actionChangeOblast(){
		
		if(practice.getRegion() != null){
			filEkateList(this.obshtiniList, practice.getRegion());
		
		} else {
			obshtiniList.clear();
			practice.setMunicipality(null);
		}
		
		nasMestoList.clear();
		practice.setTown(null);
	}
	
	/** Метод за зареждане на населените места при избор на община за адм. звено
	 *  
	 */
	public void actionChangeObshtina(){
		
		if(this.practice.getMunicipality() != null){
			filEkateList(this.nasMestoList, this.practice.getMunicipality());
		
		} else {
			this.nasMestoList.clear();
			this.practice.setTown(null);
		}		
	}
	
	/**Изчисляване на крайна дата на стаж спрямо началната дата и системната настройка  PJOBS.PERIOD.VALIDNOST.STAJ
	 *  
	 */
	public void actionSetDateTo() {
    	if(practice.getDateFrom()!=null) {
    		String period = getSystemData().getSettingsValue("PJOBS.PERIOD.VALIDNOST.STAJ");
    		if(period!=null && !"".equals(period)&& ValidationUtils.isNumber(period)) {
    			  Calendar c = Calendar.getInstance();
    		      c.setTime(practice.getDateFrom());
    		      c.add(Calendar.DATE, Integer.valueOf(period));
    		      practice.setDateTo(c.getTime());
    		}
    	}
    }
    
    public void actionSetDateToConfirm() {
    	if(practice.getConfirmDateFrom()!=null) {
    		String period = getSystemData().getSettingsValue("PJOBS.PERIOD.POTVARJDAVANE.STAJ");
    		if(period!=null && !"".equals(period)&& ValidationUtils.isNumber(period)) {
    			  Calendar c = Calendar.getInstance();
    		      c.setTime(practice.getConfirmDateFrom());
    		      c.add(Calendar.DATE, Integer.valueOf(period));
    		      practice.setConfirmDateTo(c.getTime());
    		}
    	}
    }
    
    public void actionSetDateToConfirm2() {
    	if(practice.getConfirmDateFrom2()!=null) {
    		String period = getSystemData().getSettingsValue("PJOBS.PERIOD.POTVARJDAVANE.STAJ");
    		if(period!=null && !"".equals(period)&& ValidationUtils.isNumber(period)) {
    			  Calendar c = Calendar.getInstance();
    		      c.setTime(practice.getConfirmDateFrom2());
    		      c.add(Calendar.DATE, Integer.valueOf(period));
    		      practice.setConfirmDateTo2(c.getTime());
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

	public Practice getPractice() {
		return practice;
	}
	public void setPractice(Practice practice) {
		this.practice = practice;
	}
	public List<SelectItem> getCampList() {
		return campList;
	}
	public void setCampList(List<SelectItem> campList) {
		this.campList = campList;
	}
	public String getUnitText() {
		return unitText;
	}
	public void setUnitText(String unitText) {
		this.unitText = unitText;
	}
	
	public Long getUnit() {
		return unit;
	}
	
	public void setUnit(Long unit) {
		this.unit = unit;
	}
	
	public List<SystemClassif> getOblastList() {
		if(this.oblastList == null){
			this.oblastList = new ArrayList<SystemClassif>();
			filEkateList(this.oblastList, 0L);
		}
		return oblastList;
	}
	
	public void setOblastList(List<SystemClassif> oblastList) {
		this.oblastList = oblastList;
	}
	
	public List<SystemClassif> getObshtiniList() {
		return obshtiniList;
	}
	
	public void setObshtiniList(List<SystemClassif> obshtiniList) {
		this.obshtiniList = obshtiniList;
	}
	
	public List<SystemClassif> getNasMestoList() {
		return nasMestoList;
	}
	
	public void setNasMestoList(List<SystemClassif> nasMestoList) {
		this.nasMestoList = nasMestoList;
	}

	public Campaign getCamp() {
		return camp;
	}

	public void setCamp(Campaign camp) {
		this.camp = camp;
	}

	public List<SystemClassif> getEducationAreaList() {
		return educationAreaList;
	}

	public void setEducationAreaList(List<SystemClassif> educationAreaList) {
		this.educationAreaList = educationAreaList;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public String getAdmText() {
		return admText;
	}

	public void setAdmText(String admText) {
		this.admText = admText;
	}

	public Long getAdm() {
		return adm;
	}

	public void setAdm(Long adm) {
		this.adm = adm;
		unit = null;
		unitText = "";
		
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
