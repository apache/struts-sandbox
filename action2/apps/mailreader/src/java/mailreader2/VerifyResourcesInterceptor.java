package mailreader2;

import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.*;

import java.util.Map;

import org.apache.struts.apps.mailreader.dao.UserDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VerifyResourcesInterceptor implements Interceptor  {

    protected static final Log log = LogFactory.getLog(VerifyResourcesInterceptor.class);

    public void destroy () {}

    public void init() {}

    private void addError(ValidationAware validation, String message) {
        if (validation != null) {
            validation.addActionError(message);
        }
        log.error(message);
    }

    public String intercept(ActionInvocation invocation) throws Exception {

        int errors = 0;

        Map application = invocation.getInvocationContext().getApplication();

        ValidationAware validation = null;

        final Object action = invocation.getAction();
        if (action instanceof ValidationAware) {
            validation = (ValidationAware) action;
        }

        // Confirm message resources loaded
        if (action instanceof TextProvider) {
            TextProvider tp = (TextProvider) action;
            String message = tp.getText(Constants.ERRORS_REQUIRED);
            if (null==message) {
                addError(validation,Constants.ERROR_MESSAGES_NOT_LOADED);
                errors++;
            }
        }

        // Confirm database loaded
        UserDatabase database = (UserDatabase) application.get(Constants.DATABASE_KEY);
        if (null==database) {
              try {
                  validation.addActionError(Constants.ERROR_DATABASE_NOT_LOADED);
              }
              catch (NullPointerException e) {
                  log.error(e);
              }
              errors++;
        }

        if (errors>0) {
            return Action.ERROR;
        }
        else {
            return invocation.invoke();
        }

    }
}
