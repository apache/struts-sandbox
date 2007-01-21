package actions;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloAction extends Action {

    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        HelloForm input = (HelloForm) form;
        input.setMessage(getResources(request).getMessage(MESSAGE));
        return mapping.findForward(SUCCESS);
    }

    public static final String MESSAGE = "message";
    public static final String SUCCESS = "success";

}
