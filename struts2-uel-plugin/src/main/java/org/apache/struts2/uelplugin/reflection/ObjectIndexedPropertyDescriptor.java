package org.apache.struts2.uelplugin.reflection;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;

/**
 * Taken from OGNL
 */
public class ObjectIndexedPropertyDescriptor extends PropertyDescriptor
{
    private Method indexedReadMethod;
    private Method          indexedWriteMethod;
    private Class           propertyType;

    public ObjectIndexedPropertyDescriptor(String propertyName, Class propertyType, Method indexedReadMethod, Method indexedWriteMethod) throws IntrospectionException
    {
        super(propertyName, null, null);
        this.propertyType = propertyType;
        this.indexedReadMethod = indexedReadMethod;
        this.indexedWriteMethod = indexedWriteMethod;
    }

    public Method getIndexedReadMethod()
    {
        return indexedReadMethod;
    }

    public Method getIndexedWriteMethod()
    {
        return indexedWriteMethod;
    }

    public Class getPropertyType()
    {
        return propertyType;
    }
}

