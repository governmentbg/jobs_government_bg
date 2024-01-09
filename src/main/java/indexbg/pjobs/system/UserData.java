package indexbg.pjobs.system;



import com.indexbg.system.BaseUserData;


public class UserData extends BaseUserData {
	
	private static final long serialVersionUID = 2989532705334584877L;
	
	
	/** тип потребител : външен /вътрешен*/
    private Long typeUser;
    
    /** електронна поща на потребител */
    private String emailUser;
    
    /** организация на потребителя*/
    private Long codeOrg;
    
    /** Адм. звено на вътрешни потребители*/
    private Long zveno;
	
    /** маркер за попълнен профил след логване*/
	private boolean fillProfil;
	
	/** кандидат за  (използва се за външни потребители) -1 ако е вътрешен или няма зададено*/
	private Long applyFor=-1L;
	
	/**
	 *  метод който гослагаме на всяка страница за да се поверява дали даденият потребител има достъп до нея
	 *  
	 *  <f:metadata>
	 *       <f:viewAction action="#{userData.hasAccessPage(7,614)}" />		   
	 *  </f:metadata>
	 *  
	 * */
	public String hasAccessPage(Long code_classif, Long codeAccess){ 
		
		
		if(!super.hasAccess(code_classif, codeAccess)) {
			return "index";
		} 
//		else {
//			if(!fillProfil) {
//				return "profile";
//			}
//			
//		}
		
		
		return null;
	}
	
	/**
	 *  метод който гослагаме на index страницата за проверка на попълнен профил след логване
	 *  
	 *  <f:metadata>
	 *       <f:viewAction action="#{userData.hasFillProfil()}" />		   
	 *  </f:metadata>
	 *  
	 * */
	public String hasFillProfilе() {
		if(super.getUserId()!=null && !fillProfil) {
			return "profile";
		}
		
		return null;
	}


	public Long getTypeUser() {
		return typeUser;
	}



	public void setTypeUser(Long typeUser) {
		this.typeUser = typeUser;
	}



	public String getEmailUser() {
		return emailUser;
	}



	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}



	public Long getCodeOrg() {
		return codeOrg;
	}



	public void setCodeOrg(Long codeOrg) {
		this.codeOrg = codeOrg;
	}



	public boolean isFillProfil() {
		return fillProfil;
	}



	public void setFillProfil(boolean fillProfil) {
		this.fillProfil = fillProfil;
	}

	public Long getZveno() {
		return zveno;
	}

	public void setZveno(Long zveno) {
		this.zveno = zveno;
	}

	public Long getApplyFor() {
		return applyFor;
	}

	public void setApplyFor(Long applyFor) {
		this.applyFor = applyFor;
	}


   
}
