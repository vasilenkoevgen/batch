package hello;

/**
 * Created by Evjen on 17.03.2019.
 */
public class PersonError {

    private String errorCause;

    public PersonError() {

    }

    public PersonError(String errorCause) {
        this.errorCause = errorCause;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    @Override
    public String toString() {
        return "PersonError{" +
                "errorCause='" + errorCause + '\'' +
                '}';
    }
}
