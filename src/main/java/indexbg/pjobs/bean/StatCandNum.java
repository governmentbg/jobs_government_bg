package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("statCandNum")
@ViewScoped
public class StatCandNum extends PJobsBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -48372699613195266L;
	static final Logger LOGGER = LoggerFactory.getLogger(StatCandNum.class);

	private Long userId;
    

	private AdmUserDAO userDao;
	private Date dateFrom;
	private Date dateTo;
	private List<Object[]> broiKandVid;
	private Long period;
	
	private  List<SystemClassif> periodList;

	public StatCandNum() {
		
	}
	
	@PostConstruct
	public void initData(){
		
			try {			
				this.userId = getUserData().getUserId();
				setUserDao(new AdmUserDAO(userId));
				setBroiKandVid(new ArrayList<Object[]>());
				
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			}
	}
	
	 public void actionSearch() {
		 
		    	try {
		    		setBroiKandVid(userDao.countCandidatesByApplyFor(dateFrom, dateTo));
		    		
				}  catch (DbErrorException e) {
					LOGGER.error("Грешка при търсене на тестове за потребител! ", e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
				} catch (Exception e) {
					LOGGER.error("Грешка при работа със системата!!!", e);	
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
				
				}finally {
					JPA.getUtil().closeConnection();
				}
		    	
		    	if(broiKandVid==null|| broiKandVid.isEmpty()) {
		    		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages","general.noResults"));	
		    	}
	   }
	 
	 public void actionClear() {
		 
		 period = null;
		 dateFrom = null;
		 dateTo = null;
		 broiKandVid = new ArrayList<Object[]>();
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
		

	public AdmUserDAO getUserDao() {
		return userDao;
	}

	public void setUserDao(AdmUserDAO userDao) {
		this.userDao = userDao;
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

	public List<Object[]> getBroiKandVid() {
		return broiKandVid;
	}

	public void setBroiKandVid(List<Object[]> broiKandVid) {
		this.broiKandVid = broiKandVid;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
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
}
