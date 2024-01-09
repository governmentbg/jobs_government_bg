package indexbg.pjobs.bean;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.PageText;
import indexbg.pjobs.db.dao.PageTextDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named
@RequestScoped
public class PagesTextView extends PJobsBean {	
	
	/** Основен java клас за разглеждане на статичните текстове към избраната страница
	 * 
	 * 
	 */
	private static final long serialVersionUID = -3878807729461872099L;
	static final Logger LOGGER = LoggerFactory.getLogger(PagesTextView.class);
	
	private String pageTextData;
	
	/** Иницира първоначалните стойности на обекта
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!!");		
		
		pageTextData = "";
		
		String par =JSFUtils.getRequestParameter("sect");		
		
		if(par != null && !par.isEmpty() && ValidationUtils.isNumber(par)) {
			
			try {
				
				PageText pt = new PageTextDAO(Constants.PORTAL_USER).loadPageTextByCodeSectLang(Long.valueOf(par), getCurrentLang());
								
				if(pt!=null && pt.isVisibleOnSite()) {
					pageTextData = pt.getPageText();
				}
				
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));		
			} 	 finally {
				JPA.getUtil().closeConnection();				
			}			
		} else {
			try {
				redirectExternal("pageNotFound.jsf");
			} catch (IOException e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));		
			}				
		}	
	}

	public String getPageTextData() {
		return pageTextData;
	}

	public void setPageTextData(String pageTextData) {
		this.pageTextData = pageTextData;
	}
	
}
