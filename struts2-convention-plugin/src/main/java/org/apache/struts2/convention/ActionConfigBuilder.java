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
 * This interface defines how the action configurations for the current
 * web application can be constructed. This must find all actions that
 * are not specifically defined in the struts XML files or any plugins.
 * Otherwise, the {@link ConventionUnknownHandler} will not be able to
 * handle the unknown actions. Furthermore, it must make every effort
 * to locate all action results as well. The {@link ConventionUnknownHandler}
 * doesn't handle unknown results at all and just throws exceptions.
 * </p>
 *
 * @author Brian Pontarelli
 */
public interface ActionConfigBuilder {
    /**
     * Builds all the action configurations and stores them into the XWork configuration instance
     * via XWork dependency injetion.
     */
    void buildActionConfigs();
}