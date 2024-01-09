package indexbg.pjobs.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.BaseSystemData;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dao.SystemOptionDAO;
import com.indexbg.system.db.dto.SystemOption;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@ViewScoped
@Named ( "systemOptionsEdit")
public class SystemOptionsEdit extends PJobsBean{	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2413573693113320893L;

	static final Logger LOGGER = LoggerFactory.getLogger(SystemOptionsEdit.class);
	
	private List<SystemOption> sysOptList;
	
	private SystemOptionDAO sysOptDAO;
	
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!");
		
		this.sysOptDAO = new SystemOptionDAO();	
		
		try {
			
			this.sysOptList = this.sysOptDAO.findAll();
		
		} catch (DbErrorException e) {				
			LOGGER.error("Грешка при зареждане на системните настройки! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {				
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.exception"));	
		
		} finally {
			JPA.getUtil().closeConnection();
		}		
	}
	
	public void actionSave() {
		
		if (!this.sysOptList.isEmpty()) {
			
			try {
				
				JPA.getUtil().begin();
				
				for (int i = 0; i < this.sysOptList.size(); i++) {
					//когато няма стойност
					if (this.sysOptList.get(i).getOptionValue() == null || (this.sysOptList.get(i).getOptionValue().isEmpty())) {
						//JPA.getUtil().getEntityManager().createNativeQuery("UPDATE SYSTEM_OPTIONS SET OPTION_VALUE = null WHERE ID = " + this.sysOptList.get(i).getId()).executeUpdate();	
						
						JSFUtils.addMessage("sysOptForm:tableOptions:" + i + ":optionValue", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", "Стойност"));
						return;
					} else {
						JPA.getUtil().getEntityManager().createNativeQuery("UPDATE SYSTEM_OPTIONS SET OPTION_VALUE =:VALUE WHERE ID = " + this.sysOptList.get(i).getId()).setParameter("VALUE", this.sysOptList.get(i).getOptionValue()).executeUpdate(); 		
					}					
				}
				
				JPA.getUtil().commit();
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
			
			} catch (DbErrorException e) {				
				JPA.getUtil().rollback();				
				LOGGER.error("Грешка при запис на системните настройки!! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			
			} catch (Exception e) {
				JPA.getUtil().rollback();	
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.exception"));	
				
			} finally {
				JPA.getUtil().closeConnection();
			}
			
		}
		
	}
	
	public String actionRefresh(){		
		
		BaseSystemData bsd = (BaseSystemData) JSFUtils.getManagedBean("systemData");
		bsd.getSystemPropSettings().clear();
		this.sysOptList.clear();
		initData();
		return ""; 		
	}
	
	public List<SystemOption> getSysOptList() {	
		return sysOptList;
	}

	public void setSysOptList(List<SystemOption> sysOptList) {
		this.sysOptList = sysOptList;
	}	
	
}
