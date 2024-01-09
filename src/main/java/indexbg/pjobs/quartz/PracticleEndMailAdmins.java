package indexbg.pjobs.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.SysConstants;
import com.indexbg.system.db.JPA;
import com.indexbg.system.quartz.BaseJobResult;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.dao.AdmUsersDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.system.Constants;

@DisallowConcurrentExecution
public class PracticleEndMailAdmins implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(PracticleEndMailAdmins.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(SysConstants.JOB_STATUS_OK);
		
		LOGGER.info("PracticleEndMailAdmins - Start");
		
		try {
			
			PracticeDAO pDao = new PracticeDAO(Constants.PORTAL_USER);
			List <Object[]> prList = pDao.findPracticesEndDateTo(new Date());
			
			if(prList!=null && !prList.isEmpty()) {
				
				Query qSiteComp = JPA.getUtil().getEntityManager().createNativeQuery("select option_value from system_options where option_label = 'JOBS.ADMIN.PRACTICES'");
				String admGroup = (String) qSiteComp.getSingleResult();
				
				List <Object[]> admList =  new AdmUsersDAO(Constants.PORTAL_USER).findAdmPractices(Long.valueOf(admGroup)); 
				
				Set <Long> idsPr = new HashSet<Long>();
				
				//групиране на админите по администрации 
				Map <Long ,List<Object[]>> admAdmins = new HashMap<Long ,List<Object[]>>();
				for(Object[] admin : admList) {
					Long admStr = SearchUtils.asLong(admin[2]);
					
					if(admAdmins.containsKey(admStr)) {
						List<Object[]> oldList = admAdmins.get(admStr);
						oldList.add(admin);
						admAdmins.replace(admStr, oldList);
					} else {
						List<Object[]> newList = new ArrayList<Object[]>();
						newList.add(admin);
						admAdmins.put(admStr, newList);
					}
				}
				
				//генериране съдържанието на съобщението
				Map <Long ,String> mailBody = new HashMap<Long ,String>();
				
				for(Object[] pr :prList) {
					
					Long prNumber = SearchUtils.asLong(pr[0]);
					Long adm      = SearchUtils.asLong(pr[1]);
					Long countS   = SearchUtils.asLong(pr[4]);
					Date dateTo   = SearchUtils.asDate(pr[3]);
					
					StringBuilder prBody = new StringBuilder("");
					
					prBody.append(" Обява номер :"+prNumber);
					prBody.append("; "+pr[5]+" / "+(pr[6]!=null?pr[6]:""));
					prBody.append(" <br/>приключване на кандидатстване: "+DateUtils.printDate(dateTo));
					prBody.append(" / брой кандидатствали: "+countS);
					prBody.append("<br/> ---------------------------------------------------------- <br/>");
					
					if(mailBody.containsKey(adm)) {
						String value = mailBody.get(adm);  
						mailBody.replace(adm, value+prBody.toString());
					} else {
						mailBody.put(adm, prBody.toString());
					}
					
					idsPr.add(prNumber);
				}
				
				try {
					
					JPA.getUtil().begin();
					
					for(Map.Entry<Long, String> entry : mailBody.entrySet()) {
						
						String mBody = entry.getValue();
						
						admList = admAdmins.get(entry.getKey());
						
						for(Object[]admin: admList) {
							
							
							Query queryMail = JPA.getUtil().getEntityManager().createNativeQuery(" insert into jobs_mail (id, id_user, administration, email, name_lice, subject, msg, date_mail, user_reg, code_object, status, status_date) "
									  + " values (nextval('seq_mail'), ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11)");
		  				
							queryMail.setParameter(1, admin[0]);
							queryMail.setParameter(2, admin[2]); //administration
							queryMail.setParameter(3, admin[1]); //email
							queryMail.setParameter(4, admin[4]); //nameLice
							queryMail.setParameter(5, "Приключване кандидатстване по обяви за стаж ");
							queryMail.setParameter(6, mBody);
							queryMail.setParameter(7, new Date());
							queryMail.setParameter(8, -1L);
							queryMail.setParameter(9, -1L);
							queryMail.setParameter(10, Constants.CODE_ZNACHENIE_STATUS_MAIL_NOT_SEND);
							queryMail.setParameter(11, new Date());
		
							queryMail.executeUpdate();
							
						}
					}
			        	
					String sql = "UPDATE jobs_practice SET mail_to_admin = :da WHERE ID IN (:IDS)";
					
					Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
					
					query.setParameter("da", Constants.CODE_ZNACHENIE_DA);
					query.setParameter("IDS", idsPr);
					query.executeUpdate();
					
					JPA.getUtil().commit();
				
				} catch (Exception e) {
					LOGGER.error("Error saving mail Object", e);
					JPA.getUtil().rollback();
				}
		        
			}
		} catch (Exception e) {
			jobResult.setStatus(SysConstants.JOB_STATUS_WARN);
			LOGGER.error("Възникна грешка при извличане на съобщения за изпращене!!!", e);
			throw new JobExecutionException(e);
        }  finally {
			JPA.getUtil().closeConnection();
		}
		
		LOGGER.info("PracticleEndMailAdmins - end");
		
	}

}
