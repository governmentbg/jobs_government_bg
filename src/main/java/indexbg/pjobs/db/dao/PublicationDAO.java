package indexbg.pjobs.db.dao;

import static com.indexbg.system.utils.SearchUtils.asLong;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

//import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DialectConstructor;

import indexbg.pjobs.db.Publication;
import indexbg.pjobs.system.Constants;

/**
 * @author ivanc
 *
 */

public class PublicationDAO extends TrackableDAO<Publication> {

	static final Logger LOGGER = LoggerFactory.getLogger(PublicationDAO.class);
	
	public PublicationDAO (Long userId){
		
		super(userId);		
	}
	
	/**Изгражда sql за извличане от БД на обекти/публикации по зададен филтър. Задължително включва/връща и ид. на обектите. 
	 * @param Date - dateFrom - начална дата на публикацията 
	 * @param Date - dateTo - крайна дата на публикацията
	 * @param Long codeSection - код на секция
	 * @return SelectMetadata - sql със сетнати параметри. 
	 */
	public SelectMetadata findPublFilter(Date dateFrom, Date dateTo, Long codeSection, String title, Long lang){
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		List<String> whereStr = new ArrayList<String>();
		String vendorN = JPA.getUtil().getDbVendorName();
		String selAnot = "";
		
		if (vendorN.indexOf("ORACLE") != -1){
			selAnot="dbms_lob.substr(pubLang.ANNOTATION, 300, 1 ) || CASE WHEN dbms_lob.getlength(pubLang.ANNOTATION)>300  THEN '[...]'  END as A4 ";	    		
    	}else if (vendorN.indexOf("POSTGRESQL") != -1){
    		selAnot="substring(pubLang.ANNOTATION FROM 1 FOR 300) || CASE WHEN length(pubLang.ANNOTATION) > 300 THEN '[...]' ELSE '' END  as A4 ";
    	}else{
    		selAnot="pubLang.ANNOTATION A4";  
    	}
			
//		sql.append("SELECT DISTINCT ON (pub.ID) pub.ID A0, pub.SECTION A1, pubLang.TITLE A2, pub.DATE_FROM A3, "+ selAnot + ", pub.IMAGE_PUB A5, pub.DATE_TO A6, pubLang.LANG A7 ");
		sql.append("SELECT DISTINCT pub.ID A0, pub.SECTION A1, pub.DATE_FROM A2, pub.DATE_TO A3, pub.IMAGE_PUB A4, pubLang.TITLE A5 ");
		
		String fromStr = " FROM JOBS_PUBLICATION pub LEFT JOIN JOBS_PUBLICATION_LANG pubLang ON (pub.ID=pubLang.PUBLICATION_ID AND pubLang.LANG="+lang+")";

		
		if(null!=codeSection){
	    	whereStr.add("pub.SECTION=:codeSection");
	    	params.put("codeSection", codeSection); 
		}
	    
	    
	    if (null!=dateFrom && null!=dateTo){
	    	/*whereStr.add("(pub.DATE_FROM <= "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+") AND " + 
	    			"(pub.DATE_TO is null OR "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+ "<= pub.DATE_TO)");*/
	    	whereStr.add("pub.DATE_FROM BETWEEN "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+" AND "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+""); 
		}else{
			if (null!=dateFrom && null==dateTo){
				whereStr.add("pub.DATE_FROM >= "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom));
			}else if(null==dateFrom && null!=dateTo){
				whereStr.add("(pub.DATE_FROM <= "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+")");
			}
			
		}	
	    
	   	    
	    if(null!=title && !title.isEmpty()){
	    	title=title.replaceAll("'", "''");	
	    	
	    	String [] tit = title.trim().toUpperCase().split(" ");
	    	String parm = "( ";
	    	boolean addfirst = true;
	    	for (int i = 0; i < tit.length; i++) {
	    		if (null==tit[i] || tit[i].length()<3)
	    			continue;
	    		if(addfirst) {
	    			parm+=" UPPER(pubLang.TITLE) LIKE '%"+tit[i]+"%' ";
	    			addfirst = false;
	    		}else {
	    			parm+=" OR UPPER(pubLang.TITLE) LIKE '%"+tit[i]+"%' ";
	    		}
	    	}
	    	parm+=" )";
	    	whereStr.add(parm);
		}
	    
			
	    sql.append(fromStr);
	    
	    String strWhere=""; 
	    
