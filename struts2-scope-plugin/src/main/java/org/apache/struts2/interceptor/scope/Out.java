package org.apache.struts2.interceptor.scope;

public @interface Out {
    
    /**
     * Specifies that the injected value must not be null, by default.
     */
    boolean required() default true;
    
    /**
     * Explicitly specify the scope to search, instead of searching all scopes.
     */
    ScopeType scope() default ScopeType.ACTION_CONTEXT;
    
    /**
     * The context variable name. Defaults to the name of the annotated field or getter method.
     */
    String value() default "";
}
