package org.apache.struts2.jquery;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.jquery.freemarker.models.JQueryFreemarkerModel;
import org.apache.struts2.jquery.components.JQueryForm;
import org.apache.struts2.jquery.components.JQueryHead;
import org.apache.struts2.jquery.components.JQueryReset;
import org.apache.struts2.jquery.components.JQuerySubmit;
import org.apache.struts2.jquery.components.JQueryTextField;
import org.apache.struts2.jquery.components.JQueryDatepicker;

/**
 * Describe your class here
 *
 * @author $Author$
 *         <p/>
 *         $Id$
 */
public class JQueryStrutsModels {

    protected ValueStack stack;
    protected HttpServletRequest req;
    protected HttpServletResponse res;

    protected JQueryFreemarkerModel form;
    protected JQueryFreemarkerModel head;
    protected JQueryFreemarkerModel reset;
    protected JQueryFreemarkerModel submit;
    protected JQueryFreemarkerModel textfield;
    protected JQueryFreemarkerModel datepicker;

    public JQueryStrutsModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    public JQueryFreemarkerModel getForm() {
        if (form == null ) {
            form = new JQueryFreemarkerModel(stack, req, res, JQueryForm.class);
        }
        return form;
    }

    public JQueryFreemarkerModel getHead() {
        if (head == null ) {
            head = new JQueryFreemarkerModel(stack, req, res, JQueryHead.class);
        }
        return head;
    }

    public JQueryFreemarkerModel getReset() {
        if (reset == null ) {
            reset = new JQueryFreemarkerModel(stack, req, res, JQueryReset.class);
        }
        return reset;
    }

    public JQueryFreemarkerModel getSubmit() {
        if (submit == null ) {
            submit = new JQueryFreemarkerModel(stack, req, res, JQuerySubmit.class);
        }
        return submit;
    }

    public JQueryFreemarkerModel getTextfield() {
        if (textfield == null ) {
            textfield = new JQueryFreemarkerModel(stack, req, res, JQueryTextField.class);
        }
        return textfield;
    }

    public JQueryFreemarkerModel getDatepicker() {
        if (datepicker == null ) {
            datepicker = new JQueryFreemarkerModel(stack, req, res, JQueryDatepicker.class);
        }
        return datepicker;
    }

}
