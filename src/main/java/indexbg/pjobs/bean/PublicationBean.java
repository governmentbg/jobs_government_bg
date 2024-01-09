package indexbg.pjobs.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.indexbg.indexui.navigation.Navigation;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectInUseException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.faces.context.ExternalContext;

import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.Publication;
import indexbg.pjobs.db.PublicationLang;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.PublicationDAO;
import indexbg.pjobs.db.dao.PublicationLangDAO;
import indexbg.pjobs.system.PJobsBean;

import indexbg.pjobs.system.Constants;


@Named("publData")
@ViewScoped
public class PublicationBean extends PJobsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1984244391412755105L;


	/**
	 * Основен javaBean клас за въвеждане/актуализация на публикациите
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(PublicationBean.class);
	
	
	private Publication publication = new Publication();
	private Long idPubl = null;
	private String sectionText="";
//	private String publTypeText="";
	private Long codeSection=null;
//	private Long codeType=null;
	private Long idUser=null;
	private StreamedContent imageCont=null;

	private Long lang=null;
	private Long oldLang=null;
	private HashMap<Long, String> langHM= new HashMap<Long, String>();
	private PublicationLang publSelLang = new PublicationLang();
	private List<PublicationLang> publLangList = new ArrayList<PublicationLang>();
	private List<PublicationLang> pubLdelLang= new ArrayList<PublicationLang>();
	private HashMap<Long, PublicationLang> publListHM = new HashMap<Long, PublicationLang>();
	private Boolean codeYT=false;
	private HashMap<Long, String> titleAttach = new HashMap<Long, String>();
	private HashMap<Long, String> typeFilesAttach = new HashMap<Long, String>();
	private HashMap<Long, String> typeFilesMessages = new HashMap<Long, String>();
	
	//Files
	
	private List<Files> filesList = new ArrayList<Files>();
	private List<Files> deleteFilesList = new ArrayList<Files>();
	private HashMap<Long, List<Files>> filesListHM = new HashMap<Long, List<Files>>();
	private String titleText="";
	/**Инициира/сетва първоначалните стойности на атрибутите на обектите. Чете предадените параметри от други екрани 
	 * 
	 */
	@PostConstruct
	public void initData(){
		LOGGER.debug("PostConstruct!");
		
		
		try{
			this.idUser=getUserData().getUserId();
		} catch (ObjectNotFoundException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.objectNotFound"), e.getMessage());
			LOGGER.error(e.getMessage(), e);
			this.idUser = -1L;
		}

		
		String par =JSFUtils.getRequestParameter("idPubl");
		
		actionClear();
		fillAttachTitleHM();
		clearListHM();
		
		if (par != null && !par.trim().isEmpty()){
			this.setIdPubl(Long.valueOf(par));
			loadPublById(this.idPubl);
		}

	}
	
	/**Изтрива стойностите на определени атрибути на обектите
	 * 
	 */
	public void actionClear(){
		
		this.publication = new Publication();
		this.publLangList = new ArrayList<PublicationLang>();
		this.publSelLang = new PublicationLang();
		this.setIdPubl(null);
		this.sectionText="";
//		this.publTypeText="";
		this.codeSection=null;
//		this.codeType=null;
		
		this.imageCont=null;
		this.filesList = new ArrayList<>();
		this.deleteFilesList = new ArrayList<>();
		this.codeYT=false;
		this.lang = getCurrentLang();
		this.oldLang = getCurrentLang();
		clearListHM();
		fillLangHM();
		
	//	this.publSelLang.setLang(this.lang);
	}

	public Publication getPublication() {
		return publication;
	}

	public void setPublication(Publication publication) {
		this.publication = publication;
	}

	/** Записва в БД на въведените/актуализираните информационни обекти/публикации
	 * 
	 */
	public void actionSave(){
		
		if(null==this.publSelLang.getLang())
			this.publSelLang.setLang(getOldLang());
		this.publListHM.put(getOldLang(), this.publSelLang);
		if (!checkData())
			return;
	
		setAllLangsForPublFiles();
		
		
		try {
			
			PublicationLangDAO pubLangDAO = new PublicationLangDAO(this.idUser);
			JPA.getUtil().begin();
			new PublicationDAO(this.idUser).save(this.publication);
			
			// 1. Publication langs
			
			// Delete join records for lang  
			if (!this.pubLdelLang.isEmpty()) {
				for (int i = 0; i < this.pubLdelLang.size(); i++) {
					if (this.pubLdelLang.get(i).getId() != null) {
						pubLangDAO.deleteById(this.pubLdelLang.get(i).getId());
					}
				}
			}
			
			// езиковите версии на публикациите 
			if (null!=this.publLangList) {
				for (int i = 0; i < this.publLangList.size(); i++) {
					if (null==this.publLangList.get(i).getId()) 
						this.publLangList.get(i).setPubId(this.publication.getId());
					
					pubLangDAO.save(this.publLangList.get(i));
				}
			}

			//2. File Langs
			// Delete join records for lang  
			FilesDAO filesDAO = new FilesDAO(this.idUser);
			
			if (!this.deleteFilesList.isEmpty()) {
				for (int i = 0; i < this.deleteFilesList.size(); i++) {
					if (null!=this.deleteFilesList.get(i).getId()) 
						filesDAO.deleteById(this.deleteFilesList.get(i).getId());
				}
			}
			
			// езиковите версии на files 
			if (this.filesList.size() > 0) {
				for (int i = 0; i < this.filesList.size(); i++) {
					if (this.filesList.get(i).getId() == null) {
						this.filesList.get(i).setCodeObject(this.publication.getCodeMainObject());
						this.filesList.get(i).setIdObject(this.publication.getId());	
						filesDAO.save(filesList.get(i));
					} else { //TODO - при четене от базата не се взема bytearrea и затова се прави update на 4 полета - description, visibleOnSite, userLastMod, dateLastMod
						filesDAO.updateFile(filesList.get(i).getDescription(), this.idUser, new Date(), this.filesList.get(i).getId()); 		
					}	
				}
			}
			
			JPA.getUtil().commit();
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));

			if (!this.pubLdelLang.isEmpty()) {
				this.pubLdelLang.clear();
			}
			if (!this.deleteFilesList.isEmpty()) {
				deleteFilesList.clear();
			}
			
			refreshImagePub();
						
			
			} catch (DbErrorException e) {
			 	LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
				JPA.getUtil().rollback();
			} catch (ObjectInUseException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.objectInUse"), e.getMessage());	
				JPA.getUtil().rollback();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());	
				JPA.getUtil().rollback();
			} finally {
				JPA.getUtil().closeConnection();
		} 
	
	}
	
	/** Изтрива от БД на определена публикация
	 * 
	 */
	public void actionDelete(){
		
		try {
			
			JPA.getUtil().begin();		
			FilesDAO filesDAO = new FilesDAO(getIdUser());
			// Прикачените файлове
			for (Files item: this.filesList) {
				if (null!=item.getId()){
					filesDAO.deleteById(item.getId());
				}
			}
			
			for (Files item: this.deleteFilesList) {
				if (null!=item.getId()){
					filesDAO.deleteById(item.getId());
				}
			}
			
			// Публикациите - езиковите записи
			PublicationLangDAO pubLangDAO = new PublicationLangDAO(getIdUser());
			for (PublicationLang item: this.publLangList) {
				if (null!=item.getId())
					pubLangDAO.deleteById(item.getId());
			}
			for (PublicationLang item: this.pubLdelLang) {
				if (null!=item.getId())
					pubLangDAO.deleteById(item.getId());
			}
			
			// Публикациите
			if (null!=this.publication.getId())
				new PublicationDAO(this.idUser).deleteById(this.publication.getId());
				
			JPA.getUtil().commit();			
				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));
			
			if (!this.deleteFilesList.isEmpty()) 
				this.deleteFilesList.clear();
			if (!this.pubLdelLang.isEmpty()) 
				this.pubLdelLang.clear();
			
			
			Navigation navHolder = new Navigation();			
		    int i = navHolder.getNavPath().size();
		    
		    if(i > 2) {	
		    	navHolder.goTo(String.valueOf(i-2));
		    } else if(i > 1) {	navHolder.goBack();   }
			
			
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"), e.getMessage());
			JPA.getUtil().rollback();
		} catch (ObjectInUseException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.objectInUse"), e.getMessage());
			JPA.getUtil().rollback();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());	
			JPA.getUtil().rollback();
		} finally {
			JPA.getUtil().closeConnection();
		}
		
		
	}

	public String getSectionText() {
		return sectionText;
	}

	public void setSectionText(String sectionText) {
		this.sectionText = sectionText;
	}

	public Long getCodeSection() {
		return codeSection;
	}

	public void setCodeSection(Long codeSection) {
		this.codeSection = codeSection;
		this.publication.setSection(codeSection);
	}


	/**Проверява дата от и дата до на публикациите
	 * @param nom
	 */
	public boolean checkDates(int nom){
		boolean check = true;
		if (this.publication.getDateFrom() != null && this.publication.getDateTo() != null) {
			if(this.publication.getDateFrom().compareTo(this.publication.getDateTo()) > 0) {
				check=false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","publ.dateFromLessDateTo"));
			}
		} 
		return check;
	}
	
	
	/** Избор на изображение към публикация 
	 * @param event
	 */
	public void handleFileUpload(FileUploadEvent event) {
        try {
        	if (null!=event){
        		this.publication.setImagePub(event.getFile().getContent());
        		InputStream imageStream = new ByteArrayInputStream(this.publication.getImagePub());
        	//	this.imageCont = new DefaultStreamedContent(imageStream, "image/jpeg");
        		this.imageCont = DefaultStreamedContent.builder().contentType("image/jpeg").name("").stream(() -> imageStream).build();
        	}else{
        		
    			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errorUploadFile"));
        	}
        	
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errorUploadFile"));
		}
		
    }

	
	public Date getCurrentDate() {
		return new Date();
	}

	public StreamedContent getImageCont() {
		return imageCont;
	}

	public void setImageCont(StreamedContent imageCont) {
		this.imageCont = imageCont;
	}

	
	
