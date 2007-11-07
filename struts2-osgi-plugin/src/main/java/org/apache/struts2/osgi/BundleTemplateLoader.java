package org.apache.struts2.osgi;

import java.net.URL;

import freemarker.cache.URLTemplateLoader;

/**
 * Finds FreeMarker templates in bundles
 */
public class BundleTemplateLoader extends URLTemplateLoader {

    @Override
    protected URL getURL(String name) {
        return DefaultBundleAccessor.getInstance().loadResource(name);
    }

}
