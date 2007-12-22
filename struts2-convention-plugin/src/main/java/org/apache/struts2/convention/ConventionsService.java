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

import java.util.Map;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;

/**
 * <p>
 * This interface defines the conventions that are used by the convention plugin.
 * </p>
 *
 * @author Brian Pontarelli
 */
public interface ConventionsService {
    /**
     * Locates the result location from annotations on the action class or the package.
     *
     * @param   actionClass The action class.
     * @return  The result location if it is set in the annotations otherwise, the default result
     *          location is returned.
     */
    String determineResultPath(Class<?> actionClass);

    /**
     * Delegates to the other method but first looks up the Action's class using the given class name.
     *
     * @param   className The name of the Action class.
     * @return  The result location if it is set in the annotations otherwise, the default result
     *          location is returned.
     */
    String determineResultPath(String className);

    /**
     * Returns a mapping between the result type strings and the {@link ResultTypeConfig} instances
     * based on the {@link PackageConfig} given.
     *
     * @param   packageConfig The PackageConfig to get the result types for.
     * @return  The result types or an empty Map of nothing is configured.
     */
    Map<String, ResultTypeConfig> getResultTypesByExtension(PackageConfig packageConfig);
}