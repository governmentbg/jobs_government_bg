package indexbg.pjobs.ui;

import java.util.Date;
import java.util.List;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.BaseSystemData;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.TreeUtils;

import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.system.SystemData;



@FacesComponent(value="selectDirAdmStruct",createTag=true)
public class SelectDirAdmStruct extends UINamingContainer {
	
	static final Logger LOGGER = LoggerFactory.getLogger(SelectDirAdmStruct.class);
	private BaseSystemData sd=null;
	private Date dateClassif=null;
	private enum PropertyKeys {
		root, codeClassif, selectedNode, searchWord, isSelectNode, lang, showMe
	}	

	/**
	 * Зарежда дървото по код на класификация и др.
	 * @throws DbErrorException 
	 */
	@SuppressWarnings("unchecked")
	public void loadRoot() throws DbErrorException {
		LOGGER.info("loadRoot");
		setIsSelectNode((Boolean)getAttributes().get("isSelectNode")); //  true - да позволи избор на node от дървото; false - избор само на листа... 
		setSearchWord("");
		Long userId = (Long) getAttributes().get("userId");
		if(userId == null){
			userId = -1L;
		}
		setLang((Long)getAttributes().get("lang")); // по подразбиране 1L
		Boolean saveStateTree = Boolean.valueOf((String)getAttributes().get("saveStateTree"));
	    dateClassif = getAttributeValue("dateClassif",new Date()); 
	    
		Long codeClassif1 = null;
		if(saveStateTree){ 
			codeClassif1 = getCodeClassif();
		}
		if(codeClassif1 == null || !codeClassif1.equals(0L) || !saveStateTree){
			codeClassif1 = (Long)getAttributes().get("codeClassif"); 
		}
		if(saveStateTree){ 
			setCodeClassif(codeClassif1); 
		}
		if(codeClassif1 != null && !codeClassif1.equals(0L)){ // ако случайно е подадена 0 или липсва такъв атрибут...		
			setShowMe(true);
			if(getRoot() == null || !saveStateTree){	
				LOGGER.info("Loading tree with classifCode: " + codeClassif1);
				try {
					if (sd==null){
						sd=(BaseSystemData)JSFUtils.getManagedBean("systemData");
					}
					LOGGER.debug("test debug ---------------");
					LOGGER.info("test info ---------------");
					//List<SystemClassif> classifList = sd.getSysClassification(codeClassif1, dateClassif, getLang(), userId);// new SystemClassifDAO(userId).filterByCodeClassif(codeClassif1, getLang());
					
					List<SystemClassif> classifList =   new AdministrationDAO(userId ,(SystemData) sd).builAdmdStrOfSubject(codeClassif1, dateClassif);
					
					Boolean expanded = (Boolean)getAttributes().get("expanded");
					Boolean sortByName = (Boolean)getAttributes().get("sortByName");
					List<Long> readOnlyCodes = (List<Long>) getAttributes().get("readOnlyCodes");
					TreeNode rootNode = new TreeUtils().loadTreeData3(classifList, "", sortByName, expanded, readOnlyCodes, null);
			        setRoot(rootNode);
			        
				} catch (DbErrorException e) {
					throw new DbErrorException("Възникна грешка при работа са базата данни!",e);					
				} catch (Exception e) {
					throw new DbErrorException("Възникна грешка при работа!",e);
				} finally {
					JPA.getUtil().closeConnection();
				}
			}
		} else{			 
	    	JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при работата! Не е подаден код на класификация.");
	    }
    }
	
	/**
	 * Търсене на класификация по текст
	 * @throws DbErrorException 
	 */
	public void search() throws DbErrorException{
		
		Long userId = (Long) getAttributes().get("userId");
		if(userId == null){
			userId = -1L;
		}
		Long codeClassif1 = getCodeClassif();
		if(codeClassif1 == null){
			codeClassif1 = (Long)getAttributes().get("codeClassif");
		}
		dateClassif=getAttributeValue("dateClassif", new Date());
		if(codeClassif1 != null){		
			LOGGER.info("Searching for classif with: " + getSearchWord());
			List<SystemClassif> classifList;
			try {
				if (sd==null){
					sd=(BaseSystemData)JSFUtils.getManagedBean("systemData");
				}
				classifList =   new AdministrationDAO(userId ,(SystemData) sd).builAdmdStrOfSubject(codeClassif1, dateClassif);
				TreeNode rootNode = new TreeUtils().fTree(classifList, getSearchWord(), true, true ,null ,null);
				setRoot(rootNode);
			} catch (DbErrorException e) {
				throw new DbErrorException("Възникна грешка при работа са базата данни!", e);
			} finally {
				JPA.getUtil().closeConnection();
			}
		} else{
	    	JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при работата! Не е подаден код на класификация."); 
	    }
	}
	
