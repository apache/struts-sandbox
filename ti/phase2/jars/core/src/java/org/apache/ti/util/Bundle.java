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
package org.apache.ti.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Convenience class for dealing with resource bundles.
 */
public class Bundle {

    private static final String BUNDLE_NAME = "org.apache.ti.util.messages";

    /**
     * No need, it's all static
     */
    private Bundle() {
    }

    /**
     * Returns the resource bundle named Bundle[].properties in the
     * package of the specified class.
     */
    private static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(BUNDLE_NAME);
    }


    /**
     * Returns the string specified by aKey from the errors.properties bundle.
     */
    public static String getString(String aKey) {
        try {
            return getBundle().getString(aKey);
        } catch (Exception e) {
            return getString("System_StringNotFound", new Object[]{aKey});
        }
    }

    /**
     * Returns the string specified by aKey from the errors.properties bundle.
     *
     * @param aKey The key for the message pattern in the bundle.
     * @param arg  The arg to use in the message format.
     */
    public static String getString(String aKey, Object arg) {
        return getString(aKey, new Object[]{arg});
    }

    /**
     * Returns the string specified by aKey from the errors.properties bundle.
     *
     * @param aKey The key for the message pattern in the bundle.
     * @param args The args to use in the message format.
     */
    public static String getString(String aKey, Object[] args) {
        String pattern = getBundle().getString(aKey);
        MessageFormat format = new MessageFormat(pattern);

        return format.format(args).toString();
    }

    /**
     *
     */
    public static String getErrorString(String aKey, Object[] args) {
        String pattern = getBundle().getString(aKey);
        MessageFormat format = new MessageFormat(pattern);

        return format.format(args);
    }

    public static String getErrorString(String aKey) {
        return getBundle().getString(aKey);
    }

}
