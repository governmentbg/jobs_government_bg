package indexbg.pjobs.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.primefaces.component.datatable.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


/**
 * @author idineva
 *
 */
@Named
@ViewScoped
public class CampaignList extends PJobsBean{	
	
	/**
	 * Клас за търсене на кампании за стаж в ДА.Изкарва списък на кампании по зададени критерии.
	 */
	private static final long serialVersionUID = 8534288990429849550L;

	static final Logger LOGGER = LoggerFactory.getLogger(CampaignList.class);
	
	
    private CampaignDAO campDao;

	private LazyDataModelSQL2Array campList = null;	
	private Long userId;

	private boolean admin= false; //дали е администратор от МС 
	private Long pageHidden;
	
	private String nameCamp;
	private Long [] status = {Constants.CODE_ZNACHENIE_CAMPAIGN_STATUS_ACTIVE,Constants.CODE_ZNACHENIE_CAMPAIGN_STATUS_V_PROCES};
	private Date dateStatus;
//	private Long period;
	private Date dateFrom;
	private Date dateTo;
	private Date dateFirsRank;
	private Date confDateFrom;
	private Date confDateTo;
//	private List<SystemClassif> periodList;

	public CampaignList() {
		
	}
	
	/**
	 * Инициализира променливите в класа
	 */
	@PostConstruct
	public void initData(){
		
			try {				
				this.userId = getUserData().getUserId();				
				campDao = new CampaignDAO(userId);
				if(getUserData().getTypeUser().longValue()==Constants.CODE_ZNACHENIE_TIP_POTR_VATR) {
					admin= true;
				}
				
//				if(periodList==null) {						
//					periodList = getSystemData().getSysClassification(Constants.CODE_CLASSIF_PERIOD_NOFUTURE, new Date(), getCurrentLang(), userId);
//					SysClassifUtils.doSortClassifPrev(periodList);					
//				}
//				
				@SuppressWarnings("unchecked")
				Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("campaignListSMDAttr");	
				
				if(params != null){
					
					dateFrom = (Date) params.get("dateFrom");
					dateTo = (Date) params.get("dateTo");
					nameCamp = (String) params.get("nameCamp");
					status = (Long[]) params.get("status");
					dateStatus = (Date) params.get("dateStatus");
					dateFirsRank = (Date) params.get("dateFirsRank");
					confDateFrom = (Date) params.get("confDateFrom");
					confDateTo = (Date) params.get("confDateTo");
					
				}
				actionSearch();
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			} finally {
				JPA.getUtil().closeConnection();
			}	
	}
	
	
	/**
	 * Метод за търсене на кампания
	 */
	public void actionSearch(){
		try {		  
				SelectMetadata smd = campDao.findCampaigns(nameCamp, status, dateStatus, dateFrom, dateTo, dateFirsRank, confDateFrom, confDateTo);
				String defaultSortColumn = "A4 desc";
				campList = new LazyDataModelSQL2Array(smd, defaultSortColumn);  
				
				Map<String, Object> params = new HashMap<String, Object>();			
						
				if(dateFrom!=null) {
					params.put("dateFrom", dateFrom);
				}
				
				if(dateTo!=null) {
					params.put("dateTo", dateTo);
				}
				
				if(nameCamp != null && !"".equals(nameCamp)) {
					params.put("nameCamp", nameCamp);
				}
				
				if(status != null) {
					params.put("status", status);
				}
				
				if(dateStatus != null) {
					params.put("dateStatus", dateStatus);
				}
				
				if(dateFirsRank != null) {
					params.put("dateFirsRank", dateFirsRank);
				}

				if(confDateFrom != null) {
					params.put("confDateFrom", confDateFrom);
				}
				
				if(confDateTo != null) {
					params.put("confDateTo", confDateTo);
				}
								
				addSessionScopeAttribute("campaignListSMDAttr", params);
						
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на кампания! ", e);
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
 
		dateFrom = null;
		dateTo = null;
		nameCamp = null;
		status = null;
		dateStatus = null;
        dateFrom =null;
		dateTo = null;
		dateFirsRank = null;
		confDateFrom = null;
		confDateTo = null;
		campList = null;	
		
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("campaignListPage");		
		session.removeAttribute("campaignListSMDAttr");				
				
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("campForm:tbl");		
		d.setFirst(0); 
				
	}
	
	/** Изтрива избран от списъка с резултати за стипендиантски програми
	 * @param id
	 */
	public void actionDelete(Long id){
		
		if(id!=null){
			try {
				JPA.getUtil().begin();
				campDao.deleteById(id);
				JPA.getUtil().commit();						
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));	
				actionSearch();
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
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("campaignListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("campForm:tbl");
		
		if(d != null) { 
			
			addSessionScopeAttribute("campaignListPage", d.getFirst());		
		}		
	}
	
	/** Метод за смяна на датите при избор на период за търсене.
	 * 
	 * 
	 */
//	public void changePeriod () {
//		
//    	if (this.period != null) {
//			Date[] di;
//			di = DateUtils.calculatePeriod(this.period);
//			this.setDateFrom(di[0]);
//			this.setDateTo(di[1]);
//		
//    	} else {
//			this.setDateFrom(null);
//			this.setDateTo(null);
//		}
//		
//    	return ;
//    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getToday(){
		return new Date();
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Long getPageHidden() {
		if(pageHidden == null) { 
			
			pageHidden = 1L;
			
			if(getSessionScopeValue("campaignListPage") != null) {
				
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("campForm:tbl");
				
				if(d != null) { 					
					int page = (int) getSessionScopeValue("campaignListPage");
					d.setFirst(page); 
				}
			}
		}
		return pageHidden;
	}

	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public LazyDataModelSQL2Array getCampList() {
		return campList;
	}

	public void setCampList(LazyDataModelSQL2Array campList) {
		this.campList = campList;
	}

	public String getNameCamp() {
		return nameCamp;
	}

	public void setNameCamp(String nameCamp) {
		this.nameCamp = nameCamp;
	}

	public Long[] getStatus() {
		return status;
	}

	public void setStatus(Long[] status) {
		this.status = status;
	}

	public Date getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}

	public Date getDateFirsRank() {
		return dateFirsRank;
	}

	public void setDateFirsRank(Date dateFirsRank) {
		this.dateFirsRank = dateFirsRank;
	}

	public Date getConfDateFrom() {
		return confDateFrom;
	}

	public void setConfDateFrom(Date confDateFrom) {
		this.confDateFrom = confDateFrom;
	}

	public Date getConfDateTo() {
		return confDateTo;
	}

	public void setConfDateTo(Date confDateTo) {
		this.confDateTo = confDateTo;
	}

	
}
