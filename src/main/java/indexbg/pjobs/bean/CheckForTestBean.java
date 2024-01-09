package indexbg.pjobs.bean;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.radial.RadialScales;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearPointLabels;
import org.primefaces.model.charts.optionconfig.elements.Elements;
import org.primefaces.model.charts.optionconfig.elements.ElementsLine;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.radar.RadarChartDataSet;
import org.primefaces.model.charts.radar.RadarChartModel;
import org.primefaces.model.charts.radar.RadarChartOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.words.Cell;
import com.aspose.words.CellMerge;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.NodeType;
import com.aspose.words.Row;
import com.aspose.words.SaveFormat;
import com.aspose.words.Table;
import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.UserAdd;
import indexbg.pjobs.db.UsersTests;
import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.db.dao.AdmUsersTestsDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.MSWordBookmarks;
import indexbg.pjobs.system.PJobsBean;


@Named ("checkForTestBean")
@ViewScoped
public class CheckForTestBean extends PJobsBean{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7992405425142604785L;
	static final Logger LOGGER = LoggerFactory.getLogger(CheckForTestBean.class);
    
	private Long pin;
	private String egn;
	private Long nivo;
	private AdmUserDAO userDao;
	private Long idUser;
	private Long userAdministration;
	private UserAdd user;
	private boolean visibleLevel= false;
	private boolean dyslexia = false;
	private AdmUsersTestsDAO testDao;
	private List<UsersTests> tests;
	//private boolean visibleNew = false;
	private UsersTests newTest = new UsersTests();
	private String admText="";
	private List<Long> halls = new ArrayList<Long>();
	
	private List <Object[]> testResuts = new ArrayList<Object[]>();
	
	private boolean checkNeizdarjal = false; //ако не е издържал 2 пъти,да се изведе съобщение // сега за деактивиран го искат
	private boolean checkDeaktiv = false; //
	private boolean checkAllowToTest= false; // ако е одобрен за дадено ниво,да не може да се добавя нов
	
	// Прикачени файлове
	private FilesDAO filesDAO;
	private List<Files> filesList = new ArrayList<>();
	
	
	private HashMap <Long , RadarChartModel> mapRadarModel = new  HashMap<>();
	
	
	/** String Base64 that represents the image bytes */
	private String chartImageSrcBase64; 
	
	
	public CheckForTestBean() {
		
	}
	
	@PostConstruct
	public void init() {
		try {
			idUser = getUserData().getUserId();
			userAdministration =  getUserData().getCodeOrg();
			
			userDao = new AdmUserDAO(idUser);
			testDao = new AdmUsersTestsDAO(idUser);
			filesDAO = new FilesDAO(idUser);
			user = new UserAdd();
			setTests(new  ArrayList<UsersTests>());
			halls = testDao.getHalls();
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}finally {
			JPA.getUtil().closeConnection();
	}	
		
	}
	
	public void actionNew() {
		nivo = null;
		user = new UserAdd();
		setTests(new  ArrayList<UsersTests>());
		visibleLevel = false;
		//visibleNew = false;
		dyslexia= false;
		newTest = new UsersTests();
		admText="";
		tests = new  ArrayList<UsersTests>();
		
		
		//място по подразбиране PJOBS.DEFAUL.TEST.PLACE
		String defLoc = getSystemData().getSettingsValue("PJOBS.DEFAUL.TEST.PLACE");
		if(defLoc!=null && !defLoc.isEmpty()) {
			newTest.setTestLocation(Long.valueOf(defLoc));
		}
	}
	
	public void actionClear() {
		pin = null;
		egn="";
		actionNew();
	}
	
	public void changePin() {
		egn="";
		actionNew();
	}
	

