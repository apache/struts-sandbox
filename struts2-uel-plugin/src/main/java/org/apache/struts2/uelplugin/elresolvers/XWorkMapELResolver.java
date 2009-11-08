package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import javax.el.ELContext;
import java.util.Map;
import java.util.WeakHashMap;

public class XWorkMapELResolver extends AbstractELResolver {
    private final Map<Key, Class> keyClassCache = new WeakHashMap<Key, Class>();

    public XWorkMapELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object target, Object property) {
        Map<String, Object> context = (Map) elContext.getContext(XWorkValueStackContext.class);

        if (target != null && property != null && target instanceof Map) {
            Object result = null;

            //find the key class and convert the name to that class
            Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

            Class keyClass = objectTypeDeterminer.getKeyClass(lastClass, lastProperty);

            if (keyClass == null)
                keyClass = String.class;

            Object key = getKey(context, property);
            Map map = (Map) target;
            result = map.get(key);

            if (result == null &&
                    context.get(ReflectionContextState.CREATE_NULL_OBJECTS) != null
                    && objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, false)) {
                Class valueClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, key);

                try {
                    result = objectFactory.buildBean(valueClass, context);
                    map.put(key, result);
                } catch (Exception exc) {
                }

            }

            elContext.setPropertyResolved(true);
            return result;
        }
        return null;
    }

    private Object getKey(Map context, Object name) {
        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        if (lastClass == null || lastProperty == null) {
            // return java.lang.String.class;
            // commented out the above -- it makes absolutely no sense for when setting basic maps!
            return name;
        }

        Key key = new Key(lastClass, lastProperty);
        //lookup in the cache first
        Class keyClass = keyClassCache.get(key);

        if (keyClass == null) {
            keyClass = objectTypeDeterminer.getKeyClass(lastClass, lastProperty);

            if (keyClass != null)
                keyClassCache.put(key, keyClass);
        }

        if (keyClass == null) {
            keyClass = String.class;
            keyClassCache.put(key, String.class);
        }

        return xworkConverter.convertValue(context, name, keyClass);

    }

    public void setValue(ELContext elContext, Object target, Object property, Object value) {
        Map<String, Object> context = (Map) elContext.getContext(XWorkValueStackContext.class);

        if (target != null && property != null && target instanceof Map) {
            Object key = getKey(context, property);
            Map map = (Map) target;
            map.put(key, value);
            elContext.setPropertyResolved(true);
        }
    }

    private Object getValue(Map context, Object value) {
        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        if (lastClass == null || lastProperty == null) {
            return value;
        }
        Class elementClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, null);
        if (elementClass == null) {
            return value; // nothing is specified, we assume it will be the value passed in.
        }
        return xworkConverter.convertValue(context, value, elementClass);
    }
}

class Key {
    Class clazz;
    String property;

    Key(Class clazz, String property) {
        this.clazz = clazz;
        this.property = property;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key = (Key) o;

        if (clazz != null ? !clazz.equals(key.clazz) : key.clazz != null) return false;
        if (property != null ? !property.equals(key.property) : key.property != null) return false;

        return true;
    }

    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        return result;
    }
}

