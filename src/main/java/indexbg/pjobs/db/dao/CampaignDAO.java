package indexbg.pjobs.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DialectConstructor;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.Campaign;
import indexbg.pjobs.system.Constants;

public class CampaignDAO extends TrackableDAO<Campaign> {	

	static final Logger LOGGER = LoggerFactory.getLogger(CampaignDAO.class);
	
	public CampaignDAO (Long userId){
		
		super(userId);		
	}
	
	/** Търсене на кампании за стаж по зададени параметри
	 * @param eik
	 * @param name
	 * @return
	 */
	public SelectMetadata findCampaigns(String nameCamp, Long[] status,Date dateStatus, Date dateFrom, 
			Date dateTo, Date dateFirsRank, Date confDateFrom, Date confDateTo) {
		
		
		String sql = " SELECT id as A0," 
				+ " name as A1,"
				+ " status as A2,"
				+ " status_date as A3, " 
				+ " date_from as A4, "
				+ " date_to as A5, "
				+ " ranking_date as A6, "
				+ " confirm_date_from A7, "
				+ " confirm_date_to A8 "
				+ " FROM jobs_campaign ";
		 		
		String whereClause = " ";
		String and = " ";
		String where = " WHERE "; 
		
		
		if(nameCamp != null && !"".equals(nameCamp)){
			whereClause += where +  and + " upper(name) LIKE '%" + nameCamp.trim().toUpperCase() + "%'";	
			and = " AND ";
			where = "";
		}
		
		if(status!=null && status.length != 0){
			whereClause += where +  and + " status IN (";
			 for(int i=0;i<status.length; i++) {
				 whereClause += status[i];
				 if(status.length!=1 && i!=status.length-1) {
					 whereClause +=",";
				 }
			 }
			
			whereClause +=")";	
			and = " AND ";
			where = "";
		}
		
		
		String vendorName = JPA.getUtil().getDbVendorName();
		
		if(dateStatus !=null) {
			whereClause += where + and + " status_date = " + DialectConstructor.convertDateToSQLString(vendorName, dateStatus);	
			and = " AND ";
			where = "";
		}
		
		if(dateFrom !=null) {
			whereClause += where + and + " date_from >= " + DialectConstructor.convertDateToSQLString(vendorName, dateFrom);	
			and = " AND ";
			where = "";
		}
		
		if(dateTo !=null) {
			whereClause += where + and + " date_from <= " + DialectConstructor.convertDateToSQLString(vendorName, dateTo);	
			and = " AND ";
			where = "";
		}
		
		if(dateFirsRank !=null) {
			whereClause += where + and + " ranking_date >= " + DialectConstructor.convertDateToSQLString(vendorName, dateFirsRank);	
			and = " AND ";
			where = "";
		}
		
		if(confDateFrom !=null) {
			whereClause += where + and + " confirm_date_from >= " + DialectConstructor.convertDateToSQLString(vendorName, confDateFrom);	
			and = " AND ";
			where = "";
		}
		
		if(confDateTo !=null) {
			whereClause += where + and + " confirm_date_to <= " + DialectConstructor.convertDateToSQLString(vendorName, confDateTo);	
			and = " AND ";
			where = "";
		}		
		
		SelectMetadata smd = new SelectMetadata();

		smd.setSql(sql+whereClause);
		
		
		smd.setSqlCount("SELECT COUNT(distinct ID) as counter FROM jobs_campaign "+ whereClause);			
		return smd;
	}
	
