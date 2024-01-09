package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.primefaces.component.datatable.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


/**
 * @author idineva
 *
 */
@Named
@ViewScoped
public class PracticeList extends PJobsBean{	
	
	/**
	 * Клас за търсене на обяви за стаж в ДА.Изкарва списък на обяви по зададени критерии.
	 */

	private static final long serialVersionUID = -6545356134092012668L;

	static final Logger LOGGER = LoggerFactory.getLogger(PracticeList.class);
		
	private PracticeDAO practiceDao;
    private CampaignDAO campDao;

	private LazyDataModelSQL2Array practiceList = null;	
	private Long userId;

	private boolean admin= false; //дали е администратор от МС 
	private Long pageHidden;
	
	private List<SelectItem> campList;
	
	private Long porNom;
	private Long idCamp;
	private Long oblast;
	private Long obshtina;
	private Long nasMiasto;
	private Long area;
	private String title;	
	private Long admStruct;
	private String admText;
	// Звено
	private String unitText;
	private Long unit;
	
	private Date dateFrom;
	private Date dateTo;

	// ЕКАТТЕ
	private List<SystemClassif> oblastList;
	private List<SystemClassif> obshtiniList = new ArrayList<SystemClassif>();
	private List<SystemClassif> nasMestoList = new ArrayList<SystemClassif>();

	//области на висше образование 
	private List<SystemClassif> educationAreaList = new ArrayList<SystemClassif>();
	
	public PracticeList() {
		
	}
	
	/**
	 * Инициализира променливите в класа
	 */
	@PostConstruct
	public void initData(){
		
			try {	
				loadCategories();
				this.userId = getUserData().getUserId();				
				campDao = new CampaignDAO(userId);
				practiceDao = new PracticeDAO(userId);
				campList = campDao.findActiveCampaigns(false);
				//idCamp = campDao.findLastCampaign();
				admStruct =  getUserData().getCodeOrg();
				if(admStruct!=null) {
					this.admText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, this.admStruct, getCurrentLang(), new Date(), userId);
				}
				unit = getUserData().getZveno();
				if(unit!=null) {
					unitText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, unit, getCurrentLang(), new Date(), this.userId);
				}
				if(getUserData().getTypeUser().longValue()==Constants.CODE_ZNACHENIE_TIP_POTR_VATR) {
					admin= true;										
				}
						
				@SuppressWarnings("unchecked")
				Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("practiceListSMDAttr");	
				
				if(params != null){
										
					title = (String) params.get("title");				
					porNom = (Long) params.get("porNom");
					idCamp = (Long) params.get("idCamp");
					admStruct = (Long) params.get("admStruct");
					unit = (Long) params.get("unit");
					oblast = (Long) params.get("oblast");
					obshtina = (Long) params.get("obshtina");
					nasMiasto = (Long) params.get("nasMiasto");
					area = (Long) params.get("area");
					admText = (String) getSessionScopeValue("admText");
					filEkateList(obshtiniList, oblast);
					filEkateList(nasMestoList, obshtina);				
				}
				actionSearch();
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
	
	/**
	 * Метод за търсене на обяви за стаж
	 */
	public void actionSearch(){
		try {		
			
				SelectMetadata smd = practiceDao.findPractices(porNom, idCamp, admStruct, oblast, obshtina, nasMiasto, area, title,unit,false,dateFrom,dateTo );
				String defaultSortColumn = "A0 desc";
				practiceList = new LazyDataModelSQL2Array(smd, defaultSortColumn);  
				
				Map<String, Object> params = new HashMap<String, Object>();			
				
				if(title != null && !"".equals(title)) {
					params.put("title", title);
				}
												
				if(porNom!=null) {
					params.put("porNom", porNom);
				}
				
				if(idCamp!=null) {
					params.put("idCamp", idCamp);
				}
				
				if(admStruct!=null) {
					params.put("admStruct", admStruct);
				}
				
				if(unit!=null) {
					params.put("unit", unit);
				}
				
				if(oblast!=null) {
					params.put("oblast", oblast);
				}
				
				if(obshtina!=null) {
					params.put("obshtina", obshtina);
				}
				
				if(nasMiasto!=null) {
					params.put("nasMiasto", nasMiasto);
				}
					
				if(area!=null) {
					params.put("area", area);
				}
				addSessionScopeAttribute("admText", admText);
				addSessionScopeAttribute("practiceListSMDAttr", params);
						
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
	
	/**
	 * Зачистване на зададените от потребителя стойности за търсене
	 */
	public void actionClear(){
       
		dateFrom = null;
		dateTo = null;
		porNom = null;
		idCamp = null;
		title = null;
		oblast = null;
		obshtina = null;
		nasMiasto = null;
		area = null;
		porNom = null;
		practiceList = null;
		try {
			//idCamp = campDao.findLastCampaign();
			admStruct =  getUserData().getCodeOrg();
			if(admStruct!=null) {
				this.admText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, this.admStruct, getCurrentLang(), new Date(), userId);
			}
			unit = getUserData().getZveno();
			if(unit!=null) {
				unitText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, unit, getCurrentLang(), new Date(), this.userId);
			}
		} catch (ObjectNotFoundException e) {
			LOGGER.error("! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на звено на потребител! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		}
		
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("practiceListPage");		
		session.removeAttribute("practiceListSMDAttr");				
				
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("practiceForm:tbl");		
		d.setFirst(0); 
				
	}
	
	/** Изтрива избран от списъка с резултати за обяви за стаж
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
				LOGGER.error("Грешка при изтриване на обяви за стаж! ", e);
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
		
		session.removeAttribute("practiceListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("practiceForm:tbl");
		
		if(d != null) { 
			
			addSessionScopeAttribute("practiceListPage", d.getFirst());		
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
		
		if(oblast != null){
			filEkateList(this.obshtiniList, oblast);
		
		} else {
			obshtiniList.clear();
			obshtina = null;
		}
		
		nasMestoList.clear();
		nasMiasto = null;
	}
	
	/** Метод за зареждане на населените места при избор на община за адм. звено
	 * 
	 * 
	 */
	public void actionChangeObshtina(){
		
		if(obshtina != null){
			filEkateList(this.nasMestoList, obshtina);
		
		} else {
			this.nasMestoList.clear();
			nasMiasto = null;
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

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Long getPageHidden() {
		if(pageHidden == null) { 
			
			pageHidden = 1L;
			
			if(getSessionScopeValue("practiceListPage") != null) {
				
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("campForm:tbl");
				
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

	public String getNamePract() {
		return title;
	}

	public void setNamePract(String namePract) {
		this.title = namePract;
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

	public Long getPorNom() {
		return porNom;
	}

	public void setPorNom(Long porNom) {
		this.porNom = porNom;
	}

	public Long getIdCamp() {
		return idCamp;
	}

	public void setIdCamp(Long idCamp) {
		this.idCamp = idCamp;
	}

	public List<SelectItem> getCampList() {
		return campList;
	}

	public void setCampList(List<SelectItem> campList) {
		this.campList = campList;
	}

	public Long getAdmStruct() {
		return admStruct;
	}

	public void setAdmStruct(Long admStruct) {
		this.admStruct = admStruct;
		unit = null;
		unitText = "";		
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

	public Long getArea() {
		return area;
	}

	public void setArea(Long area) {
		this.area = area;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public List<SystemClassif> getEducationAreaList() {
		return educationAreaList;
	}

	public void setEducationAreaList(List<SystemClassif> educationAreaList) {
		this.educationAreaList = educationAreaList;
	}

	public String getAdmText() {
		return admText;
	}

	public void setAdmText(String admText) {
		this.admText = admText;
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
}
