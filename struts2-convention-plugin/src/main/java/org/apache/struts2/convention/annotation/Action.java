/*
 * Copyright (c) 2007, Inversoft and Texturemedia, All Rights Reserved
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
package org.apache.struts2.convention.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation can be used to control the URL that maps to
 * a specific method in an Action class. By default, the method
 * that is invoked is the execute method of the action and the
 * URL is based on the package and class names. This annotation
 * allows developers to change the URL or invoke a different
 * method. This also allows developers to specify multiple URLs
 * that will be handled by a single class or a single method.
 * </p>
 *
 * <p>
 * This can also be used via the {@link Actions} annotation
 * to associate multiple URLs with a single method.
 * </p>
 *
 * <p>
 * Here's an example:
 * </p>
 *
 * <pre>
 * public class MyAction implements Action {
 *   {@code @Action("/foo/bar")}
 *   public String execute() {}
 * }
 * </pre>
 *
 * @author  Brian Pontarelli
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    String DEFAULT_VALUE = "DEFAULT_VALUE";

    /**
     * Allows actions to specify different URLs rather than the default that is based on the package
     * and action name. This also allows methods other than execute() to be invoked or multiple URLs
     * to map to a single class or a single method to handle multiple URLs.
     *
     * @return  The action URL.
     */
    String value() default DEFAULT_VALUE;

    /**
     * Allows action methods to specifically control the results for specific return values. These
     * results are not used for other method/URL invocations on the action. These are only used for
     * the URL that this action is associated with.
     *
     * @return  The results for the action.
     */
    Result[] results() default {};
}