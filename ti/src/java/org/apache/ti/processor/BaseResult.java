/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;

/**
 *  Wraps a controller.
 */
public abstract class BaseResult implements Result {

    protected String location;

    public void setLocation(String loc) {
        this.location = loc;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        // perform processing like evaluating location argument as expression
        doExecute(location, invocation);
    }

    protected abstract void doExecute(String path, ActionInvocation invocation) throws Exception;

}