	/**
	 * Търси кампаниите за стаж в ДА и форматира резултата в SelectItems 
	 * @param active true-активните false-всички
	 * @throws DbErrorException 
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> findActiveCampaigns(boolean active) throws DbErrorException {
		
		try {
			String query = " select id, name from jobs_campaign ";
			
					if(active) {
						query += " where ((structure_date_from <= current_date and structure_date_to >= current_date and type = :centr) or type = :decentr)"
							  + " and status = :status ";
					} else {
						query +=  " where status <> :statusAll ";
					}					
				
			query	+= " order by date_to desc " ;
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			
			if(active) {
			    q.setParameter("centr", Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR);
			    q.setParameter("decentr", Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_DECENTR);
			    q.setParameter("status", Constants.CODE_ZNACHENIE_CAMPAIGN_STATUS_ACTIVE);
			}else {
				q.setParameter("statusAll", Constants.CODE_ZNACHENIE_CAMPAIGN_STATUS_V_PROCES);
			}
			List<Object[]> result = q.getResultList();
			List<SelectItem> listResult = new ArrayList<SelectItem>();
			
			if(result!=null && !result.isEmpty()) {
				for(Object[] item : result) {
					SelectItem selItem = new SelectItem(item[0], (String) item[1]);
					listResult.add(selItem);
				}
			}
			
			return listResult;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на кампании", e);
		}
		
	}
	
	/**
	 * Връща последната кампания от текущата година на която дата до е преди днешна дата ,
	 * ако не намери търсим за предходната година и взимаме последната
	 * @return id на кампания ,null нулл ако не намери 
	 * @throws DbErrorException 
	 */
	@SuppressWarnings("unchecked")
	public Long findLastCampaign() throws DbErrorException {
		
		try {
			
			String query = " SELECT id, name " 
						+  " FROM jobs_campaign "
						+  " WHERE year = (SELECT EXTRACT(YEAR FROM CURRENT_DATE)) "
	//					+  " AND date_to < current_date"
						+  " AND type   =  " + Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR
						+  " AND status <> " + Constants.CODE_ZNACHENIE_CAMPAIGN_STATUS_V_PROCES
						+  " AND ranking_date < current_date "
						+  " ORDER BY id desc ";
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);

			List<Object[]> result = q.getResultList();
			
			if(result != null && !result.isEmpty()) {
				
				return SearchUtils.asLong(result.get(0)[0]);
				
			} else {
				//ако не намерим за текуащата търсим за миналата година и взимаме последната
				query = " SELECT id, name " 
						+  " FROM jobs_campaign "
						+  " WHERE year = (SELECT EXTRACT(YEAR FROM CURRENT_DATE)-1) "
						+  " AND type   =  " + Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR
						+  " AND status <> " + Constants.CODE_ZNACHENIE_CAMPAIGN_STATUS_V_PROCES
						+  " AND ranking_date < current_date "
						+  " ORDER BY id desc ";
			
				q = JPA.getUtil().getEntityManager().createNativeQuery(query);
	
				result = q.getResultList();
				
				if(result != null && !result.isEmpty()) {
					
					return SearchUtils.asLong(result.get(0)[0]);
				} 
				
			}

			return null;

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на всички кампании", e);
		}
		
	}

	
	/**
	 * Изваждаме всички кампании за стаж в ДА които не са в "процес на разработка" и форматира резултата в SelectItems 
	 * @throws DbErrorException 
	 */
	public List<SelectItem> findAllCampaigns() throws DbErrorException {
		
		return findActiveCampaigns(false); 
		
	}
	
	/**
	 * Изваждаме всички кампании за стаж в ДА които не са в "процес на разработка" и датата им за първо класиране е по малка от днешна дата
	 * @throws DbErrorException 
	 */
	public List<SelectItem> findCampaignsRanking() throws DbErrorException {
		
		try {
			//String query = " select id, name from jobs_campaign  where status <> :statusAll AND (ranking_date < current_date or type = :decentr)";
			String query = " select id, name from jobs_campaign  where status <> :statusAll AND ranking_date < current_date ";				
				
			query	+= " order by date_to desc " ;
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			
			
			q.setParameter("statusAll", Constants.CODE_ZNACHENIE_CAMPAIGN_STATUS_V_PROCES);
			//q.setParameter("decentr", Constants.CODE_ZNACHENIE_TYPE_CAMPAIGN_DECENTR);
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();
			List<SelectItem> listResult = new ArrayList<SelectItem>();
			
			if(result!=null && !result.isEmpty()) {
				for(Object[] item : result) {
					SelectItem selItem = new SelectItem(item[0], (String) item[1]);
					listResult.add(selItem);
				}
			}
			
			return listResult;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на кампании findCampaignsRanking", e);
		}
		
	}
	
	/**
	 * Намира името на кампанията по id
	 * @param idCamp
	 * @throws DbErrorException 
	 */
	@SuppressWarnings("unchecked")
	public String findNameCampaign(Long idCamp) throws DbErrorException {
		
		try {
			String query = " select name from jobs_campaign where id= :idCamp";
			
					
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(query);
			
			q.setParameter("idCamp", idCamp);
		
			List<Object[]> result = q.getResultList();
		
			
			if(result!=null && !result.isEmpty()) {
				return (String) result.get(0)[0];
			}else {
				return "";
			}
			
		
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на име на кампания", e);
		}
	}	
}