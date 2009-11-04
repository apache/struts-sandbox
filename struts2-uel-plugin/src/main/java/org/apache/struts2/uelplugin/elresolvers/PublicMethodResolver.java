package org.apache.struts2.uelplugin.elresolvers;

import de.odysseus.el.misc.MethodInvocation;

import javax.el.ELResolver;
import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import java.lang.reflect.Method;
import java.beans.FeatureDescriptor;
import java.util.Iterator;

public class PublicMethodResolver extends ELResolver {
    private boolean match(MethodInvocation call, Method method) {
        if (method.getName().equals(call.getName()) && method.getReturnType() != void.class) {
            if (call.getParamCount() == method.getParameterTypes().length) {
                return true;
            }
            if (method.isVarArgs() && call.isVarArgs()) {
                if (call.getParamCount() >= method.getParameterTypes().length - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Method getValue(ELContext context, Object base, Object prop) {
        if (base != null && prop instanceof MethodInvocation) {
            MethodInvocation call = (MethodInvocation) prop;
            for (Method method : base.getClass().getMethods()) {
                if (match(call, method)) {
                    context.setPropertyResolved(true);
                    return method;
                }
            }
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        throw new PropertyNotWritableException();
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return MethodInvocation.class;
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }
}

