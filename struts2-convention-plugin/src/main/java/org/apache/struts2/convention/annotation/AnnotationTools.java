/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package org.apache.struts2.convention.annotation;

import java.lang.annotation.Annotation;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class AnnotationTools {

    /**
     * Returns the annotation on the given class or the package of the class. This searchs up the
     * class hierarchy and the package hierarchy.
     *
     * @param   klass The class to search for the annotation.
     * @param   annotationClass The Class of the annotation.
     * @return  The annotation or null.
     */
    public static <T extends Annotation> T findAnnotation(Class<?> klass, Class<T> annotationClass) {
        T ann = klass.getAnnotation(annotationClass);
        while (ann == null && klass != null) {
            ann = klass.getPackage().getAnnotation(annotationClass);
            if (ann == null) {
                klass = klass.getSuperclass();
            }
        }

        return ann;
    }
}