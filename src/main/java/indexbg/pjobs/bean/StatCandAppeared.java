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

import indexbg.pjobs.db.dao.AdmUsersTestsDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("statCandAppeared")
@ViewScoped
public class StatCandAppeared extends PJobsBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -48372699613195266L;
	static final Logger LOGGER = LoggerFactory.getLogger(StatCandAppeared.class);

	private Long userId;
    


	private Date dateFrom;
	private Date dateTo;
	private List<Object[]> broiYavili ;
	private Long period;
	private AdmUsersTestsDAO testDao;
	
	private  List<SystemClassif> periodList;
	
	public StatCandAppeared() {
		
	}
	
	@PostConstruct
	public void initData(){
		
			try {			
				this.userId = getUserData().getUserId();
				testDao = new AdmUsersTestsDAO(userId);	
				broiYavili = new ArrayList<Object[]>();
				
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			}
	}
	
	 public void actionSearch() {
		 	    broiYavili.clear();
		    	try {
		    		
		    		// вече се ползва "Изпълнил тест" 10.01.2022,защото вече няма праг за издържал или неиздържал-
		   		 	// всеки,който се е явил и е завършил теста е изпълнил теста,дори да има 0 точки*
		    		Long izpalnili = testDao.countCandidates(dateFrom, dateTo, Constants.CODE_ZNACH_TEST_RESULTS_IZDARJAL);
		    		Long deaktiv = testDao.countCandidates(dateFrom, dateTo, Constants.CODE_ZNACH_TEST_RESULTS_DEAKTIVIRAN);
		    		//Long neizdarjali = testDao.countCandidates(dateFrom, dateTo, Constants.CODE_ZNACH_TEST_RESULTS_NEIZDARJAL);
		    		if(izpalnili==null) {
		    			izpalnili = 0L;
		    		}
//		    		if(neizdarjali==null) {
//		    			neizdarjali = 0L;
//		    		}
		    		
		    		if(deaktiv==null) {
		    			deaktiv = 0L;
		    		} 
		    		Object[] broi = {getMessageResourceString("labels", "statCandNum.yaviliSe"),izpalnili + deaktiv};	    				    		
		    		Object[] broiIzd = {getMessageResourceString("labels", "statCandApp.izpalniliTest"),izpalnili};
		    		Object[] broiDeaktiv = {getMessageResourceString("labels", "statCandApp.deaktiv"),deaktiv};
		    		//Object[] broiNeizd = {getMessageResourceString("labels", "statCandNum.neizdarjali"),neizdarjali};
		    		broiYavili.add(broi);
		    		broiYavili.add(broiIzd);
		    		broiYavili.add(broiDeaktiv);
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
		 broiYavili = new ArrayList<Object[]>();
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

	public List<Object[]> getBroiYavili() {
		return broiYavili;
	}

	public void setBroiYavili(List<Object[]> broiYavili) {
		this.broiYavili = broiYavili;
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
