package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.UsersTests;
import indexbg.pjobs.db.dao.AdmUsersTestsDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named("delUserDataView")
@ViewScoped
public class DeleteUserDataViewBean extends PJobsBean {

	static final Logger LOGGER = LoggerFactory.getLogger(DeleteUserDataViewBean.class);
	
	private Long idUser;
	private Long idTestUser;
	private AdmUsersTestsDAO testDao;
	private List<UsersTests> tests = new  ArrayList<UsersTests>();
	private List <Object[]> testResuts = new ArrayList<Object[]>();
	
	@PostConstruct
	public void initData() {
		try {
			idUser = getUserData().getUserId();
			testDao = new AdmUsersTestsDAO(idUser);
			if (JSFUtils.getRequestParameter("idUser") != null && !JSFUtils.getRequestParameter("idUser").equals("")) {
				idTestUser = Long.valueOf(JSFUtils.getRequestParameter("idUser"));
				tests = testDao.getUserTests(idTestUser);
			}
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));
		}finally {
			JPA.getUtil().closeConnection();
		}		
	}
	
	public void onRowToggle(ToggleEvent event) {
	   UsersTests ut  = (UsersTests) event.getData();
	   
	   if(event.getVisibility() == Visibility.VISIBLE) {
		   try {
			   testResuts = new AdmUsersTestsDAO(idUser).getDataResultTest(ut.getId());

		   } catch (DbErrorException e) {
				LOGGER.error("Грешка при търсене на заявления! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
			} catch (Exception e) {
				LOGGER.error("Грешка при работа със системата!!!", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.exception"));					
			
			} finally {
				JPA.getUtil().closeConnection();
			}
	   } else {
		   
	   }
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public List<UsersTests> getTests() {
		return tests;
	}

	public void setTests(List<UsersTests> tests) {
		this.tests = tests;
	}

	public List<Object[]> getTestResuts() {
		return testResuts;
	}

	public void setTestResuts(List<Object[]> testResuts) {
		this.testResuts = testResuts;
	}
}
