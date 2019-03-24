package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
public class PersonItemProcessor extends ChunkExceptionsBridge implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);


    private final DataSource dataSource;

    @Autowired
    public PersonItemProcessor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
//    @Transactional
    public Person process(final Person person) throws Exception {
        ValidationException validationException = new ValidationException();
        validationException.addException("ex");
        addException(validationException);
//        final String firstName = person.getFirstName().toUpperCase();
//        final String lastName = person.getLastName().toUpperCase();
//
//        final Person transformedPerson = new Person(firstName, lastName);
//
////        log.info("Converting (" + person + ") into (" + transformedPerson + ")");
//
//        return transformedPerson;
//        JdbcTemplate jdbcTemplate = new JdbcTemplate();
//        jdbcTemplate.setDataSource(dataSource);
//        jdbcTemplate.update("insert into err(text) values('ex')");

        return person;
    }
}
