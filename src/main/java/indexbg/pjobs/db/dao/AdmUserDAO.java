package indexbg.pjobs.db.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.SysConstants;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.AdmUser;
import indexbg.pjobs.db.UserAdd;
import indexbg.pjobs.db.UsersBursar;
import indexbg.pjobs.system.Constants;

public class AdmUserDAO extends TrackableDAO<AdmUser> {
	
	static final Logger LOGGER = LoggerFactory.getLogger(AdmUserDAO.class);

	public AdmUserDAO(Long userId) {
		super(userId);
	}
	
	public UsersBursar getBursaryByUserId(Long userId) throws DbErrorException {
		UsersBursar bursar = null;
		
		try {
			EntityManager em = JPA.getUtil().getEntityManager();
			bursar = em.find(UsersBursar.class, userId);
			return bursar;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извикаване на данни за одобрен стипендиант по номер на потребител", e);
		}
	}
	
	public Object[] getUserByPin(Long pin) throws DbErrorException {
		
		
		String sql = "select id,user_id,name, surname, family, egn from jobs_users_add where pin =:pin";
				
		try {
			EntityManager em = JPA.getUtil().getEntityManager();
			Query query = em.createNativeQuery(sql);
			query.setParameter("pin", pin);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = query.getResultList();
			
			if(resultList == null || resultList.size() == 0) {
				return null;
			} else {
				Object[] result = resultList.get(0);
				return result;
			}
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извикаване на данни за одобрен стипендиант по номер на потребител", e);
		}
	}
	
