package indexbg.pjobs.bean;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dao.SystemClassifOpisDAO;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.Multilang;
import com.indexbg.system.utils.TreeUtils;

import indexbg.pjobs.system.Constants;

/**
 * Пренесена версия на страницата от CAdmin
 * @author n.kanev
 */
@Named("systemClassifEdit")
@ViewScoped
public class SystemClassifEditBean extends SystemClassifParentBean {

	private static final long serialVersionUID = 2295420928093755036L;
	static final Logger LOGGER = LoggerFactory.getLogger(SystemClassifEditBean.class);
	
	/* идентификатор за нова позиция */
    private boolean itemNew = false;
    
    private boolean notEditable; 
    private Date dateFor;
    private Date dateEvent;
    private SystemClassif node;
	
    @PostConstruct
	public void init() {
    	//System.out.println("---> initData");
		try {
			
			codeClassif = Long.valueOf(JSFUtils.getRequestParameter("idObj"));
			
			//zarejdame opisanieto na clasifikaciyata
			setSysClassifOpis(new SystemClassifOpisDAO(getUserId()).load(codeClassif));	
			
			//namirame naimenovanieto na klasifikaciyat po tekushtiyat ezik
			for(Multilang ml : sysClassifOpis.getTranslations()){
				if(ml.getLang() == getLang().longValue()){
					textNameClassif = ml.getTekst();
					break;
				}
			}
			//zarejdame durvoto
			loadTreeData();
			
			//зареждаме езиците на които работи системата
			sysLangs = scDAO.filterByCodeClassif(Constants.CODE_CLASSIF_LANG, getLang());
			
			// проверяваме за многоезичност
			if(sysLangs.size() > 1) {
				multipleLang = true;
			}
			else {
				multipleLang = false;
			}
			
			getTextNode(); // ne e zaduljitelno da se izvikva
			
			dateFor = scDAO.findMaxDate(codeClassif);
            LOGGER.info("====>>>" + dateFor);
            
            valChoiseNew = null;
            
            dopInfoNode = "";
            extCode = "";
            notEditable = false;
            
            itemNew = true;
            
            selectedCodeItemForMove = null;
            
            
		}
		catch (DbErrorException e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		}
		catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		}
		finally {
			JPA.getUtil().closeConnection();
		}
		
	}
    
    @Override
    void loadTreeData() throws DbErrorException {
		//-зареждаме класификацията
		List<SystemClassif> listItems = scDAO.filterByCodeClassif(codeClassif, getLang());
		rootNode = new TreeUtils().loadTreeData3(listItems, "", false, false, null, null);
	}
    
    @Override
    void clearData() {
		selectedNode = null;
		itemNew = true;
    	textNode = null;
    	dopInfoNode = "";
        extCode = "";
        notEditable = false;
	}

	public void processSelection(NodeSelectEvent event) {
		//event.getTreeNode().setSelected(true);
		removeNewNode(rootNode);
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Минава през избор на значение от дървото!!!");
		}

		try {
			node = scDAO.findByCode(codeClassif.longValue(), ((SystemClassif) event.getTreeNode().getData()).getCode(), true);
			dopInfoNode = node.getDopInfo();
			extCode = node.getCodeExt();
			notEditable = node.isNoDelete();
			textNode.clear();
		
			for(SystemClassif tmp : sysLangs) {
				String txt[] = {"", ""};
				
				for(Multilang item : node.getTranslations()) {
					if(item.getLang() == tmp.getCode()){
						txt[0] = item.getTekst();
						txt[1] = item.getTekstopi();
						break;
					}
				}	
				
				textNode.put(tmp.getCode(), txt);
			}
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.invalidParameter"));
		} 
		
		itemNew = false;		
	}
	
	public void nodeExpand(NodeExpandEvent event) {
		System.out.println("nodeExpand====> " + ((SystemClassif)event.getTreeNode().getData()).getTekst());
	    event.getTreeNode().setExpanded(true);      
	}

	public void nodeCollapse(NodeCollapseEvent event) {
		System.out.println("nodeCollapse====> " + ((SystemClassif)event.getTreeNode().getData()).getTekst());
	    event.getTreeNode().setExpanded(false);     
	}
	
	public void nodeUnselect(NodeCollapseEvent event) {
	    event.getTreeNode().setSelected(false);     
	    System.out.println("nodeUnselect====> " + ((SystemClassif)event.getTreeNode().getData()).getTekst());
	}
	
	public void actionSaveDistp(ActionEvent event) {
		
		removeNewNode(rootNode);
		
		if(event != null){
			valChoiseNew = event.getComponent().getId();
			
			selectedNode.setSelected(false); // mahame selektiranoto v durvoto
				
			int selNodeIndex = selectedNode.getParent().getChildren().indexOf(selectedNode); 
			
			SystemClassif node = (SystemClassif) selectedNode.getData();
			
			this.node = new SystemClassif();
			this.node.setTekst("...............");
			this.node.setCodeClassif(codeClassif);
	        
			DefaultTreeNode tmpNew = new DefaultTreeNode(this.node);
			tmpNew.setType(NodeTypes.NEW.text);	
			if("saveBefore".equals(valChoiseNew)){
				this.node.setCodeParent(node.getCodeParent());
				this.node.setCodePrev(node.getCodePrev());
				selectedNode.getParent().getChildren().add(selNodeIndex, tmpNew);
			}
			else if("saveAfter".equals(valChoiseNew)){
				this.node.setCodeParent(node.getCodeParent());
				this.node.setCodePrev(node.getCode());
				selectedNode.getParent().getChildren().add(selNodeIndex + 1, tmpNew);
			}
			else if("saveChild".equals(valChoiseNew)){
				this.node.setCodeParent(node.getCode());
				this.node.setCodePrev(0);
				selectedNode.setExpanded(true);
				selectedNode.getChildren().add(0, tmpNew);
			}
			
			selectedNode  = tmpNew;
			
			StringBuilder sb = new StringBuilder();
		    sb.append("PrimeFaces.widgets.treeSingleWidget.selectNode(");
		    sb.append("$(\"#scForm\\\\:systemTree\\\\:");
		    sb.append(tmpNew.getRowKey());
		    sb.append("\")");
		    sb.append(", true)");
		    
		   // System.out.println(sb.toString());
		    
		   // RequestContext.getCurrentInstance().execute(sb.toString());
		    PrimeFaces.current().executeScript(sb.toString());
		
		//---------------------------------------------------------
		
		
		}
		dopInfoNode = "";
		extCode = "";
		notEditable = false;
		textNode = null;
		itemNew = true;
	}

	public void actionSave(){
		
		boolean flag = true;
		
		/*проверка за коректност на датата на събитието (не трябва да бъде по-малка от датата на последната актуализация и 
	     * нетрябва да е по-голяма от днешната)*/
	    if(!checkDateEvent(dateFor, dateEvent)) {
	    	flag = false;
	    }
	    
	    flag = checkData(flag);
	    if(flag) { //proverka za prazni poleta
	        try {
		        
		        if (valChoiseNew == null){ 
		        	//ako ne e pipana durvoto i pochnat direktno da slagat nov element
		        	long prev = 0;
		        	
		        	int lastIndex = rootNode.getChildCount();
		        	
		            if(lastIndex > 0){
		            	prev = ((SystemClassif) rootNode.getChildren().get(lastIndex - 1).getData()).getCode();
		            }
		            
		            node = new SystemClassif();
			        node.setCodeClassif(codeClassif);
			        node.setCodeParent(0);
			        node.setCodePrev(prev);
		        }
		        	        
		        node.setDopInfo(dopInfoNode);
		        node.setCodeExt(extCode);
		        node.setNoDelete(notEditable);
		        node.setTekst(textNode.get(getLang())[0]); // slagam go zashtoto posle shte go dobavya v durvoto
		        
		        
		        for (Map.Entry<Long, String[]> entry : textNode.entrySet()) {
					Long keyLang = entry.getKey();
					String []nodeValue = entry.getValue();
		        	
		        	Multilang ml = new Multilang();
		        	ml.setLang(keyLang);
		        	ml.setTekst(nodeValue[0]);
		        	ml.setTekstopi(nodeValue[1]);
		        	
		        	node.getTranslations().add(ml);
		        }
		        
		        try {
		        	JPA.getUtil().begin();
		        	scDAO.saveItem(node, dateEvent, false);
		        	JPA.getUtil().commit();
									
		        	itemNew = false;
					dateFor = dateEvent;
					
					//---- dobavyane na noviyat element v durvoto -------------
					
					if (valChoiseNew == null && rootNode != null) {
						
						TreeNode tmpNew = new DefaultTreeNode(node, rootNode);
						StringBuilder sb = new StringBuilder();
					    sb.append("PrimeFaces.widgets.treeSingleWidget.selectNode(");
					    sb.append("$(\"#scForm\\\\:systemTree\\\\:");
						sb.append(tmpNew.getRowKey());
						sb.append("\")");
					    sb.append(", true)");
						//RequestContext.getCurrentInstance().execute(sb.toString());
						PrimeFaces.current().executeScript(sb.toString());
					}
					else {
						//setTypeNodeDefault(rootNode);
						if (selectedNode != null){
							selectedNode.setType(DefaultTreeNode.DEFAULT_TYPE);
						}
					}
					
				    
				    //
					//---------------------------------------------------------
					getSystemData().reloadClassif(codeClassif);
					//---------------------------------------------------------
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.succesSaveMsg"));
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
	        
	        }
	        catch (Exception e) {
	        	JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			}
	    }
	}
	
	private boolean checkDateEvent(Date dateFor ,Date dateEvent){
        
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("checkDateEvent(Date, Date) - >>>> DateFor   : " + dateFor.getTime());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("checkDateEvent(Date, Date) - >>>> DateEvent : " + dateEvent.getTime());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("checkDateEvent(Date, Date) - >>>> Today     : " + new Date().getTime());
		}
		getDateEvent();
		
        if((dateFor.getTime() <= dateEvent.getTime()) && (dateEvent.getTime() <= new Date().getTime())) {
            return true;
        }
        else {
            // dateEventFalse 
        	JSFUtils.addMessage("scForm:dateEvent",FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "systemClassif.dateEventFalse"));
        }
        
        return false;
    }
	
	public void actionEditNoHistory() {
		actionEdit(false);
	}
	
	public void actionEditHistory(){
		
		/*проверка за коректност на датата на събитието (не трябва да бъде по-малка от датата на последната актуализация и 
         * нетрябва да е по-голяма от днешната)*/
        if(!checkDateEvent(dateFor, dateEvent)) {
        	return;
        }
		
		actionEdit(true);
		dateFor = dateEvent;
	}
	
	private void actionEdit(boolean history){
		
		if(checkData(true)) { //proverka za vuvedeni poleta
		
			try {
				JPA.getUtil().begin();
				
				node.setDopInfo(dopInfoNode);
				node.setCodeExt(extCode);
				node.setNoDelete(notEditable);
				for(Multilang item : node.getTranslations()){
					item.setTekst(textNode.get(item.getLang())[0]);
					item.setTekstopi(textNode.get(item.getLang())[1]);
				}
				
				scDAO.updateItem(node, dateEvent, history);
				
				//------- promenyame texta na noda v durvoto
				((SystemClassif)selectedNode.getData()).setTekst(textNode.get(getLang())[0]);
				//------
				
				JPA.getUtil().commit();
				
				getSystemData().reloadClassif(codeClassif);
				//---------------------------------------------------
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.succesUbdateMsg"));
			}
			catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(),e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
			}
			finally {
				JPA.getUtil().closeConnection();
			}
		}
	}
	
	private boolean checkData(boolean flag) {
    	if(sysClassifOpis.isExternal() && (extCode == null || extCode.trim().isEmpty())) {
    		JSFUtils.addMessage("scForm:extCode", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsert", getMessageResourceString("labels", "general.extCode")));
    		flag = false;
    	}
    	else if(sysClassifOpis.isExternal() && sysClassifOpis.isNumCodeExt()) {
    		try {
    			Long.valueOf(extCode);
    		}
    		catch(Exception e) {
    			JSFUtils.addMessage("scForm:extCode", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsertNumber", getMessageResourceString("labels", "general.extCode")));
    			flag = false;
    		}
    	}
    	
    	int i = 0;
        for(Entry<Long, String[]> entry : textNode.entrySet()) {
			if(entry.getValue()[0] ==null || entry.getValue()[0] .trim().isEmpty()) {
				JSFUtils.addMessage("scForm:itemNameTbl:" + i + ":nameItem",FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsert", getMessageResourceString("labels", "general.name")));
				flag = false;
			}
//			if(entyr.getValue()[1] ==null || entyr.getValue()[1] .trim().isEmpty()){
//				JSFUtils.addMessage("scForm:itemNameTbl:"+i+":nameItemO",FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert",getMessageResourceString("labels", "general.opis")));
//				flag = false;
//			}
			i++;
		}
        
        return flag;
    }

	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	
	public boolean isItemNew() {
		return itemNew;
	}

	public void setItemNew(boolean itemNew) {
		this.itemNew = itemNew;
	}
	
	public void setDateFor(Date dateFor) {    
        this.dateFor = dateFor; 
    }

    public Date getDateFor() {
        return dateFor;
    }    
    
    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
    }

    public Date getDateEvent() {
        if(this.dateEvent == null) {
            this.dateEvent = new Date();
        }  
        return dateEvent;
    }
    
    public boolean isNotEditable() {
		return notEditable;
	}

	public void setNotEditable(boolean notEditable) {
		this.notEditable = notEditable;
	}
	
}
