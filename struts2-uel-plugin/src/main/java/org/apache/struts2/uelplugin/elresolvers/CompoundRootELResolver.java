package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.inject.Container;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.xwork.StringUtils;

import javax.el.ELContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * An ELResolver that is capable of resolving properties against the
 * CompoundRoot if available in the ELContext.
 */
public class CompoundRootELResolver extends AbstractResolver {

    public CompoundRootELResolver(Container container) {
        super(container);
    }

    @Override
    public Object getValue(ELContext elContext, Object base, Object property) {
        if (elContext == null) {
            throw new IllegalArgumentException("ElContext cannot be null");
        }

        String propertyName = property.toString();

        if (StringUtils.startsWith(propertyName, "#"))
            return null;

        // only resolve at the root of the context
        if (base != null) {
            return null;
        }

        CompoundRoot root = (CompoundRoot) elContext.getContext(CompoundRoot.class);
        if (root == null) {
            return null;
        }

        if ("top".equals(propertyName) && root.size() > 0) {
            elContext.setPropertyResolved(true);
            return root.get(0);
        }

        Map<String, Object> reflectionContext = (Map) elContext.getContext(AccessorsContextKey.class);

        Object bean = findObjectForProperty(root, propertyName);
        if (bean != null) {
            Object retVal = reflectionProvider.getValue(propertyName, reflectionContext, bean);

            reflectionContext.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, bean.getClass());
            reflectionContext.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, propertyName);

            //if object is null, and create objects is enabled, lets do it
            if (retVal == null && ReflectionContextState.isCreatingNullObjects(reflectionContext)) {
                retVal = nullHandler.nullPropertyValue(reflectionContext, bean, property);
                reflectionProvider.setValue(propertyName, reflectionContext, bean, retVal);
            }

            elContext.setPropertyResolved(true);
            return retVal;
        }

        return null;
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (context == null) {
            throw new NullPointerException();
        }
        // only resolve at the root of the context
        if (base != null) {
            return;
        }

        CompoundRoot root = (CompoundRoot) context.getContext(CompoundRoot.class);
        Map<String, Object> reflectionContext = (Map) context.getContext(AccessorsContextKey.class);
        String propertyName = (String) property;
        try {
            if (base == null && property != null && root != null) {
                Object bean = findObjectForProperty(root, propertyName);
                if (bean != null) {
                    reflectionContext.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, bean.getClass());
                    reflectionContext.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, propertyName);


                    XWorkConverter converter = (XWorkConverter) context.getContext(XWorkConverter.class);
                    if (converter != null && root != null) {
                        Class propType = determineType(bean, propertyName);
                        value = converter.convertValue(reflectionContext, bean, null, propertyName, value, propType);
                    }
                    BeanUtils.setProperty(bean, propertyName, value);
                    context.setPropertyResolved(true);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected Class<?> determineType(Object bean, String property) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtils.getPropertyType(bean, property);
    }

    protected Object findObjectForProperty(CompoundRoot root, String propertyName) {
        if ("top".equals(propertyName) && root.size() > 0) {
            return root.get(0);
        }
        for (int i = 0; i < root.size(); i++) {
            if (PropertyUtils.isReadable(root.get(i), propertyName) || PropertyUtils.isWriteable(root.get(i), propertyName)) {
                return root.get(i);
            }
        }
        return null;
    }
}
