package indexbg.pjobs.quartz;

import static com.indexbg.system.utils.SearchUtils.asLong;
import static com.indexbg.system.utils.SearchUtils.asString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.xml.datatype.XMLGregorianCalendar;

import indexbg.pjobs.system.Constants;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
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

import indexbg.pjobs.wsclient.govcomp.ArrayOfCompetitionType;
import indexbg.pjobs.wsclient.govcomp.CompetitionService;
import indexbg.pjobs.wsclient.govcomp.CompetitionStatusesEnum;
import indexbg.pjobs.wsclient.govcomp.CompetitionType;
import indexbg.pjobs.wsclient.govcomp.ICompetitionService;

@DisallowConcurrentExecution
public class MyCompetitionsJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyCompetitionsJob.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(SysConstants.JOB_STATUS_OK);
		
		LOGGER.info("MyCompetitionsJob - Start");
				
		Map<String,List<Object[]>> admUsrMap = new HashMap<String,List<Object[]>>();
		List<Long> insertedCompList = new ArrayList<Long>();
		String siteComp = "";
		
		try {
		
			Query qSiteComp = JPA.getUtil().getEntityManager().createNativeQuery("select option_value from system_options where option_label = 'siteCompetitions'");
			siteComp = (String) qSiteComp.getSingleResult();
			
			
			String sql = "SELECT adm.eik, adm.nomer_register , subs.user_id , au.email ,au.names ,subs.administration ,adm.subject_name ,subs.position FROM jobs_subscription subs JOIN jobs_administration adm ON adm.id= subs.administration JOIN adm_users au ON subs.user_id = au.user_id";
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql);
			List<Object[]> abonamentList = (List<Object[]>)q.getResultList();
	        
			
			String sqlInsertedComp = "SELECT competition_id FROM jobs_subscription_competition"; //TODO da se pomisli samo za aktivnite kum datata na izvikvane  WHERE deadline_date >= now()
			Query qInsertedComp = JPA.getUtil().getEntityManager().createNativeQuery(sqlInsertedComp);
			List<Object>  insertedCompList_ = (List<Object>)qInsertedComp.getResultList();
			
			for(Object obj: insertedCompList_) { 
				insertedCompList.add(asLong(obj));
			}
			
	        for(Object[] rowSubs: abonamentList) {
	        	
	        	String admKey = asString(rowSubs[1]);
	        	Long   userId = asLong(rowSubs[2]);
	        	String email  = asString(rowSubs[3]);
	        	String imena  = asString(rowSubs[4]);
	        	Long   admID  = asLong(rowSubs[5]);
	        	String admName  = asString(rowSubs[6]);
	        	Long position = asLong(rowSubs[7]);
	        	
	        	if(admUsrMap.containsKey(admKey)) {
	        		admUsrMap.get(admKey).add(new Object[]{userId,email,imena,admID,admName,position});
	        	} else {
	        		List<Object[]> userList = new ArrayList<Object[]>();
	        		userList.add(new Object[]{userId,email,imena,admID,admName,position});
	        		admUsrMap.put(admKey, userList);
	        	}
	        }
	        
		} catch (Exception e) {
			jobResult.setStatus(SysConstants.JOB_STATUS_WARN);
			LOGGER.error("Възникна грешка при извличане на абонаменти за обяви за работа!!!", e);
			throw new JobExecutionException(e);
        }  finally {
			JPA.getUtil().closeConnection();
		}
		
		if(!admUsrMap.isEmpty()) {
				
			try {
				//SimpleDateFormat sdtf = new SimpleDateFormat("dd-MM-yyyy");
			
			    XMLGregorianCalendar kumdata = null;//	= DateUtils.toXMLGregorianCalendar(sdtf.parse("01-01-2013"));
//		        System.out.println("***********************");
//		        System.out.println("Create Web Service Client...");
		        CompetitionService service1 = new CompetitionService();
//		        System.out.println("Create Web Service...");
		        ICompetitionService port1 = service1.getBasicHttpsBindingICompetitionService();
//		        System.out.println("Call Web Service Operation...");
		       
		        //varteneto za izmakvane na obqwite
		        for (Map.Entry<String,List<Object[]>> entr : admUsrMap.entrySet()) {
		        	String key = entr.getKey();
		        	List<Object[]> val = entr.getValue();
		       
//		        	System.out.println("Item : " + key);
		        	
		        	 ArrayOfCompetitionType comptype = port1.searchCompetitions(null,key,null,kumdata);
		        	 List<CompetitionType> listcomp =comptype.getCompetitionType();
		        	 
		        	 
		        	 try {
		        		 
		        		 JPA.getUtil().begin();
		        		 for(CompetitionType comp: listcomp) {
			        				        		
			        		// System.out.println("comp.getStatus() ---> "+comp.getStatus().value());
			        		//System.out.println("comp.getStatus() PUBLISHED ---> "+comp.getStatus().equals(CompetitionStatusesEnum.PUBLISHED));
			        		
			        		//samo publikuvani
			        		if(comp.getStatus().equals(CompetitionStatusesEnum.PUBLISHED)) {
			        			if(!insertedCompList.contains(comp.getCompetitionID())) { //proverka dali ya imame veche zapisana pri nas
			        				// za zapis
			        				Query qcomp = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO jobs_subscription_competition(competition_id ,unitid,positionname,deadline_date,published_date ,batch_identification_number) VALUES(? ,? ,? ,? ,? ,?)");
			        				qcomp.setParameter(1, comp.getCompetitionID());
			        				qcomp.setParameter(2, comp.getUnitID());
			        				qcomp.setParameter(3, comp.getPositionName());
			        				qcomp.setParameter(4, new TypedParameterValue(StandardBasicTypes.DATE, DateUtils.toDate(comp.getSubmissionInfo().getDeadlineDate())));				
			        				qcomp.setParameter(5, new TypedParameterValue(StandardBasicTypes.DATE, DateUtils.toDate(comp.getUpdatedOn())));	
			        				qcomp.setParameter(6, comp.getBatchIdentificationNumber());
			        				qcomp.executeUpdate();
			        			}
			        			
			        			//-----------------------------------------------------------------
			        			// zapis na vrazka potrebitel obqva
			        			for(Object[]idUsr: val) {
			        						        						        					
			        					Query qcheck =JPA.getUtil().getEntityManager().createNativeQuery("SELECT 1 FROM jobs_subscription_results WHERE user_id =? AND advertisement_id=? AND adv_type =?");
			        					qcheck.setParameter(1, idUsr[0]);
			        					qcheck.setParameter(2, comp.getCompetitionID());
			        					qcheck.setParameter(3, 1L);
			        					List<Object> check = qcheck.getResultList();
			        					
			        					if(check.isEmpty()) {
			        						
			        						//ako e обявил позиция да проверяваме обявата и по позиция
			        						Long pos = asLong(idUsr[5]);
			        						if(pos==null  || comp.getPositionID() == pos.longValue()) {
				        						
							        			Query qsubscomp = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO jobs_subscription_results(user_id ,advertisement_id ,adv_type) VALUES (? ,? ,?)");
							        			qsubscomp.setParameter(1, idUsr[0]);
							        			qsubscomp.setParameter(2, comp.getCompetitionID());
							        			qsubscomp.setParameter(3, 1L);
							        			
							        			qsubscomp.executeUpdate();
							        			
							        			if(idUsr[1]!=null) {
							        				
								        			Query pquery = JPA.getUtil().getEntityManager().createNativeQuery("select mail_subject, mail_body from jobs_notification_patterns where id = 5");
								        			Object[] obj = (Object[]) pquery.getResultList().get(0);
		
								        			if (obj != null) {
								        				String zveno="";
								        				if(comp.getUnitID()!=null) {
								        					Query zquery = JPA.getUtil().getEntityManager().createNativeQuery("select str_name from jobs_administration_structures where id= "+comp.getUnitID());
								        					
															List<String> rez = zquery.getResultList();
								        					if(rez!=null && !rez.isEmpty()) {
								        						zveno = rez.get(0);
								        					}
								        				}
								        				
								        				String subject = obj[0] != null ? obj[0].toString() : "";
								        				String cont = obj[1] != null ? obj[1].toString() : "";
								        				
								        				StringBuffer dataCompet = new StringBuffer();
														
								        				dataCompet.append("<hr> Длъжност / Звено / Административна структура:");
								        				dataCompet.append("<p style='padding-left:10px; margin:0px'><b> "+comp.getPositionName()+"</b><br/>");
								        				dataCompet.append("<b> "+zveno+"</b><br/>");
								        				dataCompet.append("<b> "+idUsr[4]+"</b></p>") ;
								        				
								        				dataCompet.append("<br/>Адрес:") ;
								        				dataCompet.append("<p style='padding-left:10px; margin:0px'><b> "+comp.getSubmissionInfo().getAddressText()+" </b> </p> ");
								        				dataCompet.append("<br/>Минимален размер на основната заплата:") ;
								        				dataCompet.append("<p style='padding-left:10px; margin:0px'><b> "+comp.getSalary()+" лв. </b></p>");
														
														SimpleDateFormat sdtf = new SimpleDateFormat("dd-MM-yyyy");
														
														dataCompet.append("<br/>Срок за подаване на документи: ");	
														dataCompet.append("<p style='padding-left:10px; margin:0px'><b>"+sdtf.format(DateUtils.toDate(comp.getSubmissionInfo().getDeadlineDate()))+" г.</b> </p><hr> ");	
														
														cont = "<div style='background-color:#fff;height:90px;font-size:20px;color:#000; border-bottom:2 px solid #4a6484;'><img src='http://www.gov.bg/images/frontend/logo.png' width='100' align='center' valign ='middle'/>Портал за работа в държавната администрация</div>"
																+ "<div style='font-size:16px;color:#35475d'><br/> " + cont + "</div>";
														
														cont = cont.replace("$COMPET$", dataCompet.toString());
								        				cont = cont.replace("$LINK$", siteComp+""+comp.getCompetitionID());
								        				
								        				Query queryMail = JPA.getUtil().getEntityManager().createNativeQuery(" insert into jobs_mail (id, id_user, administration, email, name_lice, subject, msg, date_mail, user_reg, code_object, status, status_date) "
																  + " values (nextval('seq_mail'), ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11)");
								        				
														queryMail.setParameter(1, idUsr[0]);
														queryMail.setParameter(2, idUsr[3]); //administration
														queryMail.setParameter(3, idUsr[1]);
														queryMail.setParameter(4, idUsr[2]); //nameLice
														queryMail.setParameter(5, subject);
														queryMail.setParameter(6, cont);
														queryMail.setParameter(7, new Date());
														queryMail.setParameter(8, -1L);
														queryMail.setParameter(9, -1L);
														queryMail.setParameter(10, Constants.CODE_ZNACHENIE_STATUS_MAIL_NOT_SEND);
														queryMail.setParameter(11, new Date());
		
														queryMail.executeUpdate();
								        			}
								        			
							        			}
		
							        		}
			        					}
			        				
			        			}
			        		}
			        	 }
			        	 
			        	 JPA.getUtil().commit();
		        	 } catch (Exception e) {
	 			       JPA.getUtil().rollback();
	 			       jobResult.setStatus(SysConstants.JOB_STATUS_WARN);
	 				   LOGGER.error("Възникна грешка при записване на обяви за работа от уеб услугата!!!", e);
	 				   throw  new JobExecutionException(e);
	 			     }  finally {
		 				JPA.getUtil().closeConnection();
		 			 }
		        	
		        }
		        
		        LOGGER.info("MyCompetitionsJob - End");
		        
		    } catch (Exception e) {
	        	jobResult.setStatus(SysConstants.JOB_STATUS_WARN);
				LOGGER.error("Възникна грешка при връзка с уеб услигата!!!", e);
				throw new JobExecutionException(e);
	        }
			
		}
		
//		System.out.println("end ");
		
		
		
	}

}
