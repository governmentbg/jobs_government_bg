package indexbg.pjobs.bean;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import org.primefaces.PrimeFaces;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dao.SystemClassifDAO;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.db.dto.SystemClassifOpis;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectInUseException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.TreeUtils;

import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * Пренесена версия на страницата от CAdmin
 * @author n.kanev
 */
public abstract class SystemClassifParentBean extends PJobsBean {

	private static final long serialVersionUID = 5879251440016756523L;
	static final Logger LOGGER = LoggerFactory.getLogger(SystemClassifParentBean.class);
	
	SystemClassifDAO scDAO;
	Long userId;	
	String textNameClassif;
	Long codeClassif;
	TreeNode rootNode;
	TreeNode selectedNode;
	SystemClassifOpis sysClassifOpis;
	String extCode;
	HashMap<Long, String[]> textNode;
	String dopInfoNode;
	String customMess;
	
	/* езици на системата */
	Long lang;
	List<SystemClassif> sysLangs;
	boolean multipleLang;
	
	/* търсене в дървото */
	String searchText;
	private int resultFound = 0;
	private int findNext = 0;
	private TreeNode foundedNode;
	
	/* избраната позиция за преместване (преди след като подчинен)*/
	String valChoiseNew;
	
	/* позиция - избрана за преместване от дървото */
	Long selectedCodeItemForMove;
	boolean selectedForMoveIsLeaf;
	private SystemClassif lastChild;
	
	abstract void loadTreeData() throws DbErrorException;
	abstract void clearData();
	
	public SystemClassifParentBean() { 
		try {
			this.userId = getUserData().getUserId();
		}
		catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен потребител!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;
		}
		
