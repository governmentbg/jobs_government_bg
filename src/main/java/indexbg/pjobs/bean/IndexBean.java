package indexbg.pjobs.bean;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.AdvertisementDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named
@ViewScoped
public class IndexBean extends PJobsBean {
	
	private static final long serialVersionUID = 2766354945853772463L;
	
	static final Logger LOGGER = LoggerFactory.getLogger(IndexBean.class);
	private static final int ADV_COUNT = 9;
	private static final int STAJ_COUNT = 9;
	
	
	private List<Object[]> advertisements;
	private List<Object[]> stajove;
	private Long userId;
	
	@PostConstruct
    public void init() {
        AdvertisementDAO advDao = new AdvertisementDAO(Constants.PORTAL_USER);
        PracticeDAO practiceDao = new PracticeDAO(Constants.PORTAL_USER);
        
        try {
        	
        	this.userId = Constants.PORTAL_USER;
			this.advertisements = advDao.findLatest(ADV_COUNT);
			this.stajove = practiceDao.findLatestPractices(STAJ_COUNT);
			
			Collections.reverse(this.advertisements);
			
		}
        catch (DbErrorException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
		}
        catch(Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}
    }

	public List<Object[]> getAdvertisements() {
		return advertisements;
	}

	public void setAdvertisements(List<Object[]> advertisements) {
		this.advertisements = advertisements;
	}

	public List<Object[]> getStajove() {
		return stajove;
	}

	public void setStajove(List<Object[]> stajove) {
		this.stajove = stajove;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
