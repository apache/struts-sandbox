package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import org.apache.commons.beanutils.PropertyUtils;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import java.lang.reflect.InvocationTargetException;

public class XWorkBeanELResolver extends BeanELResolver {

    public XWorkBeanELResolver() {
        super(false);
    }

    /**
     * Re-implement this to always return Object. We don't want unified EL to do
     * type conversion, we do that in the setter using xwork type conversion
     * framework.
     */
    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null || property == null) {
            return null;
        }

        context.setPropertyResolved(true);
        return Object.class;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        XWorkConverter converter = (XWorkConverter) context.getContext(XWorkConverter.class);
        try {
            if (converter != null && base != null) {
                Class propType = PropertyUtils.getPropertyType(base, property.toString());
                value = converter.convertValue(null, value, propType);
            }
            super.setValue(context, base, property, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
