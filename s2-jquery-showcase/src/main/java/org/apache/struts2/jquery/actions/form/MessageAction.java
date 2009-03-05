package org.apache.struts2.jquery.actions.form;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.InterceptorRef;


@Namespace("/form")
@InterceptorRef("jsonValidationWorkflowStack")
public class MessageAction extends ActionSupport {

    private String msg;

    @Actions({
            @Action("SimpleFormGetAjaxResponse"),
            @Action("SimpleFormPostAjaxResponse"),
            @Action("SimpleFormGetNonAjaxResponse"),
            @Action("SimpleFormPostNonAjaxResponse"),
            @Action("FormWithResetGetReqAjaxResp"),
            @Action("FormWithResetPostReqAjaxResp")
    })
    public String execute() {
        return ActionSupport.SUCCESS;
    }

    @SkipValidation
    public String input() {
        return ActionSupport.INPUT;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
