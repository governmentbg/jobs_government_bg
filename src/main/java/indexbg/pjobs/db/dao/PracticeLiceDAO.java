package indexbg.pjobs.db.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.SelectMetadata;

import indexbg.pjobs.db.PracticeLice;
import indexbg.pjobs.system.Constants;

public class PracticeLiceDAO extends TrackableDAO<PracticeLice> {	

	static final Logger LOGGER = LoggerFactory.getLogger(PracticeLiceDAO.class);
	
	public PracticeLiceDAO (Long userId){
		
		super(userId);		
	}
	
	/**
	 * Намира обявите,за които е кандидатствал външен потребител(кандидат) Ако се подаде practiceID, се проверява дали е кандидатствал за конкретна обява
	 * @throws DbErrorException 
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findPracticesByUserAplly(Long idUser, Long idCamp, Long practiceID, boolean active, boolean profile) throws DbErrorException {
		
		try {
//			String query = "select p.id a0, "
//					+ " p.practice_title a1, " 
//					+ " p.practice_date_from a2,  "
//					+ " p.practice_date_to a3, "
//					+ " l.user_id a4,"
//					+ " p.confirm_date_from a5,"
//					+ " p.confirm_date_to a6, "
//					+ " l.classification a7, "
//					+ " l.status a8, "
//					+ " l.id a9, "
//					+ " CASE WHEN CURRENT_DATE >= p.confirm_date_from AND CURRENT_DATE <= p.confirm_date_to  THEN TRUE ELSE FALSE END a10, "
//					+ " CASE WHEN CURRENT_DATE >= p.confirm_date_from2 AND CURRENT_DATE <= p.confirm_date_to2  THEN TRUE ELSE FALSE END a11, "
//					+ " ranked2 a12 "
//					+ " from jobs_practice p join jobs_practice_lice l on (p.id = l.practice_id) "
//					+ " where 1=1 ";
			
			
			String query = "select p.id a0, "
					+ " p.practice_title a1, " 
					+ " p.practice_date_from a2,  "
					+ " p.practice_date_to a3, "
					+ " l.user_id a4,"
					+ " p.confirm_date_from a5,"
					+ " p.confirm_date_to a6, "
					+ " l.classification a7, "
					+ " l.status a8, "
					+ " l.id a9, "
					+ " CASE WHEN CURRENT_DATE >= p.confirm_date_from AND CURRENT_DATE <= p.confirm_date_to  THEN TRUE ELSE FALSE END a10, "
					+ " CASE WHEN CURRENT_DATE >= p.confirm_date_from2 AND CURRENT_DATE <= p.confirm_date_to2  THEN TRUE ELSE FALSE END a11, "
					+ " ranked2 a12, "
					+ " c.type a13 ";
					
					if(profile) {
						query += " , (Select distinct 3 From jobs_practice jp join jobs_practice_lice jpl  on jp.id = jpl.practice_id join jobs_campaign jc on jp.campaign_id = jc.id and jc.type =1 "
								+ "		where jc.id = p.campaign_id and jpl.user_id = l.user_id and jpl.status = 3 ) a14 ";
					}
					
					query +=    " , p.confirm_date_from2 a15 "
							  + " , p.confirm_date_to2 a16 "
							  + " , TO_CHAR(l.interview_date,'dd.MM.yyyy HH24 :MI') a17"
							  + " , l.interview_place a18";
					query += " from jobs_practice p join jobs_practice_lice l on p.id = l.practice_id join jobs_campaign c on p.campaign_id = c.id  where 1=1 ";
			
			if(idUser!=null) {
				query += " and l.user_id =:userID ";
			}
			
			if(practiceID!=null) {
				query += " and p.id = :practiceID ";
				
			}
			
			if(idCamp!=null) {
				query += " and p.campaign_id = :idCamp ";
				
			}
			//да не е по статус,а по година на кампанията
			if(active) {
				query += " and c.status = " + Constants.CODE_ZNACHENIE_CAMPAIGN_STATUS_ACTIVE +" and c.year >=  EXTRACT(YEAR FROM current_date) ";
			}
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			
			if(idUser!=null) {
			    q.setParameter("userID", idUser);	
			}
			
			if(idCamp!=null) {
				q.setParameter("idCamp", idCamp);
			}
			
			if(practiceID!=null) {
				q.setParameter("practiceID", practiceID);
			}
			
			List<Object[]> result = q.getResultList();		
			return result;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на обяви за стаж на кандидат!", e);
		}
		
	}
	
	/**
	 * Списък с кандидати за обява за стаж в ДА и данните за всеки кандидат
	 * 
	 * @param idPractice
	 * @return
	 * @throws DbErrorException
	 */
	public List<Object[]> findCandidatesForPracticeByIdP(Long idPractice) throws DbErrorException {
		
		try {
			
			String query = " SELECT pl.id, "
					+ " pl.user_id, " 
					+ " u.names unames, "
					+ " u.email emai, "
					+ " pl.status, "
					+ " pl.interview_date, "
					+ " pl.interview_place, "
					+ " pl.classification, "
					+ " pl.practice_result, "
					+ " pl.accept_date, "
					+ " pl.ranked2, "
					+ " ua.pin "
					
					+ " FROM jobs_practice_lice pl "					
					+ " LEFT OUTER JOIN adm_users u ON u.user_id = pl.user_id "
					+ " LEFT OUTER JOIN jobs_users_add ua ON ua.user_id = u.user_id "
					
					+ " WHERE pl.practice_id =:idPractice "
					
					+ " ORDER BY pl.interview_date ";
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			q.setParameter("idPractice", idPractice);
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();		
			return result;
		
		} catch (Exception e) {
			throw new DbErrorException("Грешка при зареждане данни за обяви за стаж в ДА!", e);
		}
		
	}
	
