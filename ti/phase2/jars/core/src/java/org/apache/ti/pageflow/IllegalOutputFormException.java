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
 * Base type for errors related to output forms on Forwards.
 *
 * @see Forward#addOutputForm
 */
public abstract class IllegalOutputFormException extends FlowControllerException {

    private String _forwardName;
    private String _outputFormType;


    /**
     * @param forwardName    the name of the relevant {@link Forward}.
     * @param flowController the current {@link FlowController} instance.
     * @param outputFormType the type name of the relevant output form.
     */
    public IllegalOutputFormException(String forwardName, FlowController flowController,
                                      String outputFormType) {
        super(flowController);
        _forwardName = forwardName;
        _outputFormType = outputFormType;
    }

    /**
     * Get the name of the relevant {@link Forward}.
     *
     * @return a String that is the name of the relevant {@link Forward}.
     */
    public String getForwardName() {
        return _forwardName;
    }

    /**
     * Set the name of the relevant {@link Forward}.
     *
     * @param forwardName a String that is the name of the relevant {@link Forward}.
     */
    public void setForwardName(String forwardName) {
        _forwardName = forwardName;
    }

    /**
     * Get the type name of the relevant output form.
     *
     * @return a String that is the type name of the relevant output form.
     */
    public String getOutputFormType() {
        return _outputFormType;
    }

    /**
     * Set the type name of the relevant output form.
     *
     * @param outputFormType a String that is the type name of the relevant output form.
     */
    public void setOutputFormType(String outputFormType) {
        _outputFormType = outputFormType;
    }

    /**
     * Tell whether the root cause may be session expiration in cases where the requested session ID is different than
     * the actual session ID.  In this case, the answer is <code>false</code>.
     */
    public boolean causeMayBeSessionExpiration() {
        return false;
    }
}