	/** Търси потребител в таблицата USERS_ADD по ПИН
	 * 
	 * @param pin
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public UserAdd getUserAddByPin(Long pin) throws DbErrorException {
						
		try {
			Query query = JPA.getUtil().getEntityManager().createQuery("FROM UserAdd  WHERE pin = :pin");

			query.setParameter("pin", pin);

			List<UserAdd> userAdd = query.getResultList();

			if (userAdd.size() > 0) {
				return userAdd.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извикаване на данни за потребител по ПИН", e);
		}
	}
	
	/** Търси потребител в таблицата USERS_ADD по ЕГН
	 * 
	 * @param egn
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public UserAdd getUserAddByEgn(String egn) throws DbErrorException {
						
		try {
			Query query = JPA.getUtil().getEntityManager().createQuery("FROM UserAdd  WHERE egn = :egn");

			query.setParameter("egn", egn);

			List<UserAdd> userAdd = query.getResultList();

			if (userAdd.size() > 0) {
				return userAdd.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извикаване на данни за потребител по ЕГН", e);
		}
	}
	
	
	
	public Object[] getPinByEgn(String egn, Long id) throws DbErrorException {
		
		
		String sql = "select ua.id,ua.user_id, ua.pin from jobs_users_add ua JOIN adm_users u ON (ua.user_id = u. user_id) where ua.id not in (:id) and ua.egn = :egn and u.status =:status";
			
		
		try {
			
			EntityManager em = JPA.getUtil().getEntityManager();
			Query query = em.createNativeQuery(sql);
			query.setParameter("id", id);
			query.setParameter("egn", egn);
			query.setParameter("status", 2L); //TODO статус активен-няма го вкаран в SysConstants
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = query.getResultList();
			
			if(resultList == null || resultList.size() == 0) {
				return null;
			} else {
				Object[] result = resultList.get(0);
				return result;
			}
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на ПИН по ЕГН", e);
		}
	}
	
	/** Изграждане на SQL за извличане на подходящи кандидати за работа за свободни позиции в дадена администрация по зададни критерии за търсене
	 * 
	 * @param profField
	 * @param spec
	 * @param educDegree
	 * @param expYears
	 * @param specIzisk
	 * @return
	 */
	public SelectMetadata findCandidates(Long pin ,Long profField, Long spec, Long educDegree, Long expYears, String specIzisk, Long candidateFor, Long educationArea, String name, String surname, String family) {
		
		String sql = " SELECT distinct (ua.user_id) AS A0, "
					+ " ua.pin AS A1, " 
					+ " CONCAT  (ua.name, ' ', ua.surname, ' ', ua.family) AS A2, "
					+ " ua.region AS A3, "
					+ " ua.municipality AS A4, " 
					+ " ua.town AS A5, "
					+ " get_specialnosti_lice(ua.user_id) AS A6  ";
			 		
		String tablici = " FROM jobs_users_add ua "; 
		String whereClause = " WHERE ";
		String and = " ";
		Map<String, Object> params = new HashMap<>();		
		
		if(pin != null) {
			whereClause += " ua.pin = :pin " ;
			params.put("pin", pin);
		
		} else {
			whereClause += " ua.search_agreement IS TRUE ";
		}
		
		if (name != null && !name.isEmpty()) {
			and = " AND ";
			whereClause += and + " upper(ua.name) like :name " ;
			params.put("name", "%" + name.trim().toUpperCase() + "%" );	
		}
		
		if (surname != null && !surname.isEmpty()) {
			and = " AND ";
			whereClause += and + " upper(ua.surname) like :surname " ;
			params.put("surname", "%" + surname.trim().toUpperCase() + "%" );	
		}
		
		if (family != null && !family.isEmpty()) {
			and = " AND ";
			whereClause += and + " upper(ua.family) like :family " ;
			params.put("family", "%" + family.trim().toUpperCase() + "%" );	
		}
		
		if (profField != null || spec != null || educDegree != null) {
			tablici = tablici + " LEFT OUTER JOIN jobs_users_education ued ON ua.user_id = ued.user_id ";
		}
		
		if(profField != null){			
			
			and = " AND ";
			whereClause += and + " ued.category = :profField " ;
			params.put("profField", profField);			
		}		
						
		if(spec != null){
			
			and = " AND ";
			whereClause += and + " ued.subject = :spec ";
			params.put("spec", spec);		
		}
		
		if (educDegree != null) {
			and = " AND ";
			whereClause += and + " ued.education_degree = :educDegree ";
			params.put("educDegree", educDegree);		
		}
		
		if(candidateFor != null) {
			and = " AND ";
			whereClause += and + " ua.apply_for = :candidateFor ";
			params.put("candidateFor", candidateFor);	
		}
		
		if (educationArea != null) {
			tablici = tablici + " LEFT OUTER JOIN jobs_users_student us ON ua.user_id = us.user_id ";
			
			and = " AND ";
			whereClause += and + " us.education_area = :educationArea " ;
			params.put("educationArea", educationArea);	
		}
		
		if (expYears != null || (specIzisk != null && !specIzisk.isEmpty())) {
			tablici = tablici + "  LEFT OUTER JOIN jobs_users_experience uex ON ua.user_id = uex.user_id ";			
		}
		
		if(expYears != null){
			and = " AND ";
			whereClause += and + " uex.years >= :expYears ";
			params.put("expYears", expYears);	
		}
		
		if(specIzisk != null && !specIzisk.isEmpty()){
			
			specIzisk = specIzisk.replaceAll("'", "''");
			String [] si = specIzisk.trim().toUpperCase().split(" ");
			String parm = "( ";
			for (int i = 0; i < si.length; i++) {
				if (si[i] == null || si[i].length() < 3) {
					continue;
				}
				
				if (i == 0) {
					parm += " UPPER (uex.add_info) LIKE '%" + si[i] +"%'";
				
				} else {
					parm += " OR UPPER (uex.add_info) LIKE '%" + si[i] +"%'";					
				}
			}
			
			parm += " )";			
			and = " AND ";
			whereClause += and + parm;	        
		}
		
		SelectMetadata smd = new SelectMetadata();
		
		smd.setSql(sql + tablici + whereClause);
		smd.setSqlCount(" SELECT COUNT(DISTINCT UA.USER_ID) as counter" + tablici + whereClause);	
		smd.setSqlParameters(params); 
		
		return smd;
	}
	
	
	public Long checkFillProfileApplyFor(Long userId) throws DbErrorException {		
		
		String sql = "select apply_for from jobs_users_add where user_id =:userId";			
		
		try {
			
			EntityManager em = JPA.getUtil().getEntityManager();
			Query query = em.createNativeQuery(sql);
			query.setParameter("userId", userId);
			
			@SuppressWarnings("unchecked")
			List<Object> resultList = query.getResultList();
			
			if(resultList == null || resultList.size() == 0) {
				return null;
			} else {
				return SearchUtils.asLong(resultList.get(0));
			}
		} catch (Exception e) {
			throw new DbErrorException("Грешка при проверка за попълнен профил на потребител", e);
		}
	}
	


