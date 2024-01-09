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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class CandidatesList extends PJobsBean{	
	
	/** Клас за търсене на подходящи кандидати. Изкарва списък с подходящи кандидати по зададени различни критерии.
	 * 
	 * 
	 */
	private static final long serialVersionUID =  8257776011980999954L;

	static final Logger LOGGER = LoggerFactory.getLogger(CandidatesList.class);	
	
	private AdmUserDAO usersDAO;
	private LazyDataModelSQL2Array candidatsList = null;
	private Long userId;
	private Long profField;
	private Long spec;
	private Long educDegree;
	private Long expYears;
	private String specIzisk;
	private Long pin;
	private Long candidateFor;
	private Long educationArea;
	private String name;
	private String surname;
	private String family;
	
	private Long pageHidden;
	
	private List<SystemClassif> profFieldList = new ArrayList<SystemClassif>();
	private List<SystemClassif> specList = new ArrayList<SystemClassif>();
	private List<SystemClassif> educationAreaList = new ArrayList<SystemClassif>();
	
	/** Иницира първоначалните стойности на списъка с подходящи кандидати
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.usersDAO = new AdmUserDAO(userId);
			
			// зареждане на професионалните направления
			loadCategories();
			
			@SuppressWarnings("unchecked")
			Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("candidatesListSMDAttr");	
			
			if(params != null){
				
				profField = (Long) params.get("profField");
				spec = (Long) params.get("spec");				
				educDegree = (Long) params.get("educDegree");
				expYears = (Long) params.get("expYears");	
				
				specIzisk = (String) getSessionScopeValue("specIzisk");
				pin =   (Long) params.get("pin");
				candidateFor = (Long) params.get("candidateFor");
				educationArea = (Long) params.get("educationArea");
				
				actionSearch();
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
		
		List<SystemClassif> mainLevel = new ArrayList<SystemClassif>();
		fillSysClassifList(mainLevel, 0L, Constants.CODE_SYSCLASS_SUBJECT, false);
		
		for(SystemClassif s : mainLevel) {
			fillSysClassifList(this.profFieldList, s.getCode(), Constants.CODE_SYSCLASS_SUBJECT, false);
		}
		
		this.profFieldList.sort((a, b) -> a.getTekst().compareTo(b.getTekst()));
		
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
	
	public void actionChangedCategory() {
		fillSysClassifList(this.specList, this.profField, Constants.CODE_SYSCLASS_SUBJECT, true);
	}
	
	
	/** Метод за търсене на подходящи кандидати
	 * 
	 * 
	 */
	public void actionSearch(){
		
		try {
		
			SelectMetadata smd = this.usersDAO.findCandidates(pin ,this.profField, this.spec, this.educDegree, this.expYears, this.specIzisk, this.candidateFor, this.educationArea, this.name, this.surname, this.family);
			String defaultSortColumn = "A0";	
			this.candidatsList = new LazyDataModelSQL2Array(smd, defaultSortColumn);
			
			Map<String, Object> params = new HashMap<String, Object>();			
			
			if (profField != null) {
				params.put("profField", profField);
			}
			
			if (spec != null) {
				params.put("spec", spec);	
			}			
			
			if (educDegree != null) {
				params.put("educDegree", educDegree);	
			}
			
			if (expYears != null) {
				params.put("expYears", expYears);	
			}
			
			if (pin != null) {
				params.put("pin", pin);	
			}
			
			if (candidateFor != null) {
				params.put("candidateFor", candidateFor);	
			}
			
			if (educationArea != null) {
				params.put("educationArea", educationArea);
			}
			
			addSessionScopeAttribute("candidatesListSMDAttr", params);
			
			addSessionScopeAttribute("specIzisk", specIzisk);
			addSessionScopeAttribute("name", name);
			addSessionScopeAttribute("surname", surname);
			addSessionScopeAttribute("family", family);
		
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
		
		this.candidatsList = null;
		this.profField = null;
		this.spec = null;
		this.educDegree = null;
		this.expYears = null;
		this.specIzisk = "";
		this.pin = null;
		this.educationArea = null;
		this.name = "";
		this.surname = "";
		this.family = "";
		
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("candidatesListPage");		
		session.removeAttribute("candidatesListSMDAttr");
		session.removeAttribute("specText"); 
		session.removeAttribute("specIzisk");
		session.removeAttribute("name");
		session.removeAttribute("surname");
		session.removeAttribute("family");		
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formCandidatsList:tbl");		
		d.setFirst(0); 

	}
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("candidatesListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formCandidatsList:tbl");
		
		if(d != null) { 
			
			addSessionScopeAttribute("candidatesListPage", d.getFirst());		
		}		
	}
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */

	public LazyDataModelSQL2Array getCandidatsList() {
		return candidatsList;
	}

	public void setCandidatsList(LazyDataModelSQL2Array candidatsList) {
		this.candidatsList = candidatsList;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getProfField() {
		return profField;
	}

	public void setProfField(Long profField) {
		this.profField = profField;
	}

	public Long getSpec() {
		return spec;
	}

	public void setSpec(Long spec) {
		this.spec = spec;
	}

	public Long getEducDegree() {
		return educDegree;
	}

	public void setEducDegree(Long educDegree) {
		this.educDegree = educDegree;
	}

	public Long getExpYears() {
		return expYears;
	}

	public void setExpYears(Long expYears) {
		this.expYears = expYears;
	}

	public String getSpecIzisk() {
		return specIzisk;
	}

	public void setSpecIzisk(String specIzisk) {
		this.specIzisk = specIzisk;
	}
	
	public Long getPageHidden() {
		
		if(pageHidden == null) { 
			
			pageHidden = 1L;
			
			if(getSessionScopeValue("candidatesListPage") != null) { 				
				
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formCandidatsList:tbl");
				
				if(d != null) { 					
					int page = (int) getSessionScopeValue("candidatesListPage");
					d.setFirst(page); 
				}
			}
		}
		
		return pageHidden;
	}

	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}

	public Long getPin() {
		return pin;
	}


	public void setPin(Long pin) {
		this.pin = pin;
	}
	
	public List<SystemClassif> getProfFieldList() {
		return profFieldList;
	}

	public void setProfFieldList(List<SystemClassif> profFieldList) {
		this.profFieldList = profFieldList;
	}

	public List<SystemClassif> getSpecList() {
		return specList;
	}

	public void setSpecList(List<SystemClassif> specList) {
		this.specList = specList;
	}

	public List<SystemClassif> getEducationAreaList() {
		return educationAreaList;
	}

	public void setEducationAreaList(List<SystemClassif> educationAreaList) {
		this.educationAreaList = educationAreaList;
	}

	public Long getCandidateFor() {
		return candidateFor;
	}

	public void setCandidateFor(Long candidateFor) {
		this.candidateFor = candidateFor;
	}

	public Long getEducationArea() {
		return educationArea;
	}

	public void setEducationArea(Long educationArea) {
		this.educationArea = educationArea;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}
	
	// ************************************************* END GET & SET ***************************************************** //	
	
}
