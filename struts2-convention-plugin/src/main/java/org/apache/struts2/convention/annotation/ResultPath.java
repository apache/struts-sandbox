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
 * This annotation allows the path to the results be to changed on a class
 * by class basis. This will favor the property name setting and then the
 * value.
 * </p>
 *
 * <p>
 * This is used when locating results for an action. In most cases a result
 * is a JSP or some type of template (Velocity for example). In order to
 * figure out which results are associated with an action, this class can be
 * used to set the base directory that the Convention plugin looks at when
 * trying to figure out the correct results. For example, if there is an action:
 * </p>
 *
 * <pre>
 * com.example.foo.DoSomething
 * </pre>
 *
 * <p>
 * The Convention plugin might find that the namespace is foo and the action
 * name is do_something and will need to find the results. Using this annotation
 * you can set the base path of the results to something like
 * <code>/WEB-INF/jsps</code> so that the Convention plugin will look in the
 * web application for files of this pattern:
 * </p>
 *
 * <pre>
 * /WEB-INF/jsps/foo/do_something-&lt;resultCode>.ext
 * </pre>
 *
 * @author  Brian Pontarelli
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PACKAGE})
public @interface ResultPath {
    /**
     * @return  The base result path to use for this action, instead of the default.
     */
    String value() default "";

    /**
     * @return  The name of the property from the struts.properties file that contains the result
     *          path for the action that contains this annotation. This property must be set
     *          and the struts.properties file must exist in the root of the classpath.
     */
    String property() default "";
}