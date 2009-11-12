package org.apache.struts2.uel.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.commons.lang.xwork.StringUtils;

import javax.el.ELContext;
import javax.el.ELException;
import java.util.Map;

/**
 * An ELResolver that is capable of resolving properties against the
 * CompoundRoot if available in the ELContext.
 */
public class CompoundRootELResolver extends AbstractELResolver {

    public CompoundRootELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object base, Object property) {
        //EL doesn't know of value stack, so when an expression like "A.B" is evaluated
        //this method will be called with a null target and an "A" property
        String propertyName = property.toString();

        if (base == null && !StringUtils.startsWith(propertyName, "#")) {
            CompoundRoot root = (CompoundRoot) elContext.getContext(CompoundRoot.class);
            if (root == null) {
                return null;
            }

            if ("top".equals(propertyName) && root.size() > 0) {
                elContext.setPropertyResolved(true);
                return root.get(0);
            }

            Map<String, Object> valueStackContext = getValueStackContext(elContext);

            Object bean = findObjectForProperty(valueStackContext, root, propertyName);
            if (bean != null) {
                Object retVal = reflectionProvider.getValue(propertyName, valueStackContext, bean);

                valueStackContext.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, bean.getClass());
                valueStackContext.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, propertyName);

                //if object is null, and create objects is enabled, lets do it
                if (retVal == null && ReflectionContextState.isCreatingNullObjects(valueStackContext)) {
                    retVal = nullHandler.nullPropertyValue(valueStackContext, bean, property);
                    reflectionProvider.setValue(propertyName, valueStackContext, bean, retVal);
                }

                elContext.setPropertyResolved(true);
                return retVal;
            }
        }

        return null;
    }


    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }


    public void setValue(ELContext elContext, Object base, Object property, Object value) {
        //EL doesn't know of value stack, so when an expression like "A.B" is evaluated
        //this method will be called with a null target and an "A" property
        if (base == null) {
            CompoundRoot root = (CompoundRoot) elContext.getContext(CompoundRoot.class);
            Map<String, Object> valueStackContext = getValueStackContext(elContext);
            String propertyName = (String) property;
            try {
                if (property != null && root != null) {
                    Object bean = findObjectForProperty(valueStackContext, root, propertyName);
                    if (bean != null) {
                        valueStackContext.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, bean.getClass());
                        valueStackContext.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, propertyName);


                        XWorkConverter converter = (XWorkConverter) elContext.getContext(XWorkConverter.class);
                        if (converter != null) {
                            Class propType = reflectionProvider.getPropertyDescriptor(bean.getClass(), propertyName).getWriteMethod().getParameterTypes()[0];
                            value = converter.convertValue(valueStackContext, bean, null, propertyName, value, propType);
                        }
                        reflectionProvider.setValue(propertyName, valueStackContext, bean, value);
                        elContext.setPropertyResolved(true);
                    }
                }
            } catch (Exception e) {
                throw new ELException(e);
            }
        }
    }

    protected Object findObjectForProperty(Map<String, Object> reflectionContext, CompoundRoot root, String propertyName) {
        if ("top".equals(propertyName) && root.size() > 0) {
            return root.get(0);
        }

        return reflectionProvider.getRealTarget(propertyName, reflectionContext, root);
    }
}
