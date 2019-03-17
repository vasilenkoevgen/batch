package hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.HibernateItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
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

    // tag::readerwriterprocessor[]
    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader1(@Value("#{jobParameters['inputFile']}") String datas) throws IOException {

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(datas));
        InputStreamSource inputStreamSource = null;
        ZipEntry entry = null;

        do {
            entry = zipInputStream.getNextEntry();
            if (entry.getName().equals("sample-data_1.csv")) {
                inputStreamSource = new InputStreamResource(zipInputStream);
            }
        } while (inputStreamSource == null);


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

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(datas));
        InputStreamSource inputStreamSource = null;
        ZipEntry entry = null;

        do {
            entry = zipInputStream.getNextEntry();
            if (entry.getName().equals("sample-data_2.csv")) {
                inputStreamSource = new InputStreamResource(zipInputStream);
            }
        } while (inputStreamSource == null);


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
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Person> jpaItemWriter(EntityManagerFactoryBuilder entityManagerFactoryBuilder) {
        RepositoryItemWriter<Person> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(personRepository);
        repositoryItemWriter.setMethodName("save");

        return repositoryItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
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
                .flow(step1)
                .next(step2)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Person> writer,
                      LinesSkipper linesSkipper,
                      CustomSkipListener<Person> customSkipListener,
                      RepositoryItemWriter<Person> repositoryItemWriter) throws IOException {

        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setMaxPoolSize(4);
        threadPoolExecutor.setCorePoolSize(4);
        threadPoolExecutor.afterPropertiesSet();

        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(2000)
                .reader(reader1(null)).faultTolerant().skipPolicy(linesSkipper)
                .processor(processor())
                .writer(repositoryItemWriter)
                .listener(customSkipListener)
                .taskExecutor(threadPoolExecutor)
                .build();
    }

    @Bean
    public Step step2(JdbcBatchItemWriter<Person> writer,
                      LinesSkipper linesSkipper,
                      RepositoryItemWriter<Person> repositoryItemWriter) throws IOException {
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setMaxPoolSize(4);
        threadPoolExecutor.setCorePoolSize(4);
        threadPoolExecutor.afterPropertiesSet();

        return stepBuilderFactory.get("step2")
                .<Person, Person>chunk(2000)
                .reader(reader2(null)).faultTolerant().skipPolicy(linesSkipper)
                .processor(processor())
                .writer(repositoryItemWriter)
                .taskExecutor(threadPoolExecutor)
                .build();
    }
    // end::jobstep[]
}
