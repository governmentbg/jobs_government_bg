package indexbg.pjobs.system;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.utils.DateUtils;
import com.mchange.v1.util.UnexpectedException;

import indexbg.pjobs.db.Jobs;
import indexbg.pjobs.wsclient.stJobs.ArrayOfJobClassification;
import indexbg.pjobs.wsclient.stJobs.JobClassification;
import indexbg.pjobs.wsclient.stJobs.JobClassifierCode;
import indexbg.pjobs.wsclient.stJobs.StJobs;
import indexbg.pjobs.wsclient.stJobs.StJobs_Service;


public final class JobsJobsUpdater {

	static final Logger LOGGER = LoggerFactory.getLogger(JobsJobsUpdater.class);

    

    @SuppressWarnings("unchecked")
	public String  updateJobsJobs(SystemData sd)	 throws java.lang.Exception {
    	
    	ArrayList<Jobs> jobsNew = null;
    	Date dat = DateUtils.startDate(new Date());
    	
    	String result = null;
    	
    	try {
    		
    		
    		
    		LOGGER.info("Start of method updateJobsJobs with date: " + dat);
    		
    		
        	
        	
        	
        	Long userId = -1L;
        	Long lang = Constants.CODE_DEFAULT_LANG;
        	
        	//URL wsdlURL = new URL("http://192.168.126.33:81/WebServices/StJobs.svc?wsdl");
        	
        	LOGGER.info("Getting WS Url from SYSTEM_OPTIONS");
        	String url = sd.getSettingsValue("PJOBS.stJobsURL");
        	if (url == null || url.isEmpty()) {
        		throw new UnexpectedException("Не може да се извлече настройката PJOBS.stJobsURL");
        	}
        	LOGGER.info("SystemData returns URL=" + url);
        	
        	
        	LOGGER.info("Start loading data from " + url);
        	jobsNew = downloadJobsFromWS(sd, lang, userId, url );
        	LOGGER.info("End Loading data. " + jobsNew.size() + " items found");
        	
        	result = "Услугата върна " + jobsNew.size() + " записа";
        	
    	
		
	    } catch (Exception e) {
			throw e;
		}
        	
        	
        	
        	
        try {
        	LOGGER.info("Start merging and saving data data ...");
        	
        	JPA.getUtil().begin();
        	
        	
        	
        	LOGGER.info("---> Size New: " + jobsNew.size());
        	ArrayList<Jobs> jobsOld = (ArrayList<Jobs>) JPA.getUtil().getEntityManager().createQuery("from Jobs where status = 'true'").getResultList();
        	LOGGER.info("---> Size Old: " + jobsOld.size());
        	
        	for (Jobs job : jobsNew) {
        		
        		if (!job.isStatus()) {
        			continue;
        		}
        		
        		boolean found = false;
        		for (Jobs jobO : jobsOld) {
        			if (job.getJobId().equals(jobO.getJobId())) {
        				found = true;  
//        				if (job.getJobId().equals(3423L)) {
//        					System.out.println(job.getJobName());
//        				}
        				
        				int diff = compareJobs(jobO, job);        				
        				
        				if (diff > 0) {
        					jobO.setStatus(false);
        					jobO.setDateTo(dat);
        					job.setDateFrom(dat);
        					JPA.getUtil().getEntityManager().persist(job);
        					JPA.getUtil().getEntityManager().persist(jobO);
        					LOGGER.debug("Saving "+ jobO.getJobName());
            				LOGGER.debug("Saving "+ job.getJobName());
        				}
        				
        				break;
        			}
        		}
        		if (! found) {
        			if (job.isStatus()) {
        				job.setDateFrom(dat);
        				JPA.getUtil().getEntityManager().persist(job);
        				LOGGER.debug("Saving "+ job.getJobName());
        			}
        		}
        	}
        	
        	JPA.getUtil().commit();
        	
        	LOGGER.info("End merging and saving data data ...");
			
		} catch (Exception e) {
			JPA.getUtil().rollback();
			throw e;
		}finally {
			JPA.getUtil().closeConnection();
		}
		
        LOGGER.info("End of method updateJobsJobs ");
        return result;
    }
    
    
    public static Long findItemCode(List<SystemClassif> classif, String tekst) {
    	
    	
    	if (tekst == null) {
    		return null;
    	}else {
    		tekst = tekst.trim();
    	}
    	
    	for (SystemClassif tek : classif) {
    		if (tek.getCodeExt() != null && tek.getCodeExt().trim().equalsIgnoreCase(tekst)) {
    			return tek.getCode();
    		}else {
    			if (tek.getTekst() != null && tek.getTekst().trim().equalsIgnoreCase(tekst)) {
    				return tek.getCode();
    			}
    		}
    	}
    	
    	LOGGER.error("Значението " + tekst + " не може да бъде намерено в класификация с код " + classif.get(0).getCodeClassif());
    	return null;
    }
    
    
    public static ArrayList<Jobs> downloadJobsFromWS(SystemData sd, Long lang, Long userId, String url) throws Exception{
    	
    	ArrayList<Jobs> jobs = new ArrayList<Jobs>();
    	
    	try {
			//Длъжностно ниво CODE_SYSCLASS_POSITION_LEVEL
			List<SystemClassif> levelClassif = sd.getSysClassification(Constants.CODE_SYSCLASS_POSITION_LEVEL, new Date(), lang, userId );
			
			//Правоотношение  CODE_SYSCLASS_TREATMENT
			List<SystemClassif> pravoClassif = sd.getSysClassification(Constants.CODE_SYSCLASS_TREATMENT, new Date(), lang, userId );
			
			//Ранг CODE_SYSCLASS_RANK
			List<SystemClassif> rangClassif = sd.getSysClassification(Constants.CODE_SYSCLASS_RANK, new Date(), lang, userId );
			
			//Степен на образование CODE_SYSCLASS_EDUCATION_DEGREE
			List<SystemClassif> obrClassif = sd.getSysClassification(Constants.CODE_SYSCLASS_EDUCATION_DEGREE, new Date(), lang, userId );
			
			
			
			QName serviceName = new QName("https://eisuchrda.egov.bg/IISDAIntegrationServices/Common", "StJobs");
			URL wsdlURL = new URL(url);
			
			StJobs_Service ss = new StJobs_Service(wsdlURL, serviceName);
			StJobs port = ss.getBasicHttpBindingStJobs();
			
			JobClassifierCode code =  JobClassifierCode.KDA;
			ArrayOfJobClassification res = port.getJobsClassification(code);
			
			
			
			
			for (JobClassification tek : res.getJobClassification()) {
				
				Jobs job = new Jobs();
				
								
				if (tek.getJobCode() != null) {
					job.setJobCode(tek.getJobCode().getValue());
				}
				
				if (tek.getJobId() != null) {
					job.setJobId(tek.getJobId().getValue());
				}
				
				if (tek.getParentId() != null) {
					job.setParentId(tek.getParentId().getValue());
				}
				
				if (tek.getJobName() != null) {
					job.setJobName(tek.getJobName().getValue());
				}
				
				if (tek.getJObShortName() != null) {
					job.setJobShortName(tek.getJObShortName().getValue());
				}
				
				if (tek.getJobLevelCode() != null) {
					job.setLevelCode(tek.getJobLevelCode().getValue());
				}
				
				if (tek.getJobLevelName() != null) {
					job.setLevelName(tek.getJobLevelName().getValue());					
					Long scCode = findItemCode(levelClassif, job.getLevelName());
					job.setLevelNameSc(scCode);					
				}
				
				if (tek.getMinimumProfExperience() != null) {
					job.setMinProofExperience(tek.getMinimumProfExperience().getValue());
				}
				
				if (tek.getMinimumQualification() != null && tek.getMinimumQualification().getValue() != null) {
					job.setMinQualification(tek.getMinimumQualification().getValue().value());					
					Long scCode = findItemCode(obrClassif, job.getMinQualification());
					job.setMinQualificationSc(scCode);					
				}
				
				
				
				if (tek.getMinimumRang() != null && tek.getMinimumRang().getValue() != null && tek.getMinimumRang().getValue().getMinimumRangName() != null) {
					job.setMinRang(tek.getMinimumRang().getValue().getMinimumRangName().getValue());
					
					Long scCode = findItemCode(rangClassif, job.getMinRang());
					job.setMinRangnSc(scCode);
					//System.out.println(scCode);
				}
				
				
				if (tek.getMinimumRang() != null && tek.getMinimumRang().getValue() != null && tek.getMinimumRang().getValue().getMinimumRangId() != null) {
					job.setMinRangCode(Long.valueOf(tek.getMinimumRang().getValue().getMinimumRangId().longValue()));
				}
				
				
				if (tek.getJobClassificationRowType() != null ) {
					job.setTypeItem(tek.getJobClassificationRowType().value());
					
					
				}
				
				
				if (tek.getServiceType() != null && tek.getServiceType().getValue() != null) {
					job.setPravo(tek.getServiceType().getValue().value());
					
					Long scCode = findItemCode(pravoClassif, job.getPravo());
					job.setPravoSc(scCode);
					//System.out.println(scCode);
				}
				
				
				if (job.getJobName() == null || job.getJobName().isEmpty()) {
					continue;
				}
				
				job.setStatus(tek.status);
				jobs.add(job);
				
				//JPA.getUtil().getEntityManager().persist(job);
			
			}
		} catch (Exception e) {
			LOGGER.error("Грешка при извличане на данни от услугата !!!", e);	
			throw e;
		}
    	
    	return jobs;
    	
    }
    
