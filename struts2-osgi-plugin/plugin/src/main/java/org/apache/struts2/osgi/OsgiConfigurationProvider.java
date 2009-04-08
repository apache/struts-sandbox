/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.osgi;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import org.apache.struts2.osgi.loaders.VelocityBundleResourceLoader;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.Velocity;
import org.apache.commons.lang.xwork.StringUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Bundle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Struts package provider that starts the OSGi container and deelgates package loading
 */
public class OsgiConfigurationProvider implements PackageProvider {

    private static final Logger LOG = LoggerFactory.getLogger(OsgiConfigurationProvider.class);

    private Configuration configuration;
    private ObjectFactory objectFactory;
    private OsgiHost osgiHost;

    private BundleContext bundleContext;
    private BundleAccessor bundleAccessor;
    private boolean bundlesChanged = false;

    public void destroy() {
        try {
            if (LOG.isTraceEnabled())
                LOG.trace("Stopping OSGi container");
            osgiHost.destroy();
        } catch (Exception e) {
            LOG.error("Failed to stop OSGi container", e);
        }
    }

    public void init(Configuration configuration) throws ConfigurationException {
        if (LOG.isTraceEnabled())
            LOG.trace("Starting OSGi container");
        try {
            osgiHost.setExtraBundleActivators(Arrays.asList(new BundleRegistrationListener()));
            osgiHost.init();
            bundleAccessor.setBundles(osgiHost.getBundles());
        } catch (Exception e) {
            if (LOG.isErrorEnabled())
                LOG.error("Failed to start the OSGi container", e);
            throw new ConfigurationException(e);
        }
        this.configuration = configuration;
    }

    public synchronized void loadPackages() throws ConfigurationException {
        ServiceReference[] refs;
        try {
            refs = bundleContext.getServiceReferences(PackageLoader.class.getName(), null);
        } catch (InvalidSyntaxException e) {
            throw new ConfigurationException(e);
        }


        //init action contect
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = new ActionContext(new HashMap());
            ActionContext.setContext(ctx);
        }

        Map<String, String> packageToBundle = new HashMap<String, String>();
        Set<String> bundleNames = new HashSet<String>();
        
        if (refs != null) {
            for (ServiceReference ref : refs) {
                String bundleName = ref.getBundle().getSymbolicName();
                if (!bundleNames.contains(bundleName)) {
                    bundleNames.add(bundleName);

                    if (LOG.isDebugEnabled())
                        LOG.debug("Loading packages from bundle [#0]", bundleName);

                    PackageLoader loader = (PackageLoader) bundleContext.getService(ref);
                    try {
                        ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, bundleName);
                        for (PackageConfig pkg : loader.loadPackages(ref.getBundle(), bundleContext, objectFactory, configuration.getPackageConfigs())) {
                            configuration.addPackageConfig(pkg.getName(), pkg);
                            packageToBundle.put(pkg.getName(), bundleName);
                        }
                    } finally {
                        ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, null);                        
                    }
                }
            }
        }

        //add the loaded packages to the BundleAccessor
        bundleAccessor.setPackageToBundle(packageToBundle);

        //reload container that will load configuration based on bundles (like convention plugin)
        reloadExtraProviders(configuration.getContainer());

        bundlesChanged = false;
    }

    protected void reloadExtraProviders(Container container) {
        //these providers will be reloaded for each bundle
        List<PackageProvider> providers = new ArrayList<PackageProvider>();
        PackageProvider conventionPackageProvider = container.getInstance(PackageProvider.class, "convention.packageProvider");
        if (conventionPackageProvider != null)
            providers.add(conventionPackageProvider);

        //init action context
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = new ActionContext(new HashMap());
            ActionContext.setContext(ctx);
        }

        //reload all providers by bundle
        for (Bundle bundle : osgiHost.getBundles().values()) {
            try {
                //the Convention plugin will use BundleClassLoaderInterface from the ActionContext to find resources
                //and load classes
                ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, new BundleClassLoaderInterface());
                ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, bundle.getSymbolicName());
                
                Object bundleActivator = bundle.getHeaders().get("Bundle-Activator");
                if (bundleActivator != null && StringUtils.equals(StrutsActivator.class.getName(), bundleActivator.toString())) {                   ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, bundle.getSymbolicName());
                    for (PackageProvider provider : providers) {
                        if (LOG.isDebugEnabled())
                            LOG.debug("Reloading provider [#0] for bundle [#1]", provider.getClass().getName(), bundle.getSymbolicName());
                        //get the existing packages before reloading the provider (se we can figure out what are the new packages)
                        Set<String> packagesBeforeLoading = new HashSet(configuration.getPackageConfigNames());
                        provider.loadPackages();
                        Set<String> packagesAfterLoading = new HashSet(configuration.getPackageConfigNames());
                        packagesAfterLoading.removeAll(packagesBeforeLoading);
                        if (!packagesAfterLoading.isEmpty()) {
                            //add the new packages to the map of bundle -> package
                            for (String packageName : packagesAfterLoading)
                                bundleAccessor.addPackageFromBundle(bundle, packageName);
                        }
                    }
                }
            } finally {
                ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, null);
                ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, null);
            }
        }
    }

    public synchronized boolean needsReload() {
        return bundlesChanged;
    }

    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    @Inject("felix")
    public void setOsgiHost(OsgiHost osgiHost) {
        this.osgiHost = osgiHost;
    }

    @Inject
    public void setBundleAccessor(BundleAccessor acc) {
        this.bundleAccessor = acc;
    }

    @Inject
    public void setVelocityManager(VelocityManager vm) {
        Properties props = new Properties();
        props.setProperty("osgi.resource.loader.description", "OSGI bundle loader");
        props.setProperty("osgi.resource.loader.class", VelocityBundleResourceLoader.class.getName());
        props.setProperty(Velocity.RESOURCE_LOADER, "strutsfile,strutsclass,osgi");
        vm.setVelocityProperties(props);
    }

    /**
     * Listens to bundle events and adds bundles to the bundle list when one is activated
     */
    class BundleRegistrationListener implements BundleActivator, BundleListener {
        public void start(BundleContext context) throws Exception {
            context.addBundleListener(this);
            bundleContext = context;
            bundleAccessor.setBundleContext(bundleContext);
        }

        public void stop(BundleContext ctx) throws Exception {
        }

        public void bundleChanged(BundleEvent evt) {
            if (evt.getType() == BundleEvent.STARTED && evt.getBundle().getSymbolicName() != null) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Started bundle [#0]", evt.getBundle().getSymbolicName());

                osgiHost.addBundle(evt.getBundle());
                bundlesChanged = true;
            }
        }
    }

}
