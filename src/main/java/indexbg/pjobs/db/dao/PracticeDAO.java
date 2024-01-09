package indexbg.pjobs.db.dao;

import java.util.Date;
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
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.Practice;
import indexbg.pjobs.system.Constants;

public class PracticeDAO extends TrackableDAO<Practice> {	

	static final Logger LOGGER = LoggerFactory.getLogger(PracticeDAO.class);
	
	public PracticeDAO (Long userId){
		
		super(userId);		
	}
	
	/** Търсене на обяви за стаж по зададени параметри
	 * @param porNom
	 * @param idCamp
 	 * @param admStruct
	 * @param oblast
	 * @param obshtina
	 * @param nasMiasto
	 * @param area
	 * @param title
	 * @param active - само активните обяви
	 * @return
	 */
	public SelectMetadata findPractices(Long porNom,Long idCamp, Long admStruct, Long oblast, Long obshtina, Long nasMiasto, Long area, String title, Long unit,boolean active, Date dateFrom, Date dateTo) {
		
		String sql = " SELECT id as A0," 
				+ " practice_title as A1,"
				+ " administration as A2,"				
				+ " town as A3,"
				+ " num as A4,"
				+ " region as A5, "
				+ " municipality A6,"
				+ " date_from A7, "
				+ " date_to A8"
				+ " FROM jobs_practice ";
		 		
		String whereClause = " ";
		String and = " ";
		String where = " WHERE "; 
		
		Map<String, Object> params = new HashMap<>();
		
		if(porNom!=null) {
			whereClause += where +  and + " id = :porNom ";	
			and = " AND ";
			where = "";
			params.put("porNom", porNom);
		}
		
		if(idCamp!=null) {
			whereClause += where +  and + " campaign_id = :idCamp ";	
			and = " AND ";
			where = "";
			params.put("idCamp", idCamp);
		}
		
		if(admStruct!=null) {
			whereClause += where +  and + " administration = :admStruct ";	
			and = " AND ";
			where = "";
			params.put("admStruct", admStruct);
		}
		
		if(oblast!=null) {
			whereClause += where +  and + " region = :oblast ";	
			and = " AND ";
			where = "";
			params.put("oblast", oblast);
		}
		
		if(obshtina!=null) {
			whereClause += where +  and + " municipality = :obshtina ";	
			and = " AND ";
			where = "";
			params.put("obshtina", obshtina);
		}
		
		if(nasMiasto!=null) {
			whereClause += where +  and + " town = :nasMiasto ";	
			and = " AND ";
			where = "";
			params.put("nasMiasto", nasMiasto);
		}
		
		if(area!=null) {
			whereClause += where +  and + " education_area = :area ";	
			and = " AND ";
			where = "";
			params.put("area", area);
		}
		
		if(title != null && !"".equals(title)){
			whereClause += where +  and + " upper(practice_title) LIKE :title ";	
			and = " AND ";
			where = "";
			params.put("title", "%" + title.trim().toUpperCase() + "%" );				
		}
		
//		if(status!=null){
//			whereClause += where +  and + " status = " + status ;	
//			and = " AND ";
//			where = "";
//		}
		
		if(unit!=null) {
			whereClause += where +  and + " unit = :unit ";	
			and = " AND ";
			where = "";
			params.put("unit", unit);
		}
		
//		String vendorName = JPA.getUtil().getDbVendorName();
		
//		if(dateStatus !=null) {
//			whereClause += where + and + " status_date = " + DialectConstructor.convertDateToSQLString(vendorName, dateStatus);	
//			and = " AND ";
//			where = "";
//		}
		
		if(active) {
			whereClause += where +  and + " date_from <= CURRENT_DATE AND date_to >= CURRENT_DATE  " ;		
			and = " AND ";
			where = "";
		}
		
		if(dateFrom !=null && dateTo !=null) {
			whereClause += where + and + " date_from >= :dateFrom AND date_to <= :dateTo";	
			and = " AND ";
			where = "";		
			params.put("dateFrom", DateUtils.startDate(dateFrom));
			params.put("dateTo", DateUtils.endDate(dateTo));
		}else if(dateFrom !=null) {
			whereClause += where + and + " date_from = :dateFrom ";	
			and = " AND ";
			where = "";		
			params.put("dateFrom", DateUtils.startDate(dateFrom));
		}else if(dateTo !=null) {
			whereClause += where + and + " date_to = :dateTo ";	
			and = " AND ";
			where = "";			
			params.put("dateTo", DateUtils.endDate(dateTo));
		}		
					
		SelectMetadata smd = new SelectMetadata();

		smd.setSql(sql + whereClause);
				
		smd.setSqlCount("SELECT COUNT(distinct ID) as counter FROM jobs_practice "+ whereClause);	
		
		smd.setSqlParameters(params);
		
		return smd;
	}
	
