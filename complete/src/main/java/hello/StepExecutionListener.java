package hello;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

/**
 * Created by Evjen on 17.03.2019.
 */
@Component
public class StepExecutionListener extends StepExecutionListenerSupport {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        super.beforeStep(stepExecution);
        stepExecution.getExecutionContext().put("myKey", "kkk");
    }
}
