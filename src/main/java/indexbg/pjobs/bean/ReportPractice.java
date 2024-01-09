package indexbg.pjobs.bean;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.dao.AdmUsersDAO;
import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.db.dao.PracticeLiceDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


/**
 * @author idineva
 *
 */
@Named
@ViewScoped
public class ReportPractice extends PJobsBean{	
	


	private static final long serialVersionUID = -1798288145672885942L;

	static final Logger LOGGER = LoggerFactory.getLogger(ReportPractice.class);
	
    private CampaignDAO campDao;
    private PracticeLiceDAO plDAO;
    private List<SelectItem> campList = new ArrayList<SelectItem>();
    private Long idCamp;
    private List<SelectItem> sprList = new ArrayList<SelectItem>();
    private Long typeSpr;
	private Long userId;
	private Long year;
	
	private List<Object[]> resultReport = new ArrayList<Object[]>();	
	
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
				campList = campDao.findActiveCampaigns(false);
				sprList.add(new SelectItem(Constants.CODE_ZNACHENIE_DA,getMessageResourceString("labels", "reportPractice.succesCandidates")));
				sprList.add(new SelectItem(Constants.CODE_ZNACHENIE_NE,getMessageResourceString("labels", "reportPractice.unsuccesCandidates")));
				sprList.add(new SelectItem(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED,getMessageResourceString("labels", "reportPractice.unmarked")));
				typeSpr = Constants.CODE_ZNACHENIE_DA;
				
				String idcampStr =JSFUtils.getRequestParameter("idcamp");			
				
				if (idcampStr!=null) {
					
					if (ValidationUtils.isNumber(idcampStr)) {
						idCamp = Long.valueOf(idcampStr) ;						
					}
				}else {
					idCamp = campDao.findLastCampaign();
				}
				
				String typeStr =JSFUtils.getRequestParameter("type");			
				
				if (typeStr!=null) {				
					if (ValidationUtils.isNumber(typeStr)) {
						typeSpr = Long.valueOf(typeStr);
						actionSearch();
					}
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
				resultReport = plDAO.findPracticeResult(idCamp, typeSpr,year);
														
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
 		resultReport = new ArrayList<Object[]>();
 			
	}
	
	public void actionSendMail() {
		try {
		if(typeSpr !=null && typeSpr.equals(3L) && resultReport!=null && !resultReport.isEmpty()) {
			Query qSiteComp = JPA.getUtil().getEntityManager().createNativeQuery("select option_value from system_options where option_label = 'JOBS.ADMIN.PRACTICES'");
			String admGroup = (String) qSiteComp.getSingleResult();
			List <Object[]> admList =  new AdmUsersDAO(Constants.PORTAL_USER).findAdmPractices(Long.valueOf(admGroup)); 
			
					
			//групиране на админите по администрации 
			Map <Long ,List<Object[]>> admAdmins = new HashMap<Long ,List<Object[]>>();
			for(Object[] admin : admList) {
				Long admStr = SearchUtils.asLong(admin[2]);
				
				if(admAdmins.containsKey(admStr)) {
					List<Object[]> oldList = admAdmins.get(admStr);
					oldList.add(admin);
					admAdmins.replace(admStr, oldList);
				} else {
					List<Object[]> newList = new ArrayList<Object[]>();
					newList.add(admin);
					admAdmins.put(admStr, newList);
				}
			}
			
			//генериране съдържанието на съобщението
			Map <Long ,String> mailBody = new HashMap<Long ,String>();
						
			for(Object[] pr :resultReport) {
				
				String pin = SearchUtils.asString(pr[3]);
				String imena = SearchUtils.asString(pr[4]);
				String numberPractice = SearchUtils.asString(pr[8]);
				Long adm = SearchUtils.asLong(pr[5]);
				StringBuilder prBody = new StringBuilder("");				
				prBody.append(" Обява номер : ");
				prBody.append(numberPractice !=null ? numberPractice : "");
				prBody.append(" ");
				prBody.append("; ПИН: ");
				prBody.append(pin !=null ? pin : "");
				prBody.append(" ");
				prBody.append("; Имена на стажанта: ");
				prBody.append(imena !=null ? imena: "");
				prBody.append("<br/><br/>");
				
				if(mailBody.containsKey(adm)) {
					String value = mailBody.get(adm);  
					mailBody.replace(adm, value+prBody.toString());
				} else {
					mailBody.put(adm, prBody.toString());
				}								
			}
			
			try {
				
				JPA.getUtil().begin();
				
				for(Map.Entry<Long, String> entry : mailBody.entrySet()) {
					
					String mBody = entry.getValue();
					
					admList = admAdmins.get(entry.getKey());
					if(admList!=null) {
						for(Object[]admin: admList) {
													
							Query queryMail = JPA.getUtil().getEntityManager().createNativeQuery(" insert into jobs_mail (id, id_user, administration, email, name_lice, subject, msg, date_mail, user_reg, code_object, status, status_date) "
									  + " values (nextval('seq_mail'), ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11)");
		  				
							queryMail.setParameter(1, admin[0]);
							queryMail.setParameter(2, admin[2]); //administration
							queryMail.setParameter(3, admin[1]); //email
							queryMail.setParameter(4, admin[4]); //nameLice
							queryMail.setParameter(5, "Неотбелязани стажанти като успешно завършили и незавършили ");
							queryMail.setParameter(6, "Следните кандидати не са били отбелязани като успешно/ неуспешно завършили стаж: <br/>" + mBody);
							queryMail.setParameter(7, new Date());
							queryMail.setParameter(8, -1L);
							queryMail.setParameter(9, -1L);
							queryMail.setParameter(10, Constants.CODE_ZNACHENIE_STATUS_MAIL_NOT_SEND);
							queryMail.setParameter(11, new Date());
		
							queryMail.executeUpdate();
							
						}
					}else {
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Няма намерени мейли на администратори към тази администрация!");
					}
				}
		        								
				if(admList!=null &&!admList.isEmpty()) {
					JPA.getUtil().commit();
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Успешно изпратен мейл");
				}else {
					JPA.getUtil().rollback();
				}
			} catch (Exception e) {
		
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			}
		}
		
		} catch (Exception e) {
	
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
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
	

	public List<Object[]> getResultReport() {
		return resultReport;
	}


	public void setResultReport(List<Object[]> resultReport) {
		this.resultReport = resultReport;
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
