/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

package org.apache.shale.faces;

import org.apache.shale.ViewController;
import org.apache.shale.ViewControllerMapper;
import org.apache.shale.util.TokenProcessor;

/**
 * <p>{@link ShaleConstants} are manifest constants defining global identifiers shared across
 * the internal implementation of the controller.  These constants
 * need not be referenced by applications built on the framework.</p>
 *
 * $Id$
 */
public interface ShaleConstants {
    
    
    /**
     * <p>Appication scope attribute key under which the
     * {@link TokenProcessor} instance for this application is stored.</p>
     */
    public static final String TOKEN_PROCESSOR =
      "org.apache.shale.TOKEN_PROCESSOR";


    /**
     * <p>Session scope attribute key under which we keep a <code>Set</code>
     * containing the valid transaction tokens for this session.</p>
     */
    public static final String TOKENS = "org.apache.shale.TOKENS";


    /**
     * <p>Application scope attribute under which the
     * {@link ViewControllerMapper} for translating view identifiers
     * to class names of the corresponding {@link ViewController}
     * is stored.</p>
     */
    public static final String VIEW_MAPPER =
      "org.apache.shale.faces.VIEW_MAPPER";


    /**
     * <p>Request scope attribute under which the {@link ViewController}
     * for the view that will actually be rendered (if any) is stored.</p>
     */
    public static final String VIEW_RENDERED =
      "org.apache.shale.faces.VIEW_RENDERED";


    /**
     * <p>Request scope attribute under which a <code>List</code>
     * containing all {@link ViewController}s that have been initialized
     * for the current request are stored.</p>
     */
    public static final String VIEWS_INITIALIZED =
      "org.apache.shale.faces.VIEWS_INITIALIZED";


}
