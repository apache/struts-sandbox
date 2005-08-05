/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.config.entities.ActionConfig;


/**
 * Builds actions from the config.  If an Action is not created, it is assumed to be a Controller.
 */
public class SpringObjectFactory extends ObjectFactory implements BeanFactoryAware {

    public static final String CONTROLLER_ACTION = "controllerAction";

    protected BeanFactory beanFactory;

    public void setBeanFactory(BeanFactory factory) {
        this.beanFactory = factory;
    }

    /**
     * Build an Action of the given type
     */
    public Action buildAction(ActionConfig config) throws Exception {
        Object obj = buildBean(config.getClassName());
        if (obj instanceof Action) {
            return (Action) obj;
        } else {
            ControllerAction action = (ControllerAction) beanFactory.getBean(CONTROLLER_ACTION);
            Object controller = buildBean(config.getClassName());
            action.setController(controller);
            return action;
        }
    }

}
