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

import org.primefaces.PrimeFaces;
import org.primefaces.component.datagrid.DataGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.db.dao.AdvertisementDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class AdverExtList extends PJobsBean{	
	
	/** Клас за търсене и разглеждане на обяви за мобилност. Изкарва списък с обяви за мобилност по зададени критерии.
	 * 
	 * 
	 */
	private static final long serialVersionUID =  4457922805295373547L;

	static final Logger LOGGER = LoggerFactory.getLogger(AdverExtList.class);	
	
	private AdvertisementDAO adverDAO;
	private LazyDataModelSQL2Array adversList = null;
	private String adminStrucText;
	private Long adminStruc;	
	private String zvenoText; 
	private Long zveno;	
	private Long profField;
	private Long dlajnost;
	private String dlajnostText;	
	
	private int gridcolumn = 2;
	
	private Long pageHidden;
	
	/** Иницира първоначалните стойности на списъка с обяви
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
			
			this.adverDAO = new AdvertisementDAO(Constants.PORTAL_USER);	
			
			@SuppressWarnings("unchecked")
			Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("adverExtSMDAttr");	
			
			if(params != null){
				
				adminStruc = (Long) params.get("adminStruc");
				zveno = (Long) params.get("zveno");
				dlajnost = (Long) params.get("dlajnost");
				profField = (Long) params.get("profField");				
				
				adminStrucText = (String) getSessionScopeValue("adminStrucText");
				zvenoText = (String) getSessionScopeValue("zvenoText");
				dlajnostText = (String) getSessionScopeValue("dlajnostText");
			}
			
			if ((zvenoText != null && !zvenoText.isEmpty()) || (dlajnostText != null && !dlajnostText.isEmpty())) {
				PrimeFaces.current().executeScript("toggle();");	 			
			}
			
			actionSearch();
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		
		} finally {
			JPA.getUtil().closeConnection();
		}		
		
	}
	
	
	/** Метод за търсене на на обяви за мобилност
	 * 
	 * 
	 */
	public void actionSearch(){
		
		try {
			
			SelectMetadata smd = this.adverDAO.findAdvertisement(null, null, this.adminStruc, this.zveno, null, null, null, this.dlajnost, this.profField, null, null, null, true);	
			String defaultSortColumn = "A0";			
			this.adversList = new LazyDataModelSQL2Array(smd, defaultSortColumn);			
			
			Map<String, Object> params = new HashMap<String, Object>();
			
		
			if (adminStruc != null) {
				params.put("adminStruc", adminStruc);
			}
			
			if (zveno != null) {
				params.put("zveno", zveno);	
			}
			
			if (profField != null) {
				params.put("profField", profField);
			}
			
			if (dlajnost != null) {
				params.put("dlajnost", dlajnost);	
			}					
			
			addSessionScopeAttribute("adverExtSMDAttr", params);
			
			addSessionScopeAttribute("adminStrucText", adminStrucText); 
			addSessionScopeAttribute("zvenoText", zvenoText); 
			addSessionScopeAttribute("dlajnostText", dlajnostText);
		
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на обяви за мобилност! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	
	/** Метод за зачистване на зададените стойности за търсене
	 * 
	 * 
	 */
	public void actionClear(){
		
		this.adversList = null;	
		this.adminStruc = null;
		this.adminStrucText = "";
		this.zveno = null;
		this.zvenoText = "";		
		this.dlajnost = null;
		this.dlajnostText = "";
		this.profField = null;
		
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("adverExtPage");		
		session.removeAttribute("adverExtSMDAttr");		
		session.removeAttribute("adminStrucText"); 
		session.removeAttribute("zvenoText"); 
		session.removeAttribute("dlajnostText");
		
		DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("formAdverExtList:tableGrid");		
		d.setFirst(0); 
		
		actionSearch();
	}	
	
	/** Метод за зареждане на списъка с обяви в 2 колони
	 * 
	 * 
	 */
	public void actionGridTable() {
		gridcolumn = 2;
	}
	
	/** Метод за зареждане на списъка с обяви в 1 колона
	 * 
	 * 
	 */
	public void actionListTable() {
		gridcolumn = 1;
	}
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("adverExtPage");
		
		DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("formAdverExtList:tableGrid");
		
		if(d != null) { 
			
			addSessionScopeAttribute("adverExtPage", d.getFirst());		
		}
	}
	
	public String getDirekciaForZveno(Integer administration, Integer zveno) {
		String direkcia = "";
		
		if (administration!=null && zveno!=null) {
			try {

				List<SystemClassif> classifList = new AdministrationDAO(Constants.PORTAL_USER, getSystemData())
						.builAdmdStrOfSubject(administration.longValue(), new Date());

				List<SystemClassif> parents = SysClassifUtils.getParents(classifList, zveno);

				for (int i = parents.size() - 1; i >= 0; i--) {
					SystemClassif par = parents.get(i);
					if (par.getCode() == zveno) {
						break;
					} else {
						if (direkcia.isEmpty()) {
							direkcia = par.getTekst() + "</br>";
						} else {
							direkcia += " - " + par.getTekst();
						}
					}
				}

			} catch (DbErrorException e) {
				LOGGER.error("Грешка при изграждане на дирекции и отдели!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} 
		}
		return direkcia;
	}
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */

	public LazyDataModelSQL2Array getAdversList() {
		return adversList;
	}

	public void setAdversList(LazyDataModelSQL2Array adversList) {
		this.adversList = adversList;
	}

	public String getAdminStrucText() {
		return adminStrucText;
	}

	public void setAdminStrucText(String adminStrucText) {
		this.adminStrucText = adminStrucText;
	}

	public Long getAdminStruc() {
		return adminStruc;
	}

	public void setAdminStruc(Long adminStruc) {
		
		if (this.adminStruc != null) {
			this.zveno = null;
			this.zvenoText = "";
		}
		
		this.adminStruc = adminStruc;
	}

	public String getZvenoText() {
		return zvenoText;
	}

	public void setZvenoText(String zvenoText) {
		this.zvenoText = zvenoText;
	}

	public Long getZveno() {
		return zveno;
	}

	public void setZveno(Long zveno) {
		this.zveno = zveno;
	}

	public Long getDlajnost() {
		return dlajnost;
	}

	public void setDlajnost(Long dlajnost) {
		this.dlajnost = dlajnost;
	}

	public String getDlajnostText() {
		return dlajnostText;
	}

	public void setDlajnostText(String dlajnostText) {
		this.dlajnostText = dlajnostText;
	}

	public Long getProfField() {
		return profField;
	}

	public void setProfField(Long profField) {
		this.profField = profField;
	}
	
	public int getGridcolumn() {
		return gridcolumn;
	}

	public void setGridcolumn(int gridcolumn) {
		this.gridcolumn = gridcolumn;
	}
	
	public Long getPageHidden() {
		
		if(pageHidden == null) { 
			
			pageHidden = 1L;
			
			if(getSessionScopeValue("adverExtPage") != null) { 				
				
				DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("formAdverExtList:tableGrid");
				
				if(d != null) { 					
					int page = (int) getSessionScopeValue("adverExtPage");
					d.setFirst(page); 
				}
			}
		}
		
		return pageHidden;
	}

	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}
	
	// ************************************************* END GET & SET ***************************************************** //	
	
}
