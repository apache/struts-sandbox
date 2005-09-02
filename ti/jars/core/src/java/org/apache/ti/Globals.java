/*
 * $Id: Globals.java 170121 2005-05-14 05:09:32Z martinc $
 *
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.ti;


import java.io.Serializable;

/**
 * @todo Should this class simply go away?  Do we want to expose public constants for these things (vs. APIs)?
 */
public class Globals implements Serializable {


    /**
     * The request attribute for a List of {@link org.apache.ti.core.ActionMessage} objects.
     */
    public static final String ERROR_KEY = "org.apache.ti.ERROR";

    /**
     * The request attribute for a Throwable that was raised during action processing.
     */
    public static final String EXCEPTION_KEY = "org.apache.ti.EXCEPTION";

    /**
     * The session attributes key under which the user's selected
     * <code>java.util.Locale</code> is stored, if any.  If no such
     * attribute is found, the system default locale
     * will be used when retrieving internationalized messages.  If used, this
     * attribute is typically set during user login processing.
     */
    public static final String LOCALE_KEY = "org.apache.ti.LOCALE";


    /**
     * <p>The base of the context attributes key under which our
     * module <code>MessageResources</code> will be stored.  This
     * will be suffixed with the actual module prefix (including the
     * leading "/" character) to form the actual resources key.</p>
     *
     * <p>For each request processed by the controller servlet, the
     * <code>MessageResources</code> object for the module selected by
     * the request URI currently being processed will also be exposed under
     * this key as a request attribute.</p>
     */
    public static final String MESSAGES_KEY = "org.apache.ti.MESSAGE";
}
