package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

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

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.UsersTests;
import indexbg.pjobs.db.dao.AdmUsersTestsDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@Named ("testResults")
@ViewScoped
public class TestResults extends PJobsBean{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4725933338834325614L;

	static final Logger LOGGER = LoggerFactory.getLogger(TestResults.class);
    
	private Long idUser;
	private AdmUsersTestsDAO testDao;
	private List<UsersTests> tests = new  ArrayList<UsersTests>();
	private List <Object[]> testResuts = new ArrayList<Object[]>();
	
	
	private HashMap <Long , RadarChartModel> mapRadarModel = new  HashMap<>();
	
	private Long idTestRez;
	
	public TestResults() {
		
	}
	
	@PostConstruct
	public void init() {
		try {
			idUser = getUserData().getUserId();
			testDao = new AdmUsersTestsDAO(idUser);
			if(idUser!=null) {
				tests = testDao.getUserTests(idUser);
			}
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
	
	public void onRowToggle(ToggleEvent event) {
		   UsersTests ut  = (UsersTests) event.getData();

//		   System.out.println("-----> "+ut.getId());
//		   System.out.println(event.getVisibility());
		   
		   
		   idTestRez = SearchUtils.asLong(ut.getId());
		   
		   if(event.getVisibility() == Visibility.VISIBLE) {
			   try {
				   testResuts = new AdmUsersTestsDAO(idUser).getDataResultTest(ut.getId());
				   
				   
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
	
  
	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}
	
	public Date getToday(){
		return new Date();
	}

	public List<UsersTests> getTests() {
		return tests;
	}

	public void setTests(List<UsersTests> tests) {
		this.tests = tests;
	}

	public List<Object[]> getTestResuts() {
		return testResuts;
	}

	public void setTestResuts(List<Object[]> testResuts) {
		this.testResuts = testResuts;
	}

	public HashMap <Long , RadarChartModel> getMapRadarModel() {
		return mapRadarModel;
	}

	public void setMapRadarModel(HashMap <Long , RadarChartModel> mapRadarModel) {
		this.mapRadarModel = mapRadarModel;
	}

	public Long getIdTestRez() {
		return idTestRez;
	}

	public void setIdTestRez(Long idTestRez) {
		this.idTestRez = idTestRez;
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
        
        return radarModel;
    }
}
