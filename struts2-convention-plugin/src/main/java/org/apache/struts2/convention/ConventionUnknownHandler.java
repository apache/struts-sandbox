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

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import org.apache.struts2.util.ClassLoaderUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.providers.InterceptorBuilder;
import com.opensymphony.xwork2.inject.Inject;

/**
 * <p>
 * This class is the default unknown handler for all of the Convention
 * plugin integration with XWork.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ConventionUnknownHandler implements UnknownHandler {
    private static final Logger logger = Logger.getLogger(ConventionUnknownHandler.class.getName());
    protected Configuration configuration;
    protected ObjectFactory objectFactory;
    protected ServletContext servletContext;
    protected ResultMapBuilder resultMapBuilder;
    protected String defaultParentPackageName;
    protected PackageConfig parentPackage;

    protected String resultLocation;
    protected Map<String, ResultTypeConfig> resultsByExtension;
    protected ResultTypeConfig redirectResultTypeConfig;
    private boolean redirectToSlash;

    /**
     * Constructs the unknown handler.
     *
     * @param   configuration The XWork configuration.
     * @param   objectFactory The XWork object factory used to create result instances.
     * @param   servletContext The servlet context used to help build the action configurations.
     * @param   resultMapBuilder The result map builder that is used to create results.
     * @param   resultLocation The result location within the web application (/WEB-INF/content by
     *          default).
     * @param   defaultParentPackageName The default XWork package that the unknown handler will use as
     *          the parent package for new actions and results.
     * @param   redirectToSlash A boolean parameter that controls whether or not this will handle
     *          unknown actions in the same manner as Apache, Tomcat and other web servers. This
     *          handling will send back a redirect for URLs such as /foo to /foo/ if there doesn't
     *          exist an action that responds to /foo.
     */
    @Inject
    public ConventionUnknownHandler(Configuration configuration, ObjectFactory objectFactory,
            ServletContext servletContext, ResultMapBuilder resultMapBuilder,
            @Inject("struts.convention.base.result.location") String resultLocation,
            @Inject("struts.convention.action.default.parent.package") String defaultParentPackageName,
            @Inject("struts.convention.redirect.to.slash") String redirectToSlash) {
        this.configuration = configuration;
        this.objectFactory = objectFactory;
        this.servletContext = servletContext;
        this.resultMapBuilder = resultMapBuilder;
        this.resultLocation = resultLocation;
        this.defaultParentPackageName = defaultParentPackageName;
        this.resultsByExtension = new LinkedHashMap<String,ResultTypeConfig>();

        this.parentPackage = configuration.getPackageConfig(defaultParentPackageName);
        if (parentPackage == null) {
            throw new ConfigurationException("Unknown default parent package [" + defaultParentPackageName + "]");
        }
        Map<String, ResultTypeConfig> results = parentPackage.getAllResultTypeConfigs();

        resultsByExtension.put("jsp", results.get("dispatcher"));
        resultsByExtension.put("vm", results.get("velocity"));
        resultsByExtension.put("ftl", results.get("freemarker"));
        // Issue 22 - Add html and htm as default result extensions
        resultsByExtension.put("html", results.get("dispatcher"));
        resultsByExtension.put("htm", results.get("dispatcher"));

        this.redirectResultTypeConfig = results.get("redirect");
        this.redirectToSlash = Boolean.parseBoolean(redirectToSlash);
    }

    public ActionConfig handleUnknownAction(String namespace, String actionName)
    throws XWorkException {
        // Strip the namespace if it is just a slash
        if (namespace == null || "/".equals(namespace)) {
            namespace = "";
        }

        String pathPrefix = determinePath(null, resultLocation, namespace);
        ActionConfig actionConfig = null;

        // Try /idx/action.jsp if actionName is not empty, otherwise it will just be /.jsp
        if (!actionName.equals("")) {
            for (String ext : resultsByExtension.keySet()) {
                String path = string(pathPrefix, actionName, "." , ext);
                if (logger.isLoggable(Level.FINEST)) {
                    String fqan = namespace + "/" + actionName;
                    logger.finest("Trying to locate the correct default result [" + path + "] for the FQ action [" +
                        fqan + "] with an file extension of [" + ext + "] in the directory [" + pathPrefix + "]");
                }

                try {
                    if (servletContext.getResource(path) != null) {
                        actionConfig = buildActionConfig(path, resultsByExtension.get(ext));
                        logger.finest("Found action config");
                        break;
                    }
                } catch (MalformedURLException e) {
                    logger.warning("Unable to parse path to the web application resource [" + path +
                        "] skipping...");
                }
            }
        }

        if (actionConfig == null) {
            // If the URL is /foo and there is an action we can redirect to, send the redirect to /foo/.
            // However, if that action is not in the same namespace, it is the default, so I'm not going
            // to return that.
            if (!actionName.equals("") && redirectToSlash) {
                String redirectNamespace = namespace + "/" + actionName;
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Checking if there is an action named index in the namespace [" + redirectNamespace + "]");
                }

                actionConfig = configuration.getRuntimeConfiguration().getActionConfig(redirectNamespace, "index");
                if (actionConfig != null) {
                    logger.finest("Found action config");

                    PackageConfig packageConfig = configuration.getPackageConfig(actionConfig.getPackageName());
                    if (redirectNamespace.equals(packageConfig.getNamespace())) {
                        logger.finest("Action is not a default - redirecting");
                        return buildActionConfig(redirectNamespace + "/", redirectResultTypeConfig);
                    }

                    logger.finest("Action was a default - NOT redirecting");
                }
            }

            // Otherwise, if the URL is /foo or /foo/ look for index pages in /foo/
            String resultPath = null;
            String resultExt = null;
            for (String ext : resultsByExtension.keySet()) {
                if (logger.isLoggable(Level.FINEST)) {
                    String fqan = namespace + "/" + actionName;
                    logger.finest("Checking for [" + fqan + "/" + "index." + ext + "].");
                }

                String path = string(pathPrefix, actionName, "/index." , ext);
                try {
                    if (servletContext.getResource(path) != null) {
                        resultPath = path;
                        resultExt = ext;
                        break;
                    }
                } catch (MalformedURLException e) {
                    logger.warning("Unable to parse path to the web application resource [" + path +
                        "] skipping...");
                }
            }

            // If the URL is /foo and there is /foo/index.jsp, let's send a redirect to /foo/. If the URL is
            // /foo/ (actionName is empty) and there is /foo/index.jsp, send a forward to that. Otherwise,
            // just return null.
            if (resultPath != null) {
                actionConfig = buildActionConfig(resultPath, resultsByExtension.get(resultExt));
            }
        }

        return actionConfig;
    }

    protected ActionConfig buildActionConfig(String path, ResultTypeConfig resultTypeConfig) {
        Map<String, ResultConfig> results = new HashMap<String,ResultConfig>();
        HashMap<String, String> params = new HashMap<String, String>();
        if (resultTypeConfig.getParams() != null) {
            params.putAll(resultTypeConfig.getParams());
        }
        params.put(resultTypeConfig.getDefaultResultParam(), path);

        PackageConfig pkg = configuration.getPackageConfig(defaultParentPackageName);
        List<InterceptorMapping> interceptors = InterceptorBuilder.constructInterceptorReference(pkg,
            pkg.getFullDefaultInterceptorRef(), Collections.EMPTY_MAP, null, objectFactory);
        ResultConfig config = new ResultConfig(Action.SUCCESS, resultTypeConfig.getClazz(), params);
        results.put(Action.SUCCESS, config);

        return new ActionConfig("execute", ActionSupport.class.getName(), defaultParentPackageName,
            new HashMap<String, Object>(), results, interceptors);
    }

    private Result scanResultsByExtension(String ns, String actionName, String pathPrefix, String resultCode, ActionContext actionContext) {
        Result result = null;
        for (String ext : resultsByExtension.keySet()) {
            if (logger.isLoggable(Level.FINEST)) {
                String fqan = ns + "/" + actionName;
                logger.finest("Trying to locate the correct result for the FQ action [" + fqan +
                    "] with an file extension of [" + ext + "] in the directory [" + pathPrefix + "] " +
                    "and a result code of [" + resultCode + "]");
            }

            String path = string(pathPrefix, actionName, "-", resultCode, "." , ext);
            result = findResult(path, resultCode, ext, actionContext);
            if (result != null) {
                break;
            }

            path = string(pathPrefix, actionName, "." , ext);
            result = findResult(path, resultCode, ext, actionContext);
            if (result != null) {
                break;
            }

            // Issue #6 - Scan for result-code as page name
            path = string(pathPrefix, resultCode, "." , ext);
            result = findResult(path, resultCode, ext, actionContext);
            if (result != null) {
                break;
            }

        }
      return result;
    }

    public Result handleUnknownResult(ActionContext actionContext, String actionName,
            ActionConfig actionConfig, String resultCode) throws XWorkException {

        PackageConfig pkg = configuration.getPackageConfig(actionConfig.getPackageName());
        String ns = pkg.getNamespace();
        String pathPrefix = determinePath(actionConfig, resultLocation, ns);

        Result result = scanResultsByExtension(ns, actionName, pathPrefix, resultCode, actionContext);

        if (result == null) {
            // Try /idx/action/index.jsp
            for (String ext : resultsByExtension.keySet()) {
                if (logger.isLoggable(Level.FINEST)) {
                    String fqan = ns + "/" + actionName;
                    logger.finest("Checking for [" + fqan + "/" + "index." + ext + "].");
                }

                String path = string(pathPrefix, actionName, "/index", "-", resultCode, ".", ext);
                result = findResult(path, resultCode, ext, actionContext);
                if (result != null) {
                    break;
                }

                path = string(pathPrefix, actionName, "/index." , ext);
                result = findResult(path, resultCode, ext, actionContext);
                if (result != null) {
                    break;
                }
            }
        }

        return result;
    }

    protected Result findResult(String path, String resultCode, String ext, ActionContext actionContext) {
        try {
            logger.finest("Checking ServletContext for [" + path + "]");
            if (servletContext.getResource(path) != null) {
                logger.finest("Found");
                return buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
            }

            logger.finest("Checking ClasLoader for [" + path + "]");
            String classLoaderPath = path.startsWith("/") ? path.substring(1, path.length()) : path;
            if (ClassLoaderUtils.getResource(classLoaderPath, getClass()) != null) {
                logger.finest("Found");
                return buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
            }
        } catch (MalformedURLException e) {
            logger.warning("Unable to parse template path: "+path+", skipping...");
        }

        return null;
    }

    protected Result buildResult(String path, String resultCode, ResultTypeConfig config, ActionContext invocationContext) {
        String resultClass = config.getClazz();

        Map<String,String> params = new LinkedHashMap<String,String>();
        if (config.getParams() != null) {
            params.putAll(config.getParams());
        }
        params.put(config.getDefaultResultParam(), path);

        ResultConfig resultConfig = new ResultConfig(resultCode, resultClass, params);
        try {
            return objectFactory.buildResult(resultConfig, invocationContext.getContextMap());
        } catch (Exception e) {
            throw new XWorkException("Unable to build convention result", e, resultConfig);
        }
    }

    protected String string(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
        }
        return sb.toString();
    }

    /**
     * Determines the result path prefix that the request URL is for, minus the action name. This includes
     * the base result location and the namespace, with all the slashes handled.
     *
     * @param   actionConfig (Optional) The might be a ConventionActionConfig, from which we can get the
     *          default base result location of that specific action.
     * @param   prefix The default base result location for the application.
     * @param   nameSpace The current URL namespace.
     * @return  The path prefix and never null.
     */
    protected String determinePath(ActionConfig actionConfig, String prefix, String nameSpace) {
        String finalPrefix = prefix;
        if (actionConfig != null && actionConfig instanceof ConventionActionConfig) {
            finalPrefix = ((ConventionActionConfig) actionConfig).getBaseResultLocation();
        }

        if (!finalPrefix.endsWith("/")) {
            finalPrefix += "/";
        }

        if (nameSpace == null || "/".equals(nameSpace)) {
            nameSpace = "";
        }

        if (nameSpace.length() > 0) {
            if (nameSpace.startsWith("/")) {
                nameSpace = nameSpace.substring(1);
            }

            if (!nameSpace.endsWith("/")) {
                nameSpace += "/";
            }
        }

        return finalPrefix + nameSpace;
    }

    /**
     * Not used
     */
	public Object handleUnknownActionMethod(Object action, String methodName) throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}
}