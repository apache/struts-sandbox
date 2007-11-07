package org.apache.struts2.osgi;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class StrutsActivator implements BundleActivator {

    public void start(final BundleContext ctx) throws Exception {
        ctx.registerService(PackageLoader.class.getName(), new BundlePackageLoader(), new Properties());
        ctx.getBundle().loadClass("org.apache.struts2.osgi.BundleAccessor");
    }

    public void stop(BundleContext ctx) throws Exception {
    }

}
