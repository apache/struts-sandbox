package blank2;

import com.opensymphony.xwork.ActionSupport;

/**
 * <code>HomeAction</code>
 */
public class HomeAction extends ActionSupport {

    /**
     * A default implementation that does nothing an returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception {
        return SUCCESS;
    }
}
