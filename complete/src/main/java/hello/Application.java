package hello;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableBatchProcessing
@EnableJpaRepositories
public class Application {

    public static void main(String[] args) throws Exception {

        String[] newArgs = new String[] {"inputFile=F:/datas.zip"};

        SpringApplication.run(Application.class, newArgs);
    }
}
