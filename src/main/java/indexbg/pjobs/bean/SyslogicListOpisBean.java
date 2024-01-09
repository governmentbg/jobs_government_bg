package indexbg.pjobs.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.indexbg.system.db.dao.SyslogicListDAO;
import com.indexbg.system.db.dao.SyslogicListOpisDAO;
import com.indexbg.system.db.dao.SystemClassifDAO;
import com.indexbg.system.db.dao.SystemClassifOpisDAO;
import com.indexbg.system.db.dto.LogicalListContainer;
import com.indexbg.system.db.dto.SyslogicListEntity;
import com.indexbg.system.db.dto.SyslogicListOpisEntity;
import com.indexbg.system.db.dto.SystemClassif;
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
@Named("syslogicListOpis")
@ViewScoped
public class SyslogicListOpisBean extends PJobsBean {

	private static final long serialVersionUID = -4102220438148710054L;
	static final Logger LOGGER = LoggerFactory.getLogger(SyslogicListOpisBean.class);
	
	private String nameLogicList;
	private List<SyslogicListOpisEntity> listSc;
	private SyslogicListOpisEntity sysOpis;
	private boolean visible = true;
	private List<SystemClassif> sysLangs;
	private boolean multipleLang;
	private HashMap<Long, String> textNode;
	private String importListName;
	private LogicalListContainer logicalListContainer;
	private SyslogicListOpisDAO syslogicListOpisDAO;
	private Long lang;
	private Long userId;
	private List<SystemClassifOpis> systemClassifOpises = null;
	private HashMap<Long, String> classifOpis = null;

	private Long pageHidden;
	
	 @PostConstruct
	 public void init() {
		 LOGGER.debug("PostConstruct!!!!");
		 LOGGER.info("PostConstruct!!!!");

		 try {

			 this.userId = getUserData().getUserId();
			 syslogicListOpisDAO = new SyslogicListOpisDAO(userId);
			 clearDataOpis();
			 actionSearch();

			 sysLangs = new SystemClassifDAO(userId).filterByCodeClassif(Constants.CODE_CLASSIF_LANG, getLang());
			 // проверяваме за многоезичност
			 multipleLang = sysLangs.size() > 1;
		 }
		 catch (DbErrorException e) {
			 LOGGER.error(e.getMessage(), e);
			 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		 }
		 catch (ObjectNotFoundException e) {
			 this.userId = -1L;
		 }
		 catch (Exception e) {
			 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
			 LOGGER.error(e.getMessage(), e);
		 }
		 finally {
			 JPA.getUtil().closeConnection();
		 }
	 }
	
	 public void actionSearch() {
        try {
            listSc = new SyslogicListOpisDAO(userId).filterByName(nameLogicList, getCurrentLang());
        }
        catch(DbErrorException e) {
            LOGGER.error(e.getMessage(), e);
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
        }
        finally {
            JPA.getUtil().closeConnection();
        }
    }
	 
	public void changePage() {
			
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
			
		session.removeAttribute("logicListPage");
		
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("logicListForm:listSc");
		
		if(d != null) { 
			addSessionScopeAttribute("logicListPage", d.getFirst());		
		}		
	}
	 
	public void actionNewOpis() {
		setVisible(true);
		clearDataOpis();
	}
	
	private void clearDataOpis() {
        sysOpis = new SyslogicListOpisEntity();
        textNode = null;
    }
	