	/**
	 * Проверява колко кандидати по дадена обява за стаж са класиране на първо място
	 * 
	 * @param docId
	 * @return
	 * @throws DbErrorException
	 */
	public int findCandidatesRankedInFirstCount(Long idPractice) throws DbErrorException {
		
		try {
			Number count = (Number) createNativeQuery(" SELECT count (*) cnt FROM jobs_practice_lice WHERE practice_id = ?1 and classification = 1 ")
				.setParameter(1, idPractice).getSingleResult();
			
			return count.intValue();
		
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на кандидати, класирани на първо място!", e);
		}
	}	
	
	/**
	 * Търсене на класирани кандидати за стаж по зададени параметри 
	 * 
	 * @param idCamp
	 * @param numPractice
	 * @param tipReport
	 * @return
	 */
	public SelectMetadata findRankingCandidates(Long adminStruc, Long idCamp, Long numPractice, Long tipReport) {		
		
		String sql = " SELECT p.id AS A0," 
				+ " pl.id AS A1,"
				+ " pl.user_id AS A2,"
				+ " ua.pin AS A3,"				
				+ " pl.classification AS A4,"
				+ " p.education_area AS A5,"
				+ " p.administration AS A6,"
				+ " p.unit AS A7, " 
				+ " p.region AS A8, "
				+ " p.municipality AS A9,"
				+ " p.town AS A10 ";
		
		String from = " FROM jobs_practice p ";
		from += " JOIN jobs_practice_lice pl ON pl.practice_id = p.id ";
		from += " LEFT OUTER JOIN  jobs_users_add ua ON  ua.user_id = pl.user_id ";
		
		StringBuilder where = new StringBuilder(" where 1=1 ");
		
		Map<String, Object> params = new HashMap<>();
		
		if(adminStruc != null){
			where.append(" and p.administration = :adminStruc");  
			params.put("adminStruc", adminStruc);
		}
		
		if(idCamp != null) {
			where.append(" and p.campaign_id = :idCamp");
			params.put("idCamp", idCamp);
		}
		
		if(numPractice != null) {
			where.append(" and p.id = :numPractice");
			params.put("numPractice", numPractice);
		}
		
		if (tipReport != null) {
			
			if (tipReport == 1) {
				where.append(" and pl.classification is not null ");
			}

			if (tipReport == 2) {
				where.append(" and pl.classification > 1 ");
				where.append(" and pl.ranked2 = 1 ");
			}
			
			if (tipReport == 3) { 
				where.append(" and pl.classification is not null ");
				where.append(" and pl.status = " + Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED);				
			}			
		}
				
		SelectMetadata smd = new SelectMetadata();
		
		smd.setSqlCount(" SELECT COUNT(*) as counter " + from + where);
		smd.setSql(sql + from + where);
		smd.setSqlParameters(params);
		
		return smd;
		
	}
	
