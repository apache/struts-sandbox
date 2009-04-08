package org.apache.struts2.osgi.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.struts2.dispatcher.DefaultStaticContentLoader;
import org.apache.struts2.osgi.DefaultBundleAccessor;
import org.apache.struts2.osgi.BundleAccessor;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Loads static resources from bundles 
 *
 */
public class StaticContentBundleResourceLoader extends DefaultStaticContentLoader {
    private BundleAccessor bundleAccessor;

    protected URL findResource(String path) throws IOException {
        return bundleAccessor.loadResourceFromAllBundles(path);
    }

    @Inject
    public void setBundleAccessor(BundleAccessor bundleAccessor) {
        this.bundleAccessor = bundleAccessor;
    }
}
