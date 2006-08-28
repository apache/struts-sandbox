package cookbook2.pojo;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.ModelDriven;

public class Result extends ActionSupport implements ModelDriven {

    private DirectoryEntry directoryEntry = new DirectoryEntry();

    public Object getModel() {
        return directoryEntry;
    }

}
