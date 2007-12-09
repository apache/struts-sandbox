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

import org.apache.struts2.convention.annotation.AnnotationTools;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultLocation;
import org.apache.struts2.convention.annotation.Results;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;

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
 * basis by using the {@link org.apache.struts2.convention.annotation.ResultLocation} annotation.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultResultMapBuilder implements ResultMapBuilder {
    private static final Logger logger = Logger.getLogger(DefaultResultMapBuilder.class.getName());
    private final ServletContext servletContext;
    private String resultLocation;
    private Set<String> relativeResultTypes;
    private Map<String, String> resultsByExtension = new HashMap<String, String>();

    /**
     * Constructs the SimpleResultMapBuilder using the given result location.
     *
     * @param   servletContext The ServletContext for finding the resources of the web application.
     * @param   relativeResultTypes The list of result types that can have locations that are relative
     *          and the localResultLocation (which is the baseResultLocation plus the namespace)
     *          prepended to them.
     * @param   resultLocation The default location of the results.
     */
    @Inject
    public DefaultResultMapBuilder(ServletContext servletContext,
            @Inject("struts.convention.relative.result.types") String relativeResultTypes,
            @Inject("struts.convention.result.location") String resultLocation) {
        this.servletContext = servletContext;
        this.relativeResultTypes = new HashSet<String>(Arrays.asList(relativeResultTypes.split("\\s*[,]\\s*")));
        this.resultLocation = resultLocation;

        this.resultsByExtension.put("jsp", "dispatcher");
        this.resultsByExtension.put("vm", "velocity");
        this.resultsByExtension.put("ftl", "freemarker");
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ResultConfig> build(Class<?> actionClass, String method,
            org.apache.struts2.convention.annotation.Action annotation, String actionName,
            PackageConfig packageConfig) {
        // Get the default result location from the annotation or configuration
        String localResultLocation = determineResultLocation(actionClass);

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
        if (annotation != null && annotation.results() != null && annotation.results().length > 0) {
            createFromAnnotations(results, localResultLocation, packageConfig, annotation.results());
        }

        Results resultsAnn = actionClass.getAnnotation(Results.class);
        if (resultsAnn != null) {
            createFromAnnotations(results, localResultLocation, packageConfig, resultsAnn.value());
        }

        Result resultAnn = actionClass.getAnnotation(Result.class);
        if (resultAnn != null) {
            createFromAnnotations(results, localResultLocation, packageConfig, new Result[]{resultAnn});
        }

        return results;
    }

    /**
     * Locates the result location from annotations on the action class or the package.
     *
     * @param   actionClass The action class.
     * @return  The result location if it is set in the annotations otherwise, the default result
     *          location is returned.
     */
    protected String determineResultLocation(Class<?> actionClass) {
        String localResultLocation = resultLocation;
        ResultLocation resultLocationAnnotation = AnnotationTools.findAnnotation(actionClass, ResultLocation.class);
        if (resultLocationAnnotation != null) {
            if (resultLocationAnnotation.value().equals("") && resultLocationAnnotation.property().equals("")) {
                throw new ConfigurationException("The ResultLocation annotation must have either" +
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

        return localResultLocation;
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

        URLClassLoaderResolver resolver = new URLClassLoaderResolver();
        resolver.find(new URLClassLoaderResolver.NameTest() {
            public boolean test(URL url) {
                String urlStr = url.toString();
                int index = urlStr.lastIndexOf(localResultLocation);
                String path = urlStr.substring(index + localResultLocation.length());
                return path.startsWith(actionName);
            }
        }, false, classPathLocation);

        Set<URL> matches = resolver.getMatches();
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

            // This case is when the path doesn't contain a result code
            if (indexOfDot == resultPrefix.length()) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("The result file [" + path + "] has no result code and therefore" +
                        " will be associated with success, input and error by default. This might" +
                        " be overridden by another result file or an annotation.");
                }

                if (!results.containsKey(Action.SUCCESS)) {
                    ResultConfig success = createResultConfig(new ResultInfo(Action.SUCCESS, path, packageConfig),
                        new HashMap<String, String>(), packageConfig, null, null);
                    results.put(Action.SUCCESS, success);
                }
                if (!results.containsKey(Action.INPUT)) {
                    ResultConfig input = createResultConfig(new ResultInfo(Action.INPUT, path, packageConfig),
                        new HashMap<String, String>(), packageConfig, null, null);
                    results.put(Action.INPUT, input);
                }
                if (!results.containsKey(Action.ERROR)) {
                    ResultConfig error = createResultConfig(new ResultInfo(Action.ERROR, path, packageConfig),
                        new HashMap<String, String>(), packageConfig, null, null);
                    results.put(Action.ERROR, error);
                }

            // This case is when the path contains a result code
            } else if (indexOfDot > resultPrefix.length()) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("The result file [" + path + "] has a result code and therefore" +
                        " will be associated with only that result code.");
                }

                String resultCode = path.substring(resultPrefix.length() + 1, indexOfDot);
                ResultConfig result = createResultConfig(new ResultInfo(resultCode, path, packageConfig),
                    new HashMap<String, String>(), packageConfig, null, null);
                results.put(resultCode, result);
            }
        }
    }

    protected void createFromAnnotations(Map<String, ResultConfig> resultConfigs,
            String localResultLocation, PackageConfig packageConfig, Result[] results) {
        // Check for multiple results on the class
        for (Result result : results) {
            ResultConfig config = createResultConfig(new ResultInfo(result, packageConfig),
                new HashMap<String, String>(), packageConfig, result, localResultLocation);
            if (config != null) {
                resultConfigs.put(config.getName(), config);
            }
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
     * @param   localResultLocation (Optional) Used only with the annotations to prepend to the
     *          location if the location in the annotation is relative and the type is one of the
     *          relativeResultTypes specified in the constructor.
     * @return  The ResultConfig or null if the Result annotation is given and the annotation is
     *          targeted to some other action than this one.
     */
    @SuppressWarnings(value = {"unchecked"})
    protected ResultConfig createResultConfig(ResultInfo info, Map<String, String> resultParams,
            PackageConfig packageConfig, Result result, String localResultLocation) {
        // First try to figure it out based on extension and then use the default
        ResultTypeConfig resultTypeConfig = packageConfig.getAllResultTypeConfigs().get(info.type);
        if (resultTypeConfig == null) {
            throw new ConfigurationException("The Result type [" + info.type + "] which is" +
                " defined in the Result annotation or determined by the file extension or is the" +
                " default result type for the PackageConfig of the action, could not be found as a" +
                " result-type defined for the Struts/XWork package [" + packageConfig.getName() + "]");
        }

        // Handle the annotation
        if (result != null) {
            // See if we can handle relative locations or not
            if (relativeResultTypes.contains(resultTypeConfig.getName())) {
                String location = result.location();
                if (location != null && !location.startsWith("/")) {
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

        public ResultInfo(String name, String location, PackageConfig packageConfig) {
            this.name = name;
            this.location = location;
            this.type = determineType(location, packageConfig);
        }

        public ResultInfo(Result result, PackageConfig packageConfig) {
            this.name = result.name();
            this.location = result.location();
            if (result.type() != null) {
                this.type = result.type();
            } else {
                this.type = determineType(location, packageConfig);
            }
        }

        private String determineType(String location, PackageConfig packageConfig) {
            int indexOfDot = location.lastIndexOf(".");
            if (indexOfDot > 0) {
                String extension = location.substring(indexOfDot + 1);
                return resultsByExtension.get(extension);
            } else {
                return packageConfig.getFullDefaultResultType();
            }
        }
    }
}