	/**
	 * Променя паролата на потребителя
	 * @param userId id на потребителя
	 * @param oldPass base64 криптирана настояща парола
	 * @param newPass ase64 криптирана нова парола
	 * @return false, ако потребителят не е намерен в системата (въвел е грешна парола)
	 * @throws DbErrorException
	 */
	public boolean changePassword(Long userId, String oldPass, String newPass) throws DbErrorException {
		
		boolean userFound = true;
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select user_id from ADM_USERS where password = :oldPass and user_id = :userId");
			Query sqlQuery = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			sqlQuery.setParameter("oldPass", oldPass);
			sqlQuery.setParameter("userId", userId);
			
			@SuppressWarnings("unchecked")
			List<Object> result = sqlQuery.getResultList();
			
			if(result.isEmpty()){
				userFound = false;
			} 
			else {
				sql = new StringBuilder();
				sql.append("update ADM_USERS set password = :newPass where user_id = :userId");
				sqlQuery = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
				sqlQuery.setParameter("newPass", newPass);
				sqlQuery.setParameter("userId", Long.valueOf(result.get(0).toString()));
				
				sqlQuery.executeUpdate();
			}
		} catch (Exception e) {
			LOGGER.error("Exception: " + e.getMessage());
			throw new DbErrorException(e.getMessage());
		}
		return userFound;
	}
	
	
	/** Актуализира ЕГН-то на лицето и полето dyslexia в таблицата USERS_ADD
	 * 
	 * @param egn
	 * @param id
	 * @param dyslexia
	 * @throws DbErrorException
	 */
	public void updateUsserAdd(Long id,String egn,boolean dyslexia) throws DbErrorException {
		
		try {

			Query query = createNativeQuery("update jobs_users_add set egn = ?, dyslexia = ? where id = ?"); 

			query.setParameter(1, egn);
			query.setParameter(2, dyslexia);
			query.setParameter(3, id);
			query.executeUpdate();
			
			SystemJournal j = new SystemJournal();
			
			j.setCodeObject(Constants.CODE_OBJECTS_USERS); 
			j.setIdObject(id); 
			j.setCodeAction(SysConstants.CODE_DEIN_KOREKCIA); 
			j.setDateAction(new Date()); 
			j.setIdUser(getUserId()); 
			j.setIdentObject("Променя се ЕГН на потребител с ИД: " + id+  " на дата: " + new SimpleDateFormat("dd.MM.yyyy").format(new Date())); 			
			getEntityManager().persist(j);			

		} catch (Exception e) {
			throw new DbErrorException("Възникна грешка при актуализация на ЕГН на потребител!!!", e);
		}
		
	}

	/**
	 * Създава нов ПИН за кандидат като прочита последната стойност от seq_pin (9-цифрено число) 
	 * и добавя контролна цифра по метода за валидиране на ЕГН
	 * @return новия ПИН 
	 * @throws DbErrorException 
	 */
	public Long generatePin() throws DbErrorException {
		
		long nextVal = 0;
		String seq;
		
		try {
			nextVal = nextVal("seq_pin");
		} catch (Exception e) {
			throw new DbErrorException("Грешка при прочитане на seq_pin", e);
		}
		
		seq = String.valueOf(nextVal);
		if(seq.length() != 9) {
			throw new DbErrorException("Стойността на seq_pin трябва да е 9-цифрено число");
		}
		
		int[] weights = {2, 4, 8, 5, 10, 9, 7, 3, 6};
		char seqChars[] = seq.toCharArray();
		int sum = 0;
		
		for(int i = 0; i < 9; i++) {
			sum += Character.getNumericValue(seqChars[i]) * weights[i]; 
		}
		
		sum %= 11;
		sum %= 10;
		
		String s1 = seq.substring(0, 2);
		String s2 = seq.substring(2);
		
		return Long.parseLong(s1 + String.valueOf(sum) + s2);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> countCandidatesByApplyFor(Date dateFrom, Date dateTo ) throws DbErrorException {
		
		
		String sql = "select count (ua.user_id),ua.apply_for from jobs_users_add ua join adm_users u on(ua.user_id=u.user_id) " ;
		String where = " where ";
		String and = "";
		
		if(dateFrom!=null) {
			
			sql += where + and + " u.date_reg >= :dateFrom ";		
			where = "";
			and = " and ";
		}
		
		if(dateTo!=null) {
			sql += where + and + " u.date_reg <= :dateTo ";	
		}
		 
		sql += " group by apply_for ";			
		
		try {
			
			EntityManager em = JPA.getUtil().getEntityManager();
			
			Query query = em.createNativeQuery(sql);
			
			if(dateFrom!=null) {				
				query.setParameter("dateFrom", DateUtils.startDate(dateFrom));
			}
			
			if(dateTo!=null) {
				query.setParameter("dateTo", DateUtils.endDate(dateTo));
			}
	
			return query.getResultList();
			
			 
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на брой кандидати по вид", e);
		}
	}

	/**
	 * 
	 * @param username
	 * @param names
	 * @param pin
	 * @return намира потребител на системата по потр. име / имена / пин
	 */
	public SelectMetadata searchUser(String username, String names, String pin) {
		
		Map<String, Object> params = new HashMap<>();
		
		String select = "select a.user_id, a.username, a.names, ua.pin";
		
		String from = " from adm_users a";
		from += " left outer join jobs_users_add ua on a.user_id = ua.user_id";
		
		String where = "";
		
		if(username != null && username.length() > 0) {
			
			where += " where upper(a.username) like :username ";
			params.put("username", "%" + username.trim().toUpperCase() + "%" );
		}
		
		if(names != null && names.length() > 0) {
			
			if(where.length() == 0) {
				where += " where";
			} else {
				where += " and";
			}
			
			where += " upper(a.names) like :names";
			params.put("names", "%" + names.trim().toUpperCase() + "%" );
		}
		
		if(pin != null && pin.length() > 0) {
			if(where.length() == 0) {
				where += " where";
			} else {
				where += " and";
			}
			
			where += " ua.pin = :pin ";
			params.put("pin", pin.trim().toUpperCase());
		}
		
		SelectMetadata smd = new SelectMetadata();
		smd.setSql(select + from  + where);		
		smd.setSqlCount("select count(*)" + from + where);
		smd.setSqlParameters(params);
		
		return smd;
	}
	
}
