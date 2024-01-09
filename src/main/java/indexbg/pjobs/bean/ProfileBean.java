package indexbg.pjobs.bean;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.UserUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.AdmUser;
import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.UserAdd;
import indexbg.pjobs.db.UserEducation;
import indexbg.pjobs.db.UserExperience;
import indexbg.pjobs.db.UserLanguage;
import indexbg.pjobs.db.UserStudent;
import indexbg.pjobs.db.UsersBursar;
import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.UsersBursarDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("profileBean")
@ViewScoped
public class ProfileBean extends PJobsBean {
	
	private static final long serialVersionUID = 7962674733408463328L;

	static final Logger LOGGER = LoggerFactory.getLogger(ProfileBean.class);
	
	private Long userId;
	private AdmUser user;
	private UsersBursar userBursar;
	private AdmUserDAO userDao; 
	private FilesDAO filesDao;
	private UserEducation newEducation = new UserEducation();
	private UserExperience newExperience = new UserExperience();
	private UserLanguage newLanguage = new UserLanguage();
	private String studentSubjectText;
	private String newEducationText;
	private String newExperiencePositionText;
	private boolean eduChanged = false;
	private boolean expChanged = false;
	private boolean lanChanged = false;
	//private boolean mailChanged = false;
	private String[] password = new String[3];
	
	private List<SystemClassif> regionList = new ArrayList<SystemClassif>();
	private List<SystemClassif> municipalityList = new ArrayList<SystemClassif>();
	private List<SystemClassif> townList = new ArrayList<SystemClassif>();
	private List<SystemClassif> educationCategoriesList = new ArrayList<SystemClassif>();
	private List<SystemClassif> educationSubjectList = new ArrayList<SystemClassif>();
	
	private List<Files> filesList = new ArrayList<Files>();
	private List<Long> filesToDelete = new ArrayList<Long>();
	
	private Long studentSubject;
	
	private Long   studentUni;
	private String studentUniText;
	
	
	
