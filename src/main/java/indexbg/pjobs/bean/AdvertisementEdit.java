package indexbg.pjobs.bean;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.indexui.navigation.Navigation;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.Advertisement;
import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.db.dao.AdvertisementDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.db.dao.SubscriptionDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class AdvertisementEdit extends PJobsBean {	

	/** Основен java клас за въвеждане/актуализация на обяви за мобилност
	 * 
	 */
	private static final long serialVersionUID = -9106317698163149616L;
	
	static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementEdit.class);
	
	private Long userId;
	private Advertisement adver;
	private AdvertisementDAO adverDAO;
	
	// Административно звено
	private String adminZvenoText;
	private Long adminZveno;
	
	// Звено
	private String unitText;
	private Long unit;
	
	// Длъжност
	private String positionText;
	private Long position;
	
	// ЕКАТТЕ
	private List<SystemClassif> oblastList;
	private List<SystemClassif> obshtiniList = new ArrayList<SystemClassif>();
	private List<SystemClassif> nasMestoList = new ArrayList<SystemClassif>();
	
	private List<SystemClassif> locOblastList;
	private List<SystemClassif> locObshtiniList = new ArrayList<SystemClassif>();
	private List<SystemClassif> locNasMestoList = new ArrayList<SystemClassif>();
	
	// Прикачени файлове
	private Files file = new Files();
	private FilesDAO filesDAO;
	private List<Files> filesList = new ArrayList<>();
	private List<Files> deleteFilesList = new ArrayList<>();
	
	private String direkcia;
	
	// За изчисление на заплатата ми трябва ниво и професионален опит
	private Long level;
	private Long experience;  
	
	/** Иницира първоначалните стойности на обекта
	 * 
	 * Зарежда обекта по подаден параметър от списъка с обяви
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.adverDAO = new AdvertisementDAO(this.userId);
			this.filesDAO = new FilesDAO(this.userId);
			this.adver = new Advertisement();			
			
			Long idObj = null;
			
			if (JSFUtils.getRequestParameter("idObj") != null && !JSFUtils.getRequestParameter("idObj").equals("")) {
				idObj = Long.valueOf(JSFUtils.getRequestParameter("idObj"));	
			}	
			
			if (idObj != null) {
				
				this.adver = this.adverDAO.findById(idObj);
				this.filesList = this.filesDAO.findByCodeObjAndIdObj(this.adver.getCodeMainObject(), this.adver.getId());
				
				this.unit = this.adver.getUnit();
				this.position = this.adver.getPosition();
				this.adminZveno = this.adver.getAdmUnit();
				
				this.unitText = "";
				if(unit!=null) {
					this.unitText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, unit, getCurrentLang(), new Date(), this.userId);
				}
				this.positionText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_JOBS, this.adver.getPosition(), getCurrentLang(), new Date(), this.userId);
				this.adminZvenoText = getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_FLAT, this.adver.getAdmUnit(), getCurrentLang(), new Date(), this.userId);
								
				filEkateList(obshtiniList, this.adver.getRegion());
				filEkateList(nasMestoList, this.adver.getMunicipality());
								
				filEkateList(locObshtiniList, this.adver.getLocationRegion());
				filEkateList(locNasMestoList, this.adver.getLocationMunicipality());
				
				if(unit!=null) {
					getParentDirAdmStruc(this.unit.longValue());
				} else {
					direkcia ="";
				}
				
			} else {
			
				this.adver.setAdministration(getUserData().getCodeOrg());
			}
		
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;	
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));		
		} 	
		
	}
	
	/** Проверка на въведените данни на обявата, преди запис в БД
	 * 
	 * @return true при успешна проверка
	 */
	private boolean checkData() {

		boolean save = true;
		
		if (this.adver.getAdministration() == null) {
			JSFUtils.addMessage("formAdvertisement:admin", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.admStruc")));
			save = false;
		}
		
//		if (this.unit == null) { //09.01.2020 - stava nezadyljitelno pole po iskane na MS
//			JSFUtils.addMessage("formAdvertisement:zveno", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.zveno")));
//			save = false;
//		}	
//		
		if (this.adver.getLocationRegion() == null) {
			JSFUtils.addMessage("formAdvertisement:oblastPubl", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.oblast")));
			save = false;		
		}
		
		if (this.adver.getLocationMunicipality() == null) {
			JSFUtils.addMessage("formAdvertisement:obshtinaPubl", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.obshtina")));
			save = false;		
		}
		
		if (this.adver.getLocationTown() == null) {
			JSFUtils.addMessage("formAdvertisement:nasMestoPubl", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.nasMiasto")));
			save = false;
		}
		
		if (this.position == null) {
			JSFUtils.addMessage("formAdvertisement:dlajnost", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.dlajnost")));
			save = false;
		}
		
		if (this.adver.getProfessionalField() == null) {
			JSFUtils.addMessage("formAdvertisement:profNapr", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.profNapr")));
			save = false;
		}
		
		if (this.adver.getLevel() == null) {
			JSFUtils.addMessage("formAdvertisement:dlajNivo", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.dlajNivo")));
			save = false;
		}
		
		if (this.adver.getEducationDegree() == null) {
			JSFUtils.addMessage("formAdvertisement:obrazovanie", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.minObrazStepen")));
			save = false;
		}
		
		if (this.adver.getExperience() == null) {
			JSFUtils.addMessage("formAdvertisement:opit", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.minProfOpit")));
			save = false;
		}
		
		if (this.adver.getRank() == null) {
			JSFUtils.addMessage("formAdvertisement:rang", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.rang")));
			save = false;
		}
		
		if (this.adver.getSalary() == null) {
			JSFUtils.addMessage("formAdvertisement:razmerZaplata", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.minRazmerNaOsnZapl")));
			save = false;
		}
		
		if (this.adver.getNum() == null) {
			JSFUtils.addMessage("formAdvertisement:brMesta", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.brMesta")));
			save = false;
		}
		
		if (this.adver.getDescription() == null || this.adver.getDescription().isEmpty()) {
			JSFUtils.addMessage("formAdvertisement:opisanieDlaj", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.kratkoOpisDlaj")));
			save = false;
		}
		
		if (this.adver.getDateFrom() == null) {
			JSFUtils.addMessage("formAdvertisement:datePublic", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.datePubl")));
			save = false;
		
		} else {			
			if(DateUtils.startDate(this.adver.getDateFrom()).before(DateUtils.startDate(new Date()))) {				
				JSFUtils.addMessage("formAdvertisement:datePublic", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"advertisementEdit.dateFromBeforeToday"));
				save = false;
			}
		}
		
		if (this.adver.getDateTo() == null) {
			JSFUtils.addMessage("formAdvertisement:dateSrokPodav", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.srokPodavDocum")));
			save = false;
		}
		
		if (this.adver.getRegion() == null) {
			JSFUtils.addMessage("formAdvertisement:oblast", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.oblast")));
			save = false;
		}
		
		if (this.adver.getMunicipality() == null) {
			JSFUtils.addMessage("formAdvertisement:obshtina", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.obshtina")));
			save = false;
		}
		
		if (this.adver.getTown() == null) {
			JSFUtils.addMessage("formAdvertisement:nasMesto", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.nasMiasto")));
			save = false;
		}
		
		if (this.adver.getAddress() == null || this.adver.getAddress().isEmpty()) {
			JSFUtils.addMessage("formAdvertisement:adres", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "general.adress")));
			save = false;
		}
		
		if (this.adminZveno == null) {
			JSFUtils.addMessage("formAdvertisement:adminZveno", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.adminZveno")));
			save = false;
		}	
		
		return save;
	}
	
	/** Записва на въведените данни за обявата в БД
	 * 
	 */
	public void actionSave() {
		
		if (checkData()) {
			
			boolean isNew = false;
			
			try {
				
				JPA.getUtil().begin();
				
				this.adver.setAdvType(Constants.CODE_ZNACHENIE_TYPE_ADVERTISEMENT_MOBILITY); 
				
				if (this.adminZveno != null) {
					this.adver.setAdmUnit(this.adminZveno);					
				}
				
				if (this.unit != null) {
					this.adver.setUnit(this.unit); 
				} else {
					this.adver.setUnit(null); 
				}
				
				if (this.position != null) {
					this.adver.setPosition(this.position);
				}
				
				this.adver.setStatus(-1L); // за момента статуса не се използва, но да стои така, ако те поискат системна класификация за статуса
				this.adver.setStatusDate(new Date()); 
				
				if (this.adver.getId() == null) {					
					isNew = true;					
				}
				
				this.adver = this.adverDAO.save(this.adver);
				
				JPA.getUtil().flush();
				
				if (!this.deleteFilesList.isEmpty()) {
					for (int i = 0; i < this.deleteFilesList.size(); i++) {
						if (this.deleteFilesList.get(i).getId() != null) {
							this.filesDAO.delFromFilesText(this.deleteFilesList.get(i).getId());
							this.filesDAO.deleteById(this.deleteFilesList.get(i).getId());
						}
					}
				}
		
				for (int i = 0; i < this.filesList.size(); i++) {
					
					if (this.filesList.get(i).getId() == null) {
						this.filesList.get(i).setCodeObject(this.adver.getCodeMainObject());
						this.filesList.get(i).setIdObject(this.adver.getId());	
						
						this.filesDAO.save(this.filesList.get(i));
					
					} else { // при редакция не е измъкнат целия обект и затова ще се прави ъпдейт на 4 полета - description, visibleOnSite, userLastMod, dateLastMod
						
						this.filesDAO.updateFile(this.filesList.get(i).getDescription(), this.userId, new Date(), this.filesList.get(i).getId()); 		
					}			
				}
				
				JPA.getUtil().commit();
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
				
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при запис на обява! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
	
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
	
			} finally {
				JPA.getUtil().closeConnection();
			}			
		}		
	}	
	
	/** Изтриване на обявата от БД
	 * 
	 */
	public void actionDelete() {
		
		try {
		
			JPA.getUtil().begin();
			
			if (!this.filesList.isEmpty()) {
				for (int i = 0; i < filesList.size(); i++) {				
					this.filesDAO.deleteById(Long.valueOf(filesList.get(i).getId()));				
				}
			}
			
			JPA.getUtil().flush();
			
			this.adverDAO.deleteById(Long.valueOf(adver.getId())); 
				
			JPA.getUtil().commit();			
				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));
			
			if (!this.deleteFilesList.isEmpty()) {
				deleteFilesList.clear();
			}
			
			Navigation navHolder = new Navigation();			
		    int i = navHolder.getNavPath().size();
		    
		    if(i > 1) {	
		    	navHolder.goBack();
		    }

		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при изтриване на обява! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
			this.adver = new Advertisement();
		}
	}
	
	/** Метод за измъкване на дирекцията и отдела на звеното
	 * 
	 * @param zveno
	 * @return
	 */
	public String getParentDirAdmStruc(long zveno) {
		
		try {
			
			this.direkcia = "";
			
			List<SystemClassif> classifList = new AdministrationDAO(this.userId, getSystemData()).builAdmdStrOfSubject(this.adver.getAdministration(), new Date());
			
			List<SystemClassif> parents = SysClassifUtils.getParents(classifList, zveno);

			for (int i = parents.size()-1; i >= 0; i--) {
				SystemClassif par = parents.get(i);
				if (par.getCode() == zveno) {					
					break;
				} else {
					if (direkcia.isEmpty()) {
						this.direkcia = par.getTekst() + "</br>";
					} else {
						this.direkcia += " - " + par.getTekst();
					}					
				}				
			}
		
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при изграждане на дирекции и отдели!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		}
		
		return this.direkcia;		
	}
	
	/** Метод за изчисление на минималната заплата на дадена длъжност по длъжностно ниво и години опит
	 * 
	 * @param level
	 * @param experience
	 * @param newExper
	 */
	private void checkMinSalary(Long level, Long experience, Long newExper) {
		
		Query querySal = JPA.getUtil().getEntityManager().createNativeQuery(" select sl.salary_level, sl.min from jobs_salary_level sl left outer join jobs_salary s on sl.salary_id = s.id where s.level = " + level); 
		
		@SuppressWarnings("unchecked")
		List<Object[]> salaryData = (ArrayList<Object[]>) querySal.getResultList();
		
		if (this.level == this.adver.getLevel()) {
			for (Object[] sal : salaryData) {
				
				if (newExper == null) {
					
					if (sal[0] != null && SearchUtils.asLong(sal[0]) == 1) {
						 this.adver.setSalary(SearchUtils.asLong(sal[1]));								 
						 break;
					}
					
				} else {
					
					Long different = newExper - experience;
					
					if (different <= 0) {
						 
						if (sal[0] != null && SearchUtils.asLong(sal[0]) == 1) {
							 this.adver.setSalary(SearchUtils.asLong(sal[1]));								 
							 break;
						}
						
					} else if (different >= 1 && different < 7) {							 
						
						if (sal[0] != null && SearchUtils.asLong(sal[0]) == 2) {
							 this.adver.setSalary(SearchUtils.asLong(sal[1]));								 
							 break;
						}							
					
					} else  if (different >= 7) {							 
						
						if (sal[0] != null && SearchUtils.asLong(sal[0]) == 3) {
							this.adver.setSalary(SearchUtils.asLong(sal[1]));								 
							break;
						}							
					}
				}								
			}
		}
	}
	
	/** Метод за презичисление на минималната запалата, ако се смени години професионален опит
	 * 
	 * 
	 */
	public void actionChangeExperience() {
		
		checkMinSalary(this.level, this.experience, this.adver.getExperience());
	}
	
	/** Метод за презичисление или зачистване на минималната запалата, ако се смени длъжностното ниво
	 * 
	 * 
	 */
	public void actionChangeLevel() {
		
		if (this.level == this.adver.getLevel()) {
			
			checkMinSalary(this.level, this.experience, this.adver.getExperience());
						
		} else {
		
			this.adver.setSalary(null);
		}
	}
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Advertisement getAdver() {
		return adver;
	}

	public void setAdver(Advertisement adver) {
		this.adver = adver;
	}
	
	public String getAdminZvenoText() {
		return adminZvenoText;
	}

	public void setAdminZvenoText(String adminZvenoText) {
		this.adminZvenoText = adminZvenoText;
	}

	public Long getAdminZveno() {
		return adminZveno;
	}

	public void setAdminZveno(Long adminZveno) {
		this.adminZveno = adminZveno;
	}	

	public String getUnitText() {
		return unitText;
	}

	public void setUnitText(String unitText) {
		this.unitText = unitText;
	}

	public Long getUnit() {
		return unit;
	}

	public void setUnit(Long unit) {
		
		if (unit != null) {
		
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(" select region, municipality, town from public.jobs_administration where id = " + unit); 
			
			@SuppressWarnings("unchecked")
			List<Object[]> obj = (ArrayList<Object[]>) query.getResultList();		
		
			for (Object[] location : obj) {
				
				if (location[0] != null) {
					this.adver.setLocationRegion(SearchUtils.asLong(location[0]));					
				}
				
				if (location[1] != null) {
					this.adver.setLocationMunicipality(SearchUtils.asLong(location[1]));
				}
				
				if (location[2] != null) {
					this.adver.setLocationTown(SearchUtils.asLong(location[2]));
				}			
			}
			
			getParentDirAdmStruc(unit.longValue());	
		
		} else {
			
			this.direkcia = "";
		}
	
		this.unit = unit;
	}

	public String getPositionText() {
		return positionText;
	}

	public void setPositionText(String positionText) {
		this.positionText = positionText;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		
		if (position != null) {
			
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(" select level_name_sc, min_qual_sc, min_rang_sc, min_exp from jobs_jobs where jobs_jobs.id = " + position); 
			
			@SuppressWarnings("unchecked")
			List<Object[]> obj = (ArrayList<Object[]>) query.getResultList();
			this.level = null;
			this.experience = null;
			
			for (Object[] data : obj) {
				
				if (data[0] != null) {
					this.adver.setLevel(SearchUtils.asLong(data[0]));
					this.level = Long.valueOf(SearchUtils.asString(data[0]));
				}
				
				if (data[1] != null) {
					this.adver.setEducationDegree(SearchUtils.asLong(data[1]));
				}
				
				if (data[2] != null) {
					this.adver.setRank(SearchUtils.asLong(data[2]));
				}

				if (data[3] != null) {					
					this.experience = Long.valueOf(SearchUtils.asString(data[3])) + 1;					
					this.adver.setExperience(this.experience);					
				}
				
				checkMinSalary(this.level, this.experience, null);				
			}
		
		} else {
			
			this.adver.setLevel(null);
			this.adver.setEducationDegree(null);
			this.adver.setRank(null);
			this.adver.setExperience(null);
			this.adver.setSalary(null);
			this.level = null;
			this.experience = null;
		}
		
		this.position = position;
	}

	public List<SystemClassif> getOblastList() {
		
		if(this.oblastList == null){
			this.oblastList = new ArrayList<SystemClassif>();
			filEkateList(this.oblastList, 0L);
		}
		return oblastList;
	}

	public void setOblastList(List<SystemClassif> oblastList) {
		this.oblastList = oblastList;
	}

	public List<SystemClassif> getObshtiniList() {
		return obshtiniList;
	}

	public void setObshtiniList(List<SystemClassif> obshtiniList) {
		this.obshtiniList = obshtiniList;
	}

	public List<SystemClassif> getNasMestoList() {
		return nasMestoList;
	}

	public void setNasMestoList(List<SystemClassif> nasMestoList) {
		this.nasMestoList = nasMestoList;
	}	
	
	public List<SystemClassif> getLocOblastList() {
		if(this.locOblastList == null){
			this.locOblastList = new ArrayList<SystemClassif>();
			filEkateList(this.locOblastList, 0L);
		}
		return locOblastList;
	}

	public void setLocOblastList(List<SystemClassif> locOblastList) {
		this.locOblastList = locOblastList;
	}

	public List<SystemClassif> getLocObshtiniList() {
		return locObshtiniList;
	}

	public void setLocObshtiniList(List<SystemClassif> locObshtiniList) {
		this.locObshtiniList = locObshtiniList;
	}

	public List<SystemClassif> getLocNasMestoList() {
		return locNasMestoList;
	}

	public void setLocNasMestoList(List<SystemClassif> locNasMestoList) {
		this.locNasMestoList = locNasMestoList;
	}

	public Files getFile() {
		return file;
	}

	public void setFile(Files file) {
		this.file = file;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public List<Files> getDeleteFilesList() {
		return deleteFilesList;
	}

	public void setDeleteFilesList(List<Files> deleteFilesList) {
		this.deleteFilesList = deleteFilesList;
	}
	
	public String getDirekcia() {
		return direkcia;
	}

	public void setDirekcia(String direkcia) {
		this.direkcia = direkcia;
	}
	
	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getExperience() {
		return experience;
	}

	public void setExperience(Long experience) {
		this.experience = experience;
	}
	
	// ************************************************* END GET & SET ***************************************************** //	
	
	// ************************************************* EKATTE ************************************************************ //	

	/** Метод за зареждане на общините при избор на област за адм. звено
	 * 
	 * 
	 */
	public void actionChangeOblast(){
		
		if(this.adver.getRegion() != null){
			filEkateList(this.obshtiniList, this.adver.getRegion());
		
		} else {
			this.obshtiniList.clear();
			this.adver.setMunicipality(null);
		}
		
		this.nasMestoList.clear();
		this.adver.setTown(null);
	}
	
	/** Метод за зареждане на общините при избор на област за мястото за подаване на документи
	 * 
	 * 
	 */
	public void actionChangeLocOblast(){
		
		if(this.adver.getLocationRegion() != null){
			filEkateList(this.locObshtiniList, this.adver.getLocationRegion());
		
		} else {
			this.locObshtiniList.clear();
			this.adver.setLocationMunicipality(null);
		}
		
		this.locNasMestoList.clear();
		this.adver.setLocationTown(null);
	}
	
	/** Метод за зареждане на населените места при избор на община за адм. звено
	 * 
	 * 
	 */
	public void actionChangeObshtina(){
		
		if(this.adver.getMunicipality() != null){
			filEkateList(this.nasMestoList, this.adver.getMunicipality());
		
		} else {
			this.nasMestoList.clear();
			this.adver.setTown(null);
		}		
	}
	
	/** Метод за зареждане на населените места при избор на община за мястото за подаване на документи
	 * 
	 * 
	 */
	public void actionChangeLocObshtina(){
		
		if(this.adver.getLocationMunicipality() != null){
			filEkateList(this.locNasMestoList, this.adver.getLocationMunicipality());
		
		} else {
			this.locNasMestoList.clear();
			this.adver.setLocationTown(null);
		}
	}
	
	/** Метод за измъкване на системната класификация и зареждането на списъците с област, община и населени места
	 * 
	 * 
	 * @param ekateList
	 * @param code
	 */
	public void filEkateList(List<SystemClassif> ekateList, Long code) {
		
		ekateList.clear();
		
		if(code == null){
			return;
		}
		
		try {
			
			ekateList.addAll(getSystemData().getChildrenOnNextLevel(Constants.CODE_SYSCLASS_EKATTE, code.longValue(), new Date(), getCurrentLang(), userId));
		
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		
		} finally {
			JPA.getUtil().closeConnection();
		}
		
	}
		
	// ************************************************* END EKATTE ******************************************************** //
	
	// ************************************************* FILES ************************************************************* //
	
		/** Избор на файлове за прикачване към събитието
		 * 
		 * @param event
		 */
		public void uploadFileListener(FileUploadEvent event){		
			
			try {
				
				UploadedFile upFile = event.getFile();
				
				Files fileObject = new Files();
				fileObject.setFilename(upFile.getFileName());
				fileObject.setContentType(upFile.getContentType());
				fileObject.setContent(upFile.getContent());	
				
				this.filesList.add(fileObject);
			
			} catch (Exception e) {
				LOGGER.error("Exception: " + e.getMessage());	
			} 
		}
			
		/** Извличане от БД на запазените файлове към събитието
		 * 
		 * @param file
		 */
		public void download(Files file){ 
			
			boolean ok = true;
			
			if(file.getContent() == null && file.getId() != null) {
				
				try {					
					
					file = this.filesDAO.findById(file.getId());
					
					if(file.getPath() != null && !file.getPath().isEmpty()){
						Path path = Paths.get(file.getPath());
						file.setContent(java.nio.file.Files.readAllBytes(path));
					}
				
				} catch (DbErrorException e) {
					LOGGER.error("DbErrorException: " + e.getMessage());
					ok = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
				
				} catch (IOException e) {
					LOGGER.error("IOException: " + e.getMessage());
					ok = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
					LOGGER.error(e.getMessage(), e);
				
				} catch (Exception e) {
					LOGGER.error("Exception: " + e.getMessage());
					ok = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
				
				}  finally {
					JPA.getUtil().closeConnection();
				}
			}
			
			if(ok){
				
				try {
					
					FacesContext facesContext = FacesContext.getCurrentInstance();
				    ExternalContext externalContext = facesContext.getExternalContext();
				    
				    HttpServletRequest request =(HttpServletRequest)externalContext.getRequest();
					String agent = request.getHeader("user-agent");

					
					String codedfilename = ""; 
					
					if (null != agent &&  (-1 != agent.indexOf("MSIE") || (-1 != agent.indexOf("Mozilla") && -1 != agent.indexOf("rv:11")) || (-1 != agent.indexOf("Edge"))  ) ) {
						codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
					} else if (null != agent && -1 != agent.indexOf("Mozilla")) {
						codedfilename = MimeUtility.encodeText(file.getFilename(), "UTF8", "B");
					} else {
						codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
					}
					
					
				    externalContext.setResponseHeader("Content-Type", "application/x-download");
				    externalContext.setResponseHeader("Content-Length", file.getContent().length + "");
				    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + codedfilename + "\"");
					externalContext.getResponseOutputStream().write(file.getContent());
					
					facesContext.responseComplete();
				
				} catch (IOException e) {
					LOGGER.error("IOException: " + e.getMessage());
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
					LOGGER.error(e.getMessage(), e);
				
				} catch (Exception e) {
					LOGGER.error("Exception: " + e.getMessage());
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
				} 
			}
			
		}
			
		/** Премахва избрания файл
		 * 
		 * @param file
		 */
		public void remove(Files file){
				
			this.filesList.remove(file);
			this.deleteFilesList.add(file);			
		}		
			
		// ************************************************* END FILES ************************************************************* //
		
}
