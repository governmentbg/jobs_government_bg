package indexbg.pjobs.bean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.dao.PublicationDAO;

@Named("publExtControler")
@RequestScoped
public class PublExtControler {

	
	static final Logger LOGGER = LoggerFactory.getLogger(PublExtControler.class);
	
	
	
	/**
	 *
	 */
	
	public String controlPublData(){
		
		String sect =JSFUtils.getRequestParameter("sect");
		if (sect != null && !sect.trim().isEmpty()){
			
			try {
				Long publId =  new PublicationDAO(-1L).checkForSinglePublicInSect(Long.valueOf(sect));
				
				if(publId!=null) {
					if(publId.equals(-1L)) {
						//return "pageNotFound.jsf"; //po iskane na KK
						return null;
					} else {
						return "publExtData.jsf?idPubl="+publId;
					}
				} 
				
				
			} catch (DbErrorException e) {
				LOGGER.error(e.getMessage(), e);
				return "pageNotFound.jsf";
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return "pageNotFound.jsf";
			} finally {
				JPA.getUtil().closeConnection();
			}
			
			
		} else {
			return "pageNotFound.jsf";
		}
		
	
	
		return null;
	}
	
	

}