	@PostConstruct
	public void initData() {
		
		LOGGER.debug("PostConstruct!");
		
		try {
			this.userId = getUserData().getUserId();
			this.userDao = new AdmUserDAO(this.userId);
			this.filesDao = new FilesDAO(this.userId);
			this.user = userDao.findById(this.userId);
			this.userBursar = (new UsersBursarDAO(this.userId)).findByIdUser(userId);
			this.filesList = filesDao.findByCodeObjAndIdObj(Constants.CODE_OBJECTS_USERS, this.userId);
			
			// инициализиране на lazy колекции
			user.getUserEducation().size();
			user.getUserExperience().size();
			user.getUserLanguages().size();
						
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;
		
		} catch (DbErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		
		} finally {
			JPA.getUtil().closeConnection();
		}

		// ако нямаме досега въведени данни, сега създаваме
		if(this.user.getUserAdd() == null) {
			this.user.setUserAdd(new UserAdd());
			this.user.getUserAdd().setMobilityProblems(false);
			this.user.getUserAdd().setHearingProblems(false);
			this.user.getUserAdd().setSearchAgreement(false);
		}
		
		// задаване на имената тук, в случай, че обектът е създаден вече, но не са попълнени
		if(this.user.getUserAdd().getName() == null || this.user.getUserAdd().getName().trim().equals("")) {
			setUserNames();
		}
		
		// зареждане на направленията на образование
		loadCategories();
		
		// задаване на адреса
		fillSysClassifList(regionList, 0L, Constants.CODE_SYSCLASS_EKATTE, true);
		actionChangedRegion();
		actionChangedMunicipality();
		
		// задаване на данни за студент - ако е зададен като кандидат за Стипендиант, но няма създадени данни, тук му се създават
		if(this.user.getUserAdd().getApplyFor() != null && this.user.getUserAdd().getApplyFor() == Constants.CODE_ZNACH_CANDIDATE_BURSARY) {
			if(this.user.getUserStudent() == null) {
				user.setUserStudent(new UserStudent());
			}
		}
		
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
	
	public void loadCategories() {
		List<SystemClassif> mainLevel = new ArrayList<SystemClassif>();
		fillSysClassifList(mainLevel, 0L, Constants.CODE_SYSCLASS_SUBJECT, false);
		
		for(SystemClassif s : mainLevel) {
			fillSysClassifList(this.educationCategoriesList, s.getCode(), Constants.CODE_SYSCLASS_SUBJECT, false);
		}
		
		this.educationCategoriesList.sort((a, b) -> a.getTekst().compareTo(b.getTekst()));
	}
	
	public void setUserNames() {
		String[] names = this.user.getNames().split(" ");
		if(names.length == 1) {
			this.user.getUserAdd().setName(names[0].trim());
		}
		else if(names.length == 2) {
			this.user.getUserAdd().setName(names[0].trim());
			this.user.getUserAdd().setFamily(names[1].trim());
		} 
		else if(names.length == 3) {
			this.user.getUserAdd().setName(names[0].trim());
			this.user.getUserAdd().setSurname(names[1].trim());
			this.user.getUserAdd().setFamily(names[2].trim());
		}
		else {
			this.user.getUserAdd().setName(this.user.getNames().trim());
		}
	}
	
	public void actionChangedRegion() {
		fillSysClassifList(municipalityList, this.user.getUserAdd().getRegion(), Constants.CODE_SYSCLASS_EKATTE, true);
		this.townList = new ArrayList<SystemClassif>();
		
	}
	
	public void actionChangedMunicipality() {
		fillSysClassifList(townList, this.user.getUserAdd().getMunicipality(), Constants.CODE_SYSCLASS_EKATTE, true);
	}
	
	public void actionChangedApplyFor() {
		if(this.user.getUserAdd().getApplyFor() != null && this.user.getUserAdd().getApplyFor() == Constants.CODE_ZNACH_CANDIDATE_BURSARY) {
			if(this.user.getUserStudent() == null) {
				this.user.setUserStudent(new UserStudent());
				// TODO set status and date?
				this.user.getUserStudent().setStudentStatus(-1L);
				this.user.getUserStudent().setStudentStatusDate(new Date());
				studentSubject = null;
			}
		}
	}
	
	public void actionChangedCategory() {
		fillSysClassifList(this.educationSubjectList, this.newEducation.getCategory(), Constants.CODE_SYSCLASS_SUBJECT, true);
	}
	
	public void actionSubmitFirstTab() {		
		if(validateFirstTab()) {
			try {
				// TODO candidate status?
				if(this.user.getUserAdd().getCandidateStatus() == null) {
					this.user.getUserAdd().setCandidateStatus(-1L);
					this.user.getUserAdd().setCandidateStatusDate(new Date());
				}
				
				JPA.getUtil().begin();
				
				boolean newPin = false;
				if(this.user.getUserAdd().getPin() == null) {
					this.user.getUserAdd().setPin(this.userDao.generatePin());	
					newPin = true;
				}
				
				// ако е маркирал, че е стипендиант, но си го смени на друго, изтрива обекта 
				if(this.user.getUserAdd().getApplyFor() != Constants.CODE_ZNACH_CANDIDATE_BURSARY) {
					this.user.setUserStudent(null);
					studentSubject = null;
				}
				
				// конкатенира трите имена и ги записва в ADM_USERS в колона NAMES
				String names = this.user.getUserAdd().getName().trim() + " " + this.user.getUserAdd().getSurname().trim() + " " + this.user.getUserAdd().getFamily().trim();
				names.replace("  ", " ");
				this.user.getUserAdd().setName(this.user.getUserAdd().getName().trim());
				this.user.getUserAdd().setSurname(this.user.getUserAdd().getSurname().trim());
				this.user.getUserAdd().setFamily(this.user.getUserAdd().getFamily().trim());
				
				AdmUser userPersist = this.userDao.findById(this.userId);
				userPersist.setUserAdd(user.getUserAdd());
				userPersist.setUserStudent(user.getUserStudent());
				userPersist.setNames(names);
				userPersist.setEmail(user.getEmail());
				userPersist.setPhone(user.getPhone());
				this.userDao.save(userPersist);
				JPA.getUtil().commit();
				
				
				getUserData().setApplyFor(user.getUserAdd().getApplyFor()); //за да поддържаме актуално състояние
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
				if(newPin) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.newPin") + " " + user.getUserAdd().getPin());
				}
				
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(), e);
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(), e);
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
		
	}
	
