package actions;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;


public final class LocaleAction extends Action {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {

        String language = request.getParameter(LANGUAGE);
        String country = request.getParameter(COUNTRY);

        Locale locale = getLocale(request);

        if ((language != null && language.length() > 0) &&
                (country != null && country.length() > 0)) {
            locale = new java.util.Locale(language, country);
        } else if (language != null && language.length() > 0) {
            locale = new java.util.Locale(language, "");
        }

        setLocale(request, locale);

        return mapping.findForward(SUCCESS);
    }

    private static final String LANGUAGE = "language";
    private static final String COUNTRY = "country";
    private static final String SUCCESS = "success";

}
