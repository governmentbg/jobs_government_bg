package indexbg.pjobs.quartz;

import static com.indexbg.system.utils.SearchUtils.asLong;
import static com.indexbg.system.utils.SearchUtils.asString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
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
import com.indexbg.system.exceptions.InvalidParameterException;
import com.indexbg.system.mail.Mailer3;
import com.indexbg.system.mail.Mailer3.Content;
import com.indexbg.system.quartz.BaseJobResult;
import com.indexbg.system.utils.StringUtils;

@DisallowConcurrentExecution
public class MyMailsJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyMailsJob.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(SysConstants.JOB_STATUS_OK);
		
		try {
			
			String confFile = "indexbg.mail3.properties";
			Properties mailProps = Mailer3.loadProps(confFile);
			Mailer3 mailer3 = new Mailer3();
			
			String sqlMails = "SELECT ID, EMAIL ,SUBJECT ,MSG ,TRY_TO_SEND FROM JOBS_MAIL WHERE STATUS = "+ Constants.CODE_ZNACHENIE_STATUS_MAIL_NOT_SEND +" ORDER BY ID ";
			Query qMails = JPA.getUtil().getEntityManager().createNativeQuery(sqlMails);
			List<Object[]>  mailsForSending = (List<Object[]>)qMails.getResultList();
			
			try {
				
				JPA.getUtil().begin();
				
				for(Object[] rowMail: mailsForSending) {
		        	
					String error = "";
					
					Long id  = asLong(rowMail[0]);
		        	String email = asString(rowMail[1]);
		        	String subject = asString(rowMail[2]);
		        	String cont  = asString(rowMail[3]);
		        	Long count  = asLong(rowMail[4]);
		        	
					try {
						ArrayList<String> mail = new ArrayList<String>(Arrays.asList(email.split(";")));
						mailer3.sent(Content.HTML, mailProps, mailProps.getProperty("user.name"), mailProps.getProperty("user.password"), mailProps.getProperty("mail.from"), " ", mail, subject, cont, new ArrayList<DataSource>());
					} catch (AddressException e) {
						LOGGER.error("Error sendMail AddressException", e);
						error = StringUtils.stack2string(e);
					} catch (InvalidParameterException e) {
						LOGGER.error("Error sendMail InvalidParameterException", e);
						error = StringUtils.stack2string(e);
					} catch (MessagingException e) {
						LOGGER.error("Error sendMail MessagingException", e);
						error = StringUtils.stack2string(e);
					} catch (Exception e) {
						LOGGER.error("Error sendMail Exception", e);
						error = StringUtils.stack2string(e);
					} finally {
						
						Long status = Constants.CODE_ZNACHENIE_STATUS_MAIL_SEND;
							
						if(!error.isEmpty()) { //ako ima greshka proveryavame kolko pati se e opitalo da go izprati za da znaem kakav status da slojim
							if(count>3) {
								status  = Constants.CODE_ZNACHENIE_STATUS_MAIL_ERROR; 
							} else {
								count++;
								status = Constants.CODE_ZNACHENIE_STATUS_MAIL_NOT_SEND;
							}
						}
						
						Query queryUpdate = JPA.getUtil().getEntityManager().createNativeQuery ("update JOBS_MAIL set status =?, status_date =?, error =?,try_to_send =? where id =?");
						queryUpdate.setParameter(1, status ); // status
						queryUpdate.setParameter(2, new Date() );
						queryUpdate.setParameter(3, error ); 
						queryUpdate.setParameter(4,  count);
						queryUpdate.setParameter(5,  id); // id
						queryUpdate.executeUpdate();
						
						JPA.getUtil().flush();
							
					}
		        	
		        }
				
				JPA.getUtil().commit();
			
			} catch (Exception e) {
				LOGGER.error("Error saving mail Object", e);
				JPA.getUtil().rollback();
			}
	        
	        
		} catch (Exception e) {
			jobResult.setStatus(SysConstants.JOB_STATUS_WARN);
			LOGGER.error("Възникна грешка при извличане на съобщения за изпращене!!!", e);
			throw new JobExecutionException(e);
        }  finally {
			JPA.getUtil().closeConnection();
		}
		
		
	}

}
