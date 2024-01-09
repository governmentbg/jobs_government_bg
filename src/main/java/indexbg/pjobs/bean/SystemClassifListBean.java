package indexbg.pjobs.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dao.SystemClassifDAO;
import com.indexbg.system.db.dao.SystemClassifOpisDAO;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.db.dto.SystemClassifContainer;
import com.indexbg.system.db.dto.SystemClassifOpis;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectInUseException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.Multilang;

import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * Пренесена версия на страницата от CAdmin
 * @author n.kanev
 */
@Named("systemClassifList")
@ViewScoped
public class SystemClassifListBean extends PJobsBean {

	private static final long serialVersionUID = 6770015522867615625L;
	static final Logger LOGGER = LoggerFactory.getLogger(SystemClassifListBean.class);
	private static final String ATTRIBUTES = "systemClassifListAttr";
	private static final String ATTR_TEKST = "tekst";
	private static final String ATTR_AIS = "ais";
	
	private Long userId;
	
	private String tekst;
	private Long ais;
	private List<SystemClassifOpis> classifList;
	private SystemClassifOpisDAO sysClassifOpisDao;
	private SystemClassifOpis sysClassifOpis;
	private HashMap<Long, String> textNode;
	private List<SystemClassif> sysLangs;
	private boolean multiLang;
	private ClassifType typeClassif;
	private List<SystemClassifOpis> listClasicClassif;
	private String customMess;
	private String importClassifName;
	private SystemClassifContainer scc;
	
	private Long pageHidden;
	
	@PostConstruct
	public void init() {
		
		try {
			this.userId = getUserData().getUserId();
		}
		catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;
		}
		
		this.classifList = null;
		this.sysClassifOpisDao = new SystemClassifOpisDAO(userId);
		this.sysClassifOpis = new SystemClassifOpis();
		this.textNode = new HashMap<Long, String>();
		
		try {
			this.sysLangs = new SystemClassifDAO(userId).filterByCodeClassif(Constants.CODE_CLASSIF_LANG, getCurrentLang());
			multiLang = sysLangs.size() > 1;
		}
		catch (DbErrorException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			return;
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue(ATTRIBUTES);
		if(params != null) {
			try {
				this.tekst = (String) params.get(ATTR_TEKST);
				this.ais = (Long) params.get(ATTR_AIS);
				actionSearch();
			}
			catch(NullPointerException e) { }
		}
		
	}
	
	public void actionClear() {
		this.tekst = null;
		this.ais = null;
		this.classifList = null;
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute(ATTRIBUTES);
	}
	
