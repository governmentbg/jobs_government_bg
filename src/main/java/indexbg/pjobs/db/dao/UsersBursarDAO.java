package indexbg.pjobs.db.dao;

import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.exceptions.DbErrorException;
import indexbg.pjobs.db.UsersBursar;

public class UsersBursarDAO extends TrackableDAO<UsersBursar> {	

	static final Logger LOGGER = LoggerFactory.getLogger(UsersBursarDAO.class);
	
	public UsersBursarDAO (Long userId){
		
		super(userId);		
	}
	
	@SuppressWarnings("unchecked")
	public UsersBursar findByIdUser(Long userId) throws DbErrorException {
		
		try {

			Query query = JPA.getUtil().getEntityManager().createQuery("FROM UsersBursar  WHERE userId = :userId");

			query.setParameter("userId", userId);

			List<UsersBursar> bursar = query.getResultList();

			if (bursar.size() > 0) {
				return bursar.get(0);
			} else {
				return null;
			}

		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при извличане на стипендиант!!!", e);
		}
		
	}	

	
}