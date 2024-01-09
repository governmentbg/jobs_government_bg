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

import indexbg.pjobs.db.Advertisement;
import indexbg.pjobs.system.Constants;

public class AdvertisementDAO extends TrackableDAO<Advertisement> {	

	static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementDAO.class);
	
	public AdvertisementDAO (Long userId){
		
		super(userId);		
	}
	

	/** Изграждане на SQL за извличане на Обяви за мобилност по зададни критерии за търсене
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param adminStruc
	 * @param zveno
	 * @param nasMiasto
	 * @param dlajnost
	 * @param dlajNivo
	 * @param rang
	 * @param obrazovanie
	 * @return
	 */
	public SelectMetadata findAdvertisement(Date dateFrom, Date dateTo, Long adminStruc, Long zveno, Long oblast, Long obshtina, Long nasMiasto, Long dlajnost, Long profField, Long dlajNivo, Long rang, Long obrazovanie, boolean isView) {
		
		String sql = " SELECT a.id as A0, "
					+ " a.date_from as A1, " 
					+ " a.unit as A2, "
					+ " a.location_town as A3, "
					+ " jj.job_shortname as A4, " 
					+ " a.date_to as A5, "
					+ " a.administration AS A6, "
					+ " a.salary AS A7 "
					+ " FROM jobs_advertisement a left join jobs_jobs jj on a.position = jj.id";
			 		
		String whereClause = " ";
		String and = " ";
		String where = " WHERE "; 
		
		Map<String, Object> params = new HashMap<>();
		
		whereClause += where + "a.adv_type = " + Constants.CODE_ZNACHENIE_TYPE_ADVERTISEMENT_MOBILITY;
		
		if(adminStruc != null){
			and = " AND ";
			whereClause += and + " a.administration = :adminStruc ";
			params.put("adminStruc", adminStruc);
		}
		
		if(dateFrom !=null) {
			and = " AND ";
			whereClause += and + " a.date_from >= :dateFrom ";
			params.put("dateFrom", DateUtils.startDate(dateFrom));
		}
		
		if(dateTo !=null) {
			and = " AND ";
			whereClause += and + " a.date_to <= :dateTo ";
			params.put("dateTo", DateUtils.endDate(dateTo));
		}		
				
		if(zveno != null){
			and = " AND ";
			whereClause += and + " a.unit = :zveno ";	
			params.put("zveno", zveno);
		}
		
		if (oblast != null) {
			and = " AND ";
			whereClause += and + " a.location_region = :oblast ";
			params.put("oblast", oblast);
		}
		
		if (obshtina != null) {
			and = " AND ";
			whereClause += and + " a.location_region = :oblast " + and + " a.location_municipality = :obshtina ";
			params.put("oblast", oblast);
			params.put("obshtina", obshtina);
		}
		
		if(nasMiasto != null){
			and = " AND ";
			whereClause += and + " a.location_town = :nasMiasto ";	  
			params.put("nasMiasto", nasMiasto);
		}
		
		if(dlajnost != null){
			and = " AND ";
			whereClause += and + " a.position = :dlajnost ";
			params.put("dlajnost", dlajnost);
		}
		
		if (profField != null) {
			and = " AND ";
			whereClause += and + " a.professional_field =  :profField ";
			params.put("profField", profField);
		}
		
		if(dlajNivo != null){
			and = " AND ";
			whereClause += and + " a.level = :dlajNivo ";	
			params.put("dlajNivo", dlajNivo);
		}
		
		if(rang != null){
			and = " AND ";
			whereClause += and + " a.rank = :rang ";
			params.put("rang", rang);
		}
		
		if(obrazovanie != null){
			and = " AND ";
			whereClause += and + " a.education_degree = :obrazovanie";
			params.put("obrazovanie", obrazovanie);
		}
		
		if (isView) {
			
			and = " AND ";
			whereClause += and + " a.date_from <= :dateFrom ";
			params.put("dateFrom", DateUtils.startDate(new Date()));
			
			and = " AND ";
			whereClause += and + " a.date_to >= :dateTo ";
			params.put("dateTo", DateUtils.endDate(new Date()));
		}
		
		SelectMetadata smd = new SelectMetadata();
		
		smd.setSql(sql + whereClause);
		smd.setSqlCount(" SELECT COUNT(DISTINCT a.ID) as counter FROM jobs_advertisement a " + whereClause);
		smd.setSqlParameters(params);
		
		return smd;
	}
	
	/**
	 * Връща последните n на брой обяви за мобилност  
	 * @throws DbErrorException 
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findLatest(int n) throws DbErrorException {
		
		try {
			String query = "select a.id, a.unit, a.location_town, jj.job_shortname, a.date_to, a.administration, a.salary "
					+ "from jobs_advertisement a left join jobs_jobs jj on a.position = jj.id "
					+ "where a.date_to >= current_date "
					+ "order by a.date_to desc";
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			q.setMaxResults(n);
			return q.getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извикаване на данни за обява за мобилност", e);
		}
		
	}
	
}