    public void actionSearchEgnByPin() {
    	if(pin!=null||(egn!=null && !"".equals(egn))) {
    		actionNew();
	    	try {
	    	    if(pin!=null) {
					user = userDao.getUserAddByPin(pin);
					
					if(user!=null) {
						if( user.getEgn()!=null)
						{
							egn = user.getEgn();
							visibleLevel = true;
						}else {
							JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","userTest.notFoundEgnByPin"));
						}
						if(user.getDyslexia()!=null) {
							dyslexia=user.getDyslexia();
						}
						
						
					}else {
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","userTest.notFoundPin"));
						pin=null;
						visibleLevel = false;
					}
	    	    }else {
	    	    	if(ValidationUtils.isValidEGN(egn)) {
		    	        user = userDao.getUserAddByEgn(egn);
						
						if(user!=null) {
							if( user.getPin()!=null)
							{
								pin = user.getPin();
								visibleLevel = true;
							}
							if(user.getDyslexia()!=null) {
								dyslexia=user.getDyslexia();
							}
							
							
							}else {
								JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","userTest.notFoudEgn"));
								pin=null;
								visibleLevel = false;
							}
		    	    	
	    	    	}else {
	    	    		JSFUtils.addMessage("testForm:egn",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.errEgn"));	
	    	    	}
	    	    }
			}  catch (DbErrorException e) {
				LOGGER.error("Грешка при търсене на задължени субекти! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			}finally {
				JPA.getUtil().closeConnection();
			}
    	}else {
    		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.insertParameters"));	    		
    	}
    }
    
    public void actionSaveEgn() {
    	if(checkData()){
	    	try {
				Object[] activeUser = userDao.getPinByEgn(egn,user.getId());
				
				if(activeUser!=null&& activeUser[2]!=null) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","userTest.errDubEgn") +activeUser[2]);	
				}else {
					JPA.getUtil().begin();
					userDao.updateUsserAdd(user.getId(), egn, dyslexia);					
					JPA.getUtil().commit();					
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));	
					visibleLevel=true; 
					}
				
			} catch (DbErrorException e) {
					JPA.getUtil().rollback();
					LOGGER.error("Грешка при запис на потребител! ", e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
				
			} catch (Exception e) {
					JPA.getUtil().rollback();
					LOGGER.error("Грешка при работа със системата!!!", e);	
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
				
			}finally {
					JPA.getUtil().closeConnection();
			}
    	}
    }   

