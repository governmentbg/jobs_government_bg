package indexbg.pjobs.db.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;

import indexbg.pjobs.db.Bursary;

public class BursaryDAO extends TrackableDAO<Bursary> {	

	static final Logger LOGGER = LoggerFactory.getLogger(BursaryDAO.class);
	
	public BursaryDAO (Long userId){
		
		super(userId);		
	}
	
	/** Търсене на стипендиантски програми по зададени параметри
	 * @param eik
	 * @param name
	 * @return
	 */
	public SelectMetadata findPrograms(String spec,Long admStr,Long status,boolean isExtList, Date dateFrom, Date dateTo) {		
		
		String sql = " SELECT id as A0," 
				+ " administration as A1,"
				+ " subject as A2,"
				+ " bursary as A3, " 
				+ " num as A4, "
				+ " date_from as A5, "
				+ " date_to as A6 "
				+ " FROM jobs_bursary ";
		 		
		String 	whereClause =  " ";
		String and =" ";
		String where = "where";
		
		Map<String, Object> params = new HashMap<>();		
		
		if(spec != null && !"".equals(spec)){
			whereClause += where +  and + " upper(subject) LIKE :spec ";	
			and = " AND ";
			where = "";
			params.put("spec", "%" + spec.trim().toUpperCase() + "%" );			
		}
		
		if(status!=null){
			whereClause += where +  and + " status = :status ";	
			and = " AND ";
			where = "";
			params.put("status", status);
		}
		
		if(admStr != null){
			whereClause += where +  and + " administration = :admStr";
			and = " AND ";
			where = "";
			params.put("admStr", admStr);
		}
		
		if(dateFrom !=null) {
			whereClause += where + and + " date_from >= :dateFrom ";	
			and = " AND ";
			where = "";		
			params.put("dateFrom", DateUtils.startDate(dateFrom));
		}
		
		if(dateTo !=null) {
			whereClause += where + and + " date_to <= :dateTo ";	
			and = " AND ";
			where = "";			
			params.put("dateTo", DateUtils.endDate(dateTo));
		}		
		
		if(isExtList) {
			whereClause += where +  and + " (date_to is null or date_to >= :dateTo )" ;	
			params.put("dateTo", DateUtils.endDate(new Date()));
		}
		
		SelectMetadata smd = new SelectMetadata();

		smd.setSql(sql + whereClause);
		
		smd.setSqlCount("SELECT COUNT(distinct ID) as counter FROM jobs_bursary " + whereClause);
		
		smd.setSqlParameters(params);
		
		return smd;
	}
	
}