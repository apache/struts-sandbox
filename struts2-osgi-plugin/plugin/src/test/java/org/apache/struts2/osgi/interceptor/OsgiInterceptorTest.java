package org.apache.struts2.osgi.interceptor;

import org.easymock.EasyMock;
import org.apache.struts2.osgi.OsgiHost;
import org.osgi.framework.BundleContext;

import javax.servlet.ServletContext;

import com.opensymphony.xwork2.ActionInvocation;
import junit.framework.TestCase;

public class OsgiInterceptorTest extends TestCase {
    public void testBundleContextAware() throws Exception {
        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        BundleContext bundleContext = EasyMock.createStrictMock(BundleContext.class);
        ActionInvocation actionInvocation = EasyMock.createStrictMock(ActionInvocation.class);
        BundleContextAware bundleContextAware = EasyMock.createStrictMock(BundleContextAware.class);

        EasyMock.expect(servletContext.getAttribute(OsgiHost.OSGI_BUNDLE_CONTEXT)).andReturn(bundleContext);
        EasyMock.expect(actionInvocation.getAction()).andReturn(bundleContextAware);
        bundleContextAware.setBundleContext(bundleContext);
        EasyMock.expect(actionInvocation.invoke()).andReturn("");

        EasyMock.replay(bundleContextAware);
        EasyMock.replay(servletContext);
        EasyMock.replay(actionInvocation);

        OsgiInterceptor osgiInterceptor = new OsgiInterceptor();
        osgiInterceptor.setServletContext(servletContext);
        osgiInterceptor.intercept(actionInvocation);

        EasyMock.verify(bundleContextAware);
    }

     public void testBundleContextAwareNegative() throws Exception {
        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        ActionInvocation actionInvocation = EasyMock.createStrictMock(ActionInvocation.class);
        BundleContextAware bundleContextAware = EasyMock.createStrictMock(BundleContextAware.class);

        EasyMock.expect(servletContext.getAttribute(OsgiHost.OSGI_BUNDLE_CONTEXT)).andReturn(null);
        EasyMock.expect(actionInvocation.invoke()).andReturn("");

        EasyMock.replay(bundleContextAware);
        EasyMock.replay(servletContext);
        EasyMock.replay(actionInvocation);

        OsgiInterceptor osgiInterceptor = new OsgiInterceptor();
        osgiInterceptor.setServletContext(servletContext);
        osgiInterceptor.intercept(actionInvocation);

        EasyMock.verify(bundleContextAware);
    }
}
