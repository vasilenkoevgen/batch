package hello;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evjen on 23.03.2019.
 */
public class ValidationException {

    private List<String> stringList = new ArrayList<>();

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public void addException(String ex) {
        this.stringList.add(ex);
    }
}
