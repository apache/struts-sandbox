package blank2;

import com.opensymphony.xwork.ActionSupport;

/**
 * Utilize the SUCCESS result.
 */
public class Home extends ActionSupport {

    /**
     * Return the default SUCCESS token.
     *
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception {
        return SUCCESS;
    }
}