	/**
     * Return specified attribute value or otherwise the specified default if it's null.
     */
    @SuppressWarnings("unchecked")
    private <T> T getAttributeValue(String key, T defaultValue) {
        T value = (T) getAttributes().get(key);
        return (value != null) ? value : defaultValue;
    }
	/**
	 * Зарежда избраната класификация и я предава към value на bean
	 * @param event
	 */
	public void onNodeSelect(NodeSelectEvent event) {		
		ELContext ctx = getFacesContext().getELContext();

		if(event.getTreeNode().isLeaf() || getIsSelectNode() ){	
			SystemClassif selectedClassif = (SystemClassif) event.getTreeNode().getData();
			LOGGER.info("Selected node from tree: " + selectedClassif.getTekst());
			
	        ValueExpression exprForText = getValueExpression("selectedText");
		    if(exprForText != null){
			    String selText =  selectedClassif.getTekst();
				selText = selText.replace("<b>", "");
				selText = selText.replace("</b>", "");				
				ctx = getFacesContext().getELContext();
				exprForText.setValue(ctx, selText);
		    }
		    
		    ValueExpression exprForCode = getValueExpression("selectedCode");
		    if(exprForCode != null){
		    	exprForCode.setValue(ctx, selectedClassif.getCode());
		    }
		    
		    ValueExpression exprForClassif = getValueExpression("selectedClassif");
		    if(exprForClassif != null){
		    	exprForClassif.setValue(ctx, selectedClassif);
		    }
		   // затваря модалния
		   
		    String dialogWidgetVar= "PF('one"+getClientId()+"').hide();" ;
		    
//		    RequestContext context = RequestContext.getCurrentInstance();
//		    context.execute(dialogWidgetVar); 
		    //pf 6.2 versiq
		    PrimeFaces.current().executeScript(dialogWidgetVar);
		    
		} else {
			// не позволява да се избере node от дървото	
			if(event.getTreeNode().isExpanded()){
				event.getTreeNode().setExpanded(false);
        	} else {
        		event.getTreeNode().setExpanded(true);
        	}
			event.getTreeNode().setSelected(false);
		}		
    }

	/**
	 * Зачиства компонентата и зарежда всичко наново
	 * @throws DbErrorException 
	 */
	public void clear() throws DbErrorException{
		setSearchWord("");
		setRoot(null);		
		loadRoot();
	}
	
	/**
	 * Зачиства полетата, в които се връща резултата
	 */
	public void clearInput(){
		ValueExpression expr = getValueExpression("selectedCode");
	    ELContext ctx = getFacesContext().getELContext();
	    if(expr != null){
	    	expr.setValue(ctx, null);
	    }
	    ValueExpression expr2 = getValueExpression("selectedText");
	    ELContext ctx2 = getFacesContext().getELContext();
	    if(expr2 != null){
	    	expr2.setValue(ctx2, null);
	    }
	}
	
	public TreeNode getRoot() {
		return (TreeNode) getStateHelper().eval(PropertyKeys.root, null);	
	}

	public void setRoot(TreeNode root) {
		getStateHelper().put(PropertyKeys.root, root);
	}
	
	public TreeNode getSelectedNode() {
		return (TreeNode) getStateHelper().eval(PropertyKeys.selectedNode, null);	
	}

	public void setSelectedNode(TreeNode selectedNode) {
		getStateHelper().put(PropertyKeys.selectedNode, selectedNode);
	}

	public Boolean getIsSelectNode() {
		  return (Boolean) getStateHelper().eval(PropertyKeys.isSelectNode, true); 
	}

	public void setIsSelectNode(Boolean selectNode) {
		 getStateHelper().put(PropertyKeys.isSelectNode, selectNode);
	}

	
	public String getSearchWord() {
		return (String) getStateHelper().eval(PropertyKeys.searchWord, "");	
	}
	

	public void setSearchWord(String searchWord) {
		getStateHelper().put(PropertyKeys.searchWord, searchWord.trim());
	}
	

	public Long getCodeClassif() {
		return (Long)getStateHelper().eval(PropertyKeys.codeClassif, null);
	}


	public void setCodeClassif(Long codeClassif) {
		getStateHelper().put(PropertyKeys.codeClassif, codeClassif);
	}


	public Long getLang() {
		 return (Long) getStateHelper().eval(PropertyKeys.lang, 1L); 
	}

	public void setLang(Long lang) {
		 getStateHelper().put(PropertyKeys.lang, lang);
	}

	public void setShowMe(boolean showMe) {
		getStateHelper().put(PropertyKeys.showMe, showMe);
	}

	public boolean isShowMe() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showMe, false); 
	}
	
	 
}
