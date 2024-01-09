package indexbg.pjobs.db.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.BaseSystemData;
import com.indexbg.system.H2DataContainer;
import com.indexbg.system.SysConstants;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.db.dto.SystemClassifOpis;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectInUseException;
import com.indexbg.system.model.ModelData;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.DialectConstructor;
import com.indexbg.system.utils.SearchUtils;

import indexbg.pjobs.db.AdmGroups;
import indexbg.pjobs.db.AdmUser;
import indexbg.pjobs.db.AdmUsers;
import indexbg.pjobs.db.Subscription;
import indexbg.pjobs.db.UserAdd;
import indexbg.pjobs.db.UserEducation;
import indexbg.pjobs.db.UserExperience;
import indexbg.pjobs.db.UserLanguage;
import indexbg.pjobs.db.UserStudent;
import indexbg.pjobs.db.UsersBursar;
import indexbg.pjobs.system.Constants;

/**
 * 
 * DAO for  AdmUsers
 *
 */

public class AdmUsersDAO extends TrackableDAO <AdmUsers>  {

	static final Logger LOGGER = LoggerFactory.getLogger(AdmUsersDAO.class);

	private static final long CODE_DEIN_IZTRIVANE = 0;
	
	private BaseSystemData sd = null;
	
	public AdmUsersDAO(Long userId) {
		super(userId);
	}
	
	public AdmUsersDAO(Long userId, BaseSystemData sdata) {
		super(userId);		
		this.sd = sdata;		
	}
	
	/**Търси потребител по зададен логин и парола
	 * @param login 
	 * @param pass
	 * @param active 1-търси активиран потребител, 2 - търси неактивен , 0 без оглед на активен/неактувен
	 * @return NULL ако не е намерен такъв потребител
	 * @throws DbErrorException 
	 */
	public  AdmUsers findByLoginNameAndPass(String login, String pass,int active) throws DbErrorException {
		AdmUsers user = null;
	
		StringBuffer sql=new StringBuffer();
		sql.append(" FROM AdmUsers WHERE username = ?1 ");

		sql.append("' AND password = ?2 ");

		sql.append("' ");
		switch (active) {
		case 1:
			//   Активен потребител - разрешен достъп
			sql.append(" AND status= "+Constants.CODE_ZNACHENIE_ACT);  // От класификация CODE_CLASSIF_STATUS_POTREB  (класификация 9)
			break;
		case 2:   // Неактивен потребител (неактивен, или в процес на регистрация, или заключен )
			//
			sql.append(" AND status <> " +Constants.CODE_ZNACHENIE_ACT);
			break;
		default:
			break;
		}
		
		Query query = createQuery(sql.toString());

		query.setParameter(1, login.trim());
		query.setParameter(2, pass.trim());
	
		user = (AdmUsers) query.getSingleResult();
			
		return user;

	}
	
	/**Търси потребител по зададен логин - 
	 * @param login  - login name
	 * @return NULL ако не е намерен такъв потребител
	 */
	public  AdmUsers findByLoginName(String login)  throws DbErrorException {
		AdmUsers user = null;
		if (login == null){
			return user;
		}
		login = login.trim();
	
		StringBuffer sql=new StringBuffer();
		sql.append(" FROM  AdmUsers WHERE username = ?1 ");

		sql.append("' ");
	
		try {
			Query q =  createQuery(sql.toString());
			q.setParameter(1, login.trim());
			
			user = (AdmUsers) q.getSingleResult();
			
			return user;
		  }  catch (Exception e) {         // Throwable e
	            throw new DbErrorException("Възникна грешка при търсене на данни за потребител ! - " + e.getMessage() + " - " + e.getCause().getMessage(), e);

			} finally {
				JPA.getUtil().closeConnection();
			} 	

	}
	
	/**Търси потребител по зададено idUser  - с имена и разкодиране за езика, с който работи потребителят
	 * @param  idUser  - id на потребител
	 * @param isText - дали да се ракодират текстове от номенклатурите - за езика на потребителя
	 * @return NULL ако не е намерен такъв потребител
	 */
	public AdmUsers findByIdUser(Long idUser, boolean isText ) throws DbErrorException {
		if (idUser ==  null) {
			return null;
		}
		AdmUsers user = null;
	
		StringBuffer sql=new StringBuffer();
		sql.append(" FROM  AdmUsers WHERE  id = ?");
		
		try {
			Query q = createQuery(sql.toString());
			q.setParameter(1, idUser);
			
			user = (AdmUsers) q.getSingleResult();
			
			return user;
			
		  }  catch (Exception e) {         // Throwable e
	            throw new DbErrorException("Възникна грешка при търсене на данни за потребител ! - " + e.getMessage() + " - " + e.getCause().getMessage(), e);

			} finally {
				JPA.getUtil().closeConnection();
			} 	
	

	}
	
