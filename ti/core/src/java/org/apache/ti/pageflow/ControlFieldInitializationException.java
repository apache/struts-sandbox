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
 * Exception thrown when there are errors initializing an annotated Control field.
 */
public class ControlFieldInitializationException
        extends PageFlowManagedObjectException {

    private String _fieldName;

    /**
     * Construct with no error message.
     */
    public ControlFieldInitializationException(String fieldName, PageFlowManagedObject object, Throwable cause) {
        super(object, cause);
        _fieldName = fieldName;
    }

    protected Object[] getMessageArgs() {
        return new Object[]{_fieldName, getManagedObject().getDisplayName()};
    }

    protected String[] getMessageParts() {
        return new String[]
        {
            "Exception occurred when initializing field ", " on page flow ", "."
        };
    }
}
