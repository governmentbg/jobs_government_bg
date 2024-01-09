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
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.dao.AdmUsersTestsDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("statBroiIzdarjali")
@ViewScoped
public class StatBroiIzdarjali extends PJobsBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -48372699613195266L;
	static final Logger LOGGER = LoggerFactory.getLogger(StatBroiIzdarjali.class);

	private Long userId;
    

	private Date dateFrom;
	private Date dateTo;
	private List<Object[]> listResult;
	private Long period;
	private AdmUsersTestsDAO testDao;

	private  List<SystemClassif> periodList;
	
	public StatBroiIzdarjali() {
		
	}
	
	@PostConstruct
	public void initData(){
		
			try {			
				this.userId = getUserData().getUserId();
				testDao = new AdmUsersTestsDAO(userId);
				setListResult(new ArrayList<Object[]>());
				
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			}
	}
	
	 public void actionSearch() {
		        listResult = new ArrayList<Object[]>();
		    	try {
		    		List<Object[]> resPassed = testDao.countPassedByLevel(dateFrom, dateTo);
		    		List<Object[]> avgRes = testDao.countAverageResByLevel(dateFrom, dateTo);
		    		boolean vishi = true;
		    		boolean rakovoditeli = true;
		    		boolean eksperti = true;
		    		if(resPassed!=null &&resPassed.size()>0 && avgRes!=null && avgRes.size()>0) {
		    			for(Object[] item:resPassed) {
		    				for(Object[] itemSec:avgRes) {
			    				if(item[0]==itemSec[0]) {
			    					if(SearchUtils.asLong(item[0]).equals(Constants.CODE_ZNACHENIE_NIVO_TEST_VISHI)) {
			    						vishi = false;
			    					}else if(SearchUtils.asLong(item[0]).equals(Constants.CODE_ZNACHENIE_NIVO_TEST_RAKOVODITELI)){
			    						rakovoditeli = false;
			    					}else if(SearchUtils.asLong(item[0]).equals(Constants.CODE_ZNACHENIE_NIVO_TEST_EKSPERTI)) {
			    						eksperti = false;
			    					}
			    					Object[] newValue = {item[0],item[1],itemSec[1]};
			    					listResult.add(newValue);
			    					break;
		    				}
			    		}
		    			}
		    		}
		    		if(vishi) {
	    				Object[] newValue = {Constants.CODE_ZNACHENIE_NIVO_TEST_VISHI,0L,0L};
	    				listResult.add(newValue);
	    			}
	    			
	    			if(rakovoditeli) {
	    				Object[] newValue = {Constants.CODE_ZNACHENIE_NIVO_TEST_RAKOVODITELI,0L,0L};
	    				listResult.add(newValue);
	    			}
	    			
	    			if(eksperti) {
	    				Object[] newValue = {Constants.CODE_ZNACHENIE_NIVO_TEST_EKSPERTI,0L,0L};
	    				listResult.add(newValue);
	    			}

				}  catch (DbErrorException e) {
					LOGGER.error("Грешка при търсене на тестове за потребител! ", e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
				} catch (Exception e) {
					LOGGER.error("Грешка при работа със системата!!!", e);	
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
				
				}finally {
					JPA.getUtil().closeConnection();
				}
	   }
	 
	 public void actionClear() {
		 
		 period = null;
		 dateFrom = null;
		 dateTo = null;
		 listResult = new ArrayList<Object[]>();
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

	public List<Object[]> getListResult() {
		return listResult;
	}

	public void setListResult(List<Object[]> listResult) {
		this.listResult = listResult;
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
