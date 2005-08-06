/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.config.entities.ActionConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Builds actions from the config.  If an Action is not created, it is assumed to be a Controller.
 */
public class ControllerObjectFactory extends ObjectFactory {

    private static final Log log = LogFactory.getLog(ControllerObjectFactory.class);

    /**
     * Build an Action of the given type
     */
    public Object buildPOJOAction(ActionConfig config) throws Exception {
        
        //log.warn("Building a POJO action "+config.getClassName(), new Exception());
        // TODO: this should be handled differently with Page Flow
        return super.buildPOJOAction(config);
    }

}
