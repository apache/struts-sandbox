package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.ObjectFactory;

import javax.el.ELResolver;
import javax.el.ELContext;
import java.util.Iterator;
import java.beans.FeatureDescriptor;


public abstract class AbstractResolver extends ELResolver {
    protected ReflectionProvider reflectionProvider;
    protected XWorkConverter xworkConverter;
    protected NullHandler nullHandler;
    protected ObjectTypeDeterminer objectTypeDeterminer;
    protected ObjectFactory objectFactory;

    public AbstractResolver(Container container) {
        this.reflectionProvider = container.getInstance(ReflectionProvider.class);
        this.xworkConverter = container.getInstance(XWorkConverter.class);
        this.nullHandler = container.getInstance(NullHandler.class, "java.lang.Object");
        this.objectTypeDeterminer = container.getInstance(ObjectTypeDeterminer.class);
        this.objectFactory = container.getInstance(ObjectFactory.class);
    }

    public Class<?> getCommonPropertyType(ELContext elContext, Object o) {
        return null;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object o) {
        return null;
    }

    public Class<?> getType(ELContext elContext, Object o, Object o1) {
        return null;
    }

    public boolean isReadOnly(ELContext elContext, Object o, Object o1) {
        return false;
    }
}