	public void actionSubmitSecondTab() {		
		
		try {
			JPA.getUtil().begin();
			AdmUser userPersist = this.userDao.findById(this.userId);
			
			if(eduChanged) {
				userPersist.getUserEducation().clear();
				for(UserEducation e : this.user.getUserEducation()) {
					userPersist.addEducation(e);
				}
			}
			
			if(expChanged) {
				userPersist.getUserExperience().clear();
				for(UserExperience e : this.user.getUserExperience()) {
					userPersist.addUserExperience(e);
				}
			}
			
			if(lanChanged) {
				userPersist.getUserLanguages().clear();
				for(UserLanguage l : this.user.getUserLanguages()) {
					userPersist.addLanguage(l);
				}
			}
			
			this.userDao.save(userPersist);

			// remove deleted files
			for(Long id : this.filesToDelete) {
				if(id != null) {
					this.filesDao.deleteById(id);
				}
			}
			this.filesToDelete.clear();			
			
			// save new files
			for(Files f : this.filesList) {
				if(f.getId() == null) {
					this.filesDao.save(f);
				}
			}
			
			JPA.getUtil().commit();
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
			
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error(e.getMessage(), e);
		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error(e.getMessage(), e);
		} finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	
	public void actionChangePassword() {

		if(validatePasswords() && validateNewPassword()) {
			String passOld = Base64.getEncoder().encodeToString(this.password[0].getBytes());
			String passNew = Base64.getEncoder().encodeToString(this.password[1].getBytes());
			
			try {
				JPA.getUtil().begin();
				boolean success = this.userDao.changePassword(this.userId, passOld, passNew);
				if(success) {
					JPA.getUtil().commit();
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages","pass.changed"));
				} else {
					JSFUtils.addMessage("formProfile:pass",FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","pass.wrong"));
				}
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(), e);
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(), e);
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
		
	}
	
	private boolean validateFirstTab() {
		
		boolean valid = true;
		
		if(this.user.getUserAdd().getName() == null ||this.user.getUserAdd().getName().trim().equals("")) { // ИМЕ
			JSFUtils.addMessage("formProfile:name",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.fname")));
			valid = false;
		}
		
		if(this.user.getUserAdd().getFamily() == null ||this.user.getUserAdd().getFamily().trim().equals("")) { // ФАМИЛИЯ
			JSFUtils.addMessage("formProfile:family",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.lname")));
			valid = false;
		}
		
		if(this.user.getUserAdd().getApplyFor() == null) { // КАНДИДАТ ЗА
			JSFUtils.addMessage("formProfile:applyFor",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "profile.candidateFor")));
			valid = false;
		}
		
		if(this.user.getEmail() == null || this.user.getEmail().equals("")) { // ИМЕЙЛ
			JSFUtils.addMessage("formProfile:email",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.email")));
			valid = false;
		}
		else if(!ValidationUtils.isEmailValid(this.user.getEmail())) { // НЕВАЛИДЕН ИМЕЙЛ 
			JSFUtils.addMessage("formProfile:email",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidEmail",getMessageResourceString("labels", "general.email")));
			valid = false;
		}
		/*
		else if(this.mailChanged && emailIsUsed(this.user.getEmail())) { // ВЕЧЕ ИЗПОЛЗВАН ИМЕЙЛ
			JSFUtils.addMessage("formProfile:email",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.usedEmail",getMessageResourceString("labels", "general.email")));
			valid = false;
		}
		*/
		else if(this.user.getEmail() != null && this.user.getEmail().length() > 100) { // ИМЕЙЛ - ДЪЛЖИНА
			JSFUtils.addMessage("formProfile:email",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("beanMessages", "email.long")));
			valid = false;
		}
		
		if(this.user.getUserAdd().getConditionsAgreement() == false) { // ПРИЕМА УСЛОВИЯТА ЗА ПОЛЗВАНЕ
			JSFUtils.addMessage("formProfile:agreeConditions", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "profile.conditionsAgree"));
			valid = false;
		}
		
		// ако потребителят е кандидат за стипендия, валидира и тези полета
		if(this.user.getUserAdd().getApplyFor() != null && this.user.getUserAdd().getApplyFor() == Constants.CODE_ZNACH_CANDIDATE_BURSARY) {
			
			if(this.user.getUserStudent().getEducationDegree() == null) { // ОКС
				JSFUtils.addMessage("formProfile:studentDegree",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "profile.studentDegree")));
				valid = false;
			}
			
			if(this.user.getUserStudent().getCourse() == null) { // ЗАВЪРШЕН КУРС
				JSFUtils.addMessage("formProfile:studentCourse",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "profile.studentCourse")));
				valid = false;
			}
			
