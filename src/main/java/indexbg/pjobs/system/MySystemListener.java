package indexbg.pjobs.system;

import java.util.Date;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.PreDestroyApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.servlet.ServletContext;

import indexbg.pjobs.quartz.*;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.BaseSystemData;
import com.indexbg.system.SystemDataSynchronizer;
import com.indexbg.system.db.JPA;
import com.indexbg.system.quartz.BaseJobListener;
import com.indexbg.system.utils.JSFUtils;


/**
 * Слуша за стартиране и спиране на приложението.
 */
public class MySystemListener implements SystemEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MySystemListener.class);
	
//	private static Map<String, String> allParams;
	private Scheduler scheduler;
	private BaseSystemData sd;
	
	/** @see SystemEventListener#isListenerForSource(Object) */
	@Override
	public boolean isListenerForSource(Object source) {
		return source instanceof Application;
	}

	/** @see SystemEventListener#processEvent(SystemEvent) */
	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {

		if (event instanceof PostConstructApplicationEvent) {
			myApplicationInit(event);

		} else if (event instanceof PreDestroyApplicationEvent) {
			myApplicationDestroy(event);
		}
	}
	
	private void myApplicationDestroy(final SystemEvent event) {
		LOGGER.info("myApplicationDestroy - START");

		try {
			LOGGER.info("Спиране на quartz процесите.");

			// Use this properties file instead of quartz.properties
//			System.setProperty("org.quartz.properties", "pdoi_quartz.properties");
			this.scheduler = new StdSchedulerFactory("pjobs_quartz.properties").getScheduler();

			//TODO трябва да се види кога и дали изобщо трява да се зачистват данните свързани с този scheduler
//			scheduler.clear();
			
			//scheduler.clear();
			scheduler.shutdown(); 


		} catch (Exception e) {
			LOGGER.error("Error - myApplicationDestroy !!!!", e);
		}

		try {
			LOGGER.info("Спиране на JPA.");

			JPA.shutdown();

		} catch (Exception e) {
			LOGGER.error("Error - myApplicationDestroy !!!!", e);
		}

		LOGGER.info("myApplicationDestroy - END");
	}

	private void myApplicationInit(final SystemEvent event) {
		LOGGER.info("myApplicationInit - START");

		this.sd = (BaseSystemData) JSFUtils.getManagedBean("systemData");
//		allParams = sd.getSystemPropSettings();
		
		try {
			LOGGER.info("Инициализиране на quartz процесите.");
			
			// Use this properties file instead of quartz.properties
//			System.setProperty("org.quartz.properties", "pjobs_quartz.properties");
			this.scheduler = new StdSchedulerFactory("pjobs_quartz.properties").getScheduler();
			
			if (this.scheduler==null) {
				LOGGER.error("Schedur is NULL!!!!");
			}
			
			//проверява и обработва неизпратеи съобщения
			scheduleMailsJob();
			
			//проверява и записва обяви за работа от абонамент
			//scheduleCompetitionsJob();   //04.01.24 спираме го докато не се направят свестни услуги
			
			//проверява и записва обяви за mobilnost от абонамент
			scheduleAdvertisementJob();
			
			//Обновяване на административния регистър
			scheduleUpdateAdmRegisterEntries();
			
			//Второ класиране по обяви за стаж
			scheduleRanking2Practicle();

			//Длъжности
			scheduleUpdateDlajnosti();
			
			//Приключило кандидатстване по обяви за стаж
			scheduleEndDateToPracticle();
			
			boolean start = Boolean.valueOf(this.sd.getSettingsValue("quartz.scheduler.pdjobs.start"));
			
			if (start) {
				this.scheduler.start();
			}
			
		} catch (Exception e) {
			LOGGER.error("Error - myApplicationInit !!!!", e);
		}

		
		try {
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			BaseSystemData systemData = (BaseSystemData) JSFUtils.getManagedBean("systemData");
	
			LOGGER.debug("Trying to START SystemDataSynchronizer ...");
			SystemDataSynchronizer synchronizer = new PJobsSystemDataSynchronizer(systemData, null);
			servletContext.setAttribute("systemDataSynchronizer", synchronizer);
		} catch (Exception e) {
			LOGGER.error("Error - START SystemDataSynchronizer !!!!", e);
		}
		
		
		LOGGER.info("myApplicationInit - END");
	}
	
	
	private void scheduleMailsJob () throws SchedulerException {
		
		Trigger trigMess = TriggerBuilder.newTrigger().withIdentity("trigMails", "pjobs") //
				.withDescription("Тригер, който се стартира на всеки 10 минути") //
				.withSchedule(CronScheduleBuilder.cronSchedule(this.sd.getSettingsValue("quartz.mail.trigger.cron"))) //
				.startAt(new Date(System.currentTimeMillis() + 34000L)) //
				.build(); //
		JobDetail jobMess = JobBuilder.newJob(MyMailsJob.class) //
				.withIdentity("jobMail", "pjobs") //
				.withDescription("проверява и обработва неизпратеи съобщения") //
				.build(); //
		
		JobKey jobKey = jobMess.getKey();
		this.scheduler.getListenerManager().addJobListener(new BaseJobListener("pjobsMails"), KeyMatcher.keyEquals(jobKey));
		
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.rescheduleJob(trigMess.getKey(), trigMess);
		} else {
			this.scheduler.scheduleJob(jobMess, trigMess);
		}

	}
	
	//04.01.24 спираме го докато не се направят свестни услуги
	private void scheduleCompetitionsJob() throws SchedulerException {
		
		Trigger trigComp = TriggerBuilder.newTrigger().withIdentity("trigComp", "pjobs") //
				.withDescription("Тригер, който се стартира на всеки 6 часа") //
				.withSchedule(CronScheduleBuilder.cronSchedule(this.sd.getSettingsValue("quartz.update.competitions.trigger.cron")))//"0 0 */4 ? * *")) //
				.startAt(new Date(System.currentTimeMillis() + 34000L)) //
				.build(); //
		JobDetail jobComp = JobBuilder.newJob(MyCompetitionsJob.class) //
				.withIdentity("jobComp", "pjobs") //
				.withDescription("проверява и обработва обяви за работа") //
				.build(); //
		
		JobKey jobKey = jobComp.getKey();
		this.scheduler.getListenerManager().addJobListener(new BaseJobListener("pjobsComp"), KeyMatcher.keyEquals(jobKey));
		
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.rescheduleJob(trigComp.getKey(), trigComp);
		} else {
			this.scheduler.scheduleJob(jobComp, trigComp);
		}

	}
	
	private void scheduleAdvertisementJob() throws SchedulerException {
		
		Trigger trigComp = TriggerBuilder.newTrigger().withIdentity("trigAdvertisement", "pjobs") //
				.withDescription("Тригер, който се стартира на всеки 6 часа") //
				.withSchedule(CronScheduleBuilder.cronSchedule(this.sd.getSettingsValue("quartz.update.advertisement.trigger.cron")))
				.startAt(new Date(System.currentTimeMillis() + 34000L)) //
				.build(); //
		JobDetail jobComp = JobBuilder.newJob(SubscAdvertisementJob.class) //
				.withIdentity("jobAdvertisement", "pjobs") //
				.withDescription("проверява и обработва обяви за мобилност") //
				.build(); //
		
		JobKey jobKey = jobComp.getKey();
		this.scheduler.getListenerManager().addJobListener(new BaseJobListener("pjobsAdwertisment"), KeyMatcher.keyEquals(jobKey));
		
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.rescheduleJob(trigComp.getKey(), trigComp);
		} else {
			this.scheduler.scheduleJob(jobComp, trigComp);
		}

	}
	
	private void scheduleUpdateAdmRegisterEntries() throws SchedulerException {
		
		Trigger trigUpdateAdmRegEntries = TriggerBuilder.newTrigger().withIdentity("trigAdmRegUpdate", "pjobs")
				.withDescription("Тригер, който се стартира на всеки 24 часа").withSchedule(CronScheduleBuilder
				.cronSchedule(this.sd.getSettingsValue("quartz.update.register.trigger.cron"))).startAt(new Date(System.currentTimeMillis() + 17000L))
				.build();
		
		JobDetail jobUpdateAdmRegEntries = JobBuilder.newJob(UpdateAdmRegisterJob.class) //
				.withIdentity("jobAdmRegUpdate", "pjobs") //
				.withDescription("Обновяване на задължени субекти") //
				.build(); //
		
		JobKey jobKey = jobUpdateAdmRegEntries.getKey();
		this.scheduler.getListenerManager().addJobListener(new BaseJobListener("pjobsUpdateAdmReg"), KeyMatcher.keyEquals(jobKey));
		
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.rescheduleJob(trigUpdateAdmRegEntries.getKey(), trigUpdateAdmRegEntries);
		} else {
			this.scheduler.scheduleJob(jobUpdateAdmRegEntries, trigUpdateAdmRegEntries);
		}
	}
	
	private void scheduleRanking2Practicle() throws SchedulerException {
		
		Trigger trigMess = TriggerBuilder.newTrigger().withIdentity("trigRanking2", "pjobs") //
				.withDescription("Тригер, който се стартира на всеки 24 часа") //
				.withSchedule(CronScheduleBuilder.cronSchedule(this.sd.getSettingsValue("quartz.pdjobs.ranking2.trigger.cron"))) //
				.startAt(new Date(System.currentTimeMillis() + 17000L)) //
				.build(); //
		JobDetail jobMess = JobBuilder.newJob(RankingPracticleJob.class) //
				.withIdentity("jobRanking2", "pjobs") //
				.withDescription("Извършва второ класиране на кандидати за стажове") //
				.build(); //
		
		JobKey jobKey = jobMess.getKey();
		this.scheduler.getListenerManager().addJobListener(new BaseJobListener("trigRanking2"), KeyMatcher.keyEquals(jobKey));
		
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.rescheduleJob(trigMess.getKey(), trigMess);
		} else {
			this.scheduler.scheduleJob(jobMess, trigMess);
		}

	}

	/**Зарежда длъжности (от някакъв сървис)
	 * @throws SchedulerException
	 */
	private void scheduleUpdateDlajnosti() throws SchedulerException{
		Trigger trigMess = TriggerBuilder.newTrigger().withIdentity("trigDlajnosti", "pjobs") //
				.withDescription("Тригер, който се стартира на всеки X часа") //
				.withSchedule(CronScheduleBuilder.cronSchedule(this.sd.getSettingsValue("quartz.update.dlajnosti.trigger.cron"))) //
				.startAt(new Date(System.currentTimeMillis() + 17000L)) //
				.build(); //
		JobDetail jobMess = JobBuilder.newJob(UpdateDlajnostiJob.class) //
				.withIdentity("pjobsUpdateDlajnosti", "pjobs") //
				.withDescription("Извлича длъжности чрез уеб услуга") //
				.build(); //

		JobKey jobKey = jobMess.getKey();
		this.scheduler.getListenerManager().addJobListener(new BaseJobListener("trigDlajnosti"), KeyMatcher.keyEquals(jobKey));

		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.rescheduleJob(trigMess.getKey(), trigMess);
		} else {
			this.scheduler.scheduleJob(jobMess, trigMess);
		}
	}
	
	private void scheduleEndDateToPracticle() throws SchedulerException {
		
		Trigger trigMess = TriggerBuilder.newTrigger().withIdentity("trigPracticleEnd", "pjobs") //
				.withDescription("Тригер, който се стартира на всеки 24 часа") //
				.withSchedule(CronScheduleBuilder.cronSchedule(this.sd.getSettingsValue("quartz.pdjobs.practiceEnd.trigger.cron"))) //
				.startAt(new Date(System.currentTimeMillis() + 17000L)) //
				.build(); //
		JobDetail jobMess = JobBuilder.newJob(PracticleEndMailAdmins.class) //
				.withIdentity("jobPracticleEnd", "pjobs") //
				.withDescription("Изпраща имейли на администратори за стажове които са приключили с кандидатстването") //
				.build(); //
		
		JobKey jobKey = jobMess.getKey();
		this.scheduler.getListenerManager().addJobListener(new BaseJobListener("trigPracticleEnd"), KeyMatcher.keyEquals(jobKey));
		
		if (this.scheduler.checkExists(jobKey)) {
			this.scheduler.rescheduleJob(trigMess.getKey(), trigMess);
		} else {
			this.scheduler.scheduleJob(jobMess, trigMess);
		}

	}
	
}