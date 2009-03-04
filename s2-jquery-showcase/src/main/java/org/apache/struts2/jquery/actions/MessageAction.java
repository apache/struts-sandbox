package org.apache.struts2.jquery.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class MessageAction extends ActionSupport {

    private String msg ;

    @Override
    public String execute() {
        return ActionSupport.SUCCESS;
    }

    @SkipValidation @Override
    public String input() {
        return ActionSupport.INPUT;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg ;
    }

}
