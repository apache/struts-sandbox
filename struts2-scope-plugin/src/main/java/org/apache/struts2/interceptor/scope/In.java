package org.apache.struts2.interceptor.scope;

public @interface In {
    /**
     * Specifies that a component should be instantiated if the context variable is null.
     */
    boolean create() default false;
    
    /**
     * Specifies that the injected value must not be null, by default.
     */
    boolean required() default true;
    
    /**
     * Explicitly specify the scope to search, instead of searching all scopes.
     */
    ScopeType scope() default ScopeType.UNSPECIFIED;
    
    /**
     * The context variable name. Defaults to the name of the annotated field or getter method.
     */
    String value() default "";
}
