package indexbg.pjobs.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import indexbg.pjobs.system.Constants;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.SysConstants;
import com.indexbg.system.db.JPA;
import com.indexbg.system.quartz.BaseJobResult;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.db.dao.PracticeDAO;

@DisallowConcurrentExecution
public class RankingPracticleJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(RankingPracticleJob.class);
	
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(SysConstants.JOB_STATUS_OK);
		
		LOGGER.info("RankingPracticleJob - Start");
		 
		try {
			PracticeDAO pDao = new PracticeDAO(Constants.PORTAL_USER);
			List <Object[]> prList = pDao.findPracticesForRanking2(new Date());
	        
			
	        for(Object[]pr: prList) {
	        	
	        	LOGGER.debug("id/num/svob: "+pr[0]+" / " + pr[1]+" / "+pr[2]);
	        	
	        	Long idPract  = SearchUtils.asLong(pr[0]);
	        	Long idCamp   = SearchUtils.asLong(pr[3]);
	        	Long campType = SearchUtils.asLong(pr[4]);
	        	
	        	Long freePos  = SearchUtils.asLong(pr[2]);
	        	
	        	//zaglavie na obqwa
        		String pacticleTitle =  SearchUtils.asString(pr[5]);
        		Date d1 = SearchUtils.asDate(pr[6]);
        		Date d2 = SearchUtils.asDate(pr[7]);
        		Long administration = SearchUtils.asLong(pr[8]);
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
		        			LOGGER.debug("idStudent / class : "+s[0]+" / " + s[1]);
		        			
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
		        		LOGGER.error("Error saving ranking2", e);
						JPA.getUtil().rollback();
	 			    } 
	        	}
	        }
			
		} catch (Exception e) {
			jobResult.setStatus(SysConstants.JOB_STATUS_WARN);
			LOGGER.error("Възникна грешка при второ класиране!!!", e);
			throw new JobExecutionException(e);
        }  finally {
			JPA.getUtil().closeConnection();
		}
		
		LOGGER.info("RankingPracticleJob - end");
		
	}

}