	public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        file.getContent();
        importListName = "";
        try {
            logicalListContainer = syslogicListOpisDAO.unmarshallLogicalList(new String(file.getContent(), "UTF-8"));
            SyslogicListOpisEntity systemClassifOpis = logicalListContainer.getList();
            if (!isExistList(systemClassifOpis.getId())) {
                JSFUtils.addGlobalMessage(
                		FacesMessage.SEVERITY_ERROR, 
                		getMessageResourceString(
                				Constants.beanMessages, 
                				"syslogic.listNotExist", 
                				systemClassifOpis.getId().toString()));
                return;
            }
            importListName += " N: " + systemClassifOpis.getId() + " / ";
            for (Multilang ml : systemClassifOpis.getTranslations()) {
                if (ml.getLang() == lang.longValue()) {
                    importListName += ml.getTekst() + " <br/>";
                    break;
                }
            }
            // RequestContext.getCurrentInstance().execute("PF('modalImportClassif').show()");
            PrimeFaces.current().executeScript("PF('modalImportClassif').show()");
            
        }
        catch (UnsupportedEncodingException e) {
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unEncodingException"));
            LOGGER.error(e.getMessage(), e);
        }
        catch (JAXBException e) {
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.JAXBException"));
            LOGGER.error(e.getMessage(), e);
        }
        catch (Exception e) {
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.JAXBException"));
            LOGGER.error(e.getMessage(), e);
        }
    }
	
	private boolean isExistList(Long codeClassif) {
        for (SyslogicListOpisEntity sco : listSc) {
            if (sco.getId().equals(codeClassif)) {
                return true;
            }
        }
        return false;
    }
	
	public void actionEditOpis() {
        setVisible(false);
        String codeClassif = JSFUtils.getRequestParameter("idObj");
        if (codeClassif != null && !codeClassif.trim().isEmpty()) {
            try {
                sysOpis = syslogicListOpisDAO.findById(Long.valueOf(codeClassif));
                textNode.clear();
                for (SystemClassif tmp : sysLangs) {
                    String txt = "";
                    for (Multilang item : sysOpis.getTranslations()) {
                        if (item.getLang() == tmp.getCode()) {
                            txt = item.getTekst();
                            break;
                        }
                    }
                    textNode.put(tmp.getCode(), txt);
                }
            }
            catch (DbErrorException e) {
                LOGGER.error(e.getMessage(), e);
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
            }
            finally {
                JPA.getUtil().closeConnection();
            }
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.succesUbdateMsg"));
        }
    }
	
	public void exportList() {
        String codeClassif = JSFUtils.getRequestParameter("idObj");
        if (codeClassif != null && !codeClassif.trim().isEmpty()) {
            Long codeCl = Long.valueOf(codeClassif);
            String message = null;
            try {
                message = syslogicListOpisDAO.exportLogicalList(codeCl, getSystemData(), userId);
            }
            catch (DbErrorException e) {
                LOGGER.error(e.getMessage(), e);
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
            }
            catch (JAXBException e) {
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.JAXBException"));
                LOGGER.error(e.getMessage(), e);
            }
            finally {
                JPA.getUtil().closeConnection();
            }
            
            if (message != null && !message.isEmpty()) {
                try {
                    String filename = URLEncoder.encode("syslogicList_N_" + codeClassif + ".xml", "UTF-8");
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = facesContext.getExternalContext();
                    externalContext.setResponseHeader("Content-Type", "application/xml");
                    externalContext.setResponseHeader("Content-Length", message.getBytes().length + "");
                    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
                    externalContext.getResponseOutputStream().write(message.getBytes());
                    facesContext.responseComplete();
                }
                catch (Exception e) {
                    JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }
	
	public void actionDeleteList(Long index) {
		
		if(index!=null ) {
			
	        try {
	        	
	            JPA.getUtil().begin();
	            SyslogicListDAO logicListDAO = new SyslogicListDAO(userId);
	            List<SyslogicListEntity> syslogicLists = logicListDAO.filterByName(listSc.get((int) (long) index).getId());
	            for (SyslogicListEntity entity : syslogicLists) {
	                logicListDAO.deleteById(entity.getId());
	            }
	            syslogicListOpisDAO.deleteById(listSc.get((int) (long) index).getId());
	            JPA.getUtil().commit();
	            getSystemData().reloadList(listSc.get((int) (long) index).getId());
	        }
	        catch (DbErrorException e) {
	            LOGGER.error(e.getMessage(), e);
	            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
	            JPA.getUtil().rollback();
	        }
	        catch (ObjectInUseException e) {
	            LOGGER.error(e.getMessage(), e);
	            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectInUse"));
	            JPA.getUtil().rollback();
	        }
	        finally {
	            JPA.getUtil().closeConnection();
	        }
	        actionSearch();
	        JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.succesDeleteMsg"));
		}
    }
	
	public void actionSaveOpis() {
        boolean flag = true;
        if (sysOpis.getCodeClassifVod() == 0) {
            JSFUtils.addMessage("scForm:codeClassifVod", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsert", getMessageResourceString("labels", "general.metotDinamicClass")));
            flag = false;
        }
        if (sysOpis.getCodeClassifPod() == 0) {
            JSFUtils.addMessage("scForm:codeClassifPod", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsert", getMessageResourceString("labels", "general.metotDinamicClass")));
            flag = false;
        }
        int i = 0;
        for (Map.Entry<Long, String> entry : textNode.entrySet()) {
            if (entry.getKey() == null || entry.getValue().trim().isEmpty()) {
                JSFUtils.addMessage("scForm:opisNameTbl:" + i + ":nameItem", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsert", getMessageResourceString("labels", "general.name")));
                flag = false;
            }
            i++;
        }
        if (flag) {
            try {
                for (Map.Entry<Long, String> entry : textNode.entrySet()) {
                    Long keyLang = entry.getKey();
                    String nodeValue = entry.getValue();
                    Multilang ml = new Multilang();
                    ml.setLang(keyLang);
                    ml.setTekst(nodeValue);
                    sysOpis.getTranslations().add(ml);
                }
                JPA.getUtil().begin();
                syslogicListOpisDAO.save(sysOpis);
                JPA.getUtil().commit();
                getSystemData().reloadList(sysOpis.getId());
            }
            catch (DbErrorException e) {
                JPA.getUtil().rollback();
                LOGGER.error(e.getMessage(), e);
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
            }
            catch (Exception e) {
                JPA.getUtil().rollback();
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
                LOGGER.error(e.getMessage(), e);
            }
            finally {
                JPA.getUtil().closeConnection();
            }
            actionSearch();
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.succesSaveMsg"));
        }

        PrimeFaces.current().ajax().addCallbackParam("validOpisClassif", flag);
    }
	
	public void importlogicalList() {
        if (logicalListContainer != null) {
            try {
                JPA.getUtil().begin();
                syslogicListOpisDAO.importLogicalList(logicalListContainer, getSystemData(), userId);
                JPA.getUtil().commit();
            }
            catch (DbErrorException e) {
                JPA.getUtil().rollback();
                LOGGER.error(e.getMessage(), e);
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
            }
            catch (JAXBException e) {
                JPA.getUtil().rollback();
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.JAXBException"));
                LOGGER.error(e.getMessage(), e);
            }
            catch (Exception e) {
                JPA.getUtil().rollback();
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
                LOGGER.error(e.getMessage(), e);
            }
            finally {
                JPA.getUtil().closeConnection();
            }
        }
        else {
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
            LOGGER.error("logicalListContainer is null");
        }
    }
	
	
	 /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	 
	
	public Long getPageHidden() {
		if(pageHidden == null) {
			pageHidden = 1L;
			if(getSessionScopeValue("logicListPage") != null) {
				DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("logicListForm:listSc");
				if(d != null) { 
					int page = (int) getSessionScopeValue("logicListPage");
					System.out.println("SETTING PAGE " + page);
					d.setFirst(page); 
				}
			}
		}
	
		return pageHidden;
	}
	
	public List<SystemClassifOpis> getClasifs() {
        if (systemClassifOpises == null) {
            try {
                systemClassifOpises = new SystemClassifOpisDAO(userId).filterClassifForLogicOpis(getCurrentLang());
                classifOpis = new HashMap<>();
                for (SystemClassifOpis s : systemClassifOpises) {
                    classifOpis.put(s.getId(), s.getTekst());
                }
            } catch (DbErrorException e) {
                LOGGER.error(e.getMessage(), e);
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
            } finally {
                JPA.getUtil().closeConnection();
            }
        }
        return systemClassifOpises;
    }	 

 	public void setPageHidden(Long pageHidden) {
		this.pageHidden = pageHidden;
	}
	
	public String getNameLogicList() {
        return nameLogicList;
    }

    public void setNameLogicList(String nameLogicList) {
        this.nameLogicList = nameLogicList;
    }
    
    public List<SyslogicListOpisEntity> getListSc() {
        return listSc;
    }

    public void setListSc(List<SyslogicListOpisEntity> listSc) {
        this.listSc = listSc;
    }
    
    public SyslogicListOpisEntity getSysOpis() {
        return sysOpis;
    }

    public void setSysOpis(SyslogicListOpisEntity sysOpis) {
        this.sysOpis = sysOpis;
    }
    
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void setSysLangs(List<SystemClassif> sysLangs) {
        this.sysLangs = sysLangs;
    }

    public List<SystemClassif> getSysLangs() {
        return sysLangs;
    }

	public boolean isMultipleLang() {
		return multipleLang;
	}

	public void setMultipleLang(boolean multipleLang) {
		this.multipleLang = multipleLang;
	}
	
	public HashMap<Long, String> getTextNode() {
        if (textNode == null) {
            textNode = new HashMap<Long, String>();
            for (SystemClassif tmp : getSysLangs()) {
                textNode.put(tmp.getCode(), "");
            }
        }
        return textNode;
    }

    public void setTextNode(HashMap<Long, String> textNode) {
        this.textNode = textNode;
    }
    
    public String getImportListName() {
        return importListName;
    }

    public void setImportListName(String importListName) {
        this.importListName = importListName;
    }
    
    public Long getLang() {
        if (lang == null) {
            lang = getCurrentLang();
        }
        return lang;
    }

    public void setLang(Long lang) {
        this.lang = lang;
    }
}