    public void actionSelectLevel() {
    	checkDeaktiv= false;
    	checkAllowToTest = false;
    	if(nivo!=null && user!=null && user.getUser().getId()!=null) {
	    	try {
				tests = testDao.getUserTestsByLevel(nivo, user.getUser().getId());	
				
				if(tests!=null) {
					//if(tests.size()<2) { промените са направени,защото ако е издържал,резултата му важи 3 години
					//	visibleNew = true;
						int countDeaktiv = 0;
					//	int countNeizdarjal = 0;
						for(UsersTests item:tests) {
//							if(item.getStatus()!=null &&( item.getStatus().longValue()== Constants.CODE_ZNACH_TEST_RESULTS_IZDARJAL || item.getStatus().longValue()== Constants.CODE_ZNACHENIE_SHABLON_ALLOW_TO_TEST)) {
//								visibleNew = false;
//							}
//							if(item.getStatus()!=null && item.getStatus().longValue()== Constants.CODE_ZNACH_TEST_RESULTS_NEIZDARJAL){
//								countNeizdarjal++;
//							}	
							
							if(item.getStatus()!=null && item.getStatus().longValue()== Constants.CODE_ZNACHENIE_SHABLON_ALLOW_TO_TEST) {
								checkAllowToTest = true;
							}
			
							if(item.getStatus()!=null && item.getStatus().longValue()== Constants.CODE_ZNACH_TEST_RESULTS_DEAKTIVIRAN){
								countDeaktiv++;
							}							
						}
						
//						if(countNeizdarjal>1) {
//							visibleNew = false;
//						}
						if(countDeaktiv>0) {
							checkDeaktiv=true;
						}else {
							checkDeaktiv=false;
						}
				//	} else {
//						for(UsersTests item:tests) {
//							if(item.getStatus()!=null && item.getStatus().longValue()== Constants.CODE_ZNACH_TEST_RESULTS_DEAKTIVIRAN) {
//								visibleNew = true;
//								break;
//							}
//						}
//					}	
						PrimeFaces.current().executeScript("$('.add-test').slideUp();");
				}
//				else {
//					visibleNew = true;
//				
//				}
			}  catch (DbErrorException e) {
				LOGGER.error("Грешка при търсене на тестове за потребител! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			}finally {
				JPA.getUtil().closeConnection();
			}
    	}
    	
    }
    
    private boolean checkData() {
    	boolean valid = true;
    	
    	if(pin==null){
			JSFUtils.addMessage("testForm:pin",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.pin")));	
	
			valid=false;
		}
    	
    	if(egn==null || "".equals(egn)){
			JSFUtils.addMessage("testForm:egn",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.egn")));	
	
			valid=false;
		}
		
          if(egn!=null && !"".equals(egn) && !ValidationUtils.isValidEGN(egn)) {
			
			JSFUtils.addMessage("testForm:egn",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.errEgn"));						
			valid=false;
          }
    	return valid;
    }
    
    private boolean checkTestData() {
    	boolean valid = true;
    	
    	if(nivo==null){
			JSFUtils.addMessage("testForm:nivo",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "profile.langLevel")));	
	
			valid=false;
		}
    	
    	if(newTest.getTestLocation()==null){
			JSFUtils.addMessage("testForm:testLocation",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "checkForTest.location")));	
	
			valid=false;
		}
		
    	try {
			if(getUserData().getCodeOrg()==null){
				JSFUtils.addMessage(null,FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","checkForTestBean.noOrg"));	

				valid=false;
			}
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}
    	
//    	if(newTest.getTestDate()==null){
//			JSFUtils.addMessage("testForm:dateTest",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "general.data")));	
//	
//			valid=false;
//		}
//    	
//    	if(newTest.getTestТime()==null){
//			JSFUtils.addMessage("testForm:timeTest",FacesMessage.SEVERITY_ERROR,getMessageResourceString("beanMessages","general.pleaseInsert",getMessageResourceString("labels", "checkForTest.time")));	
//	
//			valid=false;
//		}
    	
    	return valid;
    }
    
    public void actionSave() {
		if(!checkTestData()){
			return;
		}
		try {
		    newTest.setAdministration(getUserData().getCodeOrg()); 
		    newTest.setAdministrationName(getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, getUserData().getCodeOrg(), getCurrentLang(), new Date(), idUser));
		    newTest.setTestLevel(nivo);		    
		    newTest.setUserId(user.getUser().getId());
		    newTest.setStatus(Constants.CODE_ZNACH_CANDIDATE_STATUS_ODOBREN);
			JPA.getUtil().begin();
	        testDao.save(newTest);  
	        
	        if(user.getUser().getEmail()!=null &&!"".equals(user.getUser().getEmail())) {
		    	JPA.getUtil().flush();
		    	ArrayList<String> mailsTo = new ArrayList<>();
		    	mailsTo.add(user.getUser().getEmail());	    		
		    			    
				new MailDAO(idUser).saveMail(Constants.CODE_ZNACHENIE_SHABLON_ALLOW_TO_TEST, mailsTo, getUserData().getCodeOrg(), null, null,
						getSystemData().decodeItem(Constants.CODE_SYSCLASS_EKATTE, this.newTest.getTestLocation(), getCurrentLang(), new Date(), this.idUser), user.getName()+ " " + user.getFamily(), 
						user.getUser().getId(), null, idUser, Constants.CODE_OBJECT_USERS_TESTS, getSystemData(), null, getSystemData().decodeItem(Constants.CODE_SYSCLASS_NIVO_TEST, this.nivo, 
								getCurrentLang(), new Date(), this.idUser), null, null, null, null);											
			}
			
	        JPA.getUtil().commit();
					
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
        //    visibleNew=false;
          //  tests.add(newTest);
         
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при запис на тест за кандидат! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		}finally {
			JPA.getUtil().closeConnection();
		}
		  actionSelectLevel();
	}
    
    public void actionDelete(){
    	String idObj =JSFUtils.getRequestParameter("idObj");			
		if (idObj!=null) {
			try {
				JPA.getUtil().begin();
				testDao.deleteById(Long.valueOf(idObj));
				JPA.getUtil().commit();			
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesDeleteMsg"));	
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при изтриване на разпределен кандидат! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			}finally {
				JPA.getUtil().closeConnection();
			}
			actionSelectLevel();
		}		
	}
    
