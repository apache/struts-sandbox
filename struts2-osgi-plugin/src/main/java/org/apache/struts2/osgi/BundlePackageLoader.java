package org.apache.struts2.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class BundlePackageLoader implements PackageLoader {
    private static final Logger LOG = LoggerFactory.getLogger(BundlePackageLoader.class);
    
    public List<PackageConfig> loadPackages(Bundle bundle, BundleContext bundleContext, ObjectFactory objectFactory, Map<String,PackageConfig> pkgConfigs) throws ConfigurationException {
        BundleConfigurationProvider prov = new BundleConfigurationProvider("struts.xml", bundle, bundleContext);
        Configuration config = new DefaultConfiguration("struts.xml");
        for (PackageConfig pkg : pkgConfigs.values()) {
            config.addPackageConfig(pkg.getName(), pkg);
        }
        prov.setObjectFactory(objectFactory);
        prov.init(config);
        prov.loadPackages();
        return new ArrayList<PackageConfig>(config.getPackageConfigs().values());
    }
    
    static class BundleConfigurationProvider extends XmlConfigurationProvider {
        private Bundle bundle;
        private BundleContext bundleContext;

        public BundleConfigurationProvider(String filename, Bundle bundle, BundleContext bundleContext) { 
            super(filename, true);
            this.bundle = bundle;
            this.bundleContext = bundleContext;
        }
        public BundleConfigurationProvider(String filename) { super(filename); }

        @Override
        protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
            Enumeration<URL> e = bundle.getResources("struts.xml");
            Iterator<URL> iter = new EnumeratorIterator<URL>(e);
            return iter;
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
                    LOG.debug("Unable to find class #1 in bundle #2", className, bundle.getSymbolicName());

                //try to find a bean with that id
                try {
                    return SpringOSGiUtil.isValidBean(bundleContext, className);
                } catch (Exception e1) {
                    if (LOG.isDebugEnabled())
                        LOG.debug("Unable to find bean #1", className);
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
