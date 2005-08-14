/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor.chain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;

import org.apache.commons.chain.*;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;
import com.opensymphony.xwork.config.entities.ActionConfig;

import com.opensymphony.xwork.ActionContext;

import org.apache.ti.processor.InvokeAction;

/**
 * Adds code to handle ControllerActions if detected.
 *
 * @see com.opensymphony.xwork.DefaultActionInvocation
 */
public class ChainInvokeAction implements InvokeAction {

    private static final Log log = LogFactory.getLog(ChainInvokeAction.class);

    protected String catalogName = "struts-ti";
    protected String executeCmdName = "executeAction";
    public static final String RESULT = "actionResult";

    public void setExecuteActionCommandName(String name) {
        this.executeCmdName = name;
    }
    
    public void setCatalogName(String name) {
        this.catalogName = name;
    }

    /**
     *  Invokes action.  If the action method contains one parameter, this method
     *  handles its execution.  Otherwise, it is delegated to the super class.
     */
    public String invoke(Object action, ActionConfig actionConfig) throws Exception {
        
        CatalogFactory factory = CatalogFactory.getInstance();
        Catalog cat = factory.getCatalog(catalogName);
        if (cat == null) {
            throw new IllegalStateException("Cannot find catalog '"
                    + catalogName + "'");
        }
        
        Command executeCmd = cat.getCommand(executeCmdName);
        if (executeCmd == null) {
            throw new IllegalStateException("Cannot find execute action command '"
                    + executeCmdName + "'");
        }
        
        Context ctx = (Context) ActionContext.getContext().get("webContext");
        
        executeCmd.execute(ctx);
        
        String result = (String) ctx.get(RESULT);
        
        return result;
    }
    
}