	/**
	 * Справката за незаети позиции 
	 * 
	 * @param idCamp
	 * @param numPractice
	 * @param tipReport
	 * @return
	 */
	public List<Object[]> findVacantPositions(Long adminStruc, Long idCamp, Long numPractice) throws DbErrorException {
		
		try {
			
			StringBuilder sql  = new StringBuilder(" SELECT aa.* FROM ( ");
			
			sql.append(" SELECT p.id, ");
			sql.append(" p.num, ");					
			sql.append(" ( p.num - ( select count (jpl.id) ");
			sql.append(" FROM jobs_practice_lice jpl " );
			sql.append(" WHERE jpl.practice_id = p.id and jpl.accept_date is not null) ) broi_nezaeti_mesta, ");
			sql.append(" p.practice_title ");
			sql.append(" FROM jobs_practice p ");
			
			if(adminStruc != null || idCamp != null || numPractice != null){
			
				if(idCamp != null) {
					sql.append("  WHERE p.campaign_id = :idCamp");
				}
				
				if(numPractice != null) {
					sql.append(" and p.id = :numPractice");
				}
				
				if(adminStruc != null){
					sql.append(" and p.administration = :adminStruc");
				}
			
			}
			
			sql.append(" ORDER BY p.id  ");
			
			sql.append(" ) aa  ");
			sql.append(" WHERE broi_nezaeti_mesta > 0 ");					
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			
			if(idCamp != null) {
				q.setParameter("idCamp", idCamp);
			}
			
			if(numPractice != null) {
				q.setParameter("numPractice", numPractice);
			}
			
			if(adminStruc != null){
				q.setParameter("adminStruc", adminStruc);
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();
			
			return result;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на незаети позиции !", e);
		}
		
	}
	
	/**
	 * Справка за успешно завършили, незавършили стаж и немаркирани от администратор 
	 * 
	 * @param idCamp
	 * @param typeReport 1-завършили, 2 - незавършили, null- неотбелязани
	 * @return
	 */
	public List<Object[]> findPracticeResult(Long idCamp, Long typeReport, Long year) {		
		
		
		StringBuilder sql = new StringBuilder(" select l.id AS A0," 
				+ " pl.id AS A1,"
				+ " pl.user_id AS A2,"
				+ " l.pin AS A3,"	
				+ "  CASE  WHEN l.id is null then 'Изтрит потребител' ELSE "
				+ " l.name || ' ' || l.surname || '' || l.family END AS A4, "
				+ " p.administration AS A5, " 
				+ " p.unit AS A6,"
				+ " p.town AS A7, "
				+ " p.id A8, "
				+ " p.education_area A9,"
				+ " us.university AS A10 "		
				+ " from jobs_users_add l right join jobs_practice_lice pl on (l.user_id = pl.user_id)  join jobs_practice p on (p.id = pl.practice_id) "
				+ "LEFT JOIN jobs_users_student us on( us.user_id = pl.user_id) where pl.practice_result  ");
		
		
		if(typeReport.equals(Constants.CODE_ZNACHENIE_DA) || typeReport.equals(Constants.CODE_ZNACHENIE_NE)) {
			sql.append(" = :typeReport ");
			
		}else if(typeReport.equals(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED)) {
			sql.append("is null and pl.status = :plStatus ");
		}
		
		sql.append(" and p.practice_date_to < current_date ");
		
		if (idCamp != null) {
		    sql.append(" and p.campaign_id = :idCamp ");
		}else if(year!=null) {
			sql.append("and EXTRACT(YEAR FROM p.date_from) =:year "); 
		}
		
		Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());		
		
		if(typeReport.equals(Constants.CODE_ZNACHENIE_DA) || typeReport.equals(Constants.CODE_ZNACHENIE_NE)) {
			q.setParameter("typeReport", typeReport);
			
		}else if(typeReport.equals(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED)) {		
			q.setParameter("plStatus", Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED);	
		}
					
		if (idCamp != null) {			
			q.setParameter("idCamp", idCamp);
		}else if(year !=null) {
			q.setParameter("year", year);
		}
	
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.getResultList();		
		return result;
	}
	
