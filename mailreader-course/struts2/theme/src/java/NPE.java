import com.opensymphony.xwork2.Action;

public class NPE implements Action {

    public String execute() {
        throw new NullPointerException("Oops!");
    }

}
