package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.inject.Container;
import de.odysseus.el.util.SimpleContext;
import org.apache.struts2.uelplugin.elresolvers.CompoundRootELResolver;
import org.apache.struts2.uelplugin.elresolvers.ValueStackContextResolver;
import org.apache.struts2.uelplugin.elresolvers.XWorkBeanELResolver;
import org.apache.struts2.uelplugin.elresolvers.XWorkListELResolver;

import javax.el.*;


/**
 * An implementation of SimpleContext that knows about the ValueStack's
 * CompoundRoot.
 */
public class CompoundRootELContext extends SimpleContext {
    private final static BuiltinFunctionMapper BUILTIN_FUNCTION_MAPPER = new BuiltinFunctionMapper();
    public CompoundRootELContext(final Container container) {
        super(new CompositeELResolver() {
            {
                add(new ValueStackContextResolver());
                add(new XWorkListELResolver(container));
                add(new CompoundRootELResolver(container));
                add(new ArrayELResolver(false));
                add(new MapELResolver(false));
                add(new ResourceBundleELResolver());
                add(new XWorkBeanELResolver(container));
                add(new BeanELResolver());
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
