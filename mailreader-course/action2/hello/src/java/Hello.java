import com.opensymphony.xwork.ActionSupport;

/**
 * Example Action class
 */
public class Hello extends ActionSupport {

    /**
     * An example implementation that does nothing an returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception {
        return SUCCESS;
    }
}
