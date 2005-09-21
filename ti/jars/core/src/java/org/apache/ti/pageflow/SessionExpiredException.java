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


/**
 * Exception thrown in place of another {@link FlowControllerException} when:
 * <ul>
 * <li>The requested session ID is different than the current session ID (or there is no current session), and</li>
 * <li>the original exception to be thrown returns <code>true</code> for
 * {@link FlowControllerException#causeMayBeSessionExpiration}, and</li>
 * <li>The <code>&lt;throw-session-expired-exception&gt;</code> element in WEB-INF/struts-ti-config.xml is
 * set to <code>true</code> (the default)</li>.
 * </ul>
 * <p/>
 * When this exception is thrown, the original exception (considered to be a secondary effect of the session expiration)
 * can be obtained through {@link #getEffect()}.
 */
public class SessionExpiredException
        extends FlowControllerException {
    private FlowControllerException _effect;

    public SessionExpiredException(FlowControllerException effect) {
        super(effect.getFlowController());
        _effect = effect;
    }

    protected Object[] getMessageArgs() {
        return new Object[] { getActionName(), getFlowControllerURI() };
    }

    protected String[] getMessageParts() {
        return new String[] { "action ", " on page flow ", " cannot be completed because the user session has expired." };
    }

    /**
     * Get the effect of the session expiration; this is the exception that was most likely caused by the session
     * expiring.
     */
    public Throwable getEffect() {
        return _effect;
    }

    /**
     * Tell whether the root cause may be session expiration in cases where the requested session ID is different than
     * the actual session ID.  In this case, the answer is <code>true</code> (since this is the exception that is thrown
     * in for session expiration).
     */
    public boolean causeMayBeSessionExpiration() {
        return false;
    }
}
