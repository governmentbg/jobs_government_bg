package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.CampaignDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named
@ViewScoped
public class ReportPracticeAll extends PJobsBean{	
	
	private static final long serialVersionUID = 8534288990429849550L;

	static final Logger LOGGER = LoggerFactory.getLogger(ReportPracticeAll.class);
	
    private List<SelectItem> campaignsList = new ArrayList<SelectItem>();
    private Long idCamp;
    private Long year;
	
	private List<Object[]> resultReportAll = null;
	
	private CampaignDAO cmpDAO;
	private PracticeDAO pracDAO;
	
	/**
	 * Инициализира променливите в класа
	 */
	@PostConstruct
	public void initData(){
		
		
		try {
			pracDAO = new PracticeDAO(getUserData().getUserId());
			cmpDAO  = new CampaignDAO(getUserData().getUserId());
			
			year = (long) Calendar.getInstance().get(Calendar.YEAR);
			
			campaignsList = cmpDAO.findAllCampaigns();
			
			idCamp = cmpDAO.findLastCampaign();
			
			changeCamp();
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при работа с база данни справка reportAll (initData)! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		}  catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		
		} finally {
			JPA.getUtil().closeConnection();
		}	
		
		actionSearch();
	}
	
	
	/**
	 * Метод за изпълнение на справката
	 */
	public void actionSearch(){
		try {		
			
			resultReportAll = new ArrayList<Object[]>();
			resultReportAll.add(new Object []{ "Брой администратори, въвели обяви",pracDAO.reportResumo(idCamp, year ,"user_reg"),"reportA.jsf?faces-redirect=true&year="+year+"&idcamp="+idCamp});
			resultReportAll.add(new Object []{ "Брой административни структури заявили стаж",pracDAO.reportResumo(idCamp, year ,"administration"),"reportTotalNumberPractPoss.jsf?faces-redirect=true&year="+year+"&idcamp="+idCamp});
			resultReportAll.add(new Object []{ "Брой административни звена заявили стаж",pracDAO.reportResumo(idCamp, year ,"unit"),null});
			resultReportAll.add(new Object []{ "Брой области на висше образование",pracDAO.reportResumo(idCamp, year ,"education_area"),"reportO.jsf?faces-redirect=true&type=4&year="+year+"&idcamp="+idCamp}); 
			resultReportAll.add(new Object []{ "Брой области",pracDAO.reportResumo(idCamp, year ,"region"),"reportO.jsf?faces-redirect=true&type=5&year="+year+"&idcamp="+idCamp});
			resultReportAll.add(new Object []{ "Брой общини",pracDAO.reportResumo(idCamp, year ,"municipality"),"reportO.jsf?faces-redirect=true&type=6&year="+year+"&idcamp="+idCamp});
			resultReportAll.add(new Object []{ "Брой населени места",pracDAO.reportResumo(idCamp, year ,"town"),"reportO.jsf?faces-redirect=true&type=7&year="+year+"&idcamp="+idCamp});
			resultReportAll.add(new Object []{ "Общ брой стажантски обяви",pracDAO.reportResumoCountPracticle(idCamp, year),null});
			resultReportAll.add(new Object []{ "Общ брой отворени стажантски позиции",pracDAO.reportResumoSumPositions(idCamp, year),null});
			resultReportAll.add(new Object []{ "Общ брой кандидатствания по обявени позиции",pracDAO.reportResumoSumAllCandidatos(idCamp, year),null});
			resultReportAll.add(new Object []{ "Общ брой насрочени интервюта",pracDAO.reportResumoSumEntrevista(idCamp, year),null});
			resultReportAll.add(new Object []{ "Общ брой кандидатствали по обявени позиции",pracDAO.reportResumoSumCandidatos(idCamp, year),"reportV.jsf?faces-redirect=true&year="+year+"&idcamp="+idCamp});
			
			resultReportAll.add(new Object []{ "Брой области на висше образование на кандидатите",pracDAO.reportResumoEducation(idCamp, year, "JUS.education_area"),"reportUniversity.jsf?faces-redirect=true&type=2&year="+year+"&idcamp="+idCamp});
			resultReportAll.add(new Object []{ "Брой висши училища на кандидатите",pracDAO.reportResumoEducation(idCamp, year, "JUS.university"),"reportUniversity.jsf?faces-redirect=true&type=1&year="+year+"&idcamp="+idCamp});
			
			
			HashMap <Long,Long> pr = pracDAO.reportResumoPracticleResult(idCamp, year);
			if(pr.isEmpty()) {
				resultReportAll.add(new Object []{ "Общ брой завършили стаж",0l,null});
				resultReportAll.add(new Object []{ "Общ брой незавършили стаж",0l,null});
				resultReportAll.add(new Object []{ "Неотбелязани от администратор стажанти",0l,null});
			} else {
				resultReportAll.add(new Object []{ "Общ брой завършили стаж",(pr.containsKey(1L)?pr.get(1L):0L),"reportPractice.jsf?faces-redirect=true&type=1&year="+year+"&idcamp="+idCamp});
				resultReportAll.add(new Object []{ "Общ брой незавършили стаж",(pr.containsKey(2L)?pr.get(2L):0L),"reportPractice.jsf?faces-redirect=true&type=2&year="+year+"&idcamp="+idCamp});
				resultReportAll.add(new Object []{ "Неотбелязани от администратор стажанти",(pr.containsKey(0L)?pr.get(0L):0L),"reportPractice.jsf?faces-redirect=true&type=3&year="+year+"&idcamp="+idCamp});
			}
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на резултати от стаж! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	/**
	 * Зачистване на зададените от потребителя стойности за търсене
	 */
	public void actionClear(){
	       
		idCamp = null;
		year = (long) Calendar.getInstance().get(Calendar.YEAR);
		resultReportAll = null;
	}
	
	public String actionGoto(Object[] row) {
		
		return (String) row[2];
	}

	public Long getIdCamp() {
		return idCamp;
	}


	public void setIdCamp(Long idCamp) {
		this.idCamp = idCamp;
	}


	public List<SelectItem> getCampaignsList() {
		return campaignsList;
	}


	public void setCampaignsList(List<SelectItem> campaignsList) {
		this.campaignsList = campaignsList;
	}


	public Long getYear() {
		return year;
	}


	public void setYear(Long year) {
		this.year = year;
	}


	public List<Object[]> getResultReportAll() {
		return resultReportAll;
	}


	public void setResultReportAll(List<Object[]> resultReportAll) {
		this.resultReportAll = resultReportAll;
	}
	
	public void changeCamp() {
		if(idCamp!=null) {
			year = null;
		}
	}
	
}
