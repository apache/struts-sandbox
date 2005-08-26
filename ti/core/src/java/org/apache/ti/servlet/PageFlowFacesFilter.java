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
package org.apache.ti.servlet;

import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.servlet.PageFlowPageFilter;
import org.apache.ti.servlet.PageFlowPageFilter;

import java.util.HashSet;
import java.util.Set;


/**
 * Servlet Filter for JavaServer Faces requests.
 */ 
public class PageFlowFacesFilter
        extends PageFlowPageFilter
{
    private static Set/*< String >*/ VALID_FILE_EXTENSIONS = new HashSet/*< String >*/();
    
    static
    {
        VALID_FILE_EXTENSIONS.add( InternalConstants.FACES_EXTENSION );
        VALID_FILE_EXTENSIONS.add( InternalConstants.JSF_EXTENSION );
    }
    
    protected Set getValidFileExtensions()
    {
        return VALID_FILE_EXTENSIONS;
    }
}
