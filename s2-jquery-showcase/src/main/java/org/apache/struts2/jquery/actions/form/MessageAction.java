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
            @Action("/simple-theme/SimpleFormPostAjaxResponse"),
            @Action("/simple-theme/SimpleFormPostNonAjaxResponse"),
            @Action("/simple-theme/FormWithResetPostReqAjaxResp"),
            @Action("SimpleFormGetAjaxResponse"),
            @Action("SimpleFormPostAjaxResponse"),
            @Action("SimpleFormGetNonAjaxResponse"),
            @Action("SimpleFormPostNonAjaxResponse"),
            @Action("FormWithResetGetReqAjaxResp"),
            @Action("FormWithResetPostReqAjaxResp")
    }) @Override 
    public String execute() {
        return ActionSupport.SUCCESS;
    }

    @Actions({
            @Action("/simple-theme/SimpleFormPostAjaxResponse-input"),
            @Action("/simple-theme/SimpleFormPostNonAjaxResponse-input"),
            @Action("/simple-theme/FormWithResetPostReqAjaxResp-input"),
            @Action("SimpleFormGetAjaxResponse-input"),
            @Action("SimpleFormPostAjaxResponse-input"),
            @Action("SimpleFormGetNonAjaxResponse-input"),
            @Action("SimpleFormPostNonAjaxResponse-input"),
            @Action("FormWithResetGetReqAjaxResp-input"),
            @Action("FormWithResetPostReqAjaxResp-input")
    }) @SkipValidation @Override
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
