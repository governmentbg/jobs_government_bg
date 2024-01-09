package indexbg.pjobs.bean;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.serial.Base64;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.AdmUser;
import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.PageText;
import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.db.dao.PageTextDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named("regAdminBean")
@ViewScoped
/* @author yonchev*/
public class RegAdministratorBean extends PJobsBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7420922686206521508L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegAdministratorBean.class);

	private String firstName;
	private String secondName;
	private String lastName;
	//private String job;
	private String email;
	private String pass1;
	private String pass2;
	private String phone;
	private AdmUser user;
	private AdmUserDAO dao;
	private Long userID;
	private String helpText;


	// slassif vid administrator
	private List<SelectItem> groups;
	private Long[] selectedGroups;

	// Прикачени файлове
	private Files file = new Files();
	private FilesDAO filesDAO;
	private List<Files> filesList = new ArrayList<>();
	private List<Files> deleteFilesList = new ArrayList<>();

	private String captchaStr;
	
	public RegAdministratorBean(String firstName, String secondName, String lastName, String email,
			String pass1, String pass2, String phone) {
		this.firstName = firstName;
		this.secondName = secondName;
		this.lastName = lastName;
		//this.job = job;
		this.email = email;
		this.pass1 = pass1;
		this.pass2 = pass2;
		this.phone = phone;
		this.user= new AdmUser();
	}
	
	public RegAdministratorBean() {
		
	}

	@PostConstruct
	public void initData() {
		try {

			actionClear();
//			ArrayList<Object[]> tmpList = null;
//			tmpList = createAdmTypesList();
//			if(tmpList !=null && !tmpList.isEmpty()){
//				groups = ConvertInSelectItemStr(tmpList);
//			}
//			this.helpText= new StringBuffer("Настоящата заявка за регистрация се попълва от лицата съгласно издадената заповед от органа на власт ")
//					.append("за упълномощаване на служители, които отговарят за вписване и обработка на информация по отношение на: ")
//					.append("централизирания етап на конкурсите за постъпване на държавна служба;")
//					.append(" обявите за преминаване и временно преместване в друга администрация ")
//					.append("(постоянна и временна мобилност); стипендиантската програма в държавната администрация.")
//					.append(" Към заявката се прикача заповедта за упълномощаване. В случай, че едно и също лице е упълномощено за достъп до различните")
//					.append(" модули на портала с повече от една заповед, към заявката се прилагат всички заповеди. Вашата регистрация ще бъде активирана")
//					.append(" след преглед на данните и прикачените документи от страна на администраторите на портала.").toString();
			this.userID=Constants.PORTAL_USER;
			this.dao= new AdmUserDAO(this.userID);
			this.filesDAO=new FilesDAO(Constants.UNREGISTERED_USER);
//			System.out.println("Groups size is: "+groups.size());
			
			PageText pt = new PageTextDAO(userID).loadPageTextByCodeSectLang(Constants.CODE_ZNACHENIE_SECTION_ADMIN_FORM, getCurrentLang());
			
			if(pt!=null && pt.isVisibleOnSite()) {
				helpText = pt.getPageText();
			}
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(Constants.beanMessages, "general.exception"));
		}
	}



	public void actionClear() {
		this.firstName = "";
		this.secondName = "";
		this.lastName = "";
		//this.job = "";
		this.email = "";
		this.pass1 = "";
		this.pass2 = "";
		this.phone = "";
		this.setUser(new AdmUser());

		this.captchaStr = "";
		this.deleteFilesList.clear();
		this.filesList.clear();
	}
	
	public boolean checkData() {
		boolean isFine = true;
		// firstName
		if (firstName == null || firstName.trim().isEmpty()) {
			JSFUtils.addMessage("formRegAdmin:firstName", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.fname")));
			isFine = false;
		}

		// secondName
		if (secondName == null || secondName.trim().isEmpty()) {
			JSFUtils.addMessage("formRegAdmin:secondName", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.secondName")));
			isFine = false;
		}

		// lastName
		if (lastName == null || lastName.trim().isEmpty()) {
			JSFUtils.addMessage("formRegAdmin:lastName", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.lname")));
			isFine = false;
		}

		// job
//		if (job == null || job.trim().isEmpty()) {
//			JSFUtils.addMessage("formRegAdmin:job", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.job")));
//			isFine = false;
//		}
		
		// pass1
		if (pass1 == null || pass1 .trim().isEmpty()) {
			JSFUtils.addMessage("formRegAdmin:pass1", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.pass")));
			isFine = false;
		}
		if (!ValidationUtils.isValidSimboli("№\\\",.;:'%(){}[]^=<>|/`~ ", pass1)) {
			JSFUtils.addMessage("formRegAdmin:pass1", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.validPass")));
			isFine = false;
		}
		if(pass1.trim().length() < 8) {
		JSFUtils.addMessage("formRegAdmin:pass1", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.validPass")));
		isFine = false;
		}

		// pass2
		if (pass2 == null || pass2 .trim().isEmpty()) {
			JSFUtils.addMessage("formRegAdmin:pass2", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.pass1")));
			isFine = false;
		}
		
		// pass2
		if (!pass2.trim().equals(pass1.trim())) {
			JSFUtils.addMessage("formRegAdmin:pass2", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"pass.noMatch"));
			isFine = false;
		}
		// email
		if (email == null || email.trim().isEmpty() || !ValidationUtils.isEmailValid(this.email)) {
			JSFUtils.addMessage("formRegAdmin:email", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.email")));
			isFine = false;
		}

		// phone
		if (phone == null || phone.trim().isEmpty()) {
			JSFUtils.addMessage("formRegAdmin:phone", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.telefon")));
			isFine = false;
		}
		
		if (captchaStr == null || captchaStr.trim().isEmpty()) {
			JSFUtils.addMessage("formRegAdmin:captchaStr", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.captcha")));
			isFine = false;
		} else if (!checkCaptcha()) {
			// sravnyavame za pravilno vuvedeni simvoli
			JSFUtils.addMessage("formRegAdmin:captchaStr", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.captcha")));
			isFine = false;
		}
		
		if (filesList == null || filesList.isEmpty()) {
			JSFUtils.addMessage("formRegAdmin:filesUpload", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.attachedDoc")));
			isFine = false;
		}
		
		
		return isFine;
	}
	


	public void actionSave() {
		
		
		if(checkData()) {

			try {
					
				JPA.getUtil().begin();
				ArrayList<Long[]> res=checkUser(this.email);
				
				
				if(res.size()>0) {
					//ако има такъв потребител и е активен, вече има, иначе свържете се с администратор
					if((Long)res.get(0)[1]==2L) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","regAdmin.errorUser"));
					}else {
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","regAdmin.errorStatus"));
					}
					return; 
				}
				
				 long id=actionSaveUser();
				 //actionSaveUserRoles(id);
				
				JPA.getUtil().flush();
				
				if (!this.deleteFilesList.isEmpty()) {
					for (int i = 0; i < this.deleteFilesList.size(); i++) {
						if (this.deleteFilesList.get(i).getId() != null) {
							this.filesDAO.delFromFilesText(this.deleteFilesList.get(i).getId());
							this.filesDAO.deleteById(this.deleteFilesList.get(i).getId());
						}
					}
				}
		
				for (int i = 0; i < this.filesList.size(); i++) {
					
					if (this.filesList.get(i).getId() == null) {
						this.filesList.get(i).setCodeObject(Constants.CODE_OBJECTS_USERS);
						this.filesList.get(i).setIdObject( id);	
						
						this.filesDAO.save(this.filesList.get(i));
					
					} else { // при редакция не е измъкнат целия обект и затова ще се прави ъпдейт на 4 полета - description, visibleOnSite, userLastMod, dateLastMod
						
						this.filesDAO.updateFile(this.filesList.get(i).getDescription(), getUserData().getUserId(), new Date(), this.filesList.get(i).getId()); 		
					}			
				}
				//изпращане на имейл
				ArrayList<String> emails = new ArrayList<String>();
				emails.add(this.email);
				new MailDAO(getUserID()).saveMail(7L, emails, id, new Date(), null, null, this.firstName+" "+this.secondName+" "+this.lastName,
						id, null, id, Constants.CODE_OBJECTS_USERS, getSystemData(), null, null, null, null, null, null);
				
				JPA.getUtil().flush();
				
				JPA.getUtil().commit();
				actionClear();
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));

				}catch (DbErrorException e) {
				 	LOGGER.error(e.getMessage(), e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
					JPA.getUtil().rollback();
				} catch (ObjectNotFoundException e) {
					LOGGER.error(e.getMessage(), e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.objectInUse"), e.getMessage());
					this.userID=-1L;
					JPA.getUtil().rollback();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());	
					JPA.getUtil().rollback();
				} finally {
					JPA.getUtil().closeConnection();
			} 
	  }
		this.captchaStr="";
	}
	
	
	public long actionSaveUser() throws DbErrorException {
		try {

			 BigInteger id = (BigInteger) JPA.getUtil().getEntityManager()
						.createNativeQuery("SELECT nextval('seq_adm_users')").getResultList().get(0); 
			Query query = JPA.getUtil().getEntityManager()
					.createNativeQuery("INSERT INTO adm_users (user_id,username,password,user_type,names,lang,email,phone,status,status_date,"
							+ "mail_forward,date_reg,user_reg,pass_is_new ,login_attempts) "
							+ "VALUES (?,?,?,?,?,?,?,?,?,current_timestamp,?,current_timestamp,?,?,?);")
					 .setParameter(1, id.longValue())//username
					 .setParameter(2, this.email)//username
					 .setParameter(3, Base64.encodeBytes(this.pass1.getBytes()))//password
					 .setParameter(4, 1L)//user_type
					 .setParameter(5, this.firstName+" "+this.secondName+" "+this.lastName)//names
					 .setParameter(6, getCurrentLang())//lang
					 .setParameter(7, this.email)//email
					 .setParameter(8, this.phone)//phone
					 .setParameter(9, 1L)//status
					 .setParameter(10, -1L)//mail_forward
					 .setParameter(11, Constants.UNREGISTERED_USER)//user_reg
					 .setParameter(12, 1L)//pass_is_new
					 .setParameter(13, 0L);//pass_is_new
					 query.executeUpdate();
  
			return id.longValue();

		} catch (HibernateException e) {
			throw new DbErrorException("Грешка при търсене на видове групи администратори", e);
		}
	}
	
//	public String actionSaveUserRoles(Long id) throws DbErrorException {
//		try {
//
//					EntityManager manager= JPA.getUtil().getEntityManager();
//					for(Long l : this.selectedGroups) {
//						int query = manager.createNativeQuery("INSERT INTO adm_user_group(user_id,group_id) VALUES(?,?);")
//						 .setParameter(1, id)//user_id
//						 .setParameter(2,l)//group_id
//						 .executeUpdate();
//					}
//					
//			return "";
//
//		} catch (HibernateException e) {
//			throw new DbErrorException("Грешка при търсене на видове групи администратори", e);
//		}
//	}
	
	
//	public void actionDelete(){
//		
//		try {
//			
//			JPA.getUtil().begin();		
//			FilesDAO filesDAO = new FilesDAO(getUserData().getUserId());
//			
//			// Прикачените файлове
//			for (Files item: this.filesList) {
//				if (null!=item.getId()){
//					filesDAO.deleteById(item.getId());
//				}
//			}
//			
//			for (Files item: this.deleteFilesList) {
//				if (null!=item.getId()){
//					filesDAO.deleteById(item.getId());
//				}
//			}
//			
//			// Потребител
//			if (null!=this.user.getId())
//				new AdmUserDAO(getUserData().getUserId()).deleteById(this.user.getId());
//				
//			JPA.getUtil().commit();			
//				
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));
//			
//			if (!this.deleteFilesList.isEmpty()) 
//				this.deleteFilesList.clear();
//			
//		} catch (DbErrorException e) {
//			LOGGER.error(e.getMessage(), e);
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"), e.getMessage());
//			JPA.getUtil().rollback();
//		} catch (ObjectInUseException e) {
//			LOGGER.error(e.getMessage(), e);
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.objectInUse"), e.getMessage());
//			JPA.getUtil().rollback();
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage(), e);
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());	
//			JPA.getUtil().rollback();
//		} finally {
//			JPA.getUtil().closeConnection();
//		}
//	}
	

	/**
	 * checkCaptcha
	 * 
	 * @return
	 */
	public boolean checkCaptcha() {
		String captchaStr = JSFUtils.getFacesContext().getExternalContext().getSessionMap().get("myCpatchaIgg").toString();
		if (captchaStr == null) {
			captchaStr = "";
		}
		if (captchaStr.trim().equals(this.getCaptchaStr().trim())) {
			return true;
		}
		return false;
	}
	
	/** Методът връща дали вече има потреител с това потребителско им*/
	@SuppressWarnings("unchecked")
	public ArrayList<Long[]> checkUser(String userName) throws  DbErrorException{
		try{
			
			Query query = JPA.getUtil().getEntityManager().createNativeQuery("select user_id, status from adm_users where username =:USERNAME");
			
			query.setParameter("USERNAME", userName);
			ArrayList<Object[]> res1=(ArrayList<Object[]>) query.getResultList();
			ArrayList<Long[]> res= new ArrayList<Long[]> ();
			for(Object[] ob:res1) {
				Long[] l= {Long.parseLong(ob[0].toString()),Long.parseLong(ob[1].toString())};
				res.add(l);
			}
			return  res;
			
		} catch (HibernateException e) {
			throw new DbErrorException(	"Грешка при търсене на видове групи администратори", e);
		}
		
	}
	
	/** Методът връща списък от различните видове администратори от таблицата adm_groups*/
	@SuppressWarnings("unchecked")
	public ArrayList<Object[]> createAdmTypesList() throws  DbErrorException{
		try{
			
			Query query = JPA.getUtil().getEntityManager().createNativeQuery("select group_id, group_name from adm_groups where date_do is null order by group_id");
			
//			query.addScalar("STATUS_TEKST", new StringType());
//			query.addScalar("DESCRIPTION", new StringType());

			return  (ArrayList<Object[]>) query.getResultList();
			
		} catch (HibernateException e) {
			throw new DbErrorException(	"Грешка при търсене на видове групи администратори", e);
		}
		
	}
	
	
	/**
	 * Method that converts list in selectItems for the drop down list
	 */
	public List<SelectItem> ConvertInSelectItemStr(ArrayList<Object[]> lst) {	
		List<SelectItem> items = new ArrayList<SelectItem>();
		Iterator<Object[]> it = lst.iterator();
		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			if(item != null && item[0]!=null && item[1]!=null){
				items.add(new SelectItem( item[0].toString(),item[1].toString()));
			}
		}
		return items;
	}
	
	
	// ************************************************* FILES ************************************************************* //
	
	/** Избор на файлове за прикачване към събитието
	 * 
	 * @param event
	 */
	public void uploadFileListener(FileUploadEvent event){		
		
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
		
	/** Извличане от БД на запазените файлове към събитието
	 * 
	 * @param file
	 */
	public void download(Files file){ 
		
		boolean ok = true;
		
		if(file.getContent() == null && file.getId() != null) {
			
			try {					
				
				file = this.filesDAO.findById(file.getId());
				
				if(file.getPath() != null && !file.getPath().isEmpty()){
					Path path = Paths.get(file.getPath());
					file.setContent(java.nio.file.Files.readAllBytes(path));
				}
			
			} catch (DbErrorException e) {
				LOGGER.error("DbErrorException: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
			
			} catch (IOException e) {
				LOGGER.error("IOException: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			
			} catch (Exception e) {
				LOGGER.error("Exception: " + e.getMessage());
				ok = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
			
			}  finally {
				JPA.getUtil().closeConnection();
			}
		}
		
		if(ok){
			
			try {
				
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
			
			} catch (IOException e) {
				LOGGER.error("IOException: " + e.getMessage());
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			
			} catch (Exception e) {
				LOGGER.error("Exception: " + e.getMessage());
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
			} 
		}
		
	}
		
	/** Премахва избрания файл
	 * 
	 * @param file
	 */
	public void remove(Files file){
			
		this.filesList.remove(file);
		this.deleteFilesList.add(file);			
	}		
		


	/** ======================================== Getters & Setters * ================================**/

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

//	public String getJob() {
//		return job;
//	}
//
//	public void setJob(String job) {
//		this.job = job;
//	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPass1() {
		return pass1;
	}

	public void setPass1(String pass1) {
		this.pass1 = pass1;
	}

	public String getPass2() {
		return pass2;
	}

	public void setPass2(String pass2) {
		this.pass2 = pass2;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Files getFile() {
		return file;
	}

	public void setFile(Files file) {
		this.file = file;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public List<Files> getDeleteFilesList() {
		return deleteFilesList;
	}

	public void setDeleteFilesList(List<Files> deleteFilesList) {
		this.deleteFilesList = deleteFilesList;
	}

	public List<SelectItem> getGroups() {
		return groups;
	}

	public void setGroups(List<SelectItem> groups) {
		this.groups = groups;
	}

	public Long[] getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(Long[] selectedGroups) {
		this.selectedGroups = selectedGroups;
	}

//	public String getHelpText() {
//		return helpText;
//	}
//
//	public void setHelpText(String helpText) {
//		this.helpText = helpText;
//	}

	public String getCaptchaStr() {
		return captchaStr;
	}

	public void setCaptchaStr(String captchaStr) {
		this.captchaStr = captchaStr;
	}

	public AdmUser getUser() {
		return user;
	}

	public void setUser(AdmUser user) {
		this.user = user;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUseIrD(Long userID) {
		this.userID = userID;
	}

	public AdmUserDAO getDao() {
		return dao;
	}

	public void setDao(AdmUserDAO dao) {
		this.dao = dao;
	}
	

	public long getTimeStamp() {
			return  new Date().getTime(); 
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

}