/** Избор на файлове за присъединяване към публикациите 
 * @param event
 */
public void uploadFileListener(FileUploadEvent event){		
		
		try {
			
			UploadedFile upFile = event.getFile();
			if(!checkUploadedFileForDuplicate(upFile)) {
				Files fileObject = new Files();
				fileObject.setFilename(upFile.getFileName());
				fileObject.setDescription(upFile.getFileName());
				fileObject.setContentType(upFile.getContentType());
				fileObject.setContent(upFile.getContent());
				fileObject.setIdObject(getIdUser());
				fileObject.setCodeObject(this.publication.getCodeMainObject());
				fileObject.setLang(this.lang);
				getFilesList().add(fileObject);
				this.filesListHM.get(this.lang).add(fileObject);
			}
			
		
		} catch (Exception e) {
			LOGGER.error("Exception: " + e.getMessage(),e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при качване на файла!",e.getMessage());
		} 
	}

	public List<Files> getFilesList() {
		return filesList;
	}
	
	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}
	
	public void remove(Files file){
		if (null!=file) {
			this.deleteFilesList.add(file);
			this.filesList.remove(file);
			this.filesListHM.get(this.lang).remove(file);
		}
	}

	public List<Files> getDeleteFilesList() {
		return deleteFilesList;
	}

	public void setDeleteFilesList(List<Files> deleteFilesList) {
		this.deleteFilesList = deleteFilesList;
	}
	
		
	/** Извлича присъединените файлове от БД и ги предлага за разглеждане/съхраняване при клиента 
	 * @param file
	 */
	public void download(Files file){
		try {
			
			if(file.getContent() == null && file.getId() != null) {
			
				FilesDAO filesDAO = new FilesDAO(getIdUser());
				file = filesDAO.findById(file.getId());
				
				if(file.getPath() != null && !file.getPath().isEmpty()){
					Path path = Paths.get(file.getPath());
					file.setContent(java.nio.file.Files.readAllBytes(path));
				}
			
			}
						
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
			
		} catch (DbErrorException e) {
			LOGGER.error("DbErrorException: " + e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"),e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при сваляне на файла!: ",e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при сваляне на файла!: ",e.getMessage());
		}
	}

	
	/** Обработва езиковите записи 
	 * 
	 */
	public void setAllLangsForPublFiles(){

		// 1. Publication Langs Update DB records, delete old and insert new
		List<PublicationLang> pubLnew= new ArrayList<PublicationLang>();
		this.pubLdelLang= new ArrayList<PublicationLang>();
		Iterator<PublicationLang> iterator = this.publListHM.values().iterator();
		PublicationLang pubL = new PublicationLang();
		int n;
		boolean ch=true;
		while(iterator.hasNext()){
			n=0;
			pubL = new PublicationLang();
			pubL=(PublicationLang)iterator.next();
			if (null==pubL)
				continue;
			ch=checkRequiredAttr(pubL);
			if (this.publLangList.size() > 0) {
				for (int i = 0; i < this.publLangList.size(); i++) {
					if (this.publLangList.get(i).getId().equals(pubL.getId())) {
						if(ch) {
							this.publLangList.set(i, pubL);
						}else{
							this.pubLdelLang.add(this.publLangList.get(i));
						}
						n++;
					}
				}
			}

			if (n==0 && ch)
				pubLnew.add(pubL);
		}
		
		if (null!=this.pubLdelLang && this.pubLdelLang.size()>0)
			this.publLangList.removeAll(this.pubLdelLang);
		
		if (!pubLnew.isEmpty())
			this.publLangList.addAll(pubLnew);
		
		
		/*// 2. Files Langs Update DB records, delete old and insert new
				List<Files> filenew= new ArrayList<Files>();
				this.deleteFilesList= new ArrayList<Files>();
				Iterator<Files> iter = filesListHM.values().iterator();
				Files file = new Files();
				
				ch=true;
				while(iterator.hasNext()){
					n=0;
					file = new Files();
					file=(Files)iter.next();
					if (null==file)
						continue;
					if(null==file.getLang())
						ch=false;
					if (this.filesList.size() > 0) {
						for (int i = 0; i < this.filesList.size(); i++) {
							if (this.filesList.get(i).getId().equals(file.getId())) {
								if(ch) {
									this.filesList.set(i, file);
								}else{
									this.deleteFilesList.add(this.filesList.get(i));
								}
								n++;
							}
						}
					}

					if (n==0 && ch)
						filenew.add(file);
				}
				
				if (null!=this.deleteFilesList && this.deleteFilesList.size()>0)
					this.filesList.removeAll(this.deleteFilesList);
				
				if (!filenew.isEmpty())
					this.filesList.addAll(filenew);
		
		*/
		
		
	}
	
	/**Проверка на атрибутите на публикация - задължителни, преди запис в БД
	 * @return
	 */
	public boolean checkRequiredAttr(PublicationLang pub){
		boolean ver = true;
		if((null==pub.getAnnotation()|| pub.getAnnotation().isEmpty()) && (null==pub.getFullText() || pub.getFullText().isEmpty()) &&
			(null==pub.getOtherInfo() || pub.getOtherInfo().isEmpty()) && (null==pub.getTitle() || pub.getTitle().isEmpty()) && 
			(null==pub.getUrlPub() || pub.getUrlPub().isEmpty())|| null==pub.getLang()){
			ver = false;
		}
		
		return ver;
	}
	
	/**Проверка на въведените данни на публикация, преди запис в БД
	 * @return
	 */
	
	public boolean checkData(){
		boolean ver = true;
		// 1. Required
		
		if (null==this.publication.getSection()) {
			JSFUtils.addMessage("formPublData:idSectionText", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","publ.nosection"));
			ver=false;
		}
		if (null==this.publication.getTypePub()) {
			JSFUtils.addMessage("formPublData:idPublTypeText", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","publ.noTypePubl"));
			ver=false;
		}
		
		if (null==this.publication.getDateFrom()) {
			JSFUtils.addMessage("formPublData:dateOt", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","publ.noDateOt"));
			ver=false;
		}
		
		if(publSelLang == null || publSelLang.getTitle() == null || publSelLang.getTitle().trim().isEmpty()) {
			JSFUtils.addMessage("formPublData:idTitle", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","publ.noTitle"));
			ver=false;
		}
		
		/*if(null!=this.publLangList && this.publLangList.size()>0) {
			for (int i = 0; i < this.publLangList.size(); i++) {
				if (null!=this.publLangList.get(i).getId()){
					if(null==this.publLangList.get(i).getTitle()) {
						JSFUtils.addMessage("formPublData:idTitle", FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","publ.noTitle"));
						ver=false;
					}
				}
			}
		}*/
		

		// 2. Dates
		
		if(ver) {
			ver=checkDates(1);
			
			/*this.publication.setDateFrom(this.getDateFrom());
			this.publication.setDateTo(this.getDateTo());*/
			
		}
		
		
		

		return ver;		
	}
	
	


	public Long getIdPubl() {
		return idPubl;
	}


	public void setIdPubl(Long idPubl) {
		this.idPubl = idPubl;
	}

	
	/** Извлича данни за определена публикация по ид.
	 * @param idPubl
	 */
	
	public void loadPublById(Long idPubl){
		
		try {
			this.publication = (Publication) new PublicationDAO(this.idUser).findById(this.idPubl);
			/*this.dateFrom=this.publication.getDateFrom();
			this.dateTo=this.publication.getDateTo();*/
			

			this.codeSection = this.publication.getSection();
			if(null!=this.codeSection){
				this.sectionText= getSystemData().decodeItem(Constants.CODE_SYSCLASS_SECTION, this.codeSection, getCurrentLang(), new Date(), this.idUser);
			}
			
			/*this.codeType = this.publication.getTypePub();
			if(null!=this.codeType){
				this.publTypeText= getSystemData().decodeItem(Constants.CODE_SYSCLASS_PUBL_TYPE, this.codeType, getCurrentLang(), new Date(), this.idUser);
			}*/
			
			refreshImagePub();
    		
			// Езиковите версии
			this.publLangList = (List<PublicationLang>) new PublicationLangDAO(this.idUser).findByIdPubl(this.idPubl);
			this.filesList = (List<Files>)new FilesDAO(this.idUser).findByCodeObjAndIdObj(Constants.CODE_OBJECT_PUBLICATION, this.idPubl);
			
			
			loadLangHM();
			
//			fillAttachTitleHM();
			
			} catch (DbErrorException e) {
			 	LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
				
			} finally {
				JPA.getUtil().closeConnection();
			}

	}
	
	public Long getIdUser() {
		return idUser;
	}
	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	
	public void clearListHM() {
		List<SystemClassif> langElementsList= new ArrayList<SystemClassif>();
		try {
			langElementsList=getSystemData().getSysClassification(Constants.CODE_CLASSIF_LANG, new Date(), getCurrentLang(), this.idUser);
		} catch (DbErrorException e) {
			LOGGER.error("DbErrorException: " + e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"),e.getMessage());
		}
		this.publListHM = new HashMap<Long, PublicationLang>();
		for (SystemClassif item: langElementsList){
			this.publListHM.put(Long.valueOf(item.getCode()), new PublicationLang());
		}
		
		this.filesListHM = new HashMap<Long, List<Files>>();
		for (SystemClassif item: langElementsList){
			this.filesListHM.put(Long.valueOf(item.getCode()), new ArrayList<Files>());
		}
	}
	
	public void fillLangHM() {
		this.langHM= new HashMap<Long, String>();
		
		List<SystemClassif> langElementsList= new ArrayList<SystemClassif>();
		try {
			langElementsList=getSystemData().getSysClassification(Constants.CODE_CLASSIF_LANG, new Date(), getCurrentLang(), this.idUser);
		} catch (DbErrorException e) {
			LOGGER.error("DbErrorException: " + e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"),e.getMessage());
		}
		for (SystemClassif item: langElementsList){
			if(null!=item) {
				int l = (int)item.getCode();
				switch (l) {
			         case (int)Constants.CODE_ZNACHENIE_LANG_BG: 
			        	 this.langHM.put(Long.valueOf(Constants.CODE_ZNACHENIE_LANG_BG), "bg");
			        	 break;
			         case (int)Constants.CODE_ZNACHENIE_LANG_EN: 
			        	 this.langHM.put(Long.valueOf(Constants.CODE_ZNACHENIE_LANG_EN), "en");
			        	 break;
			         default: 
			        	
			         	break;
				}
				
			}
			
		}
	
	}

	
	public List<PublicationLang> getPublLangList() {
		return publLangList;
	}

	public void setPublLangList(List<PublicationLang> publLangList) {
		this.publLangList = publLangList;
	}

	public HashMap<Long, PublicationLang> getPublListHM() {
		return publListHM;
	}

	public void setPublListHM(HashMap<Long, PublicationLang> publListHM) {
		this.publListHM = publListHM;
	}

	public Long getLang() {
		/*if (!getCurrentLang().equals(this.oldLang)) {
			setLang(getCurrentLang());
			changeLang();
		}*/
		return lang;
	}
	public void setLang(Long lang) {
		this.lang = lang;
	}
	public Long getOldLang() {
		return oldLang;
	}

	public void setOldLang(Long oldLang) {
		this.oldLang = oldLang;
	}

	public PublicationLang getPublSelLang() {
		return publSelLang;
	}

	public void setPublSelLang(PublicationLang publSelLang) {
		this.publSelLang = publSelLang;
	}
	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}
	
	public void changeLang() {
		// Publication Langs
		if(null==this.publSelLang.getLang())
			this.publSelLang.setLang(getOldLang());
		this.publListHM.put(getOldLang(), this.publSelLang);
		this.publSelLang = new PublicationLang();
		this.publSelLang=this.publListHM.get(this.lang);
		if(null==this.publSelLang.getLang())
			this.publSelLang.setLang(this.lang);
		
		// Files lang
		/*if(null==this.fileSelLang.getLang())
			this.fileSelLang.setLang(getOldLang());
		this.filesListHM.put(getOldLang(), this.fileSelLang);
		this.fileSelLang = new Files();
		this.fileSelLang=this.filesListHM.get(this.lang);
		if(null==this.fileSelLang.getLang())
			this.fileSelLang.setLang(this.lang);*/

		this.oldLang=this.lang;
		refreshImagePub();
	}

	public HashMap<Long, String> getLangHM() {
		return langHM;
	}

	public void setLangHM(HashMap<Long, String> langHM) {
		this.langHM = langHM;
	}
	
	

	public List<PublicationLang> getPubLdelLang() {
		return pubLdelLang;
	}

	public void setPubLdelLang(List<PublicationLang> pubLdelLang) {
		this.pubLdelLang = pubLdelLang;
	}
	
	public void loadLangHM() {
		// Publication langs		
		if (null!=this.publLangList && this.publLangList.size() > 0) {
			for (int i = 0; i < this.publLangList.size(); i++) {
				if (null!=this.publLangList.get(i).getLang()) 
					this.publListHM.put(this.publLangList.get(i).getLang(), this.publLangList.get(i));
			}
		}
		// Дали е тип видео и се използав или не YouTube
		this.publSelLang=this.publListHM.get(this.lang);
		if (this.publication.getTypePub().equals(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_VIDEO)) && 
			(null==this.publSelLang.getUrlPub() || this.publSelLang.getUrlPub().equals(""))) {
			this.codeYT= false;
		}else {
			this.codeYT= true;
		}
		
		// Files langs
//		List<Files> tempF=new ArrayList<Files>();
		if (null!=this.filesList && this.filesList.size() > 0) {
			for (int i = 0; i < this.filesList.size(); i++) {
				if (null!=this.filesList.get(i).getLang())
					this.filesListHM.get(this.filesList.get(i).getLang()).add(this.filesList.get(i));
			}
		}
		
		
//		this.fileSelLang=this.filesListHM.get(this.lang);
		
	
	}
	
	public void refreshImagePub() {
		if(null!=this.publication.getImagePub()){
			InputStream imageStream = new ByteArrayInputStream(this.publication.getImagePub());
			//this.imageCont = new DefaultStreamedContent(imageStream, "image/jpeg");
			this.imageCont = DefaultStreamedContent.builder().contentType("image/jpeg").name("").stream(() -> imageStream).build();
		}
	}

	public HashMap<Long, List<Files>> getFilesListHM() {
		return filesListHM;
	}

	public void setFilesListHM(HashMap<Long, List<Files>> filesListHM) {
		this.filesListHM = filesListHM;
	}

	/*public List<Files> getFileSelLang() {
		return fileSelLang;
	}

	public void setFileSelLang(List<Files> fileSelLang) {
		this.fileSelLang = fileSelLang;
	}*/
	
	
	public void changeDescription(CellEditEvent event){
        Object newValue = event.getNewValue();
	}
	
	public boolean checkUploadedFileForDuplicate(UploadedFile attachFile) {
		
		if(null==attachFile.getFileName() || null==attachFile.getContentType()) 
			return false;
		
		for(Files f:this.filesList){
			if(f.getFilename().toUpperCase().toString().equals(attachFile.getFileName().toUpperCase().toString()) && 
				f.getContentType().toUpperCase().toString().equals(attachFile.getContentType().toUpperCase().toString())) {
				return true;
			}
		}
		return false;
	}

	/*public String getPublTypeText() {
		return publTypeText;
	}

	public void setPublTypeText(String publTypeText) {
		this.publTypeText = publTypeText;
	}

	public Long getCodeType() {
		return codeType;
	}

	public void setCodeType(Long codeType) {
		this.codeType = codeType;
		this.publication.setTypePub(codeType);
	}*/

	public Boolean getCodeYT() {
		return codeYT;
	}

	public void setCodeYT(Boolean codeYT) {
		this.codeYT = codeYT;
	}

	public HashMap<Long, String> getTitleAttach() {
		return titleAttach;
	}

	public void setTitleAttach(HashMap<Long, String> titleAttach) {
		this.titleAttach = titleAttach;
	}
	
	public void fillAttachTitleHM() {

		this.titleAttach= new HashMap<Long, String>();
		
		List<SystemClassif> publTypeList= new ArrayList<SystemClassif>();
		try {
			publTypeList=getSystemData().getSysClassification(Constants.CODE_SYSCLASS_PUBL_TYPE, new Date(), getCurrentLang(), this.idUser);
		} catch (DbErrorException e) {
			LOGGER.error("DbErrorException: " + e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"),e.getMessage());
		}
		for (SystemClassif item: publTypeList){
			if(null!=item) {
				int l = (int)item.getCode();
				switch (l) {
			         case (int)Constants.CODE_ZNACHENIE_TYPE_PUBL_MATERIALI: 
			        	 this.titleAttach.put(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_MATERIALI), getMessageResourceString("labels", "publ.attachedMater"));
			         	 this.getTypeFilesAttach().put(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_MATERIALI), getSystemData().getSettingsValue("fileExtensionsForAttaching"));
			        	 break;
			         case (int)Constants.CODE_ZNACHENIE_TYPE_PUBL_IMAGES: 
			        	 this.titleAttach.put(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_IMAGES), getMessageResourceString("labels", "publ.attachedImages"));
			         	 this.getTypeFilesAttach().put(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_IMAGES), "/(\\.|\\/)(gif|jpe?g|png|bmp)$/");
			         	 this.getTypeFilesMessages().put(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_IMAGES), getMessageResourceString("labels", "publ.attachedImagesMess"));
			         	 break;
			         case (int)Constants.CODE_ZNACHENIE_TYPE_PUBL_VIDEO: 
			        	 this.titleAttach.put(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_VIDEO), getMessageResourceString("labels", "publ.attachedVideo"));
			         	 this.getTypeFilesAttach().put(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_VIDEO), "/(\\.|\\/)(mp4)$/");
			         	 this.getTypeFilesMessages().put(Long.valueOf(Constants.CODE_ZNACHENIE_TYPE_PUBL_VIDEO), getMessageResourceString("labels", "publ.attachedVideoMess"));
			         	 break;	 
			         default: 
			         	break;
				}
				
			}
			
		}

	}

	public HashMap<Long, String> getTypeFilesAttach() {
		return typeFilesAttach;
	}

	public void setTypeFilesAttach(HashMap<Long, String> typeFilesAttach) {
		this.typeFilesAttach = typeFilesAttach;
	}
	
	public void prepareYouTube() {
		if (! this.codeYT) {
			this.publSelLang.setUrlPub(null);
		}

	}

	public HashMap<Long, String> getTypeFilesMessages() {
		return typeFilesMessages;
	}

	public void setTypeFilesMessages(HashMap<Long, String> typeFilesMessages) {
		this.typeFilesMessages = typeFilesMessages;
	}

}
