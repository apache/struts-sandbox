package org.apache.struts2.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class BundlePackageLoader implements PackageLoader {
    private static final Logger LOG = LoggerFactory.getLogger(BundlePackageLoader.class);

    public List<PackageConfig> loadPackages(Bundle bundle, BundleContext bundleContext, ObjectFactory objectFactory, Map<String,PackageConfig> pkgConfigs) throws ConfigurationException {
        Configuration config = new DefaultConfiguration("struts.xml");
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = new ActionContext(new HashMap());
            ActionContext.setContext(ctx);
        }

        try {
            // Ensure all requested classes and resources will be resolved using the current bundle
            ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, bundle.getSymbolicName());

            BundleConfigurationProvider prov = new BundleConfigurationProvider("struts.xml", bundle, bundleContext);
            for (PackageConfig pkg : pkgConfigs.values()) {
                config.addPackageConfig(pkg.getName(), pkg);
            }
            prov.setObjectFactory(objectFactory);
            prov.init(config);
            prov.loadPackages();
        } finally {
            ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, null);
        }

        List<PackageConfig> list = new ArrayList<PackageConfig>(config.getPackageConfigs().values());
        list.removeAll(pkgConfigs.values());
        
        return list;
    }
    
    static class BundleConfigurationProvider extends XmlConfigurationProvider {
        private Bundle bundle;
        private BundleContext bundleContext;

        public BundleConfigurationProvider(String filename, Bundle bundle, BundleContext bundleContext) { 
            super(filename, false);
            this.bundle = bundle;
            this.bundleContext = bundleContext;
        }
        public BundleConfigurationProvider(String filename) { super(filename); }

        @Override
        protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
            Enumeration<URL> e = bundle.getResources("struts.xml");
            return e.hasMoreElements() ? new EnumeratorIterator<URL>(e) : null;
        }
        
        /* 
         * Try to find the class (className) on this bundle. If the class it not found,
         * try to find an Spring bean with that name. 
         */
        @Override
        protected boolean verifyAction(String className, String name, Location loc) {
            try {
                return bundle.loadClass(className) != null;
            } catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Unable to find class [#0] in bundle [#1]", className, bundle.getSymbolicName());

                //try to find a bean with that id
                try {
                    return OsgiUtil.isValidBean(bundleContext, className);
                } catch (Exception e1) {
                    if (LOG.isDebugEnabled())
                        LOG.debug("Unable to find bean [#0]", className);
                }
                
                return false;
            }
        }
    }
    
    static class EnumeratorIterator<E> implements Iterator<E> {
        Enumeration<E> e = null;
        public EnumeratorIterator(Enumeration<E> e) {
            this.e = e;
        }
        public boolean hasNext() {
          return e.hasMoreElements();
        }

        public E next() {
          return e.nextElement();
        }

        public void remove() {
          throw new UnsupportedOperationException();
        }
      }

}
