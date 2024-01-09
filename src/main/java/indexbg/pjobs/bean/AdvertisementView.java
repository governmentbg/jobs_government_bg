package indexbg.pjobs.bean;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SysClassifUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.Advertisement;
import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.db.dao.AdvertisementDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class AdvertisementView extends PJobsBean {	

	/** Основен java клас за разглеждане на обяви за мобилност
	 * 
	 * 
	 */
	private static final long serialVersionUID = -9106317698163149616L;
	
	static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementView.class);
	
	private Advertisement adver;
	private AdvertisementDAO adverDAO;	
	
	// Прикачени файлове	
	private FilesDAO filesDAO;
	private List<Files> filesList = new ArrayList<>();
	
	private String direkcia;
	
	/** Иницира първоначалните стойности на обекта
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!!");
		
		try {
			
			this.adverDAO = new AdvertisementDAO(Constants.PORTAL_USER);
			this.filesDAO = new FilesDAO(Constants.PORTAL_USER);
			this.adver = new Advertisement();			
			
			Long idObj = null;
			
			if (JSFUtils.getRequestParameter("idObj")!= null && ValidationUtils.isNumber(JSFUtils.getRequestParameter("idObj"))) {
			
				if (JSFUtils.getRequestParameter("idObj") != null && !JSFUtils.getRequestParameter("idObj").equals("")) {
					idObj = Long.valueOf(JSFUtils.getRequestParameter("idObj"));	
				}
				
				if (idObj != null) {
					
					this.adver = this.adverDAO.findById(idObj);
					
					if (this.adver != null) {
						this.filesList = this.filesDAO.findByCodeObjAndIdObj(this.adver.getCodeMainObject(), this.adver.getId());
						setParentDirAdmStruc();
					} else {
						redirectExternal("pageNotFound.jsf");	
					}
				}
			
			} else {
				redirectExternal("pageNotFound.jsf");				
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));	
		} 	
		
	}
	
	/** 
	 * Метод за измъкване на дирекцията и отдела на звеното
	 */
	public void setParentDirAdmStruc() {
		
		long zveno = this.adver.getUnit();
		
		try {
			
			this.direkcia = "";
			
			List<SystemClassif> classifList = new AdministrationDAO(Constants.PORTAL_USER, getSystemData()).builAdmdStrOfSubject(this.adver.getAdministration(), new Date());
			
			List<SystemClassif> parents = SysClassifUtils.getParents(classifList, zveno);

			for (int i = parents.size()-1; i >= 0; i--) {
				SystemClassif par = parents.get(i);
				if (par.getCode() == zveno) {					
					break;
				} else {
					if (direkcia.isEmpty()) {
						this.direkcia = par.getTekst() + "</br>";
					} else {
						this.direkcia += " - " + par.getTekst();
					}					
				}				
			}
		
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при изграждане на дирекции и отдели!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		}
	}
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */	

	public Advertisement getAdver() {
		return adver;
	}

	public void setAdver(Advertisement adver) {
		this.adver = adver;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}
	
	public String getDirekcia() {
		return direkcia;
	}

	public void setDirekcia(String direkcia) {
		this.direkcia = direkcia;
	}
		
	// ************************************************* END GET & SET ***************************************************** //		
	
	// ************************************************* FILES ************************************************************* //
			
	/** Извличане от БД на запазените файлове към събитието
	 * 
	 * @param file
	 */
		public void download(Files file){ 
			
			boolean ok = true;
			
			if(file.getContent() == null && file.getId() != null) {
				
				try {					
					
					file = this.filesDAO.findById(file.getId());
					
					if(file.getPath() != null && !file.getPath().isEmpty()){
						Path path = Paths.get(file.getPath());
						file.setContent(java.nio.file.Files.readAllBytes(path));
					}
				
				} catch (DbErrorException e) {
					LOGGER.error("DbErrorException: " + e.getMessage());
					ok = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
				
				} catch (IOException e) {
					LOGGER.error("IOException: " + e.getMessage());
					ok = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
					LOGGER.error(e.getMessage(), e);
				
				} catch (Exception e) {
					LOGGER.error("Exception: " + e.getMessage());
					ok = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
				
				}  finally {
					JPA.getUtil().closeConnection();
				}
			}
			
			if(ok){
				
				try {
					
					FacesContext facesContext = FacesContext.getCurrentInstance();
				    ExternalContext externalContext = facesContext.getExternalContext();
				    
				    
				    HttpServletRequest request =(HttpServletRequest)externalContext.getRequest();
					String agent = request.getHeader("user-agent");

					
					String codedfilename = ""; 
					
					if (null != agent &&  (-1 != agent.indexOf("MSIE") || (-1 != agent.indexOf("Mozilla") && -1 != agent.indexOf("rv:11")) || (-1 != agent.indexOf("Edge"))  ) ) {
						codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
					} else if (null != agent && -1 != agent.indexOf("Mozilla")) {
						codedfilename = MimeUtility.encodeText(file.getFilename(), "UTF8", "B");
					} else {
						codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
					}
					
					
					
					
				    externalContext.setResponseHeader("Content-Type", "application/x-download");
				    externalContext.setResponseHeader("Content-Length", file.getContent().length + "");
				    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + codedfilename + "\"");
					externalContext.getResponseOutputStream().write(file.getContent());
					
					facesContext.responseComplete();
				
				} catch (IOException e) {
					LOGGER.error("IOException: " + e.getMessage());
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
					LOGGER.error(e.getMessage(), e);
				
				} catch (Exception e) {
					LOGGER.error("Exception: " + e.getMessage());
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
				} 
			}
			
		}			
		
		// ************************************************* END FILES ************************************************************* //
		
}