	/**
	 * Дали има въведени стажове към кампания 
	 * @throws DbErrorException 
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findPracticesByCamp(Long idCamp) throws DbErrorException {
		
		try {
			String query = " select id "
					+ " from jobs_practice "
					+ " where campaign_id = " + idCamp;
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
		
	
			
			return q.getResultList();
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на стажове", e);
		}
		
	}
	
	/**
	 * Връща последните стажове
	 * @param count брой резултати, които да върне
	 * @return Списък с резултатите с колони:
	 * <ul>
	 * 	<li>[0] id</li>
	 * 	<li>[1] practice_title</li>
	 * 	<li>[2] administration</li>
	 * 	<li>[3] region</li>
	 *  <li>[4] miunicipality</li>
	 * 	<li>[5] num</li>
	 * 	<li>[6] date_from</li>
	 * 	<li>[7] date_to</li>
	 * </ul>
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findLatestPractices(int count) throws DbErrorException {
		try {
			String sql = "select id, practice_title, administration, region, municipality, num, date_from, date_to"
					+ " from jobs_practice "
					
					+ " where date_from <= CURRENT_DATE AND date_to >= CURRENT_DATE "
					+ " order by id desc";
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql);
			
			q.setMaxResults(count);
			
			return q.getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на стажове", e);
		}
	}
	
	
	
	
	public Long reportResumo(Long campId , Long year, String colum ) throws DbErrorException {
		
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT COUNT(*) FROM (SELECT 1 FROM jobs_practice jp ");
			
			
			if(campId!=null) {
				sql.append(" WHERE jp.campaign_id =:campId "); 
			} else if(year !=null) {
				sql.append(" WHERE EXTRACT(YEAR FROM jp.date_from) =:year "); 
			} 
			sql.append(" GROUP BY "+colum+") dataSpr "); 
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			if(campId!=null) {
				q.setParameter("campId",campId);
			} else if(year !=null) {
				q.setParameter("year",year);
			} 
			q.getSingleResult();
			
			return SearchUtils.asLong(q.getSingleResult());
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изчисление на брой: "+colum, e);
		}
		
	}
	
	public Long reportResumoCountPracticle(Long campId , Long year) throws DbErrorException {
		
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT COUNT(*) FROM jobs_practice jp ");
			
			
			if(campId!=null) {
				sql.append(" WHERE jp.campaign_id =:campId "); 
			} else if(year !=null) {
				sql.append(" WHERE EXTRACT(YEAR FROM jp.date_from) =:year "); 
			} 
		
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			if(campId!=null) {
				q.setParameter("campId",campId);
			} else if(year !=null) {
				q.setParameter("year",year);
			} 
			q.getSingleResult();
			
			return SearchUtils.asLong(q.getSingleResult());
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изчисление на брой стажове ", e);
		}
		
	}
	
	public Long reportResumoSumPositions(Long campId , Long year) throws DbErrorException {
		
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT SUM(NUM) FROM jobs_practice jp ");
			
			
			if(campId!=null) {
				sql.append(" WHERE jp.campaign_id =:campId "); 
			} else if(year !=null) {
				sql.append(" WHERE EXTRACT(YEAR FROM jp.date_from) =:year "); 
			} 
		
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			if(campId!=null) {
				q.setParameter("campId",campId);
			} else if(year !=null) {
				q.setParameter("year",year);
			} 
			q.getSingleResult();
			
			return SearchUtils.asLong(q.getSingleResult());
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изчисление на брой стажове ", e);
		}
		
	}
	
	public Long reportResumoSumCandidatos(Long campId , Long year) throws DbErrorException {
		
		try {
			StringBuilder sql  = new StringBuilder("SELECT COUNT(*) FROM (SELECT 1 FROM jobs_practice JP JOIN jobs_practice_lice JPL ON JP.id = JPL.practice_id ");
			
			
			if(campId!=null) {
				sql.append(" WHERE jp.campaign_id =:campId "); 
			} else if(year !=null) {
				sql.append(" WHERE EXTRACT(YEAR FROM jp.date_from) =:year "); 
			} 
			sql.append(" GROUP BY JPL.user_id ) dataSpr "); 
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			if(campId!=null) {
				q.setParameter("campId",campId);
			} else if(year !=null) {
				q.setParameter("year",year);
			} 
			q.getSingleResult();
			
			return SearchUtils.asLong(q.getSingleResult());
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изчисление на брой стажове ", e);
		}
		
	}
	
	public Long reportResumoSumAllCandidatos(Long campId , Long year) throws DbErrorException {
		
		try {
			
			StringBuilder sql  = new StringBuilder(" SELECT COUNT(1) FROM jobs_practice JP JOIN jobs_practice_lice JPL ON JP.id = JPL.practice_id ");
			
			
			if(campId!=null) {
				sql.append(" WHERE jp.campaign_id =:campId "); 
			} else if(year !=null) {
				sql.append(" WHERE EXTRACT(YEAR FROM jp.date_from) =:year "); 
			} 
		
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			if(campId!=null) {
				q.setParameter("campId",campId);
			} else if(year !=null) {
				q.setParameter("year",year);
			} 
			q.getSingleResult();
			
			return SearchUtils.asLong(q.getSingleResult());
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изчисление на брой стажове ", e);
		}
		
	}
	
	public Long reportResumoSumEntrevista(Long campId , Long year) throws DbErrorException {
		
		try {
			
			StringBuilder sql  = new StringBuilder(" SELECT COUNT(1) FROM jobs_practice JP JOIN jobs_practice_lice JPL ON JP.id = JPL.practice_id ");
			
			sql.append(" AND JPL.interview_date IS NOT NULL ");
			
			if(campId!=null) {
				sql.append(" WHERE jp.campaign_id =:campId "); 
			} else if(year !=null) {
				sql.append(" WHERE EXTRACT(YEAR FROM jp.date_from) =:year "); 
			} 
		
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			if(campId!=null) {
				q.setParameter("campId",campId);
			} else if(year !=null) {
				q.setParameter("year",year);
			} 
			q.getSingleResult();
			
			return SearchUtils.asLong(q.getSingleResult());
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изчисление на брой стажове ", e);
		}
		
	}
	
	
	public Long reportResumoEducation(Long campId , Long year, String colum ) throws DbErrorException {
		
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT COUNT(*) FROM (SELECT 1 FROM jobs_practice jp ");
			
			
			sql.append(" JOIN jobs_practice_lice JPL ON JP.id = JPL.practice_id   "); 
			sql.append(" JOIN jobs_users_student JUS ON JUS.user_id = JPL.user_id "); 
		
			
			if(campId!=null) {
				sql.append(" WHERE jp.campaign_id =:campId "); 
			} else if(year !=null) {
				sql.append(" WHERE EXTRACT(YEAR FROM jp.date_from) =:year "); 
			} 
			sql.append(" GROUP BY "+colum+") dataSpr "); 
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			if(campId!=null) {
				q.setParameter("campId",campId);
			} else if(year !=null) {
				q.setParameter("year",year);
			} 
			
			return SearchUtils.asLong(q.getSingleResult());
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изчисление на брой: "+colum, e);
		}
		
	}
	
	
	public HashMap<Long, Long> reportResumoPracticleResult(Long campId , Long year) throws DbErrorException {

		try {
			
			StringBuilder sql  = new StringBuilder("SELECT count (1) ,JPL.practice_result FROM jobs_practice JP ");
			
			
			sql.append(" JOIN jobs_practice_lice JPL ON JP.id = JPL.practice_id AND JPL.status =:stat  "); 
			
			
			if(campId!=null) {
				sql.append(" WHERE jp.campaign_id =:campId  AND JP.practice_date_to < CURRENT_DATE "); 
			} else if(year !=null) {
				sql.append(" WHERE EXTRACT(YEAR FROM jp.date_from) =:year  AND JP.practice_date_to < CURRENT_DATE "); 
			} 
			sql.append(" GROUP BY  JPL.practice_result "); 
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			if(campId!=null) {
				q.setParameter("campId",campId);
			} else if(year !=null) {
				q.setParameter("year",year);
			}
			q.setParameter("stat", Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED);
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();
			
			HashMap<Long, Long> practicleResult = new HashMap<Long, Long>(); 
			for(Object[] res :result) {
				Long key = 0L;
				if(res[1] !=null) {
					key = SearchUtils.asLong(res[1]);
				}
				practicleResult.put(key, SearchUtils.asLong(res[0]));
			}
			
			return practicleResult;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изчисление на брой издържали /не издържали стажа ", e);
		}
	}
	
	/**
	 * Справка за брой обявени позиции по направления
	 * 
	 * @param idCamp
	 * @return
	 * @throws DbErrorException
	 */
	public List<Object[]> reportPossitionsByArea(Long idCamp, Long year) throws DbErrorException {
		
		try {
			String query = "select education_area, count (num) "					
															
					+ " from jobs_practice ";
				
							
			if(idCamp!=null) {
				query += " where campaign_id = :idCamp "  ;				
			}else if(year!=null) {
				query += " where EXTRACT(YEAR FROM date_from) =:year " ;	
			}
			query += " group by education_area ";
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			
			if(idCamp!=null) {
				q.setParameter("idCamp",idCamp);			
			}else if(year !=null) {
				q.setParameter("year",year);
			} 
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();		
			return result;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на брой обявени позиции по направления!", e);
		}
		
	}
	

