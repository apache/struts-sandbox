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

import java.util.ResourceBundle;
import java.util.Map;
import java.util.HashMap;

import org.apache.struts2.convention.annotation.AnnotationTools;
import org.apache.struts2.convention.annotation.ResultPath;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ConventionsServiceImpl implements ConventionsService {
    private String resultPath;

    public ConventionsServiceImpl(@Inject("struts.convention.result.path") String resultPath) {
        this.resultPath = resultPath;
    }

    /**
     * {@inheritDoc}
     */
    public String determineResultPath(Class<?> actionClass) {
        String localResultPath = resultPath;
        ResultPath resultPathAnnotation = AnnotationTools.findAnnotation(actionClass, ResultPath.class);
        if (resultPathAnnotation != null) {
            if (resultPathAnnotation.value().equals("") && resultPathAnnotation.property().equals("")) {
                throw new ConfigurationException("The ResultPath annotation must have either" +
                    " a value or property specified.");
            }

            String property = resultPathAnnotation.property();
            if (property.equals("")) {
                localResultPath = resultPathAnnotation.value();
            } else {
                try {
                    ResourceBundle strutsBundle = ResourceBundle.getBundle("struts");
                    localResultPath = strutsBundle.getString(property);
                } catch (Exception e) {
                    throw new ConfigurationException("The action class [" + actionClass + "] defines" +
                        " a @ResultPath annotation and a property definition however the" +
                        " struts.properties could not be found in the classpath using ResourceBundle" +
                        " OR the bundle exists but the property [" + property + "] is not defined" +
                        " in the file.", e);
                }
            }
        }

        return localResultPath;
    }

    /**
     * {@inheritDoc}
     */
    public String determineResultPath(String className) {
        try {
            return determineResultPath(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid action class configuration that references an unknown " +
                "class named [" + className + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ResultTypeConfig> getResultTypesByExtension(PackageConfig packageConfig) {
        Map<String, ResultTypeConfig> results = packageConfig.getAllResultTypeConfigs();

        Map<String, ResultTypeConfig> resultsByExtension = new HashMap<String, ResultTypeConfig>();
        resultsByExtension.put("jsp", results.get("dispatcher"));
        resultsByExtension.put("vm", results.get("velocity"));
        resultsByExtension.put("ftl", results.get("freemarker"));
        // Issue 22 - Add html and htm as default result extensions
        resultsByExtension.put("html", results.get("dispatcher"));
        resultsByExtension.put("htm", results.get("dispatcher"));
        return resultsByExtension;
    }
}