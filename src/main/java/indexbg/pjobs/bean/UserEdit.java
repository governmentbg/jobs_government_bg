package indexbg.pjobs.bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.mail.Mailer3;
import com.indexbg.system.model.SystemAttribute;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.TreeUtils;
import com.indexbg.system.utils.UserUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.AdmGroups;
import indexbg.pjobs.db.AdmOrgs;
import indexbg.pjobs.db.AdmUserRoles;
import indexbg.pjobs.db.AdmUsers;
import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.dao.AdmOrgsDAO;
import indexbg.pjobs.db.dao.AdmUsersDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;
import indexbg.pjobs.system.SystemData;
import indexbg.pjobs.system.UserData;

@Named
@ViewScoped
public class UserEdit extends PJobsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5142506805786976784L;

	static final Logger LOGGER = LoggerFactory.getLogger(UserEdit.class);

	private SystemData sd = null;
	private UserData ud;
	private Long lang = null;
	private Long idUser;
	private Long indRowIzb = null;
	private Date currentDate = new Date();
	private Long userId = null; // id на потребител за актуализация / разглеждане
	private AdmUsers user = null;

	private int param = 0;
	private boolean isZapis = false;
	private String ocenkaPass = null;

	private static Boolean otOrgEkran = Boolean.FALSE; // Параметър при връщане от екрана за въвеждане на организации

	private Long codeClassifWorkAdm; // Код на класификация за организация, в която работи потребителят
	private Long codeClassifPosition;
	private Long codeClassifStatus;
	private Long codeClassifUserType;
	private Long codeClassifLang;

	private Long begStatus = null; // Статус потребител при влизане или начало на поредна актуализация
	private AdmUsersDAO usersDAO = null;
	private Map<String, Boolean> sysNames = null;
	private List<AdmGroups> lstGroups = null; // Активните групи потребители
	private List<SelectItem> lstGroupsIzb = null; // Активните групи потребители
	private ArrayList<String> selectedGrList = new ArrayList<String>(); // Маркирани групи за нов/избрания потребител
	private List<Files> filesList = new ArrayList<>(); // Прикачени файлове за обекта User
	private Files file = new Files();
	private List<Files> deleteFilesList = new ArrayList<>();
	// *************************************
	// private CustomTreeNode<SystemClassif> rootNodeMenu;
	private CheckboxTreeNode rootNodeMenu;
	private Map<Long, Boolean> treeMenuHash = new HashMap<Long, Boolean>(); // Кои кодове от менюто са избрани в
																			// codesMenuList за една класификация
	// ************************************
	private String passInp = null; // Входно поле за разкодирана парола
	private String passPovt = null; // Поле за повторна парола
	private boolean viewChangePass = false; // Дали да се изобразява промяна на парола
	private boolean viewPass = false; // Дали да се изобразява въведената парола
	private boolean viewPassNastr = false;
	private String userTypeText = null;
	private String userLangText = null;
	private String userStatusText = null;
	private String nastrMailForwText = null;
	private static Long idOrg = null;
	private static String nameOrg = null;
	private Long admZv = null;
	private String admZvText = null;
	
	private Long admZveno = null;
	private String admZvenoText = null;
	
	private Long posDlajn = null;
	private String posDlajnText = null;
	private Long statusInp = null; // Зададен отначало статус за userInp
	// private List <SelectItem> clDANE = null;

	// Настройка за поща
	private String mailServAddress = null;
	private String mailServAddress2 = null;
	private String mailUser = null;
	private String mailPass = null;
	private String mailEMail = null;
	private Long mailForw = null;

	private LazyDataModelSQL2Array orgList = null;
	private String eikInp = null;
	private String nOrgInp = null;

	private List<String[]> sysClassifList = new ArrayList<String[]>();
	private Long selClassif;

	private TreeNode rootNode;
	private TreeNode[] selectedNode;
	private List<AdmUserRoles> selectedUserRoles; // Динамично избани роли
	private List<Long> selectedRolesClassif;
	private Map<Long, Long> mapSelRoles = new HashMap<Long, Long>();

	private int navSize = 0;

	// Задължителност за някои полета

	private boolean zadStatExpl = false;
	private boolean zadWAdmCode = false;
	private boolean zadWOrg = false;
	private boolean zadWPlace = false;
	private boolean zadPos = false;
	private boolean zadPosText = false;
	private boolean zadEmail = false;
	private boolean zadPhone = false;
	private boolean zadLang = false;

	@PostConstruct
	public void initData() {

		this.lang = getCurrentLang();
		if (this.lang == null)
			this.lang = Long.valueOf(Constants.CODE_DEFAULT_LANG);
		this.sd = getSystemData();
		if (this.sd == null)
			this.sd = new SystemData();
		try {
			this.ud = getUserData();
			if (ud != null) {
				this.idUser = this.ud.getUserId();
			}
			if (this.idUser == null)
				this.idUser = Long.valueOf(-1);
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			this.idUser = Long.valueOf(-1);
		}

		this.setIndRowIzb(null); // Индекс на реда на избрания запис от таблицата в userList
		String par = JSFUtils.getRequestParameter("par");
		if (par == null || par.trim().isEmpty()) {
			this.param = 0; // Въвеждане на нов потребител

		} else {
			// userEdit се извиква от userList с параметър par
			this.param = Integer.parseInt(par.trim()); // param = 0 - въвеждане на нов потребител;. param = 1 - edit;
														// param = 2 - view
			if (this.param != 0 && this.param != 1 && this.param != 2)
				this.param = 0; // Въвеждане на нов потребител

			// *********************************************************
			UsersList.setOtUserEkanStatic(Boolean.TRUE); // Индикация, че е извикан екран UserEdit след предаване
															// управлението от UsersList
			// *********************************************************
			if (this.param == 1 || this.param == 2) {
				par = JSFUtils.getRequestParameter("idObj");
				if (par == null || par.trim().isEmpty())
					this.param = 0; // Нов потребител
				else {
					this.userId = Long.valueOf(par.trim()); // ID на потребител, който се обработва
					par = JSFUtils.getRequestParameter("indRowU"); // Дали е подаден индекс на реда от таблицата, който
																	// е избран
					if (par != null && !par.trim().isEmpty()) {
						this.setIndRowIzb(Long.valueOf(par.trim()));
					}
				}
			}
		}

		this.usersDAO = new AdmUsersDAO(this.idUser, this.sd);

		clearAll();

		String dostap = getSystemData().getSettingsValue("classificationsForAccessControl");

		this.sysClassifList.clear();

		String[] clasList = dostap.split(",");

		try {

			for (int i = 0; i < clasList.length; i++) {
				if (clasList[i] != null) {
					clasList[i] = clasList[i].trim();
					if (clasList[i].isEmpty())
						clasList[i] = null;
				}
				if (clasList[i] == null)
					continue;
				this.sysClassifList.add(new String[] { clasList[i],
						getSystemData().getNameClassification(Long.valueOf(clasList[i]), getCurrentLang()) });
			}

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при зареждане на системна класификация! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "general.exception") + "-"
							+ e.getLocalizedMessage());
		}

		// Зареждане на системните имена от модела - за имената, които са с true - те се обработват в системата
		try {
			this.sysNames = this.usersDAO.getSysnamesUser(this.sd.getModel(), null, this.lang);

			/**
			 * Help имена за полета на потребител: Потр. име username Имена names Вид
			 * потребител userType Eлeктронна поща email Език за работа lang Телефон phone
			 * Дата на регистрация regDate Статус status Причина за статуса statusExplain
			 * :Месторабота в организация workOrg Месторабота в адм. структура workAdmCode
			 * Описание на месторабота workplace Длъжност position Описание на длъжността
			 * positionText:: Настройка за email nastrmail
			 */
			if (this.sysNames == null || this.sysNames.size() == 0) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "users.noSysNames"));
				return;
			}

		} catch (DbErrorException e) {
			this.sysNames = null;
			LOGGER.error("Грешка при зареждане на системни имена от модела! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			return;
		}

		// За тестване
		// this.sysNames.put("workOrg", Boolean.TRUE );
		// this.sysNames.put("workAdmCode", Boolean.FALSE);
		// this.sysNames.put("statusExplain", Boolean.TRUE);
		// this.sysNames.put("position", Boolean.FALSE);
		// this.sysNames.put("positionText", Boolean.FALSE);
		// this.sysNames.put("workplace", Boolean.FALSE);
		// this.sysNames.put("email", Boolean.TRUE);
		// this.sysNames.put("phone", Boolean.TRUE);

		// Получаване код на класификация за организация в която работи от модела

		if (sysNames != null && sysNames.containsKey("workAdmCode")) {
			try {
				this.codeClassifWorkAdm = null;
				SystemAttribute satt = this.sd.getModel().getAttribute("workAdmCode", "User", lang, null);
				if (satt != null) {
					if (satt.getCodeClassif() != null)
						this.codeClassifWorkAdm = Long.valueOf(satt.getCodeClassif().longValue());
					if (satt.isRequired())
						this.zadWAdmCode = true;
				}

			} catch (DbErrorException e) {
				LOGGER.error(
						"Грешка при получаване на код за класификация за организация, в която работи потребителя от модела! ",
						e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		// Получаване код на класификация за длъжност

		if (sysNames != null && sysNames.containsKey("position")) {
			try {
				this.setCodeClassifPosition(null);
				SystemAttribute satt = this.sd.getModel().getAttribute("position", "User", lang, null);
				if (satt != null) {
					if (satt.getCodeClassif() != null)
						this.codeClassifPosition = Long.valueOf(satt.getCodeClassif().longValue());
					if (satt.isRequired())
						this.zadPos = true;
				}

			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на код за класификация за длъжност за потребителя от модела! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		// Получаване код на класификация за статус на потребител от модела

		if (sysNames != null && sysNames.containsKey("status")) { // Това поле винаги съществува и е задължително за
																	// въвеждане
			try {
				this.codeClassifStatus = null;
				SystemAttribute satt = this.sd.getModel().getAttribute("status", "User", lang, null);
				if (satt != null && satt.getCodeClassif() != null)
					this.codeClassifStatus = Long.valueOf(satt.getCodeClassif().longValue());

			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на код за класификация за статус на  потребителя от модела! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		// Получаване код на класификация за вид потребител от модела
		// Засега се взима с константа от SysConstants

		if (sysNames != null && sysNames.containsKey("userType")) { // Това поле винеги съществува и е задължително за
																	// въвеждане
			try {
				this.codeClassifUserType = null;
				SystemAttribute satt = this.sd.getModel().getAttribute("userType", "User", lang, null);
				if (satt != null && satt.getCodeClassif() != null)
					this.codeClassifUserType = Long.valueOf(satt.getCodeClassif().longValue());

			} catch (DbErrorException e) {
				LOGGER.error(
						"Грешка при получаване на код за класификация за организация, в която работи потребителя от модела! ",
						e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		// Получаване код на класификация за език
		// Засега се взима с константа от SysConstants
		if (sysNames != null && sysNames.containsKey("lang")) { // По подразбиране е текущия език (български)
			try {
				this.setCodeClassifLang(null);
				SystemAttribute satt = this.sd.getModel().getAttribute("lang", "User", lang, null);
				if (satt != null) {
					if (satt.getCodeClassif() != null)
						this.codeClassifLang = Long.valueOf(satt.getCodeClassif().longValue());
					if (satt.isRequired())
						this.zadLang = true;
				}

			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на код за класификация за език за  потребителя от модела! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		// Задаване задължителност за други полета

		if (sysNames != null && sysNames.containsKey("statusExplain")) {
			try {
				SystemAttribute satt = this.sd.getModel().getAttribute("statusExplain", "User", lang, null);
				if (satt != null) {
					if (satt.isRequired())
						this.zadStatExpl = true;
				}
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на признак за задължителност на поле 'statusExplain'! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}

		}

		if (sysNames != null && sysNames.containsKey("workOrg")) {
			try {
				SystemAttribute satt = this.sd.getModel().getAttribute("workOrg", "User", lang, null);
				if (satt != null) {
					if (satt.isRequired())
						this.zadWOrg = true;
				}
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на признак за задължителност на поле 'workOrg'! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		if (sysNames != null && sysNames.containsKey("workplace")) {
			try {
				SystemAttribute satt = this.sd.getModel().getAttribute("workplace", "User", lang, null);
				if (satt != null) {
					if (satt.isRequired())
						this.zadWPlace = true;
				}
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на признак за задължителност на поле 'workplace'! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		if (sysNames != null && sysNames.containsKey("positionText")) {
			try {
				SystemAttribute satt = this.sd.getModel().getAttribute("positionText", "User", lang, null);
				if (satt != null) {
					if (satt.isRequired())
						this.zadPosText = true;
				}
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на признак за задължителност на поле 'positionText'! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		if (sysNames != null && sysNames.containsKey("email")) {
			try {
				SystemAttribute satt = this.sd.getModel().getAttribute("email", "User", lang, null);
				if (satt != null) {
					if (satt.isRequired())
						this.zadEmail = true;
				}
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на признак за задължителност на поле 'email'! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

		if (sysNames != null && sysNames.containsKey("phone")) {
			try {
				SystemAttribute satt = this.sd.getModel().getAttribute("phone", "User", lang, null);
				if (satt != null) {
					if (satt.isRequired())
						this.zadPhone = true;
				}
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при получаване на признак за задължителност на поле 'phone'! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-"
								+ e.getLocalizedMessage());

			}
		}

	}

	public boolean getViewNastrMail() { // Изобразява се ако липсва името или за него е зададено true
//	    if (!this.sysNames.containsKey("nastrmail") )  return true;
//	    if  (this.sysNames.get("nastrmail")) return true;
		return false;
	}

	public boolean getViewPageEdit() {
		if (otOrgEkran.booleanValue() == true) { // Дали е връщане от екран за въвеждане и актуализации на организации
			// Прочитане параметрите, върнати от organizations.jsf - данни за организацията
			String par = JSFUtils.getRequestParameter("idObj");
			if (par != null && !par.trim().isEmpty()) {
				UserEdit.idOrg = Long.valueOf(par.trim());
				par = JSFUtils.getRequestParameter("nameObj");
				String s = null;
				if (par != null && !par.trim().isEmpty())
					s = par.trim();
				par = JSFUtils.getRequestParameter("eik");
				if (par != null) {
					par = par.trim();
					if (par.isEmpty())
						par = null;
				}
				if (par != null) {
					if (s == null)
						s = " (ЕИК: " + par + ") ";
					else
						s += " (ЕИК:  " + par + ") ";
				}

				UserEdit.nameOrg = s;

			}

			otOrgEkran = Boolean.FALSE;
		}

		return true;
	}

	public void clearAll() {
		this.user = new AdmUsers();
		this.filesList = new ArrayList<>();

		this.user.setLoginAttempts(Long.valueOf(0l));
		this.user.setUserGroups(new ArrayList<AdmGroups>());
		this.user.setUserRoles(new ArrayList<AdmUserRoles>());
		this.setPassInp(null);
		this.setPassPovt(null);
		this.viewPass = false;
		this.viewPassNastr = false;
		this.viewChangePass = false;
		if (this.param == 0)
			this.viewChangePass = true; // Винаги
		this.user.setUserType(Long.valueOf(Constants.CODE_ZNACHENIE_TIP_POTR_VATR)); // Вътрешен потребител
		this.user.setLang(Long.valueOf(Constants.CODE_ZNACHENIE_LANG_BG)); // По подразбиране

		UserEdit.idOrg = null;
		this.admZv = null;
		admZveno = null;
		
		this.posDlajn = null;
		this.begStatus = null;
		this.setZapis(false);
		this.ocenkaPass = null;

		UserEdit.otOrgEkran = Boolean.FALSE;

		clearTexts();
		clearNastrMail();
		clearSearchOrg();
		selRolesClear(); // Подготовка за избор на роли

		this.selectedGrList = new ArrayList<String>(); // Избрани групи

	}

	public void selRolesClear() {
		this.rootNode = null;
		this.selectedNode = null;
		this.mapSelRoles.clear();
		this.setSelectedRolesClassif(new ArrayList<Long>());
		this.selectedUserRoles = new ArrayList<AdmUserRoles>();
		this.selClassif = null;

	}

	public void clearTexts() {
		this.userTypeText = null;
		this.setUserLangText(null);
		this.setUserStatusText(null);
		this.nastrMailForwText = null;
		UserEdit.nameOrg = null;
		this.admZvText = null;
		admZvenoText ="";
		this.posDlajnText = null;
	}

	public void clearNastrMail() {
		this.mailServAddress = null;
		this.mailServAddress2 = null;
		this.mailUser = null;
		this.mailPass = null;
		this.mailEMail = null;
		this.mailForw = null;

		this.mailForw = Long.valueOf(Constants.CODE_ZNACHENIE_NE);
		this.user.setMailForward(Long.valueOf(Constants.CODE_ZNACHENIE_NE)); // НЕ
	}

	public void clearSearchOrg() { // За филтъра за избор на организация
		this.eikInp = null;
		this.nOrgInp = null;
		this.orgList = null;
	}

	public void actionViewChangePass() {
		if (this.viewChangePass)
			this.viewChangePass = false;
		else
			this.viewChangePass = true;
	}

	public void actionViewPass() {
		if (this.passInp == null || this.passInp.trim().isEmpty()) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
					getMessageResourceString("labels", "userEdit.noPassInp"));
			this.viewPass = false;
			return;
		}
		if (this.viewPass)
			this.viewPass = false;
		else
			this.viewPass = true;
	}

	public void actionViewPassNastr() {
		if (this.mailPass == null || this.mailPass.trim().isEmpty()) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
					getMessageResourceString("labels", "userEdit.noPassInp"));
			this.viewPassNastr = false;
			return;
		}
		if (this.viewPassNastr)
			this.viewPassNastr = false;
		else
			this.viewPassNastr = true;
	}

	public void actionChangeStatus() {
		this.user.setStatusExplain(null);
		this.user.setStatusDate(new Date());
	}

	public void setTextsZaUser(AdmUsers userInp) { // При първоначално зареждане данните за потребител при актуализация
													// и view
		clearTexts();
		if (userInp == null)
			return;
		try {
			if (userInp.getUserType() != null)
				this.userTypeText = this.sd.decodeItem(getClassifTip(), userInp.getUserType(), this.lang, currentDate,
						this.userId);
			if (userInp.getLang() != null)
				this.userLangText = this.sd.decodeItem(getClassifLang(), userInp.getLang(), this.lang, currentDate,
						this.userId);
			if (userInp.getStatus() != null)
				this.userStatusText = this.sd.decodeItem(getClassifStat(), userInp.getStatus(), this.lang, currentDate,
						this.userId);
			if (userInp.getMailForward() != null)
				this.nastrMailForwText = this.sd.decodeItem(getClassifDANE(), userInp.getMailForward(), this.lang,
						currentDate, this.userId);
			if (userInp.getOrgCode() != null)
				// this.admZvText = this.sd.decodeItem(getClassifAdmStr(), userInp.getOrgCode(),
				// this.lang, currentDate, this.userId);
				this.admZvText = this.sd.decodeItem(getCodeClassifWorkAdm(), userInp.getOrgCode(), this.lang, currentDate, this.userId);
			if (userInp.getPosition() != null)
				this.posDlajnText = this.sd.decodeItem(getClassifDlajn(), userInp.getPosition(), this.lang, currentDate,
						this.userId);
			if (userInp.getOrgId() != null)
				UserEdit.nameOrg = getNameOrg(userInp.getOrgId());
			
			if (userInp.getZveno() != null)
				this.admZvenoText = this.sd.decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, userInp.getZveno(), getCurrentLang(), new Date(), this.userId);

		} catch (DbErrorException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при декодиране - " + e.getMessage());
			return;
		}

		return;
	}

	public void setIdentifZaUser(int i) {
		if (this.user == null)
			return;
		if (i == 1) {
			UserEdit.idOrg = this.user.getOrgId(); // id на организация
			this.admZv = this.user.getOrgCode(); // код от адм. структура
			admZveno = user.getZveno();
			this.posDlajn = this.user.getPosition(); // код за длъжност

		} else {
			this.user.setOrgId(UserEdit.idOrg); // id на организация
			this.user.setOrgCode(this.admZv); // код от адм. структура
			user.setZveno(admZveno);
			this.user.setPosition(this.posDlajn); // код за длъжност
		}

		return;
	}

	public String getNameOrg(Long idOrg) throws DbErrorException {
		if (idOrg == null)
			return null;
		AdmOrgs org = new AdmOrgsDAO(this.idUser).findById(idOrg);
		if (org == null)
			return null;
		String name = null;
		if (org.getOrgName() != null)
			name = org.getOrgName().trim();
		if (org.getEik() != null) {
			if (name == null)
				name = "(ЕИК: " + org.getEik().trim() + ") ";
			else
				name += " (ЕИК: " + org.getEik().trim() + ") ";
		}
		return name;

	}

	public void setSearchOrg() {
		UserEdit.nameOrg = "Тест";
		UserEdit.idOrg = Long.valueOf(1);
		this.user.setOrgId(Long.valueOf(1));
	}

	public void clearIzbrOrg() {
		UserEdit.nameOrg = null;
		UserEdit.idOrg = null;
		this.user.setOrgId(null);
	}

	public void changeTipPotreb() {
		if (this.user.getUserType() != null && this.user.getUserType().longValue() == getTipVanshen().longValue()) {
			// Няма да се избира адм. звено
			this.admZvText = null;
			this.admZv = null;
			admZveno = null;
			admZvenoText = null;
		}
	}

	public void getDanniOrg() {
		clearSearchOrg(); // Първо изчистване на филтъра за избор на организации

		String par = JSFUtils.getRequestParameter("idOrg");
		if (par == null || par.trim().isEmpty())
			return;
		Long id_org = Long.valueOf(par.trim());
		if (id_org == null)
			return;
		try {
			UserEdit.nameOrg = getNameOrg(id_org);
			UserEdit.idOrg = id_org;
			this.user.setOrgId(id_org);

		} catch (DbErrorException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при търсене на запис за организация с ID= " + id_org + "  - " + e.getMessage());
			return;
		}
	}

	public void getUserInp(Long idUserInp) { // Зареждане данни за потребител
		try {

			if (idUserInp != null) {

				this.user = this.usersDAO.findById(idUserInp);
				if (this.user != null) {
					if (this.user.getUserGroups() == null)
						this.user.setUserGroups(new ArrayList<AdmGroups>());
					if (this.user.getUserRoles() == null)
						this.user.setUserRoles(new ArrayList<AdmUserRoles>());

					this.selectedGrList = new ArrayList<String>();
					if (this.user.getSelectedGroups() != null && !this.user.getSelectedGroups().isEmpty()) {
						this.user.getSelectedGroups().size();
						for (String gr : this.user.getSelectedGroups())
							this.selectedGrList.add(gr);
					}

					if (this.user.getUserRoles() != null) {
						this.user.getUserRoles().size(); // За всяка класификация ролите се получават отделно

					} else
						this.user.setUserRoles(new ArrayList<AdmUserRoles>());

					this.selectedUserRoles = new ArrayList<AdmUserRoles>();
					this.selectedUserRoles.addAll(this.user.getUserRoles());

					this.begStatus = this.user.getStatus(); // Запомняне статус на потребител

					// Зареждане на текстове и Id в работни полета
					setTextsZaUser(this.user);
					setIdentifZaUser(1);
					// Актуалните настройки за поща винаги са само в this.user
					// Те се зареждат в работните полета, когато се влизав модалния диалог и при
					// излизане са записват (или не ) в this.user

					// Парола - винаги има password
					if (this.user.getPassword() != null) {
						this.user.setPassword(this.user.getPassword().trim());

						// Разкодиране на паролата и зареждане в работни полета
						// *****************************************************
						this.passInp = new String(Base64.getDecoder().decode(this.user.getPassword()), "utf-8");
						// this.passInp = this.user.getPassword() ;

						// *****************************************************
					} else
						this.passInp = null; // this.user.getPassword() = null - Невъзможно

					this.passPovt = null;
					if (this.passInp != null) {
						this.passPovt = new String(this.passInp);
						setOcenkaPass();
						// provPass ();
					}

					this.statusInp = this.user.getStatus();

				}

			}

			// ********************************************************
			// Зареждане на прикачени файлове
			// ********************************************************

			this.filesList = new FilesDAO(this.idUser).findByCodeObjAndIdObj(Constants.CODE_OBJECTS_USERS,
					this.user.getId());

			if (this.filesList == null)
				this.filesList = new ArrayList<Files>();

		} catch (NumberFormatException e) {
			LOGGER.error("Грешка при обработка на данните за потребител!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "general.formatExc") + "-"
							+ e.getLocalizedMessage());

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при получаване данни за потребитела! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "general.exception") + "-"
							+ e.getLocalizedMessage());

		} finally {
			JPA.getUtil().closeConnection();
		}

		return;

	}

	// **********************************************************************************************
	// Проверки
	// **********************************************************************************************

	public boolean provPass() { // Проверка за въведена парола
		if (this.passInp == null && this.passPovt == null) {
			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errPass") + "!");
			return false;
		}

		if (!provPassInp())
			return false;
		if (!provPassPovt())
			return false;

		return true;
	}

	public boolean provPassInp() {
		if (this.passInp == null || this.passInp.trim().isEmpty()) {
			this.ocenkaPass = null;
			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errPass") + "!");

			return false;
		}

		this.passInp = this.passInp.trim();

		// Паролата трябва да е с дължина между 8 и 15 символа, да съдържа поне едно
		// число, да съдържа малки и големи латински букви и някои от символите +_-@$&*
		// без празни интервали !
		if (this.passInp.length() < 8 || this.passInp.length() > 15) {
			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errLenPass") + "!");
			return false;

		}
		UserUtils ut = new UserUtils();

		// Полето не трябва да съдържа Недопустими символи
		if (!isValidAllSimboli("qwertyuiopasdfghjklzxcvbnm" + "QWERTYUIOPASDFGHJKLZXCVBNM" + "0123456789" + "+_-@$&*",
				this.passInp)) {

			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errSymbPass") + "!");
			return false;
		}

		// Ако паролата едновременно не съдържа символи от pattern - малки и големи
		// латински, числа и специални символи
		if (!(!ut.isValidSimboli("qwertyuiopasdfghjklzxcvbnm", this.passInp)
				&& !ut.isValidSimboli("QWERTYUIOPASDFGHJKLZXCVBNM", this.passInp)
				&& !ut.isValidSimboli("0123456789", this.passInp) && !ut.isValidSimboli("+_-@$&*", this.passInp))) {
			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errSymbPass") + "!");
			return false;

		}

//			
		// Проверка за недопустими символи
//				 if  (!new UserUtils().isValidSimboli ("№\\\",.;:'{}=<>|/`~ ", this.passInp)) {
//					  JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"users.errPassword") + "!");
//					  return false;
//				 }
//				 
		// Трябва да съдържа поне едно число
//			  if (ut.isValidSimboli ("0123456789", this.passInp)) {
//				  JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"users.errSymbChPass") + "!");
//				  return false;  
//			  }

		setOcenkaPass();

		// ********************************************************************************************************************
		if ((this.user == null || this.user.getUsername() == null || this.user.getUsername().trim().isEmpty())
				&& this.passInp.length() < 8) {
			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errLPass") + "!");
			return false;
		}

		if (this.user != null && this.user.getUsername() != null
				&& this.user.getUsername().trim().compareToIgnoreCase("zxc") != 0 && this.passInp.length() < 8) {
			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errLPass") + "!");
			return false;
		}

		// **********************************************************************************************************************

		if (this.user != null && this.user.getUsername() != null
				&& this.user.getUsername().trim().compareToIgnoreCase("zxc") != 0
				&& this.user.getUsername().trim().compareToIgnoreCase(this.passInp) == 0) {
			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errUserNameEqPass") + "!");
			return false;
		}

		if (this.passPovt != null && !this.passInp.trim().isEmpty()) {
			if (this.passInp.compareTo(this.passPovt.trim()) != 0) {
				JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errNotEqPovt") + "!");
				return false;
			}

		}

		return true;
	}

	public void setOcenkaPass() {
		if (this.passInp == null || this.passInp.isEmpty()) {
			this.ocenkaPass = null;
			return;
		}

		// if (this.passInp.length() < 6) this.ocenkaPass = "слаба парола";
		if (this.passInp.length() <= 8)
			this.ocenkaPass = "слаба парола";
		else {
			UserUtils ut = new UserUtils();
			int score = 0;

			// if password bigger than 8 give 1 point
			if (this.passInp.length() > 8)
				score++;

			// if password has both lower(at least one) and uppercase (at least one)
			// characters give 1 point
			if (!ut.isValidSimboli("qwertyuiopasdfghjklzxcvbnm", this.passInp)
					&& !ut.isValidSimboli("QWERTYUIOPASDFGHJKLZXCVBNM", this.passInp))
				score++;
			// if (ut.isValidPass(this.passInp, 1) && ut.isValidPass(this.passInp, 2))
			// score++;

			// if password has at least one number give 1 point
			if (!ut.isValidSimboli("0123456789", this.passInp))
				score++;
			// if (ut.isValidPass(this.passInp, 4)) score++;

			// if password has at least one special caracther give 1 point
			// if (!ut.isValidSimboli ("!@#$%&*?_-+()[]^" , this.passInp)) score++;
			if (!ut.isValidSimboli("+_-@$&*", this.passInp))
				score++;
			// if (ut.isValidPass(this.passInp, 3)) score++;

			// if password bigger than 10 give another 1 point
			if (this.passInp.length() > 10)
				score++;

			if (score <= 2)
				this.ocenkaPass = "слаба парола";
			else if (score > 2 && score <= 4)
				this.ocenkaPass = "средна парола";
			else if (score > 4)
				this.ocenkaPass = "силна парола";
		}

	}

	public boolean provPassPovt() {
		if (this.passInp == null || this.passInp.trim().isEmpty()) {
			JSFUtils.addMessage("formUserEdit:inPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errPass") + "!");

			return false;
		}

		if (this.passPovt == null || this.passPovt.trim().isEmpty()) {
			if (this.passInp != null && !this.passInp.isEmpty())
				JSFUtils.addMessage("formUserEdit:povtPass", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errPovt") + "!");
			return false;
		}

		if (this.passInp.compareTo(this.passPovt.trim()) != 0) {
			JSFUtils.addMessage("formUserEdit:povtPass", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errNotEqPovt") + "!");
			return false;
		}

		return true;
	}

	public boolean provUserName() { // Проверка за въведено LoginName (UserName)
		if (this.user.getUsername() == null || this.user.getUsername().trim().isEmpty()) {
			JSFUtils.addMessage("formUserEdit:inLogin", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errUserName") + "!");
			return false;
		}

		String loginName = this.user.getUsername().trim();

		// Проверка за недопустими символи
		if (!new UserUtils().isValidSimboli("№\\\",;:'%(){}[]+^=<>|/`~ ", loginName)) {
			JSFUtils.addMessage("formUserEdit:inLogin", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errLogin") + "!");
			return false;
		}

		if (loginName.compareToIgnoreCase("zxc") != 0 && loginName.length() < 5) {
			JSFUtils.addMessage("formUserEdit:inLogin", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errLUserName") + "!");
			return false;
		}

		try {
			if (this.usersDAO.checkForDublUserName(loginName, this.user.getId())) {
				JSFUtils.addMessage("formUserEdit:inLogin", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.dublUserName") + "!");
				return false;
			}
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при работа със системата при проверка за дублиране на потребителско име!!!" + "-"
					+ e.getLocalizedMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errProvDubl") + "!" + "-"
							+ e.getLocalizedMessage());
			return false;

		}
		if (this.passInp != null && !this.passInp.trim().isEmpty() && loginName.compareToIgnoreCase("zxc") != 0
				&& loginName.compareToIgnoreCase(this.passInp.trim()) == 0) {
			JSFUtils.addMessage("formUserEdit:inLogin", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errUserNameEqPass") + "!");
			return false;
		}

		this.user.setUsername(loginName);
		return true;
	}

	public boolean provEMail() { // Проверка за e-mail
		if (this.user != null && this.sysNames.containsKey("email")) { // Може да се въвежда email за потребител
			if (this.user.getEmail() != null) {
				this.user.setEmail(this.user.getEmail().trim());
				if (this.user.getEmail().isEmpty())
					this.user.setEmail(null);
			}
			if (this.zadEmail && this.user.getEmail() == null) {
				if (!this.sysNames.containsKey("statusExplain") || this.sysNames.get("statusExplain") == Boolean.FALSE)
					JSFUtils.addMessage("formUserEdit:usermail", FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(Constants.beanMessages, "users.errZadEMail"));
				else
					JSFUtils.addMessage("formUserEdit:usermail", FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(Constants.beanMessages, "users.errZadEMail"));
				return false;
			}
			if (this.user.getEmail() != null && !ValidationUtils.isEmailValid(this.user.getEmail())) {
				// JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
				// getMessageResourceString(Constants.beanMessages,"users.errEMail") + "!" );
				if (!this.sysNames.containsKey("statusExplain") || this.sysNames.get("statusExplain") == Boolean.FALSE)
					JSFUtils.addMessage("formUserEdit:usermail", FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(Constants.beanMessages, "users.errEMail") + "!");
				else
					JSFUtils.addMessage("formUserEdit:usermail", FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(Constants.beanMessages, "users.errEMail") + "!");
				return false;
			}

		}

		return true;
	}

	public boolean provPhone(int k) { // Проверка за phone
		if (this.user != null && this.sysNames.containsKey("phone")) { // Може да се въвежда телефон за потребител
			if (this.user.getPhone() != null) {
				this.user.setPhone(this.user.getPhone().trim());
				if (this.user.getPhone().isEmpty())
					this.user.setPhone(null);
			}
			if (this.zadPhone && this.user.getPhone() == null) {
				if (k == 1)
					JSFUtils.addMessage("formUserEdit:phone1", FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(Constants.beanMessages, "users.errZadPhone"));
				else if (k == 2)
					JSFUtils.addMessage("formUserEdit:phone2", FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(Constants.beanMessages, "users.errZadPhone"));
				else
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(Constants.beanMessages, "users.errZadPhone"));

				return false;
			}

			if (this.user.getPhone() != null) {
				// Проверка за допустими символи при въвеждане номер телефон/факс
				if (!isTel(this.user.getPhone())) {

					if (k == 1)
						JSFUtils.addMessage("formUserEdit:phone1", FacesMessage.SEVERITY_ERROR,
								getMessageResourceString(Constants.beanMessages, "users.errPhone"));
					else if (k == 2)
						JSFUtils.addMessage("formUserEdit:phone2", FacesMessage.SEVERITY_ERROR,
								getMessageResourceString(Constants.beanMessages, "users.errPhone"));
					else
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
								getMessageResourceString(Constants.beanMessages, "users.errPhone"));

					return false;

				}

			}
		}

		return true;

	}

	// Проверки за настройки за поща

	/**
	 * Проверка за URL 1
	 * 
	 * @param servAddrr1
	 * @return
	 */
	public boolean provServerAddrr1(String servAddrr1) {
		if (servAddrr1 != null) {
			servAddrr1 = servAddrr1.trim();
			if (servAddrr1.isEmpty())
				servAddrr1 = null;
		}
		if (servAddrr1 != null && !isValidURL(servAddrr1)) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errServAddr1Nastr") + "!");
			return false;
		}

		return true;
	}

	public boolean provServerAddrr2(String servAddrr2) {
		if (servAddrr2 != null) {
			servAddrr2 = servAddrr2.trim();
			if (servAddrr2.isEmpty())
				servAddrr2 = null;
		}
		if (servAddrr2 != null && !isValidURL(servAddrr2)) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errServAddr2Nastr") + "!");
			return false;
		}

		return true;
	}

	/**
	 * Проверка за въведен потребител в настройка за поща
	 * 
	 * @param mailUs
	 * @return
	 */
	public boolean provMailUser(String mailUs) { // Проверка за име на потребител в настройки за поща
		if (mailUs != null) {
			mailUs = mailUs.trim();
			if (mailUs.isEmpty())
				mailUs = null;
		}

		if (mailUs != null) {
			// Проверка за недопустими символи
			if (!new UserUtils().isValidSimboli("№\\\",.;:'%(){}[]+^=<>|/`~ ", mailUs)) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errMailUser") + "!");
				return false;

			}

			if (mailUs.length() < 3) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errLenMailUser") + "!");
				return false;
			}
		}

		return true;
	}

	/**
	 * Проверка за парола за поща в настройки за поща
	 * 
	 * @param mailPas
	 * @return
	 */
	public boolean provMailPass(String mailPas) {
		if (mailPas != null) {
			mailPas = mailPas.trim();
			if (mailPas.isEmpty())
				mailPas = null;
		}

		if (mailPas != null) {
			// Проверка за недопустими символи

			if (!new UserUtils().isValidSimboli("№\\\",.;:'%(){}[]^=<>|/`~ ", mailPass)) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errMailPassword") + "!");
				return false;

			}
//			     if (mailPas.indexOf(' ') >= 0) {
//			    	  JSFUtils.addGlobalMessage( FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"users.errSpMailPass") + "!");
//					  return false;
//			    	 
//			     }

			if (mailPas.length() < 3) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errLenMailPass") + "!");
				return false;
			}
		}
		return true;
	}

	/**
	 * Проверка за въведен e-mail в настройки за поща
	 * 
	 * @param mailIn
	 * @return
	 */
	public boolean provEMailNastr(String mailIn) {
		if (mailIn != null) {
			mailIn = mailIn.trim();
			if (mailIn.isEmpty())
				mailIn = null;
		}
		if (mailIn != null && !ValidationUtils.isEmailValid(mailIn)) { // Полето в страницата е mailnastr
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errEMailNastr") + "!");
			return false;
		}

		return true;
	}

	/**
	 * Всички проверки за настройка за поща - pr= 1 - проверяват се работните полета
	 * ; pr = 2 - проверяват се полетата от this.user
	 * 
	 * @return
	 */
	public boolean provAllZaNastrMail(int pr) {
		boolean prRet = true;

		if (pr == 1) {
			if (!provServerAddrr1(this.mailServAddress))
				prRet = false;
			if (!provServerAddrr2(this.mailServAddress2))
				prRet = false;
			if (!provMailUser(this.mailUser))
				prRet = false;
			if (!provMailPass(this.mailPass))
				prRet = false;
			if (!provEMailNastr(this.mailEMail))
				prRet = false;
		} else {
			if (!provServerAddrr1(this.user.getMailImapServer()))
				prRet = false;
			if (!provServerAddrr2(this.user.getMailSmtpServer()))
				prRet = false;
			if (!provMailUser(this.user.getMailUser()))
				prRet = false;
			if (!provMailPass(this.user.getMailPass()))
				prRet = false;
			if (!provEMailNastr(this.user.getMailEmail()))
				prRet = false;

		}

		return prRet;

	}

//********************************************************************************************************************	    

	public boolean provZadInfo() throws DbErrorException {
		boolean prRet = true;

		if (!provUserName())
			prRet = false;
//		  if (!provPass ())  prRet = false;

		if (this.user != null) {

			if (this.user.getNames() != null) {
				this.user.setNames(this.user.getNames().trim());
				if (this.user.getNames().isEmpty())
					this.user.setNames(null);
			}
			if (this.user.getNames() == null) {
				JSFUtils.addMessage("formUserEdit:usernames", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errNames") + "!");
				prRet = false;

			}
			if (this.user.getUserType() == null) {
				JSFUtils.addMessage("formUserEdit:tip", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errTipPotreb") + "!");
				prRet = false;
			}
			if (this.user.getLang() == null && this.zadLang) {
				JSFUtils.addMessage("formUserEdit:lang", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errLangPotreb") + "!");
				prRet = false;
			} else {
				if (this.user.getLang() == null)
					this.user.setLang(Constants.CODE_ZNACHENIE_LANG_BG); // Език по подразбиране
			}

			if (this.user.getStatus() == null) { // Статусът е задължителен
				JSFUtils.addMessage("formUserEdit:stat", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errStatPotreb") + "!");
				prRet = false;
			}

			// За останалите полета се проверява, когато за тях е зададено, че са налични и че са задължителни
			if (this.zadStatExpl
					&& (this.user.getStatusExplain() == null || this.user.getStatusExplain().trim().isEmpty())) { // Статусът е задължителен

				JSFUtils.addMessage("formUserEdit:statExpl", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errZadStatExpl") + "!");
				prRet = false;
			}

			if (this.zadWAdmCode && this.user.getUserType() != null && this.user.getUserType() != this.getTipVanshen()
					&& this.user.getOrgCode() == null) {
				JSFUtils.addMessage("formUserEdit:textZv", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errZadWorkOrg") + "!");
				prRet = false;

			}

			if (this.zadWOrg && this.user.getOrgId() == null) {

				JSFUtils.addMessage("formUserEdit:textOrg", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errZadWOrg") + "!");
				prRet = false;
			}

			if (this.zadPos && this.user.getPosition() == null) {

				JSFUtils.addMessage("formUserEdit:textDl", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errZadPos") + "!");
				prRet = false;
			}

			if (this.zadPosText
					&& (this.user.getPositionText() == null || this.user.getPositionText().trim().isEmpty())) {

				JSFUtils.addMessage("formUserEdit:textP", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errZadPosText") + "!");
				prRet = false;
			}

			if (this.zadWPlace && (this.user.getOrgText() == null || this.user.getOrgText().trim().isEmpty())) {

				JSFUtils.addMessage("formUserEdit:textO", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "users.errZadOrgText") + "!");
				prRet = false;
			}

		}

		return prRet;
	}

	public boolean isAllNastrmail() { // Проверка дали всички полета за настройка на пощата са зададени или са празни
		if ((this.user.getMailImapServer() == null || this.user.getMailImapServer().trim().isEmpty())
				&& (this.user.getMailSmtpServer() == null || this.user.getMailSmtpServer().trim().isEmpty())
				&& (this.user.getMailUser() == null || this.user.getMailUser().trim().isEmpty())
				&& (this.user.getMailPass() == null || this.user.getMailPass().trim().isEmpty())
				&& (this.user.getMailEmail() == null || this.user.getMailEmail().trim().isEmpty()))
			return true;

		if (this.user.getMailImapServer() != null && this.user.getMailSmtpServer() != null
				&& this.user.getMailUser() != null && this.user.getMailPass() != null
				&& this.user.getMailEmail() != null)
			return true; // Всички полета са зададени

		return false;
	}

	public boolean isNullNastrmail() {
		if ((this.user.getMailImapServer() == null || this.user.getMailImapServer().trim().isEmpty())
				&& (this.user.getMailSmtpServer() == null || this.user.getMailSmtpServer().trim().isEmpty())
				&& (this.user.getMailUser() == null || this.user.getMailUser().trim().isEmpty())
				&& (this.user.getMailPass() == null || this.user.getMailPass().trim().isEmpty())
				&& (this.user.getMailEmail() == null || this.user.getMailEmail().trim().isEmpty()))
			return true;
		return false;
	}

	/**
	 * Потвърждение за въвеждане настройки за поща
	 */
	public void returnWithPotv() {
		// Проверка за правилно въведени данни
		// if (!provAllZaNastrMail(1)) return;

		provAllZaNastrMail(1); // Въпреки грешки, данните се прехвърлят в this.user
		setNastrMail(2); // Ако има грешки, те се хващат при Save

		if (isNullNastrmail())
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
					getMessageResourceString(Constants.beanMessages, "users.noNastrMail") + "!");
		else if (!isAllNastrmail())
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
					getMessageResourceString(Constants.beanMessages, "users.noAllNastrMail") + "!");

	}

	// ************************************************************************************************************************

	/**
	 * Дали въведен String съдържа само цифри, тире + / ; и интервал!!!!
	 * 
	 * @param str
	 * @return
	 */
	public boolean isTel(String str) {

		Pattern pattern = Pattern.compile("^[0-9 \\-\\+\\;\\/]+$");

		Matcher matcher = pattern.matcher(str);
		if (!matcher.matches()) {
			return false;
		}
		return true;
	}

	public boolean isValidURL(String url) {

		URL u = null;

		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			return false;
		}

		try {
			u.toURI();
		} catch (URISyntaxException e) {
			return false;
		}

		return true;
	}

	// *********************************************************************************************

	public void actionSave() throws DbErrorException {
		return;
	}

	public void actionSaveOrig() throws DbErrorException {

		boolean prRet = false;

		if (this.user == null) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errUser") + "!");
			return;
		}

		// *************************************************************************************
		// Не се прави тази защита за повторно натискане на бутона ЗАПИС поради забавяне
		// на изпращане на съобщението по пощата или загубване на управлението след
		// издаване на sendMail
		// if (this.isZapis) {
		// JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,
		// getMessageResourceString(Constants.beanMessages,"users.isZapisTrue") + "!" );
		// return;
		// }
		//
		// this.isZapis = true;
		// *************************************************************************************

		// Запис на работни полета
		setIdentifZaUser(2);

		if (!provZadInfo())
			prRet = true; // Не е проверена въведена парола

		// Проверка за въведена парола
		boolean isNewUser = false;
		boolean passChangedDetected = false;
		if (this.user.getId() == null)
			isNewUser = true;

		if (!isNewUser) { // За стар потребител
			// Сравнение на паролата с оригиналната стойност в this.user
			// В this.user паролата е кодирана
			String passOldDec = null;
			try {
				passOldDec = new String(Base64.getDecoder().decode(this.user.getPassword()), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
				passOldDec = null;
			}
			if (passOldDec != null && !passInp.trim().equals(passOldDec)) {
				// значи имаме промяна на паролата.
				passChangedDetected = true;
			}
		}

		if (isNewUser || passChangedDetected) { // Проверка за въведена парола
			if (!provPass())
				prRet = true;
		}

		// Проверка за незададени за въвеждане полета
		if (!this.sysNames.containsKey("email"))
			this.user.setEmail(null);
		else {
			if (!provEMail())
				prRet = true;
		}

		if (!this.sysNames.containsKey("phone"))
			this.user.setPhone(null);
		else {
			if (!provPhone(0))
				prRet = true;
		}

		// За настройката за поща всички полета трябва да са заредени в this.user, ако е
		// зададена някаква настройка - позволява се да не са записани всичкеи полета -
		// задължително е само mailForward
		if (!provAllZaNastrMail(2))
			prRet = true;

		if (this.user.getMailForward() == null) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errMailForw") + "!");
			prRet = true;
			;
		}

		if (prRet) {
			this.setZapis(false);
			return;
		}

		// ******************************************************************************************

		if (this.user.getStatus() == null || (this.user.getStatus() != null
				&& this.user.getStatus().longValue() != Constants.CODE_ZNACHENIE_ZAKL))
			this.user.setLoginAttempts(Long.valueOf(0l));

		if (!this.sysNames.containsKey("statusExplain")) {
			if (this.user.getStatus() == null || (this.user.getStatus() != null
					&& this.user.getStatus().longValue() != Constants.CODE_ZNACHENIE_ZAKL))
				this.user.setStatusExplain(null);
		} else {
			if (this.user.getStatusExplain() != null) {

				this.user.setStatusExplain(this.user.getStatusExplain().trim());
				if (this.user.getStatusExplain().isEmpty())
					this.user.setStatusExplain(null);
			}
		}
		if (!this.sysNames.containsKey("workplace"))
			this.user.setOrgText(null);
		else {
			if (this.user.getOrgText() != null) {
				this.user.setOrgText(this.user.getOrgText().trim());
				if (this.user.getOrgText().isEmpty())
					this.user.setOrgText(null);
			}

		}
		if (!this.sysNames.containsKey("workAdmCode")) {
			this.user.setOrgCode(null);
			this.admZv = null;
			this.admZvText = null;
			admZveno = null;
			admZvenoText = null;
		} else {
			if (this.user.getUserType() != null && this.user.getUserType().longValue() == getTipVanshen().longValue()) {
				// За външен потребител не се избира адм. звено
				this.admZv = null;
				this.admZvText = null;
				admZveno = null;
				admZvenoText = null;
			}
		}
		if (!this.sysNames.containsKey("workOrg")) {
			this.user.setOrgId(null);
			UserEdit.idOrg = null;
			UserEdit.nameOrg = null;

		}

		if (!this.sysNames.containsKey("position")) {
			this.user.setPosition(null);
			this.posDlajn = null;
			this.posDlajnText = null;
		}

		if (!this.sysNames.containsKey("positionText"))
			this.user.setPositionText(null);
		else {
			if (this.user.getPositionText() != null) {
				this.user.setPositionText(this.user.getPositionText().trim());
				if (this.user.getPositionText().isEmpty())
					this.user.setPositionText(null);
			}

		}

		// Само информационно съобщение за пълнота на настройка за поща
		/*
		 * if (isNullNastrmail () )
		 * JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
		 * getMessageResourceString(Constants.beanMessages,"users.noNastrMail") + "!" );
		 * else if (!isAllNastrmail ())
		 * JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
		 * getMessageResourceString(Constants.beanMessages,"users.noAllNastrMail") + "!"
		 * );
		 */

		// Зареждане на групи
		this.user.setSelectedGroups(selectedGrList);

		// Зареждане на роли - те са готови винаги в последен вариант в
		// this.selectedUserRoles
		setAllRolesInUser(); // Зареждане на ролите в this.user в userRoles list

		// За нов потребител

		if (this.user.getId() == null) {
			// нов потребител
			this.user.setDateReg(new Date()); // Това се прави автоматично при save
			this.user.setPassLastChange(new Date());
			this.user.setLoginAttempts(Long.valueOf(0l));
			this.user.setPassIsNewBoolean(true);
			// Криптиране на паролата

			isNewUser = true;
		}

		// Проверка за промяна на парола - за стар потребител

		boolean isStatusChangeActiv = false; // Смяна на статус на активен
		boolean isOldStatusOchReg = false; // Старият статус е бил 'В процес (очакване) на регистрация '

		if (!isNewUser) {
			// Дали статусът на потребителя е сменен на активен
			if (this.user.getStatus() != null && this.user.getStatus().longValue() == Constants.CODE_ZNACHENIE_ACT) {
				// Сравнение с предишната стойност за статуса
				if (this.begStatus == null
						|| (this.begStatus != null && this.begStatus.longValue() != Constants.CODE_ZNACHENIE_ACT)) {
					isStatusChangeActiv = true;
					if (this.begStatus != null && this.begStatus.longValue() == Constants.CODE_ZNACHENIE_OCHAKV_REG)
						isOldStatusOchReg = true;
				}
			}

			// Сравнение на паролата с оригиналната стойност в this.user - това е направено
			// по-горе
			// В this.user паролата е кодирана
//			 String passOldDec = null;
//			try {
//				passOldDec = new String(Base64.getDecoder().decode(this.user.getPassword()),  "utf-8");
//			} catch (UnsupportedEncodingException e) {
//	//			e.printStackTrace();
//				passOldDec = null;
//			}
//			  if (passOldDec != null && !passInp.trim().equals(passOldDec)) {
			if (passChangedDetected) {
				// имаме промяна на паролата. за стар потребител
				this.user.setPassLastChange(currentDate);
				this.user.setPassIsNewBoolean(true);

			}
		}

		String pass = "";
		try {
			passInp = passInp.trim();
			pass = new String(passInp);
			// *********************************************************
			this.user.setPassword(Base64.getEncoder().encodeToString(passInp.getBytes("utf-8")));
			// this.user.setPassword(passInp);
			// *********************************************************

			// Промяна дата на статуса, ако е променен
			if (this.user.getId() == null || (this.statusInp == null && this.user.getStatus() != null)
					|| (this.statusInp != null && !this.user.getStatus().equals(this.statusInp))) {
				this.user.setStatusDate(DateUtils.startDate(new Date()));
				if (this.user.getStatus().equals(Constants.CODE_ZNACHENIE_ACT)) {
					this.user.setStatusExplain(null);
					this.user.setLoginAttempts(Long.valueOf(0l));
				}

			}

			// В полето LoginAttempts не трябва да има стойност null
			if (this.user.getLoginAttempts() == null)
				this.user.setLoginAttempts(Long.valueOf(0l));

			JPA.getUtil().begin();

			this.usersDAO.save(this.user);

			JPA.getUtil().flush();

			if (!this.deleteFilesList.isEmpty()) {
				for (int i = 0; i < this.deleteFilesList.size(); i++) {
					if (this.deleteFilesList.get(i).getId() != null) {
						new FilesDAO(this.idUser).delFromFilesText(this.deleteFilesList.get(i).getId());
						new FilesDAO(this.idUser).deleteById(this.deleteFilesList.get(i).getId());
					}
				}
			}

			for (int i = 0; i < this.filesList.size(); i++) {

				if (this.filesList.get(i).getId() == null) {
					this.filesList.get(i).setCodeObject(this.user.getCodeMainObject());
					this.filesList.get(i).setIdObject(this.user.getId());

					new FilesDAO(this.idUser).save(this.filesList.get(i));

				} else { // при редакция не е измъкнат целия обект и затова ще се прави ъпдейт на 4
							// полета - description, visibleOnSite, userLastMod, dateLastMod

					new FilesDAO(this.idUser).updateFile(this.filesList.get(i).getDescription(), this.userId,
							new Date(), this.filesList.get(i).getId());
				}
			}

			JPA.getUtil().commit();

			// Успешен запис
			this.userId = this.user.getId();
			getUserInp(this.userId); // Изчитане отново пълните данни за потребителя

			// Изпращане на съобщение за извършена актуализация на данни за потребител
			String nameModelDef = this.sd.decodeItem(Constants.CODE_CLASSIF_ALL_INFO_MODELS, Constants.SYSTEM_MODEL_ID,
					this.lang, currentDate, this.idUser);

			if (isNewUser) {
				String s = "";
				if (nameModelDef != null)
					s = "Вашият потребителски профил за система '" + nameModelDef
							+ "' беше успешно създаден със следните данни : ";
				else
					s = "Вашият потребителски профил  беше успешно създаден със следните данни : ";

				s += "\r\n " + "  Потребителско име : " + this.user.getUsername();
				s += "\r\n " + "  Парола : " + pass;
				sendMail("Създаден потребителски профил", s, (nameModelDef != null ? nameModelDef : ""));
			} else {
				if (isStatusChangeActiv) {
					String s = "";
					if (nameModelDef != null)
						s = "Вашият потребителски профил за система '" + nameModelDef
								+ "' става активен със следните данни : ";
					else
						s = "Вашият потребителски профил става активен със следните данни : ";

					s += "\r\n " + "  Потребителско име : " + this.user.getUsername();
					if (!isOldStatusOchReg)
						s += "\r\n " + "  Парола : " + pass;
					else
						s += "\r\n ";

					sendMail("Активен потребителски профил", s, (nameModelDef != null ? nameModelDef : ""));

				} else { // Съобщението за смяна на парола се изпраща само когато не е сменен статуса от
							// неактивен на активен
					if (passChangedDetected) {
						String s = "";
						if (nameModelDef != null)
							s = "Паролата на вашия профил в система '" + nameModelDef
									+ "' беше сменена успешно. Последни данни за профила : ";
						else
							s = "Паролата на вашия профил  беше сменена успешно. Последни данни за профила : ";
						s += "\r\n " + "  Потребителско име : " + this.user.getUsername();
						s += "\r\n " + "  Парола : " + pass;
						sendMail("Смяна на парола", s, (nameModelDef != null ? nameModelDef : ""));
					}
				}

			}

			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
					getMessageResourceString("beanMessages", "general.succesSaveMsg"));

		} catch (DbErrorException e) {

			JPA.getUtil().rollback();
			LOGGER.error("Грешка при изтриване на записан потребитела! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			this.setZapis(false);
			return;
		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "general.exception") + "-"
							+ e.getLocalizedMessage());
			this.setZapis(false);
			return;
		} finally {
			JPA.getUtil().closeConnection();

		}

		this.setZapis(false);
		return;
	}

	public void deletePotreb() {
		if (this.user.getId() == null)
			return;

		boolean prCl = true;
		String userName = this.user.getUsername();
		if (userName != null) {
			userName = userName.trim();
			if (userName.isEmpty())
				userName = null;
		}
		@SuppressWarnings("unused")
		String nameMail = this.user.getEmail();

		try {

			JPA.getUtil().begin();

			if (!this.filesList.isEmpty()) {
				for (int i = 0; i < filesList.size(); i++) {
					new FilesDAO(this.idUser).deleteById(Long.valueOf(filesList.get(i).getId()));
				}
			}

			JPA.getUtil().flush();

			// this.usersDAO.deleteById(this.user.getId());
			
			//TODO iztriwane na vyn[ni potrebiteli delete jobs_users_add
			
			this.usersDAO.delete(this.user); // Да се отбелязва в журнала изтриването

			JPA.getUtil().commit();

			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.successDeleteMsg"));

			if (!this.deleteFilesList.isEmpty()) {
				deleteFilesList.clear();
			}

			// Изпращане на съобщение за извършено изтриване на данни за потребител
//				String nameModelDef = this.sd.decodeItem(Constants.CODE_CLASSIF_INF_MODEL, SysConstants.SYSTEM_MODEL_ID, this.lang, currentDate, this.idUser);
//				 String s = "";
//					if (nameModelDef != null)
//						s = "Вашият потребителски профил за система '" + nameModelDef + "' е изтрит " ;
//					else
//						s = "Вашият потребителски профил е изтрит от системата " ;
//				    
//					if (userName != null)
//					 s += "\r\n " + "  Потребителско име : " + userName;
//										
//					sendMailToNameMail("Изтрит потребителски профил", s,(nameModelDef!=null?nameModelDef:""), nameMail);

		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при изтриване на записан потребитела! ", e);
			prCl = false;
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			return;
		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);
			prCl = false;
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "general.exception") + "-"
							+ e.getLocalizedMessage());

			return;
		} finally {
			JPA.getUtil().closeConnection();

		}

		if (prCl) {
			this.param = 0; // Въвеждане на нов потребител
			clearAll();
		}

	}

	public void setNastrMail(int i) {
		if (i != 2) {
			this.mailServAddress = this.user.getMailImapServer();
			this.mailServAddress2 = this.user.getMailSmtpServer();
			this.mailUser = this.user.getMailUser();
			this.mailPass = this.user.getMailPass();
			this.mailEMail = this.user.getMailEmail();
			this.mailForw = this.user.getMailForward();
		} else {
			if (this.mailServAddress != null) {
				this.mailServAddress = this.mailServAddress.trim();
				if (this.mailServAddress.isEmpty())
					this.mailServAddress = null;
			}
			this.user.setMailImapServer(this.mailServAddress);
			if (this.mailServAddress2 != null) {
				this.mailServAddress2 = this.mailServAddress2.trim();
				if (this.mailServAddress2.isEmpty())
					this.mailServAddress2 = null;
			}
			this.user.setMailSmtpServer(this.mailServAddress2);
			if (this.mailUser != null) {
				this.mailUser = this.mailUser.trim();
				if (this.mailUser.isEmpty())
					this.mailUser = null;
			}
			this.user.setMailUser(this.mailUser);
			if (this.mailPass != null) {
				this.mailPass = this.mailPass.trim();
				if (this.mailPass.isEmpty())
					this.mailPass = null;
			}
			this.user.setMailPass(this.mailPass);
			if (this.mailEMail != null) {
				this.mailEMail = this.mailEMail.trim();
				if (this.mailEMail.isEmpty())
					this.mailEMail = null;
			}
			this.user.setMailEmail(this.mailEMail);
			this.user.setMailForward(this.mailForw);

		}

	}

	// ******************************************************************************************************************
	public void searchOrgs() { // В модалния диалог за търсене на организации
		this.orgList = null;
		SelectMetadata smd = null;

		smd = new AdmOrgsDAO(this.idUser).findOrgs(this.eikInp, this.nOrgInp);

		try {
			this.orgList = new LazyDataModelSQL2Array(smd, "A1");
		} catch (DbErrorException e) {
			e.printStackTrace();
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при търсене на записани организации! " + "-" + e.getMessage() + e.getCause().getMessage());
			return;
		} finally {
			JPA.getUtil().closeConnection();
		}

		return;

	}

	// ***********************************************************************************************
	// Зареждане на Tree с поредна избрана класификация
	public void actionLoadTree() {

		String par = JSFUtils.getRequestParameter("idObjAccEdit");
		if (par != null)
			this.selClassif = Long.valueOf(par.trim());
		else {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString("beanMessages", "user.errCodeClas"));
			return;
		}
		@SuppressWarnings("unused")
		String rkv = JSFUtils.getRequestParameter("rkvIndexD");

		try {

			List<SystemClassif> listItems = getSystemData().getSysClassification(this.selClassif, currentDate,
					this.lang, this.idUser);

			// Получаване на досега избраните роли за тази класификация, записани в
			// this.user
			List<Long> rolesList = new ArrayList<Long>();
			if (this.selectedUserRoles != null && this.selectedUserRoles.size() > 0)
				rolesList = getSelectedRoles(this.selClassif);

			this.rootNode = new TreeUtils().loadCheckboxTree(listItems, false, rolesList, true, null, null);
			mapSelRoles.clear();
			if (!rolesList.isEmpty())
				for (Long roll : rolesList)
					mapSelRoles.put(roll, roll); // Запазване първоначалните роли при избор на класификация

			// PF po malki 7 versii
			// RequestContext.getCurrentInstance().execute("PrimeFaces.widgets.dostapWV.unselectAllRows()");
			// RequestContext.getCurrentInstance().execute("PrimeFaces.widgets.dostapWV.selectRow("
			// + rkv + ")");

			// PF po golemi 6.2 versii i raboti ako e dobawen atributa da moje da se
			// selektira red w tablicata. (podmenqme go s onclick="highlightElement(this
			// ,'tr');" )
			// PrimeFaces.current().executeScript("PrimeFaces.widgets.dostapWV.unselectAllRows()");
			// PrimeFaces.current().executeScript("PrimeFaces.widgets.dostapWV.selectRow(" +
			// rkv + ")");

		} catch (NumberFormatException e) {
			LOGGER.error("Грешка при обработка на данните!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "general.formatExc"));

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при зареждане на дърво! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "general.exception"));

		}

	}

	/**
	 * Този метод се изпълнява при всеки click на node от дървото - при select и
	 * unselect В this.selectedNode при това действие се намира пълната съвкупност
	 * от роли за заредената сласификация , записана в this.selClassif
	 */

	public void actionNodeClick() {
		// Зареждане на роли за последната избрана класификация
		if (this.param != 2 && this.selClassif != null) {
			// Всеки път ролите се зареждат отново за избраната класификация
			ArrayList<Long> codeList = new ArrayList<Long>();
			for (TreeNode item : this.selectedNode)
				codeList.add(((SystemClassif) item.getData()).getCode());
			setSelectedRoles(codeList, this.selClassif); // Актуализация на this.selectedUserRoles с пълната съвкупност
															// на избраните роли за класификацията

		}
	}

	/**
	 * Връща текущите стойности за колекция "Роли" за конкретна класификация. - от
	 * this.selectedUserRoles - избраните роли за всички класификации
	 * 
	 * @return Роли
	 */
	public ArrayList<Long> getSelectedRoles(Long codeClassif) {
		if (codeClassif == null)
			return null;
		ArrayList<Long> rez = new ArrayList<Long>();

		if (this.selectedUserRoles != null && this.selectedUserRoles.size() > 0) {
			for (AdmUserRoles role : this.selectedUserRoles) {
				if (role.getCodeClassif() != null && role.getCodeClassif().longValue() == codeClassif.longValue())
					rez.add(role.getCodeRole());
			}
		}

		return rez;
	}

	/**
	 * Зарежда текущите стойности на колекция "Роли" за конкретна класификация.в
	 * this.selectedUserRoles
	 * 
	 * 
	 */
	public void setSelectedRoles(ArrayList<Long> roles, Long codeClassif) {

		if (this.selectedUserRoles == null)
			this.selectedUserRoles = new ArrayList<AdmUserRoles>();
		else {
			// Първо изтриване на всички въведени роли за класификацията
			List<AdmUserRoles> rolsN = new ArrayList<AdmUserRoles>();
			for (AdmUserRoles rol : this.selectedUserRoles) {
				if (rol.getCodeClassif() != null && rol.getCodeClassif().longValue() == codeClassif.longValue())
					continue;
				rolsN.add(rol);
			}
			this.selectedUserRoles.clear();
			this.selectedUserRoles.addAll(rolsN);

		}

		if (roles != null && !roles.isEmpty()) { // Пълните роли за класификация codeClassif
			Map<Long, Boolean> rolsMap = new HashMap<Long, Boolean>();
			for (int i = 0; i < roles.size(); i++) {
				rolsMap.put(roles.get(i), Boolean.TRUE);
			}

			List<AdmUserRoles> dopParRols = new ArrayList<AdmUserRoles>();
			for (Long code : roles) { // Зададените роли за класификацията, получени при click - ако не е пълно ниво,
										// parent code за нивото не се връщаf
				AdmUserRoles ur = new AdmUserRoles();
				ur.setCodeClassif(codeClassif);
				ur.setCodeRole(code);
				this.selectedUserRoles.add(ur);

				List<SystemClassif> parList = null;

				try {
					parList = findAllParents(codeClassif, ur.getCodeRole(), new Date());
				} catch (DbErrorException e) {
					// e.printStackTrace();
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
							"Грешка при търсене на parent нива за роли - " + e.getLocalizedMessage());
					return;
				}
				if (parList != null && !parList.isEmpty()) {
					for (SystemClassif cl : parList) {
						if (!rolsMap.containsKey(cl.getCode())) {
							AdmUserRoles ur1 = new AdmUserRoles();
							ur1.setCodeClassif(codeClassif);
							ur1.setCodeRole(cl.getCode());
							dopParRols.add(ur1);
							rolsMap.put(cl.getCode(), Boolean.TRUE);
						}
					}
				}

			}
			// Допълване с Parent нивата, които не са зададени за всяка роля
			// Ролите се добавят в списъка, но при връщане от дървото ако не са чекнати
			// горни нива те пак не се връщат с roles ?? - от начина на обработка на дърво
			if (!dopParRols.isEmpty()) {
				for (AdmUserRoles aur : dopParRols)
					this.selectedUserRoles.add(aur); // Допълване с parent ролите за класификацията

			}

		}

	}

	/**
	 * Основен метод за формиране на ролите в this.user(userRoles) преди запис
	 */
	public void setAllRolesInUser() {

		if (this.user.getUserRoles().size() > 0) {
			if (this.selectedUserRoles == null || this.selectedUserRoles.size() == 0) {
				for (AdmUserRoles item : this.user.getUserRoles()) {
					this.user.getUserRoles().remove(item);
				}
			} else {

				// Първо изтриване старите липсващи роли
				int j = 0;
				while (j < this.user.getUserRoles().size()) {
					AdmUserRoles itemUs = this.user.getUserRoles().get(j);

					Iterator<AdmUserRoles> it = this.selectedUserRoles.iterator();
					boolean isItem = false;

					while (it.hasNext()) {
						AdmUserRoles roleTmp = it.next();

						if (roleTmp.getCodeClassif().equals(itemUs.getCodeClassif())
								&& roleTmp.getCodeRole().equals(itemUs.getCodeRole())) {
							isItem = true;
							break;

						}
					}

					if (!isItem) { // В новата съвкупност от роли тази роля липсва
						this.user.getUserRoles().remove(j);
						continue;
					}

					j++;
				}

				// Добавяне на новите роли
				for (AdmUserRoles itemSel : this.selectedUserRoles) {

					Iterator<AdmUserRoles> it = this.user.getUserRoles().iterator();
					boolean isItem = false;

					while (it.hasNext()) {
						AdmUserRoles roleTmp = it.next();

						if (roleTmp.getCodeClassif().equals(itemSel.getCodeClassif())
								&& roleTmp.getCodeRole().equals(itemSel.getCodeRole())) {
							isItem = true;
							break;
						}
					}

					if (!isItem) // Добавяне на новата роля
						this.user.getUserRoles().add(itemSel);
				}

			} // Край на else

		} else { // this.user.getUserRoles().size() == 0
			if (this.selectedUserRoles != null && this.selectedUserRoles.size() > 0) {
				for (AdmUserRoles item : this.selectedUserRoles) {
					this.user.getUserRoles().add(item);
				}
			}
		}

		return;
	}

	// намиране на всички деца на конкретния код
	public List<SystemClassif> findAllChildrens(Long codeClassif, Long codeRole, Date d) throws DbErrorException {

		List<SystemClassif> childsList = new ArrayList<SystemClassif>();

		try {

			childsList = getSystemData().getChildren(codeClassif, codeRole, d, this.lang, this.idUser);

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на списъка с деца за дадена роля! ", e);
			// JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
			// getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			throw new DbErrorException(e.getMessage() + "-" + e.getLocalizedMessage());
		}

		return childsList;
	}

	// намиране на всички родители на конкретния код
	public List<SystemClassif> findAllParents(Long codeClassif, Long codeRole, Date d) throws DbErrorException {

		List<SystemClassif> parentsList = new ArrayList<SystemClassif>();

		try {

			parentsList = getSystemData().getParents(codeClassif, codeRole, d, this.lang, this.idUser);

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на списъка с родителите! ", e);
			// JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
			// getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			throw new DbErrorException(e.getMessage() + "-" + e.getLocalizedMessage());
		}

		return parentsList;
	}

	// ***************************************************************************************************************
	// Стари варианти на единичен избор при select и unselect на node от дървото
	/**
	 * 
	 * @param event
	 */
	public void actionNodeSelect(NodeSelectEvent event) {
		try {
			AdmUserRoles usRoles = new AdmUserRoles();
			if (event.getTreeNode() == null || event.getTreeNode().getData() == null)
				return;

			usRoles.setCodeClassif(((SystemClassif) event.getTreeNode().getData()).getCodeClassif());
			usRoles.setCodeRole(((SystemClassif) event.getTreeNode().getData()).getCode());

			this.user.getUserRoles().add(usRoles);
//				System.out.println("onNodeSelect <<< " + usRoles.getCodeClassif() + " >>> " + usRoles.getCodeRole());

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errSelCodeFromClassif") + "-"
							+ e.getLocalizedMessage());
		}
	}

	public void actionNodeUnselect() {

		try {
			Iterator<AdmUserRoles> it = this.user.getUserRoles().iterator();

			while (it.hasNext()) {
				AdmUserRoles roleTmp = it.next();

				if (roleTmp.getCodeClassif().equals(Long.valueOf(this.selClassif))) {

					if (isContans(roleTmp.getCodeRole())) {
						this.user.getUserRoles().remove(roleTmp);
						break;
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "users.errDselCodeFromClassif") + "-"
							+ e.getLocalizedMessage());
		}
	}

	private boolean isContans(Long code) {

		for (TreeNode selNode : this.selectedNode) {

			Long codeClassif = ((SystemClassif) selNode.getData()).getCodeClassif();
			Long codeRole = ((SystemClassif) selNode.getData()).getCode();

//				System.out.println("onNodeUNSelect <<< " + codeClassif + " >>> " + codeRole);

			if (Long.valueOf(this.selClassif).equals(Long.valueOf(codeClassif))) {
				if (code.equals(Long.valueOf(codeRole))) {
					return false;
				}
			}
		}

		return true;
	}
	// ************************************************************************************************************

	// **************************************************************************************************

	public Long getClassifTip() {
		return Long.valueOf(Constants.CODE_CLASSIF_TIP_POTREB);
		// return this.codeClassifUserType;

	}

	public Long getTipVanshen() {
		return Long.valueOf(Constants.CODE_ZNACHENIE_TIP_POTR_VANSHEN);
	}

	public Long getClassifLang() {
		return Long.valueOf(Constants.CODE_CLASSIF_LANG);
		// return this.codeClassifLang;
	}

	public Long getClassifAdmStr() {
		return Long.valueOf(Constants.CODE_CLASSIF_ADMIN_STR);
	}

	public Long getClassifDANE() {
		return Long.valueOf(Constants.CODE_CLASSIF_DANE);
	}

	public Long getClassifStat() {
		// return Long.valueOf(Constants.CODE_CLASSIF_STATUS_POTREB);
		return this.codeClassifStatus;
	}

	public Long getClassifDlajn() {
		// return Long.valueOf(Constants. CODE_CLASSIF_DLAJNOSTI);
		return this.codeClassifPosition;
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

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public Long getIndRowIzb() {
		return indRowIzb;
	}

	public void setIndRowIzb(Long indRowIzb) {
		this.indRowIzb = indRowIzb;
	}

	public UserData getUd() {
		return ud;
	}

	public void setUd(UserData ud) {
		this.ud = ud;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Map<String, Boolean> getSysNames() throws DbErrorException {

		return sysNames;
	}

	public void setSysNames(Map<String, Boolean> sysNames) {
		this.sysNames = sysNames;
	}

	public AdmUsers getUser() {
		if ((this.param == 1 || this.param == 2) && (this.user == null || this.user.getId() == null)
				&& this.userId != null) {
			// Първо трябва да се прочетат данните за потребителя, който е избан
			getUserInp(this.userId);
		}

		return this.user;
	}

	public void setUser(AdmUsers user) {
		this.user = user;
	}

	public List<SelectItem> getLstGroupsIzb() throws DbErrorException {
		if (lstGroupsIzb == null || lstGroupsIzb.isEmpty()) {
			// Прочитане данни за активните групи потребители за избор на групи
			try {
				lstGroupsIzb = this.usersDAO.getAllActGroupsIzb();
			} catch (DbErrorException e) {
				throw new DbErrorException(e.getMessage());
			}
		}

		return lstGroupsIzb;
	}

	public void setLstGroupsIzb(List<SelectItem> lstGroupsIzb) {
		this.lstGroupsIzb = lstGroupsIzb;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public int getParam() {
		return param;
	}

	public void setParam(int param) {
		this.param = param;
	}

	public boolean isZapis() {
		return isZapis;
	}

	public void setZapis(boolean isZapis) {
		this.isZapis = isZapis;
	}

	public String getMailServAddress() {
		return mailServAddress;
	}

	public void setMailServAddress(String mailServAddress) {
		this.mailServAddress = mailServAddress;
	}

	public String getMailServAddress2() {
		return mailServAddress2;
	}

	public void setMailServAddress2(String mailServAddress2) {
		this.mailServAddress2 = mailServAddress2;
	}

	public String getMailUser() {
		return mailUser;
	}

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public String getMailPass() {
		return mailPass;
	}

	public void setMailPass(String mailPass) {
		this.mailPass = mailPass;
	}

	public String getMailEMail() {
		return mailEMail;
	}

	public void setMailEMail(String mailEMail) {
		this.mailEMail = mailEMail;
	}

	public Long getMailForw() {
		return mailForw;
	}

	public void setMailForw(Long mailForw) {
		this.mailForw = mailForw;
	}

	public String getPassInp() {
		return passInp;
	}

	public void setPassInp(String passInp) {
		this.passInp = passInp;
	}

	public String getPassPovt() {
		return passPovt;
	}

	public void setPassPovt(String passPovt) {
		this.passPovt = passPovt;
	}

	public boolean isViewChangePass() {
		return viewChangePass;
	}

	public void setViewChangePass(boolean viewChangePass) {
		this.viewChangePass = viewChangePass;
	}

	public boolean isViewPass() {
		return viewPass;
	}

	public void setViewPass(boolean viewPass) {
		this.viewPass = viewPass;
	}

	public Long getAdmZv() {
		return admZv;
	}

	public void setAdmZv(Long admZv) {
		this.admZv = admZv;
	}

	public String getAdmZvText() {
		return admZvText;
	}

	public void setAdmZvText(String admZvText) {
		this.admZvText = admZvText;
	}

	public Long getPosDlajn() {
		return posDlajn;
	}

	public void setPosDlajn(Long posDlajn) {
		this.posDlajn = posDlajn;
	}

	public String getPosDlajnText() {
		return posDlajnText;
	}

	public void setPosDlajnText(String posDlajnText) {
		this.posDlajnText = posDlajnText;
	}

	public String getUserTypeText() {
		return userTypeText;
	}

	public void setUserTypeText(String userTypeText) {
		this.userTypeText = userTypeText;
	}

	public String getUserLangText() {
		return userLangText;
	}

	public void setUserLangText(String userLangText) {
		this.userLangText = userLangText;
	}

	public String getUserStatusText() {
		return userStatusText;
	}

	public void setUserStatusText(String userStatusText) {
		this.userStatusText = userStatusText;
	}

	public LazyDataModelSQL2Array getOrgList() {
		return orgList;
	}

	public void setOrgList(LazyDataModelSQL2Array orgList) {
		this.orgList = orgList;
	}

	public String getEikInp() {
		return eikInp;
	}

	public void setEikInp(String eikInp) {
		this.eikInp = eikInp;
	}

	public String getnOrgInp() {
		return nOrgInp;
	}

	public void setnOrgInp(String nOrgInp) {
		this.nOrgInp = nOrgInp;
	}

	public ArrayList<String> getSelectedGrList() {
		return selectedGrList;
	}

	public void setSelectedGrList(ArrayList<String> selectedGrList) {
		this.selectedGrList = selectedGrList;
	}

//		public List <SelectItem> getClDANE() {
//			
//			return clDANE;
//		}
//
//		public void setClDANE(List <SelectItem> clDANE) {
//			this.clDANE = clDANE;
//		}

	public List<String[]> getSysClassifList() {
		return sysClassifList;
	}

	public void setSysClassifList(List<String[]> sysClassifList) {
		this.sysClassifList = sysClassifList;
	}

	public TreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public TreeNode[] getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode[] selectedNode) {
		this.selectedNode = selectedNode;
	}

	public Boolean getOtOrgEkran() {
		return otOrgEkran;
	}

	public void setOtOrgEkran(Boolean otOrgEkran) {
		UserEdit.otOrgEkran = otOrgEkran;
	}

	public static void setOtOrgEkanStatic(Boolean otOrgEkran) {
		UserEdit.otOrgEkran = otOrgEkran;
	}

	public Long getIdOrg() {
		return idOrg;
	}

	public void setIdOrg(Long idOrg) {
		UserEdit.idOrg = idOrg;
	}

	public static void setIdOrgStatic(Long idOrg) {
		UserEdit.idOrg = idOrg;
	}

	public String getNameOrg() {
		return nameOrg;
	}

	public void setNameOrg(String nameOrg) {
		UserEdit.nameOrg = nameOrg;
	}

	public static void setNameOrgStatic(String nameOrg) {
		UserEdit.nameOrg = nameOrg;
	}

	public List<AdmUserRoles> getSelectedUserRoles() {
		return selectedUserRoles;
	}

	public void setSelectedUserRoles(List<AdmUserRoles> selectedUserRoles) {
		this.selectedUserRoles = selectedUserRoles;
	}

	public boolean isViewPassNastr() {
		return viewPassNastr;
	}

	public void setViewPassNastr(boolean viewPassNastr) {
		this.viewPassNastr = viewPassNastr;
	}

	public int getNavSize() {
		return navSize;
	}

	public void setNavSize(int navSize) {
		this.navSize = navSize;
	}

	public String getNastrMailForwText() {
		return nastrMailForwText;
	}

	public void setNastrMailForwText(String nastrMailForwText) {
		this.nastrMailForwText = nastrMailForwText;
	}

	private void sendMail(String subject, String content, String nameModel) {
		if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
			Thread localMailThread = new Thread(() -> {
				try {
					String confFile = "indexbg.mail3.properties";
					ArrayList<String> toMails = new ArrayList<>();
					toMails.add(user.getEmail());
					Properties mailProps = Mailer3.loadProps(confFile);
					Mailer3 mailer3 = new Mailer3();
					mailer3.sent(Mailer3.Content.HTML, mailProps, mailProps.getProperty("user.name"),
							mailProps.getProperty("user.password"), mailProps.getProperty("mail.from"), nameModel,
							toMails, subject, content, new ArrayList<DataSource>());
				} catch (Exception e) {
					LOGGER.error("Exception when sending mail: " + e.getMessage());
				}
			});
			localMailThread.start();
		}
	}

	@SuppressWarnings("unused")
	private void sendMailToNameMail(String subject, String content, String nameModel, String nameMail) {
		if (nameMail != null && !nameMail.trim().isEmpty()) {
			Thread localMailThread = new Thread(() -> {
				try {
					String confFile = "indexbg.mail3.properties";
					ArrayList<String> toMails = new ArrayList<>();
					toMails.add(nameMail.trim());
					Properties mailProps = Mailer3.loadProps(confFile);
					Mailer3 mailer3 = new Mailer3();
					mailer3.sent(Mailer3.Content.HTML, mailProps, mailProps.getProperty("user.name"),
							mailProps.getProperty("user.password"), mailProps.getProperty("mail.from"), nameModel,
							toMails, subject, content, new ArrayList<DataSource>());
				} catch (Exception e) {
					LOGGER.error("Exception when sending mail: " + e.getMessage());
				}
			});
			localMailThread.start();
		}
	}

	/**
	 * Избор на файлове за прикачване към събитието
	 * 
	 * @param event
	 */
	public void uploadFileListener(FileUploadEvent event) {

		try {

			UploadedFile upFile = event.getFile();

			Files fileObject = new Files();
			fileObject.setFilename(upFile.getFileName());
			fileObject.setContentType(upFile.getContentType());
			fileObject.setContent(upFile.getContent());

			this.filesList.add(fileObject);

		} catch (Exception e) {
			LOGGER.error("Exception: " + e.getMessage());
		}
	}

	/**
	 * Метод за изтегляне на файла от базата данни на системата.
	 * 
	 * @param file - байтовете на самият файл.
	 */

	public void download(Files file) {

		boolean ok = true;

		if (file.getContent() == null && file.getId() != null) {

			try {

				FilesDAO filesDAO = new FilesDAO(this.idUser);
				file = filesDAO.findById(file.getId());

				if (file.getPath() != null && !file.getPath().isEmpty()) {
					Path path = Paths.get(file.getPath());
					file.setContent(java.nio.file.Files.readAllBytes(path));
				}

			} catch (DbErrorException e) {
				LOGGER.error("DbErrorException: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));

			} catch (IOException e) {
				LOGGER.error("IOException: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);

			} catch (Exception e) {
				LOGGER.error("Exception: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.exception"));

			} finally {
				JPA.getUtil().closeConnection();
			}
		}

		if (ok) {

			try {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();

				HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
				String agent = request.getHeader("user-agent");

				String codedfilename = "";

				if (null != agent && (-1 != agent.indexOf("MSIE")
						|| (-1 != agent.indexOf("Mozilla") && -1 != agent.indexOf("rv:11"))
						|| (-1 != agent.indexOf("Edge")))) {
					codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
				} else if (null != agent && -1 != agent.indexOf("Mozilla")) {
					codedfilename = MimeUtility.encodeText(file.getFilename(), "UTF8", "B");
				} else {
					codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
				}

				externalContext.setResponseHeader("Content-Type", "application/x-download");
				externalContext.setResponseHeader("Content-Length", file.getContent().length + "");
				externalContext.setResponseHeader("Content-Disposition",
						"attachment;filename=\"" + codedfilename + "\"");
				externalContext.getResponseOutputStream().write(file.getContent());

				facesContext.responseComplete();

			} catch (IOException e) {
				LOGGER.error("IOException: " + e.getMessage());
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);

			} catch (Exception e) {
				LOGGER.error("Exception: " + e.getMessage());
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						getMessageResourceString("beanMessages", "general.exception"));
			}
		}

	}

	/**
	 * Премахва избрания файл
	 * 
	 * @param file
	 */
	public void remove(Files file) {

		this.filesList.remove(file);
		this.deleteFilesList.add(file);
	}

	/**
	 * Методът проверява за недопустими символи в едно поле по подаден
	 * шаблон-матрица ( Напремер: №\\\",.;:'%(){}[]^=<>|/`~ ) в стрингово поле.
	 * 
	 * @param pattern - подаден шаблон-матрица - Съдържа всички валидни символи
	 * @param pole    - стрингово поле, всички символи от което трябва да се намерят
	 *                в pattern
	 * @return false - ако намери недопустими символи по подаден шаблон-матрица
	 * 
	 */
	public boolean isValidAllSimboli(String pattern, String pole) {
		if (pole == null)
			return false;
		pole = pole.trim();
		if (pole.isEmpty())
			return false;
		if (pattern.isEmpty())
			return true;

		// Проверка за недопустими символи в pole - символи, които липсват в подадения
		// pattern
		for (int i = 0; i < pole.length(); i++) {
			char c = pole.charAt(i);
			if (pattern.indexOf(c) < 0) { // Намерен есимвол, който не се съдържа в Pattern
				return false;
			}
		}
		return true;
	}

	public Long getCodeClassifWorkAdm() {
		return codeClassifWorkAdm;
	}

	public void setCodeClassifWorkAdm(Long codeClassifWorkAdm) {
		this.codeClassifWorkAdm = codeClassifWorkAdm;
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

	public Long getCodeClassifPosition() {
		return codeClassifPosition;
	}

	public void setCodeClassifPosition(Long codeClassifPosition) {
		this.codeClassifPosition = codeClassifPosition;
	}

	public Long getCodeClassifLang() {
		return codeClassifLang;
	}

	public void setCodeClassifLang(Long codeClassifLang) {
		this.codeClassifLang = codeClassifLang;
	}

	public boolean isZadStatExpl() {
		return zadStatExpl;
	}

	public void setZadStatExpl(boolean zadStatExpl) {
		this.zadStatExpl = zadStatExpl;
	}

	public boolean isZadWAdmCode() {
		return zadWAdmCode;
	}

	public void setZadWAdmCode(boolean zadWAdmCode) {
		this.zadWAdmCode = zadWAdmCode;
	}

	public boolean isZadWOrg() {
		return zadWOrg;
	}

	public void setZadWOrg(boolean zadWOrg) {
		this.zadWOrg = zadWOrg;
	}

	public boolean isZadWPlace() {
		return zadWPlace;
	}

	public void setZadWPlace(boolean zadWPlace) {
		this.zadWPlace = zadWPlace;
	}

	public boolean isZadPos() {
		return zadPos;
	}

	public void setZadPos(boolean zadPos) {
		this.zadPos = zadPos;
	}

	public boolean isZadPosText() {
		return zadPosText;
	}

	public void setZadPosText(boolean zadPosText) {
		this.zadPosText = zadPosText;
	}

	public boolean isZadEmail() {
		return zadEmail;
	}

	public void setZadEmail(boolean zadEmail) {
		this.zadEmail = zadEmail;
	}

	public boolean isZadPhone() {
		return zadPhone;
	}

	public void setZadPhone(boolean zadPhone) {
		this.zadPhone = zadPhone;
	}

	public boolean isZadLang() {
		return zadLang;
	}

	public void setZadLang(boolean zadLang) {
		this.zadLang = zadLang;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public Files getFile() {
		return file;
	}

	public void setFile(Files file) {
		this.file = file;
	}

	public List<Files> getDeleteFilesList() {
		return deleteFilesList;
	}

	public void setDeleteFilesList(List<Files> deleteFilesList) {
		this.deleteFilesList = deleteFilesList;
	}

	public String getOcenkaPass() {
		return ocenkaPass;
	}

	public void setOcenkaPass(String ocenkaPass) {
		this.ocenkaPass = ocenkaPass;
	}

	public List<AdmGroups> getLstGroups() {
		return lstGroups;
	}

	public void setLstGroups(List<AdmGroups> lstGroups) {
		this.lstGroups = lstGroups;
	}

	public CheckboxTreeNode getRootNodeMenu() {
		return rootNodeMenu;
	}

	public void setRootNodeMenu(CheckboxTreeNode rootNodeMenu) {
		this.rootNodeMenu = rootNodeMenu;
	}

	public Map<Long, Boolean> getTreeMenuHash() {
		return treeMenuHash;
	}

	public void setTreeMenuHash(Map<Long, Boolean> treeMenuHash) {
		this.treeMenuHash = treeMenuHash;
	}

	public List<Long> getSelectedRolesClassif() {
		return selectedRolesClassif;
	}

	public void setSelectedRolesClassif(List<Long> selectedRolesClassif) {
		this.selectedRolesClassif = selectedRolesClassif;
	}

	public Long getAdmZveno() {
		return admZveno;
	}

	public void setAdmZveno(Long admZveno) {
		this.admZveno = admZveno;
	}

	public String getAdmZvenoText() {
		return admZvenoText;
	}

	public void setAdmZvenoText(String admZvenoText) {
		this.admZvenoText = admZvenoText;
	}

}