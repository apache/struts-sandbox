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
package org.apache.struts2.convention;

/**
 * <p>
 * This class has some reflection helpers.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ReflectionTools {
    /**
     * Determines if the class given contains the method.
     *
     * @param   klass The class to check for the method.
     * @param   method The method name.
     * @param   parameterTypes The parameter types of the method.
     * @return  True if the method exists, false if not.
     */
    public static boolean containsMethod(Class<?> klass, String method, Class<?>... parameterTypes) {
        try {
            klass.getMethod(method, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}