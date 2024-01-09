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

import org.primefaces.component.datagrid.DataGrid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.BursaryDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("bursaryExtList")
@ViewScoped
public class BursaryExtList extends PJobsBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -48372699613195266L;
	static final Logger LOGGER = LoggerFactory.getLogger(BursaryExtList.class);
	
	 private BursaryDAO bDao;
	private LazyDataModelSQL2Array bursaryList = null;	
	
	private Long admStr;
	private String admText="";
	private String spec;
	private int gridColumn = 2;
	private Long pageHidden;
	

	public BursaryExtList() {
		
	}
	
	@PostConstruct
	public void initData(){
		
			try {			
			
				bDao =  new BursaryDAO(Constants.PORTAL_USER);
				
				@SuppressWarnings("unchecked")
				Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("bursaryExtListSMDAttr");	
				
				if(params != null){
					
					admStr = (Long) params.get("admStr");		
					spec = (String) params.get("spec");
					admText = (String) getSessionScopeValue("admText");
					
				}
				actionSearch();
				
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			}
	}
	

	public void actionSearch(){
		try {		  
				SelectMetadata smd = bDao.findPrograms(spec, admStr,Constants.CODE_ZNACHENIE_STATUS_BURSARY_ODOBRENA,true,null,null);
				String defaultSortColumn = "A5 desc";
				bursaryList = new LazyDataModelSQL2Array(smd, defaultSortColumn);  
										
				Map<String, Object> params = new HashMap<String, Object>();			
				
				if (admStr != null) {
					params.put("admStr", admStr);
				}
				
				if (spec != null) {
					params.put("spec", spec);
				}
				addSessionScopeAttribute("bursaryExtListSMDAttr", params);
				addSessionScopeAttribute("admText", admText);
				
							
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на задължени субекти! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	public void actionClear() {
		bursaryList = null;	
		admStr = null;
		admText = "";
		spec = "";
		
		//махаме запазените параметри от сесията
				FacesContext context = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
				session.removeAttribute("bursaryExtListSMDAttr");		
				session.removeAttribute("admText");		
				
				DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("bursaryListForm:tableGrid");		
				if(d != null) { 
					d.setFirst(0); 
				}
		
	}
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("bursaryListPage");
		
		DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("bursaryListForm:tableGrid");
		
		if(d != null) { 
			
			addSessionScopeAttribute("bursaryListPage", d.getFirst());		
		}		
	}

	
	/** Метод за зареждане на списъка с обяви в 2 колони
	 * 
	 * 
	 */
	public void actionGridTable() {
		this.gridColumn = 2;
	}
	
	/** Метод за зареждане на списъка с обяви в 1 колона
	 * 
	 * 
	 */
	public void actionListTable() {
		this.gridColumn = 1;
	}

	public LazyDataModelSQL2Array getBursaryList() {
		return bursaryList;
	}


	public void setBursaryList(LazyDataModelSQL2Array bursaryList) {
		this.bursaryList = bursaryList;
	}

	public Long getAdmStr() {
		return admStr;
	}


	public void setAdmStr(Long admStr) {
		this.admStr = admStr;
	}


	public String getAdmText() {
		return admText;
	}


	public void setAdmText(String admText) {
		this.admText = admText;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}
	
	public Date getToday(){
		return new Date();
	}

	public int getGridColumn() {
		return gridColumn;
	}

	public void setGridColumn(int gridColumn) {
		this.gridColumn = gridColumn;
	}

	public Long getPageHidden() {
		
if(pageHidden == null) { 
			
			pageHidden = 1L;
			
			if(getSessionScopeValue("bursaryListPage") != null) {
				
				DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("bursaryListForm:tableGrid");
				
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
	
}
