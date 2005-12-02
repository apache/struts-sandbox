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

import org.apache.ti.pageflow.internal.NavigateToException;
import org.apache.ti.pageflow.xwork.NavigateToResult;


/**
 * Exception that occurs when the
 * <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction previousAction}</code>
 * attribute is used on a {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}, a
 * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or a
 * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward},
 * but thethere is no previously-run action in the page flow.
 */
public class NoPreviousActionException extends NavigateToException {

    public NoPreviousActionException(NavigateToResult result, FlowController fc) {
        super(result, fc);
    }

    protected String[] getMessageParts() {
        return new String[]{"No previous action for navigateTo=", " on action ", " in page flow ", "."};
    }
}
