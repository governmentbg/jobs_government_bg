package indexbg.pjobs.bean;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import indexbg.pjobs.wsclient.govcomp.ArrayOfCompetitionType;
import indexbg.pjobs.wsclient.govcomp.CompetitionService;
import indexbg.pjobs.wsclient.govcomp.CompetitionType;
import indexbg.pjobs.wsclient.govcomp.ICompetitionService;


@Named ("testWSBean")
@RequestScoped
public class TestWSBean {
	
	

	public TestWSBean() {
		super();
	}
	

	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestWSBean.class);
	
	public void testCompServ() {
		
		
		try {
			//SimpleDateFormat sdtf = new SimpleDateFormat("dd-MM-yyyy");
		
		    XMLGregorianCalendar kumdata = null;//	= DateUtils.toXMLGregorianCalendar(sdtf.parse("01-01-2013"));
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        CompetitionService service1 = new CompetitionService();
	        System.out.println("Create Web Service...");
	        ICompetitionService port1 = service1.getBasicHttpBindingICompetitionService();
	        System.out.println("Call Web Service Operation...");
	        
	        
	        
	        ArrayOfCompetitionType comptype = port1.searchCompetitions(null,"0000000090",null,kumdata);
       	    List<CompetitionType> listcomp =comptype.getCompetitionType();
	        
       	    System.out.println("----  List<CompetitionType>-size---- "+listcomp.size());
		}   catch (Exception e) {
        	
			LOGGER.error("Възникна грешка при връзка с уеб услигата!!!", e);
			e.printStackTrace();
        }
		
		
	}
	
	
	
	
	
}
