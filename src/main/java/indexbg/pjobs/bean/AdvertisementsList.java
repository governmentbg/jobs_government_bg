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

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.ToggleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
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
public class AdvertisementsList extends PJobsBean{	
	
	/** Клас за търсене и редактиране на обяви за мобилност. Изкарва списък с обяви за мобилност по зададени различни критерии.
	 * 
	 * 
	 */
	private static final long serialVersionUID =  4457922805295373547L;

	static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementsList.class);	
	
	private AdvertisementDAO adverDAO;
	private LazyDataModelSQL2Array adversList = null;
	private Long userId;
	private Long period;
	private Date dateFrom;
	private Date dateTo;
	private Long adminStruc;
	private String admText;
	private String zvenoText; 
	private Long zveno; 
	private Long oblast;
	private Long obshtina;
	private Long nasMiasto;	
	private List<SystemClassif> oblastList;
	private List<SystemClassif> obshtiniList = new ArrayList<SystemClassif>();
	private List<SystemClassif> nasMestoList = new ArrayList<SystemClassif>();
	private Long profField;
	private Long dlajnost;
	private String dlajnostText;
	private Long dlajNivo;
	private Long rang;
	private Long obrazovanie;
	
	private Long pageHidden;
	
	private  List<SystemClassif> periodList;
	
	private boolean admin = false; //дали е администратор от МС 
	
	/** Иницира първоначалните стойности на списъка с обяви
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.adverDAO = new AdvertisementDAO(this.userId);
			this.adminStruc = getUserData().getCodeOrg();
			
			if(getUserData().getTypeUser().longValue() == Constants.CODE_ZNACHENIE_TIP_POTR_VATR) {
				admin = true;
				this.admText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, this.adminStruc, getCurrentLang(), new Date(), userId);
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("adverListSMDAttr");	
			
			if(params != null){
				
				adminStruc = (Long) params.get("adminStruc");
				zveno = (Long) params.get("zveno");
				dateFrom = (Date) params.get("dateFrom");
				dateTo = (Date) params.get("dateTo");
				oblast = (Long) params.get("oblast");
				obshtina = (Long) params.get("obshtina");
				nasMiasto = (Long) params.get("nasMiasto");
				dlajnost = (Long) params.get("dlajnost");
				profField = (Long) params.get("profField");
				
				filEkateList(obshtiniList, oblast);
				filEkateList(nasMestoList, obshtina);
				
				period =  (Long) getSessionScopeValue("period");
				admText = (String) getSessionScopeValue("admText");
				zvenoText = (String) getSessionScopeValue("zvenoText");
				dlajnostText = (String) getSessionScopeValue("dlajnostText");
			}
			
			actionSearch();
		
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
	
	
	/** Метод за търсене на на обяви за мобилност
	 * 
	 * 
	 */
	public void actionSearch(){
		
		try {
		
			SelectMetadata smd = this.adverDAO.findAdvertisement(this.dateFrom, this.dateTo, this.adminStruc, this.zveno, this.oblast, this.obshtina, this.nasMiasto, this.dlajnost, this.profField, this.dlajNivo, this.rang, this.obrazovanie, false);		
			String defaultSortColumn = "A0";	
			this.adversList = new LazyDataModelSQL2Array(smd, defaultSortColumn);
			
			Map<String, Object> params = new HashMap<String, Object>();			
			
			if (adminStruc != null) {
				params.put("adminStruc", adminStruc);
			}
			
			if (zveno != null) {
				params.put("zveno", zveno);	
			}
			
			if (dateFrom != null) {
				params.put("dateFrom", dateFrom);	
			}
			
			if (dateTo != null) {
				params.put("dateTo", dateTo);	
			}
			
			if (oblast != null) {
				params.put("oblast", oblast);	
			}
			
			if (obshtina != null) {
				params.put("obshtina", obshtina);	
			}
			
			if (nasMiasto != null) {
				params.put("nasMiasto", nasMiasto);	
			}
			
			if (dlajnost != null) {
				params.put("dlajnost", dlajnost);	
			}
			
			if (profField != null) {
				params.put("profField", profField);
			}					
			
			addSessionScopeAttribute("adverListSMDAttr", params);
			
			addSessionScopeAttribute("period", period); 
			addSessionScopeAttribute("admText", admText); 
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
		this.period = null;
		this.dateFrom = null;
		this.dateTo = null;
		this.zveno = null;
		this.zvenoText = "";
		this.oblast = null;
		this.obshtina = null;
		this.nasMiasto = null;
		this.dlajnost = null;
		this.dlajnostText = "";
		this.dlajNivo = null;
		this.profField = null;
		this.rang = null;
		this.obrazovanie = null;
		
		if (admin) {
			this.adminStruc = null;
			this.admText = "";
		}
		
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("adverListPage");		
		session.removeAttribute("adverListSMDAttr");		
		session.removeAttribute("period");
		session.removeAttribute("admText"); 
		session.removeAttribute("zvenoText"); 
		session.removeAttribute("dlajnostText");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formAdverList:tbl");		
		d.setFirst(0); 
		
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
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("adverListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formAdverList:tbl");
		
		if(d != null) { 
			
			addSessionScopeAttribute("adverListPage", d.getFirst());		
		}		
	}
	
	// ************************************************* EKATTE ************************************************************ //	

	/** Метод за зареждане на общините при избор на област
	 * 
	 * 
	 */
	public void actionChangeOblast(){
		
		if(this.oblast != null){
			filEkateList(this.obshtiniList, this.oblast);
		
		} else {
			this.obshtiniList.clear();
			this.setObshtina(null);
		}
		
		this.nasMestoList.clear();
		this.setNasMiasto(null); 
	}
	
	/** Метод за зареждане на населените места при избор на община
	 * 
	 * 
	 */
	public void actionChangeObshtina(){
		
		if(this.obshtina != null){
			filEkateList(this.nasMestoList, this.obshtina);
		
		} else {
			this.nasMestoList.clear();
			this.setNasMiasto(null); 
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
	
	public String getDirekciaForZveno(Integer administration, Integer zveno) {
		String direkcia = "";
		
		try {
			
			List<SystemClassif> classifList = new AdministrationDAO(this.userId, getSystemData()).builAdmdStrOfSubject(administration.longValue(), new Date());
			
			List<SystemClassif> parents = SysClassifUtils.getParents(classifList, zveno);

			for (int i = parents.size()-1; i >= 0; i--) {
				SystemClassif par = parents.get(i);
				if (par.getCode() == zveno) {					
					break;
				} else {
					if (direkcia.isEmpty()) {
						direkcia = par.getTekst() + "<br>";
					} else {
						direkcia += " - " + par.getTekst() + "<br>";
					}					
				}				
			}
			
		
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при изграждане на дирекции и отдели!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		}
		
		return direkcia;
	}
		
	// ************************************************* END EKATTE ******************************************************** //
	
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public Long getAdminStruc() {
		return adminStruc;
	}

	public void setAdminStruc(Long adminStruc) {
		this.adminStruc = adminStruc;
		
		this.zveno = null;
		this.zvenoText = "";
	}

	public String getAdmText() {
		return admText;
	}

	public void setAdmText(String admText) {
		this.admText = admText;
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

	public Long getNasMiasto() {
		return nasMiasto;
	}

	public void setNasMiasto(Long nasMiasto) {
		this.nasMiasto = nasMiasto;
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

	public Long getDlajNivo() {
		return dlajNivo;
	}

	public void setDlajNivo(Long dlajNivo) {
		this.dlajNivo = dlajNivo;
	}

	public Long getRang() {
		return rang;
	}

	public void setRang(Long rang) {
		this.rang = rang;
	}

	public Long getObrazovanie() {
		return obrazovanie;
	}

	public void setObrazovanie(Long obrazovanie) {
		this.obrazovanie = obrazovanie;
	}

	public Long getPageHidden() {
		
		if(pageHidden == null) { 
		
			pageHidden = 1L;
			
			if(getSessionScopeValue("adverListPage") != null) {
				
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formAdverList:tbl");
				
				if(d != null) { 					
					int page = (int) getSessionScopeValue("adverListPage");
					d.setFirst(page); 
				}
			}
		}
	
		return pageHidden;
	}

	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}
	

	public List<SystemClassif> getPeriodList() {
		if(periodList==null) {
			
			try {
				periodList = getSystemData().getSysClassification(Constants.CODE_CLASSIF_PERIOD_NOFUTURE, new Date(), getCurrentLang(), userId);
				SysClassifUtils.doSortClassifPrev(periodList);
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
		return periodList;
	}

	public void setPeriodList(List<SystemClassif> periodList) {
		this.periodList = periodList;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	// ************************************************* END GET & SET ***************************************************** //	
	private boolean showFocus;
	
	public void onToggle(ToggleEvent event) {
		showFocus = false;
		switch(event.getVisibility()) {
		 case VISIBLE:
			 showFocus = true;
	        break;
	      case HIDDEN:
	    	  showFocus = false;
	        break;
		
		}
    }

	public boolean isShowFocus() {
		return showFocus;
	}

	public void setShowFocus(boolean showFocus) {
		this.showFocus = showFocus;
	}
}
