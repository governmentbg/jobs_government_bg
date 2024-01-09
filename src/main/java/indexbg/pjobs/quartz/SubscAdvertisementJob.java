package indexbg.pjobs.quartz;

import static com.indexbg.system.utils.SearchUtils.asLong;
import static com.indexbg.system.utils.SearchUtils.asString;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.system.Constants;

@DisallowConcurrentExecution
public class SubscAdvertisementJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscAdvertisementJob.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(SysConstants.JOB_STATUS_OK);

		LOGGER.info("SubscAdvertisementJob - Start");
		
		try {
			Map<String,List<Object[]>> admUsrMap = new HashMap<String,List<Object[]>>();
			
			List<Object[]> advertisementList = new ArrayList<Object[]>();
		
			String sqlAdvertisement = "Select JA.id, JA.position ,JJ.job_shortname , JA.unit ,JАUNIT.str_name ,JA.administration ,ADM.subject_name , "
					+ " JA.location_town , E.ime ,JA.salary ,JA.date_to "
					+ " From jobs_advertisement JA "
					+ " JOIN jobs_administration ADM ON ADM.id = JA.administration "
					+ " JOIN jobs_administration_structures JАUNIT ON JАUNIT.ID = JA.unit "
					+ " JOIN jobs_jobs JJ ON JJ.id = JA.position "
					+ " JOIN ekatte_att E ON E.ekatte = JA.location_town "
					+ " Where "
					+ " JA.adv_type = 2 AND "
					+ " JA.date_from <= CURRENT_DATE and   JA.date_to >= CURRENT_DATE "; 
			
			Query qAdvertisement = JPA.getUtil().getEntityManager().createNativeQuery(sqlAdvertisement);
			advertisementList = (List<Object[]>)qAdvertisement.getResultList();
			
			if(advertisementList!=null && !advertisementList.isEmpty()) {
			
				String sql = "SELECT  subs.administration ,subs.user_id , au.email ,au.names ,subs.professional_field ,subs.position "
						+ " FROM jobs_subscription subs "
						+ " JOIN adm_users au ON subs.user_id = au.user_id and au.email is not null "
						+ " WHERE subs.type = 2  "
						+ " AND subs.user_id not in ( "
						+ " 	Select  SR.user_id FROM jobs_subscription_results SR"
						+ " 	WHERE SR.advertisement_id in (Select JA.id  From jobs_advertisement JA Where JA.adv_type = 2 and JA.date_from <= CURRENT_DATE and   JA.date_to >= CURRENT_DATE)"
						+ " ) ";
				Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql);
				List<Object[]> abonamentList = (List<Object[]>)q.getResultList();
			
		        for(Object[] rowSubs: abonamentList) {
		        	
		        	String admKey = asString(rowSubs[0]);
		        	Long   userId = asLong(rowSubs[1]);
		        	String email  = asString(rowSubs[2]);
		        	String imena  = asString(rowSubs[3]);
		        	Long   profField = asLong(rowSubs[4]);
		        	Long   position = asLong(rowSubs[5]);
		        	
		        	if(admUsrMap.containsKey(admKey)) {
		        		admUsrMap.get(admKey).add(new Object[]{userId,email,imena ,profField ,position});
		        	} else {
		        		List<Object[]> userList = new ArrayList<Object[]>();
		        		userList.add(new Object[]{userId,email,imena,profField ,position});
		        		admUsrMap.put(admKey, userList);
		        	}
		        }
		        
		        
		        if(!admUsrMap.isEmpty()) {
		        	
		        	Query qLink = JPA.getUtil().getEntityManager().createNativeQuery("select option_value from system_options where option_label = 'linkToPJobsSite'");
					String link = (String) qLink.getSingleResult();
		        	
		        	for(Object[] adv: advertisementList) {
		        		
		        		Long advId         = SearchUtils.asLong(adv[0]);
		        		Long advPos        = SearchUtils.asLong(adv[1]);
		        		String advPosStr   = SearchUtils.asString(adv[2]);
		        		Long advUnit       = SearchUtils.asLong(adv[3]);
		        		String advUnitStr  = SearchUtils.asString(adv[4]);
		        		Long advAdmin      = SearchUtils.asLong(adv[5]);
		        		String advAdminStr = SearchUtils.asString(adv[6]);
		        		Long advTown       = SearchUtils.asLong(adv[7]);
		        		String advTownStr  = SearchUtils.asString(adv[8]);
		        		Long advSalary     = SearchUtils.asLong(adv[9]);
		        		Date advDateTo     = SearchUtils.asDate(adv[10]);
		        		Long advProfField  = SearchUtils.asLong(adv[11]);
		        		
		        		if(admUsrMap.containsKey(advAdmin)) { //prowerka dali imame abonirali se za tazi administraciq
		        		
		        			List<Object[]> subUserAdm = admUsrMap.get(advAdmin);
		        			
		        			JPA.getUtil().begin();
		        			
		        			for(Object[] subUser: subUserAdm) {
		        				
		        				Long   uProfField = asLong(subUser[3]);
		        	        	Long   uPosition  = asLong(subUser[4]);
		        	        	
		        	        	boolean add = false;
		        	        	
		        				//проверка за дали абонамента е за тази обява
		        				if(uPosition == null && uProfField== null) {
		        					add = true;
		        				} else  if(uPosition !=null && uProfField== null) {
		        					if(uPosition.longValue() == advPos.longValue()) {
		        						add = true;
		        					}	
	        					} else if (uPosition ==null && uProfField!= null){
	        						if(uProfField.longValue() == advProfField.longValue()) {
		        						add = true;
		        					}	
	        					} else {
	        						if(uPosition.longValue() == advPos.longValue() && uProfField.longValue() == advProfField.longValue()) {
		        						add = true;
		        					}
	        					}
		        				
		        				if (add) {
		        					
		        					Long idSubscUser = SearchUtils.asLong(subUser[0]);
		        					
		        					ArrayList<String> mailsTo = new ArrayList<>();
									mailsTo.add(SearchUtils.asString(subUser[1]));
									
									String names = SearchUtils.asString(subUser[2]); 
									
		        				
						        	StringBuffer dataAdver = new StringBuffer();
									
									dataAdver.append("<hr> Длъжност / Звено / Административна структура:");
									dataAdver.append("<p style='padding-left:10px; margin:0px'><b> "+advPosStr +"</b><br/>");
									if(advUnit!=null) {
										dataAdver.append("<b> "+advUnitStr +"</b><br/>");
									}
									dataAdver.append("<b> "+advAdminStr+"</b></p>") ;
									dataAdver.append("<br/>Населено място:") ;
									dataAdver.append("<p style='padding-left:10px; margin:0px'><b> "+advTownStr+" </b> </p> ");
									dataAdver.append("<br/>Минимален размер на основната заплата:") ;
									dataAdver.append("<p style='padding-left:10px; margin:0px'><b> "+advSalary+" лв. </b></p>");
									dataAdver.append("<br/>Срок за подаване на документи: ");	
									dataAdver.append("<p style='padding-left:10px; margin:0px'><b>"+advDateTo+" г.</b> </p><hr> ");					
									
									new MailDAO(-1L).saveMail(Constants.CODE_ZNACHENIE_SHABLON_SUBSCRIBE_TO_ADVER_MOBIL, mailsTo, advAdmin, new Date(), null, null, names, idSubscUser, link, -1L, Constants.CODE_OBJECT_ADVERTISEMENT, null, dataAdver.toString(), null, null, null, null, null);
									
									JPA.getUtil().flush();
									
									Query queryInsert = JPA.getUtil().getEntityManager().createNativeQuery(" insert into jobs_subscription_results (user_id, advertisement_id, adv_type) values (?1, ?2, ?3)");
									queryInsert.setParameter(1, idSubscUser);
									queryInsert.setParameter(2, advId);
									queryInsert.setParameter(3, 2);
									
									queryInsert.executeUpdate();
								
		        				}
		        			}
		        			
		        			 JPA.getUtil().commit();
		        		}
		        	}
		        
		        }
			}
			
			LOGGER.info("SubscAdvertisementJob - End");
			
		} catch (Exception e) {
			
			JPA.getUtil().rollback();
			
			jobResult.setStatus(SysConstants.JOB_STATUS_WARN);
			LOGGER.error("Възникна грешка при SubscAdvertisementJob абонаменти за обяви за работа!!!", e);
			throw new JobExecutionException(e);
        }  finally {
			JPA.getUtil().closeConnection();
		}
		
		
	}

}
