/*
 * Copyright (c) 2001-2007, Inversoft, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.apache.struts2.convention;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * This class is a copy from the Java.net Commons repository. The unit tests
 * are in that package and modifications to this class should be migrated back
 * to that repository.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ClassClassLoaderResolver extends AbstractClassLoaderResolver<Class<?>> {
    private static final Logger logger = Logger.getLogger(ClassClassLoaderResolver.class.getName());

    /**
     * A Test that checks to see if each class is assignable to the provided class. Note
     * that this test will match the parent type itself if it is presented for matching.
     */
    public static class IsA implements Test<Class<?>> {
        private Class parent;

        /**
         * Constructs an IsA test using the supplied Class as the parent class/interface.
         *
         * @param   parentType The parent type used to test if the classes found in the directory
         *          being searched are sub-classes of this class.
         */
        public IsA(Class parentType) {
            this.parent = parentType;
        }

        /**
         * Determines if the class given is a sub-class or equal to the parent type given in the
         * constructor.
         *
         * @param   klass The class to check.
         * @return  True if type is assignable to the parent type supplied in the constructor.
         */
        public boolean test(Class<?> klass) {
            return parent.isAssignableFrom(klass);
        }

        @Override
        public String toString() {
            return "is assignable to " + parent.getSimpleName();
        }
    }

    /**
     * A Test that checks to see if each file name ends with the provided suffix.
     */
    public static class NameEndsWith implements Test<Class<?>> {
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
         * @param   klass The class object to test the name of.
         * @return  True if type name ends with the suffix supplied in the constructor.
         */
        public boolean test(Class<?> klass) {
            return klass.getSimpleName().endsWith(suffix);
        }

        @Override
        public String toString() {
            return "ends with the suffix " + suffix;
        }
    }

    /**
     * A Test that checks to see if each class is annotated with any of a number of annotations. If
     * it is, then the test returns true, otherwise false.
     */
    public static class AnnotatedWith implements Test<Class<?>> {
        private Class<? extends Annotation>[] annotations;

        /**
         * Construts an AnnotatedWith test for the specified annotation type.
         *
         * @param   annotations The list of annotations to check.
         */
        public AnnotatedWith(Class<? extends Annotation>... annotations) {
            this.annotations = annotations;
        }

        /**
         * Returns true if the class given is annotated with the any of the annotation types provided
         * to the constructor.
         *
         * @param   klass The class to check.
         * @return  True if the class is annotated with any of the annotations passed to the
         *          constructor. False otherwise.
         */
        public boolean test(Class<?> klass) {
            boolean found = false;
            for (Class<? extends Annotation> annotation : annotations) {
                found |= klass.isAnnotationPresent(annotation);
                if (found) {
                    break;
                }
            }
            return found;
        }

        @Override
        public String toString() {
            return "annotated with of of these annotations " + Arrays.asList(annotations);
        }
    }

    /**
     * Converts the path to a Class instance.
     *
     * @param   baseURLSpec The base URL specification that might reference a JAR file or a File path.
     *          This does not include the dirName or the file name, but it will include a ! for JAR
     *          URLs.
     * @param   dirName The directory name.
     * @param   file The file to prepare.
     * @return  The Class or null if the path doesn't point to a .class file.
     */
    protected Class<?> prepare(String baseURLSpec, String dirName, String file) {
        if (!file.endsWith(".class")) {
            return null;
        }

        try {
            String externalName = dirName.replace('/', '.') + "." + file.substring(0, file.indexOf('.'));
            ClassLoader loader = getClassLoader();
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Converting resource [" + externalName + "] to Class<?>");
            }

            return loader.loadClass(externalName);
        } catch (Throwable t) {
            logger.warning("Could not load class [" + file + "] due to a [" + t.getClass().getName() +
                "] with message [" + t.getMessage() + "]");
            throw new IllegalArgumentException("Could not load class [" + file + "]", t);
        }
    }
}