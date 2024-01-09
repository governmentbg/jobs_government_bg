package indexbg.pjobs.bean;

import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dao.SystemClassifDAO;
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
@Named("systemClassifLogical")
@ViewScoped
public class SystemClassifLogicalBean extends SystemClassifParentBean {

	private static final long serialVersionUID = 2295420928093755036L;
	static final Logger LOGGER = LoggerFactory.getLogger(SystemClassifLogicalBean.class);
	
	private SystemClassifOpisDAO scOpisDAO;
	private List<SystemClassif> classifIzbList;
	private SystemClassif classifIzb;
	
	@PostConstruct
	public void init() {
		
		this.codeClassif = Long.valueOf(JSFUtils.getRequestParameter("idObj"));
		
		try {
			this.scDAO = new SystemClassifDAO(this.userId);
			this.scOpisDAO = new SystemClassifOpisDAO(this.userId);
			this.sysClassifOpis = this.scOpisDAO.findById(this.codeClassif);
			loadTreeData();
			
			//зареждаме езиците на които работи системата
			this.sysLangs = scDAO.filterByCodeClassif(Constants.CODE_CLASSIF_LANG, getLang());
		}
		catch (DbErrorException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			return;
		}
		
		this.sysClassifOpis.getTranslations().stream()
			.filter(t -> t.getLang() == getLang().longValue())
			.findFirst()
			.ifPresent(multilang -> { setTextNameClassif(multilang.getTekst()); });
		
		// проверяваме за многоезичност
		this.multipleLang = this.sysLangs.size() > 1;
		
		//getTextNode(); // ne e zaduljitelno da se izvikva
		
		dopInfoNode = "";
        extCode = "";
        selectedCodeItemForMove = null;
	}

	@Override
	void loadTreeData() throws DbErrorException {
		List<SystemClassif> listItems = getSystemData().getSysClassification(this.codeClassif, new Date(), getLang(), this.userId) ;
		this.rootNode = new TreeUtils().loadTreeData3(listItems, "", false, false, null, null);
	}
	
	@Override
	void clearData() {
		dopInfoNode = "";
        extCode = "";
        selectedCodeItemForMove = null;
        selectedNode = null;
        valChoiseNew = null;
	}
	
	public void processSelection(NodeSelectEvent event) {

		if(this.sysClassifOpis.isDynamic()) {
			return;
		}
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("Минава през избор на значение от дървото!!!");
		}

		try {
			SystemClassif node = scDAO.findByCode(this.sysClassifOpis.getLogicalSourceId(), ((SystemClassif)event.getTreeNode().getData()).getCode(), true);
			dopInfoNode = node.getDopInfo();
			extCode = node.getCodeExt();
			textNode.clear();
		
			for(SystemClassif tmp : sysLangs) {
				
				String txt [] = { "", "" };
				
				for(Multilang item: node.getTranslations()){
					if(item.getLang() == tmp.getCode()){
						txt[0] = item.getTekst();
						txt[1] = item.getTekstopi();
						break;
					}
				}	
				
				textNode.put(tmp.getCode(), txt);
			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.invalidParameter"));
		}
	}
	
	public void clearChoiceNew() {
		this.valChoiseNew = null;
	}
	
