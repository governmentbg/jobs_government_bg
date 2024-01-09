package indexbg.pjobs.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.Publication;
import indexbg.pjobs.db.PublicationLang;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.PublicationDAO;
import indexbg.pjobs.db.dao.PublicationLangDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named("publExtData")
@ViewScoped
public class PublExtBean extends PJobsBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1984244391412755105L;


	/**
	 * Основен javaBean клас за въвеждане/актуализация на публикациите
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(PublExtBean.class);
	
	
	private Publication publication = new Publication();
	private Long idPubl = null;
	private String sectionText="";
	private Long codeSection=null;
	private Long idUser=null;
	private StreamedContent imageCont=null;
	private Long param=null;
	private Long oldLang=null;
	private PublicationLang publSelLang = new PublicationLang();
	private List<PublicationLang> publLangList = new ArrayList<PublicationLang>();
//	private HashMap<Long, PublicationLang> publListHM = new HashMap<Long, PublicationLang>();
	
	//Files
	
	private List<Files> filesList = new ArrayList<Files>();
//	private HashMap<Long, List<Files>> filesListHM = new HashMap<Long, List<Files>>();
	
	private String titleText="";
	
	private List<Object[]> images;
	
	
	/**Инициира/сетва първоначалните стойности на атрибутите на обектите. Чете предадените параметри от други екрани 
	 * 
	 */
	@PostConstruct
	public void initData(){
		LOGGER.debug("PostConstruct!");
		
		this.idUser = Constants.PORTAL_USER;
		
		
		
		if (JSFUtils.getRequestParameter("idPubl")!=null && ValidationUtils.isNumber(JSFUtils.getRequestParameter("idPubl"))) {
		
			actionClear();
			String par =JSFUtils.getRequestParameter("idPubl");
			if (par != null && !par.trim().isEmpty()){
				this.setIdPubl(Long.valueOf(par));
				loadPublById(this.idPubl);
			}
		
		} else {
			try {
				redirectExternal("pageNotFound.jsf");
			} catch (IOException e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));	
			}				
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
		this.codeSection=null;
		
		this.imageCont=null;
		this.filesList = new ArrayList<>();
	
		this.oldLang = getCurrentLang();
