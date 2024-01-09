package indexbg.pjobs.db.dao;


import static com.indexbg.system.utils.DialectConstructor.trim;
import static com.indexbg.system.utils.SearchUtils.trimToNULL_Upper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.indexbg.system.db.AbstractDAO;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.PersistentEntity;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.SelectMetadata;

public class QuartzDAO extends AbstractDAO<PersistentEntity>{

public QuartzDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuartzDAO(Long userId) {
		super(userId);
		// TODO Auto-generated constructor stub
	}

	public QuartzDAO(String unitName, Long userId) {
		super(unitName, userId);
		// TODO Auto-generated constructor stub
	}

	public QuartzDAO(String unitName) {
		super(unitName);
		// TODO Auto-generated constructor stub
	}

	public String findDescription (String jobName,String jobGroup) throws DbErrorException{
		
		try {
			String queryString = 
					"Select jd.DESCRIPTION "
					+ "from QRTZ_JOB_DETAILS as jd"
					+ " where jd.JOB_NAME = ? "
					+ "and jd.JOB_GROUP = ?";
			
			Query query = createNativeQuery(queryString);
			
			query.setParameter(1, jobName);
			query.setParameter(2, jobGroup);
			
			@SuppressWarnings("unchecked")
			List<String> result = query.getResultList();
			
			if (result.isEmpty()) {
				return null; // няма
			}
			return result.get(0);
		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при търсене на описание на системен процес .", e);
		}
	}	

	public List<String> getAllSchedulerNames () throws DbErrorException{
	
		try {
			String queryString = "Select distinct jd.SCHED_NAME from QRTZ_JOB_DETAILS as jd";
			Query query = createNativeQuery(queryString);
			@SuppressWarnings("unchecked")
			List<String> result = query.getResultList();
			
			if (result.isEmpty()){
				return null; //няма резултат
			}
			return result;
			
		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при търсене на всички quartz schedulers.", e);
		}
	}
	
	/**
	 * На база на аргументите подготвя SelectMetadata за търсене на job history
	 *
	 * @param resourceName
	 * @param datasetId
	 * @param execDateFrom
	 * @param execDateTo
	 * @param resourceKey
	 * @param status
	 * @param publishRequestFrom
	 * @param publishRequestTo
	 * @return
	 */
	public SelectMetadata createFindScheduleMetadata(String jobName, String triggerName, Date startTime, Date endTIme, Integer status) {
		
		String dialect = JPA.getUtil(getUnitName()).getDbVendorName();
		if (dialect == null) {
			dialect = "SQLServer";
		}

		Map<String, Object> params = new HashMap<>();

		StringBuilder select = new StringBuilder();
		StringBuilder from = new StringBuilder();
		StringBuilder where = new StringBuilder();

		boolean conditions = false; // има ли условия, по които да се търси

		select.append(" select jh.ID, jh.JOB_NAME, jh.TRIG_NAME, jh.START_TIME, jh.END_TIME, jh.STATUS, jh.COMMENTS, jh.EXCEPTIONS");
		from.append(" from job_history jh");
		where.append(" ");
		
		String jn = trimToNULL_Upper(jobName);
		if (jobName != null && !jobName.trim().equals("")) {
			where.append(conditions ? " and " : " where ");
			where.append(" upper(" + trim(dialect, "jh.JOB_NAME") + ") like '%" + jn + "%' ");
			conditions = true;
		}
		String tn = trimToNULL_Upper(triggerName);
		if (triggerName != null && !triggerName.trim().equals("")) {
			where.append(conditions ? " and " : " where ");
			where.append(" upper(" + trim(dialect, "jh.TRIG_NAME") + ") like '%" + tn + "%' ");
			conditions = true;
		}

		if (startTime != null) {
			where.append(conditions ? " and " : " where ");
			where.append(" jh.START_TIME >= :startTime ");
			params.put("startTime", startTime);
			conditions = true;
		}

		if (endTIme != null) {
			where.append(conditions ? " and " : " where ");
			where.append(" jh.END_TIME <= :endTIme ");
			params.put("endTIme", endTIme);
			conditions = true;
		}


		if (status != null && status != 0) {
			where.append(conditions ? " and " : " where ");
			where.append(" jh.STATUS = :statusParam ");
			params.put("statusParam", status);
			conditions = true;
		}


		SelectMetadata smd = new SelectMetadata();

		smd.setSql(select.toString() + from.toString() + where.toString());
		smd.setSqlCount(" select count(distinct jh.ID) from job_history jh " + where.toString());
		smd.setSqlParameters(params);

		return smd;
	} 
}
