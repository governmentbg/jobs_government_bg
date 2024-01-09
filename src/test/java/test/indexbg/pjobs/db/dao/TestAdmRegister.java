package test.indexbg.pjobs.db.dao;

import java.net.URL;

import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.Service;

import com.indexbg.system.utils.DateUtils;

import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.system.SystemData;
import indexbg.pjobs.wsclient.admreg.ArrayOfBatchType;
import indexbg.pjobs.wsclient.admreg.ArrayOfString;
import indexbg.pjobs.wsclient.admreg.IBatchInfoService;

public class TestAdmRegister {

	public static void main(String[] args) {
		
		try {
			
			
			 AdministrationDAO dao = new AdministrationDAO(-1L, new SystemData());
			 dao.updateAdmRegisterEntries();
			
			
//			URL url = new URL("https://iisda.government.bg/Services/RAS/RAS.Integration.Host/BatchInfoService.svc?singleWsdl");
//			QName qname = new QName("http://iisda.government.bg/RAS/IntegrationServices", "BatchInfoService");
//			//		http://wsmf.delo.indexbg.com
//			Service service = Service.create(url, qname);
//
//			// Hello helloPort = service.getHelloPort();
//			IBatchInfoService sPort = (IBatchInfoService) service
//					.getPort(IBatchInfoService.class,new AddressingFeature(true, true));
//			
//			
//			
//			//0000000084 МЗХ
//			ArrayOfString batchIdentificationNumber = new ArrayOfString() ;
//			batchIdentificationNumber .getString().add("0000000084");
//		
//						
//			 ArrayOfBatchType all = sPort.getBatchDetailedInfo(batchIdentificationNumber, DateUtils.toXMLGregorianCalendar(new Date()), null);
//
//			 
//			 dao.getSubjectAdministrations(all.getBatchType().get(0));
			 
			 
			 
			System.out.println("---------------------");
			
//			BatchStatusEnum status = BatchStatusEnum.ACTIVE;
//			ArrayOfBatchIdentificationInfoType searchBatchesIdentificationInfo = sPort.searchBatchesIdentificationInfo(null, null, null, null, status , DateUtils.toXMLGregorianCalendar(new Date()), null);
//			@SuppressWarnings("unused")
//						
//			List<BatchIdentificationInfoType> organizations = searchBatchesIdentificationInfo.getBatchIdentificationInfoType();
//			
//			for (BatchIdentificationInfoType org : organizations) {
//				
//				System.out.println(org.getIdentificationNumber() + "\t" + org.getName());
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
