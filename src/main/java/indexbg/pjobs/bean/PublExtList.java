package indexbg.pjobs.bean;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
//import java.util.TimeZone;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
/*import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;*/
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.primefaces.component.datagrid.DataGrid;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.Publication;
import indexbg.pjobs.db.dao.PublicationDAO;
import indexbg.pjobs.db.dao.PublicationLangDAO;
import indexbg.pjobs.system.Constants;

import indexbg.pjobs.system.PJobsBean;

@Named("publExtList")
@ViewScoped
public class PublExtList extends PJobsBean{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6682991683868742770L;
	/**
	 * Основен javaBean клас за търсене на публикации по зададени критерии за търсене и 
	 * за обръщане към javaBean клас за въвеждане/актуализация
	 */
	
	
	static final Logger LOGGER = LoggerFactory.getLogger(PublExtList.class);
	private Long idUser=null;
	private String sectionText=null;
	private Long codeSection=null;
	private Date dateFrom=null;
	private Date dateTo=null;
	private String annotation=null;
	private Long period=null;
	private LazyDataModelSQL2Array pubListT = null;
	private Publication selectedPubl=null;
//	private Long param=null;
	private String titleF=null;
	private List<Object[]> pubLangList = new ArrayList<Object[]>();
	private SelectMetadata smd = null;
	private String sortCol = "";
	private boolean hasSearched = false;
	private  List<SystemClassif> periodList;
	
