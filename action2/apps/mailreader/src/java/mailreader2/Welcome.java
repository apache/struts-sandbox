package mailreader2;

import org.apache.struts.apps.mailreader.dao.UserDatabase;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

/**
 * Verify that essential resources are available.
 */
public class Welcome extends MailreaderSupport {

    public String execute() throws Exception {

        // Confirm message resources loaded
        String message = getText(Constants.ERRORS_REQUIRED);
        if (Constants.ERRORS_REQUIRED.equals(message)) {
            addActionError(Constants.ERROR_MESSAGES_NOT_LOADED);
        }

        // Confirm database loaded
        UserDatabase database = getDatabase();
        if (null==database) {
             addActionError(Constants.ERROR_DATABASE_NOT_LOADED);
        }

        if (hasErrors()) {
            return Action.ERROR;
        }
        else {
            return ActionSupport.SUCCESS;
        }
    }
}
