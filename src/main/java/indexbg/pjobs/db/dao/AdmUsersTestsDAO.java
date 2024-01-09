package indexbg.pjobs.db.dao;


import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.DialectConstructor;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.UsersTests;
import indexbg.pjobs.system.Constants;


public class AdmUsersTestsDAO extends TrackableDAO<UsersTests> {
	
	static final Logger LOGGER = LoggerFactory.getLogger(AdmUsersTestsDAO.class);

	public AdmUsersTestsDAO(Long userId) {
		super(userId);
	}
	
	@SuppressWarnings("unchecked")
	public List<UsersTests> getUserTestsByLevel(Long level,Long userId) throws DbErrorException {
						
		try {
			Query query = JPA.getUtil().getEntityManager().createQuery("FROM UsersTests  WHERE userId = :userId and testLevel = :level order by testDate desc");
				//	+ " and ( (testDate is null or testDate >= :dateFrom) or (testDate >= :dateFrom3 and status = :status) ) "
				
			query.setParameter("userId", userId);
			query.setParameter("level", level);

//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.YEAR, -1);
//			query.setParameter("dateFrom",cal.getTime());
//			cal.add(Calendar.YEAR, -3);
//			query.setParameter("dateFrom3",cal.getTime());			
//			query.setParameter("status",Constants.CODE_ZNACH_TEST_RESULTS_IZDARJAL);
			
			List<UsersTests> tests = query.getResultList();
	
			return tests;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на тестове за потребител ", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<UsersTests> getUserTests(Long userId) throws DbErrorException {
						
		try {
			Query query = JPA.getUtil().getEntityManager().createQuery("FROM UsersTests  WHERE userId = :userId ");

			query.setParameter("userId", userId);
		
			List<UsersTests> tests = query.getResultList();
	
			return tests;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на тестове за потребител ", e);
		}
	}
	
	
	public SelectMetadata findResultsForTests(Date dateFrom, Date dateTo, Long level ,Long administration,Long status ,Long location){
		
		if(administration==null) {
			//throw obje
		}
		
		String vendorName = JPA.getUtil().getDbVendorName();
		
		SelectMetadata smd = new SelectMetadata();
		
		String sql =" SELECT ut.id A0, ut.user_id A1, u.names A2, ut.test_level A3, ut.test_location A4 ,ut.test_date A5 ,ut.test_time A6, ut.test_group_id A7, ut.status A8 ,ut.deactiv_reason A9 ,uadd.pin A10 ,uadd.egn A11 ";
		String from =" FROM jobs_users_tests ut JOIN adm_users u ON ut.user_id = u.user_id  JOIN jobs_users_add uadd ON u.user_id = uadd.user_id";
		String where =" WHERE ut.administration ="+administration;
	

		if(dateFrom !=null) {
			
			where +=" and ut.test_date >= " + DialectConstructor.convertDateToSQLString(vendorName, dateFrom);			
		}
		
		if(dateTo !=null) {
			where += " and ut.test_date <= " + DialectConstructor.convertDateToSQLString(vendorName, dateTo);			
		}
		
		if(level != null) {
			where +=" and ut.test_level = " +level;
		}
		
		if(status != null) {
			where +=" and ut.status = " +status;
		}
		
		if(location !=null) {
			where +=" and ut.test_location = " +location;
		}
		
		smd.setSql(sql+from + where);
		
		smd.setSqlCount(" SELECT COUNT(ut.id) as counter FROM jobs_users_tests ut " + where);
		
		return smd;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getDataResultTest(Long userTest) throws DbErrorException{
		
		
		
		
		try {
			String sql = "SELECT module ,area ,points ,max_points FROM jobs_users_results WHERE jobs_users_tests_id =?1 order by module ,area";
			
			Query query = createNativeQuery(sql); 
			
			query.setParameter(1, userTest);			
			List<Object[]> rez = query.getResultList();
			
			Collections.rotate(rez, rez.size()-1);
			
			return rez;
						
			
		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при зареждане на резултати от проведен тест с ид: "+userTest, e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getHalls() throws DbErrorException{
			
		try {
			String sql = "select town from tst_hall where id in (Select max(id) FROM tst_hall group by town)";
			
			Query query = createNativeQuery(sql); 		
			return query.getResultList();
						
			
		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при зареждане на зали ", e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Long countCandidates(Date dateFrom, Date dateTo, Long status ) throws DbErrorException {			
			
			String sql = "select count  ( distinct user_id) from jobs_users_tests " ;
			String where = " where ";
			String and = "";
			
			if(dateFrom!=null) {				
				sql += where + and + " test_date >= :dateFrom " ;
				where = "";
				and = " and ";
			}
			
			if(dateTo!=null) {
				sql += where + and + " test_date <= :dateTo " ;	
				where = "";
				and = " and ";
			}
			
			if(status !=null) {
				sql += where + and + "status = :status" ;
			}
			 	
			try {
				EntityManager em = JPA.getUtil().getEntityManager();
				Query query = em.createNativeQuery(sql);
				
				if(dateFrom != null) {
					query.setParameter("dateFrom", DateUtils.startDate(dateFrom));
				}
				
				if(dateTo != null) {
					query.setParameter("dateTo", DateUtils.endDate(dateTo));
				}
				
				if(status != null) {
					query.setParameter("status", status);
				}		
				
				List<Long> rezult = query.getResultList();
				
				if (rezult != null && !rezult.isEmpty()) {				
					return SearchUtils.asLong(rezult.get(0));				
				} else {
					return null;
				}
				 
			} catch (Exception e) {
				throw new DbErrorException("Грешка при извличане на брой кандидати по вид", e);
			}
		}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> countPassedByLevel(Date dateFrom, Date dateTo) throws DbErrorException {			
			
			String sql = " select test_Level, count( id) from jobs_users_tests where status =  " +Constants.CODE_ZNACH_TEST_RESULTS_IZDARJAL;

			if (dateFrom != null) {
				sql += " and test_date >= :dateFrom ";

			}

			if (dateTo != null) {
				sql += " and test_date <= :dateTo ";

			}
			
			sql += " group by test_level ";	
			 	
			try {
				
				EntityManager em = JPA.getUtil().getEntityManager();
				Query query = em.createNativeQuery(sql);
				
				if(dateFrom != null) {
					query.setParameter("dateFrom", DateUtils.startDate(dateFrom));
				}
				
				if(dateTo != null) {
					query.setParameter("dateTo", DateUtils.endDate(dateTo));
				}		
				
				List<Object[]> rezult = query.getResultList();
						
				return rezult;	
				 
			} catch (Exception e) {
				throw new DbErrorException("Грешка при извличане на кандидати,издържали тест", e);
			}
		}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> countAverageResByLevel(Date dateFrom, Date dateTo) throws DbErrorException {			
			
			String sql = " select t.test_Level, sum( r.points)/count(distinct r.jobs_users_tests_id) from jobs_users_tests t join jobs_users_results r on(t.id=r.jobs_users_tests_id)";
			String where = " where ";
			String and = "";
			
			if(dateFrom!=null) {
				
				sql += where + and + " t.test_date >= :dateFrom " ;		
				where = "";
				and = " and ";
			}
			
			if(dateTo!=null) {
				sql += where + and + " t.test_date <= :dateTo ";		
				where = "";
				and = " and ";
			}
			
			sql += " group by t.test_level ";
			 	
			try {
				EntityManager em = JPA.getUtil().getEntityManager();
				Query query = em.createNativeQuery(sql);
		
				if(dateFrom != null) {
					query.setParameter("dateFrom", DateUtils.startDate(dateFrom));
				}
				
				if(dateTo != null) {
					query.setParameter("dateTo", DateUtils.endDate(dateTo));
				}
				
				List<Object[]> rezult = query.getResultList();
						
				return rezult;	
				 
			} catch (Exception e) {
				throw new DbErrorException("Грешка при извличане на среден резултат по видове тестове", e);
			}
		}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getAvgResByArea(Date dateFrom, Date dateTo, Long module) throws DbErrorException {			
			
			String sql = " select sum (r.points)/count(r.jobs_users_tests_id),r.area,t.test_level from jobs_users_results r join  jobs_users_tests t on (r.jobs_users_tests_id = t.id)  where r.module = :module " ;
					
			if (dateFrom != null) {
				sql += "and t.test_date >= :dateFrom ";
			}

			if (dateTo != null) {
				sql += " and t.test_date <= :dateTo ";

			}
			
			sql += " group by r.area,t.test_level order by t.test_level, r.area ";

			try {
				
				EntityManager em = JPA.getUtil().getEntityManager();
				Query query = em.createNativeQuery(sql);
				
				if(dateFrom != null) {
					query.setParameter("dateFrom", DateUtils.startDate(dateFrom));
				}
				
				if(dateTo != null) {
					query.setParameter("dateTo", DateUtils.endDate(dateTo));
				}
				
				if(module != null) {
					query.setParameter("module", module);
				}		
				
				List<Object[]> rezult = query.getResultList();
						
				return rezult;	
				 
			} catch (Exception e) {
				throw new DbErrorException("Грешка при извличане на среден резултат по модули и области", e);
			}
		}
	
}
