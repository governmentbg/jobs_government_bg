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

import org.apache.commons.lang3.math.NumberUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.Bursary;
import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.dao.BursaryDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named ("bursaryBean")
@ViewScoped 
public class BursaryProgramsBean extends PJobsBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2324768526029212577L;
	static final Logger LOGGER = LoggerFactory.getLogger(BursaryProgramsBean.class);
	private Bursary bursary;
	private Long userId;
	private BursaryDAO bDao;
	private boolean admin;
	//private boolean odobrena=false; Скрито на 04.08.2023 по искане  на МС
	
	// Прикачени файлове
	private Files file = new Files();
	private FilesDAO filesDAO;
	private List<Files> filesList = new ArrayList<>();
	private List<Files> deleteFilesList = new ArrayList<>();


	public BursaryProgramsBean() {
		
	}
	
	@PostConstruct
	public void init() {
		setBursary(new Bursary());
		
		try {
			this.userId = getUserData().getUserId();
			
			bDao =  new BursaryDAO(userId);
			filesDAO = new FilesDAO(userId);
			
	
			if(getUserData().getTypeUser() != null  && getUserData().getTypeUser().longValue()==Constants.CODE_ZNACHENIE_TIP_POTR_VATR) {
				admin= true;
			}
			
			String idObj =JSFUtils.getRequestParameter("idObj");			
			
			if (idObj!=null) {
				if (NumberUtils.isDigits(idObj)) {
					bursary = bDao.findById(Long.valueOf(idObj));
					if(bursary==null) {
						redirectExternal("pageNotFound.jsf");
					}
					this.filesList = this.filesDAO.findByCodeObjAndIdObj(this.bursary.getCodeMainObject(), this.bursary.getId());
//				    if(bursary.getStatus()!=null && bursary.getStatus().longValue() == Constants.CODE_ZNACHENIE_STATUS_BURSARY_ODOBRENA) {
//				    	odobrena = true;
//				    }else {
//				    	odobrena = false;
//				    }
				} else {
					redirectExternal("pageNotFound.jsf");				
				}
			} else {
				actionNew();			
			}
			
			
		
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			this.userId = -1L;
		} finally {
			JPA.getUtil().closeConnection();
		}	
	}
    private boolean checkData() {
		
		boolean flagSave = false;
		if(bursary.getAdministration()==null){
			JSFUtils.addMessage("exhbForm:admStr",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.admStruc")));	
	
			flagSave=true;
		}
		
		if(bursary.getSubject()==null || "".equals(bursary.getSubject())){
			JSFUtils.addMessage("exhbForm:spec",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.subject")));	
	
			flagSave=true;
		}
		
		if(bursary.getBursary()==null){
			JSFUtils.addMessage("exhbForm:razmer",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.size")));	
	
			flagSave=true;
		}
		
		if(bursary.getNum()==null){
			JSFUtils.addMessage("exhbForm:broi",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.num")));	
	
			flagSave=true;
		}

		if(bursary.getDateFrom()==null){
			JSFUtils.addMessage("exhbForm:dateFrom",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "bursary.dateFrom")));	
	
			flagSave=true;
		}else if(bursary.getId()==null && DateUtils.startDate(bursary.getDateFrom()).before(DateUtils.startDate(getToday()))) {
			JSFUtils.addMessage("exhbForm:dateFrom",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","bursaryPrograms.dateFromBeforeToday"));	
			
			flagSave=true;
		}
		
		if(bursary.getStatus()==null){
			JSFUtils.addMessage("exhbForm:status",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.status")));	
	
			flagSave=true;
		}
		
		if(bursary.getDateFrom()!=null && bursary.getDateTo()!=null && bursary.getDateTo().before(bursary.getDateFrom())) {
			JSFUtils.addMessage("exhbForm:dateTo",FacesMessage.SEVERITY_ERROR,getMessageResourceString(null,getMessageResourceString("labels", "general.dateFromLessDateTo")));	
			
			flagSave=true;
		}

		return flagSave;
	}


    public void actionSave() {
		if(checkData()){
			return;
		}
		try {
		    
			JPA.getUtil().begin();
			bDao.save(bursary);
			
			JPA.getUtil().flush();
			
			if (!this.deleteFilesList.isEmpty()) {
				for (int i = 0; i < this.deleteFilesList.size(); i++) {
					if (this.deleteFilesList.get(i).getId() != null) {
						this.filesDAO.delFromFilesText(this.deleteFilesList.get(i).getId());
						this.filesDAO.deleteById(this.deleteFilesList.get(i).getId());
					}
				}
			}
	
			for (int i = 0; i < this.filesList.size(); i++) {
				
				if (this.filesList.get(i).getId() == null) {
					this.filesList.get(i).setCodeObject(this.bursary.getCodeMainObject());
					this.filesList.get(i).setIdObject(this.bursary.getId());	
					
					this.filesDAO.save(this.filesList.get(i));
				
				} else { 
					
					this.filesDAO.updateFile(this.filesList.get(i).getDescription(), this.userId, new Date(), this.filesList.get(i).getId()); 		
				}			
			}
			
			JPA.getUtil().commit();
					
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));	
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при запис на стипендиантска програма! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
		}
		
	}
    
    public void actionNew() {
		
		bursary= new Bursary();
		bursary.setStatus(Constants.CODE_ZNACHENIE_STATUS_BURSARY_ODOBRENA);
		bursary.setStatusDate(new Date());

		//odobrena=false;
		filesList = new ArrayList<>();
		try {
			bursary.setAdministration(getUserData().getCodeOrg());
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));	
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		} 
	}

	
    public void actionDelete(){
		if(bursary.getId()!=null){
			try {
				JPA.getUtil().begin();
				
				if (!this.filesList.isEmpty()) {
					for (int i = 0; i < filesList.size(); i++) {				
						this.filesDAO.deleteById(Long.valueOf(filesList.get(i).getId()));				
					}
				}
				
				JPA.getUtil().flush();
				
				bDao.deleteById(bursary.getId());
				JPA.getUtil().commit();	
		
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));	
				
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при изтриване на стипендиантска програма! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			}finally {
				JPA.getUtil().closeConnection();
			}
			
			actionNew();
		}		
	}
    
