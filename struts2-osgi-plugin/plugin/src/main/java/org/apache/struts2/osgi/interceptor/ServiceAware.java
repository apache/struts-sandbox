package org.apache.struts2.osgi.interceptor;

import java.util.List;

public interface ServiceAware<T> {
    void setServices(List<T> service);
}
