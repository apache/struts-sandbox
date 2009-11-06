package org.apache.struts2.uelplugin.reflection;

public interface ClassCache {

    void setClassInspector(ClassCacheInspector inspector);

    void clear();

    int getSize();

    Object get(Class key);

    Object put(Class key, Object value);
}
