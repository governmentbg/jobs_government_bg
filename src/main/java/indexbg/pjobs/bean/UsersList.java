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
import org.primefaces.event.ToggleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.model.SystemAttribute;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.AdmGroups;
import indexbg.pjobs.db.dao.AdmUsersDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;
import indexbg.pjobs.system.SystemData;
import indexbg.pjobs.system.UserData;

@Named
@ViewScoped
public class UsersList extends PJobsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7848879099732484185L;

	static final Logger LOGGER = LoggerFactory.getLogger(UsersList.class);

	private SystemData sd = null;
	private UserData ud;
	private Long lang = null;
	private Long idUser;
	private Date currentDate = new Date();

	private int param = 0;

	private Long codeClassifWorkAdm; // Код на класификация за организация, в която работи потребителят
	private Long codeClassifPosition;
	private Long codeClassifStatus;
	private Long codeClassifUserType;
	private Long codeClassifLang;

	private static Boolean otUserEkran = Boolean.FALSE;

	private AdmUsersDAO usersDAO = null;
	private List<AdmGroups> lstGroups = null;
	private Map<String, Boolean> sysNames = null;
	
	// ***********************
	private String nameWorkAdmStr = "workAdmCode"; // Дали ще се работи с класификация за работно място за потребител - име на параметер
	// ************************

	private String username;
	private boolean loginEQ;
	private String imena;
	private Long statP;
	private Long tipP;
	private Long grupa;
	private Long admZv;
	private String admZvText = null;
	private Long period;
	private Date dateOt;
	private Date dateDo;
	private Long pin;

	private LazyDataModelSQL2Array tablList = null;

	private List<SelectItem> statList = null;

	private List<SystemClassif> periodList;
	
	private Long pageHidden;

	@PostConstruct
	public void initData() {

		this.lang = getCurrentLang();
		
		if (this.lang == null) {
			this.lang = Long.valueOf(Constants.CODE_DEFAULT_LANG);
		}
		
		this.sd = getSystemData();
		
		if (this.sd == null) {
			this.sd = new SystemData();
		}
		
		try {
			
			this.ud = getUserData();
			
			if (this.ud != null) {
				this.idUser = this.ud.getUserId();
			}
			
			if (this.idUser == null) {
				this.idUser = Long.valueOf(-1);
			}
		
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			this.idUser = Long.valueOf(-1);
		}

		String par = JSFUtils.getRequestParameter("par");

		if (par != null && !par.trim().isEmpty()) {
			
			this.param = Integer.parseInt(par.trim());
			
			if (this.param != 1 && this.param != 2) {
				this.param = 1; // 1 - филтър за актуализация; 2 - филтър за View
			}
		
		} else {
			this.param = 1; // По подразбиране - филтър за актуализиция
		}

		this.usersDAO = new AdmUsersDAO(this.idUser, this.sd);

		// Зареждане на системните имена от модела - за имената, които са с true - те се обработват в системата

		try {
			
			this.sysNames = this.usersDAO.getSysnamesUser(this.sd.getModel(), null, this.lang);

			/**
			 * Help имена за полета на потребител: 
			 * Потр. име - username 
			 * Имена - names 
			 * Вид потребител - userType 
			 * Eлeктронна поща - email 
			 * Език за работа - lang 
			 * Телефон - phone
			 * Дата на регистрация - regDate 
			 * Статус - status 
			 * Причина за статуса - statusExplain
			 * Месторабота в организация - workOrg 
			 * Месторабота в адм. структура - workAdmCode
			 * Описание на местоработаvworkplace 
			 * Длъжност - position 
			 * Описание на длъжността - positionText 
			 * Настройка за email - nastrmail
			 */

		} catch (DbErrorException e) {
			
			this.sysNames = null;
			LOGGER.error("Грешка при зареждане на системни имена от модела! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			return;
		}

		// Получаване код на класификация за организация в която работи от модела

		if (sysNames != null && sysNames.containsKey(nameWorkAdmStr)) {
			
			try {
				
				this.codeClassifWorkAdm = null;
				SystemAttribute satt = this.sd.getModel().getAttribute("workAdmCode", "User", lang, null);
				
				if (satt != null && satt.getCodeClassif() != null)
					this.codeClassifWorkAdm = Long.valueOf(satt.getCodeClassif().longValue());

			} catch (DbErrorException e) {
				LOGGER.error( "Грешка при получаване на код за класификация за организация, в която работи потребителя от модела! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			}
		}

		// Получаване код на класификация за длъжност
		if (sysNames != null && sysNames.containsKey("position")) {
			
			try {
				
				this.setCodeClassifPosition(null);
				SystemAttribute satt = this.sd.getModel().getAttribute("position", "User", lang, null);
				
				if (satt != null && satt.getCodeClassif() != null)
					this.codeClassifPosition = Long.valueOf(satt.getCodeClassif().longValue());

			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на код за класификация за длъжност за потребителя от модела! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			}
		}

		// Получаване код на класификация за статус на потребител от модела
		if (sysNames != null && sysNames.containsKey("status")) {
			
			try {
				
				this.codeClassifStatus = null;
				SystemAttribute satt = this.sd.getModel().getAttribute("status", "User", lang, null);
				
				if (satt != null && satt.getCodeClassif() != null)
					this.codeClassifStatus = Long.valueOf(satt.getCodeClassif().longValue());

			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на код за класификация за статус на  потребителя от модела! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			}
		}

		// Получаване код на класификация за вид потребител от модела // Засега се взима с константа от SysConstants

		if (sysNames != null && sysNames.containsKey("userType")) {
			
			try {
				
				this.codeClassifUserType = null;
				SystemAttribute satt = this.sd.getModel().getAttribute("userType", "User", lang, null);
				
				if (satt != null && satt.getCodeClassif() != null)
					this.codeClassifUserType = Long.valueOf(satt.getCodeClassif().longValue());

			} catch (DbErrorException e) {
				LOGGER.error( "Грешка при получаване на код за класификация за организация, в която работи потребителя от модела! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			}
		}

		// Получаване код на класификация за език // Засега се взима с константа от SysConstants

		if (sysNames != null && sysNames.containsKey("lang")) {
			
			try {
			
				this.setCodeClassifLang(null);
				SystemAttribute satt = this.sd.getModel().getAttribute("lang", "User", lang, null);
				
				if (satt != null && satt.getCodeClassif() != null)
					this.codeClassifLang = Long.valueOf(satt.getCodeClassif().longValue());

			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на код за класификация за език за  потребителя от модела! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			}
		}

		//clearAll();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("usersListSMDAttr");	
		
		if(params != null){
			
			dateOt = (Date) params.get("dateOt");
			dateDo = (Date) params.get("dateDo");
			username = (String) params.get("username");
			loginEQ = (Boolean) params.get("loginEQ");
			imena = (String) params.get("imena");
			statP = (Long) params.get("statP");
			tipP = (Long) params.get("tipP");
			grupa = (Long) params.get("grupa");
			admZv = (Long) params.get("admZv");
			pin = (Long) params.get("pin");
			
			period =  (Long) getSessionScopeValue("period");
			admZvText = (String) getSessionScopeValue("admZvText");
			
			actionSearch();
		}
	}

	public void clearAll() {
		
		this.username = null;
		this.loginEQ = false;
		this.imena = null;
		this.statP = null;
		this.tipP = null;
		this.grupa = null;
		this.admZv = null;
		this.admZvText = null;
		this.period = null;
		this.dateOt = null;
		this.dateDo = null;
		this.pin = null;

		setOtUserEkran(Boolean.FALSE);
		
		this.tablList = null;
		
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("usersListPage");		
		session.removeAttribute("usersListSMDAttr");		
		session.removeAttribute("period");
		session.removeAttribute("admZvText"); 
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formUsersList:tbl");		
		d.setFirst(0); 
	}

	public boolean getViewPage() {
		
		if (otUserEkran.booleanValue() == true) { // Дали е връщане от екран за въвеждане на потребител
			// int pageNSave = -1;
			// pageNSave = this.pageN;
			actionSearch();
			// if (pageNSave > 1) this.pageN = pageNSave; // На стр. преди преминаването към
			// екрана за въвеждане

			otUserEkran = Boolean.FALSE;
		}

		return true;
	}

	public void onToggle(ToggleEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " си сменя статуса ", "Статус: '" + event.getVisibility().name() + "'");
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * Метод за смяна на датите при избор на период за търсене.
	 * 
	 * 
	 */
	public void changePeriod() {

		if (this.period != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.period);
			this.setDateOt(di[0]);
			this.setDateDo(di[1]);

		} else {
			this.setDateOt(null);
			this.setDateDo(null);
		}

		return;
	}

	public boolean checkDati(int i) {
		
		boolean ret = true;
		
		if (this.dateOt != null && this.dateDo != null) {
			
			if (this.dateOt.compareTo(this.dateDo) > 0) {
				
				if (i <= 0 || i > 2)
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Дата От е по-голяма от Дата До!");
				
				else {

					if (i == 1)
						JSFUtils.addMessage("formUsersList:dateOt'", FacesMessage.SEVERITY_ERROR, "Дата От е по-голяма от Дата До!");
					else
						JSFUtils.addMessage("formUsersList:dateDo'", FacesMessage.SEVERITY_ERROR, "Дата От е по-голяма от Дата До!");
				}
				
				ret = false;
			
			} else
				
				this.period = null;
		}

		return ret;
	}

	public void actionSearch() {
		
		if (!checkDati(0))
			
			return;

		// Допълване с ниските звена от класификация за работно място (за всеки случай - може би не е необходимо, когато не е административната структура)
		List<Long> admStrList = null;
		
		if (sysNames.containsKey(nameWorkAdmStr) && admZv != null) {
			admStrList = new ArrayList<Long>();
			admStrList.add(admZv);

			try {
				
//				admStrList = this.usersDAO.getPodNivaFromClassifForIzbor (admStrList,  getClassifAdmStr(), this.sd,  currentDate, this.lang, this.idUser) ;
				admStrList = this.usersDAO.getPodNivaFromClassifForIzbor(admStrList, this.codeClassifWorkAdm, this.sd, currentDate, this.lang, this.idUser);
			
			} catch (DbErrorException e) {
				e.printStackTrace();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при допълване с под-нива от класификация за работно място ! " + "-" + e.getMessage() + e.getCause().getMessage());
				return;
			}

		}

		List<Long> typePotrList = null;
		
		if (tipP != null) {
			
			typePotrList = new ArrayList<Long>();
			typePotrList.add(tipP);

			try {
				
				typePotrList = this.usersDAO.getPodNivaFromClassifForIzbor(typePotrList, getClassifTip(), this.sd, currentDate, this.lang, this.idUser);
			
			} catch (DbErrorException e) {
				e.printStackTrace();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при допълване с под-нива от класификация за тип потребител ! " + "-" + e.getMessage() + e.getCause().getMessage());
				return;
			}

		}

		List<Long> grList = null;
		
		if (this.grupa != null) {
			grList = new ArrayList<Long>();
			grList.add(this.grupa);
		}

		this.tablList = null;
		
		SelectMetadata smd = null;

		smd = this.usersDAO.filterForUsers(this.dateOt, this.dateDo, this.username, this.loginEQ, this.imena, typePotrList, this.statP, grList, admStrList, this.lang, this.pin);

		try {
			
			this.tablList = this.usersDAO.newLazyDataModel(smd, "loginn");
			
			Map<String, Object> params = new HashMap<String, Object>();	
			
			if (dateOt != null) {
				params.put("dateOt", dateOt);	
			}
			
			if (dateDo != null) {
				params.put("dateDo", dateDo);	
			}
			
			if (username != null) {
				params.put("username", username);	
			}
			
			params.put("loginEQ", loginEQ);	
			
			
			if (imena != null) {
				params.put("imena", imena);	
			}
			
			if (statP != null) {
				params.put("statP", statP);	
			}
			
			if (tipP != null) {
				params.put("tipP", tipP);
			}
			
			if (grupa != null) {
				params.put("grupa", grupa);
			}
			
			if (admZv != null) {
				params.put("admZv", admZv);
			}
			
			if (pin != null) {
				params.put("pin", pin);	
			}			
			
			addSessionScopeAttribute("usersListSMDAttr", params);
			
			addSessionScopeAttribute("period", period); 
			addSessionScopeAttribute("admZvText", admZvText); 
		
		
		} catch (DbErrorException e) {
			e.printStackTrace();
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на записани потребители! " + "-" + e.getMessage() + e.getCause().getMessage());
			return;
		
		} finally {
			JPA.getUtil().closeConnection();
		}

		return;
	}

	public Long getClassifStat() {
		// return Long.valueOf(Constants.CODE_CLASSIF_STATUS_POTREB);
		return this.codeClassifStatus;
	}

	public Long getClassifDlajn() {
		// return Long.valueOf(Constants. CODE_CLASSIF_DLAJNOSTI);
		return this.codeClassifPosition;
	}

	public Long getClassifLang() {
		return Long.valueOf(Constants.CODE_CLASSIF_LANG);
		// return this.codeClassifLang;
	}

	public Long getClassifTip() {
		return Long.valueOf(Constants.CODE_CLASSIF_TIP_POTREB);
		// return this.codeClassifUserType;
	}

	public Long getTipVanshen() {
		return Long.valueOf(Constants.CODE_ZNACHENIE_TIP_POTR_VANSHEN);
	}

	public Long getClassifAdmStr() {
		return Long.valueOf(Constants.CODE_CLASSIF_ADMIN_STR);
	}

	public SystemData getSd() {
		return sd;
	}

	public void setSd(SystemData sd) {
		this.sd = sd;
	}

	public Long getLang() {
		return lang;
	}

	public void setLang(Long lang) {
		this.lang = lang;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public UserData getUd() {
		return ud;
	}

	public void setUd(UserData ud) {
		this.ud = ud;
	}

	public List<SelectItem> getStatList() {
		return statList;
	}

	public void setStatList(List<SelectItem> statList) {
		this.statList = statList;
	}

	public int getParam() {
		return param;
	}

	public void setParam(int param) {
		this.param = param;
	}

	public boolean isLoginEQ() {
		return loginEQ;
	}

	public void setLoginEQ(boolean loginEQ) {
		this.loginEQ = loginEQ;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public Long getStatP() {
		return statP;
	}

	public void setStatP(Long statP) {
		this.statP = statP;
	}

	public Long getAdmZv() {
		return admZv;
	}

	public void setAdmZv(Long admZv) {
		this.admZv = admZv;
	}

	public Date getDateOt() {
		return dateOt;
	}

	public void setDateOt(Date dateOt) {
		this.dateOt = dateOt;
	}

	public Date getDateDo() {
		return dateDo;
	}

	public void setDateDo(Date dateDo) {
		this.dateDo = dateDo;
	}

	public Long getPin() {
		return pin;
	}

	public void setPin(Long pin) {
		this.pin = pin;
	}

	public LazyDataModelSQL2Array getTablList() {
		return tablList;
	}

	public void setTablList(LazyDataModelSQL2Array tablList) {
		this.tablList = tablList;
	}

	public List<AdmGroups> getLstGroups() throws DbErrorException {
		
		if (lstGroups == null || lstGroups.isEmpty()) {
			// Прочитане данни за активните групи потребители
			
			try {
				
				lstGroups = this.usersDAO.getAllActGroups();
			
			} catch (DbErrorException e) {
				throw new DbErrorException(e.getMessage());
			}
		}
		return lstGroups;
	}

	public void setLstGroups(List<AdmGroups> lstGroups) {
		this.lstGroups = lstGroups;
	}

	public String getImena() {
		return imena;
	}

	public void setImena(String imena) {
		this.imena = imena;
	}

	public Long getTipP() {
		return tipP;
	}

	public void setTipP(Long tipP) {
		this.tipP = tipP;
	}

	public Long getGrupa() {
		return grupa;
	}

	public void setGrupa(Long grupa) {
		this.grupa = grupa;
	}

	public Map<String, Boolean> getSysNames() throws DbErrorException {
		return sysNames;
	}

	public void setSysNames(Map<String, Boolean> sysNames) {
		this.sysNames = sysNames;
	}

	public String getNameWorkAdmStr() {
		return nameWorkAdmStr;
	}

	public void setNameWorkAdmStr(String nameWorkAdmStr) {
		this.nameWorkAdmStr = nameWorkAdmStr;
	}

	public String getAdmZvText() {
		return admZvText;
	}

	public void setAdmZvText(String admZvText) {
		this.admZvText = admZvText;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public Boolean getOtUserEkran() {
		return otUserEkran;
	}

	public void setOtUserEkran(Boolean otUserEkran) {
		UsersList.otUserEkran = otUserEkran;
	}

	public static void setOtUserEkanStatic(Boolean otUserEkran) {
		UsersList.otUserEkran = otUserEkran;
	}

	public Long getCodeClassifWorkAdm() {
		return codeClassifWorkAdm;
	}

	public void setCodeClassifWorkAdm(Long codeClassifWorkAdm) {
		this.codeClassifWorkAdm = codeClassifWorkAdm;
	}

	public Long getCodeClassifPosition() {
		return codeClassifPosition;
	}

	public void setCodeClassifPosition(Long codeClassifPosition) {
		this.codeClassifPosition = codeClassifPosition;
	}

	public Long getCodeClassifStatus() {
		return codeClassifStatus;
	}

	public void setCodeClassifStatus(Long codeClassifStatus) {
		this.codeClassifStatus = codeClassifStatus;
	}

	public Long getCodeClassifUserType() {
		return codeClassifUserType;
	}

	public void setCodeClassifUserType(Long codeClassifUserType) {
		this.codeClassifUserType = codeClassifUserType;
	}

	public Long getCodeClassifLang() {
		return codeClassifLang;
	}

	public void setCodeClassifLang(Long codeClassifLang) {
		this.codeClassifLang = codeClassifLang;
	}

	public List<SystemClassif> getPeriodList() {

		if (periodList == null) {

			try {

				periodList = getSystemData().getSysClassification(Constants.CODE_CLASSIF_PERIOD_NOFUTURE, new Date(),
						getCurrentLang(), idUser);
				SysClassifUtils.doSortClassifPrev(periodList);

			} catch (DbErrorException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg"), e.getMessage());

			} catch (UnexpectedResultException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.unexpectedResult"), e.getMessage());

			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "general.exception"));

			} finally {
				JPA.getUtil().closeConnection();
			}
		}

		return periodList;
	}

	public void setPeriodList(List<SystemClassif> periodList) {
		this.periodList = periodList;
	}

	public Long getPageHidden() {
		
		if(pageHidden == null) { 
		
			pageHidden = 1L;
			
			if(getSessionScopeValue("usersListPage") != null) {
				
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formUsersList:tbl");
				
				if(d != null) { 					
					int page = (int) getSessionScopeValue("usersListPage");
					d.setFirst(page); 
				}
			}
		}
	
		return pageHidden;
	}

	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("usersListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formUsersList:tbl");
		
		if(d != null) { 
			
			addSessionScopeAttribute("usersListPage", d.getFirst());		
		}		
	}

}
