package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import javax.el.ELContext;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.Iterator;


public abstract class AbstractELResolver extends ELResolver {
    protected final ReflectionProvider reflectionProvider;
    protected final XWorkConverter xworkConverter;
    protected final NullHandler nullHandler;
    protected final ObjectTypeDeterminer objectTypeDeterminer;
    protected final ObjectFactory objectFactory;

    public AbstractELResolver(Container container) {
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
