package indexbg.pjobs.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.dao.BursaryDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;



/**
 * @author idineva
 *
 */
@Named ("bursaryProgramsList")
@ViewScoped
public class BursaryProgramsList extends PJobsBean{	
	
	/**
	 * Клас за търсене на стипендиантски програми.Изкарва списък на СП по зададени критерии.
	 */
	private static final long serialVersionUID = 8534288990429849550L;

	static final Logger LOGGER = LoggerFactory.getLogger(BursaryProgramsList.class);
	
	
    private BursaryDAO bDao;

	private LazyDataModelSQL2Array bursaryList = null;	
	private Long userId;
	private String spec;
	private Long admStr;
	private String admText="";
	private boolean admin= false; //дали е администратор от МС 
	private Long pageHidden;
	
	private Long period;
	private Date dateFrom;
	private Date dateTo;
	private List<SystemClassif> periodList;

	public BursaryProgramsList() {
		
	}
	
	/**
	 * Инициализира променливите в класа
	 */
	@PostConstruct
	public void initData(){
		
			try {				
				this.userId = getUserData().getUserId();				
				bDao =  new BursaryDAO(userId);
				if(getUserData().getTypeUser().longValue()==Constants.CODE_ZNACHENIE_TIP_POTR_VATR) {
					admin= true;
				}else {
					admStr = getUserData().getCodeOrg();
				}
				
				if(periodList==null) {
						
						periodList = getSystemData().getSysClassification(Constants.CODE_CLASSIF_PERIOD_NOFUTURE, new Date(), getCurrentLang(), userId);
						SysClassifUtils.doSortClassifPrev(periodList);
					
				}
				
				@SuppressWarnings("unchecked")
				Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("bursaryListSMDAttr");	
				
				if(params != null){
					
					admStr = (Long) params.get("admStr");		
					spec = (String) params.get("spec");
					admText = (String) getSessionScopeValue("admText");
					dateFrom = (Date) params.get("dateFrom");
					dateTo = (Date) params.get("dateTo");
					period =  (Long) getSessionScopeValue("period");
					
				}
				actionSearch();
			} catch (DbErrorException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
			} catch (UnexpectedResultException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.unexpectedResult"), e.getMessage());
			}catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			} finally {
				JPA.getUtil().closeConnection();
			}	
	}
	
	
	/**
	 * Метод за търсене на СП
	 */
	public void actionSearch(){
		try {		  
				SelectMetadata smd = bDao.findPrograms(spec, admStr,null,false,dateFrom,dateTo);
				String defaultSortColumn = "A5 desc";
				bursaryList = new LazyDataModelSQL2Array(smd, defaultSortColumn);  
				
				Map<String, Object> params = new HashMap<String, Object>();			
				
				if (admStr != null) {
					params.put("admStr", admStr);
				}
				
				if (spec != null) {
					params.put("spec", spec);
				}
				
				if(dateFrom!=null) {
					params.put("dateFrom", dateFrom);
				}
				
				if(dateTo!=null) {
					params.put("dateTo", dateTo);
				}
				
				if(period!=null) {
					params.put("period", period);
				}
				
				addSessionScopeAttribute("bursaryListSMDAttr", params);
				addSessionScopeAttribute("admText", admText);
						
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на стипендиантски програми! ", e);
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
		spec="";
		if(admin) {
			admStr=null;
			admText="";			
		}
		bursaryList = null;
		dateFrom = null;
		dateTo = null;
		period = null;
		
		//махаме запазените параметри от сесията
				FacesContext context = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
				session.removeAttribute("bursaryListPage");		
				session.removeAttribute("bursaryListSMDAttr");				
				session.removeAttribute("admText"); 
				
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("exhibitionForm:tbl");		
				d.setFirst(0); 
				
	}
	
	/** Изтрива избран от списъка с резултати за стипендиантски програми
	 * @param id
	 */
	public void actionDelete(Long id){
		
		if(id!=null){
			try {
				JPA.getUtil().begin();
				bDao.deleteById(id);
				JPA.getUtil().commit();						
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));	
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при изтриване на стипендиантска програма! ", e);
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
		
		session.removeAttribute("bursaryListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("exhibitionForm:tbl");
		
		if(d != null) { 
			
			addSessionScopeAttribute("bursaryListPage", d.getFirst());		
		}		
	}
	
	/** Метод за смяна на датите при избор на период за търсене.
	 * 
	 * 
	 */
	public void changePeriod () {
		
    	if (this.period != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.period);
			this.setDateFrom(di[0]);
			this.setDateTo(di[1]);
		
    	} else {
			this.setDateFrom(null);
			this.setDateTo(null);
		}
		
    	return ;
    }
	
	

	public LazyDataModelSQL2Array getBursaryList() {
		return bursaryList;
	}

	public void setBursaryList(LazyDataModelSQL2Array bursaryList) {
		this.bursaryList = bursaryList;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public Long getAdmStr() {
		return admStr;
	}

	public void setAdmStr(Long admStr) {
		this.admStr = admStr;
	}

	public Date getToday(){
		return new Date();
	}

	public String getAdmText() {
		return admText;
	}

	public void setAdmText(String admText) {
		this.admText = admText;
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
			
			if(getSessionScopeValue("bursaryListPage") != null) {
				
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("exhibitionForm:tbl");
				
				if(d != null) { 					
					int page = (int) getSessionScopeValue("bursaryListPage");
					d.setFirst(page); 
				}
			}
		}
		return pageHidden;
	}

	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}

	public Long getPeriod() {

		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
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

	public List<SystemClassif> getPeriodList() {
		return periodList;
	}

	public void setPeriodList(List<SystemClassif> periodList) {
		this.periodList = periodList;
	}

	
}
