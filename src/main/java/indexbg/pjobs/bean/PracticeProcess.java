package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.JSFUtils;
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.ValidationUtils;

import indexbg.pjobs.db.AdmUsers;
import indexbg.pjobs.db.Practice;
import indexbg.pjobs.db.PracticeLice;
import indexbg.pjobs.db.dao.AdmUsersDAO;
import indexbg.pjobs.db.dao.MailDAO;
import indexbg.pjobs.db.dao.PracticeDAO;
import indexbg.pjobs.db.dao.PracticeLiceDAO;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

/**
 * @author dessy
 *
 */
@Named
@ViewScoped
public class PracticeProcess extends PJobsBean {	

	/** Основен java клас за насрочване на интервю и класиране на кандидати за стажове в ДА
	 * 
	 */
	private static final long serialVersionUID = -2698816709064552417L;
	
	static final Logger LOGGER = LoggerFactory.getLogger(PracticeProcess.class);
	
	private Long userId;
	
	private Practice practice;
	private PracticeLice practiceLice;
	private PracticeDAO practiceDao;
	private PracticeLiceDAO pracLiceDAO;
	
	private List<Object[]>candidatesList;
	private List<Object[]>firstRankingList;
	private List<Object[]>secondRankingList;
	private List<Object[]>secondList;
	private List<Object[]>finalRankingList;
	private List<Object[]>practiceResultList;
	
	private String names;
	private boolean editInterviewData;
	private boolean finishFirstRanked;
	private boolean finishSecondRanked;	
	private boolean finishFinalRanked;	
	private boolean finishPractice;
	private HashMap<Long, Boolean> selectRanked2;
	private boolean changeInterview;
	
	private int freePositionAfterFirst;
	private int freePositionAfterSecond;
	
	private boolean viewSecondRanking;
	
	private List<Object[]>second2RankingList;
	private boolean viewSecond2Ranking;
	
	private boolean viewConfirmRanked;
	