	/**
	 * Справка за 
	 * 
	 * @param idCamp
	 * @return
	 * @throws DbErrorException
	 */
	public List<Object[]> reportStrudentUniversity(Long idCamp, Long year) throws DbErrorException {
		
		try {
			String query = "select count(pl.user_id), "					
					+ " us.university "													
					+ " from jobs_users_student us "
					+ " right join jobs_practice_lice pl on( us.user_id = pl.user_id) join jobs_practice p on (pl.practice_id = p.id) ";
							
			if(idCamp!=null) {
				query += " where p.campaign_id = :idCamp" ;				
			}else if(year!=null) {
				query += " where EXTRACT(YEAR FROM p.date_from) =:year " ;	
			}
			
			query += " group by us.university ";
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			
			if(idCamp!=null) {
				q.setParameter("idCamp", idCamp);
			}else if(year!=null) {
				q.setParameter("year", year);
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();		
			return result;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на брой кандидати по университети!", e);
		}
		
	}	
	
	/**
	 * Справка за брой студенти по направления
	 * 
	 * @param idCamp
	 * @return
	 * @throws DbErrorException
	 */
	public List<Object[]> reportStrudentEducationArea(Long idCamp, Long year) throws DbErrorException {
		
		try {
			String query = "select p.education_area, "					
					+ " count(pl.user_id) "													
					+ " from jobs_practice p  left join  jobs_practice_lice pl on  (pl.practice_id = p.id) ";
				
							
			if(idCamp!=null) {
				query += " where p.campaign_id = :idCamp"  ;				
			}else if(year!=null) {
				query += " where EXTRACT(YEAR FROM p.date_from) =:year " ;	
			}
			query += " group by p.education_area ";
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			
			if(idCamp!=null) {
				q.setParameter("idCamp", idCamp);
			}else if(year!=null) {
				q.setParameter("year", year);
			}
								
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();		
			return result;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на брой студенти по направления!", e);
		}
		
	}
	
	/**
	 * Намира всички стажове към кампания на кандидат
	 * 
	 * @param idCamp
	 * @return
	 * @throws DbErrorException
	 */
	public List<Object[]> findCandidateByIdCamp(Long idCamp, Long idCandidat) throws DbErrorException {
		
		try {
			String query = " select p.id, "
					+ " l.status "
					+ " from jobs_practice p "
					+ " join jobs_practice_lice l on (p.id = l.practice_id) "					
					+ " where 1=1 ";
			
			if(idCamp != null) {
				query += " and p.campaign_id = :idCamp ";				
			}
			
			if(idCandidat != null) {
				query += " and l.user_id = :idCandidat ";				
			}
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			
			if(idCamp != null) {
				q.setParameter("idCamp", idCamp);
			}
			
			if(idCandidat != null) {
				q.setParameter("idCandidat", idCandidat);
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();		
			return result;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на кандидат за стаж към кампания!", e);
		}
		
	}
	
	/**
	 * Справка за общ брой кандидатствания по обявени позиции
	 * 
	 * @param idCamp
	 * @param year
	 * @return
	 */
	public List<Object[]> findTotalNumerPractPossitions(Long idCamp, Long year) {		
		
		
		StringBuilder sql = new StringBuilder(" SELECT "
				+ "    p.id, "
				+ "    p.administration, "
				+ "    p.unit, "
				+ "    p.region, "
				+ "    p.municipality, "
				+ "    p.town, "
				+ "    p.education_area, "
				+ "    p.num, "
				+ "    count(pl.id) as kandidati, "
				+ "    count(pl.interview_date)  as nasr_interview, "
				+ "    count (pl.accept_date) as begin_practice, "
				+ "    SUM( CASE WHEN pl.practice_result = :da THEN 1 ELSE 0 END ) as zavarshili, "
				+ "    SUM( CASE WHEN pl.practice_result = :ne THEN 1 ELSE 0 END ) as nezavarshili, "
				+ "    SUM( CASE WHEN pl.practice_result is null AND accept_date is not null THEN 1 ELSE 0 END ) as neotbeliazani "
				+ " FROM "
				+ "    jobs_practice p "
				+ " LEFT JOIN "
				+ "    jobs_practice_lice pl "
				+ "     ON   (p.id = pl.practice_id) "
				+ " WHERE p.practice_date_to < current_date ");
				

		
		if(idCamp!=null) {
			sql.append("and p.campaign_id =:idCamp "); 
		} else if(year !=null) {
			sql.append("and EXTRACT(YEAR FROM p.date_from) =:year "); 
		} 
		
		sql.append(" group by   p.id  ");
		Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());		
		
		q.setParameter("da", Constants.CODE_ZNACHENIE_DA);
		q.setParameter("ne", Constants.CODE_ZNACHENIE_NE);			
		if (idCamp != null) {			
			q.setParameter("idCamp", idCamp);
		}else if(year !=null) {
			q.setParameter("year", year);
		}
			
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.getResultList();		
		return result;
	}
}