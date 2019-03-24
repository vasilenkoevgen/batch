package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		jdbcTemplate.execute("truncate table people");
		log.info("peoples truncated");
		jdbcTemplate.execute("truncate table people_error");
		log.info("people_errors truncated");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("executed in {}", jobExecution.getEndTime().getTime() - jobExecution.getCreateTime().getTime());
			log.info("!!! JOB FINISHED! Time to verify the results");

			Long errorsCount = jdbcTemplate.queryForObject("SELECT count(error_cause) FROM people_error", Long.class);

			Long totalCounts = jdbcTemplate.queryForObject("SELECT count(first_name) FROM people", Long.class);

			log.info("total counts = {}", totalCounts);
			log.info("error counts = {}", errorsCount);

			Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();

//			try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream("F:/datas.zip"))) {
//
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

//			while (true){
//
//			}

		}
	}
}
