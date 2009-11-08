package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import javax.el.ELContext;
import javax.el.ELException;
import java.lang.reflect.Array;
import java.util.Map;


public class XWorkArrayELResolver extends AbstractELResolver {
    public XWorkArrayELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object target, Object property) {
        if (target != null && property != null && target.getClass().isArray()) {

            Map<String, Object> context = (Map) elContext.getContext(XWorkValueStackContext.class);

            Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

            Integer index = null;

            if (property instanceof Number)
                index = ((Number) property).intValue();
            else {
                try {
                    index = Integer.valueOf(property.toString());
                } catch (NumberFormatException e) {
                    //ignore
                }
            }

            if (index != null) {
                if (ReflectionContextState.isCreatingNullObjects(context) && objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, true)) {
                    Class clazz = target.getClass().getComponentType();

                    if (index < 0 || index >= Array.getLength(target)) {
                        //nothing to do here, as we cannot set a new array
                        throw new ELException("Index [" + index + "] is out of bounds");
                    } else {
                        //valid index
                        Object obj = Array.get(target, index);
                        if (obj == null) {
                            try {
                                obj = objectFactory.buildBean(clazz, context);
                                Array.set(target, index, obj);
                            } catch (Exception e) {
                                throw new ELException("unable to instantiate a new object for property [" + lastProperty + "]", e);
                            }
                        }

                        elContext.setPropertyResolved(true);
                        return obj;
                    }
                } else {
                    //try normal list
                    if (index < Array.getLength(target)) {
                        elContext.setPropertyResolved(true);
                        return Array.get(target, index);
                    }
                }
            }
        }

        return null;
    }

    public void setValue(ELContext elContext, Object target, Object property, Object value) {
        if (target != null && property != null && target.getClass().isArray()) {

            Map<String, Object> context = (Map) elContext.getContext(XWorkValueStackContext.class);

            Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

            Integer index = null;

            if (property instanceof Number)
                index = ((Number) property).intValue();
            else {
                try {
                    index = Integer.valueOf(property.toString());
                } catch (NumberFormatException e) {
                    //ignore
                }
            }

            Class clazz = target.getClass().getComponentType();

            if (index != null) {
                if (ReflectionContextState.isCreatingNullObjects(context) && objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, true)) {

                    if (index < 0 || index >= Array.getLength(target)) {
                        //nothing to do here, as we cannot set a new array
                        throw new ELException("Index [" + index + "] is out of bounds");
                    } else {
                        //valid index
                        Object convertedValue = xworkConverter.convertValue(context, value, clazz);
                        Array.set(target, index, convertedValue);
                        elContext.setPropertyResolved(true);
                    }
                }
            } else {
                //try normal list
                if (index < Array.getLength(target)) {
                    Object convertedValue = xworkConverter.convertValue(context, value, clazz);
                    Array.set(target, index, convertedValue);
                    elContext.setPropertyResolved(true);
                }
            }
        }
    }
}
