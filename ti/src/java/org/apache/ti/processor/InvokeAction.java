/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;
import com.opensymphony.xwork.config.entities.ActionConfig;


/**
 * Adds code to handle ControllerActions if detected.
 *
 * @see com.opensymphony.xwork.DefaultActionInvocation
 */
public interface InvokeAction {

    /**
     *  Invokes action.  If the action method contains one parameter, this method
     *  handles its execution.  Otherwise, it is delegated to the super class.
     */
    public String invokeAction(Object action, ActionConfig actionConfig) throws Exception;
        
}
