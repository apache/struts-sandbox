package org.apache.struts2.jquery.actions.form;

import com.opensymphony.xwork2.ActionSupport;

import java.util.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;

/**
 * Created by IntelliJ IDEA.
 * User: wesw
 * Date: Mar 10, 2009
 * Time: 3:18:06 PM
 * To change this template use File | Settings | File Templates.
 */
@InterceptorRef("jsonValidationWorkflowStack")
public class SimpleFormWithDatePickerAction extends ActionSupport {

    private String msg ;
    private Date date ;

    @Override @Action("/form/simple-form-with-date-picker")
    public String execute() {
        return ActionSupport.SUCCESS;
    }

    @Override @Action("/form/simple-form-with-date-picker-input")
    public String input() {
        return ActionSupport.INPUT;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
