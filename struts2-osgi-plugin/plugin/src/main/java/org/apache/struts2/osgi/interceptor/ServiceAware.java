package org.apache.struts2.osgi.interceptor;

import java.util.List;

/**
 * Classes implementing this interface, will be injected a list of services
 * registered with the type of the parameterized type
 * @param <T> The type of the service
 */
public interface ServiceAware<T> {
    void setServices(List<T> services);
}