	/** Иницира първоначалните стойности на данните
	 * 
	 * 
	 */
	@PostConstruct
	public void initData(){
		
		LOGGER.debug("PostConstruct!!!!");
		
		try {
			
			this.userId = getUserData().getUserId();
			this.practiceDao = new PracticeDAO(userId);
			this.pracLiceDAO = new PracticeLiceDAO(userId);
			this.practice = new Practice();
			this.practiceLice = new PracticeLice();
			this.candidatesList = new ArrayList<Object[]>();	
			this.firstRankingList = new ArrayList<Object[]>();	
			this.secondRankingList = new ArrayList<Object[]>();	
			this.secondList = new ArrayList<>();
			this.finalRankingList = new ArrayList<Object[]>();
			this.practiceResultList = new ArrayList<Object[]>();
			this.editInterviewData = true;
			this.finishFirstRanked = false;
			this.finishSecondRanked = false;
			this.finishFinalRanked = false;
			this.finishPractice = false;
			this.selectRanked2 = new HashMap<>();
			this.changeInterview = false;
			this.freePositionAfterFirst = 0;
			this.freePositionAfterSecond = 0;
			this.viewSecondRanking = false;
			
			this.second2RankingList = new ArrayList<Object[]>();	
			this.viewSecond2Ranking = false;
			
			this.viewConfirmRanked = true;
			
			Long idObj = null;
			
			if (JSFUtils.getRequestParameter("idObj") != null && !JSFUtils.getRequestParameter("idObj").equals("")) {
				idObj = Long.valueOf(JSFUtils.getRequestParameter("idObj"));	
			}
			
			if (idObj != null) {
				this.practice = this.practiceDao.findById(idObj);	// намира се обявата за стаж			
			}
			
			if (this.practice.getRankingDate() != null && this.practice.getRankingDate().before(new Date())) {				
				this.editInterviewData = false; 
			}
			
			if (this.practice.getPracticeDateTo() != null && this.practice.getPracticeDateTo().before(new Date())) {
				this.finishPractice = true;
			}
			
			//System.out.println("COMPARE DATES >>> " + DateUtils.startDate(this.practice.getConfirmDateTo2()).compareTo(DateUtils.startDate(new Date())));
			
			if (this.practice.getConfirmDateTo2() != null && DateUtils.startDate(this.practice.getConfirmDateTo2()).compareTo(DateUtils.startDate(new Date())) < 0) {
				this.finishFinalRanked = true;
			}
			
			if (this.practice.getRankingDate() != null && DateUtils.startDate(this.practice.getRankingDate()).compareTo(DateUtils.startDate(new Date())) < 0
					&& getUserData().getTypeUser().longValue() != Constants.CODE_ZNACHENIE_TIP_POTR_VATR)  {
				
				this.viewConfirmRanked = false;
			}
			
			// намират се кандидатите за стаж по обявата
			this.candidatesList = this.pracLiceDAO.findCandidatesForPracticeByIdP(this.practice.getId());
			
			actionOpenTabFirstRanking(false);
			
		} catch (ObjectNotFoundException e) {
			LOGGER.error("Не е намерен обект!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.objectNotFound"));
			this.userId = -1L;	
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));		
		
		} finally {
			JPA.getUtil().closeConnection();
		}  	
		
	}	
	
	@SuppressWarnings("rawtypes")
	public void onTabChange(TabChangeEvent event) {
		
		if(event != null) {
			
			LOGGER.debug("onTabChange"+  "Active Tab: " + event.getTab().getId());

			String activeTab =  event.getTab().getId();
			
			if(activeTab.equals("tabInterview")) {
				actionOpenTabInterview();
				
			} else if(activeTab.equals("tabFirstRanking")) {
				actionOpenTabFirstRanking(true);
				
//			} else if(activeTab.equals("tabSecondRanking")) {
//				actionOpenTabSecondRanking();
				
			} else if(activeTab.equals("tabSecond2Ranking")) {
				actionOpenTabSecond2Ranking();
				
			} else if(activeTab.equals("tabFinalRanking")) {
				actionOpenTabFinalRanking();
							
			} else if(activeTab.equals("tabPracticeResult")) {
				actionOpenTabPracticeResult();
				
			}
		}		
	}
	
	// Отваря се таба за насрочване на интервю
	public void actionOpenTabInterview() {
		
//		try {
//			
//			// намират се кандидатите за стаж по обявата
//			this.candidatesList = this.pracLiceDAO.findCandidatesForPracticeByIdP(this.practice.getId());
//		
//		} catch (DbErrorException e) {
//			LOGGER.error("Грешка при работа със системата!!!", e);
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));	
//		} 
		
	}
	
	// Отваря се таба за първо класиране
	public void actionOpenTabFirstRanking(boolean fromPage) {	
		
		this.firstRankingList = new ArrayList<Object[]>();		
		this.secondList = new ArrayList<>();
		
		// това се слага тук, тъй като при преминаване към друг таб, ако не е завършено класирането и връщане пак тук списъка трябва да се зареди пак!
		if (fromPage) { // само ако се натиска от страницата таба
			try {
				
				this.candidatesList = this.pracLiceDAO.findCandidatesForPracticeByIdP(this.practice.getId());
			
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при работа със системата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));		
			
			} finally {
				JPA.getUtil().closeConnection();
			} 	
		}
		
		for (Object[] candidate : candidatesList) { 
			
			if (candidate[5] != null) {
				this.firstRankingList.add(candidate); // добавят се кандидатите за 2-ро класиране
			}
			
			if (candidate[7] != null) {
				this.finishFirstRanked = true;						
				
				if (Long.valueOf(candidate[7].toString()) > 1) { 
					this.secondList.add(candidate);					
				}
			}
		}
		
		if (this.firstRankingList.size() > 0 && this.finishFirstRanked) {
			//сортира се списъка за второ класиране по мястото на класиране
			//sort(this.firstRankingList);
		}
		
		//TODO - това го коментирам, за да не влиза в таба за второ класиране с чекбоксовете, който е скрит и заменен с таб за второ класиране, в който кандидатите са с rancked2 = 1
//		if (this.secondList.size() > 0) {
//			actionOpenTabSecondRanking();
//		}	
		
		if (this.finishFirstRanked) {
			//това го коментирам, за да не влиза в таба за второ класиране с чекбоксовете, който е скрит и заменен с таб за второ класиране, в който кандидатите са с rancked2 = 1
//			if((DateUtils.startDate(this.practice.getConfirmDateTo()).compareTo(DateUtils.startDate(new Date())) < 0)) {
//				this.viewSecondRanking = true;	
//			}
			
			if((DateUtils.startDate(this.practice.getRankingDate2()).compareTo(DateUtils.startDate(new Date())) < 0)) {
				this.viewSecond2Ranking = true;					
			}
		}
		// това го коментирам, за да не влиза в таба за второ класиране с чекбоксовете, който е скрит и заменен с таб за второ класиране, в който кандидатите са с rancked2 = 1
//		if (!this.viewSecondRanking && this.finishFinalRanked) {
//			this.viewSecondRanking = true;
//		}
	}
	
	// Отваря се таба за второ класиране - само с тези, които са с ranked2 = true
	public void actionOpenTabSecond2Ranking() {
		
		this.second2RankingList = new ArrayList<Object[]>();	
		
		for (Object[] candidate : candidatesList) { 
			
			if (candidate[10] != null) {
				this.second2RankingList.add(candidate); // добавят се кандидатите за 1-во класиране
			}
		}
		
		//сортира се списъка за второ класиране по мястото на класиране
		sort(this.second2RankingList);
	}

	// Отваря се таба за второ класиране
