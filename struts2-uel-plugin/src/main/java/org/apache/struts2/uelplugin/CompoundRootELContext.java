package org.apache.struts2.uelplugin;

import de.odysseus.el.util.SimpleContext;
import org.apache.struts2.uelplugin.elresolvers.CompoundRootELResolver;
import org.apache.struts2.uelplugin.elresolvers.XWorkBeanELResolver;
import org.apache.struts2.uelplugin.elresolvers.ValueStackContextResolver;

import javax.el.*;


/**
 * An implementation of SimpleContext that knows about the ValueStack's
 * CompoundRoot.
 */
public class CompoundRootELContext extends SimpleContext {
    private final static BuiltinFunctionMapper BUILTIN_FUNCTION_MAPPER = new BuiltinFunctionMapper();
    public CompoundRootELContext() {
        super(new CompositeELResolver() {
            {
                add(new CompoundRootELResolver());
                add(new ValueStackContextResolver());
                add(new ArrayELResolver(false));
                add(new ListELResolver(false));
                add(new MapELResolver(false));
                add(new ResourceBundleELResolver());
                add(new XWorkBeanELResolver());
            }});
    }

    @Override
    public VariableMapper getVariableMapper() {
        return null;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return BUILTIN_FUNCTION_MAPPER;
    }
}
