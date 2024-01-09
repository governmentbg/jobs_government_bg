package test.indexbg.pjobs.db.dao;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.indexbg.system.SysConstants;
import com.indexbg.system.db.JPA;
import com.indexbg.system.quartz.BaseJobResult;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;

public class TestRanking2{

	@Test
	public void TestRanking() {
		
		BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(SysConstants.JOB_STATUS_OK);
		
		try {
			PracticeDAO pDao = new PracticeDAO(Constants.PORTAL_USER);
			List <Object[]> prList = pDao.findPracticesForRanking2ForTestsOnly(161L);
		//	List <Object[]> prList = pDao.findPracticesForRanking2(new Date());
			
	        for(Object[]pr: prList) {
	        	
	        	System.out.println("id/num/svob: "+pr[0]+" / " + pr[1]+" / "+pr[2]);
	        	
	        	Long idPract  = SearchUtils.asLong(pr[0]);
	        	Long idCamp   = SearchUtils.asLong(pr[3]);
	        	Long campType = SearchUtils.asLong(pr[4]);
	        	
	        	Long freePos  = SearchUtils.asLong(pr[2]);
	        	
	        	//zaglavie na obqwa
        		String pacticleTitle =  SearchUtils.asString(pr[5]);
        		Date d1 = SearchUtils.asDate(pr[6]);
        		Date d2 = SearchUtils.asDate(pr[7]);
        		Long administration = SearchUtils.asLong(pr[4]);
	        	//izmykwane na kandidatite za второто класиране
	        	List <Object[]> studList = pDao.findStudentsForRanking2(idCamp, idPract, campType);
	        	
	        	if(studList!=null && studList.size()>0) {
	        		// класират се първите от списъка спрямо колко свободни места има
	        		int broi = studList.size();
	        		if(freePos.intValue()< broi) {broi = freePos.intValue();}
	        		
	        		try {
	    				
	    				JPA.getUtil().begin();
	        		
		        		for(int i=0; i<broi; i++) {
		        			Object[]s = studList.get(i);
		        			System.out.println("idStudent / class : "+s[0]+" / " + s[1]);
		        			
		        			Long recorId       = SearchUtils.asLong(s[4]);
		        			Long studentId     = SearchUtils.asLong(s[0]);
		        			String email       = SearchUtils.asString(s[2]);
		        			String studentName = SearchUtils.asString(s[3]);
		        			
		        			
		        			
		        			Query q = JPA.getUtil().getEntityManager().createNativeQuery("UPDATE jobs_practice_lice SET ranked2=? WHERE id=?");
		        			q.setParameter(1, Constants.CODE_ZNACHENIE_DA ); 
		        			q.setParameter(2,  recorId);
							q.executeUpdate();
		        			
		        			ArrayList<String> mailsTo = new ArrayList<>();
		        			if(email!=null && !email.isEmpty()) {
		        				mailsTo.add(email);
		        				new MailDAO(Constants.PORTAL_USER).saveMail(Constants.CODE_ZNACHENIE_SHABLON_RANKED_SECOND_RANKING, mailsTo, administration, new Date(), null, null,studentName, studentId, null, Constants.PORTAL_USER, Constants.CODE_OBJECT_APPLY, null, null, null, null, pacticleTitle, d1, d2);					
		        			}
		        			
		        			
		        			
		        		}
		        		
		        		JPA.getUtil().commit();
		        		
		        	} catch (Exception e) {
		        		e.printStackTrace();
		    			fail();
						JPA.getUtil().rollback();
	 			    } 
	        	}
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
        }  finally {
			JPA.getUtil().closeConnection();
		}
		
		
	}
	
	
	
	
	

	
	
}