//	public void actionOpenTabSecondRanking() {
//		
//		this.secondRankingList = new ArrayList<>();
//		this.selectRanked2 = new HashMap<>();
//		
//		int countFirstPlace = 0;
//		
//		for (Object[] candidate : candidatesList) {
//			
//			if (candidate[7] != null) {
//				this.finishFirstRanked = true;
//				if(Long.valueOf(candidate[7].toString()).equals(1L) && Long.valueOf(candidate[4].toString()).equals(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED)) {
//					countFirstPlace++;
//				}
//			}	
//			
//			if (candidate[10] != null) {
//				this.finishFirstRanked = true;
//				if(Long.valueOf(candidate[10].toString()).equals(1L)) {
//					countFirstPlace++;
//				}
//			}	
//		}
//		
//		this.freePositionAfterFirst = this.practice.getNum().intValue() - countFirstPlace;	
//		
//		if (!this.secondList.isEmpty()) {
//
//			List<Object[]> secRan = new ArrayList<>();
//
//			for (Object[] sec : this.secondList) {
//
//				if (!checkExistInOtherPractice(Long.valueOf(sec[1].toString()))) {
//					secRan.add(sec); // добавя се кандидата за 2 -ро класиране
//				}
//			}
//
//			this.secondRankingList = secRan;
//
//			for (Object[] sec : secondList) {
//				if (!secRan.contains(sec)) {
//					this.secondRankingList.add(sec);
//				}
//			}
//		}
//		
//		//сортира се списъка за второ класиране по мястото на класиране
//		sort(this.secondRankingList);
//		
//		int countRanked = 0;
//		
//		if(!this.secondRankingList.isEmpty()) {
//			int count = 0;
//			boolean ranked = false;
//			for (Object[] secRanked : this.secondRankingList) {					
//				if (secRanked[10] != null) {
//					this.selectRanked2.put(Long.valueOf(secRanked[0].toString()), Boolean.TRUE);
//					countRanked++;
//					ranked = true;
//				} else {
//					if (count < this.freePositionAfterFirst && !ranked) {
//						this.selectRanked2.put(Long.valueOf(secRanked[0].toString()), Boolean.TRUE);
//					} else {						
//						this.selectRanked2.put(Long.valueOf(secRanked[0].toString()), Boolean.FALSE);
//					}
//				}
//				count++;
//			}
//		}
//		
//		if(countRanked > 0) {
//			this.finishSecondRanked = true;
//		}
//	}

	public void actionOpenTabFinalRanking() {
		
		this.finalRankingList = new ArrayList<Object[]>();
		this.freePositionAfterSecond = 0;
		
		// TODO - това се използва, ако таб второ класиране е с чекбокс 
//		for (Object[] secRanked : this.secondRankingList) {
//			
//			if (secRanked[10] == null) {
//				this.freePositionAfterSecond++;
//				this.finalRankingList.add(secRanked);
//			}
//		}
		
		int aceptPractice = 0;
		for (Object[] candidat : this.candidatesList) {			
			if (candidat[9] != null) {
				aceptPractice++;
			}			
		}
		
		this.freePositionAfterSecond = this.practice.getNum().intValue() - aceptPractice;
		
		if (!this.secondList.isEmpty()) {
			
			for (Object[] sec : this.secondList) {
				if (sec[10] == null) {
					if (!checkExistInOtherPractice(Long.valueOf(sec[1].toString()))) {
						this.finalRankingList.add(sec); // добавя се кандидата за финално класиране
					}					
				}
				
//				if (this.finalRankingList.size() == this.freePositionAfterSecond) {
//					break;
//				}
			}			
		}		
		//сортира се списъка за финално класиране по мястото на класиране
		sort(this.finalRankingList);
	}

	public void actionOpenTabPracticeResult() {
		
		this.practiceResultList = new ArrayList<Object[]>();
		
		for (Object[] candidate : candidatesList) {
			
			if (candidate[4] != null && Long.valueOf(candidate[4].toString()).equals(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED)) {
				this.practiceResultList.add(candidate);				
			}			
		}	
	}
	
	// Въвеждат се данни за интервю за кандидата - дата и място 
	public void actionSetDataInterview(Long idPracLice) {
		
		try {

			for (Object[] candidate : candidatesList) {

				if (idPracLice.equals(SearchUtils.asLong(candidate[0]))) {

					this.practiceLice = this.pracLiceDAO.findById(idPracLice);

					this.names = SearchUtils.asString(candidate[2]);
					
					if (this.practiceLice.getDateInterview() != null || (this.practiceLice.getInterviewPlace() != null && !"".equals(this.practiceLice.getInterviewPlace()))) {
						changeInterview = true;
					}
				}
			}

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при зареждане данните на кандидата за обява за стаж!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		
		} finally {
			JPA.getUtil().closeConnection();
		} 
		
	}
	
	// Записват се въведените данни за интервюто 
	public void actionSaveDataInterview() {
		
		boolean save = true;

		if (this.practiceLice.getDateInterview() == null) {
			JSFUtils.addMessage("formPracticeProcess:dateInterview", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsert", getMessageResourceString("labels", "practiceProcess.dateInterview")));
			save = false;
		} else {
			
			if (DateUtils.startDate(this.practiceLice.getDateInterview()).compareTo(DateUtils.startDate(this.practice.getRankingDate())) >= 0) {
				JSFUtils.addMessage("formPracticeProcess:dateInterview", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "practiceProcess.dateInterviewAfterRankingDate"));
				save = false;
			}
		}

		if (this.practiceLice.getInterviewPlace() == null || this.practiceLice.getInterviewPlace().isEmpty()) {
			JSFUtils.addMessage("formPracticeProcess:interviewPlace", FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.pleaseInsert", getMessageResourceString("labels", "practiceProcess.placeInterview")));
			save = false;
		}

		if (save) {

			try {
				
				ArrayList<String> mailsTo = new ArrayList<>();
				
				AdmUsers user = new AdmUsersDAO(userId).getDanniForUser(this.practiceLice.getIdUser());
				
				mailsTo.add(user.getEmail());
				
				JPA.getUtil().begin();

				this.practiceLice.setStatus(Constants.CODE_ZNACHENIE_TYPE_STATUS_APPROVED_FOR_INTERVIEW);
				this.practiceLice.setStatusDate(new Date());

				this.practiceLice = this.pracLiceDAO.save(this.practiceLice);
				
				JPA.getUtil().flush(); 	
				
				Calendar c = Calendar.getInstance();
				c.setTime(this.practiceLice.getDateInterview());
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				
				String hourInterview = String.format("%02d:%02d", hour, minute);

				if (changeInterview) {
					// Изпращане на съобщение за промяна на датаи/или адрес на интервю на потребител
					new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_CHANGE_INTERVIEW_DATE_ADDRESS, mailsTo, getUserData().getCodeOrg(), new Date(), hourInterview, this.practiceLice.getInterviewPlace(), this.names, this.practiceLice.getIdUser(), null, this.userId, Constants.CODE_OBJECT_APPLY, getSystemData(), null, null, null, this.practice.getPracticeTitle(), this.practiceLice.getDateInterview(), null);					
				} else {	
					// Изпращане на съобщение за одобряване за интервю на потребител
					new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_APPROVED_FOR_INTERVIEW, mailsTo, getUserData().getCodeOrg(), new Date(), hourInterview, this.practiceLice.getInterviewPlace(), this.names, this.practiceLice.getIdUser(), null, this.userId, Constants.CODE_OBJECT_APPLY, getSystemData(), null, null, null, this.practice.getPracticeTitle(), this.practiceLice.getDateInterview(), null);					
				}				
											
				JPA.getUtil().commit();

				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
				
				this.candidatesList = this.pracLiceDAO.findCandidatesForPracticeByIdP(this.practice.getId());
				
				PrimeFaces.current().executeScript("PF('modalDataInterview').hide();");

			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при запис на данни за интервю на кандидата! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));

			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при запис на данни за интервю на кандидата!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));

			} finally {
				JPA.getUtil().closeConnection();
			}
		}
	}
	
	// Проверка за въведените места за класиране
	public void actionCheckFirstRanking(int index) {
		
		if(this.firstRankingList.get(index)[7] != null) {
			
			int broi = this.practice.getNum().intValue();
				
			if (Long.valueOf(this.firstRankingList.get(index)[7].toString()) <= 0) { 	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.plsInsertNumGreater0"));
				this.firstRankingList.get(index)[7] = null;
				return;
			
			} else 	if (Long.valueOf(this.firstRankingList.get(index)[7].toString()) == 1) { 
				
				int numRankedFirst = 0;
				
				for (Object[] candidateRanked : this.firstRankingList) {					
					if(candidateRanked[7] != null) {
						if (Long.valueOf(candidateRanked[7].toString()).equals(Long.valueOf(1))) {	 						
							numRankedFirst++;
						}
					}
				}
				
				if (broi < numRankedFirst) {
					
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.cantRankedFirst", broi));
					this.firstRankingList.get(index)[7] = null;
					return;
				}
			
			} else 	if (Long.valueOf(this.firstRankingList.get(index)[7].toString()) > 1) { 
				
				if (broi > numbeFirstPlaceClassification(firstRankingList)) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.rankedFirstMaxNumber"));
					this.firstRankingList.get(index)[7] = null;
					return;
				
				} else {
				
					for (Object[] candidate : candidatesList) {
						
						if (!Long.valueOf(this.firstRankingList.get(index)[0].toString()).equals(Long.valueOf(candidate[0].toString()))) {						
							if (candidate[7] != null) {
								
								Long classification = Long.valueOf(candidate[7].toString());
	
								if (classification > 1 && Long.valueOf(this.firstRankingList.get(index)[7].toString()) == classification) {
									JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.existRankedNum", classification));
									this.firstRankingList.get(index)[7] = null;
									break;
								} 						
							} 
						}			
					}
				}
			}
		}		
	}
	
	// Изчислява колко кандидати са класирани на 1-во място
	public int numbeFirstPlaceClassification(List<Object[]>rankingList) {
		
		int number = 0;		
		for (Object[] first : rankingList) {
			if (first[7] != null) {
				
				if(Long.valueOf(first[7].toString()).equals(1L)) {
					number++;
				}
			}
		}		
		return number; 	
	}
	
	// Метод за потвърждаване на първо класиране
	public void actionConfirmFirstRanked() {
		
//		boolean update = true;
//		
//		for (Object[] candidateRanked : this.firstRankingList) {
//			
//			if (candidateRanked[7] == null || candidateRanked[7].equals("")) {
//				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.notRankedCandidate"));
//				update = false;
//				PrimeFaces.current().executeScript("PF('modalConfirmRanked').hide();");
//				break;
//			}
//		}
//		
//		if (update) {
			
		try {
			
			JPA.getUtil().begin();
			
			for (Object[] candidateRanked : this.firstRankingList) {
				
				boolean ranked = false;
				boolean notRanked = false;
				
				ArrayList<String> mailsToRancked = new ArrayList<>();		
				ArrayList<String> mailsToNotRancked = new ArrayList<>();	
				
				this.practiceLice = this.pracLiceDAO.findById(Long.valueOf(candidateRanked[0].toString()));
				
				this.names = SearchUtils.asString(candidateRanked[2]);
				
				if(ValidationUtils.isNotBlank(candidateRanked[7].toString())) {
					
					this.practiceLice.setClassification(Long.valueOf(candidateRanked[7].toString()));
					this.practiceLice.setStatus(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_RANKED);
					this.practiceLice.setStatusDate(new Date());
					
					this.practiceLice = this.pracLiceDAO.save(this.practiceLice);
				}
				
				if (this.practiceLice.getClassification() != null && this.practiceLice.getClassification().equals(1L)) {
					ranked = true;
					mailsToRancked.add(SearchUtils.asString(candidateRanked[3]));	
				}
				
				if (this.practiceLice.getClassification() == null || this.practiceLice.getDateInterview() == null) {
					notRanked = true;
					mailsToNotRancked.add(SearchUtils.asString(candidateRanked[3]));	
				}
				
				JPA.getUtil().flush();
				
				if (ranked) {
					new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_RANKED_FIRST_RANKING, mailsToRancked, getUserData().getCodeOrg(), new Date(), null, null, this.names, this.practiceLice.getIdUser(), null, this.userId, Constants.CODE_OBJECT_APPLY, getSystemData(), null, null, null, this.practice.getPracticeTitle(), this.practice.getConfirmDateFrom(), this.practice.getConfirmDateTo());				
				}
				
				if (notRanked) {
					new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_NOT_RANKED_CANDIDATE, mailsToNotRancked, getUserData().getCodeOrg(), new Date(), null, null, this.names, this.practiceLice.getIdUser(), null, this.userId, Constants.CODE_OBJECT_APPLY, getSystemData(), null, null, null, this.practice.getPracticeTitle(), null, null);				
				}				
			}
			
			this.candidatesList = this.pracLiceDAO.findCandidatesForPracticeByIdP(this.practice.getId());
			
			ArrayList<String> mailsToNotRancked = new ArrayList<>();	
			
			for (Object[] cand : candidatesList) {
				this.names = SearchUtils.asString(cand[2]);
				if (cand[5] == null) {
					mailsToNotRancked.add(SearchUtils.asString(cand[3]));	
					
					JPA.getUtil().flush();
					
					new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_NOT_RANKED_CANDIDATE, mailsToNotRancked, getUserData().getCodeOrg(), new Date(), null, null, this.names, this.practiceLice.getIdUser(), null, this.userId, Constants.CODE_OBJECT_APPLY, getSystemData(), null, null, null, this.practice.getPracticeTitle(), null, null);				
				}
			}			
						
			JPA.getUtil().commit();
			
			int countFirstPlace = 0;
			
			for (Object[] candidate : candidatesList) {
				
				if (candidate[7] != null) {						
					if(Long.valueOf(candidate[7].toString()).equals(1L)) {
						countFirstPlace++;
					}
				}
			}
			
			this.freePositionAfterFirst = this.practice.getNum().intValue() - countFirstPlace;
			
			this.freePositionAfterSecond = this.freePositionAfterFirst;
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
			
			this.finishFirstRanked = true;
			
			PrimeFaces.current().executeScript("PF('modalConfirmRanked').hide();");
			
			actionOpenTabFirstRanking(false);
		
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при първо класиране на кандидата! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (ObjectNotFoundException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при първо класиране на кандидата! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.objectNotFound"));
		
		} finally {
			JPA.getUtil().closeConnection();
		}
			
