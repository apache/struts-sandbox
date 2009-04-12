package org.apache.struts2.osgi.interceptor;

import org.osgi.framework.BundleContext;

/**
 * Actions implementing this interface will receive an instance of the BundleContext,
 * the OsgiInterceptor must be applied to the action.
 */
public interface BundleContextAware {
    void setBundleContext(BundleContext bundleContext);
}
