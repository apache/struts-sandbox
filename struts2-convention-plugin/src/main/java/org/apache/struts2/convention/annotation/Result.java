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
 * This annotation is used to specify non-convention based results for
 * the Struts convention handling. This annotation is added to a class
 * and can be used to specify the result location for a specific result
 * code from an action method. Furthermore, this can also be used to
 * handle results only for specific action methods within an action class
 * (if there are multiple).
 * </p>
 *
 * @author Brian Pontarelli
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Result {
    /**
     * @return  The name of the result mapping. This is the value that is returned from the action
     *          method and is used to associate a location with a return value.
     */
    String name();

    /**
     * @return  The location of the result within the web application or anywhere on disk. Since the
     *          base result location can be modified using a number of methods, including the
     *          {@link org.apache.struts2.convention.annotation.BaseResultLocation} annotation, this can contain the variable
     *          <code>baseResultLocation</code> which will be token replaced with the appropriate
     *          value.
     */
    String location() default "";

    /**
     * @return  The type of the result. This is usually setup in the struts.xml or struts-plugin.xml
     *          and is a simple name that is mapped to a result Class.
     */
    String type() default "";

    /**
     * @return  The parameters passed to the result. This is a list of strings that form a name/value
     *          pair chain since creating a Map for annotations is not possible. An example would be:
     *          <code>{"key", "value", "key2", "value2"}</code>.
     */
    String[] params() default {};
}