	private int gridcolumn =1;
	/**
	 * Инициира/сетва първоначалните стойности на атрибутите на филтъра за търсене. Чете предадените параметри от други екрани
	 */
	@PostConstruct
	public void initData(){
		try{	
			String sect =JSFUtils.getRequestParameter("sect");
			if (sect != null && !sect.trim().isEmpty()){
				this.setCodeSection(Long.valueOf(sect));
			}else {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.invalidParameter"));
			}

			this.idUser=Constants.PORTAL_USER;
			
			@SuppressWarnings("unchecked")
			Map<String, Object> params  = (Map<String, Object>) getSessionScopeValue("publExtListFindAttr");	
			if(params != null){
				if (null!=params.get("dateFrom")) 
					this.dateFrom=(Date)params.get("dateFrom");

				if (null!=params.get("dateTo"))
					this.dateTo=(Date)params.get("dateTo");
				
				if (null!=params.get("titleF")) 
					this.titleF=(String)params.get("titleF");
			
				
				if (null!=getSessionScopeValue("period"))
					this.period =  (Long) getSessionScopeValue("period");
		
				actionFind();
				
				if(getSessionScopeValue("publExtListPage") != null) {
					
					DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("formExtList:tableGrid");
					
					if(d != null) { 					
						int page = (int) getSessionScopeValue("publExtListPage");
						d.setFirst(page); 
					}
				}
	
			}else {
				actionFind();
			}
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		} finally {
			JPA.getUtil().closeConnection();
		}	
		
		
		
	//	actionClear();
		
	//	actionFind();
	}
	
	
	/**Изтрива стойностите на филтъра за търсене на публикациите
	 * 
	 */
	public void actionClear(){
		this.sectionText=null;
		this.dateFrom=null;
		this.dateTo=null;
		this.period=null;
		this.setSelectedPubl(new Publication());
		this.pubListT = null;
		this.titleF=null;
		
		//махаме запазените параметри от сесията
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.removeAttribute("publExtListPage");
		session.removeAttribute("publExtListFindAttr");
		
		session.removeAttribute("period");
				
		DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formPublList:tablePubl");		
		d.setFirst(0); 
	}
	
	

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
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
	}

	

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	
	/**Проверява дата от и дата до на публикациите
	 * @param nom
	 */
	public boolean checkDates(int nom){
		boolean rez=true;
		this.setPeriod(null);
		if (this.getDateFrom() != null && this.getDateTo() != null) {
			if(this.getDateFrom().compareTo(this.getDateTo()) > 0) {
				rez=false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","publ.dateFromLessDateTo"));
			}
		} 
		return rez;
	}
	
	

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public Date getCurrentDate() {
		return new Date();
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}
	
	public void changePeriod () {
    	if (this.period != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.period);
			this.setDateFrom(di[0]);
			this.setDateTo(di[1]);
		} else {
			this.setDateFrom(null);
			this.setDateTo(null);
		}
		return ;
    }
	
	
	
	
	/** Метод за търсене в БД на публикации по зададените критерии/филтър 
	 * 
	 */
	public void actionFind(){
		
		if(titleF!=null && !titleF.trim().isEmpty() && titleF.length()<3) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.searchLenght"));
			return;
		}
		
		this.pubListT = null;

		this.smd = new SelectMetadata();
		
		PublicationDAO publDao = new PublicationDAO(this.idUser);
		
		try {
			this.smd = publDao.findPublExtFilter(this.dateFrom, this.dateTo, this.codeSection, this.titleF, getCurrentLang(), new Date());
			this.sortCol = "A4";
			this.pubListT = publDao.newLazyDataModel(this.smd, this.sortCol);
			this.pubListT.getRowCount();
			
			Map<String, Object> params = new HashMap<String, Object>();
			/*if (null != this.getCodeSection())
				params.put("codeSection", this.codeSection);	*/
			
			if (null != this.getDateFrom())
				params.put("dateFrom", this.dateFrom);	
			
			if (null != this.getDateTo())
				params.put("dateTo", this.dateTo);	
			
			if (null != this.getTitleF())
				params.put("titleF", this.titleF);	


			addSessionScopeAttribute("publExtListFindAttr", params);
			
			if (null!=this.getPeriod())
				addSessionScopeAttribute("period", this.period);
			
			
		
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
		
		
		/*if(this.pubListT.getRowCount()>1)
			this.hasSearched = false;*/
		
	}

	public LazyDataModelSQL2Array getPubListT() {
		return pubListT;
	}

	public void setPubListT(LazyDataModelSQL2Array pubListT) {
		this.pubListT = pubListT;
	}

	public void prepareGo(){
		this.pubListT.getResult().clear();
	}
	
	
	
	/** Метод за скалиране/редуциране на размера на изображенията, 
	 * показвани в таблицата на намерените публикации по зададен филтър на търсене в БД 
	 * @param ba
	 * @param proc
	 * @return
	 */
	public StreamedContent createImageCont(byte[] ba, int proc){
		StreamedContent imagCont=null;

		if (null!=ba){
				
	        try {
	        	
				ByteArrayInputStream inS = new ByteArrayInputStream(ba);
	        	BufferedImage img = ImageIO.read(inS);
	        	int h=img.getHeight();
	        	int w=img.getWidth();
	        	h=(img.getHeight()*proc)/100;
	        	w=(img.getWidth()*proc)/100;
	            
	            Image scaledImage = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
	            BufferedImage imageBuff = new BufferedImage(w, h, img.getType());
	            imageBuff.getGraphics().drawImage(scaledImage, 0, 0, null);

	            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	            ImageIO.write(imageBuff, "jpeg", buffer);
	            buffer.flush();
	            imageBuff.flush();
	            byte[] resizeBa=buffer.toByteArray();
	            buffer.close();
	        	
	            InputStream imageStream = new ByteArrayInputStream(resizeBa);
	          //  imagCont = new DefaultStreamedContent(imageStream, "image/jpeg");
	            imagCont = DefaultStreamedContent.builder().contentType("image/jpeg").name("").stream(() -> imageStream).build();
	            imageStream.close();
	            
			} catch (Exception e) {
				LOGGER.error(e.getMessage(),e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при показване на изображение!", e.getMessage());
			}
		}
		return imagCont;
	}

	public Publication getSelectedPubl() {
		return selectedPubl;
	}



	public void setSelectedPubl(Publication selectedPubl) {
		this.selectedPubl = selectedPubl;
	}
	

	public String getTitleF() {
		return titleF;
	}



	public void setTitleF(String titleF) {
		this.titleF = titleF;
	}
	
	//???
	public void findPublBySection(Long codeSect){

		try {
			
			this.pubLangList = new PublicationLangDAO(this.idUser).findPublLangBySect(codeSect, new Date(), getCurrentLang());
			
			} catch (DbErrorException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
				
			} finally {
				JPA.getUtil().closeConnection();
			}
	}



	public List<Object[]> getPubLangList() {
		return pubLangList;
	}



	public void setPubLangList(List<Object[]> pubLangList) {
		this.pubLangList = pubLangList;
	}
	
	public void sortReport() {
		if(null!=getSortCol()) {
			PublicationDAO publDao = new PublicationDAO(this.idUser);
			// Тука пресортиране на изхода	
			try {
				
				this.pubListT = publDao.newLazyDataModel(getSmd(), getSortCol());
				this.pubListT.getRowCount();
			
			} catch (DbErrorException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
			} finally {
				JPA.getUtil().closeConnection();
			}
			
		}
	}



	public SelectMetadata getSmd() {
		return smd;
	}



	public void setSmd(SelectMetadata smd) {
		this.smd = smd;
	}



	public String getSortCol() {
		return sortCol;
	}



	public void setSortCol(String sortCol) {
		this.sortCol = sortCol;
	}



	public int getGridcolumn() {
		return gridcolumn;
	}



	public void setGridcolumn(int gridcolumn) {
		this.gridcolumn = gridcolumn;
	}
	
	public void actionGridTable() {
		gridcolumn=2;
	}
	
	public void actionListTable() {
		gridcolumn=1;
	}



	public boolean isHasSearched() {
		return hasSearched;
	}

	public void setHasSearched(boolean hasSearched) {
		this.hasSearched = hasSearched;
	}
	
	
	public void changePage() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);		
		
		session.removeAttribute("publExtListPage");
		
		DataGrid d = (DataGrid) FacesContext.getCurrentInstance().getViewRoot().findComponent("formExtList:tableGrid");
		
		if(d != null) { 
			int a=d.getFirst();
			addSessionScopeAttribute("publExtListPage", d.getFirst());		
		}

	}


	public List<SystemClassif> getPeriodList() {
		if(periodList==null) {
			
			try {
				periodList = getSystemData().getSysClassification(Constants.CODE_CLASSIF_PERIOD_NOFUTURE, getCurrentDate(), getCurrentLang(), idUser);
				SysClassifUtils.doSortClassifPrev(periodList);
			} catch (DbErrorException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
			} catch (UnexpectedResultException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.unexpectedResult"), e.getMessage());
			}catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
			} finally {
				JPA.getUtil().closeConnection();
			}	
		}
		return periodList;
	}


	public void setPeriodList(List<SystemClassif> periodList) {
		this.periodList = periodList;
	}

}
