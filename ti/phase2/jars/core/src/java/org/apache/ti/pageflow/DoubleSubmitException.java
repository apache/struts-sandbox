/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.pageflow;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Exception thrown when an action marked with the
 * {@link org.apache.ti.pageflow.annotations.ti.action#preventDoubleSubmit &#64;ti.action(preventDoubleSubmit=...}
 * annotation attribute has been submitted to more than once.
 */
public class DoubleSubmitException
        extends FlowControllerException
        implements ResponseErrorCodeSender {

    DoubleSubmitException(FlowController fc) {
        super(fc);
    }

    protected Object[] getMessageArgs() {
        return new Object[]{getActionName(), getFlowControllerURI()};
    }

    protected String[] getMessageParts() {
        return new String[]{"A double-submit occurred for action ", " in page flow ", "."};
    }

    public void sendResponseErrorCode(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, getLocalizedMessage());
    }

    /**
     * Tell whether the root cause may be session expiration in cases where the requested session ID is different than
     * the actual session ID.  In this case, the answer is <code>true</code>.
     */
    public boolean causeMayBeSessionExpiration() {
        return true;
    }
}
