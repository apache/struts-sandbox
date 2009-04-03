package org.apache.struts2.osgi;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.osgi.loaders.VelocityBundleResourceLoader;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.Velocity;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
        Map<String, String> packageToBundle = new HashMap<String, String>();
        Set<String> bundleNames = new HashSet<String>();
        if (refs != null) {
            for (ServiceReference ref : refs) {
                if (!bundleNames.contains(ref.getBundle().getSymbolicName())) {
                    bundleNames.add(ref.getBundle().getSymbolicName());

                    if (LOG.isDebugEnabled())
                        LOG.debug("Loading packages from bundle [#0]", ref.getBundle().getSymbolicName());

                    PackageLoader loader = (PackageLoader) bundleContext.getService(ref);
                    for (PackageConfig pkg : loader.loadPackages(ref.getBundle(), bundleContext, objectFactory, configuration.getPackageConfigs())) {
                        configuration.addPackageConfig(pkg.getName(), pkg);
                        packageToBundle.put(pkg.getName(), ref.getBundle().getSymbolicName());
                    }
                }
            }
        }
        bundleAccessor.init(osgiHost.getBundles(), bundleContext, packageToBundle);
        bundlesChanged = false;
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
        }

        public void stop(BundleContext ctx) throws Exception {
        }

        public void bundleChanged(BundleEvent evt) {
            if (evt.getType() == BundleEvent.STARTED && evt.getBundle().getSymbolicName() != null) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Started bundle [#0]", evt.getBundle().getSymbolicName());

                osgiHost.getBundles().put(evt.getBundle().getSymbolicName(), evt.getBundle());
                bundlesChanged = true;
            }
        }
    }

}
