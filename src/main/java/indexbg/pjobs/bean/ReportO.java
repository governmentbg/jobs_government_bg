package indexbg.pjobs.bean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named
@ViewScoped
public class ReportO extends PJobsBean {

	private static final long serialVersionUID = -2661380488058436393L;

	static final Logger LOGGER = LoggerFactory.getLogger(ReportO.class);

	private LazyDataModelSQL2Array resultReport = null;

	private Long userId;
	private Long typeReport;

	/**
	 * Инициализира променливите в класа
	 */
	@PostConstruct
	public void initData() {

		try {

			Long idCamp = null;
			Long year = null;

			String idcampStr = JSFUtils.getRequestParameter("idcamp");

			if (idcampStr != null && ValidationUtils.isNumber(idcampStr)) {
				idCamp = Long.valueOf(idcampStr);
			}

			String yearStr = JSFUtils.getRequestParameter("year");

			if (yearStr != null && ValidationUtils.isNumber(yearStr)) {
				year = Long.valueOf(yearStr);
			}
			
			String type = JSFUtils.getRequestParameter("type");

			if (type != null && ValidationUtils.isNumber(type)) {
				this.typeReport = Long.valueOf(type);
			}
			
			if (year != null || idCamp != null || type != null) {
				
				SelectMetadata smd = new PracticeDAO(getUserData().getUserId()).findReportO(idCamp, year, this.typeReport); 
				String defaultSortColumn = "A0 asc";
				resultReport = new LazyDataModelSQL2Array(smd, defaultSortColumn);
			}

		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
		}

	}

	public LazyDataModelSQL2Array getResultReport() {
		return resultReport;
	}

	public void setResultReport(LazyDataModelSQL2Array resultReport) {
		this.resultReport = resultReport;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTypeReport() {
		return typeReport;
	}

	public void setTypeReport(Long typeReport) {
		this.typeReport = typeReport;
	}

}
