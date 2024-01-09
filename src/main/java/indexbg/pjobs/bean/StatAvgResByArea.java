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

@Named("statAvgRezByArea")
@ViewScoped
public class StatAvgResByArea extends PJobsBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -48372699613195266L;
	static final Logger LOGGER = LoggerFactory.getLogger(StatAvgResByArea.class);

	private Long userId;
    

	private Date dateFrom;
	private Date dateTo;

	private Long period;
	private AdmUsersTestsDAO testDao;
    private List<Module> modulesResult = new ArrayList<Module>();
    
    private  List<SystemClassif> periodList;
    
	public StatAvgResByArea() {
		
	}
	
	@PostConstruct
	public void initData(){
		
			try {			
				this.userId = getUserData().getUserId();
				testDao = new AdmUsersTestsDAO(userId);			
				
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			}
	}
	
	public void actionSearch() {
				modulesResult = new ArrayList<Module>();
		    	try {
		    		List<Object[]> resultModule1 = testDao.getAvgResByArea(dateFrom, dateTo, 1L);
		    		List<AvgRes> avgResList = new ArrayList<AvgRes>();
		    		AvgRes avgRes = new AvgRes();
		    		
		    	    if(resultModule1!=null && !resultModule1.isEmpty()) {
		    	    	
		    	    	boolean isFoundArea = false;
		    	    	for(Object [] item: resultModule1) {
		    	    		for(int i=0;i<avgResList.size();i++) {
		    	    			if(SearchUtils.asLong(item[1]).equals(avgResList.get(i).getArea())) {		    	    				
		    	    				if(SearchUtils.asLong(item[2]).equals(1L)) {//левъла
		    	    					avgResList.get(i).setAvg1(SearchUtils.asDouble(item[0]));
		    	    				}else if(SearchUtils.asLong(item[2]).equals(2L)) {
		    	    					avgResList.get(i).setAvg2(SearchUtils.asDouble(item[0]));
		    	    				}else if(SearchUtils.asLong(item[2]).equals(3L)) {
		    	    					avgResList.get(i).setAvg3(SearchUtils.asDouble(item[0]));
		    	    				}	    	    				
		    	    				isFoundArea = true;
		    	    				break;
		    	    			}
		    	    			
		    	    		}
		    	    		if(!isFoundArea) {
		    	    			avgRes = new AvgRes();
		    	    			if(SearchUtils.asLong(item[2]).equals(1L)) {//левъла
		    	    				avgRes.setAvg1(SearchUtils.asDouble(item[0]));
	    	    				}else if(SearchUtils.asLong(item[2]).equals(2L)) {
	    	    					avgRes.setAvg2(SearchUtils.asDouble(item[0]));
	    	    				}else if(SearchUtils.asLong(item[2]).equals(3L)) {
	    	    					avgRes.setAvg3(SearchUtils.asDouble(item[0]));
	    	    				}
		    	    			avgRes.setArea(SearchUtils.asLong(item[1]));
		    	    			avgResList.add(avgRes);
		    	    			isFoundArea = false;
		    	    		}
		    	    	}
		    	    	modulesResult.add(new Module(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, 1L, getCurrentLang(), new Date(), userId), avgResList));
		    	    	
		    	    }
		    		
		    		List<Object[]> resultModule2 = testDao.getAvgResByArea(dateFrom, dateTo, 2L);
		    		
		    		avgResList = new ArrayList<AvgRes>();
		    		avgRes = new AvgRes();
		    		
		    	    if(resultModule2!=null && !resultModule2.isEmpty()) {
		    	    	
		    	    	boolean isFoundArea = false;
		    	    	for(Object [] item: resultModule2) {
		    	    		for(int i=0;i<avgResList.size();i++) {
		    	    			if(SearchUtils.asLong(item[1]).equals(avgResList.get(i).getArea())) {		    	    				
		    	    				if(SearchUtils.asLong(item[2]).equals(1L)) {//левъла
		    	    					avgResList.get(i).setAvg1(SearchUtils.asDouble(item[0]));
		    	    				}else if(SearchUtils.asLong(item[2]).equals(2L)) {
		    	    					avgResList.get(i).setAvg2(SearchUtils.asDouble(item[0]));
		    	    				}else if(SearchUtils.asLong(item[2]).equals(3L)) {
		    	    					avgResList.get(i).setAvg3(SearchUtils.asDouble(item[0]));
		    	    				}	    	    				
		    	    				isFoundArea = true;
		    	    				break;
		    	    			}
		    	    			
		    	    		}
		    	    		if(!isFoundArea) {
		    	    			avgRes = new AvgRes();
		    	    			if(SearchUtils.asLong(item[2]).equals(1L)) {//левъла
		    	    				avgRes.setAvg1(SearchUtils.asDouble(item[0]));
	    	    				}else if(SearchUtils.asLong(item[2]).equals(2L)) {
	    	    					avgRes.setAvg2(SearchUtils.asDouble(item[0]));
	    	    				}else if(SearchUtils.asLong(item[2]).equals(3L)) {
	    	    					avgRes.setAvg3(SearchUtils.asDouble(item[0]));
	    	    				}
		    	    			avgRes.setArea(SearchUtils.asLong(item[1]));
		    	    			avgResList.add(avgRes);
		    	    			isFoundArea = false;
		    	    		}
		    	    	}
		    	    	modulesResult.add(new Module(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, 2L, getCurrentLang(), new Date(), userId), avgResList));
		    	    	
		    	    }
		    	    
		    	    
		    		List<Object[]> resultModule3 = testDao.getAvgResByArea(dateFrom, dateTo, 3L);
		    		
		    		avgResList = new ArrayList<AvgRes>();
		    		avgRes = new AvgRes();
		    		
		    	    if(resultModule3!=null && !resultModule3.isEmpty()) {
		    	    	
		    	    	boolean isFoundArea = false;
		    	    	for(Object [] item: resultModule3) {
		    	    		for(int i=0;i<avgResList.size();i++) {
		    	    			if(SearchUtils.asLong(item[1]).equals(avgResList.get(i).getArea())) {		    	    				
		    	    				if(SearchUtils.asLong(item[2]).equals(1L)) {//левъла
		    	    					avgResList.get(i).setAvg1(SearchUtils.asDouble(item[0]));
		    	    				}else if(SearchUtils.asLong(item[2]).equals(2L)) {
		    	    					avgResList.get(i).setAvg2(SearchUtils.asDouble(item[0]));
		    	    				}else if(SearchUtils.asLong(item[2]).equals(3L)) {
		    	    					avgResList.get(i).setAvg3(SearchUtils.asDouble(item[0]));
		    	    				}	    	    				
		    	    				isFoundArea = true;
		    	    				break;
		    	    			}
		    	    			
		    	    		}
		    	    		if(!isFoundArea) {
		    	    			avgRes = new AvgRes();
		    	    			if(SearchUtils.asLong(item[2]).equals(1L)) {//левъла
		    	    				avgRes.setAvg1(SearchUtils.asDouble(item[0]));
	    	    				}else if(SearchUtils.asLong(item[2]).equals(2L)) {
	    	    					avgRes.setAvg2(SearchUtils.asDouble(item[0]));
	    	    				}else if(SearchUtils.asLong(item[2]).equals(3L)) {
	    	    					avgRes.setAvg3(SearchUtils.asDouble(item[0]));
	    	    				}
		    	    			avgRes.setArea(SearchUtils.asLong(item[1]));
		    	    			avgResList.add(avgRes);
		    	    			isFoundArea = false;
		    	    		}
		    	    	}
		    	    	modulesResult.add(new Module(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, 3L, getCurrentLang(), new Date(), userId), avgResList));
		    	    	
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
		    	if(modulesResult==null || modulesResult.isEmpty()) {
		    		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages","general.noResults"));	
		    	
		    	}
	}
	 
	public void actionClear() {
		 
		 period = null;
		 dateFrom = null;
		 dateTo = null;
		 modulesResult = new ArrayList<Module>();

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
		
	public class AvgRes {
			public AvgRes(Long area, Double avg1,Double avg2,Double avg3) {
				setArea(area);
			    setAvg1(avg1);
			    setAvg2(avg2);
				setAvg3(avg3);
			}
		
			public AvgRes() {
			}
		
			private Long area;		
			private Double avg1;
			private Double avg2;
			private Double avg3;
			
			public Long getArea() {
				return area;
			}

			public void setArea(Long area) {
				this.area = area;
			}

			public Double getAvg1() {
				return avg1;
			}

			public void setAvg1(Double avg1) {
				this.avg1 = avg1;
			}

			public Double getAvg2() {
				return avg2;
			}

			public void setAvg2(Double avg2) {
				this.avg2 = avg2;
			}

			public Double getAvg3() {
				return avg3;
			}

			public void setAvg3(Double avg3) {
				this.avg3 = avg3;
			}
					
	}
	
	public class Module {
		public Module(String name, List<AvgRes> res) {
			setName(name);
		    setRes(res);
		}
	
		public Module() {
		}
	
		String name;
		List<AvgRes> res;
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<AvgRes> getRes() {
			return res;
		}

		public void setRes(List<AvgRes> res) {
			this.res = res;
		}

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

	public List<Module> getModulesResult() {
		return modulesResult;
	}

	public void setModulesResult(List<Module> modulesResult) {
		this.modulesResult = modulesResult;
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
