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

package org.apache.shale.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Utility wrapper around resource bundles that provides locale-specific
 * message string lookups, as well as parameter replacement services.</p>
 *
 * $Id$
 */
public class Messages {
    

    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a new {@link Messages} wrapper around the specified
     * resource bundle name, loaded by the default class loader.</p>
     *
     * @param name Name of the requested <code>ResourceBundle</code>
     */
    public Messages(String name) {

        this(name, null);

    }

    
    /**
     * <P>Construct a new {@link Messages} wrapper around the specified
     * resource bundle name, loaded by the specified class loader.</p>
     *
     * @param name Name of the requested <code>ResourceBundle</code>
     * @param cl <code>ClassLoader</code> to use for loading this
     *  resource bundle, or <code>null</code> for the default
     */
    public Messages(String name, ClassLoader cl) {

        this.name = name;
        this.cl = cl;

    }


    // ------------------------------------------------------ Instance Variables


    /**
     * <p>Set of localized <code>ResourceBundle</code> instances we have ever
     * retrieved, keyed by <code>Locale</code>.</p>
     */
    private Map bundles = new HashMap();


    /**
     * <p><code>ClassLoader</code> from which to load the specfied
     * resource bundle.</p>
     */
    private ClassLoader cl = null;


    /**
     * <p>The default <code>Locale</code> for this server.</p>
     */
    private Locale defaultLocale = Locale.getDefault();


    /**
     * <p><code>MessageFormat</code> used to perform parameter substitution.</p>
     */
    private MessageFormat format = new MessageFormat("");


    /**
     * <p>Name of the resource bundle to be retrieved.</p>
     */
    private String name = null;


    // ---------------------------------------------------------- Public Methods


    /**
     * <p>Retrieve the specified message string for the default locale.  If no
     * message can be found, return <code>null</code>.</p>
     *
     * @param key Key to the message string to look up
     */
    public String getMessage(String key) {

        return getMessage(key, defaultLocale);

    }


    /**
     * <p>Retrieve the specified message string for the default locale, and
     * perform parameter substitution with the specified parameters.  If no
     * message can be found, return <code>null</code>.</p>
     *
     * @param key Key to the message string to look up
     * @param params Parameter replacement values
     */
    public String getMessage(String key, Object params[]) {

        return getMessage(key, defaultLocale, params);

    }


    /**
     * <p>Retrieve the specified message string for the specified locale.  If no
     * message can be found, return <code>null</code>.</p>
     *
     * @param key Key to the message string to look up
     * @param locale Locale used to localize this message
     */
    public String getMessage(String key, Locale locale) {

        ResourceBundle rb = getBundle(locale);
        try {
            return rb.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }

    }


    /**
     * <p>Retrieve the specified message string for the specified locale, and
     * perform parameter substitution with the specified parameters.  If no
     * message can be found, return <code>null</code>.</p>
     *
     * @param key Key to the message string to look up
     * @param locale Locale used to localize this message
     * @param params Parameter replacement values
     */
    public String getMessage(String key, Locale locale, Object params[]) {

        String message = getMessage(key, locale);
        if ((message == null) || (params == null) || (params.length < 1)) {
            return message;
        }
        synchronized(format) {
            format.applyPattern(message);
            message = format.format(params);
        }
        return message;

    }


    // --------------------------------------------------------- Private Methods


    /**
     * <p>Return the localized <code>ResourceBundle</code> for the specified
     * <code>Locale</code>.</p>
     *
     * @param locale Locale used to select the appropriate resource bundle
     */
    private ResourceBundle getBundle(Locale locale) {

        ResourceBundle rb = null;
        synchronized (bundles) {
            rb = (ResourceBundle) bundles.get(locale);
            if (rb == null) {
                if (cl == null) {
                    rb = ResourceBundle.getBundle(name, locale);
                } else {
                    rb = ResourceBundle.getBundle(name, locale, cl);
                }
                bundles.put(locale, rb);
            }
            return rb;
        }

    }


}
