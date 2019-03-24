package hello;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

/**
 * Created by Evjen on 22.03.2019.
 */
@Component
public class StepListener extends StepExecutionListener {



    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return super.afterStep(stepExecution);
    }
}
