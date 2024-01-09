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

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.SaveFormat;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.dao.AdmUsersTestsDAO;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.MSWordBookmarks;
import indexbg.pjobs.system.PJobsBean;



@Named("testsResult")
@ViewScoped
public class TestsResult extends PJobsBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3044452214697552758L;
	static final Logger LOGGER = LoggerFactory.getLogger(TestsResult.class);
	
	private LazyDataModelSQL2Array resultTestsList;	
	private List <Object[]> testResuts = new ArrayList<Object[]>();
	private Long userId;
	private Long administration;
	
	private Long period;
	private Date dateFrom;
	private Date dateTo;
	private Long level;
	private Long status;
	private Long location;
	
	private List<Long> halls = new ArrayList<Long>();
	
	private  List<SystemClassif> periodList;
	
	// Прикачени файлове
	private FilesDAO filesDAO;
	private List<Files> filesList = new ArrayList<>();
	
	
	private HashMap <Long , RadarChartModel> mapRadarModel = new  HashMap<>();
	
	private Long idTestRez;
	
	
	/** String Base64 that represents the image bytes */
	private String chartImageSrcBase64;
	
	public TestsResult() {
	}
	
	@PostConstruct
	public void init() {
		try {
			setUserId(getUserData().getUserId());
			administration = getUserData().getCodeOrg(); 
			halls =  new AdmUsersTestsDAO(userId).getHalls();
			filesDAO = new FilesDAO(userId);
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}finally {
			JPA.getUtil().closeConnection();
		}	
		
		search();
		
		idTestRez=null;
		
	}
	
	
	/** Метод за смяна на датите при избор на период за търсене.
	 * 
	 * 
	 */
	public void changePeriod () {
		
    	if (period != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(period);
			setDateFrom(di[0]);
			setDateTo(di[1]);
		
    	} else {
			setDateFrom(null);
			setDateTo(null);
		}
		
    	return ;
    }
	
	
	public void search() {
				
		
		try {
			SelectMetadata smd  = new AdmUsersTestsDAO(userId).findResultsForTests(dateFrom, dateTo, level,administration , status,location);
			String defaultSortColumn = "A0";
			
			resultTestsList = new LazyDataModelSQL2Array(smd, defaultSortColumn);
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на заявления! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
		
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	
	/** Метод за зачистване на зададените стойности за търсене
	 * 
	 * 
	 */
	public void actionClear(){
		
		period = null;
		dateFrom = null;
		dateTo = null;
		level = null;
		location = null;
		status = null;
		resultTestsList = null;
	}
	
	public void onRowToggle(ToggleEvent event) {
	   Object row []  = (Object[]) event.getData();

//	   System.out.println("-----> "+row[0]);
//	   System.out.println(event.getVisibility());
	   
	   if(event.getVisibility() == Visibility.VISIBLE) {
		   try {
			   
			    idTestRez = SearchUtils.asLong(row[0]);
			   
			   testResuts = new AdmUsersTestsDAO(userId).getDataResultTest(idTestRez);
			   if(row[7]!=null) {
				   this.filesList = this.filesDAO.findByCodeObjAndIdObj(Constants.CODE_OBJECT_TST_TEST_GROUP, SearchUtils.asLong(row[7]));
			   }
//			   List <Object[]> resuts = new AdmUsersTestsDAO(userId).getDataResultTest(SearchUtils.asLong(row[0]));
//			   for(Object[] res:resuts) {
//				   testResuts.add(Object[] {})
//			   }
			   
			   if(!mapRadarModel.containsKey(idTestRez) && !testResuts.isEmpty()) {
				   mapRadarModel.put(idTestRez, createRadarModel(testResuts));
			   }
		   } catch (DbErrorException e) {
				LOGGER.error("Грешка при търсене на заявления! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);	
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
	
	public LazyDataModelSQL2Array getResultTestsList() {
		return resultTestsList;
	}

	public void setResultTestsList(LazyDataModelSQL2Array resultTestsList) {
		this.resultTestsList = resultTestsList;
	}

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List <Object[]> getTestResuts() {
		return testResuts;
	}

	public void setTestResuts(List <Object[]> testResuts) {
		this.testResuts = testResuts;
	}

	public Long getLocation() {
		return location;
	}

	public void setLocation(Long location) {
		this.location = location;
	}

	public List<Long> getHalls() {
		return halls;
	}

	public void setHalls(List<Long> halls) {
		this.halls = halls;
	}
	
	public List<SystemClassif> getPeriodList() {
		if(periodList==null) {
			
			try {
				periodList = getSystemData().getSysClassification(Constants.CODE_CLASSIF_PERIOD_NOFUTURE, new Date(), getCurrentLang(), userId);
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
        		List<String> labell = new ArrayList<>();
            	Long key = SearchUtils.asLong(item[0]);	
        		
	        	switch (key.intValue()) {
	
		    		case 0:
		    			label = "Общ резултат";//getMessageResourceString("lables", "testResults.points");
		    			labell =Arrays.asList(label);
		    		break;
		    		case 1:
		    			label = getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, SearchUtils.asLong(item[1]),  getCurrentLang(), new Date(), userId);
		    			labell =Arrays.asList(label.split("  "));
		    		break;	
		    		case 2:
		    		case 3:
		    			label = getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, SearchUtils.asLong(item[0]),   getCurrentLang(), new Date(), userId);
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
        
        return radarModel;
    }
	
	
	public HashMap <Long , RadarChartModel> getMapRadarModel() {
		return mapRadarModel;
	}

	public void setMapRadarModel(HashMap <Long , RadarChartModel> mapRadarModel) {
		this.mapRadarModel = mapRadarModel;
	}
	
	public void generateTestPdfExport(Object[] rowData) {
		try {
			
			if(rowData == null ) {
				
				LOGGER.error("Липсват данни на тест за експорт!!!");
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Липсват данни на тест за експорт!");
				return;
			}
			
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
			
			//1. Взема шаблона 
			String loadShablon="/resources/docs/participantResultShablon.docx";		
			ServletContext context_ = (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
			InputStream  shabl = new FileInputStream(context_.getRealPath("")+loadShablon);
			
			Long idTest =  SearchUtils.asLong(rowData[0]);
			
			
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
			
			
			hashBook.put("uchastnik"," "+rowData[2]);
			hashBook.put("pin"," "+rowData[10]);
			hashBook.put("egn"," "+rowData[11]);
			hashBook.put("nivo",  getSystemData().decodeItem(Constants.CODE_SYSCLASS_NIVO_TEST, SearchUtils.asLong(rowData[3]),getCurrentLang(), new Date(), userId) ); 
			hashBook.put("mqsto",getSystemData().decodeItem(Constants.CODE_SYSCLASS_EKATTE, SearchUtils.asLong(rowData[4]),getCurrentLang(), new Date(), userId));
			hashBook.put("data",sdfDate.format(rowData[5]) +"г. от "+sdfTime.format(rowData[6])+"ч.");
			hashBook.put("administracia"," "+getSystemData().decodeItem(Constants.CODE_SYSCLASS_ADM_REGISTER, administration,getCurrentLang(), new Date(), userId));
			
			
			try {
				
				String sumTxt = getMessageResourceString("labels","general.sum_result");
				String pointsTxt = getMessageResourceString("labels","testResults.points");
				
				List<Object[]> resuts = new AdmUsersTestsDAO(userId).getDataResultTest(idTest);
				
				
				StringBuilder data  = new StringBuilder();
				for(Object[] resut:resuts) {
					
					Long module =  SearchUtils.asLong(resut[0]);
					Long area  =  SearchUtils.asLong(resut[1]);
					
					data.append("\n \t");
					if(module.longValue() == 0) {
						data.append(sumTxt);
					} else if(module.longValue() == 1) {
						data.append(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, area,getCurrentLang(), new Date(), userId));
					} else if(module.longValue() == 2 || module.longValue() == 3) {
						data.append(getSystemData().decodeItem(Constants.CODE_SYSCLASS_MODUL, module,getCurrentLang(), new Date(), userId));
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

	public Long getIdTestRez() {
		return idTestRez;
	}

	public void setIdTestRez(Long idTestRez) {
		this.idTestRez = idTestRez;
	}
	
	
	public String getChartImageSrcBase64() {
		return chartImageSrcBase64;
	}

	public void setChartImageSrcBase64(String chartImageSrcBase64) {
		this.chartImageSrcBase64 = chartImageSrcBase64;
	}
	
	
}
