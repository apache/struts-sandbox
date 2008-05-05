package org.apache.struts2.osgi.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.apache.struts2.dispatcher.DefaultStaticContentLoader;
import org.apache.struts2.osgi.DefaultBundleAccessor;

/**
 * Loads static resources from bundles 
 *
 */
public class StaticContentBundleResourceLoader extends DefaultStaticContentLoader {
    @Override
    protected InputStream findInputStream(String path) throws IOException {
        return DefaultBundleAccessor.getInstance().loadResourceFromAllBundlesAsStream(path);
    }
}
