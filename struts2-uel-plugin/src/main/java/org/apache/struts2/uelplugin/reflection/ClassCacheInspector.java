package org.apache.struts2.uelplugin.reflection;

/**
 * Taken from OGNL
 */
public interface ClassCacheInspector {

    /**
     * Invoked just before storing a class type within a cache instance.
     *
     * @param type The class that is to be stored.
     * @return True if the class can be cached, false otherwise.
     */
    boolean shouldCache(Class type);
}
