import com.opensymphony.xwork.Action;

public class NPE implements Action {

    public String execute() {
        throw new NullPointerException("Oops!");
    }

}
