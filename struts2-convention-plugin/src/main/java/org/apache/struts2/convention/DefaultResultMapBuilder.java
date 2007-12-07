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

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import org.apache.struts2.convention.annotation.BaseResultLocation;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ResolverUtil;

/**
 * <p>
 * This class implements the ResultMapBuilder and traverses the web
 * application content directory looking for reasonably named JSPs and
 * other result types as well as annotations. This naming is in this
 * form:
 * </p>
 *
 * <pre>
 * /baseResultLocation/namespace/action-&lt;result>.jsp
 * </pre>
 *
 * <p>
 * If there are any files in these locations than a result is created
 * for each one and the result names is the last portion of the file
 * name up to the . (dot).
 * </p>
 *
 * <p>
 * When results are found, new ResultConfig instances are created. The
 * result config that is created has a number of thing to be aware of:
 * </p>
 *
 * <ul>
 * <li>The result-type is always the default result type for the XWork
 *  package. This current can not be modified.</li>
 * <li>The result config contains the location parameter, which is
 *  required by most result classes to figure out where to find the result.
 *  In addition, the config has all the parameters from the default result-type
 *  configuration.</li>
 * </ul>
 *
 * <p>
 * After loading the files in the web application, this class will then
 * use any annotations on the action class to override what was found in
 * the web application files. These annotations are the {@link Result}
 * and {@link org.apache.struts2.convention.annotation.Results} annotations. These two annotations allow an action
 * to supply different or non-forward based results for specific return
 * values of an action method.
 * </p>
 *
 * <p>
 * The base result location used by this class for locating JSPs and other
 * such result files can be set using the Struts2 constant named
 * <b>struts.convention.base.result.location</b>.
 * </p>
 *
 * <p>
 * Also, the base result location can be changed on an action class instance
 * basis by using the {@link BaseResultLocation} annotation.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultResultMapBuilder implements ResultMapBuilder {
    private static final Logger logger = Logger.getLogger(DefaultResultMapBuilder.class.getName());
    private final ServletContext servletContext;
    private String baseResultLocation;
    private Set<String> relativeResultTypes;
    private Map<String, String> resultsByExtension = new HashMap<String, String>();

    /**
     * Constructs the SimpleResultMapBuilder using the given result location.
     *
     * @param   servletContext The ServletContext for finding the resources of the web application.
     * @param   relativeResultTypes The list of result types that can have locations that are relative
     *          and the localResultLocation (which is the baseResultLocation plus the namespace)
     *          prepended to them.
     */
    @Inject
    public DefaultResultMapBuilder(ServletContext servletContext,
            @Inject("struts.convention.relative.result.types") String relativeResultTypes) {
        this.servletContext = servletContext;
        this.relativeResultTypes = new HashSet<String>(Arrays.asList(relativeResultTypes.split("\\s*[,]\\s*")));

        this.resultsByExtension.put("jsp", "dispatcher");
        this.resultsByExtension.put("vm", "velocity");
        this.resultsByExtension.put("ftl", "freemarker");
    }

    /**
     * {@inheritDoc}
     */
    @Inject("struts.convention.base.result.location")
    public void setBaseResultLocation(String baseResultLocation) {
        this.baseResultLocation = baseResultLocation;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ResultConfig> build(Class<?> actionClass, String actionName,
            PackageConfig packageConfig) {
        // See if the class wants to change the resultLocation
        String localResultLocation = baseResultLocation;
        BaseResultLocation resultLocationAnnotation = actionClass.getAnnotation(BaseResultLocation.class);
        if (resultLocationAnnotation != null) {
            if (resultLocationAnnotation.value().equals("") && resultLocationAnnotation.property().equals("")) {
                throw new ConfigurationException("The BaseResultLocation annotation must have either" +
                    " a value or property specified.");
            }

            String property = resultLocationAnnotation.property();
            if (property.equals("")) {
                localResultLocation = resultLocationAnnotation.value();
            } else {
                try {
                    ResourceBundle strutsBundle = ResourceBundle.getBundle("struts");
                    localResultLocation = strutsBundle.getString(property);
                } catch (Exception e) {
                    throw new ConfigurationException("The action class [" + actionClass + "] defines" +
                        " a @ResultLocation annotation and a property definition however the" +
                        " struts.properties could not be found in the classpath using ResourceBundle" +
                        " OR the bundle exists but the property [" + property + "] is not defined" +
                        " in the file.", e);
                }
            }
        }

        // Add a slash
        if (!localResultLocation.endsWith("/")) {
            localResultLocation = localResultLocation + "/";
        }

        // Check for resources with the action name
        final String namespace = packageConfig.getNamespace();
        if (namespace != null && namespace.startsWith("/")) {
             localResultLocation = localResultLocation + namespace.substring(1);
        } else if (namespace != null) {
            localResultLocation = localResultLocation + namespace;
        }

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Using final calculated namespace [" + namespace + "]");
        }

        // Add that ending slash for concatentation
        if (!localResultLocation.endsWith("/")) {
            localResultLocation += "/";
        }

        String resultPrefix = localResultLocation + actionName;

        Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();
        createFromResources(results, localResultLocation, resultPrefix, actionName, packageConfig);
        createFromAnnotations(results, localResultLocation, actionClass, actionName, packageConfig);

        return results;
    }

    /**
     * Creates any result types from the resources available in the web application. This scans the
     * web application resources using the servlet context.
     *
     * @param   results The results map to put the result configs created into.
     * @param   localResultLocation The calculated local location of the resources.
     * @param   resultPrefix The prefix for the result. This is usually <code>/localResultLocation/actionName</code>.
     * @param   actionName The action name which is used only for logging in this implementation.
     * @param   packageConfig The package configuration which is passed along in order to determine
     *          the default result type.
     */
    protected void createFromResources(Map<String, ResultConfig> results, final String localResultLocation,
            final String resultPrefix, final String actionName, PackageConfig packageConfig) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Searching for results in the Servlet container at [" + localResultLocation +
                "] with result prefix of [" + resultPrefix + "]");
        }

        // Build from web application using the ServletContext
        @SuppressWarnings("unchecked")
        Set<String> paths = servletContext.getResourcePaths(localResultLocation);
        if (paths != null) {
            for (String path : paths) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Processing resource path [" + path + "]");
                }

                makeResults(path, resultPrefix, results, packageConfig);
            }
        }

        // Building from the classpath
        String classPathLocation = localResultLocation.startsWith("/") ?
            localResultLocation.substring(1, localResultLocation.length()) : localResultLocation;
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Searching for results in the class path at [" + classPathLocation +
                "] with a result prefix of [" + resultPrefix + "] and action name [" + actionName + "]");
        }

        ResolverUtil resolver = new ResolverUtil();
        resolver.findInPackage(new ResolverUtil.ResourceTest() {
            public boolean matches(URL url) {
                String urlStr = url.toString();
                int index = urlStr.lastIndexOf(localResultLocation);
                String path = urlStr.substring(index + localResultLocation.length());
                return path.startsWith(actionName) && !path.contains("/");
            }
        }, classPathLocation);

        Set<URL> matches = resolver.getResources();
        for (URL match : matches) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Processing URL [" + match + "]");
            }

            String urlStr = match.toString();
            int index = urlStr.lastIndexOf(resultPrefix);
            String path = urlStr.substring(index);
            makeResults(path, resultPrefix, results, packageConfig);
        }
    }

    /**
     * Makes all the results for the given path.
     *
     * @param   path The path to build the result for.
     * @param   resultPrefix The is the result prefix which is the result location plus the action name.
     *          This is used to determine if the path contains a result code or not.
     * @param   results The Map to place the result(s)
     * @param   packageConfig The package config the results belong to.
     */
    protected void makeResults(String path, String resultPrefix, Map<String, ResultConfig> results,
            PackageConfig packageConfig) {
        if (path.startsWith(resultPrefix)) {
            int indexOfDot = path.indexOf('.', resultPrefix.length());

            // Grab the dash-resultCode. and pick out the resultCode
            if (indexOfDot == resultPrefix.length()) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("The result file [" + path + "] has no result code and therefore" +
                        " will be associated with success, input and error by default. This might" +
                        " be overridden by another result file or an annotation.");
                }

                if (!results.containsKey(Action.SUCCESS)) {
                    ResultConfig success = createResultConfig(new ResultInfo(Action.SUCCESS, path),
                        new HashMap<String, String>(), packageConfig, null, null, null);
                    results.put(Action.SUCCESS, success);
                }
                if (!results.containsKey(Action.INPUT)) {
                    ResultConfig input = createResultConfig(new ResultInfo(Action.INPUT, path),
                        new HashMap<String, String>(), packageConfig, null, null, null);
                    results.put(Action.INPUT, input);
                }
                if (!results.containsKey(Action.ERROR)) {
                    ResultConfig error = createResultConfig(new ResultInfo(Action.ERROR, path),
                        new HashMap<String, String>(), packageConfig, null, null, null);
                    results.put(Action.ERROR, error);
                }
            } else if (indexOfDot > resultPrefix.length()) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("The result file [" + path + "] has a result code and therefore" +
                        " will be associated with only that result code.");
                }

                String resultCode = path.substring(resultPrefix.length() + 1, indexOfDot);
                ResultConfig result = createResultConfig(new ResultInfo(resultCode, path),
                    new HashMap<String, String>(), packageConfig, null, null, null);
                results.put(resultCode, result);
            }
        }
    }

    /**
     * This method uses the {@link Results} and {@link Result} annotations to construct and add more
     * ResultConfig instances to the mapping.
     *
     * @param   resultConfigs The map to add the created ResultConfig instances to.
     * @param   localResultLocation The calculated local location of the resources.
     * @param   actionClass The action class that is used to get the annotations via reflection.
     * @param   packageConfig The package configuration that is used in case the annotations don't
     * @param   actionName The name of the action that the results are being created for. This is
     *          used to verify that the annotation is associated with the action.
     */
    protected void createFromAnnotations(Map<String, ResultConfig> resultConfigs,
            String localResultLocation, Class<?> actionClass, String actionName,
            PackageConfig packageConfig) {
        // check if any annotations are around
        while (actionClass != Object.class) {
            //noinspection unchecked
            Results results = actionClass.getAnnotation(Results.class);
            if (results != null) {
                // first check here...
                for (int i = 0; i < results.value().length; i++) {
                    Result result = results.value()[i];
                    ResultConfig config = createResultConfig(new ResultInfo(result),
                        new HashMap<String, String>(), packageConfig, result, actionName, localResultLocation);
                    if (config != null) {
                        resultConfigs.put(config.getName(), config);
                    }
                }
            }

            // what about a single Result annotation?
            Result result = actionClass.getAnnotation(Result.class);
            if (result != null) {
                ResultConfig config = createResultConfig(new ResultInfo(result),
                    new HashMap<String, String>(), packageConfig, result, actionName, localResultLocation);
                if (config != null) {
                    resultConfigs.put(config.getName(), config);
                }
            }

            actionClass = actionClass.getSuperclass();
        }
    }

    /**
     * Creates the result configuration for the single result annotation. This will use all the
     * information from the annotation and anything that isn't specified will be fetched from the
     * PackageConfig defaults (if they exist).
     *
     * @param   info The result info that is used to create the ResultConfig instance.
     * @param   resultParams The parameters passed to the result.
     * @param   packageConfig The PackageConfig to use to fetch defaults for result and parameters.
     * @param   result (Optional) The result annotation to pull additional information from.
     * @param   actionName (Optional) The name of the current action, which is used only with the
     *          annotations to verify that the annotation is targeting the current action.
     * @param   localResultLocation (Optional) Used only with the annotations to prepend to the
     *          location if the location in the annotation is relative and the type is one of the
     *          relativeResultTypes specified in the constructor.
     * @return  The ResultConfig or null if the Result annotation is given and the annotation is
     *          targeted to some other action than this one.
     */
    @SuppressWarnings(value = {"unchecked"})
    protected ResultConfig createResultConfig(ResultInfo info, Map<String, String> resultParams,
            PackageConfig packageConfig, Result result, String actionName, String localResultLocation) {
        // First try to figure it out based on extension and then use the default
        String type = info.type;
        if (StringTools.isTrimmedEmpty(type)) {
            String path = info.location;
            int indexOfDot = path.lastIndexOf(".");
            if (indexOfDot > 0) {
                String extension = path.substring(indexOfDot + 1);
                type = this.resultsByExtension.get(extension);
            }
        }

        ResultTypeConfig resultTypeConfig;
        if (StringTools.isTrimmedEmpty(type)) {
            String defaultResultType = packageConfig.getFullDefaultResultType();
            resultTypeConfig = packageConfig.getAllResultTypeConfigs().get(defaultResultType);
            if (resultTypeConfig == null) {
                throw new ConfigurationException("The no default result type is defined for the" +
                    " Struts/Xwork package [" + packageConfig.getName() + "]. Therefore you must" +
                    " define the result-type for all actions using the Result(s) annotation(s).");
            }
        } else {
            resultTypeConfig = packageConfig.getAllResultTypeConfigs().get(type);
            if (resultTypeConfig == null) {
                throw new ConfigurationException("The Result type [" + type + "] which is" +
                    " defined in the Result annotation, could not be found as a result-type defined" +
                    " for the Struts/XWork package [" + packageConfig.getName() + "]");
            }
        }

        // Handle the annotation
        if (result != null) {
            // Skip the result if it doesn't belong to this action
            if (result.action() != null && result.action().trim().length() > 0 &&
                    !result.action().equals(actionName)) {
                return null;
            }

            // See if we can handle relative locations or not
            if (relativeResultTypes.contains(resultTypeConfig.getName())) {
                String location = result.location();
                if (!location.startsWith("/")) {
                    location = localResultLocation + location;
                }

                info.location = location;
            }

            Map<String, String> params = createParameterMap(result.params());
            resultParams.putAll(params);
        }

        // Add the default parameters for the result type config (if any)
        if (resultTypeConfig.getParams() != null) {
            resultParams.putAll(resultTypeConfig.getParams());
        }

        String defaultParam;
        try {
            Class<?> cls = ClassLoaderUtil.loadClass(resultTypeConfig.getClazz(), this.getClass());
            defaultParam = (String) cls.getField("DEFAULT_PARAM").get(null);
        } catch (Exception e) {
            // not sure why this happened, but let's just use a sensible choice
            defaultParam = "location";
        }

        HashMap<Object,Object> params = new HashMap<Object,Object>();
        if (resultParams != null) {
            params.putAll(resultParams);
        }

        // Don't put the value for location if a parameter has already been set
        if (!params.containsKey(defaultParam)) {
            params.put(defaultParam, info.location);
        }

        return new ResultConfig(info.name, resultTypeConfig.getClazz(), params);
    }

    protected Map<String, String> createParameterMap(String[] parms) {
        Map<String, String> map = new HashMap<String, String>();
        int subtract = parms.length % 2;
        if (subtract != 0) {
            throw new ConfigurationException("The Result annotation uses an array of strings for" +
                " parameters and they must be in a key value pair configuration. It looks like you" +
                " have specified an odd number of parameters and there should only be an even number." +
                " (e.g. params = {\"key\", \"value\"})");
        }

        for (int i = 0; i < parms.length; i = i + 2) {
            String key = parms[i];
            String value = parms[i + 1];
            map.put(key, value);
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Adding parmeter ["+ key + ":" + value + "] to result.");
            }
        }

        return map;
    }

    private class ResultInfo {
        public final String name;
        public String location;
        public final String type;

        public ResultInfo(String name, String location, String type) {
            this.name = name;
            this.location = location;
            this.type = type;
        }

        public ResultInfo(String name, String location) {
            this.name = name;
            this.location = location;
            this.type = null;
        }

        public ResultInfo(Result result) {
            this.name = result.name();
            this.location = result.location();
            this.type = result.type();
        }
    }
}