	public void actionSearch() {
		
		try {
			this.classifList = this.sysClassifOpisDao.filterByName(this.tekst.trim(), false, getCurrentLang(), this.ais);
		}
		catch (DbErrorException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		if(tekst != null) {
			params.put(ATTR_TEKST, tekst.trim());
		}
		if(ais != null) {
			params.put(ATTR_AIS, ais);
		}
		addSessionScopeAttribute(ATTRIBUTES, params);
	}

	public void actionEditOpis(Long idClassif) {
		try {
			this.sysClassifOpis = this.sysClassifOpisDao.findById(Long.valueOf(idClassif));
			
			this.typeClassif = ClassifType.NORMAL;
			if(this.sysClassifOpis.isDynamic()) {
				this.typeClassif = ClassifType.DYNAMIC;
			} 
			else if(this.sysClassifOpis.isLogical()) {
				this.typeClassif = ClassifType.LOGICAL;
			}
			
			getTextNode().clear();
			
			for(SystemClassif tmp : this.sysLangs) {
				
				String txt ="";
				for(Multilang item : this.sysClassifOpis.getTranslations()){
					if(item.getLang() == tmp.getCode()){
						txt = item.getTekst();
						break;
					}
				}	
				
				getTextNode().put(tmp.getCode(), txt);
			}
				
		}
		catch (DbErrorException e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		}
		finally {
			JPA.getUtil().closeConnection();
		}	
	}
	
	private void clearDataOpis() {
		
		this.typeClassif = ClassifType.NORMAL;
		
		this.sysClassifOpis = new SystemClassifOpis();
		this.sysClassifOpis.setDynamic(false);
		this.sysClassifOpis.setDynamicMethod(null);
		this.sysClassifOpis.setLogical(false);
		this.sysClassifOpis.setLogicalSourceId(null);
		this.sysClassifOpis.setExternal(false);
		this.sysClassifOpis.setUserEditable(true);
		this.sysClassifOpis.setCacheStartup(true);
		this.sysClassifOpis.setLinear(true);

		this.textNode = null;
	}
	
	public void actionNewOpis(){
		clearDataOpis();
	}

	public void actionSaveOpis(){
		
		boolean flag = true;

		if(typeClassif == ClassifType.DYNAMIC){
			if(this.sysClassifOpis.getDynamicMethod() == null || this.sysClassifOpis.getDynamicMethod().trim().isEmpty()){
				JSFUtils.addMessage("scForm:dynamicMethod",
						FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(
								Constants.beanMessages,
								"general.pleaseInsert",
								getMessageResourceString("labels", "general.metotDinamicClass")));
				flag = false;
			}
		}
		else if(typeClassif == ClassifType.LOGICAL){
			if(this.sysClassifOpis.getLogicalSourceId() == null){
				JSFUtils.addMessage("scForm:logicalSourceId", 
							FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(
									Constants.beanMessages, 
									"general.pleaseSelect", 
									getMessageResourceString("labels", "general.classIztLogic")));
				flag = false;
			}
		}
		
		int i = 0;
		for(Long key : textNode.keySet()){
			if(textNode.get(key) == null || textNode.get(key).trim().isEmpty()){
				JSFUtils.addMessage("scForm:opisNameTbl:" + i + ":nameItem",
						FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(
								Constants.beanMessages,
								"general.pleaseInsert",
								getMessageResourceString("labels", "general.name")));
				flag = false;
			}
			i++;
		}
		
		if(this.sysClassifOpis.getAisId() == null){
			JSFUtils.addMessage("scForm:aisId",
					FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(
							Constants.beanMessages,
							"general.pleaseSelect",
							getMessageResourceString("labels", "general.ais")));
			flag = false;
		}
		
		if(!this.sysClassifOpis.isExternal()){
			this.sysClassifOpis.setNumCodeExt(false);
		}
		
		if(flag){
			
			try {
				//tipa na klasifikaciyata
				if(typeClassif == ClassifType.NORMAL){ //classic
					this.sysClassifOpis.setDynamic(false);
					this.sysClassifOpis.setDynamicMethod(null);
					this.sysClassifOpis.setLogical(false);
					this.sysClassifOpis.setLogicalSourceId(null);
				}
				else if(typeClassif == ClassifType.DYNAMIC){ //dynamic
					this.sysClassifOpis.setDynamic(true);
					this.sysClassifOpis.setLogical(false);
					this.sysClassifOpis.setLogicalSourceId(null);
				}
				else if(typeClassif == ClassifType.LOGICAL){ //logical
					this.sysClassifOpis.setLogical(true);
					this.sysClassifOpis.setDynamic(false);
					this.sysClassifOpis.setDynamicMethod(null);
				}
				
				//zapalvam ezicita
				for (Map.Entry<Long, String> entry : textNode.entrySet()) {
					Long keyLang = entry.getKey();
					String nodeValue = entry.getValue();
		        	
		        	Multilang ml = new Multilang();
		        	ml.setLang(keyLang);
		        	ml.setTekst(nodeValue);
		        	
		        	this.sysClassifOpis.getTranslations().add(ml);
		        }
				
	        	JPA.getUtil().begin();
	        	this.sysClassifOpisDao.save(this.sysClassifOpis);
	        	JPA.getUtil().commit();
	        	
	        	getSystemData().reloadClassif(this.sysClassifOpis.getId());
	        	
	        	//---------------------------------------------------
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,getMessageResourceString(Constants.beanMessages,"general.successMsg"));
	        }
			catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(),e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
			}
			catch (Exception e) {
				JPA.getUtil().rollback();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			}
			finally {
				JPA.getUtil().closeConnection();
			}
		
			//reload classif list
			actionSearch();
		}
		
//		RequestContext context = RequestContext.getCurrentInstance();
//		context.addCallbackParam("validOpisClassif", flag);
		PrimeFaces.current().ajax().addCallbackParam("validOpisClassif", flag);
	}
	
	public void actionDeleteClassif(Long idClassif, int index){
		
		try {
			JPA.getUtil().begin();				
			this.sysClassifOpisDao.deleteById(idClassif);
			JPA.getUtil().commit();
			this.classifList.remove(index);
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,getMessageResourceString(Constants.beanMessages,"general.successDeleteMsg"));
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		} catch (ObjectInUseException e) {
			
        	JPA.getUtil().rollback();
            
            String mess = e.getMessage();
			
			String [] classifOrLogicList = mess.split("!");
			
			if(classifOrLogicList.length > 0){
			
				StringBuilder sb = new StringBuilder();
				
				try {
					sb.append(" <br/>");
					
					String[] keyVall = classifOrLogicList[0].split(",");
					for(int i = 0; i < keyVall.length; i++){
						sb.append(getMessageResourceString(Constants.LABELS, "general.classif"));
						sb.append(": ");
						sb.append(getSystemData().getNameClassification(Long.valueOf(keyVall[i]), getCurrentLang()));
						sb.append(" <br/>");
					}
					
					if(classifOrLogicList.length > 1) {
						String[] keyVall1 = classifOrLogicList[1].split(",");
						for(int i = 0; i < keyVall1.length; i++){
							sb.append(" <br/>");
							sb.append(getMessageResourceString(Constants.LABELS, "syslogic.name"));
							sb.append(": ");
							sb.append(getSystemData().getNameList(Long.valueOf(keyVall1[i]), getCurrentLang()));
						}
						
					}
				}
				catch (DbErrorException e1) {
					LOGGER.error(e.getMessage(),e1);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
				}
				
				setCustomMess(sb.toString());
				
				PrimeFaces.current().executeScript("PF('modalDeleteMess').show()");
			}
		}
		catch (Exception e) {
			JPA.getUtil().rollback();
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		}
		finally {
			JPA.getUtil().closeConnection();
		} 
	}
	
	public void exportClassif(Long idClassif){
		
		Long codeCl = Long.valueOf(idClassif);
		String message =null;
		
		try {
			message = this.sysClassifOpisDao.exportSystemClassif(codeCl, getSystemData(), this.userId);
		}
		catch (DbErrorException e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		}
		catch (JAXBException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.JAXBException"));
			LOGGER.error(e.getMessage(), e);
		}
		finally {
			JPA.getUtil().closeConnection();
		}
		
		
		if(message != null && !message.isEmpty()) {
			try {
				String filename = URLEncoder.encode("classif_N_" + idClassif + ".xml", "UTF-8");
		    	
		    	FacesContext facesContext = FacesContext.getCurrentInstance();
			    ExternalContext externalContext = facesContext.getExternalContext();
			    externalContext.setResponseHeader("Content-Type", "application/xml");
			    externalContext.setResponseHeader("Content-Length", message.getBytes().length + "");
			    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
				externalContext.getResponseOutputStream().write( message.getBytes());
				facesContext.responseComplete();
			}
			catch (Exception e) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		
		UploadedFile file = event.getFile();
        file.getContent();
        
        importClassifName = "";
        
        try {
			scc = sysClassifOpisDao.unmarshallOpisClassif(new String(file.getContent(),"UTF-8"));
			for(SystemClassifOpis sco : scc.getClassifications()){
				
				if(!isExistClassif(sco.getId())){
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.classifNotExist", sco.getId().toString()));
					return;
				}
				importClassifName += " N: " + sco.getId() + " / ";
				for(Multilang ml : sco.getTranslations()) {
					if( ml.getLang() == getCurrentLang().longValue() ) {
						importClassifName += ml.getTekst() + " <br/>";
						break;
					}
				}
			}
			
			PrimeFaces.current().executeScript("PF('modalImportClassif').show()");
		}
        catch (UnsupportedEncodingException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unEncodingException"));
			LOGGER.error(e.getMessage(), e);
		}
        catch (JAXBException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.JAXBException"));
			LOGGER.error(e.getMessage(), e);
		}
        catch (Exception e){
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.JAXBException"));
			LOGGER.error(e.getMessage(), e);
		} 
        
        
    }
	
	private boolean isExistClassif(Long codeClassif) {
		for(SystemClassifOpis sco : this.classifList){
			if(sco.getId().equals(codeClassif)) {
				return true;
			}
		}
		return false;
	}
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("classifListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("systemClassifListForm:tbl");
		
		if(d != null) { 
			addSessionScopeAttribute("classifListPage", d.getFirst());		
		}		
	}
	
	public enum ClassifType {
		NORMAL, DYNAMIC, LOGICAL
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	public String getTekst() {
		return tekst;
	}

	public void setTekst(String tekst) {
		this.tekst = tekst;
	}

	public Long getAis() {
		return ais;
	}

	public void setAis(Long ais) {
		this.ais = ais;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<SystemClassifOpis> getClassifList() {
		return classifList;
	}

	public void setClassifList(List<SystemClassifOpis> classifList) {
		this.classifList = classifList;
	}
	
	public HashMap<Long, String> getTextNode() {
		if(textNode == null){
			
			textNode = new HashMap<Long, String>();
			
			for(SystemClassif tmp : getSysLangs()) {
				textNode.put(tmp.getCode(), "");
			}
		}
		return textNode;
	}

	public void setTextNode(HashMap<Long, String> textNode) {
		this.textNode = textNode;
	}
	
	public void setSysLangs(List<SystemClassif> sysLangs) {
		this.sysLangs = sysLangs;
	}

	public List<SystemClassif> getSysLangs() {
		return sysLangs;
	}

	public boolean isMultiLang() {
		return multiLang;
	}

	public void setMultiLang(boolean multiLang) {
		this.multiLang = multiLang;
	}
	
	public ClassifType getTypeClassif() {
		return typeClassif;
	}

	public void setTypeClassif(ClassifType typeClassif) {
		this.typeClassif = typeClassif;
	}

	public SystemClassifOpis getSysClassifOpis() {
		return sysClassifOpis;
	}

	public void setSysClassifOpis(SystemClassifOpis sysClassifOpis) {
		this.sysClassifOpis = sysClassifOpis;
	}
	
	public List<SystemClassifOpis> getListClasicClassif() {
		
		if(this.listClasicClassif == null) {
			try {
				this.listClasicClassif = this.sysClassifOpisDao.filterByName(null, true, getCurrentLang(), null);
			}
			catch (DbErrorException e) {
				LOGGER.error(e.getMessage(),e);
				this.listClasicClassif = new ArrayList<>();
			}
		}
		
		return listClasicClassif;
	}

	public void setListClasicClassif(List<SystemClassifOpis> listClasicClassif) {
		this.listClasicClassif = listClasicClassif;
	}
	
	public String getCustomMess() {
		return customMess;
	}

	public void setCustomMess(String customMess) {
		this.customMess = customMess;
	}
	
	public String getImportClassifName() {
		return importClassifName;
	}

	public void setImportClassifName(String importClassifName) {
		this.importClassifName = importClassifName;
	}	
	
	public Long getPageHidden() {
		if(pageHidden == null) {
			pageHidden = 1L;
			if(getSessionScopeValue("classifListPage") != null) {
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("systemClassifListForm:tbl");
				if(d != null) { 
					int page = (int) getSessionScopeValue("classifListPage");
					System.out.println("SETTING PAGE " + page);
					d.setFirst(page); 
				}
			}
		}
	
		return pageHidden;
	}

	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}
	
}
