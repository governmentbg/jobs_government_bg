package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.SysConstants;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dao.SystemOptionDAO;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.db.dto.SystemOption;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.InvalidParameterException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.mail.Mailer3;
import com.indexbg.system.mail.Mailer3.Content;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.StringUtils;
import com.indexbg.system.utils.TreeUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.Administration;
import indexbg.pjobs.db.Contacts;
import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.db.dao.ContactsDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named ("contactFormBean")
@ViewScoped
public class ContactFormBean  extends PJobsBean  implements  Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8241720248364161033L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ContactFormBean.class);

	private String names;
	private String email;
	private String about;
	private String message;
	private String captchaStr;
	
	private TreeNode admRootNode; 
	private String   searchWord;
	private Long codeAdmStruct;
	private String nameAdmStruct;
	
	private String admTown="";
	private String admAdress="";
	private String admPhone="";
	private String admFax="";
	private String admEmail="";
	
	
	private String admMSTown="";
	private String admMSAdress="";
	private String admMSPhone="";
	private String admMSFax="";
	private String admMSEmail="";
	
	private Long idUser;
	
	
	
	@PostConstruct
	public void initData() {
		
		idUser = Constants.UNREGISTERED_USER;
		
		try {
			
			if(null!=getUserData()) {
				idUser=getUserData().getUserId();
				this.email=getUserData().getEmailUser();
				this.names=getUserData().getLiceNames();
			}

			ContactsDAO cDao =  new ContactsDAO(getUserData().getUserId());	
			List<Contacts> contactsList = cDao.findAll();
			Contacts contacts = new Contacts();
			if(contactsList!=null && contactsList.size()>0) {
			   	contacts = cDao.findAll().get(0);
			}else {
				contacts = new Contacts();			
			}
			Administration admData = new AdministrationDAO(this.idUser,getSystemData()).findById(13776L);
			if(null!=admData) {
				
				if(admData.getTown()!=null)
					admMSTown = getSystemData().decodeItem(Constants.CODE_SYSCLASS_EKATTE, admData.getTown(), getCurrentLang(), new Date(), idUser);
				if(admData.getZipCode()!=null)
					admMSTown += " " + admData.getZipCode();
				if(admData.getAddress()!=null)
					admMSAdress = admData.getAddress();
				if(admData.getPhone()!=null)
					admMSPhone = contacts.getPhones();
				if(admData.getFax()!=null)
					admMSFax = contacts.getFax();
				if(admData.getEmail()!=null)
					admMSEmail  = contacts.getEmails();
			}
			
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене в базата данни! " + "-" + e.getMessage() + e.getCause().getMessage() );
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCaptchaStr() {
		return captchaStr;
	}

	public void setCaptchaStr(String captchaStr) {
		this.captchaStr = captchaStr;
	}
	

	public boolean checkData() {
		boolean isFine = true;
		if(captchaStr==null ||captchaStr.trim().isEmpty()){
			JSFUtils.addMessage("formContact:captchaStr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.captcha")));
			isFine = false;
		} else if(!checkCaptcha()){
			//sravnyavame za pravilno vuvedeni simvoli
			JSFUtils.addMessage("formContact:captchaStr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.captcha")));
			isFine = false;
		}
		//names
		if(names==null ||names.trim().isEmpty()){
			JSFUtils.addMessage("formContact:names",FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.imena")));
			isFine = false;
		}
		//email
		if(email==null ||email.trim().isEmpty() || !ValidationUtils.isEmailValid(this.email)){
			JSFUtils.addMessage("formContact:email",FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.email")));
			isFine = false;
		}
		//about
		if(about==null ||about.trim().isEmpty()){
			JSFUtils.addMessage("formContact:about",FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "contactForm.about")));
			isFine = false;
		}
		//message
		if(message==null ||message.trim().isEmpty()){
			JSFUtils.addMessage("formContact:message",FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "contactForm.message")));
			isFine = false;
		}
		
		return isFine;
	}

	/**
	 * Mail send
	 * @throws DbErrorException 
	 */
	private void sendMail(){

		// String registerDate = Base64Coder.encodeString(new
		// SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()).toString());
		String confFile = "indexbg.mail3.properties";

		String subject = this.about;
		
		StringBuffer cont = new StringBuffer();
		cont.append("<div style='background-color:#fff;height:90px;font-size:20px;color:#fff'><img src='http://www.gov.bg/images/frontend/logo.png' width='100' align='center' />");
		cont.append(getMessageResourceString(Constants.LABELS,"general.title"));
		cont.append("</div>");
		cont.append("<div style='font-size:16px;color:#35475d'><br/>");
		cont.append("<b>Постъпило е запитване от:</b> ");
		cont.append(names);
		cont.append("<br/>");
		cont.append("<b>Имейл за кореспонденция:</b> ");
		cont.append(email);
		cont.append("<hr>");
		cont.append(message);
		cont.append("</div>");
		
	//	String error = null;
		SystemOptionDAO optionsDAO= new SystemOptionDAO();
		if(checkData()) {
		
		try {
			
			SystemOption op= optionsDAO.findById(37L);
			String mail = op.getOptionValue();
		
			Properties mailProps = Mailer3.loadProps(confFile);
			Mailer3 mailer3 = new Mailer3();
			
			mailer3.sent(Content.HTML, mailProps, mailProps.getProperty("user.name"),
					mailProps.getProperty("user.password"), mailProps.getProperty("mail.from"), "PJOBS", mail, subject +" от "+this.email,
					cont.toString(), new ArrayList<DataSource>());
			
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "contact.success"));
		} catch (AddressException e) {
			LOGGER.error("Error sendMail AddressException", e);
	//		error = StringUtils.stack2string(e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "contact.error"));
		} catch (InvalidParameterException e) {
			LOGGER.error("Error sendMail InvalidParameterException", e);
	//		error = StringUtils.stack2string(e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "contact.error"));
		} catch (MessagingException e) {
			LOGGER.error("Error sendMail MessagingException", e);
	//		error = StringUtils.stack2string(e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "contact.error"));
		} catch (Exception e) {
			LOGGER.error("Error sendMail Exception", e);
	//		error = StringUtils.stack2string(e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "contact.error"));
		} finally {

				try {
					JPA.getUtil().begin();
					
					/* za jurnaliraneto */
					SystemJournal j = new SystemJournal();
					j.setCodeObject(Constants.CODE_OBJECT_CONTACT_EMAIL); // на заявлението
					j.setIdObject(-1L); // ид на заявление
					j.setCodeAction(SysConstants.CODE_DEIN_KOREKCIA); // кода на корекция - CODE_DEIN_KOREKCIA
					j.setDateAction(new Date()); // датата на промяната
					j.setIdUser(Constants.UNREGISTERED_USER); // ид на потребител
					j.setIdentObject("Изпращаме имейл на "+this.email); // какво се променя
					
					JPA.getUtil().getEntityManager().persist(j);
					
					JPA.getUtil().commit();
				} catch (DbErrorException e) {
					LOGGER.error("Грешка при изпращане на имейл", e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "contact.error"));
					JPA.getUtil().rollback();
				}
				
				JPA.getUtil().closeConnection();
			}
		}
	}

	public void clearForm() {
		this.names = "";
		this.email = "";
		this.about = "";
		this.message = "";
		this.captchaStr="";
	}

	@Override
	public void run() {
		sendMail();
		clearForm();
	}

	/**
	 * checkCaptcha
	 * @return
	 */
	public boolean checkCaptcha() {
		String captchaStr =  JSFUtils.getFacesContext().getExternalContext().getSessionMap().get("myCpatchaIgg").toString();
		if (captchaStr == null) {
			captchaStr = "";
		}
		if (captchaStr.trim().equals(this.getCaptchaStr().trim())) {
			return true;
		}		
		return false;
	}

	public TreeNode getAdmRootNode() {
	    if(admRootNode==null){
			
			try {
				
				List<SystemClassif> listItems =  getSystemData().getSysClassification(Constants.CODE_SYSCLASS_ADM_REGISTER, new Date(), getCurrentLang(), idUser);
				
				admRootNode = new TreeUtils().loadTreeData3(listItems, "", false,false ,null ,null);
				
			} catch (DbErrorException e) {
				LOGGER.error(e.getMessage(),e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"),e.getMessage());
			} catch (Exception e) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"),e.getMessage());
				LOGGER.error(e.getMessage(),e);
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
		return admRootNode;
	}

	public void setAdmRootNode(TreeNode admRootNode) {
		this.admRootNode = admRootNode;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	
	
	/**Метод за търсене на определена административна структура по наименование
	 * @throws DbErrorException
	 */
	public void search() throws DbErrorException {
		
		LOGGER.info("Searching for classif with: " + getSearchWord());
		try {
			
			List<SystemClassif> classifList =  getSystemData().getSysClassification(Constants.CODE_SYSCLASS_ADM_REGISTER, new Date(), getCurrentLang(), idUser);
			TreeNode rootNode = new TreeUtils().fTree(classifList, getSearchWord(), true, true ,null ,null);
			setAdmRootNode(rootNode);
			
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"), e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	public void refreshTree() throws DbErrorException{ 
		setSearchWord(null);
		admRootNode =null;
		getAdmRootNode();
	}
	
	/**Метод за извличане на код на службата на администратор
	 * @param event
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		admTown   = "";
		admAdress = "";
		admPhone  = "";
		admFax    = "";
		admEmail  = "";
		nameAdmStruct = "";
		
		
		if(event.getTreeNode().isLeaf()){
		
			SystemClassif item = (SystemClassif) event.getTreeNode().getData();
			
			if (null!=item){
				codeAdmStruct=Long.valueOf(item.getCode());
				nameAdmStruct = item.getTekst();
				
				
				try{
					
					
					Administration admData=new AdministrationDAO(this.idUser,getSystemData()).findById(codeAdmStruct);
					
					if(null!=admData) {
						
						if(admData.getTown()!=null) {
							admTown  = "Населено място: "+ getSystemData().decodeItem(Constants.CODE_SYSCLASS_EKATTE, admData.getTown(), getCurrentLang(), new Date(), idUser);
							if(admData.getZipCode()!=null)
								admTown += " пк. "+ admData.getZipCode();
						}
						if(admData.getAddress()!=null)
							admAdress = "Адрес: "+admData.getAddress();
						if(admData.getPhone()!=null)
							admPhone  = "Телефон(и): "+admData.getPhone();
						if(admData.getFax()!=null)
							admFax    = "Факс: "+admData.getFax(); 
						if(admData.getEmail()!=null)
							admEmail  = "Имейл: <a href='mailto:"+admData.getEmail()+"'>"+admData.getEmail()+"</a>";
					}
					
				} catch (DbErrorException e) {
					LOGGER.error(e.getMessage(),e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене в базата данни! " + "-" + e.getMessage() + e.getCause().getMessage() );
				} finally {
					JPA.getUtil().closeConnection();
				}
				
			}
			
		} else {
			if(event.getTreeNode().isExpanded()){
				event.getTreeNode().setExpanded(false);
        	} else {
        		event.getTreeNode().setExpanded(true);
        	}
			event.getTreeNode().setSelected(false);
		}
    }
	
	public void onNodeUnselect(NodeUnselectEvent event) {
    	setCodeAdmStruct(null);
    }


	public Long getCodeAdmStruct() {
		return codeAdmStruct;
	}


	public void setCodeAdmStruct(Long codeAdmStruct) {
		this.codeAdmStruct = codeAdmStruct;
	}


	public String getNameAdmStruct() {
		return nameAdmStruct;
	}


	public void setNameAdmStruct(String nameAdmStruct) {
		this.nameAdmStruct = nameAdmStruct;
	}


	public String getAdmAdress() {
		return admAdress;
	}


	public void setAdmAdress(String admAdress) {
		this.admAdress = admAdress;
	}


	public String getAdmTown() {
		return admTown;
	}


	public void setAdmTown(String admTown) {
		this.admTown = admTown;
	}


	public String getAdmPhone() {
		return admPhone;
	}


	public void setAdmPhone(String admPhone) {
		this.admPhone = admPhone;
	}


	public String getAdmFax() {
		return admFax;
	}


	public void setAdmFax(String admFax) {
		this.admFax = admFax;
	}


	public String getAdmEmail() {
		return admEmail;
	}


	public void setAdmEmail(String admEmail) {
		this.admEmail = admEmail;
	}


	public String getAdmMSTown() {
		return admMSTown;
	}


	public void setAdmMSTown(String admMSTown) {
		this.admMSTown = admMSTown;
	}


	public String getAdmMSAdress() {
		return admMSAdress;
	}


	public void setAdmMSAdress(String admMSAdress) {
		this.admMSAdress = admMSAdress;
	}


	public String getAdmMSPhone() {
		return admMSPhone;
	}


	public void setAdmMSPhone(String admMSPhone) {
		this.admMSPhone = admMSPhone;
	}


	public String getAdmMSFax() {
		return admMSFax;
	}


	public void setAdmMSFax(String admMSFax) {
		this.admMSFax = admMSFax;
	}


	public String getAdmMSEmail() {
		return admMSEmail;
	}


	public void setAdmMSEmail(String admMSEmail) {
		this.admMSEmail = admMSEmail;
	}
	
	public long getTimeStamp() {
			return  new Date().getTime(); 
	}
}
