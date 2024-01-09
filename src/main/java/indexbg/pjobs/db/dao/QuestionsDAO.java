package indexbg.pjobs.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import indexbg.pjobs.db.Questions;
import indexbg.pjobs.system.Constants;

public class QuestionsDAO extends TrackableDAO <Questions> {
	
static final Logger LOGGER = LoggerFactory.getLogger(QuestionsDAO.class);
	
	public QuestionsDAO (Long userId){
		
		super(userId);		
	}
	
	
	
	/**Изгражда sql за извличане от БД на обекти/въпроси по зададен филтър. Задължително включва/връща и ид. на обектите. 
	 
	 * @param Long codeGrupa - код на група на въпроса
	 * @param Long codeGrupa - код на статус на въпроса
	 * @param String quest - думи от въпроса
	 * @param Long lang - език   
	 * @return SelectMetadata - sql със сетнати параметри. 
	 */
	public SelectMetadata findQuestFilter(Long codeGrupa, Boolean codeStatus, String quest, Long lang){
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		List<String> whereStr = new ArrayList<String>();
		String vendorN = JPA.getUtil().getDbVendorName();
		String selQuest = "";
		
		if (vendorN.indexOf("ORACLE") != -1){
			selQuest="dbms_lob.substr(que.QUEST, 300, 1 ) || CASE WHEN dbms_lob.getlength(que.QUEST)>300  THEN '[...]'  END as A2 ";	    		
    	}else if (vendorN.indexOf("POSTGRESQL") != -1){
    		selQuest="substring(que.QUEST FROM 1 FOR 300) || CASE WHEN length(que.QUEST) > 300 THEN '[...]' ELSE '' END  as A2 ";
    	}else{
    		selQuest="que.QUEST A2";  
    	}
			
		sql.append("SELECT DISTINCT que.ID A0, que.GRUPA A1, "+selQuest+", que.STATUS A3 ");
		
		String fromStr = " FROM JOBS_QUESTIONS que ";

		
		if(null!=codeGrupa){
	    	whereStr.add("que.GRUPA=:codeGrupa");
	    	params.put("codeGrupa", codeGrupa); 
		}
		
		if (null!=codeStatus && codeStatus) {
			Long stat=1L;
	    	whereStr.add("que.STATUS=:STAT");
	    	params.put("STAT", stat); 
		}
	   	    
	    if(null!=quest && !quest.isEmpty()){
	    	quest=quest.replaceAll("'", "''");	
	    	
	    	String [] tit = quest.trim().toUpperCase().split(" ");
	    	String parm = "( ";
	    	for (int i = 0; i < tit.length; i++) {
	    		if (null==tit[i] || tit[i].length()<3)
	    			continue;
	    		if(i==0) {
	    			parm+="UPPER(que.QUEST) LIKE '%"+tit[i]+"%'";
	    		}else {
	    			parm+=" OR UPPER(que.QUEST) LIKE '%"+tit[i]+"%'";
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
		
		SelectMetadata smd = new SelectMetadata();
		smd.setSql(sql.toString());
		smd.setSqlCount("SELECT COUNT(distinct que.ID) as counter  "+fromStr+strWhere);
		smd.setSqlParameters(params);
		
		return smd;
	}
	
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
	
	
	/**Изгражда sql за извличане от БД на обекти/често задавани въпроси по код на групата и език.
	 * @param codeGrupa - код на групата
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Questions> findGrupaQuestFilter(Long grupa, Long lang) throws DbErrorException {
		
		try {

			Query query = JPA.getUtil().getEntityManager().createQuery("FROM Questions  WHERE lang =?1 and grupa =?2 and status =?3 ");

			query.setParameter(1, lang);
			query.setParameter(2, grupa);
			query.setParameter(3, Long.valueOf(Constants.CODE_ZNACHENIE_DA));	
			List<Questions> quest = query.getResultList();

			return quest;
			
		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при извличане на често задаваните въпроси!!!", e);
		}


	}
	
	

}
