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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.AnnotationTools;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;

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
    private String resultLocation;
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
     * @param   actionPackages (Optional) An optional list of action packages that this should create
     *          configuration for.
     * @param   excludePackages (Optional) A  list of packages that should be skipped when building
     *          configuration.
     * @param   packageLocators (Optional) A list of names used to find action packages.
     * @param   defaultParentPackage The default parent package for all the configuration.
     * @param   resultLocation The default result location.
     */
    @Inject
    public PackageBasedActionConfigBuilder(Configuration configuration, ActionNameBuilder actionNameBuilder,
            ResultMapBuilder resultMapBuilder, ObjectFactory objectFactory,
            @Inject("struts.convention.redirect.to.slash") String redirectToSlash,
            @Inject(value = "struts.convention.action.packages", required = false) String actionPackages,
            @Inject(value = "struts.convention.exclude.packages", required = false) String excludePackages,
            @Inject(value = "struts.convention.package.locators", required = false) String packageLocators,
            @Inject("struts.convention.default.parent.package") String defaultParentPackage,
            @Inject("struts.convention.result.location") String resultLocation) {
        if (StringTools.isTrimmedEmpty(actionPackages) && StringTools.isTrimmedEmpty(packageLocators)) {
            throw new ConfigurationException("At least a list of action packages or action package locators " +
                "must be given using one of the properties [struts.convention.action.packages] or " +
                "[struts.convention.package.locators]");
        }

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
        this.resultLocation = resultLocation;
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

        Set<Class<?>> classes = findActionsInNamedPackages();
        classes.addAll(findActionsUsingPackageLocators());
        buildConfiguration(classes);
    }

    protected Set<Class<?>> findActionsInNamedPackages() {
        ClassClassLoaderResolver resolver = new ClassClassLoaderResolver();
        resolver.find(new ClassClassLoaderResolver.Test<Class<?>>() {
            public boolean test(Class type) {
                return com.opensymphony.xwork2.Action.class.isAssignableFrom(type) ||
                    type.getSimpleName().endsWith("Action");
            }
        }, true, actionPackages);

        return resolver.getMatches();
    }

    protected Set<Class<?>> findActionsUsingPackageLocators() {
        ClassClassLoaderResolver resolver = new ClassClassLoaderResolver();
        resolver.findByLocators(new ClassClassLoaderResolver.Test<Class<?>>() {
            public boolean test(Class<?> type) {
                return com.opensymphony.xwork2.Action.class.isAssignableFrom(type) ||
                    type.getSimpleName().endsWith("Action");
            }
        }, true, excludePackages, packageLocators);

        return resolver.getMatches();
    }

    protected void buildConfiguration(Set<Class<?>> classes) {
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

            // Determine the default namespace and action name
            String defaultActionNamespace = determineActionNamespace(actionClass);
            String defaultActionName = determineActionName(actionClass);

            // Determine the action package
            String actionPackage = actionClass.getPackage().getName();
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Processing class [" + actionClass.getName() + "] in package [" +
                    actionPackage + "]");
            }

            // Create or retrieve the default package config
            PackageConfig defaultPkgCfg = getPackageConfig(packageConfigs, defaultActionNamespace,
                actionPackage, actionClass, null);

            // Verify that the annotations have no errors and also determine if the default action
            // configuration should still be built or not.
            Map<String, List<Action>> map = getActionAnnotations(actionClass);
            Set<String> actionNames = new HashSet<String>();
            if (!map.containsKey("execute") && ReflectionTools.containsMethod(actionClass, "execute")) {
                boolean found = false;
                for (String method : map.keySet()) {
                    List<Action> actions = map.get(method);
                    for (Action action : actions) {

                        // Check if there are duplicate action names in the annotations.
                        String actionName = action.value().equals(Action.DEFAULT_VALUE) ? defaultActionName : action.value();
                        if (actionNames.contains(actionName)) {
                            throw new ConfigurationException("The action class [" + actionClass +
                                "] contains two methods with an action name annotation whose value " +
                                "is the same (they both might be empty as well).");
                        } else {
                            actionNames.add(actionName);
                        }

                        // Check if there are multiple annotations with no value.
                        if (action.value().equals(Action.DEFAULT_VALUE)) {
                            found = true;
                        }
                    }
                }

                // Build the default
                if (!found) {
                    createActionConfig(defaultPkgCfg, actionClass, defaultActionName, "execute", null);
                }
            }

            // Build the actions for the annotations
            for (String method : map.keySet()) {
                List<Action> actions = map.get(method);
                for (Action action : actions) {
                    PackageConfig pkgCfg = defaultPkgCfg;
                    if (!action.value().contains("/")) {
                        pkgCfg = getPackageConfig(packageConfigs, defaultActionNamespace, actionPackage,
                            actionClass, action);
                    }

                    createActionConfig(pkgCfg, actionClass, defaultActionName, method, action);
                }
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
     * Locates all of the {@link Actions} and {@link Action} annotations on methods within the Action
     * class and its parent classes.
     *
     * @param   actionClass The action class.
     * @return  The list of annotations or an empty list if there are none.
     */
    protected Map<String, List<Action>> getActionAnnotations(Class<?> actionClass) {
        Method[] methods = actionClass.getMethods();
        Map<String, List<Action>> map = new HashMap<String, List<Action>>();
        for (Method method : methods) {
            Actions actionsAnnotation = method.getAnnotation(Actions.class);
            if (actionsAnnotation != null) {
                Action[] actionArray = actionsAnnotation.value();
                boolean valuelessSeen = false;
                List<Action> actions = new ArrayList<Action>();
                for (Action ann : actionArray) {
                    if (ann.value().equals(Action.DEFAULT_VALUE) && !valuelessSeen) {
                        valuelessSeen = true;
                    } else if (ann.value().equals(Action.DEFAULT_VALUE)) {
                        throw new ConfigurationException("You may only add a single Action " +
                            "annotation that has no value parameter.");
                    }

                    actions.add(ann);
                }

                map.put(method.getName(), actions);
            } else {
                Action ann = method.getAnnotation(Action.class);
                if (ann != null) {
                    map.put(method.getName(), Arrays.asList(ann));
                }
            }
        }

        return map;
    }

    /**
     * Determines the namespace for the action based on the action class. If there is a {@link Namespace}
     * annotation on the class (including parent classes) or on the package that the class is in, than
     * it is used. Otherwise, the Java package name that the class is in is used inconjunction with
     * either the <b>struts.convention.action.packages</b> or <b>struts.convention.package.locators</b>
     * configuration values. These are used to determine which part of the Java package name should
     * be converted into the namespace for the XWork PackageConfig.
     *
     * @param   actionClass The action class.
     * @return  The namespace or an empty string.
     */
    protected String determineActionNamespace(Class<?> actionClass) {
        // Check if there is a class or package level annotation for the namespace
        Namespace ns = AnnotationTools.findAnnotation(actionClass, Namespace.class);
        if (ns != null) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Using non-default action namespace from Namespace annotation of [" +
                    ns.value() + "]");
            }

            return ns.value();
        }

        String pkg = actionClass.getPackage().getName();
        String pkgPart = null;
        if (actionPackages != null) {
            for (String actionPackage : actionPackages) {
                if (pkg.startsWith(actionPackage)) {
                    pkgPart = actionClass.getName().substring(actionPackage.length() + 1);
                }
            }
        }

        if (pkgPart == null && packageLocators != null) {
            for (String packageLocator : packageLocators) {
                int index = pkg.lastIndexOf(packageLocator);

                // This ensures that the match is at the end, beginning or has a dot on each side of it
                if (index >= 0 && (index + packageLocator.length() == pkg.length() || index == 0 ||
                        (pkg.charAt(index - 1) == '.' && pkg.charAt(index + packageLocator.length()) == '.'))) {
                    pkgPart = actionClass.getName().substring(index + packageLocator.length() + 1);
                }
            }
        }

        if (pkgPart != null) {
            final int indexOfDot = pkgPart.lastIndexOf('.');
            if (indexOfDot >= 0) {
                String convertedNamespace = actionNameBuilder.build(pkgPart.substring(0, indexOfDot));
                return "/" + convertedNamespace.replace('.', '/');
            }
        }

        return "";
    }

    /**
     * Converts the class name into an action name using the ActionNameBuilder.
     *
     * @param   actionClass The action class.
     * @return  The action name.
     */
    protected String determineActionName(Class<?> actionClass) {
        String actionName = actionNameBuilder.build(actionClass.getName());
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Got actionName for class [" + actionClass + "] of [" + actionName + "]");
        }

        return actionName;
    }

    /**
     * Creates a single ActionConfig object.
     *
     * @param   pkgCfg The package the action configuration instance will belong to.
     * @param   actionClass The action class.
     * @param   actionName The name of the action.
     * @param   method The method that the annotation was on (if the annotation is not null).
     * @param   annotation The ActionName annotation that might override the action name and possibly
     */
    protected void createActionConfig(PackageConfig pkgCfg, Class<?> actionClass, String actionName,
            String method, Action annotation) {
        String actionMethod = null;
        if (annotation != null) {
            actionName = annotation.value() != null && annotation.value().equals(Action.DEFAULT_VALUE) ?
                actionName : annotation.value();
            actionName = StringTools.lastToken(actionName, "/");
            actionMethod = method;
        }

        ConventionActionConfig actionConfig = new ConventionActionConfig();
        actionConfig.setClassName(actionClass.getName());
        actionConfig.setPackageName(pkgCfg.getName());
        if (actionMethod != null) {
            actionConfig.setMethodName(actionMethod);
        }

        // Add the base result location for the unknown handler
        actionConfig.setBaseResultLocation(resultLocation);

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Creating action config for class [" + actionClass + "], name [" + actionName +
                "] and package name [" + pkgCfg.getName() + "] in namespace [" + pkgCfg.getNamespace() +
                "]");
        }

        Map<String, ResultConfig> results = resultMapBuilder.build(actionClass, method, annotation,
            actionName, pkgCfg);
        actionConfig.setResults(results);

        pkgCfg.addActionConfig(actionName, actionConfig);
    }

    private PackageConfig getPackageConfig(final Map<String, PackageConfig> packageConfigs,
            String actionNamespace, final String actionPackage, final Class<?> actionClass,
            Action action) {
        if (action != null && !action.value().equals(Action.DEFAULT_VALUE)) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Using non-default action namespace from the Action annotation of [" +
                    action.value() + "]");
            }
            actionNamespace = StringTools.upToLastToken(action.value(), "/");
        }

        // Next grab the parent annotation from the class
        ParentPackage parent = AnnotationTools.findAnnotation(actionClass, ParentPackage.class);
        String parentName = null;
        if (parent != null) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Using non-default parent package from annotation of [" + parent.value() + "]");
            }

            parentName = parent.value();
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
        String name = actionPackage + "#" + parentPkg.getName() + "#" + actionNamespace;
        PackageConfig pkgConfig = packageConfigs.get(name);
        if (pkgConfig == null) {
            pkgConfig = new PackageConfig();
            pkgConfig.setName(name);
            pkgConfig.setNamespace(actionNamespace);
            pkgConfig.addParent(parentPkg);
            packageConfigs.put(name, pkgConfig);
        }

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Created package config named [" + name + "] with a namespace [" +
                actionNamespace + "]");
        }

        return pkgConfig;
    }
}