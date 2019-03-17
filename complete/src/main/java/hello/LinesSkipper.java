package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by Evjen on 16.03.2019.
 */
@Component
public class LinesSkipper implements SkipPolicy {

    private static final Logger log = LoggerFactory.getLogger(LinesSkipper.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LinesSkipper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean shouldSkip(Throwable throwable, int i) throws SkipLimitExceededException {

//        log.info("ERROR = {}", throwable.getCause());
//
//        jdbcTemplate.update("INSERT INTO people_error (error_cause) VALUES (?)", throwable.getCause().getMessage());

        return true;
//        return false;
    }
}
