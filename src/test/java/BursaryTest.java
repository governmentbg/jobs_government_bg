import java.util.Date;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;

import indexbg.pjobs.db.Bursary;
import indexbg.pjobs.db.dao.BursaryDAO;


public class BursaryTest {

	public BursaryTest() {
		
	}
	
	private Bursary bursary = new Bursary();
	
	@org.junit.Test
	public void testMethod() {
		bursary.setAdministration(-1L);
		bursary.setBursary(-1L);
		bursary.setSubject("a"); 
		bursary.setNum(-1L);
		bursary.setDateFrom(new Date());
		bursary.setDateTo(new Date());
		bursary.setAddInfo(" a");
		bursary.setStatus(-1L);
		bursary.setStatusDate(new Date());
		try {
			JPA.getUtil().begin();
			BursaryDAO bdao = new BursaryDAO(-1L);
			bdao.save(bursary);
			JPA.getUtil().commit();
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			e.printStackTrace();
		}catch (Exception e) {
			JPA.getUtil().rollback();
			e.printStackTrace();	
		
		}finally {
			JPA.getUtil().closeConnection();
		}
		
		
	}
   
}