		if (!whereStr.isEmpty()) {
			strWhere+=" WHERE ";
			for (int i = 0; i < whereStr.size(); i++) {	
				strWhere+=whereStr.get(i);
				if (i != (whereStr.size() - 1)) {
					strWhere+=" AND ";
				}
			}
		}
		sql.append(strWhere); 
//		sql.append(strWhere + " ORDER BY pub.ID "); 
		
		SelectMetadata smd = new SelectMetadata();
		smd.setSql(sql.toString());
		smd.setSqlCount("SELECT COUNT(distinct pub.ID) as counter  "+fromStr+strWhere);
		smd.setSqlParameters(params);
		
		return smd;
	}
	
	
	/**Изгражда sql за извличане от БД на обекти/публикации по зададен филтър. Задължително включва/връща и ид. на обектите. 
	 * @param Date - dateFrom - начална дата на публикацията 
	 * @param Date - dateTo - крайна дата на публикацията
	 * @param Long codeSection - код на секция
	 * @return SelectMetadata - sql със сетнати параметри. 
	 */
	public SelectMetadata findPublExtFilter(Date dateFrom, Date dateTo, Long codeSection, String title, Long lang, Date currentDate){
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		List<String> whereStr = new ArrayList<String>();
		String vendorN = JPA.getUtil().getDbVendorName();
		String selAnot = "";
		
		
		if (vendorN.indexOf("ORACLE") != -1){
			selAnot="dbms_lob.substr(pubLang.ANNOTATION, 300, 1 ) || CASE WHEN dbms_lob.getlength(pubLang.ANNOTATION)>300  THEN '[...]'  END as A2 ";	    		
    	}else if (vendorN.indexOf("POSTGRESQL") != -1){
    		selAnot="substring(pubLang.ANNOTATION FROM 1 FOR 300) || CASE WHEN length(pubLang.ANNOTATION) > 300 THEN '[...]' ELSE '' END  as A2 ";
    	}else{
    		selAnot="pubLang.ANNOTATION A2";  
    	}
			
		sql.append("SELECT distinct pub.ID A0, pubLang.TITLE A1, "+ selAnot+ ", pub.IMAGE_PUB A3, pub.DATE_FROM A4, pubLang.URL_PUB A5 ,pub.type_pub A6 ");
		
		String fromStr = " FROM JOBS_PUBLICATION pub JOIN JOBS_PUBLICATION_LANG pubLang ON (pub.ID=pubLang.PUBLICATION_ID and pubLang.LANG=:lang)";
		
		if(null!=codeSection){
	    	whereStr.add("pub.SECTION=:codeSection");
	    	params.put("codeSection", codeSection); 
		}
	    
	    
	    /*if (null!=dateFrom && null!=dateTo){
	    	whereStr.add("pub.DATE_FROM >= "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+ " AND (pub.DATE_TO IS NULL OR pub.DATE_TO<= "+ DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+")");
		}else{
			if (null!=dateFrom && null==dateTo){
				whereStr.add("pub.DATE_FROM >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+ " AND (pub.DATE_TO IS NULL OR pub.DATE_TO>= "+ DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+")" );
			}else if(null==dateFrom && null!=dateTo){
				whereStr.add("pub.DATE_TO IS NULL OR pub.DATE_TO >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo));
			}else {
				whereStr.add("pub.DATE_TO IS NULL OR pub.DATE_TO >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate));
			}
		}*/
	    
	    
	    if (null!=dateFrom && null!=dateTo){
	    	whereStr.add("((pub.DATE_FROM BETWEEN "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+" AND "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+") AND (pub.DATE_TO IS NULL OR pub.DATE_TO >= "+ DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+"))");
		}else{
			if (null!=dateFrom && null==dateTo){
				whereStr.add("(pub.DATE_FROM >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+ " AND (pub.DATE_TO IS NULL OR pub.DATE_TO>= "+ DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+"))" );
			}else if(null==dateFrom && null!=dateTo){
				whereStr.add("(pub.DATE_FROM <= " + DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+" AND (pub.DATE_TO IS NULL OR (pub.DATE_TO >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+")))");
			}else {
				whereStr.add("(pub.DATE_TO IS NULL OR pub.DATE_TO >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+")");
			}
		}	
	    
	    whereStr.add("(pub.DATE_FROM <="+ DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+")");
	   	    
	    if(null!=title && !title.isEmpty()){
	    	title=title.replaceAll("'", "''");	
	    	
	    	String [] tit = title.trim().toUpperCase().split(" ");
	    	String parm = "( ";
	    	boolean addfirst = true;
	    	for (int i = 0; i < tit.length; i++) {
	    		if (null==tit[i] || tit[i].length()<3)
	    			continue;
	    		
	    		if(addfirst) {
	    			parm+=" UPPER(pubLang.TITLE) LIKE '%"+tit[i]+"%' ";
	    			addfirst = false;
	    		}else {
	    			parm+=" OR UPPER(pubLang.TITLE) LIKE '%"+tit[i]+"%' ";
	    		}
	    	}
	    	parm+=" )";
	    	whereStr.add(parm);
		}
	    
	    if(null!=lang) {
//			whereStr.add("pubLang.LANG=:lang");
	    	params.put("lang", lang); 
		}
			
	    sql.append(fromStr);
	    
	    String strWhere=""; 
	    
		if (!whereStr.isEmpty()) {
			strWhere+=" WHERE ";
			for (int i = 0; i < whereStr.size(); i++) {	
				strWhere+=whereStr.get(i);
				if (i != (whereStr.size() - 1)) {
					strWhere+=" AND ";
				}
			}
		}
		sql.append(strWhere); 
