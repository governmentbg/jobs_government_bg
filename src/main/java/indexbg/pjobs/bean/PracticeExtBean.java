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
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.AdmUser;
import indexbg.pjobs.db.Campaign;
import indexbg.pjobs.db.Practice;
import indexbg.pjobs.db.PracticeLice;
import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.db.dao.PracticeLiceDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;
import indexbg.pjobs.system.UserData;

/**
 * @author idineva
 *
 */
@Named
@ViewScoped
public class PracticeExtBean extends PJobsBean{
	
	/** Клас за разглеждане на обява за стаж и кандидатстване за обява
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1333186263865157874L;
	static final Logger LOGGER = LoggerFactory.getLogger(PracticeExtBean.class);
	private Practice practice;
	private Long userId;
	private PracticeDAO practiceDao;

	private UserData userData;
	// Звено
	private String unitText;
	private Long unit;
	
	//области на висше образование 
	private List<SystemClassif> educationAreaList = new ArrayList<SystemClassif>();
	//дали да се вижда бутона за кандидатстване - само ако е стажант/стипендиант
	private boolean visibleAplly = false;
	private AdmUser user;
	//областта на висше обрзование на потребителя/кандаитата
	private Long userEduArea;
	//дали кандидатът вече е кандидатсвтал за тази обява
	private boolean disableApply = false;
	private boolean centrCamp;
	
	private String campName = "";
	
	private String mess ="";
	
	@PostConstruct
	public void init() {
			
		try {
			setMess("");
			
			userData = getUserData();
			this.userId = userData.getUserId();
			if(userId!=null) {		
				user =   new AdmUserDAO(this.userId).findById(this.userId);
				
				if(user!=null) {
				
					if(user.getUserStudent()!=null) {//ако потребителят е логнат,взимаме областта му на висше образование
						userEduArea = user.getUserStudent().getEducationArea();
					}
				}
				if(userData.getApplyFor()!=null && userData.getApplyFor().equals(Constants.CODE_ZNACH_CANDIDATE_BURSARY)) { //проверяваме дали е стипендиант/стажант
				    visibleAplly = true;
				}
			}	
			
			loadCategories();// зареждане на областите на висше образование
		
			practiceDao = new PracticeDAO(userId);

			String idObj =JSFUtils.getRequestParameter("idObj");			
			
			if (idObj!=null) {
				if (ValidationUtils.isNumber(idObj)) {
					practice = practiceDao.findById(Long.valueOf(idObj));
					if(practice==null) {
						redirectExternal("pageNotFound.jsf");
					}else { 
						if(practice.getDateFrom()!=null && practice.getDateTo()!=null 
								&& DateUtils.startDate(practice.getDateFrom()).before(getToday()) && 
								DateUtils.endDate(practice.getDateTo()).after(getToday())) {
							
														
							unit = practice.getUnit();
							if(unit!=null) {
								unitText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, unit, getCurrentLang(), new Date(), Constants.PORTAL_USER);
							}
							
							Campaign camp =new CampaignDAO(userId).findById(practice.getIdCampaign());
							if(camp!=null && camp.getTypeCamp() == Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR) {
								centrCamp = true;
							}else {
								centrCamp = false;
							}
							
							if(camp!=null) {
								campName = camp.getNameCamp();
							}
							
							
							if(userId!=null && visibleAplly) {
								// проверка дали канидатът вече е кандидатствал за тази обява
								PracticeLiceDAO practLiceDAO  = new PracticeLiceDAO(userId);
								List<Object[]> practiceList = new ArrayList<Object[]>();			
								practiceList = practLiceDAO.findPracticesByUserAplly(userId, practice.getIdCampaign(),practice.getId(),false ,false);
								
								if(practiceList!=null && !practiceList.isEmpty()) {
									setMess(getMessageResourceString("beanMessages", "practice.appl"));
									disableApply = true;
								} else  if(!practice.getEducationArea().equals(userEduArea)) {
									//проверка дали областта на висше образование на обявата за стаж съвпада с областта на кандидата
									setMess(getMessageResourceString("beanMessages", "practice.notMatchEduArea"));
									disableApply = true;
								} else if(centrCamp) { // ако е децентрализирана,може да кандидатства неограничен брой пъти	
									String maxApply = getSystemData().getSettingsValue("PJOBS.MAX.PRACTICE.STUDENT");
									
									practiceList  = practLiceDAO.findPracticesByUserAplly(userId, practice.getIdCampaign(),null,false ,false); 
									if(maxApply != null && !"".equals(maxApply) && practiceList!=null && ValidationUtils.isNumber(maxApply) && practiceList.size()>=Long.valueOf(maxApply).intValue()) {						
										
										setMess(getMessageResourceString("beanMessages", "practice.moreThan",maxApply));
										disableApply = true;	
										
									} 
									
								}
								
							}
						}else {							
							redirectExternal("pageNotFound.jsf");
												
						}
					}				
				} else {
					redirectExternal("pageNotFound.jsf");				
				}
				
				
			} else {
				redirectExternal("pageNotFound.jsf");				
			}
		
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
			list.addAll(getSystemData().getChildrenOnNextLevel(sysClassifCode, parentCode.longValue(), new Date(), getCurrentLang(), Constants.PORTAL_USER));
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
	   
	/** Метод за кандидатсване за стаж
	 *  
	 */
	public void actionApplying() {
      
		if(userId!=null && practice!=null) {
			
			PracticeLiceDAO practLiceDAO  = new PracticeLiceDAO(userId);		
			try {
				List<Object[]> practiceList = new ArrayList<Object[]>();	
				
				// проверка дали канидатът вече е кандидатствал за тази обява
				practiceList = practLiceDAO.findPracticesByUserAplly(userId, practice.getIdCampaign(),practice.getId(),false ,false);
				if(practiceList!=null && !practiceList.isEmpty()) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practice.appl"));
					return;
				}
				// проверка дали не е изтекъл срока за кандидатстване по тази обява
				if(practice.getDateTo()==null  || DateUtils.endDate(practice.getDateTo()).before(getToday())) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practice.applySrok"));
					return;
				}
				
				String maxApply = getSystemData().getSettingsValue("PJOBS.MAX.PRACTICE.STUDENT");
			
				if(centrCamp) {// ако е децентрализирана,може да кандидатства неограничен брой пъти				
					 practiceList  = practLiceDAO.findPracticesByUserAplly(userId, practice.getIdCampaign(),null,false ,false); 
				}
				
				//проверка дали областта на висше образование на обявата за стаж съвпада с областта на кандидата
				if(!practice.getEducationArea().equals(userEduArea)) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practice.notMatchEduArea"));
					
				//един външен потребител има право да кандидатства само до n на брой обяви в рамките на една централизирана кампания
				} else if(maxApply != null && !"".equals(maxApply) && practiceList!=null && ValidationUtils.isNumber(maxApply) && practiceList.size()>=Long.valueOf(maxApply).intValue()) {						
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practice.moreThan",maxApply));								
				} else {
				
					PracticeLice apply = new PracticeLice();
					apply.setIdPractice(practice.getId());
					apply.setIdUser(userId);
					apply.setStatus(Constants.CODE_ZNACHENIE_STATUS_CAND_APPLY);
					apply.setStatusDate(getToday());
					
					JPA.getUtil().begin();				
					practLiceDAO.save(apply);	
					JPA.getUtil().flush();
					
					ArrayList<String> mail = new ArrayList<>();
					mail.add(userData.getEmailUser());
					
					new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_PRACTICE_APPLY, mail, practice.getAdministration(), new Date(), null, null, null, userId, null, userId, Constants.CODE_OBJECT_PRACTICE, 
							getSystemData(), null, null, null, practice.getPracticeTitle(), practice.getPracticeDateFrom(), practice.getPracticeDateTo());
					
				
					JPA.getUtil().commit();					
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "practice.apply"));
					disableApply =true;
				}
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при кандидатстване за обява за стаж! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			}finally {
				JPA.getUtil().closeConnection();
				
			}
		} else {
			LOGGER.error("Грешка при кандидатстване за обява за стаж! userId==null or practice==null ");
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
	
	public List<SystemClassif> getEducationAreaList() {
		return educationAreaList;
	}

	public void setEducationAreaList(List<SystemClassif> educationAreaList) {
		this.educationAreaList = educationAreaList;
	}

	public boolean isVisibleAplly() {
		return visibleAplly;
	}

	public void setVisibleAplly(boolean visibleAplly) {
		this.visibleAplly = visibleAplly;
	}

	public boolean isDisableApply() {
		return disableApply;
	}

	public void setDisableApply(boolean disableApply) {
		this.disableApply = disableApply;
	}

	public boolean isCentrCamp() {
		return centrCamp;
	}

	public void setCentrCamp(boolean centrCamp) {
		this.centrCamp = centrCamp;
	}

	public String getCampName() {
		return campName;
	}

	public void setCampName(String campName) {
		this.campName = campName;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

}
