package indexbg.pjobs.db.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.TrackableDAO;

import indexbg.pjobs.db.AdmGroups;

public class AdmGroupsDAO extends TrackableDAO<AdmGroups> {	

	static final Logger LOGGER = LoggerFactory.getLogger(AdmGroupsDAO.class);
	
	public AdmGroupsDAO (Long userId){
		
		super(userId);		
	}
	
}
