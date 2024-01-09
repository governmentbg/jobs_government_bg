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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.PageText;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.PageTextDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named
@ViewScoped
public class PagesText extends PJobsBean {	
	
	/** Основен java клас за въвеждане/актуализация на статични текстове към различни страници
	 * 
	 * 
	 */
	private static final long serialVersionUID = -3878807729461872099L;
	static final Logger LOGGER = LoggerFactory.getLogger(PagesText.class);
	
	private Long userId;
	private Long lang = null;
	private PageText txtPages;
	private PageTextDAO pageTxtDAO;
	private Long sect;
	
	// Прикачени файлове
	private Files file = new Files();
	private FilesDAO filesDAO;
	private List<Files> filesList = new ArrayList<>();
	private List<Files> deleteFilesList = new ArrayList<>();

	
	/** Иницира първоначалните стойности на обекта
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.pageTxtDAO = new PageTextDAO(userId);
			this.filesDAO = new FilesDAO(this.userId);
			this.txtPages = new PageText();			
			this.lang = getCurrentLang();
			
			String sect = JSFUtils.getRequestParameter("sect");
			
			if (sect != null && !sect.trim().isEmpty()){
				
				this.sect = Long.valueOf(sect);
				getTextByLang(this.sect);	 							
			
			} else {				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.invalidParameter"));
			}
		
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;	
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));		
		}		
	}
	
	/** Метод за измъкване на текстовете от всяка страница на езика, който е подаден
	 * 
	 * @param sect
	 * 
	 */
	private void getTextByLang (Long sect) {
		
		try {
			
			txtPages = new PageTextDAO(getUserData().getUserId()).loadPageTextByCodeSectLang(sect, lang);
			
			if (txtPages == null) {
				txtPages = new PageText();
			}
			
			txtPages.setCodePage(sect);
			
			this.filesList = new ArrayList<>();
			if (this.txtPages.getId() != null) {
				this.filesList = this.filesDAO.findByCodeObjAndIdObjAndLang(sect, this.txtPages.getId(), this.lang);
			}
		
		} catch (NumberFormatException e) {
			LOGGER.error("Грешка при обработка на данните!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.formatExc"));
		
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при работа с базата! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}		
		
	}
	
	/** метод за смяна на езика и измъкването ан текстовете за този език за конкретната страница
	 * 
	 * 
	 */
	public void actionChangeLang() {
		
		getTextByLang(txtPages.getCodePage());
	}
	
	/** Записва/редактира въведените текстове към конкретната страница на избрания език в БД
	 * 
	 * 
	 */
	public void actionSave() {
		
		boolean check = true;
		
		if (txtPages.getPageText() == null || txtPages.getPageText().isEmpty()) {
			JSFUtils.addMessage("formPageText:staticTxt", FacesMessage.SEVERITY_ERROR,getMessageResourceString(Constants.beanMessages,"general.pleaseInsert", getMessageResourceString("labels", "pagesText.txt")));
			check = false;
		}
		
		if (check) {
			
			try {
				
				JPA.getUtil().begin();
				
				txtPages.setLang(lang); 
			
				txtPages = pageTxtDAO.save(txtPages);			
				
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
						this.filesList.get(i).setCodeObject(this.txtPages.getCodePage());
						this.filesList.get(i).setIdObject(this.txtPages.getId());
						this.filesList.get(i).setLang(this.lang); 
						
						this.filesDAO.save(this.filesList.get(i));
					
					} else { // при редакция не е измъкнат целия обект и затова ще се прави ъпдейт на 4 полета - description, visibleOnSite, userLastMod, dateLastMod
						
						this.filesDAO.updateFile(this.filesList.get(i).getDescription(), this.userId, new Date(), this.filesList.get(i).getId()); 		
					}			
				}
				
				JPA.getUtil().flush();
				
				JPA.getUtil().commit();
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
				
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при запис на обява! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
	
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
	
			} finally {
				JPA.getUtil().closeConnection();				
			}			
		}		
	}	
	
	// ************************************************* GET & SET ***************************************************** //

	/** GET & SET
	 * 
	 * 
	 */
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getLang() {
		return lang;
	}

	public void setLang(Long lang) {
		this.lang = lang;
	}

	public PageText getTxtPages() {
		return txtPages;
	}

	public void setTxtPages(PageText txtPages) {
		this.txtPages = txtPages;
	}
	
	public Long getSect() {
		return sect;
	}

	public void setSect(Long sect) {
		this.sect = sect;
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

	public List<Files> getDeleteFilesList() {
		return deleteFilesList;
	}

	public void setDeleteFilesList(List<Files> deleteFilesList) {
		this.deleteFilesList = deleteFilesList;
	}
	
	// ************************************************* END GET & SET ***************************************************** //	
	
	// ************************************************* FILES ************************************************************* //
	
	/** Избор на файлове за прикачване към избраната страница
	 * 
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
		
	/** Извличане от БД на запазените файлове към избраната страница
	 * 
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
	
}
