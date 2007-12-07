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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Namespace;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ResolverUtil;

/**
 * <p>
 * This class implements the ActionConfigBuilder interface and uses
 * a list of comma separated packages to find the actions. This uses
 * the XWork2 ResolverUtil to find the classes that implement the
 * XWork2 {@link com.opensymphony.xwork2.Action} interface or whose name ends in {@code Action}.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class PackageBasedActionConfigBuilder implements ActionConfigBuilder {
    private static final Logger logger = Logger.getLogger(PackageBasedActionConfigBuilder.class.getName());
    private final Configuration configuration;
    private final ActionNameBuilder actionNameBuilder;
    private final ResultMapBuilder resultMapBuilder;
    private final ObjectFactory objectFactory;
    private String defaultParentPackage = "struts-default";
    private String baseResultLocation;
    private boolean redirectToSlash;
    private String[] actionPackages;
    private String[] excludePackages;
    private String[] packageLocators;

    /**
     * Constructs actions based on a list of packages.
     *
     * @param   configuration The XWork configuration that the new package configs and action configs
     *          are added to.
     * @param   actionNameBuilder The action name builder used to convert action class names to action
     *          names.
     * @param   resultMapBuilder The result map builder used to create ResultConfig mappings for each
     *          action.
     * @param   objectFactory The ObjectFactory used to create the actions and such.
     * @param   redirectToSlash A boolean parameter that controls whether or not this will create an
     *          action for indexes. If this is set to true, index actions are not created because
     *          the unknown handler will redirect from /foo to /foo/. The only action that is created
     *          is to the empty action in the namespace (e.g. the namespace /foo and the action "").
     */
    @Inject
    public PackageBasedActionConfigBuilder(Configuration configuration, ActionNameBuilder actionNameBuilder,
            ResultMapBuilder resultMapBuilder, ObjectFactory objectFactory,
            @Inject("struts.convention.redirect.to.slash") String redirectToSlash,
            @Inject("struts.convention.action.packages", required = false) String actionPackages,
            @Inject("struts.convention.exclude.packages", required = false) String excludePackages,
            @Inject("struts.convention.package.locators", required = false) String packageLocators,
            @Inject("struts.convention.default.parent.package") String defaultParentPackage,
            @Inject("struts.convention.base.result.location") String baseResultLocation) {
        this.configuration = configuration;
        this.actionNameBuilder = actionNameBuilder;
        this.resultMapBuilder = resultMapBuilder;
        this.objectFactory = objectFactory;
        this.redirectToSlash = Boolean.parseBoolean(redirectToSlash);
        this.actionPackages = actionPackages.split("\\s*[,]\\s*");
        this.excludePackages = excludePackages.split("\\s*[,]\\s*");
        this.packageLocators = packageLocators.split("\\s*[,]\\s*");

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Setting action default parent package to [" + defaultParentPackage + "]");
        }

        this.defaultParentPackage = defaultParentPackage;
        this.baseResultLocation = baseResultLocation;
    }

    /**
     * Builds the action configurations by loading all classes in the packages specified by the
     * property <b>struts.convention.action.packages</b> and then figuring out which classes implement Action
     * or have Action in their name. Next, if this class is in a Java package that hasn't been
     * inspected a new PackageConfig (XWork) is created for that Java package using the Java package
     * name. This will contain all the ActionConfigs for all the Action classes that are discovered
     * within that Java package. Next, each class is inspected for the {@link ParentPackage}
     * annotation which is used to control the parent package for a specific action. Lastly, the
     * {@link ResultMapBuilder} is used to create ResultConfig instances of the action.
     */
    public void buildActionConfigs() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Loading action configurations from action packages " +
                Arrays.asList(actionPackages));
        }

        ResolverUtil<Object> resolver = new ResolverUtil<Object>();
        resolver.find(new ResolverUtil.ClassTest() {
            public boolean matches(Class type) {
                return com.opensymphony.xwork2.Action.class.isAssignableFrom(type) ||
                    type.getSimpleName().endsWith("Action");
            }
        }, actionPackages);

        Set<Class<?>> classes = resolver.getClasses();
        Map<String, PackageConfig> packageConfigs = new HashMap<String, PackageConfig>();

        for (Class<?> actionClass : classes) {
            // Tell the ObjectFactory about this class
            try {
                objectFactory.getClassInstance(actionClass.getName());
            } catch (ClassNotFoundException e) {
                // Impossible
                new Throwable().printStackTrace();
                System.exit(1);
            }

            // Action sub package below the action package
            String actionSubPackage = actionClass.getPackage().getName();
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Processing class [" + actionClass.getName() + "] in package [" +
                    actionSubPackage + "]");
            }

            // Figure out the namespace and action name
            String actionNamespace = "";
            String actionName = "";
            for (String actionPackage : actionPackages) {
                if (actionSubPackage.startsWith(actionPackage)) {

                    String name = actionClass.getName().substring(actionPackage.length() + 1);
                    final int indexOfDot = name.lastIndexOf('.');
                    if (indexOfDot > -1) {
                        String convertedNamespace = actionNameBuilder.build(name.substring(0, indexOfDot));
                        actionNamespace += "/" + convertedNamespace.replace('.', '/');
                    }

                    actionName = actionNameBuilder.build(name.substring(indexOfDot + 1));
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest("Got actionName for class [" + actionClass + "] of [" + actionName + "]");
                    }

                    break;
                }
            }

            // Create or retrieve the package config
            PackageConfig defaultPkgCfg = getPackageConfig(packageConfigs, actionNamespace,
                actionSubPackage, actionClass, null);

            // Now see if there are any action annotations that would change the action creation
            // at all. This could actually end up creating multiple actions
            Actions actionsAnnotation = actionClass.getAnnotation(Actions.class);
            if (actionsAnnotation != null) {
                Action[] actionArray = actionsAnnotation.value();
                boolean namelessSeen = false;
                for (Action ann : actionArray) {
                    if (ann.name().equals(Action.DEFAULT_ACTION_NAME) && !namelessSeen) {
                        namelessSeen = true;
                    } else if (ann.name().equals(Action.DEFAULT_ACTION_NAME)) {
                        throw new ConfigurationException("You may only add a single ActionName " +
                            "annotation that has no name parameter.");
                    }

                    // Build or retrieve a package config that uses the namespace from the ActionName
                    // annotation rather than elsewhere
                    PackageConfig pkgCfg = defaultPkgCfg;
                    if (!ann.namespace().equals(Action.DEFAULT_NAMESPACE)) {
                        pkgCfg = getPackageConfig(packageConfigs, actionNamespace, actionSubPackage,
                            actionClass, ann);
                    }

                    createActionConfig(pkgCfg, actionClass, actionName, ann);
                }
            } else {
                Action ann = actionClass.getAnnotation(Action.class);

                // Build or retrieve a package config that uses the namespace from the ActionName
                // annotation rather than elsewhere
                PackageConfig pkgCfg = defaultPkgCfg;
                if (ann != null && !ann.namespace().equals(Action.DEFAULT_NAMESPACE)) {
                    pkgCfg = getPackageConfig(packageConfigs, actionNamespace, actionSubPackage,
                        actionClass, ann);
                }

                createActionConfig(pkgCfg, actionClass, actionName, ann);
            }
        }

        // Determine all the default actions and results based on this logic:
        // 1. Loop over all the namespaces such as /foo and see if it has an action named index
        // 2. If an action doesn't exists in the parent namespace of the same name, create an action
        //    in the parent namespace of the same name as the namespace that points to the index
        //    action in the namespace. e.g. /foo -> /foo/index
        // 3. Create the action in the namespace for empty string if it doesn't exist. e.g. /foo/
        //    the action is "" and the namespace is /foo
        Map<String, PackageConfig> byNamespace = new HashMap<String, PackageConfig>();
        Collection<PackageConfig> values = packageConfigs.values();
        for (PackageConfig packageConfig : values) {
            byNamespace.put(packageConfig.getNamespace(), packageConfig);
        }

        // Step #1
        Set<String> namespaces = byNamespace.keySet();
        for (String namespace : namespaces) {
            // First see if the namespace has an index action
            PackageConfig pkgConfig = byNamespace.get(namespace);
            ActionConfig indexActionConfig = pkgConfig.getAllActionConfigs().get("index");
            if (indexActionConfig == null) {
                continue;
            }

            // Step #2
            if (!redirectToSlash) {
                int lastSlash = namespace.lastIndexOf('/');
                if (lastSlash >= 0) {
                    String parentAction = namespace.substring(lastSlash + 1);
                    String parentNamespace = namespace.substring(0, lastSlash);
                    PackageConfig parent = byNamespace.get(parentNamespace);
                    if (parent == null || parent.getAllActionConfigs().get(parentAction) == null) {
                        if (parent == null) {
                            parent = new PackageConfig();
                            parent.setName(parentNamespace);
                            parent.setNamespace(parentNamespace);
                            parent.addAllParents(pkgConfig.getParents());
                            packageConfigs.put(parentNamespace, parent);
                        }

                        if (parent.getAllActionConfigs().get(parentAction) == null) {
                            parent.addActionConfig(parentAction, indexActionConfig);
                        }
                    } else if (logger.isLoggable(Level.FINEST)) {
                        logger.finest("The parent namespace [" + parentNamespace + "] already contains " +
                            "an action [" + parentAction + "]");
                    }
                }
            }

            // Step #3
            if (pkgConfig.getAllActionConfigs().get("") == null) {
                pkgConfig.addActionConfig("", indexActionConfig);
            }
        }

        // Add the new actions to the configuration
        Set<String> packageNames = packageConfigs.keySet();
        for (String packageName : packageNames) {
            configuration.addPackageConfig(packageName, packageConfigs.get(packageName));
        }

        // Tell XWork to rebuild the runtime so that it will intercept the actions from now on
        configuration.rebuildRuntimeConfiguration();
    }

    /**
     * Creates a single ActionConfig object.
     *
     * @param   pkgCfg The package the action configuration instance will belong to.
     * @param   actionClass The action class.
     * @param   actionName The name of the action.
     * @param   annotation The ActionName annotation that might override the action name and possibly
     *          set the action method.
     */
    protected void createActionConfig(PackageConfig pkgCfg, Class<?> actionClass,
            String actionName, Action annotation) {
        String actionMethod = null;
        if (annotation != null) {
            actionName = annotation.name() != null && annotation.name().equals(Action.DEFAULT_ACTION_NAME) ?
                actionName : annotation.name();
            actionMethod = annotation.method() != null && annotation.method().equals(Action.DEFAULT_ACTION_METHOD) ?
                null : annotation.method();
        }

        ConventionActionConfig actionConfig = new ConventionActionConfig();
        actionConfig.setClassName(actionClass.getName());
        actionConfig.setPackageName(pkgCfg.getName());
        if (actionMethod != null) {
            actionConfig.setMethodName(actionMethod);
        }

        // Add the base result location for the unknown handler
        actionConfig.setBaseResultLocation(baseResultLocation);

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Creating action config for class [" + actionClass + "], name [" + actionName +
                "] and package name [" + pkgCfg.getName() + "] in namespace [" + pkgCfg.getNamespace() +
                "]");
        }

        Map<String, ResultConfig> results = resultMapBuilder.build(actionClass, actionName, pkgCfg);
        actionConfig.setResults(results);

        pkgCfg.addActionConfig(actionName, actionConfig);
    }

    private PackageConfig getPackageConfig(final Map<String, PackageConfig> packageConfigs,
            String actionNamespace, final String actionPackage, final Class<?> actionClass,
            Action action) {
        // First figure out the namespace of the package using the ActionName annotation if it exists
        String namespaceName = null;
        if (action != null && !action.namespace().equals(Action.DEFAULT_NAMESPACE)) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Using non-default action namespace from the ActionName annotation of [" +
                    action.namespace() + "]");
            }
            namespaceName = action.namespace();
        }

        // Next figure out the namespace of the package using the class level annotation
        if (namespaceName == null) {
            Namespace ns = actionClass.getAnnotation(Namespace.class);
            if (ns != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Using non-default action namespace from annotation of [" + ns.value() + "]");
                }

                namespaceName = ns.value();
            }
        }

        // Next try the package level annotation
        if (namespaceName == null) {
            Namespace ns = actionClass.getPackage().getAnnotation(Namespace.class);
            if (ns != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Using non-default action namespace from package level annotation of [" +
                        ns.value() + "]");
                }

                namespaceName = ns.value();
            }
        }

        // Lastly, use the value passed in, which is based off the Java package name
        if (namespaceName == null) {
            namespaceName = actionNamespace;
        }

        // Next grab the parent annotation from the class
        ParentPackage parent = actionClass.getAnnotation(ParentPackage.class);
        String parentName = null;
        if (parent != null) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Using non-default parent package from annotation of [" + parent.value() + "]");
            }

            parentName = parent.value();
        }

        // Next try the package level annotation
        if (parentName == null) {
            parent = actionClass.getPackage().getAnnotation(ParentPackage.class);
            if (parent != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Using non-default parent package from package level annotation of [" +
                        parent.value() + "]");
                }

                parentName = parent.value();
            }
        }

        // Finally use the default
        if (parentName == null) {
            parentName = defaultParentPackage;
        }

        if (parentName == null) {
            throw new ConfigurationException("Unable to determine the parent XWork package for the action class [" +
                actionClass.getName() + "]");
        }

        PackageConfig parentPkg = configuration.getPackageConfig(parentName);
        if (parentPkg == null) {
            throw new ConfigurationException("Unable to locate parent package [" + parentName + "]");
        }

        // Grab based on package-namespace and if it exists, we need to ensure the existing one has
        // the correct parent package. If not, we need to create a new package config
        String name = actionPackage + "#" + parentPkg.getName() + "#" + namespaceName;
        PackageConfig pkgConfig = packageConfigs.get(name);
        if (pkgConfig == null) {
            pkgConfig = new PackageConfig();
            pkgConfig.setName(name);
            pkgConfig.setNamespace(namespaceName);
            pkgConfig.addParent(parentPkg);
            packageConfigs.put(name, pkgConfig);
        }

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Created package config named [" + name + "] with a namespace [" +
                namespaceName + "]");
        }

        return pkgConfig;
    }
}