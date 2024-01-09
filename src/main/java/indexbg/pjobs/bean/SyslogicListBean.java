package indexbg.pjobs.bean;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dao.SyslogicListDAO;
import com.indexbg.system.db.dto.SyslogicListEntity;
import com.indexbg.system.db.dto.SystemClassif;
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
@Named("syslogicList")
@ViewScoped
public class SyslogicListBean extends PJobsBean {

	private static final long serialVersionUID = -7803755632236165082L;
	static final Logger LOGGER = LoggerFactory.getLogger(SyslogicListBean.class);
	
	private long codeClassifVod;
    private long codeClassifPod;
    private long codeClassif;
    private List<SyslogicListEntity> listSc;
    private Long userId;
    private SyslogicListDAO syslogicListDAO;
    private TreeNode rootNodeVod;
    private TreeNode rootNodePod;
    private TreeNode selectedNodeVod;
    private TreeNode[] selectedNodePod;
    private Date newDate;

    @PostConstruct
    public void init() {
        LOGGER.debug("PostConstruct!!!!");
        LOGGER.info("PostConstruct!!!!");
        
        try {
	        this.userId = getUserData().getUserId();
	        codeClassif = Long.valueOf(JSFUtils.getRequestParameter("idObj"));
	        codeClassifVod = Long.valueOf(JSFUtils.getRequestParameter("idVod"));
	        codeClassifPod = Long.valueOf(JSFUtils.getRequestParameter("idPod"));
	        syslogicListDAO = new SyslogicListDAO(userId);
	        actionSearch();
            loadTreeData();
        }
        catch (DbErrorException e) {
            LOGGER.error(e.getMessage(), e);
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
        }
        catch (ObjectNotFoundException e) {
			this.userId = -1L;
		}
    }

    private void loadTreeData() throws DbErrorException {
        List<SystemClassif> listItems, listItems1 = null;
        //SystemClassifDAO scDAO = new SystemClassifDAO(userId);
        listItems = getSystemData().getSysClassification(codeClassifVod, null, getCurrentLang(), userId);//scDAO.filterByCodeClassif(codeClassifVod, getCurrentLang());
        rootNodeVod = new TreeUtils().loadTreeData3(listItems, "", false, false, null, null);
        listItems1 = getSystemData().getSysClassification(codeClassifPod, null, getCurrentLang(), userId);//scDAO.filterByCodeClassif(codeClassifPod, getCurrentLang());
        rootNodePod = new TreeUtils().loadTreeData3(listItems1, "", false, false, null, null);
    }

    public void actionSearch() {
        try {
            listSc = new SyslogicListDAO(userId).filterByName(codeClassif); 
        }
        catch (DbErrorException e) {
            LOGGER.error(e.getMessage(), e);
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
        }
        finally {
            JPA.getUtil().closeConnection();
        }
    }

