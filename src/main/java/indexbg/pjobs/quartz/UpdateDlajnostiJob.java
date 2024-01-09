package indexbg.pjobs.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.SysConstants;
import com.indexbg.system.db.JPA;
import com.indexbg.system.quartz.BaseJobResult;

import indexbg.pjobs.system.JobsJobsUpdater;
import indexbg.pjobs.system.SystemData;

@DisallowConcurrentExecution
public class UpdateDlajnostiJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDlajnostiJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("UpdateDlajnostiJob - Start");

        BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(SysConstants.JOB_STATUS_OK);
		
		
		
		try {
			
			
			SystemData sd = new SystemData();
			
			String result = new JobsJobsUpdater().updateJobsJobs(sd);
			jobResult.setComment(result);
			jobExecutionContext.setResult(jobResult);
			
			
		} catch (Exception e) {
			jobResult.setStatus(SysConstants.JOB_STATUS_ERROR);
			LOGGER.error(e.getMessage(), e);
			JobExecutionException ex = new JobExecutionException(e);
			ex.setRefireImmediately(false);
			throw ex;
		} finally {
			JPA.getUtil().closeConnection();
		}
		

        LOGGER.info("UpdateDlajnostiJob - End");
    }
}
