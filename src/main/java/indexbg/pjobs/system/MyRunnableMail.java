package indexbg.pjobs.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.InvalidParameterException;
import com.indexbg.system.mail.Mailer3;
import com.indexbg.system.mail.Mailer3.Content;
import com.indexbg.system.utils.StringUtils;

import indexbg.pjobs.db.Mail;
import indexbg.pjobs.db.dao.MailDAO;

//Ne e izpolzwa
@Deprecated 
public class MyRunnableMail implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MyRunnableMail.class);
	
	private long codeShablon = 0;
	
	private ArrayList<String> mail;
	
	private String nameLice;	
	private String nivo;
	private String date;
	private String hour;
	private String adres;		
	private String link;
	private Long administration;
	private Long userReg;
	
	private Long idUser;
	
	public MyRunnableMail(long codeShablon, ArrayList<String> mail, Long idUser){
		
		this.codeShablon = codeShablon;
		this.mail = mail;		 
		this.idUser = idUser;
	}
	
	public MyRunnableMail(long codeShablon, ArrayList<String> mail, String nivo, String date, String hour, String adres, String nameLice, Long idUser, String link, Long administration, Long userReg){
		
		this.codeShablon = codeShablon;
		this.mail = mail;		 
		this.idUser = idUser;
		this.nivo = nivo;
		this.date = date;
		this.hour = hour;
		this.adres = adres;
		this.nameLice = nameLice;		
		this.link = link;
		this.administration = administration;
		this.userReg = userReg;
	}	
	 
	 /** Mail send
	 * 
	 * 
	 */
	private void sendMail() {
		
		//String registerDate = Base64Coder.encodeString(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()).toString());
		String confFile = "indexbg.mail3.properties";		
		
		String subject = "";
		String cont = "";
		String error = null;
		
		try {
			
			Query query = JPA.getUtil().getEntityManager().createNativeQuery ("select mail_subject, mail_body from jobs_notification_patterns where id = " + codeShablon);
			
			Object[] obj = (Object[]) query.getResultList().get(0);
			
			if(obj != null) {
				subject = obj[0] != null ? obj[0].toString() : "";
				cont =  obj[1] != null ? obj[1].toString() : "";
			}			
				
			if (nameLice != null) {
				cont = "Уважаема/и г-жо/г-н " + nameLice + ", "  + "\n" + "<br/><br/>" + cont;
			}
			
			if (nivo != null ) {
				 cont = cont.replace("$NIVO$", nivo);
			}
			
			if (date != null ) {
				 cont = cont.replace("$DATE$", date);
			}
			
			if (hour != null ) {
				 cont = cont.replace("$HOUR$", hour);
			}
			
			if (adres != null ) {
				 cont = cont.replace("$ADRES$", adres);
			}
			
			if (link != null) {				
				cont = cont.replace("$LINK$", link);
			}
			
			cont = "<div style='background-color:#fff;height:90px;font-size:20px;color:#000; border-bottom:2 px solid #4a6484;'><img src='http://www.gov.bg/images/frontend/logo.png' width='100' align='center' valign ='middle' />Портал за работа в държавната администрация</div>"
					+ "<div style='font-size:16px;color:#35475d'><br/> " + cont + "</div>";
			
			Properties mailProps = Mailer3.loadProps(confFile);
			Mailer3 mailer3 = new Mailer3();
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
			
			//the record that will handle successful/ unsuccessful sent email
			// the user email, email subject, email body, id_user, the date of send email, error string containg stacktrace , name lice ako e fizichesko lice
			
			String mailtxt = "";
			
			for(String s:mail ){
				if(mailtxt.length() > 0) mailtxt += ";";
				mailtxt += s;
			}
			
			try {
				
				Mail mail = new Mail(idUser, administration, mailtxt,  nameLice,  subject,  cont, new Date(),  error, userReg);
				
//				System.out.println("idUser " + this.idUser + " administration " + administration + " admin " +  admin + " mailtxt " + mailtxt + " nameLice " + nameLice + " subject " + subject 
//						+ " cont " + cont + " date "  + new Date() + " error " + error + " userReg " + userReg);
								
				JPA.getUtil().begin();
				new MailDAO(idUser).save(mail);
				JPA.getUtil().commit();
				
			} catch (DbErrorException e) {
				LOGGER.error("Error saving mail Object", e);
				JPA.getUtil().rollback();
			
			} catch (Exception e) {
				LOGGER.error("Error saving mail Object", e);
				JPA.getUtil().rollback();
			}
			
			JPA.getUtil().closeConnection();
		}
	 }
	
	 public void run(){ 
		 sendMail();
	 }
	
}
