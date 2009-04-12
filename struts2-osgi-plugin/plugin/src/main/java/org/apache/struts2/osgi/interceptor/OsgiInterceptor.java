package org.apache.struts2.osgi.interceptor;

import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.inject.Inject;

import javax.servlet.ServletContext;

import org.apache.struts2.osgi.OsgiHost;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.ArrayList;

/**
 * If a class implements BundleContextAware, this interceptor will call the setBundleContext(BundleContext)
 * method on it
 */
public class OsgiInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(OsgiInterceptor.class);

    private BundleContext bundleContext;

    public String intercept(ActionInvocation invocation) throws Exception {
        if (bundleContext != null) {
            Object action = invocation.getAction();

            //inject BundleContext
            if (action instanceof BundleContextAware)
                ((BundleContextAware)action).setBundleContext(bundleContext);

            //inject service implementations
            if (action instanceof ServiceAware) {
                Type[] types = action.getClass().getGenericInterfaces();
                if (types != null) {
                    for (Type type : types) {
                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            if (parameterizedType.getRawType() instanceof Class) {
                                Class clazz = (Class) parameterizedType.getRawType();
                                if (ServiceAware.class.equals(clazz)) {
                                    Class serviceClass = (Class) parameterizedType.getActualTypeArguments()[0];
                                    ServiceReference[] refs = bundleContext.getAllServiceReferences(serviceClass.getName(), null);
                                    //get the services
                                    if (refs != null) {
                                        List services = new ArrayList(refs.length);
                                        for (ServiceReference ref : refs) {
                                            Object service = bundleContext.getService(ref);
                                            //wow, that's a lot of nested ifs
                                            if (service != null)
                                                services.add(service);
                                        }

                                        if (!services.isEmpty())
                                            ((ServiceAware)action).setServices(services);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (LOG.isWarnEnabled()){
            LOG.warn("The OSGi interceptor was not able to find the BundleContext in the ServletContext");          
        }

        return invocation.invoke();
    }

    @Inject
    public void setServletContext(ServletContext servletContext) {
        this.bundleContext = (BundleContext) servletContext.getAttribute(OsgiHost.OSGI_BUNDLE_CONTEXT);
    }
}
