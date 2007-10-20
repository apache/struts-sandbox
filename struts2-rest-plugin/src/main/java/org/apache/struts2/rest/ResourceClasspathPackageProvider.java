package org.apache.struts2.rest;

import org.apache.struts2.config.ClasspathPackageProvider;

import com.opensymphony.xwork2.util.ResolverUtil.ClassTest;

/**
 * Checks for actions ending in Resource indicating a Rest resource
 */
public class ResourceClasspathPackageProvider extends ClasspathPackageProvider {
    
    @Override
    protected ClassTest createActionClassTest() {
        return new ClassTest() {
            // Match Action implementations and classes ending with "Resource"
            public boolean matches(Class type) {
                return (type.getSimpleName().endsWith("Resource"));
            }
        };
    }
    
    @Override
    protected String getClassSuffix() {
        return "Resource";
    }

}
