/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import com.opensymphony.xwork.Action;

/**
 *  Wraps a controller
 */
public class ControllerAction implements Action {

    protected Object controller;
    protected Object form;

    public void setController(Object w) {
        this.controller = w;
    }

    public Object getController() {
        return controller;
    }

    public Object getForm() {
        return form;
    }

    public void setForm(Object obj) {
        this.form  = obj;
    }


    public String execute() {

        // TODO
        return Action.SUCCESS;
    }

}