	/**
	 * Прочитане основни данни за потребител  от таблица ADM_USERS
	 *  @param idUser - id на потребител
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public AdmUsers   getDanniForUser (Long idUser)  throws DbErrorException {
		  if (idUser == null) {
			  return null;
		  }
		
		  // Прочитане на данни за потребител
		   String strSql = " select user_id, username, password, user_type, names, lang, status,  status_date, email,   phone, org_id, org_code, org_text, position, position_text  from adm_users where  user_id =  ?1 "  ;
		  		   
				   try {
					   Query q = createNativeQuery(strSql);	
					   q.setParameter(1, idUser);
						
						List<Object[]> rez = q.getResultList();
				 
						Object[] row  = rez.get(0);
							AdmUsers user = new AdmUsers();
							user.setId(SearchUtils.asLong(row[0]));
							user.setUsername(SearchUtils.asString(row[1]));
							user.setPassword(SearchUtils.asString(row[2]));
							user.setUserType(SearchUtils.asLong(row[3]));
							user.setNames(SearchUtils.asString(row[4]));
							user.setLang(SearchUtils.asLong(row[5]));
							user.setStatus(SearchUtils.asLong(row[6]));
							user.setStatusDate(SearchUtils.asDate(row[7]));
							user.setEmail(SearchUtils.asString(row[8]));
							user.setPhone(SearchUtils.asString(row[9]));
							user.setOrgId(SearchUtils.asLong(row[10]));
							user.setOrgCode(SearchUtils.asLong(row[11]));
							user.setOrgText(SearchUtils.asString(row[12]));
							user.setPosition(SearchUtils.asLong(row[13]));
							user.setPositionText(SearchUtils.asString(row[14]));
												
							  return user;
					  
				   }  catch (Exception e) {         // Throwable e
			            throw new DbErrorException("Възникна грешка при търсене на данни за потребител ! - " + e.getMessage() + " - " + e.getCause().getMessage(), e);
	
					} finally {
						JPA.getUtil().closeConnection();
					} 
				   
		       		
	}
	
	/**
	 *  Прочитане пълни данни за потребител 
	 * @param idUser - id на патребител 
	 * @return
	 * @throws DbErrorException
	 */
	public  AdmUsers getUserById (Long id) throws DbErrorException {
		  if (id == null){
			  return null;
		  }
		  AdmUsers user = this.findById(id);
		   if (user == null){
			   return null;
		   }
		   
		   return user;
	}
	
	/**
	 *  Прочитане данни за групи потребители, активни към днешна дата
	 * @return
	 * @throws DbErrorException
	 */
	public  List <AdmGroups>   getAllActGroups ()  throws DbErrorException {
		 
		  // Прочитане на данни за групи, актуални до момента
		String s_d = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
		String vendorN = JPA.getUtil().getDbVendorName();  // С каква база се работи
//		String dateToday = DialectConstructor.convertDateOnlyToSQLString(vendorN, new Date());   // Преобразуване като дата
		
		// date_do се сравнява като низ с днешната дата 
		// DialectConstructor.convertSQLDateToString(vendorN, "date_do") - дава низ 'dd.mm.yyyy' за date_do
		String strSql = " select group_id, group_name from adm_groups  where  (date_do is null or (date_do is not null and " + DialectConstructor.convertSQLDateToString(vendorN, "date_do") + " =  '" + s_d + "' )) ";
				  		   
		       List<AdmGroups> lstGr = new ArrayList<AdmGroups> ();
				   try {
					   Query q = createNativeQuery(strSql);	
					   
						
						@SuppressWarnings("unchecked")
						List<Object[]> rez = q.getResultList();
						if (rez != null && rez.size() > 0) {
							for (int i = 0; i < rez.size(); i++) {
						   		Object[] row  = rez.get(i);
						   		Long id = SearchUtils.asLong(row[0]);
						   		String n = SearchUtils.asString(row[1]);
						   		AdmGroups gr = new AdmGroups();
						   		gr.setId(id);
						   		gr.setGroupName(n);
						   		lstGr.add(gr);
							}
						} else return null;
						
					  
				   }  catch (Exception e) {
			            throw new DbErrorException("Възникна грешка при търсене на данни за групи потребители ! - " + e.getMessage() + " - " + e.getCause().getMessage(), e);
	
					} finally {
						JPA.getUtil().closeConnection();
					}
				   
		       		return lstGr; 
	}
	
	/**
	 * Прочитане данни за групи потребители, активни към днешна дата - items за избор
	 *  
	 * @return
	 * @throws DbErrorException
	 */
	public  List <SelectItem>   getAllActGroupsIzb ()  throws DbErrorException {
		 
		 List <AdmGroups> grList = getAllActGroups ();       // Прочитане на данни за групи, актуални до момента
		 List<SelectItem> lstGr = new ArrayList<SelectItem> (); 
		if (grList!= null && !grList.isEmpty()) {
			for (AdmGroups gr:grList) {
				SelectItem item =  new SelectItem(gr.getId().toString(), gr.getGroupName());
				lstGr.add(item);
			}
			
		}
		
		return lstGr;
	
	}
	

