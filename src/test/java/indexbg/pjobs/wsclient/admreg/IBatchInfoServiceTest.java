package indexbg.pjobs.wsclient.admreg;


import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.utils.DateUtils;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;
import java.net.URL;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class IBatchInfoServiceTest  {

@Test
    public void searchBatchesIdentificationInfoTest(){
        try {
            URL url = new URL("https://iisda.government.bg/Services/RAS/RAS.Integration.Host/BatchInfoService.svc?singleWsdl");
            QName qname = new QName("http://iisda.government.bg/RAS/IntegrationServices", "BatchInfoService");
            //		http://wsmf.delo.indexbg.com
            Service service = Service.create(url, qname);

            // Hello helloPort = service.getHelloPort();
            IBatchInfoService sPort = (IBatchInfoService) service
                    .getPort(IBatchInfoService.class,new AddressingFeature(true, true));

            BatchStatusEnum status = BatchStatusEnum.ACTIVE;
            ArrayOfBatchIdentificationInfoType searchBatchesIdentificationInfo = sPort.searchBatchesIdentificationInfo(null, null, null, null, status , DateUtils.toXMLGregorianCalendar(new Date()), null);
            @SuppressWarnings("unused")

            List<BatchIdentificationInfoType> organizations = searchBatchesIdentificationInfo.getBatchIdentificationInfoType();
            assertNotNull(organizations);
            assertTrue(organizations.size()>0);
        } catch (Exception e) {
           e.printStackTrace();
           fail();
        }
    }
}