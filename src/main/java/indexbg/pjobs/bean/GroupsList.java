package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.TreeUtils;

import indexbg.pjobs.db.AdmGroupRoles;
import indexbg.pjobs.db.AdmGroups;
import indexbg.pjobs.db.AdmUsers;
import indexbg.pjobs.db.dao.AdmGroupsDAO;
import indexbg.pjobs.db.dao.AdmUsersDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named
@ViewScoped
public class GroupsList extends PJobsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7056074939252508693L;

	static final Logger LOGGER = LoggerFactory.getLogger(GroupsList.class);

	private Long userId;
	private AdmGroupsDAO groupsDAO;
	private AdmUsersDAO userDao;
	private AdmGroups group;
	private List<AdmGroups> listGrops = new ArrayList<AdmGroups>();

	private String usersName;
	private List<Long> usersList = new ArrayList<>();
	private List<String> usersImena = new ArrayList<String>();

	private List<String[]> sysClassifList = new ArrayList<String[]>();
	private String selClassif;

	private TreeNode rootNode;
	private TreeNode[] selectedNode;

	private boolean fromEdit;
	private String selRkvForGr;
	private String selRkvForDos;

	public GroupsList() {

	}

	@PostConstruct
	public void initData() {

		LOGGER.debug("PostConstruct!!!");

		String dostap = getSystemData().getSettingsValue("classificationsForAccessControl");

		sysClassifList.clear();

		String[] clasList = dostap.split(",");

		try {

			this.userId = getUserData().getUserId();

			this.groupsDAO = new AdmGroupsDAO(this.userId);
			this.userDao = new AdmUsersDAO(this.userId);

			for (int i = 0; i < clasList.length; i++) {

				this.sysClassifList.add(new String[] { clasList[i], getSystemData().getNameClassification(Long.valueOf(clasList[i]), getCurrentLang()) });
			}

			this.listGrops = this.groupsDAO.findAll();
			
			this.fromEdit = false;

		} catch (NumberFormatException e) {
			LOGGER.error("Грешка при обработка на данните!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.formatExc"));

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при зареждане на системна класификация! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

		} catch (ObjectNotFoundException e) {
			this.userId = -1L;

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}

	}

	public void actionSave() {

		if (this.group.getGroupName() == null || this.group.getGroupName().isEmpty()) {

			JSFUtils.addMessage("formGroupList:nameGr", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsert", getMessageResourceString("labels", "groupsList.nameGroup")));
			return;

		} else {

			try {

				JPA.getUtil().begin();

				this.group.getGroupUsers().clear();

				if (!this.usersList.isEmpty()) {
					for (Long idUser : this.usersList) {

						this.group.getGroupUsers().add(this.userDao.findById(idUser));
					}
				}

				if (this.group.getId() == null) {
					this.group.setDateOt(getToday());
				}

				this.group = this.groupsDAO.save(this.group);

				JPA.getUtil().commit();

				if (this.group.getId() == null) {
					
					this.listGrops.add(this.group);
				
				} else {
					
					this.listGrops = this.groupsDAO.findAll();
					
					if (this.selRkvForGr != null && !this.selRkvForGr.isEmpty()) {
						//RequestContext.getCurrentInstance().execute("PrimeFaces.widgets.groupWV.selectRow(" + this.selRkvForGr + ")");
						PrimeFaces.current().executeScript("PrimeFaces.widgets.groupWV.selectRow(" + this.selRkvForGr + ")");
					}

					if (this.selRkvForDos != null && !this.selRkvForDos.isEmpty()) {
						//RequestContext.getCurrentInstance().execute("PrimeFaces.widgets.dostapWV.selectRow(" + this.selRkvForDos + ")");
						PrimeFaces.current().executeScript("PrimeFaces.widgets.dostapWV.selectRow(" + this.selRkvForDos + ")");
					}
				}

				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
				
				PrimeFaces.current().executeScript("scrollToErrors()");

			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при запис на група! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));

			} finally {
				JPA.getUtil().closeConnection();
			}
		}

	}

	public void actionEdit() {

		this.fromEdit = true;
		this.usersList.clear();
		this.usersImena.clear();

		String idObj = JSFUtils.getRequestParameter("idObj");
		this.selRkvForGr = JSFUtils.getRequestParameter("rkvIndexG");

		try {

			if (idObj != null && !("").equals(idObj.trim())) {
				
				this.group = new AdmGroups();

				this.group = this.groupsDAO.findById(Long.valueOf(idObj));

				if (!this.group.getGroupUsers().isEmpty()) {

					for (AdmUsers user : this.group.getGroupUsers()) {
						this.usersImena.add(user.getNames());
						this.usersList.add(user.getId());
					}
				}

				this.group.getGroupRoles().size();
			}

			//PF po malki 6.2 versii
			//RequestContext.getCurrentInstance().execute("PrimeFaces.widgets.groupWV.unselectAllRows()");
			//RequestContext.getCurrentInstance().execute("PrimeFaces.widgets.groupWV.selectRow(" + this.selRkvForGr + ")");
			
			//PF po golemi 6.2 versii i raboti ako e dobawen atributa da moje da se selektira red w tablicata. (podmenqme go s onclick="highlightElement(this ,'tr');" )
			//PrimeFaces.current().executeScript("PrimeFaces.widgets.groupWV.unselectAllRows()");
			//PrimeFaces.current().executeScript("PrimeFaces.widgets.groupWV.selectRow(" + this.selRkvForGr + ")");

		} catch (NumberFormatException e) {
			LOGGER.error("Грешка при обработка на данните!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.formatExc"));

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при редакция на група! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
			this.rootNode = null;
			this.selectedNode = null;
		}

	}

	public void actionNew() {

		this.fromEdit = true;
		this.group = new AdmGroups();

		this.usersList.clear();
		this.usersImena.clear();
		this.usersName = "";
		this.selClassif = "";

		this.rootNode = null;
		this.selectedNode = null;
	}

	public void actionDelete() {

		String idObjDel = JSFUtils.getRequestParameter("idObjDel");

		try {

			JPA.getUtil().begin();

			this.groupsDAO.deleteById(Long.valueOf(idObjDel));

			JPA.getUtil().commit();

			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.successDeleteMsg"));
			
			PrimeFaces.current().executeScript("scrollToErrors()");

		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при изтриване на група! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
			actionNew();
			initData();
			this.fromEdit = false;
		}
	}

	public void actionChangeActivate() {

		String idObjAct = JSFUtils.getRequestParameter("idObjAct");

		try {

			if (idObjAct != null && !("").equals(idObjAct.trim())) {

				JPA.getUtil().begin();

				for (int i = 0; i < this.listGrops.size(); i++) {

					AdmGroups grTemp = this.listGrops.get(i);

					if (grTemp.getId().equals(Long.valueOf(idObjAct))) {

						if (grTemp.getDateDo() != null) {
							grTemp.setDateDo(null);

							this.groupsDAO.save(grTemp);
						}
					}
				}

				JPA.getUtil().commit();
			}

		} catch (NumberFormatException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при обработка на данните!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.formatExc"));

		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при активиране на група! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
			actionNew();
			this.fromEdit = false;
		}

	}

	public void actionChangeDeactivate() {

		String idObjDeact = JSFUtils.getRequestParameter("idObjDeact");

		try {

			if (idObjDeact != null && !("").equals(idObjDeact.trim())) {

				JPA.getUtil().begin();

				for (int i = 0; i < this.listGrops.size(); i++) {

					AdmGroups grTemp = this.listGrops.get(i);

					if (grTemp.getId().equals(Long.valueOf(idObjDeact))) {

						if (grTemp.getDateDo() == null) {
							grTemp.setDateDo(getToday());

							this.groupsDAO.save(grTemp);
						}
					}

				}

				JPA.getUtil().commit();
			}

		} catch (NumberFormatException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при обработка на данните!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.formatExc"));

		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при деактивиране на група! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
			actionNew();
			this.fromEdit = false;
		}

	}

	public void actionLoadTree() {

		this.rootNode = null;
		this.selectedNode = null;

		this.selClassif = JSFUtils.getRequestParameter("idObjAccEdit");
		this.selRkvForDos = JSFUtils.getRequestParameter("rkvIndexD");

		try {

			List<SystemClassif> listItems = getSystemData().getSysClassification(Long.valueOf(this.selClassif), getToday(), getCurrentLang(), this.userId);

			List<Long> rolesList = new ArrayList<>();
			if (this.group.getId() != null) {
				for (AdmGroupRoles roleTmp : this.group.getGroupRoles()) {
					if (roleTmp.getCodeClassif().equals(Long.valueOf(this.selClassif))) {
						rolesList.add(roleTmp.getCodeRole());
					}
				}
			}

			rootNode = new TreeUtils().loadCheckboxTree(listItems, false, rolesList, true, null, null);
			
			//PF po malki 7 versii
			//RequestContext.getCurrentInstance().execute("PrimeFaces.widgets.dostapWV.unselectAllRows()");
			//RequestContext.getCurrentInstance().execute("PrimeFaces.widgets.dostapWV.selectRow(" + this.selRkvForDos + ")");
			
			//PF po golemi 7 versii i raboti ako e dobawen atributa da moje da se selektira red w tablicata. (podmenqme go s onclick="highlightElement(this ,'tr');" )
			//PrimeFaces.current().executeScript("PrimeFaces.widgets.dostapWV.unselectAllRows()");
			//PrimeFaces.current().executeScript("PrimeFaces.widgets.dostapWV.selectRow(" + this.selRkvForDos + ")");
			
		} catch (NumberFormatException e) {
			LOGGER.error("Грешка при обработка на данните!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.formatExc"));

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при зареждане на дърво! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}

	}

	public void actionDeleteUsersList() {

		this.usersList.clear();
		this.usersImena.clear();
		this.usersName = "";
	}

	public void actionDeleteUser() {

		String selectedName = JSFUtils.getRequestParameter("selectedName");

		try {

			for (Long user : this.usersList) {

				AdmUsers userTmp = this.userDao.findById(user);

				if (userTmp.getNames().equals(selectedName)) {
					this.usersImena.remove(selectedName);
					this.usersList.remove(user);
					break;
				}
			}

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при изтриване на участник в групата! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		}
	}

	// при маркиране се добавят ролите към баг-а на групата
	public void actionNodeSelect(NodeSelectEvent event) {

		TreeNode node = event.getTreeNode();
		AdmGroupRoles grRoles = new AdmGroupRoles();

		if (node.getParent().isPartialSelected() || node.getParent().isSelected()) { // ако родителя е избран или са избрани само някои от децата

			if (isContansCodeRole(((SystemClassif) node.getData()).getCode())) {
				grRoles = new AdmGroupRoles();

				grRoles.setCodeClassif(((SystemClassif) node.getData()).getCodeClassif());
				grRoles.setCodeRole(((SystemClassif) node.getData()).getCode());

				this.group.getGroupRoles().add(grRoles);
			}

			List<SystemClassif> parentsList = findAllParents(((SystemClassif) node.getData()).getCodeClassif(), (((SystemClassif) node.getData()).getCode()));

			for (SystemClassif sc : parentsList) {
				if (sc.getCodeParent() != 0 && isContansCodeRole(sc.getCodeParent())) {

					grRoles = new AdmGroupRoles();
					grRoles.setCodeClassif(sc.getCodeClassif());
					grRoles.setCodeRole(sc.getCodeParent());

					this.group.getGroupRoles().add(grRoles);
				}
			}

			List<TreeNode> childrenList = node.getChildren(); // децата на родителя

			for (TreeNode treeNode : childrenList) { // обикалят се децата

				if (!treeNode.getChildren().isEmpty()) {

					List<TreeNode> childsList = treeNode.getChildren(); // ако децата са родители

					for (TreeNode nodeTree : childsList) { // обикалят се техните деца и се добавят

						if (isContansCodeRole(((SystemClassif) nodeTree.getData()).getCode())) {
							grRoles = new AdmGroupRoles();

							grRoles.setCodeClassif(((SystemClassif) nodeTree.getData()).getCodeClassif());
							grRoles.setCodeRole(((SystemClassif) nodeTree.getData()).getCode());

							this.group.getGroupRoles().add(grRoles);
						}
					}
				}

				if (isContansCodeRole(((SystemClassif) treeNode.getData()).getCode())) {
					grRoles = new AdmGroupRoles(); // добавят се децата, които не са родители

					grRoles.setCodeClassif(((SystemClassif) treeNode.getData()).getCodeClassif());
					grRoles.setCodeRole(((SystemClassif) treeNode.getData()).getCode());

					this.group.getGroupRoles().add(grRoles);
				}
			}
		}
	}

	// проверява се дали този код на роля вече го има добавен в баг-а на групата
	public boolean isContansCodeRole(Long code) {

		for (AdmGroupRoles role : this.group.getGroupRoles()) {

			if (Long.valueOf(this.selClassif).equals(Long.valueOf(role.getCodeClassif()))) {
				if (code.equals(Long.valueOf(Long.valueOf(role.getCodeRole())))) {
					return false;
				}
			}
		}
		return true;
	}

	// при размаркиране се махат ролите от баг-а на групата
	public void actionNodeUnselect(NodeUnselectEvent event) {

		TreeNode node = event.getTreeNode();

		Long codeClassif = ((SystemClassif) node.getData()).getCodeClassif(); // код на системна класификация
		Long codeRole = ((SystemClassif) node.getData()).getCode(); // код на роля
		Long codeParent = ((SystemClassif) node.getData()).getCodeParent(); // код на родител

		List<AdmGroupRoles> listForRemove = new ArrayList<AdmGroupRoles>(); // списък за махане на ролите

		listForRemove.add(findRole(codeClassif, codeRole)); // към листа се добавя роля за махане

		if (!node.getParent().isPartialSelected() || !node.getParent().isSelected()) { // ако е избран родителя

			List<TreeNode> childrenList = node.getChildren();

			for (TreeNode nodeChilds : childrenList) {

				if (!nodeChilds.isSelected() && !isContansCodeRole(((SystemClassif) nodeChilds.getData()).getCode())) {
					if (!isExistForRemove(listForRemove, ((SystemClassif) nodeChilds.getData()).getCode())) {
						listForRemove.add(findRole(codeClassif, ((SystemClassif) nodeChilds.getData()).getCode())); // към листа се добавя всички деца на избрания родител
					}
				}
			}
		}

		if (!node.isLeaf()) {
			List<TreeNode> childsList = node.getParent().getChildren(); // списък с децата

			boolean remove = false;
			for (TreeNode nodeTree : childsList) { // обикаля се списъка с децата, ако са махнати всички, трябва да се махне и родителя

				if (nodeTree.isSelected()) {
					remove = false;
					break;
				} else {
					remove = true;
				}
			}

			if (remove) {
				if (!isExistForRemove(listForRemove, codeParent) && !isContansCodeRole(codeParent)) {
					listForRemove.add(findRole(codeClassif, codeParent)); // всички деца са махнати и затова се добавя родителя да се махне също
				}
			}

		} else {

			List<TreeNode> treeParents = new ArrayList<TreeNode>();
			TreeUtils.getParents(node, treeParents);

			for (int i = 0; i < treeParents.size() - 1; i++) {
				if (!treeParents.get(i).isPartialSelected()) {
					SystemClassif sc = ((SystemClassif) treeParents.get(i).getData());
					if (!isExistForRemove(listForRemove, sc.getCode()) && !isContansCodeRole(sc.getCode())) {
						listForRemove.add(findRole(codeClassif, sc.getCode()));
					}
				}
			}
		}

		for (AdmGroupRoles remRole : listForRemove) { // обикаля се списъка за махане и се махат всички роли, които са в него
			this.group.getGroupRoles().remove(remRole);
		}
	}

	// проверява се дали този код на роля вече го има в списъка за махане
	public boolean isExistForRemove(List<AdmGroupRoles> listForRemove, Long code) {

		for (AdmGroupRoles role : listForRemove) {

			if (Long.valueOf(this.selClassif).equals(Long.valueOf(role.getCodeClassif()))) {
				if (code.equals(Long.valueOf(role.getCodeRole()))) {
					return true;
				}
			}
		}
		return false;
	}

	// намира конкретния елемент по подаден код на класификация и код на роля
	public AdmGroupRoles findRole(Long codeClassif, Long codeRole) {

		AdmGroupRoles grRole = new AdmGroupRoles();

		for (AdmGroupRoles role : this.group.getGroupRoles()) {

			if (Long.valueOf(this.selClassif).equals(Long.valueOf(codeClassif))) {
				if (codeRole.equals(Long.valueOf(Long.valueOf(role.getCodeRole())))) {
					grRole = role;
				}
			}
		}

		return grRole;
	}

	// намиране на всички родители на конкретния код
	public List<SystemClassif> findAllParents(Long codeClassif, Long codeRole) {

		List<SystemClassif> parentsList = new ArrayList<SystemClassif>();

		try {

			parentsList = getSystemData().getParents(codeClassif, codeRole, getToday(), getCurrentLang(), this.userId);

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на списъка с родителите! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		}

		return parentsList;
	}

	public Long getCodeClassifUser() {
		return Long.valueOf(Constants.CODE_CLASSIF_USERS);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public AdmGroups getGroup() {
		return group;
	}

	public void setGroup(AdmGroups group) {
		this.group = group;
	}

	public List<AdmGroups> getListGrops() {
		return listGrops;
	}

	public void setListGrops(List<AdmGroups> listGrops) {
		this.listGrops = listGrops;
	}

	public String getUsersName() {
		return usersName;
	}

	public void setUsersName(String usersName) {
		this.usersName = usersName;
	}

	public List<Long> getUsersList() {
		return usersList;
	}

	public void setUsersList(List<Long> usersList) {

		this.usersImena = new ArrayList<String>();

		if (!usersList.isEmpty()) {

			try {

				for (Long user : usersList) {

					AdmUsers userTmp = this.userDao.findById(user);
					usersImena.add(userTmp.getNames());
				}

			} catch (DbErrorException e) {
				LOGGER.error("Грешка при търсене на участник в групата! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			}
		}

		this.usersList = usersList;
	}

	public List<String> getUsersImena() {
		return usersImena;
	}

	public void setUsersImena(List<String> usersImena) {
		this.usersImena = usersImena;
	}

	public List<String[]> getSysClassifList() {
		return sysClassifList;
	}

	public void setSysClassifList(List<String[]> sysClassifList) {
		this.sysClassifList = sysClassifList;
	}

	public String getSelClassif() {
		return selClassif;
	}

	public void setSelClassif(String selClassif) {
		this.selClassif = selClassif;
	}

	public TreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public TreeNode[] getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode[] selectedNode) {
		this.selectedNode = selectedNode;
	}

	public boolean isFromEdit() {
		return fromEdit;
	}

	public void setFromEdit(boolean fromEdit) {
		this.fromEdit = fromEdit;
	}

	public String getSelRkvForGr() {
		return selRkvForGr;
	}

	public void setSelRkvForGr(String selRkvForGr) {
		this.selRkvForGr = selRkvForGr;
	}

	public String getSelRkvForDos() {
		return selRkvForDos;
	}

	public void setSelRkvForDos(String selRkvForDos) {
		this.selRkvForDos = selRkvForDos;
	}

	public Date getToday() {
		return new Date();
	}

	private Date dateFrom;

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

}
