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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.osgi.loaders.VelocityBundleResourceLoader;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.Velocity;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Struts package provider that starts the OSGi container and deelgates package loading
 */
public class OsgiConfigurationProvider implements PackageProvider {

    private static final String STRUTS_ENABLED = "Struts2-Enabled";
    private static final Logger LOG = LoggerFactory.getLogger(OsgiConfigurationProvider.class);

    private Configuration configuration;
    private ObjectFactory objectFactory;

    private OsgiHost osgiHost;
    private BundleContext bundleContext;
    private BundleAccessor bundleAccessor;
    private boolean bundlesChanged = false;
    private ServletContext servletContext;

    public void init(Configuration configuration) throws ConfigurationException {
        osgiHost = (OsgiHost) servletContext.getAttribute(StrutsOsgiListener.OSGI_HOST);
        bundleContext = osgiHost.getBundleContext();
        bundleAccessor.setBundleContext(bundleContext);
        // I can't figure out why BundleAccessor doesn't get the OsgiHost injected
        //for reason it always gets a new instace...weird
        bundleAccessor.setOsgiHost(osgiHost);
        this.configuration = configuration;
    }

    public synchronized void loadPackages() throws ConfigurationException {
        //init action contect
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = new ActionContext(new HashMap());
            ActionContext.setContext(ctx);
        }

        Map<String, String> packageToBundle = new HashMap<String, String>();
        Set<String> bundleNames = new HashSet<String>();

        //iterate over the bundles and load packages from them
        for (Bundle bundle : osgiHost.getBundles().values()) {
            String bundleName = bundle.getSymbolicName();
            if (shouldProcessBundle(bundle) && !bundleNames.contains(bundleName)) {
                bundleNames.add(bundleName);

                if (LOG.isDebugEnabled())
                    LOG.debug("Loading packages from bundle [#0]", bundleName);

                PackageLoader loader = new BundlePackageLoader();
                for (PackageConfig pkg : loader.loadPackages(bundle, bundleContext, objectFactory, configuration.getPackageConfigs())) {
                    configuration.addPackageConfig(pkg.getName(), pkg);
                    packageToBundle.put(pkg.getName(), bundleName);
                }
            }
        }

        //add the loaded packages to the BundleAccessor
        bundleAccessor.setPackageToBundle(packageToBundle);

        //reload container, that will load configuration based on bundles (like convention plugin)
        reloadExtraProviders(configuration.getContainer());

        bundlesChanged = false;
    }

    protected void reloadExtraProviders(Container container) {
        //these providers will be reloaded for each bundle
        List<PackageProvider> providers = new ArrayList<PackageProvider>();
        PackageProvider conventionPackageProvider = container.getInstance(PackageProvider.class, "convention.packageProvider");
        if (conventionPackageProvider != null)
            providers.add(conventionPackageProvider);

        //reload all providers by bundle
        ActionContext ctx = ActionContext.getContext();
        for (Bundle bundle : osgiHost.getBundles().values()) {
            String bundleName = bundle.getSymbolicName();
            if (shouldProcessBundle(bundle)) {
                try {
                    //the Convention plugin will use BundleClassLoaderInterface from the ActionContext to find resources
                    //and load classes
                    ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, new BundleClassLoaderInterface());
                    ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, bundleName);

                    for (PackageProvider provider : providers) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Reloading provider [#0] for bundle [#1]", provider.getClass().getName(), bundleName);
                        }

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
                } finally {
                    ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, null);
                    ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, null);
                }
            }
        }
    }

    /**
     * Checks for "Struts2-Enabled" header in the bundle
     */
    protected boolean shouldProcessBundle(Bundle bundle) {
        String strutsEnabled = (String) bundle.getHeaders().get(STRUTS_ENABLED);

        return "true".equalsIgnoreCase(strutsEnabled);
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

    @Inject
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void destroy() {
        try {
            if (LOG.isTraceEnabled())
                LOG.trace("Stopping OSGi container");
            osgiHost.destroy();
        } catch (Exception e) {
            LOG.error("Failed to stop OSGi container", e);
        }
    }
}
