package indexbg.pjobs.system;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.BaseSystemData;
import com.indexbg.system.SysConstants;
import com.indexbg.system.SystemDataSynchronizer;
import com.indexbg.system.db.JPA;

/**
 * Допълнително синхронизира и динамични класификации за системата
 *
 * @author belev
 */
public class PJobsSystemDataSynchronizer extends SystemDataSynchronizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(PJobsSystemDataSynchronizer.class);

	/**
	 * @param systemData
	 * @param seconds
	 */
	public PJobsSystemDataSynchronizer(BaseSystemData systemData, Long seconds) {
		super(systemData, seconds);
	}

	/** */
	@Override
	protected void processDynamicClassif(Date h2Date) {
		LOGGER.debug("processDynamicClassif -> RUN <-");

		try {
			String sql = "select max (a.id) as syncme from jobs_administration a having max (a.date_reg) > :h2Date or max (a.date_last_mod) > :h2Date";

			Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter("h2Date", h2Date);

			@SuppressWarnings("unchecked")
			List<Object> syncme = query.getResultList();

			if (!syncme.isEmpty()) { // ако има значи трябва да се синхронизира
				LOGGER.info("Start reset dynamic classifications: SystemData={}, h2Date={}", this.systemData.getClass().getName(), h2Date);
				LOGGER.info("\tCODE_CLASSIF={}", Constants.CODE_SYSCLASS_ADM_REGISTER);

				this.systemData.reloadClassif(Constants.CODE_SYSCLASS_ADM_REGISTER);
				// да се зареди наново
				this.systemData.getSysClassification(Constants.CODE_SYSCLASS_ADM_REGISTER, new Date(), SysConstants.CODE_DEFAULT_LANG, -1l);
				
				this.systemData.reloadClassif(Constants.CODE_SYSCLASS_ADM_FLAT);
				// да се зареди наново
				this.systemData.getSysClassification(Constants.CODE_SYSCLASS_ADM_FLAT, new Date(), SysConstants.CODE_DEFAULT_LANG, -1l);
			}

		} catch (Exception e) {
			LOGGER.error("SystemData synchronization processDynamicClassif ERROR", e);
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
}
