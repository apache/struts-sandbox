package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import javax.el.ELContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class XWorkListELResolver extends AbstractELResolver {
    public XWorkListELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object target, Object property) {
        if (target != null && property != null && target instanceof List) {

            Map<String, Object> context = (Map) elContext.getContext(XWorkValueStackContext.class);
            List list = (List) target;

            Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

            Integer numericValue = null;
            try {
                numericValue = Integer.valueOf(property.toString());
            } catch (NumberFormatException e) {
                //ignore
            }

            if (numericValue != null) {
                if (ReflectionContextState.isCreatingNullObjects(context) && objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, true)) {
                    int index = numericValue.intValue();
                    int listSize = list.size();

                    /*if (lastClass == null || lastProperty == null) {
                        return super.getProperty(context, target, name);
                    }*/
                    Class beanClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, property);
                    if (listSize <= index) {
                        Object result = null;

                        for (int i = listSize; i < index; i++) {
                            list.add(null);
                        }
                        try {
                            list.add(index, result = objectFactory.buildBean(beanClass, context));
                        } catch (Exception exc) {
                            throw new XWorkException(exc);
                        }

                        elContext.setPropertyResolved(true);
                        return result;
                    } else if (list.get(index) == null) {
                        Object result = null;
                        try {
                            list.set(index, result = objectFactory.buildBean(beanClass, context));
                        } catch (Exception exc) {
                            throw new XWorkException(exc);
                        }

                        elContext.setPropertyResolved(true);
                        return result;
                    } else {
                        elContext.setPropertyResolved(true);
                        return list.get(index);
                    }
                } else {
                    //try normal list
                    if (numericValue < list.size()) {
                        elContext.setPropertyResolved(true);
                        return list.get(numericValue);
                    }
                }
            }
        }

        return null;
    }

    public void setValue(ELContext elContext, Object target, Object property, Object value) {
        Map<String, Object> context = (Map) elContext.getContext(XWorkValueStackContext.class);
        if (target != null && property != null && target instanceof List) {
            Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
            Class convertToClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, property);

            if (property instanceof String && value.getClass().isArray()) {
                // looks like the input game in the form of "someList.foo" and
                // we are expected to define the index values ourselves.
                // So let's do it:

                Collection c = (Collection) value;
                Object[] values = (Object[]) value;
                for (Object v : values) {
                    try {
                        Object o = objectFactory.buildBean(convertToClass, context);
                        reflectionProvider.setProperty(property.toString(), value, target, context);
                        c.add(o);
                    } catch (Exception e) {
                        throw new XWorkException("Error converting given String values for Collection.", e);
                    }
                }
            }

            Object realValue = getRealValue(context, value, convertToClass);

            Long numericValue = null;
            try {
                numericValue = Long.valueOf(property.toString());
            } catch (NumberFormatException e) {
                //ignore
            }

            if (target instanceof List && numericValue != null) {
                //make sure there are enough spaces in the List to set
                List list = (List) target;
                int listSize = list.size();
                int count = numericValue.intValue();
                if (count >= listSize) {
                    for (int i = listSize; i <= count; i++) {
                        list.add(null);
                    }
                }

                ((List) target).set(numericValue.intValue(), realValue);
            }
        }
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return xworkConverter.convertValue(context, value, convertToClass);
    }
}