	public SelectMetadata repordResumoA(Long idCamp, Long year) {
		
		
		String sqlSelect = " SELECT au.user_id A0,  au.names A1, au.org_code A2, au.org_zveno A3, au.email A4";
		String sqlFrom   = " FROM adm_users au  ";
		String whereClause = " ";
	
		if(idCamp!=null) {
			whereClause = "  WHERE au.user_id in ( SELECT jp.user_reg   FROM jobs_practice jp   WHERE jp.campaign_id ="+idCamp+" GROUP BY jp.user_reg ) ";
		} else if(year!=null) {
			whereClause = "  WHERE au.user_id in ( SELECT jp.user_reg   FROM jobs_practice jp   WHERE EXTRACT(YEAR FROM jp.date_from) ="+year+" GROUP BY jp.user_reg ) ";
		}
		
					
		SelectMetadata smd = new SelectMetadata();

		smd.setSql(sqlSelect+sqlFrom+whereClause);
				
		smd.setSqlCount("SELECT COUNT(au.user_id) as counter "+sqlFrom+ whereClause);	
		
		return smd;
	}
	
	public SelectMetadata repordResumoV(Long idCamp, Long year) {
		
		
		String sqlSelect = " SELECT  au.user_id A0, jua.pin A1,  au.names A2,  jus.university A3, jus.education_area A4 ";
		String sqlFrom   = " FROM adm_users au JOIN jobs_users_student jus ON jus.user_id = au.user_id JOIN jobs_users_add jua ON jua.user_id  = au.user_id  ";
		String whereClause = " ";
	
		if(idCamp!=null) {
			whereClause = " WHERE au.user_id in ( SELECT JPL.user_id FROM jobs_practice JP JOIN jobs_practice_lice JPL ON JP.id = JPL.practice_id   WHERE jp.campaign_id ="+idCamp+" GROUP BY JPL.user_id ) ";
		} else if(year!=null) {
			whereClause = " WHERE au.user_id in (  SELECT JPL.user_id FROM jobs_practice JP JOIN jobs_practice_lice JPL ON JP.id = JPL.practice_id  WHERE EXTRACT(YEAR FROM jp.date_from) ="+year+" GROUP BY JPL.user_id) ";
		}
		
					
		SelectMetadata smd = new SelectMetadata();

		smd.setSql(sqlSelect+sqlFrom+whereClause);
				
		smd.setSqlCount("SELECT COUNT(au.user_id) as counter "+sqlFrom+ whereClause);	
		
		return smd;
	}
	
