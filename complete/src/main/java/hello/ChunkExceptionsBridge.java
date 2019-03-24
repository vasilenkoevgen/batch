package hello;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by Evjen on 23.03.2019.
 */
public abstract class ChunkExceptionsBridge {

    private static final List<ValidationException> VALIDATION_EXCEPTIONS = new CopyOnWriteArrayList<>();

    protected void addException(ValidationException ex) {
        VALIDATION_EXCEPTIONS.add(ex);
    }

    protected List<String> getAllExceptions() {

        List<String> exceptions = VALIDATION_EXCEPTIONS
                .stream()
                .flatMap(e -> e.getStringList()
                        .stream())
                .collect(Collectors.toList());

        VALIDATION_EXCEPTIONS.clear();

        return exceptions;
    }

}
