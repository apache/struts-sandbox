/*
 * Copyright (c) 2007, Inversoft and Texturemedia, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicab`le law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.apache.struts2.convention;

/**
 * <p>
 * This interface defines the method that is used to create action
 * names based on the name of a class.
 * </p>
 *
 * @author Brian Pontarelli
 */
public interface ActionNameBuilder {
    /**
     * Given the name of the class, this method should build an action name.
     *
     * @param   className The class name.
     * @return  The action name and never null.
     */
    String build(String className);
}