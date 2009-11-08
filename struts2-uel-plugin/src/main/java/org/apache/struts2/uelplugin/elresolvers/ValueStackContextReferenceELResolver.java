package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang.xwork.StringUtils;

import javax.el.ELContext;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;


public class ValueStackContextReferenceELResolver extends AbstractELResolver {
    public ValueStackContextReferenceELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object base, Object property) {
        String objectName = property.toString();
        if (StringUtils.startsWith(objectName, "#")) {
            objectName = StringUtils.removeStart(property.toString(), "#");

            ActionContext actionContext = ActionContext.getContext();
            if (elContext != null) {
                ValueStack valueStack = actionContext.getValueStack();
                if (valueStack != null) {
                    Object obj = valueStack.getContext().get(objectName);
                    if (obj != null) {
                        Map<String, Object> reflectionContext = (Map) elContext.getContext(XWorkValueStackContext.class);

                        reflectionContext.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, obj.getClass());
                        reflectionContext.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, objectName);
                        elContext.setPropertyResolved(true);
                        return obj;
                    }
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
