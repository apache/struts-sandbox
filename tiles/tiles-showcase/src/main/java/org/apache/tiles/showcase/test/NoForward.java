/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.tiles.showcase.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.tiles.ComponentContext;
import org.apache.tiles.access.TilesAccess;


/**
 * Implementation of <strong>Action</strong> that create a TileContext in order
 * to force the TilesRequestProcessor to do an include instead of a forward.
 * The side effect is that request.getRequestURI will return the URL of the
 * calling struts action instead of the URL of the tiles layout.
 * See the jsp documentation to understand why.
 * Usage:
 * use this action in conjunction with an action declaration in struts config.
 * The action declaration should have one "success" forward to a Tile.
 * <pre>
 *   <action     path="/showRequestURI"
 *       		     type="org.apache.tiles.showcase.test.NoForward">
 *    <forward  name="success"        path="test.action.noforward"/>
 *  </action>
 * </pre>
 * @version $Rev$ $Date$
 */

public final class NoForward extends Action {



    // --------------------------------------------------------- Public Methods


    /**
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception Exception if the application business logic throws
     *  an exception
     * @since Struts 1.1
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws Exception {
      // Try to retrieve tile context
    
      ComponentContext context = TilesAccess.getContainer(request.getSession()
            .getServletContext()).getComponentContext(request, response);;
	  return (mapping.findForward("success"));
    }


}