    public void actionCheckDeaktiv() {
    	if(checkAllowToTest) {
    		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","checkForTest.allowToTest"));	
    	}else {
    		PrimeFaces.current().executeScript("$('.add-test').slideToggle();"); 		
    	}
    	if(checkDeaktiv) {
    		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, getMessageResourceString("beanMessages","checkForTestBean.checkDeaktiv"));	
    	}
    	
    	
    }

	public Long getPin() {
		return pin;
	}

	public void setPin(Long pin) {
		this.pin = pin;
	}

	public String getEgn() {
		return egn;
	}

	public void setEgn(String egn) {
		this.egn = egn;
	}

	public Long getNivo() {
		return nivo;
	}

	public void setNivo(Long nivo) {
		this.nivo = nivo;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}
	
	public Date getToday(){
		return new Date();
	}

	public boolean isVisibleLevel() {
		return visibleLevel;
	}

	public void setVisibleLevel(boolean visibleLevel) {
		this.visibleLevel = visibleLevel;
	}

	public boolean isDyslexia() {
		return dyslexia;
	}

	public void setDyslexia(boolean dyslexia) {
		this.dyslexia = dyslexia;
	}

	public List<UsersTests> getTests() { 
		return tests;
	}

	public void setTests(List<UsersTests> tests) {
		this.tests = tests;
	}

	public UsersTests getNewTest() {
		return newTest;
	}

	public void setNewTest(UsersTests newTest) {
		this.newTest = newTest;
	}

	public String getAdmText() {
		return admText;
	}

	public void setAdmText(String admText) {
		this.admText = admText;
	}

	public List<Long> getHalls() {
		return halls;
	}

	public void setHalls(List<Long> halls) {
		this.halls = halls;
	}

	public UserAdd getUser() {
		return user;
	}

	public void setUser(UserAdd user) {
		this.user = user;
	}

	public Long getUserAdministration() {
		return userAdministration;
	}

	public void setUserAdministration(Long userAdministration) {
		this.userAdministration = userAdministration;
	}
	
	public void onRowToggle(ToggleEvent event) {
		UsersTests row  = (UsersTests) event.getData();
	   
	   if(event.getVisibility() == Visibility.VISIBLE) {
		   try {
			   Long idTestRez = SearchUtils.asLong(row.getId());
			   
			   testResuts = new AdmUsersTestsDAO(idUser).getDataResultTest(idTestRez);
			   this.filesList = this.filesDAO.findByCodeObjAndIdObj(Constants.CODE_OBJECT_TST_TEST_GROUP, row.getTestGroupId());
			   
			   if(!mapRadarModel.containsKey(idTestRez)) {
				   mapRadarModel.put(idTestRez, createRadarModel(testResuts));
			   }
		   } catch (DbErrorException e) {
				LOGGER.error("Грешка при зареждане на резултати! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				LOGGER.error("Грешка при зареждане на резултати!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			} finally {
				JPA.getUtil().closeConnection();
			}
	   } else {
		   
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
	
	public List <Object[]> getTestResuts() {
		return testResuts;
	}

	public void setTestResuts(List <Object[]> testResuts) {
		this.testResuts = testResuts;
	}

	public boolean isCheckNeizdarjal() {
		return checkNeizdarjal;
	}

	public void setCheckNeizdarjal(boolean checkNeizdarjal) {
		this.checkNeizdarjal = checkNeizdarjal;
	}

	public boolean isCheckDeaktiv() {
		return checkDeaktiv;
	}

	public void setCheckDeaktiv(boolean checkDeaktiv) {
		this.checkDeaktiv = checkDeaktiv;
	}
	
	/** Генерира справка за кандитата в пдф формат 
	 */
	public void generatePdfExport()  {
		
		
		try {
			SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
			
			//1. Взема шаблона 
			String loadShablon="/resources/docs/participantsResultsShablon.docx";		
			ServletContext context_ = (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
			InputStream  shabl = new FileInputStream(context_.getRealPath("")+loadShablon);
			
			// 2. Зарежда лиценза за работа с MS Word documents.
			new MSWordBookmarks().setLicense();
			
			if (shabl == null || shabl.available()==0){
				LOGGER.error("Липсва шаблон за този вид експорт!!!");
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Липсва шаблон за този вид експорт!");
				shabl.close();
				return;
			}
			
			
			
			// 3. Създава празен документ от шаблона 
			Document docShablon = new Document(shabl);
			DocumentBuilder builder =  new DocumentBuilder(docShablon);
			
			int tableIndex =0;
			Table table = (Table)docShablon.getChild(NodeType.TABLE, tableIndex, true);
			Row clonedRow;
			int i = 1;
			
			//System.out.println(table.getLastRow().getCells().getCount());
			//table.getLastRow().getRowFormat().setHeightRule(HeightRule.EXACTLY);
			
			try {
				
				String sumTxt = getMessageResourceString("labels","general.sum_result");
				String pointsTxt = getMessageResourceString("labels","testResults.points");
				
				for(UsersTests ut: tests) {
					
	
					List<Object[]> resuts = new AdmUsersTestsDAO(idUser).getDataResultTest(SearchUtils.asLong(ut.getId()));
					
					if (i == 1){
						
						if(ut.getTestLocation()!=null){
							builder.moveToCell(tableIndex, i, 0, 0);
							builder.write(getSystemData().decodeItem(Constants.CODE_SYSCLASS_EKATTE, ut.getTestLocation(),getCurrentLang(), new Date(), idUser));
						}
						
						if(ut.getTestDate()!=null){
							builder.moveToCell(tableIndex, i,1, 0);
							builder.write(sdfDate.format(ut.getTestDate()));
						} 
		
						if(ut.getTestТime()!=null){
							builder.moveToCell(tableIndex, i,2, 0);
							builder.write(sdfTime.format(ut.getTestТime()));
						} 
						
						if(ut.getAdministration()!=null){
							builder.moveToCell(tableIndex, i,3, 0);
							builder.write(getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, ut.getAdministration(),getCurrentLang(), new Date(), idUser));
						} 
						
						i++;
						//--------- rezultati -------------------------------------
						clonedRow = (Row)table.getLastRow().deepClone(true);
						
						for(Cell cell : clonedRow.getCells()){
						    cell.removeAllChildren();	
						}
						
						table.appendChild(clonedRow);	
						
						builder.moveToCell(tableIndex, i, 0, 0);
						builder.getCellFormat().setHorizontalMerge(CellMerge.FIRST);
						if(resuts== null || resuts.isEmpty()) {
							builder.write("\t Няма въведени резултати от изпита!");
						} else {
							StringBuilder data  = new StringBuilder();
							for(Object[] resut:resuts) {
								
								Long module =  SearchUtils.asLong(resut[0]);
								Long area  =  SearchUtils.asLong(resut[1]);
								
								data.append("\n \t");
								if(module.longValue() == 0) {
									data.append(sumTxt);
								} else if(module.longValue() == 1) {
									data.append(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, area,getCurrentLang(), new Date(), idUser));
								} else if(module.longValue() == 2 || module.longValue() == 3) {
									data.append(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, module,getCurrentLang(), new Date(), idUser));
								} 
								data.append("\n \t");
								data.append(pointsTxt+": "+resut[2]);
								
							}
							data.append("\n");
							builder.write(data.toString());
						}
						
						builder.moveToCell(tableIndex, i, 1, 0);
						builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
						
						builder.moveToCell(tableIndex, i, 2, 0);
						builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
						
						builder.moveToCell(tableIndex, i, 3, 0);
						builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
						
						//---------------------------------------------------------
				
					} else {
						clonedRow = (Row)table.getLastRow().deepClone(true);
						
						for(Cell cell : clonedRow.getCells()){
						    cell.removeAllChildren();	
						    cell.getCellFormat().setHorizontalMerge(CellMerge.NONE);
						}
						
					//	clonedRow.getRowFormat().setHeightRule(HeightRule.EXACTLY);
						
						table.appendChild(clonedRow);
						
						if(ut.getTestLocation()!=null){
							builder.moveToCell(tableIndex, i, 0, 0);
							builder.write(getSystemData().decodeItem(Constants.CODE_SYSCLASS_EKATTE, ut.getTestLocation(),getCurrentLang(), new Date(), idUser));
						}
						
						if(ut.getTestDate()!=null){
							builder.moveToCell(tableIndex, i,1, 0);
							builder.write(sdfDate.format(ut.getTestDate()));
						} 
		
						if(ut.getTestТime()!=null){
							builder.moveToCell(tableIndex, i,2, 0);
							builder.write(sdfTime.format(ut.getTestТime()));
						} 
						
						if(ut.getAdministration()!=null){
							builder.moveToCell(tableIndex, i,3, 0);
							builder.write(getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, ut.getAdministration(),getCurrentLang(), new Date(), idUser));
						} 
						
						
						i++;
						
						//--------- rezultati -------------------------------------
						clonedRow = (Row)table.getLastRow().deepClone(true);
						
						for(Cell cell : clonedRow.getCells()){
						    cell.removeAllChildren();	
						}
						//clonedRow.getRowFormat().setHeightRule(HeightRule.AUTO);
						table.appendChild(clonedRow);	
						
						builder.moveToCell(tableIndex, i, 0, 0);
						builder.getCellFormat().setHorizontalMerge(CellMerge.FIRST);
						if(resuts== null || resuts.isEmpty()) {
							builder.write("\t Няма въведени резултати от изпита!");
						} else {
							StringBuilder data  = new StringBuilder();
							
							for(Object[] resut:resuts) {
								
								Long module =  SearchUtils.asLong(resut[0]);
								Long area  =  SearchUtils.asLong(resut[1]);
								
								data.append("\n \t");
								if(module.longValue() == 0) {
									data.append(sumTxt);
								} else if(module.longValue() == 1) {
									data.append(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, area,getCurrentLang(), new Date(), idUser));
								} else if(module.longValue() == 2 || module.longValue() == 3) {
									data.append(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, module,getCurrentLang(), new Date(), idUser));
								} 
								data.append("\n \t");
								data.append(pointsTxt+": "+resut[2]);
								
								
								
							}
							data.append("\n");
							builder.write(data.toString());
						}
						
						builder.moveToCell(tableIndex, i, 1, 0);
						builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
						
						builder.moveToCell(tableIndex, i, 2, 0);
						builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
						
						builder.moveToCell(tableIndex, i, 3, 0);
						builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
						
						
						//---------------------------------------------------------
					}
					i++;
				
				}
			
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при зареждане на резултати! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} finally {
				JPA.getUtil().closeConnection();
			}
			
			HashMap<String,String> hashBook=new HashMap<String, String>();
			
			hashBook.put("uchastnik"," "+user.getName()+" "+user.getFamily());
			hashBook.put("pin",pin.toString());
			hashBook.put("egn",egn);
			hashBook.put("nivo",  getSystemData().decodeItem(Constants.CODE_SYSCLASS_NIVO_TEST, nivo,getCurrentLang(), new Date(), idUser) ); 
			
			
			new MSWordBookmarks().setBookmarksReturnDoc(docShablon, hashBook);
			
			
			
			
			try {
				
				FacesContext fContext = JSFUtils.getFacesContext();		
				
				
				String fileName = "participantsResults.pdf";
				
				HttpServletResponse response = (HttpServletResponse) fContext.getExternalContext().getResponse();
				ServletOutputStream out = response.getOutputStream();
		
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition","attachment;filename=" + fileName);
				
				
				ByteArrayOutputStream dstStream = new ByteArrayOutputStream();
				docShablon.save(dstStream, SaveFormat.PDF);
				out.write(dstStream.toByteArray());
				
				out.flush();
				out.close();
		                
	            fContext.responseComplete();   
	            
	        } catch (IOException e) { 
	
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("generatePdfExport() - " + e.getMessage());
				}
	        }  
			
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("ObjectNotFoundException!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при попълването на шаблона с експорт!");
		} catch (Exception e) {
			LOGGER.error("Грешка при попълването на шаблона с експорт!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при попълването на шаблона с експорт!!!");
		}	

	}
	
	
	public void generateTestPdfExport() {
		try {
			//System.out.println("-----> "+ chartImageSrcBase64);
			if(JSFUtils.getRequestParameter("idTest")== null || JSFUtils.getRequestParameter("idTest").isEmpty()) {
				
				LOGGER.error("Липсва идентификатор на тест за експорт!!!");
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Липсва идентификатор на тест за експорт!");
				return;
			}
			
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
			
			//1. Взема шаблона 
			String loadShablon="/resources/docs/participantResultShablon.docx";		
			ServletContext context_ = (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
			InputStream  shabl = new FileInputStream(context_.getRealPath("")+loadShablon);
			
			Long idTest = Long.valueOf(JSFUtils.getRequestParameter("idTest"));
			
			
			if (shabl == null || shabl.available()==0){
				LOGGER.error("Липсва шаблон за този вид експорт!!!");
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Липсва шаблон за този вид експорт!");
				shabl.close();
				return;
			}
			
			// 2. Зарежда лиценза за работа с MS Word documents.
			new MSWordBookmarks().setLicense();
			
			// 3. Създава празен документ от шаблона 
			Document docShablon = new Document(shabl);
			
			HashMap<String,String> hashBook=new HashMap<String, String>();
			
			hashBook.put("uchastnik"," "+user.getName()+" "+user.getFamily());
			hashBook.put("pin",pin.toString());
			hashBook.put("egn",egn);
			hashBook.put("nivo",  getSystemData().decodeItem(Constants.CODE_SYSCLASS_NIVO_TEST, nivo,getCurrentLang(), new Date(), idUser) ); 
			
			
			//------- namirame testa po identifikator ------------------
			UsersTests userTest=null;
			for (UsersTests ut: tests) {
				if(ut.getId().longValue() == idTest.longValue()) {
					userTest = ut;
				}
			}
			
			hashBook.put("mqsto",getSystemData().decodeItem(Constants.CODE_SYSCLASS_EKATTE, userTest.getTestLocation(),getCurrentLang(), new Date(), idUser));
			hashBook.put("data",sdfDate.format(userTest.getTestDate()) +"г.  от "+sdfTime.format(userTest.getTestТime())+"ч.");
			hashBook.put("administracia"," "+getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, userTest.getAdministration(),getCurrentLang(), new Date(), idUser));
			
			try {
				
				String sumTxt = getMessageResourceString("labels","general.sum_result");
				String pointsTxt = getMessageResourceString("labels","testResults.points");
				
				List<Object[]> resuts = new AdmUsersTestsDAO(idUser).getDataResultTest(idTest);
				
				
				StringBuilder data  = new StringBuilder();
				for(Object[] resut:resuts) {
					
					Long module =  SearchUtils.asLong(resut[0]);
					Long area  =  SearchUtils.asLong(resut[1]);
					
					data.append("\n \t");
					if(module.longValue() == 0) {
						data.append(sumTxt);
					} else if(module.longValue() == 1) {
						data.append(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, area,getCurrentLang(), new Date(), idUser));
					} else if(module.longValue() == 2 || module.longValue() == 3) {
						data.append(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, module,getCurrentLang(), new Date(), idUser));
					} 
					data.append("\n \t");
					data.append(pointsTxt+": "+resut[2]);
					
				}
								
				hashBook.put("results",data.toString());
				
				
				
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при зареждане на резултати! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} finally {
				JPA.getUtil().closeConnection();
			}
			
			
			new MSWordBookmarks().setBookmarksReturnDoc(docShablon, hashBook);
			
			if(chartImageSrcBase64!=null && !chartImageSrcBase64.isEmpty()) {
				DocumentBuilder builder =  new DocumentBuilder(docShablon);
				builder.moveToDocumentEnd();
				builder.insertHtml("<image src='"+chartImageSrcBase64+"'/>", true);
			}
			
			try {
				
				FacesContext fContext = JSFUtils.getFacesContext();		
				
				
				String fileName = "participantResult.pdf";
				
				HttpServletResponse response = (HttpServletResponse) fContext.getExternalContext().getResponse();
				ServletOutputStream out = response.getOutputStream();
		
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition","attachment;filename=" + fileName);
				
				
				ByteArrayOutputStream dstStream = new ByteArrayOutputStream();
				docShablon.save(dstStream, SaveFormat.PDF);
				out.write(dstStream.toByteArray());
				
				out.flush();
				out.close();
		                
	            fContext.responseComplete();   
	            
	        } catch (IOException e) { 
	
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("generatePdfExport() - " + e.getMessage());
				}
	        }  
			
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("ObjectNotFoundException!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при попълването на шаблона с експорт!");
		} catch (Exception e) {
			LOGGER.error("Грешка при попълването на шаблона с експорт!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при попълването на шаблона с експорт!!!");
		}	
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}
	
	
	public RadarChartModel createRadarModel(List<Object[]>testResuts) {  
		RadarChartModel radarModel = new RadarChartModel();
        ChartData data = new ChartData();
        
        List<List<String>> labels = new ArrayList<>();
        List<Number> dataVal = new ArrayList<>();
        List<Number> dataVal2 = new ArrayList<>();
        
        for(Object[] item :testResuts) {
        	
//        	<h:outputText value="#{systemData.decodeItem(10093, tResult[0],  checkForTestBean.currentLang, now, checkForTestBean.idUser)}" rendered="#{tResult[0]==2 or tResult[0]==3}"/>
//		      <h:outputText value=" #{systemData.decodeItem(10093, tResult[1],  checkForTestBean.currentLang, now, checkForTestBean.idUser)}" rendered="#{tResult[0]==1}"/>
//		      
//		      <h:outputText value=" #{labels['general.sum_result']}" rendered="#{tResult[0]==0}"/>
        	
        	try {
        	
        		String label = "";
            	Long key = SearchUtils.asLong(item[0]);	
            	List<String> labell = new ArrayList<>();
	        	switch (key.intValue()) {
	
		    		case 0:
		    			label = "Общ резултат";//getMessageResourceString("lables", "testResults.points");
		    			labell =Arrays.asList(label);
		    		break;
		    		case 1:
		    			label = getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, SearchUtils.asLong(item[1]),  getCurrentLang(), new Date(), idUser);
		    			labell =Arrays.asList(label.split("  "));
		    		break;	
		    		case 2:
		    		case 3:
		    			label = getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, SearchUtils.asLong(item[0]),   getCurrentLang(), new Date(), idUser);
						labell =Arrays.asList(label.split("  "));
		    		break;
	    			
	        	}
	        	
	        	if(key.longValue()!=0) {
		        	labels.add(labell);
		        	dataVal2.add((Number)item[2]);
		        	
		        	Number maxPointsModule =100; //това са процентни точки и винаги са 100 за всеки модул
		        	
	//	        	Number maxPointsModule = (Number)item[3];
	//	        	if(maxPointsModule==null) {
	//	        		maxPointsModule = 50;
	//	        		LOGGER.error("Грешка при попълването на графика няма максимални точки!!!");
	//	    			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при попълването на графика няма максимални точки!");
	//	        	}
		        	dataVal.add(maxPointsModule);
	        	}
        	} catch (DbErrorException e) {
        		LOGGER.error("Грешка при зареждане на резултати! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			}
        }
        
        
        
        RadarChartDataSet radarDataSet = new RadarChartDataSet();
        radarDataSet.setLabel("Максимален брой точки по модул");
        radarDataSet.setFill(true);
        radarDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        radarDataSet.setBorderColor("rgb(255, 99, 132)");
        radarDataSet.setPointBackgroundColor("rgb(255, 99, 132)");
        radarDataSet.setPointBorderColor("#fff");
        radarDataSet.setPointHoverBackgroundColor("#fff");
        radarDataSet.setPointHoverBorderColor("rgb(255, 99, 132)");
        radarDataSet.setData(dataVal);
         
        RadarChartDataSet radarDataSet2 = new RadarChartDataSet();
        radarDataSet2.setLabel("Резултат на учaстника");
        radarDataSet2.setFill(true);
        radarDataSet2.setBackgroundColor("rgba(54, 162, 235, 0.2)");
        radarDataSet2.setBorderColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBackgroundColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBorderColor("#fff");
        radarDataSet2.setPointHoverBackgroundColor("#fff");
        radarDataSet2.setPointHoverBorderColor("rgb(54, 162, 235)");
        radarDataSet2.setData(dataVal2); 
         
        data.addChartDataSet(radarDataSet);
        data.addChartDataSet(radarDataSet2);
        data.setLabels(labels);
        
         
        /* Options */
        RadarChartOptions options = new RadarChartOptions();
        Elements elements = new Elements();
        ElementsLine elementsLine = new ElementsLine();
        elementsLine.setTension(0);
        elementsLine.setBorderWidth(3);
        
        elements.setLine(elementsLine);
        options.setElements(elements);
        
        RadialScales rad = new RadialScales(); 	
        //this refers to the point labels so you can change whatever you want (font size, color, font family)
        RadialLinearPointLabels pointLabels = new RadialLinearPointLabels();	
		pointLabels.setFontSize(16);
		rad.setPointLabels(pointLabels);	//here it is the assignment to the object which gathers the scales options
		options.setScales(rad);
		
		Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontSize(16);
        legend.setLabels(legendLabels);
        options.setLegend(legend);
		
        
        radarModel.setOptions(options);
        radarModel.setData(data);
        
        //radarModel.setExtender("chartExtender");
       
		
        return radarModel;
    }
	
	
	public HashMap <Long , RadarChartModel> getMapRadarModel() {
		return mapRadarModel;
	}

	public void setMapRadarModel(HashMap <Long , RadarChartModel> mapRadarModel) {
		this.mapRadarModel = mapRadarModel;
	}

	public String getChartImageSrcBase64() {
		return chartImageSrcBase64;
	}

	public void setChartImageSrcBase64(String chartImageSrcBase64) {
		this.chartImageSrcBase64 = chartImageSrcBase64;
	}

	
}