//		}		
	}
	
	// Метод за потвърждаване на второ класиране
//	public void actionConfirmSecondRanked() {
//		
//		boolean update = true;		
//			
//		try {
//			
//			if (this.freePositionAfterFirst == 0) {
//				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.notFreePosition"));
//				update = false;
//				return;
//			
//			} else {
//				
//				boolean notChecked = this.selectRanked2.values().stream().allMatch(Boolean.FALSE::equals);				
//				
//				if (notChecked) {
//					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.notSecondRankedCandidate", this.freePositionAfterFirst));
//					update = false;
//					return;
//				}
//				
//				int sizeSel = (int) selectRanked2.values().stream().filter(Boolean.TRUE::equals).count();
//				
//				if (this.freePositionAfterFirst < sizeSel) {
//					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.cantRankedSecond", this.freePositionAfterFirst));
//					update = false;
//					return;
//				}
//				
//				StringBuilder sb = new StringBuilder();		
//				List<Object[]> leaveCandidate = new ArrayList<>();
//				List<Object[]> removeCandidate = new ArrayList<>();
//				int countExist = 0;
//				
//				for (Object[] sec : this.secondRankingList) {	
//					if (checkExistInOtherPractice(Long.valueOf(sec[1].toString()))) {
//						sb.append(sec[2].toString());
//						countExist++;
//						removeCandidate.add(sec);
//					} else {
//						leaveCandidate.add(sec);
//					}
//				}
//				
//				if (countExist > 0) {
//					
//					if (CollectionUtils.containsAny(this.secondRankingList, removeCandidate)) {
//						this.secondRankingList = leaveCandidate;					
//					}					
//					
//					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.existCandidateInOtherPractice", sb.toString()));
//					update = false;
//					return;
//				}				
//			}
//			
//			if (update) {
//				
//				ArrayList<String> mailsTo = new ArrayList<>();
//				
//				int numberRanked2 = 0;
//				
//				JPA.getUtil().begin();
//				
//				for (Object[] candidateRanked : this.secondRankingList) {	
//					
//					for (HashMap.Entry<Long, Boolean> entry : this.selectRanked2.entrySet()) {
//						
//						if (entry.getKey().equals(Long.valueOf(candidateRanked[0].toString())) && entry.getValue().booleanValue()) {
//							
//							mailsTo.add(SearchUtils.asString(candidateRanked[3]));	
//							
//							this.practiceLice = this.pracLiceDAO.findById(Long.valueOf(candidateRanked[0].toString()));
//														
//							this.practiceLice.setRanked2(Constants.CODE_ZNACHENIE_DA); 
//							
//							this.practiceLice = this.pracLiceDAO.save(this.practiceLice);
//							
//							numberRanked2++;
//						}						
//				    }					
//				}
//				
//				JPA.getUtil().flush();
//				
//				new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_RANKED_SECOND_RANKING, mailsTo, getUserData().getCodeOrg(), new Date(), null, null, this.names, this.practiceLice.getIdUser(), null, this.userId, Constants.CODE_OBJECT_APPLY, getSystemData(), null, null, null, this.practice.getPracticeTitle(), this.practice.getConfirmDateFrom2(), this.practice.getConfirmDateTo2());					
//				
//				JPA.getUtil().commit();
//				
//				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
//				
//				this.finishSecondRanked = true;
//				
//				this.freePositionAfterFirst = this.freePositionAfterFirst - numberRanked2;
//				
//				if (this.practice.getConfirmDateTo2() != null && DateUtils.startDate(this.practice.getConfirmDateTo2()).compareTo(DateUtils.startDate(new Date())) > 0) {
//					this.finishFinalRanked = true;
//				}
//				
//				PrimeFaces.current().executeScript("PF('modalConfirmSecondRanked').hide();");				
//			}
//
//		} catch (DbErrorException e) {
//			JPA.getUtil().rollback();
//			LOGGER.error("Грешка при второ класиране на кандидата! ", e);
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
//		
//		} catch (Exception e) {
//			JPA.getUtil().rollback();
//			LOGGER.error("Грешка при второ класиране на кандидата! ", e);
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
//
//		} finally {
//			JPA.getUtil().closeConnection();
//		} 
//		
//	}
	
	// Проверка дали кандидата е приел друг стаж към същата кампания
	public boolean checkExistInOtherPractice(Long idCandidat) {

		boolean existInOtherPractice = false;		
		
		try {
		
			List<Object[]> resPracticeList = this.pracLiceDAO.findCandidateByIdCamp(this.practice.getIdCampaign(), idCandidat);		
	
			if (resPracticeList.size() > 0) {
	
				for (Object[] usersByCamp : resPracticeList) {
	
					Long practiceId = Long.valueOf(usersByCamp[0].toString());
					Long status = Long.valueOf(usersByCamp[1].toString());
	
					// проверява се дали кандидата е приел друг стаж в същата кампания
					if (!this.practice.getId().equals(practiceId)) {
	
						if (!status.equals(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED)) {
							existInOtherPractice = false; 
						} else {
							existInOtherPractice = true;
						}
					}
				}
			}
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.exception"));	
		
		}  finally {
			JPA.getUtil().closeConnection();
		}

		return existInOtherPractice;
	}
	
	// Метод за приемане на стаж на финално класиране
	public void actionAcceptedPractice(Long idPracLice, Long idCandidate, String name) {
		
		boolean save = true;
			
		if (checkExistInOtherPractice(idCandidate)) {
			
			List<Object[]> leaveCandidate = new ArrayList<>();
			for (Object[] finRan : this.finalRankingList) {
				if (!Long.valueOf(finRan[0].toString()).equals(idPracLice)) {
					leaveCandidate.add(finRan);					
				}				
			}
			this.finalRankingList = leaveCandidate;
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.existCandidateInOtherPracticeAccept", name));
			save = false;
			return;
		} 
		
		if (this.finalRankingList.size() == this.freePositionAfterSecond) {
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "practiceProcess.maxNumberCandidates"));
			save = false;
			return;
		}
		
		if (save) {

			try {
		
				this.practiceLice = this.pracLiceDAO.findById(idPracLice);
				
				ArrayList<String> mailsTo = new ArrayList<>();
				
				AdmUsers user = new AdmUsersDAO(userId).getDanniForUser(idCandidate);
				
				mailsTo.add(user.getEmail());
				
				JPA.getUtil().begin();

				this.practiceLice.setStatus(Constants.CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED);
				this.practiceLice.setStatusDate(new Date());
				this.practiceLice.setAcceptDate(new Date());
				this.practiceLice.setRanked2(Constants.CODE_ZNACHENIE_DA); 

				this.practiceLice = this.pracLiceDAO.save(this.practiceLice);
				
				JPA.getUtil().flush(); 	
				
				// Изпращане на съобщение за приемане стаж от администратор
				new MailDAO(userId).saveMail(Constants.CODE_ZNACHENIE_SHABLON_ACCEPT_PRACTICE_FROM_ADMIN, mailsTo, getUserData().getCodeOrg(), new Date(), null, null, name, this.practiceLice.getIdUser(), null, this.userId, Constants.CODE_OBJECT_APPLY, getSystemData(), null, null, null, this.practice.getPracticeTitle(), null, null);					
											
				JPA.getUtil().commit();

				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
				
				this.candidatesList = this.pracLiceDAO.findCandidatesForPracticeByIdP(this.practice.getId());
				
				for (Object[] finalCandidate : this.finalRankingList) {
					if (Long.valueOf(finalCandidate[0].toString()).equals(idPracLice)) {
						this.second2RankingList.remove(finalCandidate);
					}
				}
				
				actionOpenTabFinalRanking();
				
				int aceptPractice = 0;
				for (Object[] candidat : this.candidatesList) {			
					if (candidat[9] != null) {
						aceptPractice++;
					}			
				}
				
				this.freePositionAfterSecond = this.practice.getNum().intValue() - aceptPractice;
				
		
			} catch (DbErrorException e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при запис на приемане на стаж! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
	
			} catch (Exception e) {
				JPA.getUtil().rollback();
				LOGGER.error("Грешка при запис на приемане на стаж!!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));
	
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
	}
	
	// Метод за записване на резултат от стажа
	public void actionSaveResultPractice() {
			
		try {
			
			JPA.getUtil().begin();
			
			for (Object[] resultCandidate : this.practiceResultList) {
				
				if (resultCandidate[8] != null) {
					
					this.practiceLice = this.pracLiceDAO.findById(Long.valueOf(resultCandidate[0].toString()));
					
					this.practiceLice.setPracticeResult(Long.valueOf(resultCandidate[8].toString()));

					this.practiceLice = this.pracLiceDAO.save(this.practiceLice);					
				}				
			}
			
			this.candidatesList = this.pracLiceDAO.findCandidatesForPracticeByIdP(this.practice.getId());
						
			JPA.getUtil().commit();
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString("beanMessages", "general.succesSaveMsg"));
		
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Грешка при записване на резултат от стажа! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));		
		
		} finally {
			JPA.getUtil().closeConnection();
		}	
				
	}
		
	
	// ************************************************* GET & SET ***************************************************** //

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Practice getPractice() {
		return practice;
	}

	public void setPractice(Practice practice) {
		this.practice = practice;
	}

	public PracticeLice getPracticeLice() {
		return practiceLice;
	}

	public void setPracticeLice(PracticeLice practiceLice) {
		this.practiceLice = practiceLice;
	}