    public void actionDelete() {
        try {
            Long index = Long.valueOf(JSFUtils.getRequestParameter("delObj"));
            JPA.getUtil().begin();
            syslogicListDAO.deleteById(listSc.get((int) (long) index).getId());
            JPA.getUtil().commit();
            getSystemData().reloadList(codeClassif);
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

    public void actionInit() {
        for (TreeNode s : selectedNodePod) {
            s.setSelected(false);
        }
        if (selectedNodeVod != null) {
            selectedNodeVod.setSelected(false);
        }

        selectedNodeVod = null;
        selectedNodePod = null;
        newDate = new Date();
    }

    public void actionSave() {
        boolean flag = true;
        SystemClassif node = null;
        if (selectedNodeVod == null) {
            JSFUtils.addMessage("scForm:panelVod", FacesMessage.SEVERITY_ERROR,
                    getMessageResourceString(Constants.beanMessages, "general.pleaseInsert",
                            getMessageResourceString("labels", "syslogiclist.vod")));
            flag = false;
        }
        else {
            node = (SystemClassif) selectedNodeVod.getData();
        }
        
        if(selectedNodePod == null || selectedNodePod.length == 0) {
            JSFUtils.addMessage("scForm:panelPod", FacesMessage.SEVERITY_ERROR,
                    getMessageResourceString(Constants.beanMessages, "general.pleaseInsert",
                            getMessageResourceString("labels", "syslogiclist.pod")));
            flag = false;
        }
        
        if(newDate == null) {
            JSFUtils.addMessage("scForm:newDate", FacesMessage.SEVERITY_ERROR,
                    getMessageResourceString(Constants.beanMessages, "general.pleaseInsert",
                            getMessageResourceString("labels", "syslogiclist.DataOt")));
            flag = false;
        }
        
        if (flag) {
            for (SyslogicListEntity entity : listSc) {
                for (TreeNode treeNode : selectedNodePod) {
                    SystemClassif nodepod = (SystemClassif) treeNode.getData();
                    if (entity.getCodeVod() == node.getCode() && entity.getCodePod() == nodepod.getCode()) {
                        if (entity.getDateDo() == null || newDate.before(entity.getDateDo())) {
                           
                        	try {
                        	
                        		String message = getSystemData().decodeItem(this.codeClassifVod, entity.getCodeVod(), getCurrentLang(), null, userId)  + " " + 
                        					 getSystemData().decodeItem(this.codeClassifPod, entity.getCodePod(), getCurrentLang(), null, userId) +
								        " Моля променете датата или изключете тази комбинация от избора си, за можете да направите запис.";
                        		
                        		JSFUtils.addMessage("scForm:panelPod", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "syslogic.plaseInsert") + message);
                                flag = false;
							
                        	} catch (DbErrorException e) {				               
				                LOGGER.error(e.getMessage(), e);
				                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
							}                        	
                        }
                    }
                }
            }
        }
        if (flag) {
            try {
                JPA.getUtil().begin();
                @SuppressWarnings("unused")
				int i = 0;
                for (TreeNode s : selectedNodePod) {
                    i++;
                    SyslogicListEntity syslogicListEntity = new SyslogicListEntity();
                    syslogicListEntity.setCodeList(codeClassif);
                    syslogicListEntity.setCodeVod(node.getCode());
                    SystemClassif nodepod = (SystemClassif) s.getData();
                    syslogicListEntity.setCodePod(nodepod.getCode());
                    syslogicListEntity.setDateOt(newDate);
                    syslogicListDAO.save(syslogicListEntity);
                }
                JPA.getUtil().commit();
                getSystemData().reloadList(codeClassif);
            }
            catch (DbErrorException e) {
                JPA.getUtil().rollback();
                LOGGER.error(e.getMessage(), e);
                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
            }
            finally {
                JPA.getUtil().closeConnection();
            }
            actionSearch();
            JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.succesSaveMsg"));
        }
        
        PrimeFaces.current().ajax().addCallbackParam("validOpisClassif", flag);
    }

    public List<SyslogicListEntity> getListSc() {
        return listSc;
    }

    public void setListSc(List<SyslogicListEntity> listSc) {
        this.listSc = listSc;
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        String column = event.getColumn().getClientId();
        boolean flag = true;
        if (newValue == null && column.contains("dateOt")) {
            JSFUtils.addMessage(column, FacesMessage.SEVERITY_ERROR,
                    getMessageResourceString(Constants.beanMessages, "general.pleaseInsert",
                            getMessageResourceString("labels", "syslogiclist.Date")));
            flag = false;

        }
        if (flag && ((newValue == null && column.contains("dateDo")) ||
                (newValue != null && !newValue.equals(oldValue)))) {
            int rowIndex = event.getRowIndex();
            int i = 0;
            if (listSc.get(rowIndex).getDateDo() != null &&
                    listSc.get(rowIndex).getDateDo().before(listSc.get(rowIndex).getDateOt())) {
                JSFUtils.addMessage(column, FacesMessage.SEVERITY_ERROR,
                        getMessageResourceString(Constants.beanMessages, "general.pleaseInsert",
                                getMessageResourceString("labels", "syslogiclist.Date")));
                flag = false;
            }
            if (flag) {
                for (SyslogicListEntity s : listSc) {
                    if (s.getCodeVod() == listSc.get(rowIndex).getCodeVod() &&
                            s.getCodePod() == listSc.get(rowIndex).getCodePod() && rowIndex != i) {
                        if (newValue == null || listSc.get(rowIndex).getDateOt().before(s.getDateDo()) || s.getDateDo() != null) {
                            JSFUtils.addMessage(column, FacesMessage.SEVERITY_ERROR,
                                    getMessageResourceString(Constants.beanMessages, "general.pleaseInsert",
                                            getMessageResourceString("labels", "syslogiclist.Date")));
                            flag = false;
                        }
                    }
                    i++;
                }
            }
            if (flag) {
                try {
                    JPA.getUtil().begin();
                    syslogicListDAO.save(listSc.get(rowIndex));
                    JPA.getUtil().commit();
                    getSystemData().reloadList(codeClassif);
                } catch (DbErrorException e) {
                    JPA.getUtil().rollback();
                    LOGGER.error(e.getMessage(), e);
                    JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
                } catch (Exception e) {
                    JPA.getUtil().rollback();
                    JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.unexpectedResult"));
                    LOGGER.error(e.getMessage(), e);
                } finally {
                    JPA.getUtil().closeConnection();
                }

                JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(Constants.beanMessages, "general.succesUbdateMsg"));
            } else {
                if (column.contains("dateOt")) {
                    listSc.get(rowIndex).setDateOt((Date) event.getOldValue());
                } else {
                    listSc.get(rowIndex).setDateDo((Date) event.getOldValue());
                }
            }
//            RequestContext context = RequestContext.getCurrentInstance();
//            context.addCallbackParam("validOpisClassif", flag);
            
            PrimeFaces.current().ajax().addCallbackParam("validOpisClassif", flag);
        }
    }
    
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public TreeNode getSelectedNodeVod() {
        return selectedNodeVod;
    }

    public void setSelectedNodeVod(TreeNode selectedNodeVod) {
        this.selectedNodeVod = selectedNodeVod;
    }

    public TreeNode[] getSelectedNodePod() {
        return selectedNodePod;
    }

    public void setSelectedNodePod(TreeNode[] selectedNodePod) {
        this.selectedNodePod = selectedNodePod;
    }

    public TreeNode getRootNodeVod() {
        return rootNodeVod;
    }

    public void setRootNodeVod(TreeNode rootNodeVod) {
        this.rootNodeVod = rootNodeVod;
    }

    public TreeNode getRootNodePod() {
        return rootNodePod;
    }

    public void setRootNodePod(TreeNode rootNodePod) {
        this.rootNodePod = rootNodePod;
    }

    public Date getNewDate() {
        return newDate;
    }

    public void setNewDate(Date newDate) {
        this.newDate = newDate;
    }

    public long getCodeClassifVod() {
        return codeClassifVod;
    }

    public void setCodeClassifVod(long codeClassifVod) {
        this.codeClassifVod = codeClassifVod;
    }

    public long getCodeClassifPod() {
        return codeClassifPod;
    }

    public void setCodeClassifPod(long codeClassifPod) {
        this.codeClassifPod = codeClassifPod;
    }

    public long getCodeClassif() {
        return codeClassif;
    }

    public void setCodeClassif(long codeClassif) {
        this.codeClassif = codeClassif;
    }
}
