package indexbg.pjobs.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.SysConstants;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.JobHistory;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.ObjectNotFoundException;
import com.indexbg.system.pagination.LazyDataModelSQL2Array;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;
import indexbg.pjobs.db.dao.QuartzDAO;

import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;


@ViewScoped
@Named ( "manageQuartzProcess")
public class ManageQuartzProcess  extends PJobsBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 267320933392089731L;
	
	static final Logger LOGGER = LoggerFactory.getLogger(ManageQuartzProcess.class);
	
	private List<QuartzProcess> allSystemProcess = new ArrayList<QuartzProcess>();
	
	private LazyDataModelSQL2Array systemProcessJournal;;
	
	private QuartzDAO quartzDAO = new QuartzDAO();
	
	private JobHistory filterEntity = new JobHistory();
	
	private Object[] jobHistoryDetails;
	
	private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
	
	//статична променлива, по която заключвам методите за стоп и старт реално да се синхронизирани измежду различните инстанции на класа
	private static final Object LOCK = new Object();
	
	public class QuartzProcess {
		
		private JobKey jobKey;
		private String groupName;
		private String description;
		private Trigger trigger;
		private Scheduler scheduler;
		
		public QuartzProcess() {
			super();
		}
		
		public boolean isPaused() throws SchedulerException {
			TriggerState triggerState = this.scheduler.getTriggerState(this.trigger.getKey());
			return triggerState.equals(TriggerState.PAUSED);
			
		}
		
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Trigger getTrigger() {
			return trigger;
		}

		public void setTrigger(Trigger trigger) {
			this.trigger = trigger;
		}

		public Scheduler getScheduler() {
			return scheduler;
		}

		public void setScheduler(Scheduler scheduler) {
			this.scheduler = scheduler;
		}

		public JobKey getJobKey() {
			return jobKey;
		}

		public void setJobKey(JobKey jobKey) {
			this.jobKey = jobKey;
		}


	}
	
	@PostConstruct
	public void intitializeData (){
		
		LOGGER.info("ManageQuartzProcess_PostConstruct!!!");	
		
		
		try {
			
			SelectMetadata smd = this.quartzDAO.createFindScheduleMetadata(null, null, null, null, null);
			
			this.setSystemProcessJournal(new LazyDataModelSQL2Array(smd, null));
			
			List<String> allSchedulers = getAllSchedulerNames();//взимам всички регистирирани в базата schedulers 
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("cAdmin_quartz.properties"));//зареждам конфигурацинонния файл на кварца за използване на базата
			
			if (allSchedulers != null && allSchedulers.size() > 0){
				for (String schedulerName : allSchedulers) {
					
					
					properties.put("org.quartz.scheduler.instanceName", schedulerName);
					
					Scheduler scheduler = new StdSchedulerFactory(properties).getScheduler();
					
					for (String groupName : scheduler.getJobGroupNames()) {
						
					    for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					    	 
							String jobName = jobKey.getName();
							String jobGroup = jobKey.getGroup();
							
							@SuppressWarnings("unchecked")
							List<Trigger> triggers =  (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
							
							QuartzProcess process = new QuartzProcess();
							 
							process.setTrigger(triggers.get(0));
							process.setJobKey(jobKey);
							String desc = getDescription(jobName, jobGroup);
							process.setDescription(desc);
							//scheduler-а, към който принадлежи процеса
							process.setScheduler(scheduler);
							this.allSystemProcess.add(process);
						  
						}
					}
				}
			}
			LOGGER.info("ManageQuartzProcess_PostConstruct_END!!!");	
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.exception"));
		}
	}
	public void searchJournal(){
		
		try {
			SelectMetadata smd = this.quartzDAO.createFindScheduleMetadata(filterEntity.getJobKeyName(), filterEntity.getTriggerKeyName(), filterEntity.getStartTime(), filterEntity.getEndTime(), filterEntity.getStatus());
		//	String defaultSortColumn = "A0_ID";
			this.systemProcessJournal = new LazyDataModelSQL2Array(smd, null);
			
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене в журнала на системните процеси! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.errDataBaseMsg"));
		
		} catch (Exception e) {
			LOGGER.error("Грешка при работа със системата!!!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages", "general.exception"));

		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	public List<String> getAllSchedulerNames () {
		
		try {
			List<String> schedulerNames = quartzDAO.getAllSchedulerNames();
			
			return schedulerNames;
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		} finally {
			JPA.getUtil().closeConnection();
		}
		return null;
	}
 	
	
	public String getDescription (String jobName, String jobGroup){
		
		try {
			String desc = quartzDAO.findDescription(jobName, jobGroup);
			
			return desc;
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		} finally {
			JPA.getUtil().closeConnection();
		}
		return null;
	}
	
	public void stopProcess(QuartzProcess process) {
		synchronized(LOCK){
			try {
				process.getScheduler().pauseTrigger(process.getTrigger().getKey());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.exception"));
			}
		}
	}
	
	public void startProcess (QuartzProcess process) {
		synchronized(LOCK){
			try {
				process.getScheduler().resumeTrigger(process.getTrigger().getKey());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.exception"));
			}
		}
	}
	
	/**
	 * @param status
	 * @return
	 */
	public String getImageSource(Number status) {
		if (status == null) {
			return null;
		}

		switch (status.intValue()) {
		case SysConstants.JOB_STATUS_OK:
			return "ready.png";
		case SysConstants.JOB_STATUS_WARN:
			return "warning.png";
		case SysConstants.JOB_STATUS_ERROR:
			return "error.png";

		default:
			return null;
		}
	}

	/**
	 * @param status
	 * @return
	 * @throws DbErrorException
	 */
	public String getImageSourceAlt(Number status) {
		if (status == null) {
			return null;
		}
		try {
			return getSystemData().decodeItem(Long.valueOf(SysConstants.CLASSIF_JOB_STATUS), Long.valueOf(status.intValue()),getCurrentLang(), getToday(), getUserData().getUserId());
		} catch (ObjectNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.exception"));
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.exception"));
		}
		return null;
	}
	
	public String formatDate(Date date) {
		if(date != null) {
			return format.format(date);
		}else {
			return "";
		}
	}
	
	public Date getToday(){
		return new Date();
	}
	
	public List<QuartzProcess> getAllSystemProcess() {
		return allSystemProcess;
	}

	public void setAllSystemProcess(List<QuartzProcess> allProcess) {
		this.allSystemProcess = allProcess;
	}

	public LazyDataModelSQL2Array getSystemProcessJournal() {
		return systemProcessJournal;
	}

	public void setSystemProcessJournal(LazyDataModelSQL2Array systemProcessJournal) {
		this.systemProcessJournal = systemProcessJournal;
	}

	public JobHistory getFilterEntity() {
		return filterEntity;
	}

	public void setFilterEntity(JobHistory filterEntity) {
		this.filterEntity = filterEntity;
	}

	public Object[] getJobHistoryDetails() {
		return jobHistoryDetails;
	}

	public void setJobHistoryDetails(Object[] jobHistoryDetails) {
		this.jobHistoryDetails = jobHistoryDetails;
	}

}
