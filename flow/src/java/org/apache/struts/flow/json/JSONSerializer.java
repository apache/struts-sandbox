package org.apache.struts.flow.json;

// java util imports:
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.*;

import java.text.ParseException;
import org.apache.commons.logging.*;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 *  Serializes objects into JSON
 */
public class JSONSerializer {

    private static final Hashtable propDescs = new Hashtable();
    
    private Properties varLookup = new Properties();
    private final static Log log = LogFactory.getLog(JSONSerializer.class);

    public void setVariableLookupTable(Properties props) {
       this.varLookup = props;
       Map.Entry entry;
       Map map = new HashMap();
       for (Iterator i = props.entrySet().iterator(); i.hasNext(); ) {
           entry = (Map.Entry)i.next();
           map.put(entry.getValue(), entry.getKey());
       }
       props.putAll(map);
    }
    
    public String serialize(Object  bean) {
        Object o = serialize(bean, null, null, new ArrayList());
        if (o != null) {
            return o.toString();
        } 
        return null;
    }
    
    /**
     *  Recursive function to serialize objects to JSON objects that will
     *  handle serialization into JSON. Currently it will
     *  serialize Collections, Maps, Arrays, and javabeans. It maintains a 
     *  stack of objects serialized already in the
     *  current functioncall. This is used to avoid looping (stack overflow) of
     *  circular linked objects.
     *
     *@param  bean              The object you want serialized.
     *@param  name              The name of the object, used for element
     *      &lt;name/&gt;
     *@param  handler           XMLConsumer
     *@param  stack             Vector of objects we're serializing since the
     *      first calling of this function (to prevent looping on circular
     *      references).
     *@exception  SAXException  If something goes wrong
     */
     private Object serialize(Object bean, String name, Object parent, List stack) { 
        // Check stack for this object
        //if ((bean != null) && (stack.contains(bean))) {
            //if (log.isInfoEnabled()) {
            //    log.info("Circular reference detected, not serializing object: " + name);
            //}
        //    return null;
        //}
        //else 
        if (bean != null) {
            // Push object onto stack.
            // Don't push null objects ( handled below)
            stack.add(bean);
        } else {
            // bean is null
            return null;
        }    
        String clsName = bean.getClass().getName();
        Object jsonobject = null;

        // It depends on the object and it's value what todo next:
        if (bean instanceof Map) {
            
            Map map = (Map) bean;
            JSONObject object = createObject(parent, name);

            // Loop through keys and call ourselves
            for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
                Object key = i.next();
                Object Objvalue = map.get(key);

                serialize(Objvalue, key.toString(), object, stack);
            }
            jsonobject = object;
        } else if (bean instanceof Collection) {
            Collection col = (Collection) bean;

            JSONArray array = createArray(parent, name);
            // Iterate through components, and call ourselves to process elements
            for (Iterator i = col.iterator(); i.hasNext(); ) {
                serialize(i.next(), null, array, stack);
            }
            jsonobject = array;
        } else if (bean.getClass().isArray()) {
            JSONArray array = createArray(parent, name);
            // It's an array, loop through it and keep calling ourselves
            for (int i = 0; i < Array.getLength(bean); i++) {
                serialize(Array.get(bean, i), null, array, stack);
            }
            jsonobject = array;
        }
        else if (clsName.startsWith("java.lang")) {
            String val = bean.toString();
            if (parent != null) {
                if (name == null) {
                    ((JSONArray)parent).put(val);
                } else {
                    name = varLookup.getProperty(name, name);
                    ((JSONObject)parent).put(name, val);
                }
            }
            jsonobject = val;
        } else {
            // Not java.lang, so we can call ourselves with this object's values
            try {
                PropertyDescriptor[] props = (PropertyDescriptor[])propDescs.get(bean.getClass());
                if (props == null) {
                    BeanInfo info = Introspector.getBeanInfo(bean.getClass());
                    props = info.getPropertyDescriptors();
                    propDescs.put(bean.getClass(), props);
                }
                JSONObject object = createObject(parent, name);
                

                for (int i = 0; i < props.length; i++) {
                    Class t = props[i].getPropertyType();
                    String n = props[i].getName();
                    Method m = props[i].getReadMethod();
                    
                    //System.out.println("read method:"+m);
                    
                    // Call ourselves with the result of the method invocation
                    if (m != null && !m.getName().equals("getClass")) {
                        serialize(m.invoke(bean, null), n, object, stack);
                    }
                }
                jsonobject = object;
            }
            catch (Exception e) {
                //log.error(e, e);
                e.printStackTrace();
                //throw new SAXException(e.getMessage());
            }
        }

        // Remove object from stack
        stack.remove(bean);
        return jsonobject;
    }
    
    private JSONArray createArray(Object parent, String name) {
        JSONArray array = new JSONArray();
        if (parent != null) {
            if (parent instanceof JSONArray) {
                ((JSONArray)parent).put(array);
            } else {
                name = varLookup.getProperty(name, name);
                ((JSONObject)parent).put(name, array);
            }
        } 
        return array;
    }
     
    private JSONObject createObject(Object parent, String name) {
        JSONObject array = new JSONObject();
        if (parent != null) {
            if (parent instanceof JSONArray) {
                ((JSONArray)parent).put(array);
            } else {
                name = varLookup.getProperty(name, name);
                ((JSONObject)parent).put(name, array);
            }
        }
        return array;
    }
    
}

