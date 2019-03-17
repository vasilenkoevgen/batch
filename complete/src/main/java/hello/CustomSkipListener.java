package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.stereotype.Component;

/**
 * Created by Evjen on 17.03.2019.
 */
@Component
public class CustomSkipListener<T> {

    private static final Logger log = LoggerFactory.getLogger(CustomSkipListener.class);

    @OnReadError
    public void onReadError(Exception ex) {
        log.info("read error = {}", ex.getMessage());
    }

    @OnSkipInRead
    public void onSkipRead(Throwable ex) {
        log.info("read skip = {}", ex.getMessage());
    }

//    @OnWriteError
//    public void onWriteError(Exception ex) {
//        log.info("write error = {}", ex.getMessage());
//    }

    @OnSkipInWrite
    public void onSkipWrite(T person, Throwable ex) {
        log.info("write skip = {}", ex.getMessage());
    }

}
