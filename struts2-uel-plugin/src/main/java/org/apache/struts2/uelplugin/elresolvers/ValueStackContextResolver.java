package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

import javax.el.ELResolver;
import javax.el.ELContext;
import java.util.Iterator;
import java.beans.FeatureDescriptor;


public class ValueStackContextResolver extends ELResolver {
    public Object getValue(ELContext context, Object base, Object property) {
        String objectName = property.toString();

        ActionContext actionContext = ActionContext.getContext();
        if (context != null) {
            ValueStack valueStack = actionContext.getValueStack();
            if (valueStack != null) {
                Object obj = valueStack.getContext().get(objectName);
                if (obj != null) {
                    context.setPropertyResolved(true);
                    return obj;
                }
            }
        }

        return null;
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
        return true;
    }

    public void setValue(ELContext elContext, Object o, Object o1, Object o2) {
    }
}
