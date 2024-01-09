package indexbg.pjobs.db.dao;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.exceptions.DbErrorException;

import indexbg.pjobs.db.PageText;

public class PageTextDAO extends TrackableDAO<PageText> {	

	static final Logger LOGGER = LoggerFactory.getLogger(PageTextDAO.class);
	
	public PageTextDAO (Long userId){
		
		super(userId);		
	}	
	
	
	public PageText loadPageTextByCodeSectLang (Long codeSect, Long lang) throws DbErrorException { 
		
		try {
			
			Query query = createQuery("from PageText where codePage = ?1 and lang = ?2"); 
			
			query.setParameter(1, codeSect);	
			query.setParameter(2, lang);	
			
			
			if(query.getResultList()!= null && !query.getResultList().isEmpty()) {
				return (PageText) query.getResultList().get(0);
			}
			
			return null;			
			
		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при зареждане на списък със събития по ид на заявлението!", e);
		}
		
	}
	
	
}