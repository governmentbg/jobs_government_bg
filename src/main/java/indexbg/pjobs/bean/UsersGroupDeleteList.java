package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
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
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.AdmUsers;
import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.dao.AdmUsersDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;
import indexbg.pjobs.system.SystemData;
import indexbg.pjobs.system.UserData;

@Named
@ViewScoped
public class UsersGroupDeleteList extends PJobsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6000382651591349515L;

	static final Logger LOGGER = LoggerFactory.getLogger(UsersGroupDeleteList.class);

	private SystemData sd = null;
	private UserData ud;
	private Long lang = null;
	private Long idUser;
	private Date currentDate = new Date();
	
	private AdmUsersDAO usersDAO = null;
	
	private Long period;
	private Date dateOt;
	private Date dateDo;
	private boolean usersOver5Years;

	private LazyDataModelSQL2Array tablList = null;

	private List<SystemClassif> periodList;
	
	private List<Object[]> userSelectedAllM = new ArrayList<>();
	private List<Object[]> userSelectedTmp = new ArrayList<>(); 
	
	private Long applyFor;

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

		this.usersDAO = new AdmUsersDAO(this.idUser, this.sd);
		
		this.usersOver5Years = true;
		
		this.applyFor = Long.valueOf(Constants.CODE_ZNACH_CANDIDATE_BURSARY);
		
		actionSearch();			 
	}

	public void clearAll() {
		
		this.period = null;
		this.dateOt = null;
		this.dateDo = null;
		this.usersOver5Years = true;
		
		this.applyFor = Long.valueOf(Constants.CODE_ZNACH_CANDIDATE_BURSARY);
		
		this.tablList = null;		
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
		
		if(userSelectedAllM!=null) {
			userSelectedAllM.clear();
		}
		if(userSelectedTmp!=null) {
			userSelectedTmp.clear();
		}
		
		if (!checkDati(0))
			
		return;
		
		try {
	
			this.tablList = null;
			
			SelectMetadata smd = null;
			
			if (usersOver5Years) {
				
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR, -5);		
				
				smd = this.usersDAO.searchUsersForDelete(this.applyFor, this.dateOt, calendar.getTime());
			
			} else {
				
				smd = this.usersDAO.searchUsersForDelete(this.applyFor, this.dateOt, this.dateDo);				
			}
			
			this.tablList = this.usersDAO.newLazyDataModel(smd, "datereg");		
		
		} catch (DbErrorException e) {
			e.printStackTrace();
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на записани потребители! " + "-" + e.getMessage() + e.getCause().getMessage());
			return;
		
		} finally {
			JPA.getUtil().closeConnection();
		}

		return;
	}
	
	/**
	 * Метод за групово изтриване на избрани потребители
	 * 
	 */
	public void actionDeleteSelectedUsers() {
		
		AdmUsersDAO usersDAO = new AdmUsersDAO(this.idUser, this.sd);
		AdmUsers user = new AdmUsers();		
		ArrayList<String> mailsTo = new ArrayList<>();
		String usernames = "";
		String name = "";

		try {
			
			JPA.getUtil().begin();
			
			for (Object[] row : userSelectedAllM) {			
				
				Long userId = SearchUtils.asLong(row[0]);
				
				user = this.usersDAO.findById(userId);
				
				if (user.getEmail() != null) {
					mailsTo.add(user.getEmail());
				}
				
				if (user.getUsername() != null) {
					usernames = user.getUsername();
				}
				
				if (user.getNames() != null) {
					name = user.getNames();
				}
				
				JPA.getUtil().flush();
				
				List<Files> filesList = new FilesDAO(this.idUser).findByCodeObjAndIdObj(Constants.CODE_OBJECTS_USERS, user.getId());

				if (!filesList.isEmpty()) {
					for (int i = 0; i < filesList.size(); i++) {
						new FilesDAO(this.idUser).deleteById(Long.valueOf(filesList.get(i).getId()));
					}
				}

				JPA.getUtil().flush();
				
				usersDAO.delete(user); 
				
				JPA.getUtil().flush();
				
				// Изпращане на съобщение за извършено изтриване на потребител
				
				new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_DELETE_USER, mailsTo, getUserData().getCodeOrg(), null, null, null, name, userId, null, this.idUser, Constants.CODE_OBJECTS_USERS, getSystemData(),null, null, usernames, null, null, null);
											
			}
			
			JPA.getUtil().commit();
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.successDeleteMsg"));

			this.userSelectedAllM.clear();
			this.userSelectedTmp.clear();

			actionSearch();
				
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при групово изтриване на потребители! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg") + "-" + e.getLocalizedMessage());
			
		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception") + "-" + e.getLocalizedMessage());

		} finally {
			JPA.getUtil().closeConnection();

		}
	}
	
	/*
	 * Множествен избор на потребители
	 */
	/**
	 * Избира всички редове от текущата страница
	 * 
	 * @param eventT
	 */
	  public void onRowSelectAll(ToggleSelectEvent eventT) {    
			ArrayList<Object[]> tmpList = new ArrayList<>();
	    	tmpList.addAll(getUserSelectedAllM());
	    	if(eventT.isSelected()) {
	    		for (Object[] object : getUserSelectedTmp()) {
	    			if(object != null && object.length > 0) {
		    			boolean bo = true;
		    			Integer l2 = Integer.valueOf(object[0].toString());
		    			for (Object[] j : tmpList) { 
		    				Integer l1 = Integer.valueOf(j[0].toString());        			
		    	    		if(l1.equals(l2)) {    	    			
		    	    			bo = false;
		    	    			break;
		    	    		}
		        		}
		    			if(bo) {
		    				tmpList.add(object);
		    			}
	    			}
	    		}    		
	    	}else {
		    	List<Object[]> tmpLPageClass =  getTablList().getResult();// rows from current page....    		
				for (Object[] object : tmpLPageClass) {
					if(object != null && object.length > 0) {
						Integer l2 =Integer.valueOf(object[0].toString());
						for (Object[] j : tmpList) { 
							Integer l1 = Integer.valueOf(j[0].toString());        			
				    		if(l1.equals(l2)) {    	    			
				    			tmpList.remove(j);
				    			break;
				    		}	
			    		}
					}
				}    		
			}
			setUserSelectedAllM(tmpList);	    	
			LOGGER.debug("onToggleSelect->>");	    	    	   
	}
		    
    /** 
     * Избира един ред
     * 
     * @param eventS
     */
    @SuppressWarnings("rawtypes")
	public void onRowSelect(SelectEvent eventS) {    	
    	if(eventS!=null  && eventS.getObject()!=null) {
    		List<Object[]> tmpLst =  getUserSelectedAllM();
    		
    		Object[] object = (Object[]) eventS.getObject();
    		if(object != null && object.length > 0) {
	    		boolean bo = true;
	    		Integer l3 = Integer.valueOf(object[0].toString());
				for (Object[] j : tmpLst) { 
					Integer l1 = Integer.valueOf(j[0].toString());        			
		    		if(l1.equals(l3)) {
		    			bo = false;
		    			break;
		    		}
		   		}
				if(bo) {
					tmpLst.add(object);
					setUserSelectedAllM(tmpLst);   
				}
    		}
    	}	    	
    	LOGGER.error(String.format("1 onRowSelectIil->> %s", getUserSelectedAllM().size()));
    }
		    
    /**
     * Премахва избирането на един ред
     * 
     * @param eventU
     */
    @SuppressWarnings("rawtypes")
	public void onRowUnselect(UnselectEvent eventU) {
     	if(eventU!=null  && eventU.getObject()!=null) {
    		Object[] object = (Object[]) eventU.getObject();
    		ArrayList<Object[] > tmpLst = new ArrayList<>();
    		tmpLst.addAll(getUserSelectedAllM());
    		for (Object[] j : tmpLst) {
    			if(j != null && j.length > 0 
    				&& object != null && object.length > 0) {
    				Integer l1 = Integer.valueOf(j[0].toString());
	    			Integer l2 = Integer.valueOf(object[0].toString());
		    		if(l1.equals(l2)) {
		    			tmpLst.remove(j);
		    			setUserSelectedAllM(tmpLst);
		    			break;
		    		}
    			}
    		}
    	}
    }

    /**
     * За да се запази селектирането(визуалано на екрана) при преместване от една страница в друга
     */
    public void onPageUpdateSelected(){ 
    	if (getUserSelectedAllM() != null && !getUserSelectedAllM().isEmpty()) {
    		getUserSelectedTmp().clear();
    		getUserSelectedTmp().addAll(getUserSelectedAllM());
    	}		    	
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

	public UserData getUd() {
		return ud;
	}

	public void setUd(UserData ud) {
		this.ud = ud;
	}	

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
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

	public boolean isUsersOver5Years() {
		return usersOver5Years;
	}

	public void setUsersOver5Years(boolean usersOver5Years) {
		this.usersOver5Years = usersOver5Years;
	}

	public LazyDataModelSQL2Array getTablList() {
		return tablList;
	}

	public void setTablList(LazyDataModelSQL2Array tablList) {
		this.tablList = tablList;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
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
	
	public List<Object[]> getUserSelectedAllM() {
		return userSelectedAllM;
	}

	public void setUserSelectedAllM(List<Object[]> userSelectedAllM) {
		this.userSelectedAllM = userSelectedAllM;
	}

	public List<Object[]> getUserSelectedTmp() {
		return userSelectedTmp;
	}

	public void setUserSelectedTmp(List<Object[]> userSelectedTmp) {
		this.userSelectedTmp = userSelectedTmp;
	}

	public Long getApplyFor() {
		return applyFor;
	}

	public void setApplyFor(Long applyFor) {
		this.applyFor = applyFor;
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