    public static int compareJobs(Jobs job1, Jobs job2) {
    	
    	
    	
    	if (job1.getJobCode() == null && job2.getJobCode() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getJobCode() == null && job2.getJobCode() != null) || (job1.getJobCode() != null && job2.getJobCode() == null)) {
    			return 1;
    		}else {
    			if(! job1.getJobCode().equals(job2.getJobCode())){
    				return 1;
    			}
    		}
    	}
    	
    	if (job1.getJobName() == null && job2.getJobName() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getJobName() == null && job2.getJobName() != null) || (job1.getJobName() != null && job2.getJobName() == null)) {
    			return 2;
    		}else {
    			if(! job1.getJobName().equals(job2.getJobName())){
    				return 2;
    			}
    		}
    	}
    	
    	
    	if (job1.getJobShortName() == null && job2.getJobShortName() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getJobShortName() == null && job2.getJobShortName() != null) || (job1.getJobShortName() != null && job2.getJobShortName() == null)) {
    			return 3;
    		}else {
    			if(! job1.getJobShortName().equals(job2.getJobShortName())){
    				return 3;
    			}
    		}
    	}
    	
    	
    	if (job1.getLevelCode() == null && job2.getLevelCode() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getLevelCode() == null && job2.getLevelCode() != null) || (job1.getLevelCode() != null && job2.getLevelCode() == null)) {
    			return 4;
    		}else {
    			if(! job1.getLevelCode().equals(job2.getLevelCode())){
    				return 4;
    			}
    		}
    	}
    	
    	if (job1.getLevelName() == null && job2.getLevelName() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getLevelName() == null && job2.getLevelName() != null) || (job1.getLevelName() != null && job2.getLevelName() == null)) {
    			return 5;
    		}else {
    			if(! job1.getLevelName().equals(job2.getLevelName())){
    				return 5;
    			}
    		}
    	}
    	
    	if (job1.getLevelNameSc() == null && job2.getLevelNameSc() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getLevelNameSc() == null && job2.getLevelNameSc() != null) || (job1.getLevelNameSc() != null && job2.getLevelNameSc() == null)) {
    			return 6;
    		}else {
    			if(! job1.getLevelNameSc().equals(job2.getLevelNameSc())){
    				return 6;
    			}
    		}
    	}
    	
    	if (job1.getMinProofExperience() == null && job2.getMinProofExperience() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getMinProofExperience() == null && job2.getMinProofExperience() != null) || (job1.getMinProofExperience() != null && job2.getMinProofExperience() == null)) {
    			return 7;
    		}else {
    			if(! job1.getMinProofExperience().equals(job2.getMinProofExperience())){
    				return 7;
    			}
    		}
    	}
    	
    	if (job1.getMinQualification() == null && job2.getMinQualification() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getMinQualification() == null && job2.getMinQualification() != null) || (job1.getMinQualification() != null && job2.getMinQualification() == null)) {
    			return 8;
    		}else {
    			if(! job1.getMinQualification().equals(job2.getMinQualification())){
    				return 8;
    			}
    		}
    	}
    	
    	if (job1.getMinQualificationSc() == null && job2.getMinQualificationSc() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getMinQualificationSc() == null && job2.getMinQualificationSc() != null) || (job1.getMinQualificationSc() != null && job2.getMinQualificationSc() == null)) {
    			return 9;
    		}else {
    			if(! job1.getMinQualificationSc().equals(job2.getMinQualificationSc())){
    				return 9;
    			}
    		}
    	}
    	
    	if (job1.getMinRang() == null && job2.getMinRang() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getMinRang() == null && job2.getMinRang() != null) || (job1.getMinRang() != null && job2.getMinRang() == null)) {
    			return 10;
    		}else {
    			if(! job1.getMinRang().equals(job2.getMinRang())){
    				return 10;
    			}
    		}
    	}
    	
    	if (job1.getMinRangCode() == null && job2.getMinRangCode() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getMinRangCode() == null && job2.getMinRangCode() != null) || (job1.getMinRangCode() != null && job2.getMinRangCode() == null)) {
    			return 11;
    		}else {
    			if(! job1.getMinRangCode().equals(job2.getMinRangCode())){
    				return 11;
    			}
    		}
    	}
    	
    	if (job1.getMinRangnSc() == null && job2.getMinRangnSc() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getMinRangnSc() == null && job2.getMinRangnSc() != null) || (job1.getMinRangnSc() != null && job2.getMinRangnSc() == null)) {
    			return 12;
    		}else {
    			if(! job1.getMinRangnSc().equals(job2.getMinRangnSc())){
    				return 12;
    			}
    		}
    	}
    	
    	if (job1.getParentId() == null && job2.getParentId() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getParentId() == null && job2.getParentId() != null) || (job1.getParentId() != null && job2.getParentId() == null)) {
    			return 13;
    		}else {
    			if(! job1.getParentId().equals(job2.getParentId())){
    				return 13;
    			}
    		}
    	}
    	
    	if (job1.getPravo() == null && job2.getPravo() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getPravo() == null && job2.getPravo() != null) || (job1.getPravo() != null && job2.getPravo() == null)) {
    			return 14;
    		}else {
    			if(! job1.getPravo().equals(job2.getPravo())){
    				return 14;
    			}
    		}
    	}
    	
    	if (job1.getPravoSc() == null && job2.getPravoSc() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getPravoSc() == null && job2.getPravoSc() != null) || (job1.getPravoSc() != null && job2.getPravoSc() == null)) {
    			return 15;
    		}else {
    			if(! job1.getPravoSc().equals(job2.getPravoSc())){
    				return 15;
    			}
    		}
    	}
    	
    	if (job1.getTypeItem() == null && job2.getTypeItem() == null) {
    		//Няма промяна
    	}else {
    		if ((job1.getTypeItem() == null && job2.getTypeItem() != null) || (job1.getTypeItem() != null && job2.getTypeItem() == null)) {
    			return 16;
    		}else {
    			if(! job1.getTypeItem().equals(job2.getTypeItem())){
    				return 16;
    			}
    		}
    	}
    	
    	
    	return 0;
    	
    	
    }
    

}
