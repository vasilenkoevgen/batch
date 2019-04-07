package hello;

import org.apache.commons.collections.set.UnmodifiableSet;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Evjen on 22.03.2019.
 */
@Component
public class StepListener extends StepExecutionListener {


    private static Set<String> innSet;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return super.afterStep(stepExecution);
    }

    @SuppressWarnings("unchecked")
    public void getSet() {
        Set<String> newSet = new HashSet<>();

        innSet = UnmodifiableSet.decorate(newSet);
    }
}
