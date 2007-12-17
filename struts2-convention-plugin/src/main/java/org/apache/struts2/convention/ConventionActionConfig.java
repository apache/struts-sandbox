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

import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * <p>
 * This class is a custom action config object that allows the Convention plugin
 * to add additional parameters to the ActionConfig, without blowing up the static
 * initializer interceptor, which use the parameters from the ActionConfig
 * class and attempts to calls static setters on the Action classes with the
 * same names.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ConventionActionConfig extends ActionConfig {
    private String baseResultLocation;

    public String getBaseResultLocation() {
        return baseResultLocation;
    }

    public void setResultPath(String baseResultLocation) {
        this.baseResultLocation = baseResultLocation;
    }
}