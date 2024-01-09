package indexbg.pjobs.db.dao;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.pagination.SelectMetadata;

import indexbg.pjobs.db.AdmOrgs;

public class AdmOrgsDAO extends TrackableDAO<AdmOrgs> {
	

	static final Logger LOGGER = LoggerFactory.getLogger(AdmOrgsDAO.class);
	
	public AdmOrgsDAO (Long userId){
		
		super(userId);		
	}
	
	public SelectMetadata findOrgs(String eik,String imena) {
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		String sql = " SELECT ID as A0," // 
				+ " EIK as A1,"
				+ " ORG_NAME as A2,"
				+ " EMAIL as A3,"
				+ " PHONE as A4,"
				+ " WEBSITE as A5, "
				+ " ADDRESS as A6, " 
				+ " EKATTE as A7 "
				+ " FROM ADM_ORGS  ";
		 		
		String 	whereClause =  " ";
		String and=" ";
		String where = "where";
		
		if(eik != null && !"".equals(eik)){
			whereClause += where + " EIK LIKE :eik ";
			params.put("eik", "%" + eik.trim() + "%"); 
			and = " AND ";
			where = "";
		}
		
		if(imena != null && !"".equals(imena)){
			whereClause += where +  and + " ORG_NAME LIKE :imena ";			
			params.put("imena", "%" + imena.trim() + "%"); 	 
		}
		
		SelectMetadata smd = new SelectMetadata();
		
		smd.setSql(sql+whereClause);
		smd.setSqlCount("SELECT COUNT(distinct ID) as counter FROM ADM_ORGS "+ whereClause);
		smd.setSqlParameters(params);
			
		System.out.println(smd.getSql());
		System.out.println(smd.getSqlCount());
		
		return smd;
	}
}