	public  List<Object[]> findPracticesForRanking2(Date rankingDate) throws DbErrorException{
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT JP.id ,JP.num  ,(JP.num - AA.br) svob ,JP.campaign_id ,C.type camp_type ,JP.practice_title,JP.confirm_date_from2 ,JP.confirm_date_to2 ,JP.administration");
			sql.append(" FROM jobs_practice JP , "); 
			sql.append("  (SELECT JPL.practice_id , count(1) br"); 
			sql.append(" 	FROM jobs_practice PR JOIN jobs_practice_lice JPL "); 
			sql.append(" 	ON PR.id = JPL.practice_id AND JPL.classification =:class AND JPL.status =:stat "); 
			sql.append(" 	AND PR.ranking_date2 =:rankingDate "); 
			sql.append(" 	GROUP BY JPL.practice_id"); 
			sql.append("  	) AA "); 
			sql.append(" , jobs_campaign C ");
			sql.append(" WHERE JP.ID = AA.practice_id  "); 
			sql.append(" AND  C.id = JP.campaign_id "); 
			sql.append(" AND (JP.num - AA.br)>0  "); 
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			q.setParameter("stat",Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED);
			q.setParameter("class",1);
			q.setParameter("rankingDate",DateUtils.startDate(rankingDate));
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();
			
			return result;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на обяви за второ класиране findPracticleForRanking2", e);
		}
	}
	
	public  List<Object[]> findPracticesForRanking2ForTestsOnly(Long idPracticle) throws DbErrorException{
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT JP.id ,JP.num  ,(JP.num - AA.br) svob ,JP.campaign_id ,C.type camp_type ,JP.practice_title,JP.confirm_date_from2 ,JP.confirm_date_to2 ,JP.administration");
			sql.append(" FROM jobs_practice JP , "); 
			sql.append("  (SELECT JPL.practice_id , count(1) br"); 
			sql.append(" 	FROM jobs_practice PR JOIN jobs_practice_lice JPL "); 
			sql.append(" 	ON PR.id = JPL.practice_id AND JPL.classification =:class AND JPL.status =:stat "); 
			sql.append(" 	AND PR.id =:idPracticle "); 
			sql.append(" 	GROUP BY JPL.practice_id"); 
			sql.append("  	) AA "); 
			sql.append(" , jobs_campaign C ");
			sql.append(" WHERE JP.ID = AA.practice_id  "); 
			sql.append(" AND  C.id = JP.campaign_id "); 
			sql.append(" AND (JP.num - AA.br)>0  "); 
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			q.setParameter("stat",Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED);
			q.setParameter("class",1);
			q.setParameter("idPracticle",idPracticle);
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();
			
			return result;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на обяви за второ класиране findPracticleForRanking2", e);
		}
	}
	
	public  List<Object[]> findStudentsForRanking2(Long idCamp ,Long idPract,Long cmpType) throws DbErrorException{
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT JPL.user_id ,JPL.classification ,AU.email ,AU.names ,JPL.id ");
			sql.append("  FROM jobs_practice_lice JPL JOIN adm_users AU ON AU.user_id = JPL.user_id"); 
			sql.append("  WHERE JPL.practice_id =:idPract AND JPL.classification >:class "); 
			
			//ako e централизирана кампания ще търсим студентите да не са приели по друга обява стаж
			if(cmpType.longValue()==Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR) {
				sql.append(" AND  JPL.user_id not in ( ");
				sql.append(" SELECT PL.user_id FROM jobs_practice_lice PL  ");
				sql.append(" JOIN jobs_practice P ON P.id = PL.practice_id ");
				sql.append(" WHERE PL.status =:stat AND P.campaign_id=:idCamp ");
				sql.append(" ) ");
			}
			
			sql.append(" ORDER BY JPL.classification ");
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			q.setParameter("idPract",idPract);
			q.setParameter("class",1);
			if(cmpType.longValue()==Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR) {
				q.setParameter("stat",Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED);
				q.setParameter("idCamp",idCamp);
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();
			
			return result;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на обяви за второ класиране findPracticleForRanking2", e);
		}
	}
	
	/**
	 * Извежда списък с обяви на които е приключила датата за кандидатстване и не е изпращан имейл на администратор
	 * @param dateTo
	 * @return List<Object[]> practice[] {id, administration ,unit ,dateTo ,countStudents} 
	 * @throws DbErrorException 
	 */
	public  List<Object[]> findPracticesEndDateTo(Date dateTo) throws DbErrorException{
		
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT JP.ID ,JP.administration ,JP.unit ,JP.date_to ,(SELECT count(1) FROM jobs_practice_lice JPL WHERE JPL.practice_id = JP.id)");
			sql.append(" ,ADM.subject_name ,JAS.str_name ");
			sql.append(" FROM jobs_practice JP  LEFT JOIN jobs_administration ADM ON ADM.id = JP.administration "); 
			sql.append(" LEFT JOIN jobs_administration_structures JAS ON JAS.ID = JP.unit ");
			sql.append(" WHERE JP.date_to <=:dateTO "); 
			sql.append(" AND JP.mail_to_admin =:mta "); 
			sql.append(" ORDER BY JP.administration ,JP.unit "); 
			
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			q.setParameter("mta",Constants.CODE_ZNACHENIE_NE); //не е изпращан имел
		    q.setParameter("dateTO",DateUtils.startDate(dateTo));
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();
			
			return result;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на обяви на които е изтекъл срока за кандидатстване!", e);
		}
		
	}
	
	public SelectMetadata findReportO(Long idCamp, Long year, Long type) {
		
		String sqlSelect = " ";
		String sqlFrom   = " ";
		String whereClause = " ";
		String groupBy = " ";
		
		Map<String, Object> params = new HashMap<>();
		
		SelectMetadata smd = new SelectMetadata();
		
		if (type == Long.valueOf(4)) { 
			
			sqlSelect = " SELECT distinct jp.education_area A0, gettextatt(" + Constants.CODE_SYSCLASS_SUBJECT + ", jp.education_area, jp.date_from, " + Constants.CODE_DEFAULT_LANG +") A1 "; 
			sqlFrom   = " FROM jobs_practice jp  ";
			whereClause = " ";
		
			if(idCamp!=null) {
				whereClause = " WHERE jp.campaign_id =:idCamp ";
				params.put("idCamp", idCamp);
			
			} else if(year!=null) {
				whereClause = " WHERE EXTRACT(YEAR FROM jp.date_from) =:year ";
				params.put("year", year);				
			}
			
			groupBy = " GROUP BY jp.education_area, jp.date_from ";
			
			smd.setSql(sqlSelect + sqlFrom + whereClause + groupBy);
			
			smd.setSqlCount("SELECT COUNT(distinct jp.education_area) as counter "+ sqlFrom + whereClause);
			
			smd.setSqlParameters(params);
		
		} else if (type == Long.valueOf(5)) { 
			
			sqlSelect = " SELECT jp.region A0, eo.ime A1";
			sqlFrom   = " FROM jobs_practice jp  ";
			sqlFrom  += " LEFT JOIN public.ekatte_oblasti eo on eo.id = jp.region ";
			whereClause = " ";
		
			if(idCamp!=null) {
				whereClause = " WHERE jp.campaign_id =:idCamp ";
				params.put("idCamp", idCamp);
			
			} else if(year!=null) {
				whereClause = " WHERE EXTRACT(YEAR FROM jp.date_from) =:year ";
				params.put("year", year);				
			}
			
			groupBy = " GROUP BY jp.region, eo.ime  ";
			
			smd.setSql(sqlSelect + sqlFrom + whereClause + groupBy);
			
			smd.setSqlCount("SELECT COUNT(distinct jp.region) as counter "+ sqlFrom + whereClause);
			
			smd.setSqlParameters(params);
			
		} else if (type == Long.valueOf(6)) { 
			
			sqlSelect = " SELECT jp.municipality A0, eo.ime A1";
			sqlFrom   = " FROM jobs_practice jp  ";
			sqlFrom  += " LEFT JOIN public.ekatte_obstini eo on eo.id = jp.municipality ";
			whereClause = " ";
		
			if(idCamp!=null) {
				whereClause = " WHERE jp.campaign_id =:idCamp ";
				params.put("idCamp", idCamp);
			
			} else if(year!=null) {
				whereClause = " WHERE EXTRACT(YEAR FROM jp.date_from) =:year ";
				params.put("year", year);				
			}
			
			groupBy = " GROUP BY jp.municipality, eo.ime  ";
			
			smd.setSql(sqlSelect + sqlFrom + whereClause + groupBy);
			
			smd.setSqlCount("SELECT COUNT(distinct jp.municipality) as counter "+ sqlFrom + whereClause);
			
			smd.setSqlParameters(params);
			
		} else if (type == Long.valueOf(7)) { 
			
			sqlSelect = " SELECT jp.town A0, ea.ime A1";
			sqlFrom   = " FROM jobs_practice jp  ";
			sqlFrom  += " LEFT JOIN public.ekatte_att ea on ea.ekatte = jp.town ";
			whereClause = " ";
		
			if(idCamp!=null) {
				whereClause = " WHERE jp.campaign_id =:idCamp ";
				params.put("idCamp", idCamp);
			
			} else if(year!=null) {
				whereClause = " WHERE EXTRACT(YEAR FROM jp.date_from) =:year ";
				params.put("year", year);				
			}
			
			groupBy = " GROUP BY jp.town, ea.ime ";
			
			smd.setSql(sqlSelect + sqlFrom + whereClause + groupBy);
			
			smd.setSqlCount("SELECT COUNT(distinct jp.town) as counter "+ sqlFrom + whereClause);
			
			smd.setSqlParameters(params);
			
		}
		
		return smd;
	}
	
}