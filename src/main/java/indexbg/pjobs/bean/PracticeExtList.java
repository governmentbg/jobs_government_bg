package indexbg.pjobs.bean;

import java.util.ArrayList;
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

import org.primefaces.component.datagrid.DataGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("practiceExtList")
@ViewScoped
public class PracticeExtList extends PJobsBean{

	
	private static final long serialVersionUID = 6782136983554137988L;
	static final Logger LOGGER = LoggerFactory.getLogger(PracticeExtList.class);	
	private PracticeDAO practiceDao;
	private LazyDataModelSQL2Array practiceList = null;	
	private Long admStr;
	private String admText="";
	private Long educationArea;
	private int gridColumn = 2;
	private Long pageHidden;
	private Long oblast;
	private Long obshtina;
	
	// ЕКАТТЕ
	private List<SystemClassif> oblastList;
	private List<SystemClassif> obshtiniList = new ArrayList<SystemClassif>();
	
	//области на висше образование 
	private List<SystemClassif> educationAreaList = new ArrayList<SystemClassif>();
	
	public PracticeExtList() {
		
	}
	
	@PostConstruct
	public void initData(){
		
			try {	
				loadCategories();
		
				practiceDao =  new PracticeDAO(Constants.PORTAL_USER);
				
				@SuppressWarnings("unchecked")
				Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("practiceExtListSMDAttr");	
				
				if(params != null){
					
					admStr = (Long) params.get("admStr");		
					educationArea = (Long) params.get("educationArea");
					admText = (String) getSessionScopeValue("admText");
					oblast = (Long) params.get("oblast");
					obshtina = (Long) params.get("obshtina");
				}
				actionSearch();
				
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
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
	
	public void actionSearch(){
		try {		  
				SelectMetadata smd = practiceDao.findPractices(null, null, admStr, oblast, obshtina, null, educationArea, null,null,true,null,null);
				String defaultSortColumn = "A0 desc";
				setPracticeList(new LazyDataModelSQL2Array(smd, defaultSortColumn));  
										
				Map<String, Object> params = new HashMap<String, Object>();			
				
				if (admStr != null) {
					params.put("admStr", admStr);
				}
				
				if (educationArea != null) {
					params.put("educationArea", educationArea);
				}
				
				if (oblast != null) {
					params.put("oblast", oblast);
				}
				
				if (obshtina != null) {
					params.put("obshtina", obshtina);
				}
				addSessionScopeAttribute("practiceExtListSMDAttr", params);
				addSessionScopeAttribute("admText", admText);
				
							
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на обяви за стаж! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	public void actionClear() {
		setPracticeList(null);	
		admStr = null;
		admText = "";
		educationArea = null;
		oblast = null;
		obshtina = null;
		obshtiniList = new ArrayList<>();
		
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("practiceExtListSMDAttr");		
		session.removeAttribute("admText");		
				
		DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("practiceForm:tableGrid");		
		if(d != null) { 
			d.setFirst(0); 
		}
		
	}
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("practiceListPage");
		
		DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("practiceListForm:tableGrid");
		
		if(d != null) { 
			
			addSessionScopeAttribute("practiceListPage", d.getFirst());		
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

	/** Метод за зареждане на общините при избор на област за адм. звено
	 * 
	 * 
	 */
	public void actionChangeOblast(){
		
		if(oblast != null){
			filEkateList(this.obshtiniList, oblast);
		
		} else {
			obshtiniList.clear();
			obshtina = null;
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
			
			ekateList.addAll(getSystemData().getChildrenOnNextLevel(Constants.CODE_SYSCLASS_EKATTE, code.longValue(), new Date(), getCurrentLang(), Constants.PORTAL_USER));
		
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
			
			if(getSessionScopeValue("practiceListPage") != null) {
				
				DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("practiceListForm:tableGrid");
				
				if(d != null) { 					
					int page = (int) getSessionScopeValue("practiceListPage");
					d.setFirst(page); 
				}
			}
		}
		return pageHidden;
	}

	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}

	public LazyDataModelSQL2Array getPracticeList() {
		return practiceList;
	}

	public void setPracticeList(LazyDataModelSQL2Array practiceList) {
		this.practiceList = practiceList;
	}

	public Long getEducationArea() {
		return educationArea;
	}

	public void setEducationArea(Long educationArea) {
		this.educationArea = educationArea;
	}

	public Long getOblast() {
		return oblast;
	}

	public void setOblast(Long oblast) {
		this.oblast = oblast;
	}

	public Long getObshtina() {
		return obshtina;
	}

	public void setObshtina(Long obshtina) {
		this.obshtina = obshtina;
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

	public List<SystemClassif> getEducationAreaList() {
		return educationAreaList;
	}

	public void setEducationAreaList(List<SystemClassif> educationAreaList) {
		this.educationAreaList = educationAreaList;
	}
	
}
