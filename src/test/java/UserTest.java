import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Ignore;
import org.junit.Test;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectInUseException;

import indexbg.pjobs.db.AdmUser;
import indexbg.pjobs.db.UserAdd;
import indexbg.pjobs.db.UserEducation;
import indexbg.pjobs.db.UserExperience;
import indexbg.pjobs.db.UserLanguage;
import indexbg.pjobs.db.dao.AdmUserDAO;

public class UserTest {
	private AdmUserDAO dao = new AdmUserDAO(4001L);
	
	@Test
	@Ignore
	public void testRead() throws DbErrorException {		
		AdmUser u = dao.findById(4001L);
		System.out.println("Kod na potrebitelia: " + u.getId());
		System.out.println("Kod na detailite: " + u.getUserAdd().getId());	
	}
	
	@Test
	@Ignore
	public void testInsert() throws DbErrorException, ObjectInUseException {
		try {
			JPA.getUtil().begin();
			
			AdmUser u = dao.findById(4001L);
			
			UserLanguage ul = new UserLanguage();
			ul.setLanguage(27L);
			ul.setLevel(27L);
			
			u.addLanguage(ul);
			
			JPA.getUtil().getEntityManager().persist(u);
			
			JPA.getUtil().commit();
		
		} catch (Exception e) {
			
			JPA.getUtil().rollback();
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	@Test
	
	public void testDelete() throws DbErrorException {
		try {
			JPA.getUtil().begin();
			AdmUser u = dao.findById(4001L);
			
			
			if(u.getUserLanguages().size()>0) {
			
				u.getUserLanguages().remove(0);
			}
			
//			UserLanguage ul = new UserLanguage();
//			ul.setLanguage(1L);
//			ul.setLevel(1L);
//			
//			u.addLanguage(ul);
			
			JPA.getUtil().getEntityManager().persist(u);
			JPA.getUtil().commit();
		
		} catch (Exception e) {
			
			JPA.getUtil().rollback();
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
}
