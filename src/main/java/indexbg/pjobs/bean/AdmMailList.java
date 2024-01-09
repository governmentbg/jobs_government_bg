package indexbg.pjobs.bean;

import static com.indexbg.system.utils.SearchUtils.asLong;
import static com.indexbg.system.utils.SearchUtils.asString;

import java.util.ArrayList;
import java.util.Arrays;
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
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.InvalidParameterException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.mail.Mailer3;
import com.indexbg.system.mail.Mailer3.Content;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.StringUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;



@Named
@ViewScoped
public class AdmMailList extends PJobsBean {	
	
	/** Клас за зареждане на всички мейли по статус и период
	 * 
	 */
	private static final long serialVersionUID = 9042143399318452262L;

	static final Logger LOGGER = LoggerFactory.getLogger(AdmMailList.class);
	
	private MailDAO mailDAO;
	private LazyDataModelSQL2Array mailsList = null;
	private Long userId;
	
	private Long period;
	private Date dateFrom;
	private Date dateTo;
	
	private Long status;
	private String email;
	private String nameLice;
	
	private Long adm;
	
	private List<Object[]> selectedMails;
	
	private  List<SystemClassif> periodList;
	
	public AdmMailList() {
		
	}
	
	/** Иницира първоначалните стойности
	 * 
	 * 
	 */
	@PostConstruct
	public void init() {
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.mailDAO = new MailDAO(this.userId);
			this.status = Constants.CODE_ZNACHENIE_STATUS_MAIL_ERROR;
			
			if(getUserData().getTypeUser().longValue()==Constants.CODE_ZNACHENIE_TIP_POTR_VATR) {
				adm = null; //bez ogranichenie na syobshteniyata (admin na platformata)
			} else {
				adm = null;//getUserData().getCodeOrg(); //saobshteniya samo kam negovata administraciya (moderator)
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
	
	/** Метод за търсене на мейли от администратори
	 * 
	 * 
	 */
	public void actionSearch(){
		
		try {
		
			SelectMetadata smd = this.mailDAO.findMailsByAdmin(this.dateFrom, this.dateTo, this.status, this.email, this.nameLice, adm);
			String defaultSortColumn = "A0";	
			this.mailsList = new LazyDataModelSQL2Array(smd, defaultSortColumn);
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на мейли! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	
	/** Метод за изпращане на мейл от администратори
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void actionSendMail() {
		
		try {
			
			Long idObj = null;
			
			if (JSFUtils.getRequestParameter("idObj") != null && !JSFUtils.getRequestParameter("idObj").equals("")) {
				idObj = Long.valueOf(JSFUtils.getRequestParameter("idObj"));	
			}
		
			String confFile = "indexbg.mail3.properties";
			Properties mailProps = Mailer3.loadProps(confFile);
			Mailer3 mailer3 = new Mailer3();
			
			String sqlMails = "SELECT ID, EMAIL, SUBJECT, MSG FROM JOBS_MAIL WHERE ID = " + idObj; 
			Query qMails = JPA.getUtil().getEntityManager().createNativeQuery(sqlMails);
			List<Object[]>  mailsForSending = (List<Object[]>)qMails.getResultList();
			
			try {
				
				JPA.getUtil().begin();
				
				for(Object[] rowMail: mailsForSending) {
		        	
					String error = "";
					
					Long id = asLong(rowMail[0]);
		        	String email = asString(rowMail[1]);
		        	String subject = asString(rowMail[2]);
		        	String cont  = asString(rowMail[3]);
		        	
					try {
						
						ArrayList<String> mail = new ArrayList<String>(Arrays.asList(email.split(";")));
						mailer3.sent(Content.HTML, mailProps, mailProps.getProperty("user.name"), mailProps.getProperty("user.password"), mailProps.getProperty("mail.from"), " ", mail, subject, cont, new ArrayList<DataSource>());
					
					} catch (AddressException e) {
						LOGGER.error("Error sendMail AddressException", e);
						error = StringUtils.stack2string(e);
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, error);
					
					} catch (InvalidParameterException e) {
						LOGGER.error("Error sendMail InvalidParameterException", e);
						error = StringUtils.stack2string(e);
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, error);
					
					} catch (MessagingException e) {
						LOGGER.error("Error sendMail MessagingException", e);
						error = StringUtils.stack2string(e);
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, error);
					
					} catch (Exception e) {
						LOGGER.error("Error sendMail Exception", e);
						error = StringUtils.stack2string(e);
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, error);
					
					} finally {
						
						Long status = Constants.CODE_ZNACHENIE_STATUS_MAIL_SEND;
						
						Query queryUpdate = JPA.getUtil().getEntityManager().createNativeQuery ("update JOBS_MAIL set status =?, status_date =?, error =? where id =?");
						queryUpdate.setParameter(1, status ); // status
						queryUpdate.setParameter(2, new Date());
						queryUpdate.setParameter(3, error ); 
						queryUpdate.setParameter(4,  id); // id
						queryUpdate.executeUpdate();
						
						JPA.getUtil().flush();							
					}		        	
		        }
				
				JPA.getUtil().commit();
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSendMail"));
			
			} catch (Exception e) {
				LOGGER.error("Error saving mail Object", e);
				JPA.getUtil().rollback();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.sendMailErr"));
			}
		
		} catch (Exception e) {
			LOGGER.error("Възникна грешка при извличане на съобщения за изпращене!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
			
        }  finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	
	/** Метод за смяна статуса на избрани мейли
	 * 
	 * 
	 */	
	private void changeStatus(Long status){
		
		if (!this.selectedMails.isEmpty()) {
			
			try {
				
				JPA.getUtil().begin();		
			
				for (Object[] mail : selectedMails) {
					
					Long id = asLong(mail[0]);	
					
					Query queryUpdate = JPA.getUtil().getEntityManager().createNativeQuery ("update JOBS_MAIL set status =?, status_date =?, try_to_send =? where id =?");
					queryUpdate.setParameter(1, status ); 
					queryUpdate.setParameter(2, new Date());
					queryUpdate.setParameter(3, 0L); 
					queryUpdate.setParameter(4, id); 
					
					queryUpdate.executeUpdate();
				}	
				
				JPA.getUtil().commit();
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.succesChangeStatus", getSystemData().decodeItem(Constants.CODE_SYSCLASS_STATUS_MAIL, status, getCurrentLang(), new Date(), userId))); 
					
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при смяна на статус! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
	
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
	
			} finally {
				JPA.getUtil().closeConnection();
				this.selectedMails.clear();
			}
		
		} else {
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "admMailList.notSelectedMail"));
		}
	}
	
	public void actionStatusSendMail() {
		
		changeStatus(Constants.CODE_ZNACHENIE_STATUS_MAIL_SEND); 
	}
	
	public void actionStatusNotSendMail() {
		
		changeStatus(Constants.CODE_ZNACHENIE_STATUS_MAIL_NOT_SEND); 
	}

	public void actionStatusErrorMail() {
	
	changeStatus(Constants.CODE_ZNACHENIE_STATUS_MAIL_ERROR); 
}
	

	/** Метод за зачистване на зададените стойности за търсене
	 * 
	 * 
	 */
	public void actionClear(){
		
		this.mailsList = null;
		this.period = null;
		this.dateFrom = null;
		this.dateTo = null;
		this.status = Constants.CODE_ZNACHENIE_STATUS_MAIL_ERROR;
		this.email = "";
		this.nameLice = "";		
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
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */
	
	public LazyDataModelSQL2Array getMailsList() {
		return mailsList;
	}

	public void setMailsList(LazyDataModelSQL2Array mailsList) {
		this.mailsList = mailsList;
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

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNameLice() {
		return nameLice;
	}

	public void setNameLice(String nameLice) {
		this.nameLice = nameLice;
	}

	public Long getAdm() {
		return adm;
	}

	public void setAdm(Long adm) {
		this.adm = adm;
	}

	public List<Object[]> getSelectedMails() {
		return selectedMails;
	}

	public void setSelectedMails(List<Object[]> selectedMails) {
		this.selectedMails = selectedMails;
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
	// ************************************************* END GET & SET ***************************************************** //	
	
}
