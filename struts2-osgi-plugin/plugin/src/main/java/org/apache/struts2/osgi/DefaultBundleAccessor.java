package org.apache.struts2.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class DefaultBundleAccessor implements BundleAccessor {

    private static DefaultBundleAccessor self;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBundleAccessor.class);
    
    private Map<String, Bundle> bundles = new HashMap<String, Bundle>();
    private BundleContext bundleContext;
    private Map<String, String> packageToBundle;

    public DefaultBundleAccessor() {
        self = this;
    }
    
    // todo: this is crap
    public static DefaultBundleAccessor getInstance() {
        return self;
    }
    
    public void setBundles(Map<String,Bundle> bundles) {
        this.bundles = bundles;
    }
    
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        Class cls = null;
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            bundle.loadClass(className);
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
        if (bundleName != null) {
            ActionConfig actionConfig = ctx.getActionInvocation().getProxy().getConfig();
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

    public void setPackageToBundleMapping(Map<String, String> packageToBundle) {
        this.packageToBundle = packageToBundle;
    }

    public InputStream loadResourceAsStream(String name) throws IOException {
        URL url = loadResource(name);
        if (url != null) { 
            return url.openStream();
        }
        return null;
    }
}
