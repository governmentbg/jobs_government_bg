package indexbg.pjobs.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.UnexpectedResultException;

import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.system.SystemData;

@ManagedBean
@ViewScoped
public class Test {
	
	static final Logger LOGGER = LoggerFactory.getLogger(Test.class);
	
	public void updateRegistry() {
		LOGGER.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		try {
			AdministrationDAO respSubDAO = new AdministrationDAO(-1l,new SystemData());
			String comment = respSubDAO.updateAdmRegisterEntries();
			LOGGER.info("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
		} catch (DbErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