//	public List<Object[]> getPracticeListByCampaign() {
//		return practiceListByCampaign;
//	}
//
//	public void setPracticeListByCampaign(List<Object[]> practiceListByCampaign) {
//		this.practiceListByCampaign = practiceListByCampaign;
//	}

	public List<Object[]> getCandidatesList() {
		return candidatesList;
	}

	public void setCandidatesList(List<Object[]> candidatesList) {
		this.candidatesList = candidatesList;
	}

	public List<Object[]> getFirstRankingList() {
		return firstRankingList;
	}

	public void setFirstRankingList(List<Object[]> firstRankingList) {
		this.firstRankingList = firstRankingList;
	}

	public List<Object[]> getSecondRankingList() {
		return secondRankingList;
	}

	public void setSecondRankingList(List<Object[]> secondRankingList) {
		this.secondRankingList = secondRankingList;
	}

	public List<Object[]> getSecondList() {
		return secondList;
	}

	public void setSecondList(List<Object[]> secondList) {
		this.secondList = secondList;
	}

	public List<Object[]> getFinalRankingList() {
		return finalRankingList;
	}

	public void setFinalRankingList(List<Object[]> finalRankingList) {
		this.finalRankingList = finalRankingList;
	}

	public List<Object[]> getPracticeResultList() {
		return practiceResultList;
	}

	public void setPracticeResultList(List<Object[]> practiceResultList) {
		this.practiceResultList = practiceResultList;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public boolean isEditInterviewData() {
		return editInterviewData;
	}

	public void setEditInterviewData(boolean editInterviewData) {
		this.editInterviewData = editInterviewData;
	}

	public boolean isFinishFirstRanked() {
		return finishFirstRanked;
	}

	public void setFinishFirstRanked(boolean finishFirstRanked) {
		this.finishFirstRanked = finishFirstRanked;
	}

	public boolean isFinishSecondRanked() {
		return finishSecondRanked;
	}

	public void setFinishSecondRanked(boolean finishSecondRanked) {
		this.finishSecondRanked = finishSecondRanked;
	}

	public boolean isFinishFinalRanked() {
		return finishFinalRanked;
	}

	public void setFinishFinalRanked(boolean finishFinalRanked) {
		this.finishFinalRanked = finishFinalRanked;
	}

	public boolean isFinishPractice() {
		return finishPractice;
	}

	public void setFinishPractice(boolean finishPractice) {
		this.finishPractice = finishPractice;
	}

	public HashMap<Long, Boolean> getSelectRanked2() {
		return selectRanked2;
	}

	public void setSelectRanked2(HashMap<Long, Boolean> selectRanked2) {
		this.selectRanked2 = selectRanked2;
	}
	
	public boolean isChangeInterview() {
		return changeInterview;
	}

	public void setChangeInterview(boolean changeInterview) {
		this.changeInterview = changeInterview;
	}	
	
	public int getFreePositionAfterFirst() {
		return freePositionAfterFirst;
	}

	public void setFreePositionAfterFirst(int freePositionAfterFirst) {
		this.freePositionAfterFirst = freePositionAfterFirst;
	}

	public int getFreePositionAfterSecond() {
		return freePositionAfterSecond;
	}

	public void setFreePositionAfterSecond(int freePositionAfterSecond) {
		this.freePositionAfterSecond = freePositionAfterSecond;
	}
	
	public boolean isViewSecondRanking() {
		return viewSecondRanking;
	}

	public void setViewSecondRanking(boolean viewSecondRanking) {
		this.viewSecondRanking = viewSecondRanking;
	}
	
	// ************************************************* END GET & SET ***************************************************** //		

	public List<Object[]> getSecond2RankingList() {
		return second2RankingList;
	}

	public void setSecond2RankingList(List<Object[]> second2RankingList) {
		this.second2RankingList = second2RankingList;
	}

	public boolean isViewSecond2Ranking() {
		return viewSecond2Ranking;
	}

	public void setViewSecond2Ranking(boolean viewSecond2Ranking) {
		this.viewSecond2Ranking = viewSecond2Ranking;
	}

	public boolean isViewConfirmRanked() {
		return viewConfirmRanked;
	}

	public void setViewConfirmRanked(boolean viewConfirmRanked) {
		this.viewConfirmRanked = viewConfirmRanked;
	}

	//Това се налага защото jsf HashMap не работи с integer
	public Long castLong(Integer i) {
		
		if(i == null) return -1L;
		
		return Long.valueOf(i); 		
	}
	
	public static void sort(List<Object[]> list) {		
		 
        list.sort((o1, o2) -> (SearchUtils.asLong(o1[7])).compareTo(SearchUtils.asLong(o2[7])));
    }	
}
