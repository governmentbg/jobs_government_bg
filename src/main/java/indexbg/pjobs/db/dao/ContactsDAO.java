package indexbg.pjobs.db.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.TrackableDAO;

import indexbg.pjobs.db.Contacts;

public class ContactsDAO extends TrackableDAO<Contacts> {	

	static final Logger LOGGER = LoggerFactory.getLogger(ContactsDAO.class);
	
	public ContactsDAO (Long userId){
		
		super(userId);		
	}
	
	
}