//    public void addMessage() {
//    	if(odobrena) {
//			bursary.setStatus(Constants.CODE_ZNACHENIE_STATUS_BURSARY_ODOBRENA);
//		}else {
//			bursary.setStatus(Constants.CODE_ZNACHENIE_STATUS_BURSARY_REG);
//		}	
//        String summary = odobrena ? "Одобрена" : "Регистрирана";
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
//    }
	
    
 // ************************************************* FILES ************************************************************* //
	
 		/** Избор на файлове за прикачване 
 		 * 
 		 * @param event
 		 */
 		public void uploadFileListener(FileUploadEvent event){		
 			
 			try {
 				
 				UploadedFile upFile = event.getFile();
 				
 				Files fileObject = new Files();
 				fileObject.setFilename(upFile.getFileName());
 				fileObject.setContentType(upFile.getContentType());
 				fileObject.setContent(upFile.getContent());	
 				
 				this.filesList.add(fileObject);
 			
 			} catch (Exception e) {
 				LOGGER.error("Exception: " + e.getMessage());	
 			} 
 		}
 			
 		/** Извличане от БД на запазените файлове 
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
 			
 		/** Премахва избрания файл
 		 * 
 		 * @param file
 		 */
 		public void remove(Files file){
 				
 			this.filesList.remove(file);
 			this.deleteFilesList.add(file);			
 		}		
 			
 		// ************************************************* END FILES ************************************************************* //
 		
	public Bursary getBursary() {
		return bursary;
	}

	public void setBursary(Bursary bursary) {
		this.bursary = bursary;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getToday(){
		return new Date();
	}


//	public boolean isOdobrena() {
//		return odobrena;
//	}
//
//	public void setOdobrena(boolean odobrena) {	
//		this.odobrena = odobrena;		
//	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Files getFile() {
		return file;
	}

	public void setFile(Files file) {
		this.file = file;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

}
