package org.apache.struts2.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.util.Map.Entry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * Helper class that find resources and loads classes from the list of bundles
 */
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

    public static DefaultBundleAccessor getInstance() {
        return self;
    }

    public Object getService(ServiceReference ref) {
        return bundleContext.getService(ref);
    }

    public ServiceReference getServiceReference(String className) {
        return bundleContext.getServiceReference(className);
    }

    public void init(Map<String, Bundle> bundles, BundleContext bundleContext, Map<String, String> packageToBundle) {
        this.bundles = Collections.unmodifiableMap(bundles);
        this.bundleContext = bundleContext;
        this.packageToBundle = packageToBundle;
        this.packagesByBundle = new HashMap<Bundle, Set<String>>();
        for (Map.Entry<String, String> entry : packageToBundle.entrySet()) {
            Bundle bundle = bundles.get(entry.getValue());
            addPackageFromBundle(bundle, entry.getKey());
        }
    }

    /**
     *  Add as Bundle -> Package mapping 
     * @param bundle the bundle where the package was loaded from
     * @param packageName the anme of the loaded package
     */
    public void addPackageFromBundle(Bundle bundle, String packageName) {
        this.packageToBundle.put(packageName, bundle.getSymbolicName());
        Set<String> pkgs = packagesByBundle.get(bundle);
        if (pkgs == null) {
            pkgs = new HashSet<String>();
            packagesByBundle.put(bundle, pkgs);
        }
        pkgs.add(packageName);
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        Class cls = null;

        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            cls = bundle.loadClass(className);
            LOG.debug("Located class [#0] in bundle [#1]", className, bundle.getSymbolicName());
        }

        //try all the bundles
        for (Bundle bundle2 : bundles.values()) {
            try {
                return bundle2.loadClass(className);
            } catch (Exception ex) {
                //ignore
            }
        }

        if (cls == null) {
            //try to find a bean with that id (hack for spring that searches all bundles)
            try {
                Object bean = OsgiUtil.getBean(bundleContext, className);
                if (bean != null)
                    cls = bean.getClass();
            } catch (Exception e) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Unable to find bean [#0]", className);
            }
        }

        if (cls == null) {
            throw new ClassNotFoundException("Unable to find class " + className + " in bundles");
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
        return loadResources(name, false);
    }

    public List<URL> loadResources(String name, boolean translate) throws IOException {
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            List<URL> resources = new ArrayList<URL>();
            Enumeration e = bundle.getResources(name);
            while (e.hasMoreElements()) {
                resources.add(translate ? OsgiUtil.translateBundleURLToJarURL((URL) e.nextElement(), getCurrentBundle()) : (URL) e.nextElement());
            }
            return resources;
        }

        return null;
    }

    public URL loadResourceFromAllBundles(String name) throws IOException {
        for (Map.Entry<String, Bundle> entry : bundles.entrySet()) {
            Enumeration e = entry.getValue().getResources(name);
            if (e.hasMoreElements()) {
                return (URL) e.nextElement();
            }
        }

        return null;
    }

    public InputStream loadResourceFromAllBundlesAsStream(String name) throws IOException {
        URL url = loadResourceFromAllBundles(name);
        if (url != null) {
            return url.openStream();
        }
        return null;
    }

    public URL loadResource(String name) {
        return loadResource(name, false);
    }

    public URL loadResource(String name, boolean translate) {
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            URL url = bundle.getResource(name);
            try {
                return translate ? OsgiUtil.translateBundleURLToJarURL(url, getCurrentBundle()) : url;
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to translate bunfle URL to jar URL", e);
                }

                return null;
            }
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
