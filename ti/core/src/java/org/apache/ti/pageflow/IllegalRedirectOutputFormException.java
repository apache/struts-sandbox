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
 * Exception that occurs when an output form has been added to a forward that resolves to a
 * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward} annotation marked with
 * <code>{@link org.apache.ti.pageflow.annotations.ti.forward#redirect redirect}=true</code>.
 * Output forms may not be used on redirect forwards.
 *
 * @see Forward#addOutputForm
 */
public class IllegalRedirectOutputFormException extends IllegalOutputFormException {

    /**
     * @param forwardName    the name of the relevant {@link Forward}.
     * @param flowController the current {@link FlowController} instance.
     * @param outputFormType the type name of the relevant output form.
     */
    public IllegalRedirectOutputFormException(String forwardName, FlowController flowController, String outputFormType) {
        super(forwardName, flowController, outputFormType);
    }

    protected Object[] getMessageArgs() {
        return new Object[]{getForwardName(), getActionName(), getFlowControllerURI(), getOutputFormType()};
    }

    public String[] getMessageParts() {
        return new String[]
        {
            "The forward \"", "\" on action ", " in page flow ", " has at least one output form (type ",
            "), but is set to redirect=\"true\". Output forms may not be used on redirect forwards."
        };
    }
}
