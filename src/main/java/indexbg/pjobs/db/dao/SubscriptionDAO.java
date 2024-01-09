package indexbg.pjobs.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.AbstractDAO;
import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;

import indexbg.pjobs.db.Subscription;

public class SubscriptionDAO extends AbstractDAO<Subscription> {
	
	static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionDAO.class);

	public List<Subscription> findByUserId(Long userId) throws DbErrorException {
		
		String query = "select s from Subscription s where s.userId = :userId ";
		
		try {
			EntityManager em = JPA.getUtil().getEntityManager();
			TypedQuery<Subscription> q = em.createQuery(query, Subscription.class);
			q.setParameter("userId", userId);
			return q.getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извикаване на данни за абонамент по номер на потребител", e);
		}
		
	}
	
	public LazyDataModelSQL2Array getCompetitionsByUserId(Long userId) throws DbErrorException {
		String sql = "select distinct comp.positionname, comp.published_date, comp.deadline_date, jas.str_name, adm.subject_name, comp.competition_id ";
		String counter = "select count (distinct(comp.positionname, comp.deadline_date, comp.published_date, jas.str_name, comp.batch_identification_number)) as counter ";
		
		String query = "from jobs_subscription_results res ";
		query += "inner join jobs_subscription_competition comp ";
		query += "on res.advertisement_id = comp.competition_id ";
		query += "inner join jobs_administration adm ";
		query += "on comp.batch_identification_number = adm.nomer_register ";
		query += " left join  jobs_administration_structures jas on  comp.unitId = jas.id ";
		query += "where res.user_id = " + userId + " and adv_type = 1";
		
		sql += query;
		counter += query;
		
		SelectMetadata smd = new SelectMetadata();
		smd.setSql(sql);
		smd.setSqlCount(counter);
		
		try {
			return new LazyDataModelSQL2Array(smd, null);
		} catch(DbErrorException e) {
			throw new DbErrorException("Грешка при извикаване на данни за конкурси за работа по номер на потребител", e);
		}
	}
	
	/** Зареждане на ид и мейлите на потребителите, които са се абонирани за обяви към дадена администрация
	 * 
	 * @param administration
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> subscUsers (Long administration) throws DbErrorException {
		
		try {
			
			Query query = createNativeQuery(" SELECT u.user_id, u.email, u.names FROM jobs_subscription s JOIN adm_users u ON s.user_id = u.user_id WHERE s.administration = ? and s.type = 2 "); 
			
			query.setParameter(1, administration);	
			
			return query.getResultList();			
			
		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при зареждане на абонирани потребители!", e);
		}
		
	}
	
	public LazyDataModelSQL2Array getAdvertSubsByUserId(Long userId) throws DbErrorException {
		
		String selectSql = "select  jad.id ,jj.job_shortname , adm.subject_name ,jas.str_name,jad.date_from ,jad.date_to ";
		String selectCountSql = "select count(1) ";		
		
		String sql = " from jobs_subscription_results res " + 
				" inner join jobs_advertisement jad on res.advertisement_id = jad.id " + 
				" left join jobs_administration adm on jad.administration = adm.id " + 
				" left join  jobs_administration_structures jas on  jad.unit = jas.id " + 
				" left join  jobs_jobs jj on  jad.position = jj.id " + 
				" where res.user_id = "+userId+" and res.adv_type = 2  ";
		
		SelectMetadata smd = new SelectMetadata();
		smd.setSql(selectSql+sql);
		smd.setSqlCount(selectCountSql+sql);
		
		try {
			return new LazyDataModelSQL2Array(smd, null);
		} catch(DbErrorException e) {
			throw new DbErrorException("Грешка при извикаване на данни за конкурси за работа по номер на потребител", e);
		}
	}
}
