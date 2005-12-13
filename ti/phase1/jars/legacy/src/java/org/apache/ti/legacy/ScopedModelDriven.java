/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.legacy;

import com.opensymphony.xwork.ModelDriven;

/**
 * ModelDriven Actions provide a model object to be pushed onto the ValueStack
 * in addition to the Action itself, allowing a FormBean type approach like Struts.
 *
 * @author Jason Carreira
 *         Created Apr 8, 2003 6:22:42 PM
 */
public interface ScopedModelDriven extends ModelDriven {

    /**
     * @return the model to be pushed onto the ValueStack instead of the Action itself
     */
    void setModel(Object model);
}
