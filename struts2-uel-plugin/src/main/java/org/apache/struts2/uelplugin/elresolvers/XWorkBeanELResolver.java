package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import javax.el.ELContext;
import javax.el.ELException;
import java.beans.IntrospectionException;
import java.util.Map;

/**
 * Sets and gets property values from objects using the ReflectionProvider
 */
public class XWorkBeanELResolver extends AbstractELResolver {
    private static final Logger LOG = LoggerFactory.getLogger(XWorkBeanELResolver.class);

    public XWorkBeanELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object target, Object property) {
        if (target != null && property != null) {
            Map<String, Object> valueStackContext = getValueStackContext(elContext);
            String propertyName = property.toString();
            Class targetType = target.getClass();

            //only handle this if there is such a property
            try {
                if (reflectionProvider.getGetMethod(targetType, propertyName) != null) {
                    try {
                        Object obj = reflectionProvider.getValue(propertyName, valueStackContext, target);

                        valueStackContext.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, targetType);
                        valueStackContext.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, propertyName);

                        //if object is null, and create objects is enabled, lets do it
                        if (obj == null && ReflectionContextState.isCreatingNullObjects(valueStackContext)) {
                            obj = nullHandler.nullPropertyValue(valueStackContext, target, property);
                            reflectionProvider.setValue(propertyName, valueStackContext, target, obj);
                        }

                        elContext.setPropertyResolved(true);
                        return obj;
                    } catch (Exception e) {
                        throw new ELException(e);
                    }
                }
            } catch (IntrospectionException e) {
                //ok move on
                LOG.info("There was an error while reading [" + propertyName + "]", e);
            }
        }

        return null;
    }

    public void setValue(ELContext elContext, Object target, Object property, Object value) {
        if (target != null && property != null) {
            try {
                Map<String, Object> reflectionContext = getValueStackContext(elContext);
                String propertyName = property.toString();
                Class targetType = target.getClass();

                //only handle this if there is such a property
                if (reflectionProvider.getSetMethod(targetType, propertyName) != null) {

                    Class expectedType = reflectionProvider.getPropertyDescriptor(targetType, propertyName).getWriteMethod().getParameterTypes()[0];
                    Class valueType = value.getClass();

                    //convert value, if needed
                    if (!expectedType.isAssignableFrom(valueType)) {
                        value = xworkConverter.convertValue(reflectionContext, value, expectedType);
                    }
                    reflectionProvider.setValue(propertyName, reflectionContext, target, value);
                    elContext.setPropertyResolved(true);
                }
            } catch (Exception e) {
                throw new ELException(e);
            }
        }
    }
}
