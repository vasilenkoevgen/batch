package hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * Created by Evjen on 06.04.2019.
 */

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
//@DataJpaTest
//@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class })
public class BatchTest {

//    @Autowired
//    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job baseJob;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobRepository jobRepository;

//    private JobRepository jobRepository() {
//        MapJobRepositoryFactoryBean factoryBean = new MapJobRepositoryFactoryBean(new ResourcelessTransactionManager());
//        try {
//            JobRepository jobRepository = factoryBean.getObject();
//            return jobRepository;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    @MockBean
//    private JobCompletionNotificationListener jobCompletionNotificationListener;
//    @MockBean
//    private LinesSkipper linesSkipper;
//    @MockBean
//    private CustomChunkListener chunkListener;


    @Test
    public void testBatch() throws Exception {
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJob(baseJob);
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJobRepository(jobRepository);

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("inputFile", "F:/datas.zip");
        jobParametersBuilder.addString("myKey", "kkk");
        jobParametersBuilder.addLong("date", new Date().getTime());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());

        System.out.println("test");


    }
}
