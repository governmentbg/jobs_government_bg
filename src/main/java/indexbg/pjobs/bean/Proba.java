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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.Visibility;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.optionconfig.elements.Elements;
import org.primefaces.model.charts.optionconfig.elements.ElementsLine;
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


@Named ("proba")
@ViewScoped
public class Proba extends PJobsBean{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7992405425142604785L;
	static final Logger LOGGER = LoggerFactory.getLogger(Proba.class);
    
	
	

	
	
	/** String Base64 that represents the image bytes */
	private String chartImageSrcBase64; 
	
	private RadarChartModel radarModel;
	
	public RadarChartModel getRadarModel() {
		return radarModel;
	}

	public void setRadarModel(RadarChartModel radarModel) {
		this.radarModel = radarModel;
	}

	public Proba() {
		
          
        
	}
	
	
	public void generateTestPdfExport() {
		
		
		System.out.println("---> "+chartImageSrcBase64);
	}
	
	@PostConstruct
	public void init() {
		radarModel = new RadarChartModel();
        ChartData data = new ChartData();
         
        RadarChartDataSet radarDataSet = new RadarChartDataSet();
        radarDataSet.setLabel("My First Dataset");
        radarDataSet.setFill(true);
        radarDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        radarDataSet.setBorderColor("rgb(255, 99, 132)");
        radarDataSet.setPointBackgroundColor("rgb(255, 99, 132)");
        radarDataSet.setPointBorderColor("#fff");
        radarDataSet.setPointHoverBackgroundColor("#fff");
        radarDataSet.setPointHoverBorderColor("rgb(255, 99, 132)");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(65);
        dataVal.add(59);
        dataVal.add(90);
        dataVal.add(81);
        dataVal.add(56);
        dataVal.add(55);
        dataVal.add(40);
        radarDataSet.setData(dataVal);
         
        RadarChartDataSet radarDataSet2 = new RadarChartDataSet();
        radarDataSet2.setLabel("My Second Dataset");
        radarDataSet2.setFill(true);
        radarDataSet2.setBackgroundColor("rgba(54, 162, 235, 0.2)");
        radarDataSet2.setBorderColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBackgroundColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBorderColor("#fff");
        radarDataSet2.setPointHoverBackgroundColor("#fff");
        radarDataSet2.setPointHoverBorderColor("rgb(54, 162, 235)");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(28);
        dataVal2.add(48);
        dataVal2.add(40);
        dataVal2.add(19);
        dataVal2.add(96);
        dataVal2.add(27);
        dataVal2.add(100);
        radarDataSet2.setData(dataVal2);
         
        data.addChartDataSet(radarDataSet);
        data.addChartDataSet(radarDataSet2);
         
        List<String> labels = new ArrayList<>();
        labels.add("Eating");
        labels.add("Drinking");
        labels.add("Sleeping");
        labels.add("Designing");
        labels.add("Coding");
        labels.add("Cycling");
        labels.add("Running");
        data.setLabels(labels);
         
        /* Options */
        RadarChartOptions options = new RadarChartOptions();
        Elements elements = new Elements();
        ElementsLine elementsLine = new ElementsLine();
        elementsLine.setTension(0);
        elementsLine.setBorderWidth(3);
        elements.setLine(elementsLine);
        options.setElements(elements);
         
        radarModel.setOptions(options);
        radarModel.setData(data);
		
	}

	public String getChartImageSrcBase64() {
		return chartImageSrcBase64;
	}

	public void setChartImageSrcBase64(String chartImageSrcBase64) {
		this.chartImageSrcBase64 = chartImageSrcBase64;
	}
	

	
}