	/**
	 *   Получаване пълен HashMap с роли за потребител - код на класификация, HashMap с ролите за тази класификация
	 * @param idUser                                         id на потребител
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public Map <Long, Map<Long, Long>> getAllRolesForUser (Long idUser) throws DbErrorException {
		if (idUser == null)   return null;
		
		Map <Long, Map<Long, Long>>  allRoles = new HashMap <Long, Map<Long, Long>>();
		Map<Long, Long> rolesClasif = new  HashMap<Long, Long> ();
		
		// Първо по idUser се намират ролите за всички групи на потребителя и се формират по класификации
		 String strSql = " select  distinct  gr.code_classif, gr.code_role "
		 		+ " from adm_group_roles gr, adm_user_group us "
				+ " where us.group_id = gr.group_id  and us.user_id = ?1 ";
			
			// След това по idUser се намират индивидуалните роли за потребителя по класификации
			
			 String strSql2 = " select  distinct  us.code_classif, us.code_role "
				 		+ " from adm_user_roles us "
						+ " where  us.user_id = ?1 ";
			 
			 try {
				   Query q = createNativeQuery(strSql);	
				   q.setParameter(1, idUser);
					
					List<Object[]> rez = q.getResultList();
			
					if (rez != null && rez.size() > 0) {
						for (int i = 0; i < rez.size(); i++) {
							Object[] row  = rez.get(i);
					         Long codeCl = 	SearchUtils.asLong(row[0]);
					         Long role      =    SearchUtils.asLong(row[1]);
						     if (!allRoles.containsKey(codeCl))
						    	 rolesClasif = new  HashMap<Long, Long> ();
						     else  rolesClasif =  allRoles.get(codeCl);
						     if (!rolesClasif.containsKey(role)) {
						    	 rolesClasif.put(role,  role);
						    	 allRoles.put(codeCl, rolesClasif );
						     }
						}
						
					}
					
					
					   q = createNativeQuery(strSql2);	
					   q.setParameter(1, idUser);
						
						 rez = q.getResultList();
				
						if (rez != null && rez.size() > 0) {
							for (int i = 0; i < rez.size(); i++) {
								Object[] row  = rez.get(i);
						         Long codeCl = 	SearchUtils.asLong(row[0]);
						         Long role      =    SearchUtils.asLong(row[1]);
							     if (!allRoles.containsKey(codeCl))
							    	 rolesClasif = new  HashMap<Long, Long> ();
							     else  rolesClasif =  allRoles.get(codeCl);
							     if (!rolesClasif.containsKey(role)) {
							    	 rolesClasif.put(role,  role);
							    	 allRoles.put(codeCl, rolesClasif );
							     }
							}
							
						}
					
			   }  catch (Exception e) {
		            throw new DbErrorException("Възникнала  грешка при търсене на данни за роли за потребител  ! - " + e.getMessage() + " - " + e.getCause().getMessage(), e);

				} finally {
					JPA.getUtil().closeConnection();
				}
		
	    
			 return allRoles;
		 
		 
	}
	

	
	/**
	 * Формиране SQL за филтър за търсене на потребители
	 * 
	 * @param dateOt       - Период за дата регистрация за потребители
	 * @param dateDo       -
	 * @param userN        - login name
	 * @param isEqLogin    - Дали да се търси за точно съвпадение за login name
	 * @param imena        - имена
	 * @param typePotrList - тип потребител - включени са и поднива за даден тип
	 * @param stat         - Статус
	 * @param grupiList    - Списък с указани групи
	 * @param admStr       - списък с указани нива от класификация за работно място (или адм. структура) за атрибут workAdmCode - ще се търсят потребители и за поднивата
	 * @param lang         - език, който работи потребителят
	 * @param pin          - ПИН
	 * @return
	 */
	public SelectMetadata filterForUsers(Date dateOt, Date dateDo, String userN, boolean isEqLogin, String imena, List<Long> typePotrList, Long stat, List<Long> grupiList, List<Long> admStr, Long lang, Long pin) {

		HashMap<String, Object> params = new HashMap<String, Object>();

		String sql = "SELECT  us.user_id id, us.username loginn, us.names unames, us.email mail,  ";
		sql += " us.user_type tip, us.status stat, us.lang lang, us.date_reg dreg, ";
		sql += " us.org_code astr, us.org_text orgtext, ua.pin uapin";

		String from = " from adm_users us ";

		from += " LEFT OUTER JOIN jobs_users_add ua ON ua.user_id = us.user_id ";

		String where = " where 1=1 ";
		
		// Интервал за дата регистрация
		if (dateOt != null || dateDo != null) {
			if (dateOt != null) {
				dateOt = DateUtils.startDate(dateOt);
				where += " and us.date_reg >= :d1 ";
				params.put("d1", dateOt);
			}

			if (dateDo != null) {
				dateDo = DateUtils.endDate(dateDo);
				where += " and us.date_reg <=  :d2 ";
				params.put("d2", dateDo);

			}
		}
		
		if (userN != null) {
			userN = userN.trim();
			if (!userN.isEmpty()) {
				if (!isEqLogin) {					
					where += " and upper(us.username) like :userN ";
					params.put("userN", "%" + userN.trim().toUpperCase() + "%"); 
					
				} else {
					where += " and us.username = :userN ";
					params.put("userN", userN.trim());
				}
			}
		}
		
		if (imena != null) {
			imena = imena.trim();
			if (!imena.isEmpty()) {
				String s = "";
				String[] str = imena.split(" ");

				if (str != null && str.length > 1) {
					String sss = "";
					int k = 0;
					for (int i = 0; i < str.length; i++) {
						if (str[i] == null || str[i].trim().isEmpty()) {
							continue;
						} else {
							str[i] = str[i].trim();
						}
						if (str[i].length() >= 1) {
							k++;

							if (k > 1) {
								sss += " AND  ";
							}
							sss += " UPPER (us.names) LIKE :str[i] ";
							params.put("str[i]", "%" + str[i].trim().toUpperCase() + "%" );
						}
					} // Край на for

					if (sss.length() > 0) {
						sss = "(" + sss + ")";
						s += " and " + sss;

					} else {
						s += " and " + " UPPER(us.names) LIKE :imena ";
						params.put("imena", "%" + imena.trim().toUpperCase() + "%" );
					}
				} else {
					s += " and " + " UPPER(us.names) LIKE :imena ";
					params.put("imena", "%" + imena.trim().toUpperCase() + "%" );
				}

				if (!s.isEmpty()) {
					where += s;
				}
			}
		}
		
		if (typePotrList != null && !typePotrList.isEmpty()) {
			String s = "";
			for (int i = 0; i < typePotrList.size(); i++) {
				if (!s.isEmpty())
					s += ",";
				s += String.valueOf(typePotrList.get(i));
			}
			if (!s.isEmpty()) {
				s = "(" + s + ") ";
				where += " and us.user_type  in " + s + " ";
			}
		}
		
		if (stat != null && stat.longValue() > 0) {
			where += " and us.status = :stat ";
			params.put("stat", stat);
		}

		// Връзка със зададени групи
		if (grupiList != null && grupiList.size() > 0) {
			String s = "";
			for (int i = 0; i < grupiList.size(); i++) {
				if (!s.isEmpty())
					s += ",";
				s += String.valueOf(grupiList.get(i));
			}
			
			if (!s.isEmpty()) {
				s = "(" + s + ") ";
				from += " inner join adm_user_group gr on gr.user_id = us.user_id ";
				where += " and  gr.group_id in " + s + " ";
			}
		}

		if (admStr != null && !admStr.isEmpty()) {
			String s = "";
			for (int i = 0; i < admStr.size(); i++) {
				if (!s.isEmpty())
					s += ",";
				s += String.valueOf(admStr.get(i));
			}
			if (!s.isEmpty()) {
				s = "(" + s + ") ";
				where += " and us.org_code  in " + s + " ";
			}
		}

		if (pin != null) {
			where += " and ua.pin = :pin ";
			params.put("pin", pin);
		}

		SelectMetadata smd = new SelectMetadata();

		smd.setSql(sql + from + where);
		smd.setSqlCount("SELECT COUNT(distinct us.user_id) as counter  " + from + where);
		smd.setSqlParameters(params);

		return smd;

	}
	
	
	/**
	 *  Генериране на списък с данни за LazyDataModelSQL2Array обект
	 * @param smd                                    Обект SelectMetadata                         
	 * @param defaultSortColumn             име на колона за пэрвоначално сортиране
	 * @return
	 * @throws DbErrorException
	 */
	public LazyDataModelSQL2Array newLazyDataModel (SelectMetadata smd , String defaultSortColumn ) throws DbErrorException { 
	   
		if (smd == null) return null;
		 LazyDataModelSQL2Array list = null;
		try {
							
				list = new LazyDataModelSQL2Array(smd, defaultSortColumn);  
				
		} catch (DbErrorException e) { 
			e.printStackTrace();
			throw new DbErrorException ("Грешка при изпълнение на търсене на записи за  LazyDataModelSQL2Array обект! - " +e.getMessage() + " -" + e.getCause().getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
	
		return list;
	}
	
	/**
	 *  Връща MAP със системните имена на активните атрибути на обекта потребител 
	 * @param h2                                            H2DataContainer
	 * @param lang                                          Език
	 * @return
	 * @throws DbErrorException
	 */
	public Map<String, Boolean> getSysnamesUser (ModelData md, H2DataContainer h2, Long lang) throws DbErrorException  {
		
		Map<String, Boolean> strM = new HashMap<String, Boolean> ();
		if (md == null && h2 == null)  return null;
		if (md == null) {
			
			  try {
				  md = new ModelData(h2);
			  } catch (Exception e) { // има проблем с създаването на таблиците
						LOGGER.error(e.getMessage(), e);
						throw new DbErrorException ("Грешка при създаване таблици на модела при получаване системните имена на атрибутите за обект потребител!");
			 }
		 }
		
		   List<String> strL = md.getAttributeSysnames(SysConstants.SYSNAME_OBJECT_POTREBITEL, lang, null);    // По код на обекта се получават действащи атрибути за него
		   if (strL != null && strL.size() > 0) {
			   for (String item: strL) {
				   strM.put(item,  Boolean.TRUE);
			   }
			   
		   }
		   
		   return strM;
	}
	
	
	/**
	 * Връща списък със стойности на редове от класификация за избор - трябва да се подаде адрес на SystemData
	 * @param codeClassif  - код на класификация
	 * @param sdata - адрес на SystemData
	 * @param dat - дата, към която се получава класификацията
	 * @param lang - език за прекодиране за класификацията
	 * @param userId - текущ потребител
	 * @return
	 * @throws DbErrorException
	 */
	 public List <SelectItem> getClassifForIzbor (Long  codeClassif,  BaseSystemData sdata,  Date dat, Long lang, Long userId) throws   DbErrorException  {
		 if (sdata == null) sdata = this.sd;
		 if (sdata == null)  return null;
		 if (userId == null) userId = getUserId();
		 if (userId == null) return null;
		 if (lang == null) lang = Long.valueOf(Constants.CODE_DEFAULT_LANG) ;
		 if (dat == null) dat = new Date();
		 
		 List<SystemClassif>  classif  = sdata.getSysClassification(codeClassif, dat, lang, userId);
				 
		 List<SelectItem> listIzbor = new ArrayList<SelectItem> ();
		 if (classif != null && classif.size() > 0) {
			  for (int i = 0; i  < classif.size(); i++ )  {
				  SystemClassif cl = classif.get(i);
				  SelectItem item = new SelectItem (String.valueOf(cl.getCode()), cl.getTekst());
				  listIzbor.add(item);
				  
			  }
			 
		 }
				 
		 return listIzbor;
	 }
	 
	 /**
	  *   Допълване на списък с нива от класификация с всички поднива за тези нива
	  * @param niva                                        списък с нива от класификация
	  * @param codeClassif                            код на класификацията
	  * @param sdata                                     Адрес на BaseSystemData за системата
	  * @param dat                                         Дата
	  * @param lang                                       Eзик
	  * @param userId                                    Текущ потребител
	  * @return
	  * @throws DbErrorException
	  */
	 public List<Long> getPodNivaFromClassifForIzbor (List<Long> niva,  Long  codeClassif,  BaseSystemData sdata,  Date dat, Long lang, Long userId) throws   DbErrorException  {
		 if (niva == null || niva.isEmpty())  return null;
		 if (sdata == null) sdata = this.sd;
		 if (sdata == null)  return niva;
		 if (userId == null) userId = getUserId();
		 if (userId == null) return niva;
		 if (lang == null) lang = Long.valueOf(Constants.CODE_DEFAULT_LANG) ;
		 if (dat == null) dat = new Date();
		 
				 
		 List <Long> nivaAdd = new ArrayList<Long> ();
		 
		 for (int i = 0; i < niva.size(); i++) {
			    Long code = niva.get(i);
			    nivaAdd.add(code);
			    // Получаване и запис на поднивата в класификацията
			 	List<SystemClassif>  sysclL = sdata.getChildren(codeClassif.longValue(),  code.longValue(), dat , lang, userId);
			 	if (sysclL != null && sysclL.size() > 0) {
			 		for (SystemClassif item: sysclL) {
			 			nivaAdd.add(item.getCode());
			 		}
			 	}
		 }
		 
		 return nivaAdd;
	 }
	
	 /**
	  *   Имена на всички класификации в системата
	  * @param sdata                            BaseSystemData
	  * @param lang                              Език
	  * @return
	  * @throws DbErrorException
	  */
	 public Map<Long, String>  getNamesForAllClassif (BaseSystemData sdata, Long lang) throws DbErrorException {
		 if (sdata == null)  sdata = this.sd;
		 if (sdata == null)  return null;
		 if (lang == null) lang = Long.valueOf(Constants.CODE_DEFAULT_LANG) ;
		 
		 LinkedHashMap <Long, String>  namesCl = new LinkedHashMap<Long, String> ();
		 
		 List<SystemClassifOpis> list =  sdata.getAllClassifications( lang);
		 if (list != null && list.size() > 0) {
			 for (SystemClassifOpis item: list) 
				 namesCl.put(item.getId(), item.getTekst());
		 }
		 
		 return namesCl;
		 
	 }
	 
	 /**
	  *  Класификации за роли на потребител -  те се зададени м модела като параметър - кодове, разделени със запетаи
	  * @param sdata                                    BaseSystemData
	  * @param namesAllClassif                  Map с имена за всички класификации за език
	  * @return
	  * @throws DbErrorException
	  */
		public Map<Long, String>  getClassifForPotrebRoles (BaseSystemData sdata, Map <Long, String> namesAllClassif)  throws DbErrorException {
			if (sdata == null)  sdata = this.sd;
			if (sdata == null)  return null;
			
			LinkedHashMap <Long, String>  classif = new LinkedHashMap<Long, String> ();
			String  clList = null;
			try {
			   clList = sdata.getSettingsValue(Constants.OPTION_CLASSIF_ACCESS_CONTROL);    // OPTION_CLASSIF_ACCESS_CONTROL - задава име на параметъра
			  }  catch (Exception e) {
		            throw new DbErrorException("Възникнала  грешка при търсене на данни за класификации за роли за потребител  ! - " + e.getMessage() + " - " + e.getCause().getMessage(), e);

				} finally {
					JPA.getUtil().closeConnection();
				} 
			if (clList != null) {
				clList = clList.trim();
				if (clList.isEmpty()) clList = null;
			}
			if (clList == null )  return classif;
			  String[]	str = clList.split(",");             // Получаване кодове на класификации за роли на потребители
		    	
	         	if (str != null && str.length > 0) { 
	             
	         		for (int i = 0; i < str.length; i++) {
	         			if (str[i] == null || str[i].trim().isEmpty()){
	           				continue;
	           			}else{
	           				str[i] = str[i].trim();
	           			}
	         			
	         			Long codeClassif = null;
	         			try {
	         				 codeClassif = Long.valueOf(str[i]) ;
	         			} catch (Exception e1) {  // Грешка при преобразуване
	         				continue;
	         			}
	         			if (codeClassif == null)  continue;
	         			         			
	         			classif.put(codeClassif, namesAllClassif.get(codeClassif));
	         			
	         		}
	         	}		
			
			return classif;
		}
	 

		/**
		 *  Получаване списък за избор от предварително формиран MAP с имена  
		 * @param mIzbor
		 * @return
		 */
		public List<SelectItem> setSelectItemList (Map <Long, String> mIzbor)  {
			   if (mIzbor == null || mIzbor.size() == 0)  return null;
			   Set<Long> k =  mIzbor.keySet();
			   Iterator<Long> it = k.iterator();
			   List<SelectItem>  list = new ArrayList<SelectItem> ();
			   while (it.hasNext()) {
				   Long  key = (Long)it.next();
				   String n = mIzbor.get(key);
				   list.add(new SelectItem(key.toString(), n)); 
			   }
			
			   return list;
		}
		
		/**
		 *  Дали има дублиране на нововъдено username
		 * @param username
		 * @param idUser
		 * @return
		 * @throws DbErrorException
		 */
		public boolean checkForDublUserName(String username,Long idUser) throws DbErrorException {
			
			 try {   
									
					String sql = " SELECT USER_ID FROM ADM_USERS WHERE  USERNAME =:loginName ";
					
					if(idUser!=null){
						sql+=" AND USER_ID <> " + idUser;
					}
					
					
						   Query q = createNativeQuery(sql);	
						   q.setParameter("loginName", username);
							
							@SuppressWarnings("unchecked")
							List<Object[]> rez = q.getResultList();
					
							if (rez != null && rez.size() > 0) {
								return true;
							}else{
								return false;
							}
								
			  }  catch (Exception e) {
		            throw new DbErrorException("Възникнала  грешка при определяне дали има дублиране за username  ! - " + e.getMessage() + " - " + e.getCause().getMessage(), e);

				} finally {
					JPA.getUtil().closeConnection();
				}

		} 
	
		/**
		 *  Изтриване на ролите за индивидуален достъп за потребител-
		 * @param idUser                        id на потребител
		 * @param commitYes - да завършва ли транзакцията (true)
		 * @throws DbErrorException
		 */
		public void deleteRolesForUser (Long idUser, boolean commitYes) throws DbErrorException {
	          if (idUser == null)  return;
			String sql = " delete from  adm_user_roles   where user_id = ?1 ";
			
		 	try {
						if (commitYes)	JPA.getUtil().begin();	
						   Query q = createNativeQuery(sql);	
						   q.setParameter("1", idUser);
						   q.executeUpdate();
								
						   if (commitYes) JPA.getUtil().commit();
											
								
					} catch (DbErrorException e) {
						if (commitYes) JPA.getUtil().rollback();
						LOGGER.error("Грешка при изтриване на роли за индивидуален достъп на записан потребител с id = " + idUser + " ! ", e);
				 
					      throw new DbErrorException ("Грешка при изтриване на роли за индивидуален достъп на записан потребител  в базата !" + " - " + e.getLocalizedMessage());
					} catch (Exception e) {
						if (commitYes) JPA.getUtil().rollback();
						LOGGER.error("Грешка при изтриване на роли за индивидуален достъп на записан потребител  с id = " + idUser + " ! ", e);
						  throw new DbErrorException ("Грешка при изтриване на роли за индивидуален достъп на записан потребител  в базата !" + " - " + e.getLocalizedMessage());
					} finally {
						if (commitYes) JPA.getUtil().closeConnection();
						
					}
		}
		
		/**
		 *  Актуализация парола на потребител в базата-
		 * @param newPass                   нова парола
		 * @param idUser                        id на потребител
		 * @throws DbErrorException
		 */
		public void setNewPassword (String newPass, Long idUser) throws DbErrorException {
			if (newPass != null) {
				newPass = newPass.trim();
				if (newPass.isEmpty())  newPass = null;
			}
			if (newPass == null || idUser == null)  return;
			String sql = " update adm_users set password = ?1  where user_id = ?2 ";
			
		 	try {
						
						JPA.getUtil().begin();	
						   Query q = createNativeQuery(sql);	
						   q.setParameter("1", newPass);
						   q.setParameter("2", idUser);
						   q.executeUpdate();
								
						JPA.getUtil().commit();
											
								
					} catch (DbErrorException e) {
						JPA.getUtil().rollback();
						LOGGER.error("Грешка при актуализация на парола на записан потребител с id = " + idUser + " ! ", e);
				 
					      throw new DbErrorException ("Грешка при опит за корекция на парола на потребител в базата !" + " - " + e.getLocalizedMessage());
					} catch (Exception e) {
						JPA.getUtil().rollback();
						LOGGER.error("Грешка при актуализация на парола на записан потребител с id = " + idUser + " ! ", e);
						  throw new DbErrorException ("Грешка при опит за корекция на парола на потребител в базата !" + " - " + e.getLocalizedMessage());
					} finally {
						JPA.getUtil().closeConnection();
						
					}
			
		}

		
		public Long getUserAdmZveno() throws DbErrorException {
			
			
			String sql = "select org_zveno from adm_users where user_id =:id";
					
			try {
				EntityManager em = JPA.getUtil().getEntityManager();
				Query query = em.createNativeQuery(sql);
				query.setParameter("id", getUserId());
				
				@SuppressWarnings("unchecked")
				List<Object> resultList = query.getResultList();
				
				if(resultList == null || resultList.size() == 0) {
					return null;
				} else {
					Object rez = resultList.get(0);
					return SearchUtils.asLong(rez);
					
				}
			} catch (Exception e) {
				throw new DbErrorException("Грешка при зареждане на звено на потребител", e);
			}
		}
		
	public void delete(AdmUsers entity) throws DbErrorException, ObjectInUseException {
		
		try {
			
			Integer cnt;
			Query query;
			
			// ADM_USER_CERTS.USER_ID 
			query = createNativeQuery(" delete from ADM_USER_CERTS where USER_ID = :userId ");
			query.setParameter("userId", entity.getId());
			cnt = query.executeUpdate();
			LOGGER.debug("Delete {} ADM_USER_CERTS for USER_ID={} ", cnt, entity.getId());
			
			//ADM_USER	
			AdmUser user = new AdmUserDAO(getUserId()).findById(entity.getId());
			
			// JOBS_USERS_ADD.USER_ID 
			UserAdd userAdd = user.getUserAdd();
			if (userAdd != null) {
				query = createNativeQuery(" delete from JOBS_USERS_ADD where USER_ID = :userId ");
				query.setParameter("userId", user.getId());
				cnt = query.executeUpdate();
				LOGGER.debug("Delete {} JOBS_USERS_ADD for USER_ID={} ", cnt, user.getId());
			}
			
			// JOBS_USERS_STUDENT.USER_ID 
			UserStudent userStudent = user.getUserStudent();
			if (userStudent != null) {				
				query = createNativeQuery(" delete from JOBS_USERS_STUDENT where USER_ID = :userId ");
				query.setParameter("userId", user.getId());
				cnt = query.executeUpdate();
				LOGGER.debug("Delete {} JOBS_USERS_STUDENT for USER_ID={} ", cnt, user.getId());
			}
			
			// JOBS_USERS_EDUCATION.USER_ID 
			List<UserEducation> userEducation = user.getUserEducation();
			if (!userEducation.isEmpty()) {					
				query = createNativeQuery(" delete from JOBS_USERS_EDUCATION where USER_ID = :userId ");
				query.setParameter("userId", user.getId());
				cnt = query.executeUpdate();
				LOGGER.debug("Delete {} JOBS_USERS_EDUCATION for USER_ID={} ", cnt, user.getId());					
			}
			
			// JOBS_USERS_EXPERIENCE.USER_ID 
			List<UserExperience> userExperience = user.getUserExperience();
			if (!userExperience.isEmpty()) {					
				query = createNativeQuery(" delete from JOBS_USERS_EXPERIENCE where USER_ID = :userId ");
				query.setParameter("userId", user.getId());
				cnt = query.executeUpdate();
				LOGGER.debug("Delete {} JOBS_USERS_EXPERIENCE for USER_ID={} ", cnt, user.getId());					
			}
			
			// JOBS_USERS_LANGUAGES.USER_ID 
			List<UserLanguage> userLanguage = user.getUserLanguages();
			if (!userLanguage.isEmpty()) {					
				query = createNativeQuery(" delete from JOBS_USERS_LANGUAGES where USER_ID = :userId ");
				query.setParameter("userId", user.getId());
				cnt = query.executeUpdate();
				LOGGER.debug("Delete {} JOBS_USERS_LANGUAGES for USER_ID={} ", cnt, user.getId());					
			}
			
			// JOBS_USERS_BURSAR.USER_ID 
			UsersBursar userBursar = new UsersBursarDAO(getUserId()).findByIdUser(user.getId());
			if (userBursar != null) {
				query = createNativeQuery(" delete from JOBS_USERS_BURSAR where USER_ID = :userId ");
				query.setParameter("userId", user.getId());
				cnt = query.executeUpdate();
				LOGGER.debug("Delete {} JOBS_USERS_BURSAR for USER_ID={} ", cnt, user.getId());
			}				
			
			// JOBS_USERS_TESTS.USER_ID 
//			List<UsersTests> usersTests = new AdmUsersTestsDAO(getUserId()).getUserTests(user.getId());
//			if (!usersTests.isEmpty()) {
//				for (UsersTests ut : usersTests) {						
//					// JOBS_USERS_RESULTS 
//					query = createNativeQuery(" delete from JOBS_USERS_RESULTS where JOBS_USERS_TESTS_ID = :usersTestsId ");
//					query.setParameter("usersTestsId", ut.getId());
//					cnt = query.executeUpdate();
//					LOGGER.debug("Delete {} JOBS_USERS_RESULTS for JOBS_USERS_TESTS_ID={} ", cnt,  ut.getId());
//				}
//			}
//			
//			query = createNativeQuery(" delete from JOBS_USERS_TESTS where USER_ID = :userId ");
//			query.setParameter("userId", user.getId());
//			cnt = query.executeUpdate();
//			LOGGER.debug("Delete {} JOBS_USERS_TESTS for USER_ID={} ", cnt, user.getId());
			
			// JOBS_SUBSCRIPTION.USER_ID 
			List<Subscription> subscriptions = new SubscriptionDAO().findByUserId(entity.getId());
			if (!subscriptions.isEmpty()) {
				query = createNativeQuery(" delete from JOBS_SUBSCRIPTION where USER_ID = :userId ");
				query.setParameter("userId", entity.getId());
				cnt = query.executeUpdate();
				LOGGER.debug("Delete {} JOBS_SUBSCRIPTION for USER_ID={} ", cnt, entity.getId());
			}	
			
			// JOBS_SUBSCRIPTION_RESULTS.USER_ID 
			query = createNativeQuery(" delete from JOBS_SUBSCRIPTION_RESULTS where USER_ID = :userId ");
			query.setParameter("userId", entity.getId());
			cnt = query.executeUpdate();
			LOGGER.debug("Delete {} JOBS_SUBSCRIPTION_RESULTS for USER_ID={} ", cnt, entity.getId());						
			
			SystemJournal j = new SystemJournal();
			Date date = new Date();

			j.setCodeAction(CODE_DEIN_IZTRIVANE);
			j.setCodeObject(Constants.CODE_OBJECTS_USERS);
			j.setDateAction(date);
			j.setIdentObject("Премахване на данните от свързаните таблици с потребителя, който юе се трие");
			j.setIdObject(entity.getId());
			j.setIdUser(getUserId());

			getEntityManager().persist(j);
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти с потребител!", e);
		}
		
		// Накрая се трие целия обект
		super.delete(entity);
		
	}
	
	public SelectMetadata searchUsersForDelete(Long applyFor, Date dateOt, Date dateDo) {
		
		HashMap<String, Object> params = new HashMap<String, Object>();

		String select = " SELECT u.user_id id, u.username username, u.names unames, ";
		select += " u.email email, u.status status, u.date_reg datereg ";

		String from = " FROM adm_users u ";
		from += " LEFT OUTER JOIN jobs_users_add ua on u.user_id = ua.user_id";

		String where = " WHERE ua.apply_for = " + Constants.CODE_ZNACH_CANDIDATE_BURSARY;
		//String where = " WHERE ua.apply_for = " + applyFor;

		// Интервал за дата регистрация
		if (dateOt != null || dateDo != null) {
			if (dateOt != null) {
				dateOt = DateUtils.startDate(dateOt);
				where += " and u.date_reg >= :d1 ";
				params.put("d1", dateOt);
			}

			if (dateDo != null) {
				dateDo = DateUtils.endDate(dateDo);
				where += " and u.date_reg <=  :d2 ";
				params.put("d2", dateDo);
			}
		}

		SelectMetadata smd = new SelectMetadata();
		smd.setSql(select + from + where);
		smd.setSqlCount("select count(*)" + from + where);
		smd.setSqlParameters(params);

		return smd;
	}
	
	
	/**
	 * Извежда списък с активни администратори на студентски стажове по ид на група за стажове и с въведен имейл адрес 
	 * @param dateTo
	 * @return List<Object[]> users[] {user_id, email ,administration ,unit} 
	 * @throws DbErrorException 
	 */
	public  List<Object[]> findAdmPractices(Long groupPract) throws DbErrorException{
		
		try {
			
			StringBuilder sql  = new StringBuilder("SELECT U.user_id ,U.email ,U.org_code ,U.org_zveno , U.names ");
			sql.append(" FROM adm_users U JOIN adm_user_group UG ON U.user_id = UG.user_id AND UG.group_id =:groupPract ");
			sql.append(" WHERE U.status =:stat AND U.email IS NOT NULL "); 
			sql.append(" ORDER BY U.org_code ,U.org_zveno  ");
			
			
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString());
			q.setParameter("stat",Constants.CODE_ZNACHENIE_ACT); //активен потребител
		    q.setParameter("groupPract",groupPract);
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.getResultList();
			
			return result;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на обяви на които е изтекъл срока за кандидатстване!", e);
		}
		
	}
	
}
