package indexbg.pjobs.system;

import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;

import org.jboss.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.oauth2.BaseJWTAuthService;

import indexbg.pjobs.db.dao.AdmUserDAO;
import indexbg.pjobs.db.dao.AdmUsersDAO;



@Path("/auth")
public class JWTAuthService extends BaseJWTAuthService {

	static final Logger LOG = Logger.getLogger(JWTAuthService.class);

	@Override
	protected void loadUserData(HttpSession session, JsonObject json) {
		UserData userData = new UserData(); 
		userData.setUserId(json.get("id").getAsLong());
		userData.setLoginName(json.get("login_name").getAsString());
		userData.setLiceNames(json.get("names").getAsString());
				
		JsonArray roles = json.get("roles").getAsJsonArray();
		
		HashMap<String, HashMap<String,Boolean>> rolesMap = new HashMap<>();
		for(JsonElement je: roles){
			HashMap<String,Boolean> tempMap = new HashMap<>();
			String key = je.getAsJsonObject().get("key").getAsString();
			JsonArray tempArray = je.getAsJsonObject().get("values").getAsJsonArray();
			for(JsonElement je2: tempArray){
				tempMap.put(je2.getAsString(), true);
			}
			rolesMap.put(key, tempMap);
		}
		LOG.info("USER ROLES: " + rolesMap.toString());
		userData.setAccessValues(rolesMap);
		
		if(json.get("type")!=null && json.get("type").isJsonPrimitive()){
			try {
				Long typeUser = json.get("type").getAsLong();
				userData.setTypeUser(typeUser);
			} catch (Exception e) {
				userData.setTypeUser(null);
			}
		}
		
		if(json.get("email")!=null){
			userData.setEmailUser(json.get("email").getAsString());
		}
		
		if(json.get("org_code")!=null && json.get("org_code").isJsonPrimitive()){
			try {
				Long orgCode = json.get("org_code").getAsLong();
				userData.setCodeOrg(orgCode);
			} catch (Exception e) {
				userData.setCodeOrg(null);
			}
		}
		
		userData.setFillProfil(false);
		userData.setApplyFor(-1L);
		if(userData.getTypeUser().longValue() == Constants.CODE_ZNACHENIE_TIP_POTR_VANSHEN){
						
			try {
				Long applyFor = new AdmUserDAO(userData.getUserId()).checkFillProfileApplyFor(userData.getUserId());
				
				if(applyFor!=null) {
					userData.setFillProfil(true);
					userData.setApplyFor(applyFor);
				}
			}catch (DbErrorException e) {
				LOG .error(e.getMessage(),e);
			} catch (Exception e) {
				LOG .error(e.getMessage(), e);
			} finally {
				JPA.getUtil().closeConnection();
			}
						
			
		} else if(userData.getTypeUser().longValue() == Constants.CODE_ZNACHENIE_TIP_POTR_VATR){
			userData.setFillProfil(true); // nqma kakwo da popylwat moderatorite
			
			userData.setTypeUser(Constants.CODE_ZNACHENIE_TYPE_USER_MODERATOR);
			
			if(userData.hasAccess(7L, 76L)){ 
				userData.setTypeUser(Constants.CODE_ZNACHENIE_TIP_POTR_VATR);
			}
			
			//ще измъкваме звеното (не е направено при логването защото ползваме ААServer -а то е специфично за проекта)
			try {
				userData.setZveno(new AdmUsersDAO(userData.getUserId()).getUserAdmZveno());
			}catch (DbErrorException e) {
				LOG .error(e.getMessage(),e);
			} catch (Exception e) {
				LOG .error(e.getMessage(), e);
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
		
		session.setAttribute("userData", userData);
		
	}


}