	private void actionSave(SystemClassif item, boolean dontDheckData) {
		
		if(dontDheckData || !containItem(rootNode, item.getCode())) {
		
			try {
				long parent = 0;
				long prev   = 0;
				int  level  = 1;
				
				item.setId(null);
				item.setCodeExt(null);
				item.setDopInfo(null);
				item.setNoDelete(false);
				item.setDateOt(new Date());
				item.setCodeClassif(codeClassif); //slagam coda na logicheskata
				
				
				if(rootNode.getChildCount() > 0) {
		        	prev = ((SystemClassif) rootNode.getChildren().get(rootNode.getChildCount() - 1).getData()).getCode();
		        }
				
				if(selectedNode != null && valChoiseNew != null) {
					SystemClassif node = (SystemClassif) selectedNode.getData();
					
					if("addNewBefore".equals(valChoiseNew)){
						parent = node.getCodeParent();
						prev   = node.getCodePrev();
						level  = node.getLevelNumber();
					}
					else if("addNewAfter".equals(valChoiseNew)){
						parent = node.getCodeParent();
						prev   = node.getCode();
						level  = node.getLevelNumber();
					}
					else if("addNewChild".equals(valChoiseNew)){
						parent = node.getCode();
						prev   = 0;
						level  = (node.getLevelNumber() + 1);
					}
				}
				
				item.setLevelNumber(level);
				item.setCodeParent(parent);
				item.setCodePrev(prev);
				
				JPA.getUtil().begin();
	        	scDAO.saveItem(item, new Date(), true);
	        	JPA.getUtil().commit();
								
	        	//---- dobavyane na noviyat element v durvoto -------------
				
	        	if(selectedNode != null && valChoiseNew != null) {
	        		DefaultTreeNode tmpNew = new DefaultTreeNode(item);
	        		
	        		int selNodeIndex = selectedNode.getParent().getChildren().indexOf(selectedNode); 
					
					if("addNewBefore".equals(valChoiseNew)) {
						selectedNode.getParent().getChildren().add(selNodeIndex, tmpNew);
					}
					else if("addNewAfter".equals(valChoiseNew)) {
						selectedNode.getParent().getChildren().add(selNodeIndex + 1, tmpNew);
					}
					else if("addNewChild".equals(valChoiseNew)) {
						selectedNode.setExpanded(true);
						selectedNode.getChildren().add(0, tmpNew);
					}
				}
	        	else {
					new DefaultTreeNode(item,rootNode);
				}
				//---------------------------------------------------------
	        	
				getSystemData().reloadClassif(codeClassif);
				
			}
			catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error(e.getMessage(),e);
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
		else {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, getMessageResourceString(Constants.beanMessages, "general.itemExist"));
		}
	}
	
	private boolean containItem(TreeNode treeNode, long code) {
		
		SystemClassif item = (SystemClassif) treeNode.getData();
		
		if(item != null && item.getCode() == code) {
			return true;
		}
		else {
			for(TreeNode child : treeNode.getChildren()) {
				if(containItem(child, code)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void actionNewDistp(ActionEvent event){
		this.valChoiseNew =  event.getComponent().getId();
	}
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	
	public List<SystemClassif> getClassifIzbList() {
		return classifIzbList;
	}

	public void setClassifIzbList(List<SystemClassif> classifIzbList) {
		if(classifIzbList != null && !classifIzbList.isEmpty()) {
			try {
				if(classifIzbList.size() == 1) {
					actionSave(classifIzbList.get(0).clone(), false);			
				}
				else {
					StringBuilder sb = new StringBuilder();
					
					for(SystemClassif scItem : classifIzbList) {
						if(containItem(rootNode, scItem.getCode())) {
							//dobavyame go v saobshtenieto che se sadarja
							sb.append(scItem.getTekst());
							sb.append(" (");
							sb.append(scItem.getCode());
							sb.append(")");
							sb.append("<br/>");
						}
						else {
							actionSave(scItem.clone(),true);	
						}
					}
				
					if(sb.length() != 0){
						setCustomMess(sb.toString());
						PrimeFaces.current().executeScript("PF('modalCustomMess').show()");
					}
				}
			}
			catch (CloneNotSupportedException e) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			}
			
		}
	}
	
	public SystemClassif getClassifIzb() {
		return classifIzb;
	}

	public void setClassifIzb(SystemClassif classifIzb) {
		if(classifIzb != null){
			try {
				actionSave(classifIzb.clone(), false);			
			}
			catch (CloneNotSupportedException e) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
				LOGGER.error(e.getMessage(), e);
			}			
		}
		this.classifIzb = classifIzb;
	}
}