		scDAO = new SystemClassifDAO(getUserId());
		selectedForMoveIsLeaf = false;
		lastChild = null;
	}
	
	public void actionSearch() {
		if(searchText != null && !searchText.trim().isEmpty()) {
			try {
				if(searchInTree()) {
					findNext++;
				}
				else {
					if(findNext > 0) { //startirame na novo turseneto
						resultFound = 0;
						findNext = 0;
				
						if(foundedNode != null) {
							((SystemClassif) foundedNode.getData())
								.setTekst(
									((SystemClassif) foundedNode.getData())
									.getTekst()
									.replace("<span id='mark_' style='background-color: #7389EC; color:#FFFFFF'>", "")
									.replace("</span>", ""));
						}

						foundedNode = null;
						TreeUtils.expandCollapseAllNodes(rootNode, false);
						searchInTree();
						findNext++;
					}
					else {
						//nyama namereni znacheniya
					}
				}
			}
			catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				//  addGlobalMessage(FacesMessage.SEVERITY_ERROR , getMessageResourceString("beanMessages", "general.IO")); 
			}
		}
	}
	
	private boolean searchInTree() throws IOException {
		
		resultFound = 0;
		  
		if(foundedNode != null) {
			((SystemClassif) foundedNode.getData())
				.setTekst(((SystemClassif) foundedNode.getData())
					.getTekst()
					.replace("<span id='mark_' style='background-color: #5f94b5; color:#FFFFFF'>", "")
					.replace("</span>", ""));
		}
		  
		foundedNode = searchTree(rootNode.getChildren().iterator(), findNext);
		
		if (foundedNode != null) {
		   
			((SystemClassif) foundedNode.getData())
				.setTekst(com.indexbg.system.utils.StringUtils.markingSearchText(
					((SystemClassif) foundedNode.getData()).getTekst(), 
					searchText, 
					"<span id='mark_' style='background-color: #5f94b5; color:#FFFFFF'>",
					"</span>"));
				   
			TreeUtils.expand(foundedNode);
			return true;
		}
		return false;
	}
	
	private TreeNode searchTree(Iterator<TreeNode> itr, int resultNum) {
		while(itr.hasNext()) {
			TreeNode el = itr.next();
			if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(
					((SystemClassif)el.getData()).getTekst(),
					searchText.trim().toLowerCase())) { 
				resultFound++;
				if(resultNum < resultFound) {
					return el;
				}
			}
			TreeNode chilldrenSearch = searchTree(el.getChildren().iterator(), resultNum);
			if (chilldrenSearch != null){
				return chilldrenSearch;
			}
		}
		return null;
	}
	
	public void actionClearNodeForMove() {
		selectedCodeItemForMove = null;
		removeNewNode(rootNode);
		TreeUtils.setTypeNodeDefault(rootNode); 
	}
	
	public void actionSelectForMove() {
		
		removeNewNode(rootNode);
		TreeUtils.setTypeNodeDefault(rootNode);
		
		selectedCodeItemForMove = Long.valueOf(((SystemClassif) selectedNode.getData()).getCode());
		selectedNode.setType(NodeTypes.SELECTED.text);
		
		selectedNode.setSelected(false);
		
		selectedForMoveIsLeaf = selectedNode.isLeaf();
		lastChild = null;
		if(selectedNode.getChildCount() > 0){
        	lastChild = (SystemClassif)(selectedNode.getChildren().get(selectedNode.getChildCount() - 1)).getData();
		}
		
		markSelectableYesNo(selectedNode, false, NodeTypes.SELECTED.text);
	}
	
	private void markSelectableYesNo(TreeNode tn, boolean selectable, String typeNode) {
		tn.setSelectable(selectable);
		tn.setType(typeNode);
		for(TreeNode tnc : tn.getChildren()){
			if(tnc.getChildCount() > 0){
				markSelectableYesNo(tnc, selectable, typeNode);
			}
			else {
				tnc.setSelectable(selectable);
				tnc.setType(typeNode);
			}
		}
	}
	
	public void actionMoveItem(ActionEvent event) {
		
		if(event != null) {
			String moveType = event.getComponent().getId();
			long parent = 0;
	        long prev   = 0;
	        
	        SystemClassif node = (SystemClassif) selectedNode.getData();
	        
            if("moveBefore".equals(moveType)) {
				parent = node.getCodeParent();
                prev   = node.getCodePrev();
			}
            else if("moveAfter".equals(moveType)) {
				parent = node.getCodeParent();
                prev   = node.getCode();
			}
            else if("moveChild".equals(moveType)) {
				parent = node.getCode();
                prev   = Long.valueOf(0);
			}
            else if("moveChildOnly".equals(moveType)) {
				parent = node.getCode();
                prev   = Long.valueOf(0);
			}
			
	        try {
				JPA.getUtil().begin();
				SystemClassif selectedItemForMove = scDAO.findByCode(codeClassif.longValue(), selectedCodeItemForMove.longValue(), true); //za da imam pulnite danni
	        	scDAO.moveItem(selectedItemForMove, prev, parent, new Date(), lastChild);
	        	JPA.getUtil().commit();
	        	
	        	//---- izgrajdame durvoto na novo zashtoto sme mesteli -----
	        	loadTreeData(); //TODO
				//----------------------------------------------------------
	        	
	        	getSystemData().reloadClassif(codeClassif);
	        	
	        	selectedForMoveIsLeaf = false;
	        	selectedCodeItemForMove = null;
	    		lastChild = null;
	    		//---------------------------------------------------
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.successMsg"));
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
	}
	
	public void actionDeleteItem(ActionEvent event) {
		
		if(event != null) {
			
			SystemClassif node = (SystemClassif) selectedNode.getData();
	        
            try {
				JPA.getUtil().begin();
				SystemClassif selectedItemForDelete = scDAO.findByCode(node.getCodeClassif(), node.getCode(), true); //za da imam pulnite danni
	        	scDAO.deleteItem(selectedItemForDelete, new Date(), sysClassifOpis.isLogical());
	        	JPA.getUtil().commit();
	        	
	        	selectedNode.getParent().getChildren().remove(selectedNode);
	        	clearData();
	        	getSystemData().reloadClassif(codeClassif);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,getMessageResourceString(Constants.beanMessages,"general.successDeleteMsg"));
	        }
            catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(),e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
			}
            catch (ObjectInUseException e) {
				JPA.getUtil().rollback();
				
				if(sysClassifOpis.isLogical()) {
					LOGGER.error(e.getMessage(),e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectInUse"));
				}
				else {
					String mess = e.getMessage();
					String[] classifOrLogicList = mess.split("!");
					
					if(classifOrLogicList.length > 0) {
					
						StringBuilder sb = new StringBuilder();
						
						try {
							sb.append("<br/>");
							sb.append(getMessageResourceString(Constants.LABELS, "general.classif"));
							sb.append(": ");
							sb.append(generateErrorDeleteMessage(classifOrLogicList[0].split(";") , true));
							if(classifOrLogicList.length > 1){
								sb.append(" <br/>");
								sb.append(getMessageResourceString(Constants.LABELS, "syslogic.name"));
								sb.append(": ");
								sb.append(generateErrorDeleteMessage(classifOrLogicList[1].split(";"), false));
							}
						}
						catch (DbErrorException e1) {
							LOGGER.error(e.getMessage(), e1);
							JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
						}
						
						setCustomMess(sb.toString());
						
						PrimeFaces.current().executeScript("PF('modalItemDelete').show()");
					}
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
	}
	
	private StringBuilder generateErrorDeleteMessage(String[] classifUseList, boolean classOrLogicList) 
			throws NumberFormatException, DbErrorException {
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < classifUseList.length; i++) {

			String[] keyVall = classifUseList[i].split(":");
			sb.append("<br/>");
			sb.append("&nbsp;");
			if(classOrLogicList) {
				sb.append(getSystemData().getNameClassification(Long.valueOf(keyVall[0]), getLang()));
			}
			else {
				sb.append(getSystemData().getNameList(Long.valueOf(keyVall[0]), getLang()));
			}
			sb.append(" ("+keyVall[0]+")");
			sb.append(" <br/>");
			
			String val = keyVall[1].substring(0, keyVall[1].length() - 1);
			if(val.indexOf(",") > -1) {
				sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				sb.append(getMessageResourceString(Constants.LABELS, "general.itemsDoDelete"));
				sb.append(": ");
				sb.append(" <br/>");
				
				String[] codeItems = val.split(",");
				for(int j = 0; j < codeItems.length; j++) {
					sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					sb.append(getSystemData().decodeItem(codeClassif, Long.valueOf(codeItems[j]), getLang(), new Date(), userId));
					sb.append(" (" + codeItems[j] + ")");
					sb.append(" <br/>");
				}
			}

		}
		return sb;
	}
	
	public void actionChangeLang() {
		try {
			loadTreeData();
		}
		catch (DbErrorException e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		}
		catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		}
		finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	void removeNewNode(TreeNode n) {
		Iterator<TreeNode> iterator = n.getChildren().iterator();
        while(iterator.hasNext()) {
        	TreeNode s = iterator.next();
        	
        	if(s.getType().equals(NodeTypes.NEW.text)) {
				s.getParent().getChildren().remove(s);
				break;
			}
        	else if(s.getChildCount() > 0) {
				removeNewNode(s);
			}
        	removeNewNode(s);
        }
	}
	
	public enum NodeTypes {
		NEW("new"), SELECTED("Selected");
		
		public String text;
		
		private NodeTypes(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	public String getTextNameClassif() {
		return textNameClassif;
	}

	public void setTextNameClassif(String textNameClassif) {
		this.textNameClassif = textNameClassif;
	}
	
	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	public TreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}
	
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	
	public Long getSelectedCodeItemForMove() {
		return selectedCodeItemForMove;
	}

	public void setSelectedCodeItemForMove(Long selectedCodeItemForMove) {
		this.selectedCodeItemForMove = selectedCodeItemForMove;
	}
	
	public SystemClassifOpis getSysClassifOpis() {
		return sysClassifOpis;
	}

	public void setSysClassifOpis(SystemClassifOpis sysClassifOpis) {
		this.sysClassifOpis = sysClassifOpis;
	}
	
	public boolean isSelectedForMoveIsLeaf() {
		return selectedForMoveIsLeaf;
	}

	public void setSelectedForMoveIsLeaf(boolean selectedForMoveIsLeaf) {
		this.selectedForMoveIsLeaf = selectedForMoveIsLeaf;
	}
	
	public Long getLang() {
		if(lang == null) {
			lang = getCurrentLang();
		}
		return lang;
	}

	public void setLang(Long lang) {
		this.lang = lang;
	}
	
	public void setSysLangs(List<SystemClassif> sysLangs) {
		this.sysLangs = sysLangs;
	}

	public List<SystemClassif> getSysLangs() {
		return sysLangs;
	}
	
	public String getExtCode() {
		return extCode;
	}

	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}

	public boolean isMultipleLang() {
		return multipleLang;
	}

	public void setMultipleLang(boolean multipleLang) {
		this.multipleLang = multipleLang;
	}
	
	public void setTextNode(HashMap<Long, String[]> textNode) {
		this.textNode = textNode;
	}

	public HashMap<Long, String[]> getTextNode() {
		if(textNode == null){
			
			textNode = new HashMap<Long, String[]>();
			
			for(SystemClassif tmp : getSysLangs()) {
				textNode.put(tmp.getCode(), new String[]{"",""});
			}
		}
		return textNode;
	}
	
	public String getDopInfoNode() {
		return dopInfoNode;
	}

	public void setDopInfoNode(String dopInfoNode) {
		this.dopInfoNode = dopInfoNode;
	}
	
	public String getCustomMess() {
		return customMess;
	}

	public void setCustomMess(String customMess) {
		this.customMess = customMess;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getCodeClassif() {
		return codeClassif;
	}
	
	public void setCodeClassif(Long codeClassif) {
		this.codeClassif = codeClassif;
	}
	
}