			if(this.user.getUserStudent().getSubject() == null) { // СПЕЦИАЛНОСТ
				JSFUtils.addMessage("formProfile:studentSubject",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.subject")));
				valid = false;
			}
			if(this.user.getUserStudent().getUniversity() == null) { // Uniwersitet
				JSFUtils.addMessage("formProfile:studentUniversity",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.university")));
				valid = false;
			}
		}
		
		// ако потребителят е ободрен за явяване на тест, валидира и тези полета
		if(this.user.getUserAdd().getCandidateStatus() != null && this.user.getUserAdd().getCandidateStatus() == Constants.CODE_ZNACH_CANDIDATE_APPROVED) {
			
			if(this.user.getUserAdd().getMobilityProblems() == null) { // ДВИГАТЕЛНИ ПРОБЛЕМИ
				JSFUtils.addMessage("formProfile:probMove",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "profile.motorProblems")));
				valid = false;
			}
			
			if(this.user.getUserAdd().getHearingProblems() == null) { // СЛУХОВИ ПРОБЛЕМИ
				JSFUtils.addMessage("formProfile:probHear",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "profile.hearProblems")));
				valid = false;
			}
			
		}
		
		return valid;
	}
	
	/**
	 * Проверява дали полетата са попълнени и дали двете нови пароли си съвпадат 
	 * @return true, ако всичко е валидно
	 */
	private boolean validatePasswords() {
		
		boolean valid = true;
		
		if(this.password[0] == null ||this.password[0].trim().equals("")) { // ПАРОЛА
			JSFUtils.addMessage("formProfile:pass",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.pass")));
			valid = false;
		}
		
		if(this.password[1] == null ||this.password[1].trim().equals("")) { // НОВА ПАРОЛА
			JSFUtils.addMessage("formProfile:pass-new",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.passNew")));
			valid = false;
		}
		
		if(this.password[2] == null ||this.password[2].trim().equals("")) { // НОВА ПАРОЛА 2
			JSFUtils.addMessage("formProfile:pass-new2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.pass1")));
			valid = false;
		}
		
		if(this.password[1] != null && this.password[2] != null && !this.password[1].equals(this.password[2])) { // ПАРОЛИТЕ ДА СЪВПАДАТ
			JSFUtils.addMessage("formProfile:pass-new2",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","pass.noMatch"));
			valid = false;
		}
		
		return valid;
	}

	/**
	 * Проверява дали новата парола отговаря на критериите за парола от RegistrationService.register в AAServer
	 * 	<ul>
	 * 		<li>да е поне 6 символа</li>
	 * 		<li>да не съдържа №\\\",.;:'%(){}[]^=<>|/`~ и шпации</li>
	 *  </ul>
	 * @return true, ако паролата е валидна
	 */
	private boolean validateNewPassword() {

		boolean valid = true;
		
		if(this.password[1].length() < 6) { // паролата е под 6 символа
			JSFUtils.addMessage("formProfile:pass-new",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","pass.short"));
			valid = false;
		}
		if(this.password[1].length() > 50) { // паролата е над 50 символа
			JSFUtils.addMessage("formProfile:pass-new",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","pass.long"));
			valid = false;
		}
		if(!(new UserUtils()).isValidSimboli("№\\\",.;:'%(){}[]^=<>|/`~ ", this.password[1])) {
			JSFUtils.addMessage("formProfile:pass-new",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","pass.symbols"));
			valid = false;
		}
		
		return valid;
	}
	
	/**
	 * @param email
	 * @return връща true, ако имейлът вече присъства в базата
	 */
	/*
	private boolean emailIsUsed(String email) {
		try {
			Query query = JPA.getUtil().getEntityManager().createQuery("select id from AdmUser where upper(email) = :email");
			query.setParameter("email", email.trim().toUpperCase());
			
			if(query.getResultList().size() > 0) {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	*/

	public void addEducation() {

		boolean valid = true;
		
		if(this.newEducation.getEducationDegree() == null) { // СТЕПЕН
			JSFUtils.addMessage("formProfile:newEducationDegree",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("labels", "profile.eduDegree")));
			valid = false;
		}
		if(this.newEducation.getCategory() == null) { // НАПРАВЛЕНИЕ
			JSFUtils.addMessage("formProfile:newEducationCategory",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("labels", "advertisement.profNapr")));
			valid = false;
		}
		if(this.newEducation.getSubject() == null) { // СПЕЦИАЛНОСТ
			JSFUtils.addMessage("formProfile:newEducationSubject",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("labels", "bursary.subject")));
			valid = false;
		}
		
		if(!valid) return;
		
		if(this.user.getUserEducation() == null) {
			this.user.setUserEducation(new ArrayList<UserEducation>());
		}
		
		this.user.addEducation(this.newEducation);
		this.eduChanged = true;
		this.newEducation = new UserEducation();
	}
	
	public void addExperience() {
		boolean valid = true;
		
		if(this.newExperience.getYears() == null) { // БРОЙ ГОДИНИ
			JSFUtils.addMessage("formProfile:newExperienceYears",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("labels", "profile.years")));
			valid = false;
		}
		if(this.newExperience.getPosition() == null && this.newExperience.getAddInfo() == null) { // ДЛЪЖНОСТ ИЛИ ОПИСАНИЕ
			if(this.newExperience.getPosition() == null) {
				JSFUtils.addMessage("formProfile:newExperiencePosition",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("labels", "advertisement.dlajnost")));
			}
			else {
				JSFUtils.addMessage("formProfile:newExperienceInfo",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("labels", "profile.descInfo")));
			}
			valid = false;
		}
		
		if(!valid) return;
		
		if(this.user.getUserExperience() == null) {
			this.user.setUserExperience(new ArrayList<UserExperience>());
		}
		
		this.user.addUserExperience(this.newExperience);
		this.expChanged = true;
		this.newExperience = new UserExperience();

	}
	
	public void addLanguage() {
		boolean valid = true;
		
		if(this.newLanguage.getLanguage() == null) { // ЕЗИК
			JSFUtils.addMessage("formProfile:newLanguageLang",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("labels", "profile.lang")));
			valid = false;
		}
		if(this.newLanguage.getLevel() == null) { // НИВО
			JSFUtils.addMessage("formProfile:newLanguageLevel",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.invalidParameter",getMessageResourceString("labels", "profile.langLevel")));
			valid = false;
		}
		
		if(!valid) return;
		
		if(this.user.getUserExperience() == null) {
			this.user.setUserExperience(new ArrayList<UserExperience>());
		}
		
		this.user.addLanguage(this.newLanguage);
		this.lanChanged = true;
		this.newLanguage = new UserLanguage();
	}
	
	public void deleteEducation(int index) {
		this.user.getUserEducation().remove(index);
		this.eduChanged = true;
	}
	
	public void deleteExperience(int index) {
		this.user.getUserExperience().remove(index);
		this.expChanged = true;
	}
	
	public void deleteLanguage(int index) {
		this.user.getUserLanguages().remove(index);
		this.lanChanged = true;
	}
		
	public void fileUpload(FileUploadEvent event) {
		
		try {
			UploadedFile upFile = event.getFile();
			if(!checkUploadedFileForDuplicate(upFile)) {
				Files fileObject = new Files();
				fileObject.setFilename(upFile.getFileName());
				fileObject.setDescription(upFile.getFileName());
				fileObject.setContentType(upFile.getContentType());
				fileObject.setContent(upFile.getContent());
				fileObject.setIdObject(this.userId);
				fileObject.setCodeObject(this.user.getCodeMainObject());
				fileObject.setLang(this.getCurrentLang());
				this.filesList.add(fileObject);
			//	System.out.println("File added");
			}
		} catch (Exception e) {
			LOGGER.error("Exception: " + e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при качване на файла!",e.getMessage());
		}
	}
	
	public boolean checkUploadedFileForDuplicate(UploadedFile attachFile) {
		if(attachFile.getFileName() == null || attachFile.getContentType() == null) 
			return false;
		
		for(Files f : this.filesList){
			if(f.getFilename().toUpperCase().equals(attachFile.getFileName().toUpperCase()) && 
				f.getContentType().toUpperCase().equals(attachFile.getContentType().toUpperCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	public void fileDownload(Files file){
		try {
			
			if(file.getContent() == null && file.getId() != null) {
			
				FilesDAO filesDAO = new FilesDAO(this.userId);
				file = filesDAO.findById(file.getId());
				
				if(file.getPath() != null && !file.getPath().isEmpty()){
					Path path = Paths.get(file.getPath());
					file.setContent(java.nio.file.Files.readAllBytes(path));
				}
			}
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
		    ExternalContext externalContext = facesContext.getExternalContext();

		    HttpServletRequest request =(HttpServletRequest)externalContext.getRequest();
			String agent = request.getHeader("user-agent");

			
			String codedfilename = ""; 
			
			if (null != agent &&  (-1 != agent.indexOf("MSIE") || (-1 != agent.indexOf("Mozilla") && -1 != agent.indexOf("rv:11")) || (-1 != agent.indexOf("Edge"))  ) ) {
				codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
			} else if (null != agent && -1 != agent.indexOf("Mozilla")) {
				codedfilename = MimeUtility.encodeText(file.getFilename(), "UTF8", "B");
			} else {
				codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
			}
			
		    externalContext.setResponseHeader("Content-Type", "application/x-download");
		    externalContext.setResponseHeader("Content-Length", file.getContent().length + "");
		    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + codedfilename + "\"");
			externalContext.getResponseOutputStream().write(file.getContent());
			
			facesContext.responseComplete();
			
		} catch (DbErrorException e) {
			LOGGER.error("DbErrorException: " + e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"),e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при сваляне на файла!: ",e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при сваляне на файла!: ",e.getMessage());
		}
	}
	
	public void deleteFile(int index) {
		this.filesToDelete.add(this.filesList.get(index).getId());
		this.filesList.remove(index);
	}
	
	/*
	public void invalidateEmail() {
		this.mailChanged = true;
	}
	*/
	
	public AdmUser getUser() {
		return user;
	}

	public void setUser(AdmUser user) {
		this.user = user;
	}

	public UsersBursar getUserBursar() {
		return userBursar;
	}

	public void setUserBursar(UsersBursar userBursar) {
		this.userBursar = userBursar;
	}

	public UserEducation getNewEducation() {
		return newEducation;
	}

	public void setNewEducation(UserEducation newEducation) {
		this.newEducation = newEducation;
	}

	public UserExperience getNewExperience() {
		return newExperience;
	}

	public void setNewExperience(UserExperience newExperience) {
		this.newExperience = newExperience;
	}

	public UserLanguage getNewLanguage() {
		return newLanguage;
	}

	public void setNewLanguage(UserLanguage newLanguage) {
		this.newLanguage = newLanguage;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<SystemClassif> getRegionList() {
		return regionList;
	}

	public void setRegionList(List<SystemClassif> regionList) {
		this.regionList = regionList;
	}

	public List<SystemClassif> getMunicipalityList() {
		return municipalityList;
	}

	public void setMunicipalityList(List<SystemClassif> municipalityList) {
		this.municipalityList = municipalityList;
	}

	public List<SystemClassif> getTownList() {
		return townList;
	}

	public void setTownList(List<SystemClassif> townList) {
		this.townList = townList;
	}

	public List<SystemClassif> getEducationCategoriesList() {
		return educationCategoriesList;
	}

	public void setEducationCategoriesList(List<SystemClassif> educationCategoriesList) {
		this.educationCategoriesList = educationCategoriesList;
	}

	public List<SystemClassif> getEducationSubjectList() {
		return educationSubjectList;
	}

	public void setEducationSubjectList(List<SystemClassif> educationSubjectList) {
		this.educationSubjectList = educationSubjectList;
	}

	public String getStudentSubjectText() {
		try {
			if(this.user.getUserStudent()!=null && this.user.getUserStudent().getSubject() != null) {
				return getSystemData().decodeItem(Constants.CODE_SYSCLASS_SUBJECT, this.user.getUserStudent().getSubject(), getCurrentLang(), new Date(), userId);
			}
			
			else {
				return null;
			}
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на специалност! ", e);
			e.printStackTrace();
		}
		return null;
	}

	public void setStudentSubjectText(String studentSubjectText) {
		this.studentSubjectText = studentSubjectText;
	}

	public String getNewEducationText() {
		try {
			if(this.newEducation.getSubject() != null) {
				return getSystemData().decodeItem(Constants.CODE_SYSCLASS_SUBJECT, this.newEducation.getSubject(), getCurrentLang(), new Date(), userId);
			} else {
				return null;
			}
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на специалност! ", e);
		}
		return null;
	}

	public void setNewEducationText(String newEducationText) {
		this.newEducationText = newEducationText;
	}

	public String getNewExperiencePositionText() {
		try {
			if(this.newExperience.getPosition() != null) {
				return getSystemData().decodeItem(Constants.CODE_SYSCLASS_JOBS, this.newExperience.getPosition(), getCurrentLang(), new Date(), userId);
			} else {
				return null;
			}
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на специалност! ", e);
		}
		return null;
	}

	public void setNewExperiencePositionText(String newExperiencePositionText) {
		this.newExperiencePositionText = newExperiencePositionText;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public String[] getPassword() {
		return password;
	}

	public void setPassword(String[] password) {
		this.password = password;
	}

	public Long getStudentSubject() {
		return studentSubject;
	}

	public void setStudentSubject(Long studentSubject) {
		
		if(user.getUserStudent()!=null) {
			
			user.getUserStudent().setSubject(studentSubject);
			
			user.getUserStudent().setEducationArea(null);
			
			if(studentSubject !=null) {
	
					
					List<SystemClassif> parentsList = new ArrayList<SystemClassif>();
	
					try {
	
						parentsList = getSystemData().getParents(Constants.CODE_SYSCLASS_SUBJECT, studentSubject, new Date(), getCurrentLang(), userId);
						
						
						for(SystemClassif item: parentsList) {
							//System.out.println(item.getCode() +","+ item.getTekst());
							user.getUserStudent().setEducationArea(item.getCode()); //koeto e posledno 
						}
					} catch (DbErrorException e) {
						 LOGGER.error("Грешка при търсене на SYSCLASS_SUBJECT с родителите! ", e);
						 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
					}
			
			} 
		}
		this.studentSubject = studentSubject;
	}

	
	public Date getToday() {
		return new Date();
	}

	public Long getStudentUni() {
		return studentUni;
	}

	public void setStudentUni(Long studentUni) {
		
		if(user.getUserStudent()!=null) {
			
			user.getUserStudent().setUniversity(studentUni);
			
		}
		this.studentUni = studentUni;
	}

	public String getStudentUniText() {
		try {
			if(this.user.getUserStudent()!=null && this.user.getUserStudent().getUniversity() != null) {
				return getSystemData().decodeItem(Constants.CODE_SYSCLASS_UNIVERSITY, this.user.getUserStudent().getUniversity(), getCurrentLang(), new Date(), userId);
			} else {
				return null;
			}
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на специалност! ", e);
		}
		return studentUniText;
	}

	public void setStudentUniText(String studentUniText) {
		this.studentUniText = studentUniText;
	}
}
