package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ReflectionContextFactory for Unified EL.
 */
public class UELReflectionContextFactory implements ReflectionContextFactory {
    public Map createDefaultContext(Object root) {
        return new HashMap();
    }
}
