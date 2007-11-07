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

public class DefaultBundleAccessor implements BundleAccessor {

    private static DefaultBundleAccessor self;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBundleAccessor.class);
    
    private Map<String, Bundle> bundles = new HashMap<String, Bundle>();
    private Map<String,String> classToBundle = new HashMap<String,String>();
    private BundleContext bundleContext;
    
    public DefaultBundleAccessor() {
        self = this;
    }
    
    // todo: this is crap
    public static DefaultBundleAccessor getInstance() {
        return self;
    }
    
    public void setBundles(Map<String,Bundle> bundles) {
        this.bundles = bundles;
        classToBundle.clear();
    }
    
    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class cls = null;
        if (classToBundle.containsKey(name)) {
            bundles.get(classToBundle.get(name)).loadClass(name);
        } else {
            for (Entry<String,Bundle> entry : bundles.entrySet()) {
                try {
                    cls = entry.getValue().loadClass(name);
                    if (cls != null) {
                        classToBundle.put(name, entry.getKey());
                    }
                } catch (ClassNotFoundException ex) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("class not found in bundle "+entry.getValue().getSymbolicName());
                    }
                }
            }
        }
        
        if (cls == null) {
            //try spring
            try {
                Object bean = SpringOSGiUtil.getBean(bundleContext, name);
                if (bean != null)
                    cls = bean.getClass();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        if (cls == null) {
            throw new ClassNotFoundException("Unable to find class "+name+" in bundles");
        }
        return cls;
    }
    
    public List<URL> loadResources(String name) throws IOException {
        List<URL> resources = new ArrayList<URL>();
        for (Entry<String,Bundle> entry : bundles.entrySet()) {
            Enumeration e = entry.getValue().getResources(name);
            while (e.hasMoreElements()) {
                resources.add((URL) e.nextElement());
            }
        }
        return resources;
    }

    public URL loadResource(String name) {
        URL url = null;
        for (Entry<String,Bundle> entry : bundles.entrySet()) {
            url = entry.getValue().getResource(name);
            if (url != null)
                break;
        }
        return url;
    }

    public InputStream loadResourceAsStream(String name) throws IOException {
        URL url = loadResource(name);
        if (url != null) {
            return url.openStream();
        }
        return null;
    }
}