//		sql.append(strWhere + " ORDER BY pub.DATE_FROM DESC"); 
		
		SelectMetadata smd = new SelectMetadata();
		smd.setSql(sql.toString());
		smd.setSqlCount("SELECT COUNT(distinct pub.ID) as counter  "+fromStr+strWhere);
		smd.setSqlParameters(params);
		
		return smd;
	}
	
	
	/*public SelectMetadata findPublExtFilter(Date dateFrom, Date dateTo, Long codeSection, String title, Long lang, Date currentDate){
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		List<String> whereStr = new ArrayList<String>();
		String vendorN = JPA.getUtil().getDbVendorName();
		String selAnot = "";
		
		
		if (vendorN.indexOf("ORACLE") != -1){
			selAnot="dbms_lob.substr(pubLang.ANNOTATION, 300, 1 ) || CASE WHEN dbms_lob.getlength(pubLang.ANNOTATION)>300  THEN '[...]'  END as A2 ";	    		
    	}else if (vendorN.indexOf("POSTGRESQL") != -1){
    		selAnot="substring(pubLang.ANNOTATION FROM 1 FOR 300) || CASE WHEN length(pubLang.ANNOTATION) > 300 THEN '[...]' ELSE '' END  as A2 ";
    	}else{
    		selAnot="pubLang.ANNOTATION A2";  
    	}
			
		sql.append("SELECT distinct pub.ID A0, pubLang.TITLE A1, "+ selAnot+", pub.IMAGE_PUB A3, pub.DATE_FROM A4 ");
		
		String fromStr = " FROM JOBS_PUBLICATION pub JOIN JOBS_PUBLICATION_LANG pubLang ON (pub.ID=pubLang.PUBLICATION_ID)";
		
		if(null!=codeSection){
	    	whereStr.add("pub.SECTION=:codeSection");
	    	params.put("codeSection", codeSection); 
		}
	    
	    
	    if (null!=dateFrom && null!=dateTo){
	    	whereStr.add("((pub.DATE_FROM BETWEEN "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+" AND "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+") AND (pub.DATE_TO IS NULL OR pub.DATE_TO >= "+ DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+"))");
		}else{
			if (null!=dateFrom && null==dateTo){
				whereStr.add("(pub.DATE_FROM >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+ " AND (pub.DATE_TO IS NULL OR pub.DATE_TO>= "+ DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+"))" );
			}else if(null==dateFrom && null!=dateTo){
				whereStr.add("(pub.DATE_FROM <= " + DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+" AND (pub.DATE_TO IS NULL OR (pub.DATE_TO <="+DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+")))");
			}else {
				whereStr.add("(pub.DATE_TO IS NULL OR pub.DATE_TO >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+")");
			}
		}	
	    
	   	    
	    if(null!=title && !title.isEmpty()){
	    	title=title.replaceAll("'", "''");	
	    	
	    	String [] tit = title.trim().toUpperCase().split(" ");
	    	String parm = "( ";
	    	for (int i = 0; i < tit.length; i++) {
	    		if (null==tit[i] || tit[i].length()<3)
	    			continue;
	    		if(i==0) {
	    			parm+="UPPER(pubLang.TITLE) LIKE '%"+tit[i]+"%'";
	    		}else {
	    			parm+=" OR UPPER(pubLang.TITLE) LIKE '%"+tit[i]+"%'";
	    		}
	    	}
	    	parm+=" )";
	    	whereStr.add(parm);
		}
	    
	    if(null!=lang) {
	    	params.put("lang", lang); 
		}
			
	    sql.append(fromStr);
	    
	    String strWhere=""; 
	    
		if (!whereStr.isEmpty()) {
			strWhere+=" WHERE ";
			for (int i = 0; i < whereStr.size(); i++) {	
				strWhere+=whereStr.get(i);
				if (i != (whereStr.size() - 1)) {
					strWhere+=" AND ";
				}
			}
		}
		sql.append(strWhere); 
//		sql.append(strWhere + " ORDER BY pub.DATE_FROM DESC"); 
		
		SelectMetadata smd = new SelectMetadata();
		smd.setSql(sql.toString());
		smd.setSqlCount("SELECT COUNT(distinct pub.ID) as counter  "+fromStr+strWhere);
		smd.setSqlParameters(params);
		
		return smd;
	}*/
	
	/*public SelectMetadata findPublExtFilter(Date dateFrom, Date dateTo, Long codeSection, String title, Long lang, Date currentDate){
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		List<String> whereStr = new ArrayList<String>();
		String vendorN = JPA.getUtil().getDbVendorName();
		String selAnot = "";
		
		
		if (vendorN.indexOf("ORACLE") != -1){
			selAnot="dbms_lob.substr(pubLang.ANNOTATION, 300, 1 ) || CASE WHEN dbms_lob.getlength(pubLang.ANNOTATION)>300  THEN '[...]'  END as A2 ";	    		
    	}else if (vendorN.indexOf("POSTGRESQL") != -1){
    		selAnot="substring(pubLang.ANNOTATION FROM 1 FOR 300) || CASE WHEN length(pubLang.ANNOTATION) > 300 THEN '[...]' ELSE '' END  as A2 ";
    	}else{
    		selAnot="pubLang.ANNOTATION A2";  
    	}
			
		sql.append("SELECT distinct pub.ID A0, pubLang.TITLE A1, "+ selAnot+", pub.IMAGE_PUB A3, pub.DATE_FROM A4 ");
		
		String fromStr = " FROM JOBS_PUBLICATION pub JOIN JOBS_PUBLICATION_LANG pubLang ON (pub.ID=pubLang.PUBLICATION_ID AND pubLang.LANG="+lang+")";
		
		fromStr+=" WHERE pub.ID IN (SELECT distinct pub.ID A0 " + 
				" FROM " + 
				" JOBS_PUBLICATION pub JOIN JOBS_PUBLICATION_LANG pubLang ON (pub.ID=pubLang.PUBLICATION_ID) ";
		
		if(null!=codeSection){
	    	whereStr.add("pub.SECTION=:codeSection");
	    	params.put("codeSection", codeSection); 
		}
	    
	    
	    if (null!=dateFrom && null!=dateTo){
	    	whereStr.add("((pub.DATE_FROM BETWEEN "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+" AND "+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+") AND (pub.DATE_TO IS NULL OR pub.DATE_TO >= "+ DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+"))");
		}else{
			if (null!=dateFrom && null==dateTo){
				whereStr.add("(pub.DATE_FROM >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, dateFrom)+ " AND (pub.DATE_TO IS NULL OR pub.DATE_TO>= "+ DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+"))" );
			}else if(null==dateFrom && null!=dateTo){
				whereStr.add("(pub.DATE_FROM <= " + DialectConstructor.convertDateOnlyToSQLString(vendorN, dateTo)+" AND (pub.DATE_TO IS NULL OR (pub.DATE_TO <="+DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+")))");
			}else {
				whereStr.add("(pub.DATE_TO IS NULL OR pub.DATE_TO >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, currentDate)+")");
			}
		}	
	    
	   	    
	    if(null!=title && !title.isEmpty()){
	    	title=title.replaceAll("'", "''");	
	    	
	    	String [] tit = title.trim().toUpperCase().split(" ");
	    	String parm = "( ";
	    	for (int i = 0; i < tit.length; i++) {
	    		if (null==tit[i] || tit[i].length()<3)
	    			continue;
	    		if(i==0) {
	    			parm+="UPPER(pubLang.TITLE) LIKE '%"+tit[i]+"%'";
	    		}else {
	    			parm+=" OR UPPER(pubLang.TITLE) LIKE '%"+tit[i]+"%'";
	    		}
	    	}
	    	parm+=" )";
	    	whereStr.add(parm);
		}
	    
    	
	    
	    
	    if(null!=lang) {
	    	params.put("lang", lang); 
		}
			
	    sql.append(fromStr);
	    
	    String strWhere=""; 
	    
		if (!whereStr.isEmpty()) {
			strWhere+=" WHERE ";
			for (int i = 0; i < whereStr.size(); i++) {	
				strWhere+=whereStr.get(i);
				if (i != (whereStr.size() - 1)) {
					strWhere+=" AND ";
				}
			}
		}
		strWhere+=(")");
		sql.append(strWhere); 
//		sql.append(strWhere + " ORDER BY pub.DATE_FROM DESC"); 
		
		SelectMetadata smd = new SelectMetadata();
		smd.setSql(sql.toString());
		smd.setSqlCount("SELECT COUNT(distinct pub.ID) as counter  "+fromStr+strWhere);
		smd.setSqlParameters(params);
		
		return smd;
	}*/
	
	/*SELECT distinct pub.ID A0, pubLang.TITLE A1, 
	substring(pubLang.ANNOTATION FROM 1 FOR 300) || CASE WHEN length(pubLang.ANNOTATION) > 300 THEN '[...]' ELSE '' END  as A2 , 
	pub.IMAGE_PUB A3, pub.DATE_FROM A4  
	FROM 
	JOBS_PUBLICATION pub JOIN JOBS_PUBLICATION_LANG pubLang ON (pub.ID=pubLang.PUBLICATION_ID and pubLang.LANG=1) 
	WHERE 
	pub.ID IN (SELECT distinct pub.ID A0  
	FROM 
	JOBS_PUBLICATION pub JOIN JOBS_PUBLICATION_LANG pubLang ON (pub.ID=pubLang.PUBLICATION_ID) 
	WHERE 
	pub.SECTION=8)*/
	
	
		
	/** Извлича данни от БД по зададен sql и атрибут за сортиране на данните
	 * @param smd - sql за избор на данни 
	 * @param defaultSortColumn - колона от таблица в базата данни, по която се сортират данните
	 * @return
	 * @throws DbErrorException
	 */
	public LazyDataModelSQL2Array newLazyDataModel (SelectMetadata smd, String defaultSortColumn ) throws DbErrorException { 
		   
		if (smd == null) return null;
		 LazyDataModelSQL2Array list = null;
		try {
							
			list = new LazyDataModelSQL2Array(smd, defaultSortColumn);  
				
		} catch (DbErrorException e) { 
			LOGGER.error(e.getMessage(), e);
			throw new DbErrorException ("Грешка при търсене на записи от БД!" +e.getCause().getMessage(), e);
		}
	
		return list;
	}
	
	
	
	


	
	/** Извлича прикачените файлове към дадена секция
	 * @param dat - Date - текуща дата, към която секцията е актуална
	 * @param codeSection - Long - код на секцията 
	 * @param codeObjPubl - код на обекта/секцията
	 * @return
	 * @throws DbErrorException
	 */
	
	/*@SuppressWarnings("unchecked")
	public List<Object[]> findRelPublFiles(Date dat, Long codeSection, Long codeObjPubl) throws DbErrorException{
		
		String vendorN = JPA.getUtil().getDbVendorName();
		
		String sqlSel="SELECT pub.ID, COUNT(f.ID) "+    
			"FROM "+ 
			"PDOI_PUBLICATION pub "+ 
			"JOIN FILES f ON (pub.ID = f.ID_OBJECT AND f.CODE_OBJECT=?) "+ 
			"WHERE "+ 
			"pub.SECTION=? AND pub.DATE_FROM <="+DialectConstructor.convertDateOnlyToSQLString(vendorN, dat)+" "+
			"AND (pub.DATE_TO IS NULL OR pub.DATE_TO >="+DialectConstructor.convertDateOnlyToSQLString(vendorN, dat)+") "+
			"GROUP by pub.ID";
		
		try{
			Query query = createNativeQuery(sqlSel); 

			query.setParameter(1, codeObjPubl);
			query.setParameter(2, codeSection);
			
			return query.getResultList();
		
		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при зареждане на данни от БД", e);
		}
		
	}*/
	
	
	/** Извлича от БД само анотацията на дадена публикация
	 * @param id - по ид. на публикацията
	 * @return - връща анотацията 
	 * @throws DbErrorException
	 */
	/*@SuppressWarnings("unchecked")
	public List<Object[]> findAnnotById(Long id)  throws DbErrorException {
		
		String sqlSel="SELECT pub.ANNOTATION "+    
				"FROM "+ 
				"PDOI_PUBLICATION pub "+ 
				"WHERE "+ 
				"pub.ID=?";
			
			try{
				Query query = createNativeQuery(sqlSel); 

				query.setParameter(1, id);
				
				return query.getResultList();
			
			} catch (Exception e) {
				throw new DbErrorException("Възникна грешка при зареждане на данни от БД", e);
			}
	
	}*/
	
	
	/** Извлича от БД на данни за контакт на администраторите 
	 * @param orgCode - код на службата но администратора
	 * @param userType - код на потребител тип администратор
	 * @return 
	 * @throws DbErrorException
	 */
	/*@SuppressWarnings("unchecked")
	public List<Object[]> findAdminDataByOrgCode(Long orgCode, List<Long> userType)  throws DbErrorException {
		
		String sqlSel="SELECT au.USER_ID, au.NAMES, au.EMAIL, au.PHONE, au.USER_TYPE, rs.ADDRESS "+    
				"FROM "+ 
				"ADM_USERS au "+
				"LEFT OUTER JOIN PDOI_RESPONSE_SUBJECT rs ON (au.ORG_CODE = rs.ID) "+ 
				"WHERE "+ 
				"au.ORG_CODE = ? "+
				"AND au.USER_TYPE IN ("+userType.toString().trim().substring(1, userType.toString().trim().length()-1)+") "+
				"ORDER BY au.NAMES ";
			
			try{
				Query query = createNativeQuery(sqlSel); 

				query.setParameter(1, orgCode);
			
				
				return query.getResultList();
			
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new DbErrorException("Възникна грешка при четене на данни от БД", e);
			}
	
	}*/
	
	/**
	 * 
	 * @param classifCode
	 * @param roleCode
	 * @return
	 * @throws DbErrorException
	 */
	/*@SuppressWarnings("unchecked")
	public List<Object[]> findAdminData(Long classifCode, Long roleCode) throws DbErrorException {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT au.names, au.email, au.phone ");
		sb.append("FROM adm_users au ");
		sb.append("JOIN adm_user_roles aur ON ");
		sb.append("aur.user_id = au.user_id AND aur.code_classif = ? and aur.code_role = ?");
		
		try{
			Query query = createNativeQuery(sb.toString()); 
			query.setParameter(1, classifCode);
			query.setParameter(2, roleCode);
			
			return (List<Object[]>) query.getResultList();
		
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new DbErrorException("Възникна грешка при четене на данни от БД", e);
		}
	}*/
	
	
	/** Извлича от БД на EMAIL на администратора, по неговия код на службата
	 * @param orgCode - код на службата
	 * @return
	 * @throws DbErrorException
	 */
	/*@SuppressWarnings("unchecked")
	public List<Object[]> findAdminEmailByOrgCode(Long orgCode)  throws DbErrorException {
		
		String sqlSel="SELECT au.USER_ID, au.EMAIL FROM ADM_USERS au WHERE au.ORG_CODE = ? ";
			
			try{
				Query query = createNativeQuery(sqlSel); 

				query.setParameter(1, orgCode);
			
				return query.getResultList();
			
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new DbErrorException("Възникна грешка при четене на данни от БД", e);
			}
	
	}*/
	
	
	public Long checkForSinglePublicInSect(Long section) throws DbErrorException {
		try{
			String sql = "SELECT ID FROM JOBS_PUBLICATION WHERE SECTION = ?"; //TODO
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql);
			q.setParameter(1,  section);
			
			List<Object> rez = q.getResultList();
			
			if(rez!=null && rez.size()==1) {
				return asLong(rez.get(0));
			} else if(rez==null || rez.size()==0) {
				return -1L;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new DbErrorException("Възникна грешка при четене на данни от БД checkCountPublicinSection", e);
		}
		return null;
	}
	
}