//		clearListHM();
	}

	public Publication getPublication() {
		return publication;
	}

	public void setPublication(Publication publication) {
		this.publication = publication;
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


	public List<Files> getFilesList() {
		return filesList;
	}
	
	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
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
			
			if(this.publication != null) {

				this.codeSection = this.publication.getSection();
				refreshImagePub();
	    		
				// Езиковите версии
				this.publLangList = (List<PublicationLang>) new PublicationLangDAO(this.idUser).findByIdPublSingleLang(this.idPubl, getCurrentLang());
				
				
				if(publication.getTypePub()==null || publication.getTypePub().longValue()==1) {
					this.filesList = (List<Files>)new FilesDAO(this.idUser).findByCodeObjAndIdObjAndLang(Constants.CODE_OBJECT_PUBLICATION, this.idPubl, getCurrentLang());
				} else {
					this.filesList = (List<Files>)new FilesDAO(this.idUser).loadAllData(Constants.CODE_OBJECT_PUBLICATION, this.idPubl, getCurrentLang());	
					
					if(publication.getTypePub().longValue()==2) {
						images = new ArrayList<Object[]>();
						if(publication.getImagePub()!=null) {
							InputStream imageStream1 = new ByteArrayInputStream(this.publication.getImagePub());
							images.add(new Object[]{ DefaultStreamedContent.builder().contentType("image/jpeg").name("").stream(() -> imageStream1).build()," "});	
						}
						for(Files f :filesList) {
							images.add(new Object[]{ 
									DefaultStreamedContent.builder().contentType(f.getContentType()).name("").stream(() -> new ByteArrayInputStream(f.getContent())).build()
									,f.getDescription() });	
						}
					}
				}				
				
				if (null!=this.publLangList && this.publLangList.size()>0)
					this.publSelLang = this.publLangList.get(0);
				
//				loadLangHM();
			
			} else {
				redirectExternal("pageNotFound.jsf");	
			}
			
			} catch (DbErrorException e) {
			 	LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
				
			} catch (IOException e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));	
						
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

	public Long getParam() {
		return param;
	}

	public void setParam(Long param) {
		this.param = param;
	}



	/*public void clearListHM() {
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
	
	}*/
	

	
	public List<PublicationLang> getPublLangList() {
		return publLangList;
	}

	public void setPublLangList(List<PublicationLang> publLangList) {
		this.publLangList = publLangList;
	}

	/*public HashMap<Long, PublicationLang> getPublListHM() {
		return publListHM;
	}

	public void setPublListHM(HashMap<Long, PublicationLang> publListHM) {
		
		this.publListHM = publListHM;
	}*/

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
	
	/*public HashMap<Long, String> getLangHM() {
		return langHM;
	}

	public void setLangHM(HashMap<Long, String> langHM) {
		this.langHM = langHM;
	}*/

	/*public List<PublicationLang> getPubLdelLang() {
		return pubLdelLang;
	}

	public void setPubLdelLang(List<PublicationLang> pubLdelLang) {
		this.pubLdelLang = pubLdelLang;
	}*/
	
	/*public void loadLangHM() {
		// Publication langs		
		if (null!=this.publLangList && this.publLangList.size() > 0) {
			for (int i = 0; i < this.publLangList.size(); i++) {
				if (null!=this.publLangList.get(i).getLang()) 
					this.publListHM.put(this.publLangList.get(i).getLang(), this.publLangList.get(i));
			}
		}
		
		// Проверява за липсващи езикови версии 
		List<Long> lanYes=new ArrayList<>(); 
		List<Long> lanNo=new ArrayList<>(); 
		Set<Long> hmSet=this.publListHM.keySet(); 
		for (Long item: hmSet) {
			if (null!=this.publListHM.get(item).getLang()){
				lanYes.add(item);
			}else {
				lanNo.add(item);
			}
		}
		
		if (null!=lanNo && lanNo.size()>0) {
			PublicationLang pBG=new PublicationLang();
			if(!lanYes.isEmpty() && lanYes.contains(Long.valueOf(Constants.CODE_ZNACHENIE_LANG_BG))) {
				pBG =this.publListHM.get(Long.valueOf(Constants.CODE_ZNACHENIE_LANG_BG));
				for (Long item: lanNo) {
					this.publListHM.get(item).setAnnotation(pBG.getAnnotation());
					this.publListHM.get(item).setFullText(pBG.getFullText());
					this.publListHM.get(item).setLang(item);
					this.publListHM.get(item).setOtherInfo(pBG.getOtherInfo());
					this.publListHM.get(item).setTitle(pBG.getTitle());
					this.publListHM.get(item).setUrlPub(pBG.getUrlPub());
				}
			}
		}
		
		// Files langs
		if (null!=this.filesList && this.filesList.size() > 0) {
			for (int i = 0; i < this.filesList.size(); i++) {
				if (null!=this.filesList.get(i).getLang())
					this.filesListHM.get(this.filesList.get(i).getLang()).add(this.filesList.get(i));
			}
		}

	}*/
	
	public void refreshImagePub() {
		if(null!=this.publication.getImagePub()){
			InputStream imageStream = new ByteArrayInputStream(this.publication.getImagePub());
			//this.imageCont = new DefaultStreamedContent(imageStream, "image/jpeg");
			this.imageCont = DefaultStreamedContent.builder().contentType("image/jpeg").name("").stream(() -> imageStream).build();
		        
		}
	}

	public List<Object[]> getImages() {
		return images;
	}

	public void setImages(List<Object[]> images) {
		this.images = images;
	}

	/*public HashMap<Long, List<Files>> getFilesListHM() {
		return filesListHM;
	}

	public void setFilesListHM(HashMap<Long, List<Files>> filesListHM) {
		this.filesListHM = filesListHM;
	}*/

	public String getParamsVideo() throws UnsupportedEncodingException {
		if(publication!=null) {
			String pr =publication.getId()+";"+getCurrentLang();
			return new String(Base64.getEncoder().encodeToString(pr.getBytes()));
		}
		return "";
	}

}
