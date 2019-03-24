package hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public PersonRepository personRepository;

    ZipFile zipFile;
    ZipFile zipFile2;

//    @Bean
//    @StepScope


    // tag::readerwriterprocessor[]
    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader1(@Value("#{jobParameters['inputFile']}") String datas) throws IOException {

        zipFile = new ZipFile(datas);
        ZipEntry entry = zipFile.getEntry("test.zip");
        ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream(entry));
        InputStreamSource inputStreamSource = null;

        do {
            entry = zipInputStream.getNextEntry();
            if (entry.getName().equals("sample-data_1.csv")) {
                inputStreamSource = new InputStreamResource(zipInputStream);
            }
        } while (inputStreamSource == null);

//        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(datas));
//        InputStreamResource inputStreamSource = null;
//        ZipEntry entry = null;
//
//        zipInputStream.getNextEntry();
//        ZipInputStream zipInputStream1 = new ZipInputStream(zipInputStream);
//
//        do {
//            entry = zipInputStream1.getNextEntry();
//            if (entry.getName().equals("sample-data_1.csv")) {
//                inputStreamSource = new InputStreamResource(zipInputStream1);
//            }
//        } while (inputStreamSource == null);


        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource((Resource) inputStreamSource)
//                .resource(new ClassPathResource("sample-data_1.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    // tag::readerwriterprocessor[]
    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader2(@Value("#{jobParameters['inputFile']}") String datas) throws IOException {

        zipFile2 = new ZipFile(datas);
        ZipEntry entry = zipFile.getEntry("test.zip");
        ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream(entry));
        InputStreamSource inputStreamSource = null;

        do {
            entry = zipInputStream.getNextEntry();
            if (entry.getName().equals("sample-data_2.csv")) {
                inputStreamSource = new InputStreamResource(zipInputStream);
            }
        } while (inputStreamSource == null);

//        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(datas));
//        InputStreamResource inputStreamSource = null;
//        ZipEntry entry = null;
//
//        zipInputStream.getNextEntry();
//        ZipInputStream zipInputStream1 = new ZipInputStream(zipInputStream);
//
//        do {
//        entry = zipInputStream1.getNextEntry();
//        if (entry.getName().equals("sample-data_2.csv")) {
//            inputStreamSource = new InputStreamResource(zipInputStream1);
//        }
//        } while (inputStreamSource == null);


        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource((Resource) inputStreamSource)
//                .resource(new ClassPathResource("sample-data_2.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<Person, Future<Person>> processor(DataSource dataSource) throws Exception {
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(4);

        AsyncItemProcessor<Person, Person> processor = new AsyncItemProcessor<>();
        processor.setTaskExecutor(simpleAsyncTaskExecutor);
        processor.setDelegate(new PersonItemProcessor(dataSource));
        processor.afterPropertiesSet();

        return processor;
    }

    @Bean
    @Qualifier(value = "asyncItemWriter")
    public ItemWriter<Future<Person>> asyncItemWriter(ItemWriter<Person> jdbcBatchItemWriter) {
        AsyncItemWriter<Person> itemWriter = new AsyncItemWriter<>();
        itemWriter.setDelegate(jdbcBatchItemWriter);

        return itemWriter;
    }

    @Bean
    @Qualifier(value = "jdbcBatchItemWriter")
    public ItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1, Step step2) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .listener(jobExecutionListenerSupport())
                .flow(step1)
//                .next(step2)
                .end()
                .build();
    }

    @Bean
    public CustomSkipListener<Person> customSkipListener() {
        CustomSkipListener<Person> customSkipListener = new CustomSkipListener<>();
        return customSkipListener;
    }

    @Bean
    public Step step1(ItemWriter<Future<Person>> asyncItemWriter,
                      LinesSkipper linesSkipper,
                      CustomChunkListener customChunkListener,
                      DataSource dataSource) throws Exception {

        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setMaxPoolSize(4);
        threadPoolExecutor.setCorePoolSize(4);
        threadPoolExecutor.afterPropertiesSet();

        return stepBuilderFactory.get("step1")
                .<Person, Future<Person>>chunk(2000)
                .reader(reader1(null))
                .faultTolerant().skipPolicy(linesSkipper)
                .processor(processor(dataSource))
                .writer(asyncItemWriter)
                .listener(customChunkListener)
//                .taskExecutor(threadPoolExecutor)
                .build();
    }

//    @Bean
//    public Step step2(JdbcBatchItemWriter<Person> writer,
//                      LinesSkipper linesSkipper,
//                      RepositoryItemWriter<Person> repositoryItemWriter) throws IOException {
//        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
//        threadPoolExecutor.setMaxPoolSize(4);
//        threadPoolExecutor.setCorePoolSize(4);
//        threadPoolExecutor.afterPropertiesSet();
//
//        return stepBuilderFactory.get("step2")
//                .<Person, Person>chunk(2000)
//                .reader(reader2(null)).faultTolerant().skipPolicy(linesSkipper)
//                .processor(processor())
//                .writer(repositoryItemWriter)
//                //.taskExecutor(threadPoolExecutor)
//                .build();
//    }
//    // end::jobstep[]

    @Bean
    public JobExecutionListenerSupport jobExecutionListenerSupport() {


        return new JobExecutionListenerSupport() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                try {
                    zipFile.close();
//                    zipFile2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
