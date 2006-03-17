package cookbook2.pojo;

import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.ActionSupport;

public class Result extends ActionSupport implements ModelDriven {

    private DirectoryEntry directoryEntry = new DirectoryEntry();

    public Object getModel() {
        return directoryEntry;
    }

    public String method1() {

        directoryEntry.setHours(new Integer(37));

        return SUCCESS;
    }
}
