package indexbg.pjobs.db.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.AbstractDAO;
import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.DialectConstructor;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.Mail;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.SystemData;

/**
 * @author dessy
 *
 */
public class MailDAO extends AbstractDAO<Mail> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailDAO.class);
	
	public MailDAO(Long idUser){
		
		super(idUser);		
	}
	
	
	/** Изграждане на SQL за извличане на мейли по ид на кандидат или по мейл
	 * 
	 * @param idUser
	 * @param mail
	 * @return
	 */
	public SelectMetadata findMailsByUser(Long idUser, String mail) {
		
		String sql = " SELECT id as A0, "
					+ " administration as A1, " 
					+ " name_lice as A2, "
					+ " subject as A3, "
					+ " date_mail as A4, "
					+ " msg as A5 "
					+ " FROM jobs_mail ";
			 		
		String whereClause = " ";
		String and = " ";
		String where = " WHERE "; 
		
		whereClause += where;
		
		if(idUser != null){
			whereClause += and + " id_user = " + idUser;	        
		}
				
		if(mail != null){
			and = " AND ";
			whereClause += and + " email LIKE " + " '%" + mail + "%'";
		}		
		
		SelectMetadata smd = new SelectMetadata();
		
		smd.setSql(sql + whereClause);
		smd.setSqlCount(" SELECT COUNT(DISTINCT ID) as counter FROM jobs_mail" + whereClause);			
		
		return smd;
	}
	
	public void saveMail(long codeShablon, ArrayList<String> mail, Long administration, Date date, String hour, String adres, String nameLice, Long idUser, String link, Long userReg, Long codeObject, SystemData sd, String adver, String nivo, String usernames, String titlePractice, Date dateOtPract, Date dateToPrac) throws DbErrorException {

		Date today = SearchUtils.asDate(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");		
		
		String subject = "";
		String cont = "";	

		try {

			Query query = createNativeQuery("select mail_subject, mail_body from jobs_notification_patterns where id = " + codeShablon);

			Object[] obj = (Object[]) query.getResultList().get(0);

			if (obj != null) {
				subject = obj[0] != null ? obj[0].toString() : "";
				cont = obj[1] != null ? obj[1].toString() : "";
			}

			if (nameLice != null) {
				cont = "Уважаема/и г-жо/г-н " + nameLice + ", " + "\n" + "<br/><br/>" + cont;
			}

			if (nivo != null ) {
				 cont = cont.replace("$NIVO$", nivo);
			}

			if (date != null) {
				cont = cont.replace("$DATE$", SearchUtils.asString(sdf.format(date)));
			}

			if (hour != null) {
				cont = cont.replace("$HOUR$", hour);
			}

			if (adres != null) {
				cont = cont.replace("$ADRES$", adres);
			}

			if (link != null) {
				cont = cont.replace("$LINK$", link);
			}
			
			if (adver != null) {
				cont = cont.replace("$ADVER$", adver);
			}
			
			if (usernames != null) {
				cont = cont.replace("$USERNAMES$", usernames);
			}
			
			if (titlePractice != null) {
				cont = cont.replace("$TITLE$", titlePractice);
			}
			
			if (dateOtPract != null) {
				cont = cont.replace("$DATA_OT$", SearchUtils.asString(sdf.format(dateOtPract)));
			}
			
			if (dateToPrac != null) {
				cont = cont.replace("$DATA_DO$", SearchUtils.asString(sdf.format(dateToPrac)));
			}
			
			cont = "<div style='background-color:#fff;height:90px;font-size:20px;color:#000; border-bottom:2 px solid #4a6484;'><img src='http://www.gov.bg/images/frontend/logo.png' width='100' align='center' valign ='middle' />Портал за работа в държавната администрация</div>"
					+ "<div style='font-size:16px;color:#35475d'><br/> " + cont + "</div>";
			
			for (String mailtxt : mail) {
				
				Query queryMail = createNativeQuery(" insert into jobs_mail (id, id_user, administration, email, name_lice, subject, msg, date_mail, user_reg, code_object, status, status_date) "
												  + " values (nextval('seq_mail'), ?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11)");
				queryMail.setParameter(1, idUser);
				queryMail.setParameter(2, administration);
				queryMail.setParameter(3, mailtxt);
				queryMail.setParameter(4, nameLice);
				queryMail.setParameter(5, subject);
				queryMail.setParameter(6, cont);
				
				if (date != null) {
					queryMail.setParameter(7, date);
				} else {
					queryMail.setParameter(7, today);
				}
				
				queryMail.setParameter(8, userReg);
				queryMail.setParameter(9, codeObject);
				queryMail.setParameter(10, Constants.CODE_ZNACHENIE_STATUS_MAIL_NOT_SEND);
				queryMail.setParameter(11, today);

				queryMail.executeUpdate();				
			}
		
		} catch (Exception e) {
			throw new DbErrorException("Error saveMail Exception!!!", e);
		} 
	}
	
	/** Изграждане на SQL за извличане на мейли по ид на кандидат или по мейл
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param idUser
	 * @param status
	 * @param mail
	 * @param nameLice
	 * @return
	 */
	public SelectMetadata findMailsByAdmin(Date dateFrom, Date dateTo, Long status, String mail, String nameLice, Long administration) {
		
		String sql = " SELECT id as A0, "				
					+ " email AS A1, "
					+ " name_lice AS A2, "
					+ " subject AS A3, "
					+ " date_mail AS A4, "
					+ " status AS A5, "
					+ " error AS A6,  "	
					+ " msg AS A7  "	
					+ " FROM jobs_mail ";
		
		StringBuilder whereClause = new  StringBuilder("");
		ArrayList<String> uslovia = new ArrayList<String>();
		
		String vendorName = JPA.getUtil().getDbVendorName();		
		
		if(dateFrom !=null) {
			uslovia.add(" status_date >= " + DialectConstructor.convertDateToSQLString(vendorName, DateUtils.startDate(dateFrom)));			
		}
		
		if(dateTo !=null) {
			uslovia.add(" status_date <= " + DialectConstructor.convertDateToSQLString(vendorName,  DateUtils.endDate(dateTo)));			
		}
		
		if(status != null){
			uslovia.add(" status = " + status);	        
		}
		
		if(administration!=null) {
			uslovia.add(" administration = " + administration);
		}
		
		if(mail != null && !mail.isEmpty()){
			uslovia.add(" email LIKE " + " '%" + mail + "%'");
		}
		
		if(nameLice != null && !nameLice.isEmpty()){
			uslovia.add(" name_lice LIKE " + " '%" + nameLice + "%'");
		}
		
		if (!uslovia.isEmpty()) {
			whereClause.append(" WHERE ");
			for (int i = 0; i < uslovia.size(); i++) {
				whereClause.append(uslovia.get(i));
				if (i != (uslovia.size() - 1)) {
					whereClause.append(" AND ");
				}
			}
		}
		
		SelectMetadata smd = new SelectMetadata();
		
		smd.setSql(sql + whereClause.toString());
		smd.setSqlCount(" SELECT COUNT(DISTINCT ID) as counter FROM jobs_mail" + whereClause.toString());			
		
		return smd;
	}

}
