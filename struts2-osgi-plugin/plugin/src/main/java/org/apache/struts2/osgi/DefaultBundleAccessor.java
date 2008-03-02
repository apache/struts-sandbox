package org.apache.struts2.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class DefaultBundleAccessor implements BundleAccessor {

    private static DefaultBundleAccessor self;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBundleAccessor.class);
    
    private Map<String, Bundle> bundles = new HashMap<String, Bundle>();
    private BundleContext bundleContext;
    private Map<String, String> packageToBundle;
    private Map<Bundle, Set<String>> packagesByBundle;

    public DefaultBundleAccessor() {
        self = this;
    }
    
    // todo: this is crap
    public static DefaultBundleAccessor getInstance() {
        return self;
    }
    
    public void init(Map<String,Bundle> bundles, BundleContext bundleContext, Map<String, String> packageToBundle) {
        this.bundles = Collections.unmodifiableMap(bundles);
        this.bundleContext = bundleContext;
        this.packageToBundle = Collections.unmodifiableMap(packageToBundle);
        this.packagesByBundle = new HashMap<Bundle, Set<String>>();
        for (Map.Entry<String,String> entry : packageToBundle.entrySet()) {
            Bundle bundle = bundles.get(entry.getValue());
            Set<String> pkgs = packagesByBundle.get(bundle);
            if (pkgs == null) {
                pkgs = new HashSet<String>();
                packagesByBundle.put(bundle, pkgs);
            }
            pkgs.add(entry.getKey());
        }
        this.packagesByBundle = Collections.unmodifiableMap(packagesByBundle);
    }
    
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        Class cls = null;
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            cls = bundle.loadClass(className);
            LOG.debug("Located class #1 in bundle #2", className, bundle.getSymbolicName());
        }

        if (cls == null) {
            //try to find a bean with that id (hack for spring that searches all bundles)
            try {
                Object bean = SpringOSGiUtil.getBean(bundleContext, className);
                if (bean != null)
                    cls = bean.getClass();
            } catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Unable to find bean #1", className);
            }
        }
        
        if (cls == null) {
            throw new ClassNotFoundException("Unable to find class "+className+" in bundles");
        }
        return cls;
    }

    private Bundle getCurrentBundle() {
        ActionContext ctx = ActionContext.getContext();
        String bundleName = (String) ctx.get(CURRENT_BUNDLE_NAME);
        if (bundleName == null) {
            ActionInvocation inv = ctx.getActionInvocation();
            ActionProxy proxy = inv.getProxy();
            ActionConfig actionConfig = proxy.getConfig();
            bundleName = packageToBundle.get(actionConfig.getPackageName());
        }
        if (bundleName != null) {
            return bundles.get(bundleName);
        }
        return null;
    }

    public List<URL> loadResources(String name) throws IOException {
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            List<URL> resources = new ArrayList<URL>();
            Enumeration e = bundle.getResources(name);
            while (e.hasMoreElements()) {
                resources.add((URL) e.nextElement());
            }
            return resources;
        }

        return null;
    }

    public URL loadResource(String name) {
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            return bundle.getResource(name);
        }
        return null;
    }

    public Map<String, Bundle> getBundles() {
        return bundles;
    }

    public Set<String> getPackagesByBundle(Bundle bundle) {
        return packagesByBundle.get(bundle);
    }

    public InputStream loadResourceAsStream(String name) throws IOException {
        URL url = loadResource(name);
        if (url != null) { 
            return url.openStream();
        }
        return null;
    }
}
