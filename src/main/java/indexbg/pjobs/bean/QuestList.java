package indexbg.pjobs.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.primefaces.component.datatable.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;
import indexbg.pjobs.db.dao.QuestionsDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("questDataList")
@ViewScoped
public class QuestList extends PJobsBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1095081126611937076L;

	/**
	 * Основен javaBean клас за търсене на въпроси по зададени критерии за търсене и 
	 * за обръщане към javaBean клас за въвеждане/актуализация
	 */

	static final Logger LOGGER = LoggerFactory.getLogger(QuestList.class);
	private Long idUser=null;
	private String grupaText=null;
	private Long codeGrupa=null;
	private Boolean codeStatus=null;
	private String questF=null;
	private LazyDataModelSQL2Array questListT = null;


	/**
	 * Инициира/сетва първоначалните стойности на атрибутите на филтъра за търсене. Чете предадените параметри от други екрани
	 */
	@PostConstruct
	public void initData(){
		try{
			this.idUser=getUserData().getUserId();
			
			@SuppressWarnings("unchecked")
			Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("questListFindAttr");	
			if(params != null){
				if (null!=params.get("codeGrupa"))
					this.codeGrupa=(Long)params.get("codeGrupa");
				if (null!=params.get("codeStatus")) {
					this.codeStatus=(Boolean)params.get("codeStatus");
				}else {
					this.setCodeStatus(null);
				}
				if (null!=params.get("questF"))
					this.questF=(String)params.get("questF");
				
				if (null!=getSessionScopeValue("grupaText"))
					this.grupaText = (String) getSessionScopeValue("grupaText");
		
				actionFind();
				
				if(getSessionScopeValue("questListPage") != null) {
					
					DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formQuestList:tableQuest");
					
					if(d != null) { 					
						int page = (int) getSessionScopeValue("questListPage");
						d.setFirst(page); 
					}
				}
	
			}
		
		}catch (ObjectNotFoundException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.objectNotFound"), e.getMessage());
			LOGGER.error(e.getMessage(), e);
			this.idUser = -1L;
		}catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		} finally {
			JPA.getUtil().closeConnection();
		}	
	
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	
	public Date getCurrentDate() {
		return new Date();
	}

	
	/**Изтрива стойностите на филтъра за търсене на въпроси
	 * 
	 */
	public void actionClear(){
		this.setGrupaText(null);
		this.setCodeGrupa(null);
		this.setCodeStatus(null);
		this.setQuestF(null);
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("questListPage");
		session.removeAttribute("questListFindAttr");
		session.removeAttribute("grupaText");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formQuestList:tableQuest");		
		d.setFirst(0); 

	}
	
	
	/** Метод за търсене в БД на въпроси по зададените критерии/филтър 
	 * 
	 */
	public void actionFind(){
		 	
		this.setQuestListT(null);

		SelectMetadata smd = null;
		
		QuestionsDAO questDao = new QuestionsDAO(this.idUser);
		
		try {
			smd = questDao.findQuestFilter(this.getCodeGrupa(), this.getCodeStatus(), this.getQuestF(),  getCurrentLang());
			String sortCol = "A1";
			this.questListT = questDao.newLazyDataModel(smd, sortCol);
			this.questListT.getRowCount();
		
			
			Map<String, Object> params = new HashMap<String, Object>();
			if (this.getCodeGrupa() != null) {
				params.put("codeGrupa", this.codeGrupa);	
			}
			
			if (null!=this.getCodeStatus() && this.getCodeStatus()) {
				params.put("codeStatus", this.codeStatus);
			}	
			
			if (this.getQuestF() != null) {
				params.put("questF", this.questF);
			}
			
			addSessionScopeAttribute("questListFindAttr", params);
			
			if (null!=this.getGrupaText())
				addSessionScopeAttribute("grupaText", this.getGrupaText());
			
		
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
		}catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		} finally {
			JPA.getUtil().closeConnection();
		}
		return;
	}

	public String getGrupaText() {
		return grupaText;
	}

	public void setGrupaText(String grupaText) {
		this.grupaText = grupaText;
	}

	
	public String getQuestF() {
		return questF;
	}

	public void setQuestF(String questF) {
		this.questF = questF;
	}

	
	public LazyDataModelSQL2Array getQuestListT() {
		return questListT;
	}

	public void setQuestListT(LazyDataModelSQL2Array questListT) {
		this.questListT = questListT;
	}

	public Long getCodeGrupa() {
		return codeGrupa;
	}

	public void setCodeGrupa(Long codeGrupa) {
		this.codeGrupa = codeGrupa;
	}

	public Boolean getCodeStatus() {
		return codeStatus;
	}

	public void setCodeStatus(Boolean codeStatus) {
		this.codeStatus = codeStatus;
	}

	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("questListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formQuestList:tableQuest");
		
		if(d != null) { 
			
			addSessionScopeAttribute("questListPage", d.getFirst());		
		}		
	}	
	
}
