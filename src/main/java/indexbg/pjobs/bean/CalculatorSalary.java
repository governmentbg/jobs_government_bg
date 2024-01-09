package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.PageText;
import indexbg.pjobs.db.dao.PageTextDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class CalculatorSalary extends PJobsBean {	

	/** Основен java клас за калкулатор за изчисление на заплата
	 * 
	 * 
	 */
	private static final long serialVersionUID = 7823415335244397772L;
	
	static final Logger LOGGER = LoggerFactory.getLogger(CalculatorSalary.class);
	
	private Long userId;	
	
	// Длъжност
	private String positionText;
	private Long position;
	
	//Минимален професионален опит за длъжността
	private Long minProfOpit;
	
	//Години, професионален опит
	private Long profOpit;	
	
	//Длъжностно ниво
	private Long level;
	
	//Години, професионален опит
	private Long minSalary;
		
	//Години, професионален опит
	private Long maxSalary;
	
	//Текста на инструкциите
	private String txtInstructions;	
	
	// валутата на заплатата - ще идва от системна настройка - PJOBS.CALCULO.SALARIO
	private String currence;
	
	/** Иницира първоначалните стойности на обекта
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!!");
		
		try {
			
			this.txtInstructions = "";
			
			PageText txtPages = new PageTextDAO(Constants.PORTAL_USER).loadPageTextByCodeSectLang(41L, getCurrentLang());
			
			if(txtPages.isVisibleOnSite()) {
				txtInstructions = txtPages.getPageText();
			}
			
			this.currence = getSystemData().getSettingsValue("PJOBS.CALCULO.SALARIO");
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));		
		} 	
		
	}	
	
	
	/** Метод за изчисление на минимална и максимална заплата спрямо степени и длъжностни нива
	 * 
	 * 
	 */
	public void calcLevelSalary() {
		
		boolean check = true;		
		
		if (this.position == null) {
			JSFUtils.addMessage("formCalcSalary:dlajnost", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "advertisement.dlajnost")));
			check = false;
		}
		
		if (this.profOpit == null) {
			JSFUtils.addMessage("formCalcSalary:profOpit", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "calcSalary.profOpitYears")));
			check = false;
		}		
		
		if (check) {
			
			if (this.level != null) {
				
				Query query = JPA.getUtil().getEntityManager().createNativeQuery(" select sl.salary_level,sl.min, sl.max from jobs_salary_level sl left outer join jobs_salary s on sl.salary_id = s.id where level = " + this.level); 
				
				@SuppressWarnings("unchecked")
				List<Object[]> salaryData = (ArrayList<Object[]>) query.getResultList();
				
				for (Object[] data : salaryData) {
						
					if (this.profOpit <= this.minProfOpit) {
													 
						if (data[0] != null && SearchUtils.asLong(data[0]) == 1) {
							 this.minSalary = SearchUtils.asLong(data[1]);	
							 this.maxSalary = SearchUtils.asLong(data[2]);
							 break;
						}
						
					} else if (this.profOpit > this.minProfOpit) {
						
						Long different = this.profOpit - this.minProfOpit;
						
						if (different >= 1 && different < 7) {							 
							
							if (data[0] != null && SearchUtils.asLong(data[0]) == 2) {
								 this.minSalary = SearchUtils.asLong(data[1]);	
								 this.maxSalary = SearchUtils.asLong(data[2]);
								 break;
							}							
						
						} else  if (different >= 7) {							 
							
							if (data[0] != null && SearchUtils.asLong(data[0]) == 3) {
								 this.minSalary = SearchUtils.asLong(data[1]);	
								 this.maxSalary = SearchUtils.asLong(data[2]);
								 break;
							}							
						}							
					}		
										
				}				
			}
		}
		
	}
	
	/** Метод за зачистване на полета по екрана
	 *  
	 */
	public void actionClear(){		
		
		this.position = null;
		this.positionText = "";
		this.minProfOpit = null;
		this.profOpit = null;
		this.minSalary = null;
		this.maxSalary = null;
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
			
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(" select level_name_sc, min_exp from jobs_jobs where jobs_jobs.id = " + position);
			
			@SuppressWarnings("unchecked")
			List<Object[]> positionData = (ArrayList<Object[]>) query.getResultList();
			
			for (Object[] data : positionData) {
				
				if (data[0] != null) {
					this.setLevel(SearchUtils.asLong(data[0]));					
				}
				
				System.out.println("Level >>> " + this.level);
				
				if (data[1] != null) {					
					this.setMinProfOpit(Long.valueOf(SearchUtils.asString(data[1])));
				} else {
					this.setMinProfOpit(0L);
				}
			}			
		
		} else {			
			this.setMinProfOpit(null);					
		}
		
		this.position = position;
	}

	public Long getMinProfOpit() {
		return minProfOpit;
	}

	public void setMinProfOpit(Long minProfOpit) {
		this.minProfOpit = minProfOpit;
	}

	public Long getProfOpit() {
		return profOpit;
	}

	public void setProfOpit(Long profOpit) {
		this.profOpit = profOpit;
	}

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getMinSalary() {
		return minSalary;
	}

	public void setMinSalary(Long minSalary) {
		this.minSalary = minSalary;
	}

	public Long getMaxSalary() {
		return maxSalary;
	}

	public void setMaxSalary(Long maxSalary) {
		this.maxSalary = maxSalary;
	}

	public String getTxtInstructions() {
		return txtInstructions;
	}

	public void setTxtInstructions(String txtInstructions) {
		this.txtInstructions = txtInstructions;
	}


	public String getCurrence() {
		return currence;
	}


	public void setCurrence(String currence) {
		this.currence = currence;
	}	
	
	// ************************************************* END GET & SET ***************************************************** //	
			
}
