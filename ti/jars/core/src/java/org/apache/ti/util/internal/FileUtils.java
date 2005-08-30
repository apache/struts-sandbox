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
package org.apache.ti.util.internal;

import java.io.File;

public class FileUtils {

    private static final boolean OS_CASE_SENSITIVE = !new File("x").equals(new File("X"));


    /**
     * Tell whether a given URI is absolute, i.e., whether it contains a scheme-part (e.g., "http:").
     *
     * @param uri the URI to test.
     * @return <code>true</code> if the given URI is absolute.
     */
    public static boolean isAbsoluteURI(String uri) {
        //
        // This method needs to be fast, so it can't use java.net.URI.
        //
        if (uri.length() == 0 || uri.charAt(0) == '/') return false;

        for (int i = 0, len = uri.length(); i < len; ++i) {
            char c = uri.charAt(i);

            if (c == ':') {
                return true;
            } else if (c == '/') {
                return false;
            }
        }

        return false;
    }

    /**
     * Tell whether a URI ends in a given String.
     */
    public static boolean uriEndsWith(String uri, String ending) {
        int queryStart = uri.indexOf('?');

        if (queryStart == -1) {
            return uri.endsWith(ending);
        } else {
            return uri.length() - queryStart >= ending.length()
                    && uri.substring(queryStart - ending.length(), queryStart).equals(ending);
        }
    }

    /**
     * Get the file extension from a file name.
     *
     * @param filename the file name.
     * @return the file extension (everything after the last '.'), or the empty string if there is no
     *         file extension.
     */
    public static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot != -1 ? filename.substring(lastDot + 1) : "";
    }

    public static String stripFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot != -1 ? filename.substring(0, lastDot) : filename;
    }

    /**
     * Tell whether the current operating system is case-sensitive with regard to file names.
     */
    public static boolean isOSCaseSensitive() {
        return OS_CASE_SENSITIVE;
    }

    /**
     * Compare two strings, with case sensitivity determined by the operating system.
     *
     * @param s1 the first String to compare.
     * @param s2 the second String to compare.
     * @return <code>true</code> when:
     *         <ul>
     *         <li>the strings match exactly (including case), or,</li>
     *         <li>the operating system is not case-sensitive with regard to file names, and the strings match,
     *         ignoring case.</li>
     *         </ul>
     * @see #isOSCaseSensitive()
     */
    public static boolean osSensitiveEquals(String s1, String s2) {
        if (OS_CASE_SENSITIVE) {
            return s1.equals(s2);
        } else {
            return s1.equalsIgnoreCase(s2);
        }
    }

    /**
     * Tell whether a string ends with a particular suffix, with case sensitivity determined by the operating system.
     *
     * @param str    the String to test.
     * @param suffix the suffix to look for.
     * @return <code>true</code> when:
     *         <ul>
     *         <li><code>str</code> ends with <code>suffix</code>, or,</li>
     *         <li>the operating system is not case-sensitive with regard to file names, and <code>str</code> ends with
     *         <code>suffix</code>, ignoring case.</li>
     *         </ul>
     * @see #isOSCaseSensitive()
     */
    public static boolean osSensitiveEndsWith(String str, String suffix) {
        if (OS_CASE_SENSITIVE) {
            return str.endsWith(suffix);
        } else {
            int strLen = str.length();
            int suffixLen = suffix.length();

            if (strLen < suffixLen) {
                return false;
            }

            return (str.substring(strLen - suffixLen).equalsIgnoreCase(suffix));
        }
    }
}

