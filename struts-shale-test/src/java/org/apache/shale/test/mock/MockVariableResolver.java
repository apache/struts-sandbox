/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shale.test.mock;

import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

/**
 * <p>Mock implementation of <code>VariableResolver</code>.</p>
 *
 * <p>This implementation recognizes the standard scope names
 * <code>applicationScope</code>, <code>facesContext</code>,
 * <code>RequestScope</code>, and
 * <code>sessionScope</code>, plus it knows how to search in ascending
 * scopes for non-reserved names.</p>
 *
 * $Id$
 */

public class MockVariableResolver extends VariableResolver {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a default instance.</p>
     */
    public MockVariableResolver() {
    }


    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    // ------------------------------------------------ VariableResolver Methods


    public Object resolveVariable(FacesContext context, String name) {

        if ((context == null) || (name == null)) {
            throw new NullPointerException();
        }

        // Check for magic names
        if ("applicationScope".equals(name)) {
            return external().getApplicationMap();
        } else if ("facesContext".equals(name)) {
            return FacesContext.getCurrentInstance();
        } else if ("requestScope".equals(name)) {
            return external().getRequestMap();
        } else if ("sessionScope".equals(name)) {
            return external().getSessionMap();
        }

        // Search ascending scopes for non-magic names
        Map map = null;
        map = external().getRequestMap();
        if (map.containsKey(name)) {
            return map.get(name);
        }
        map = external().getSessionMap();
        if ((map != null) && (map.containsKey(name))) {
            return map.get(name);
        }
        map = external().getApplicationMap();
        if (map.containsKey(name)) {
            return map.get(name);
        }

        // No such variable can be found
        return null;

    }



    // --------------------------------------------------------- Private Methods


    private ExternalContext external() {

        return FacesContext.getCurrentInstance().getExternalContext();

    }


}
