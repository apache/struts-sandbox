/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.convention;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is maintained in the Java.net Commons library and the test cases
 * are located there. Any updates to this class should be done in that project
 * and then migrated over.

 * <p>
 * Locates files within the current ClassLoader/ClassPath. This
 * class begins from a directory and locates all the files in that
 * directory and possibly in sub-directories. For each file located
 * either on the file system or in a JAR file the Test classes and
 * interfaces defined in this class can be called to determine if
 * it matches.
 * </p>
 *
 * <p>
 * When files are matched using the Test interfaces and classes
 * from this class they are added to a set of matches. These matches can
 * then be fetched and used however is required.
 * </p>
 */
public class URLClassLoaderResolver extends AbstractClassLoaderResolver<URL> {

    /**
     * A simple interface that specifies how to test relative file names to determine if they
     * are to be included in the results.
     */
    public static interface NameTest extends Test<URL> {
    }

    /**
     * A Test that checks to see if each file name ends with the provided suffix.
     */
    public static class NameEndsWith implements NameTest {
        private String suffix;

        /**
         * Constructs a NameEndsWith test using the supplied suffix. This can be used to test class
         * names as well, but remember that the file name passed to the match method will include
         * the <strong>.class</strong> extension and that will need to be stripped or included in
         * the suffix.
         *
         * @param   suffix The suffix to match.
         */
        public NameEndsWith(String suffix) {
            this.suffix = suffix;
        }

        /**
         * Determines if the file name given ends with the given suffix.
         *
         * @param   url The file name to check.
         * @return  True if type name ends with the suffix supplied in the constructor.
         */
        public boolean test(URL url) {
            return url.toString().endsWith(suffix);
        }

        @Override
        public String toString() {
            return "ends with the suffix " + suffix;
        }
    }

    /**
     * Concatenates the strings together to for a URL.
     *
     * @param   baseURLSpec The base URL specification that might reference a JAR file or a File path.
     *          This does not include the dirName or the file name, but it will include a ! for JAR
     *          URLs.
     * @param   dirName The dirName that is appended to the URL.
     * @param   file The file to prepare.
     * @return  The result.
     */
    protected String convertName(String baseURLSpec, String dirName, String file) {
        return baseURLSpec + dirName + "/" + file;
    }

    /**
     * Makes a URL to the resource using the URL and relativePath given.
     *
     * @param   spec The URL.
     * @return  Returns the URL which is the url parameter plus the relativePath parameter.
     */
    protected URL prepare(String spec) {
        try {
            return new URL(spec);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to make a URL", e);
        }
    }
}