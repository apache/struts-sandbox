package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.inject.Container;
import de.odysseus.el.util.SimpleContext;
import org.apache.struts2.uelplugin.elresolvers.*;

import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;


/**
 * An implementation of SimpleContext that knows about the ValueStack's
 * CompoundRoot.
 */
public class CompoundRootELContext extends SimpleContext {
    private final static BuiltinFunctionMapper BUILTIN_FUNCTION_MAPPER = new BuiltinFunctionMapper();

    public CompoundRootELContext(final Container container) {
        super(new CompositeELResolver() {
            {
                add(new MethodInvocationGuardELResolver(container));
                add(new CompoundRootELResolver(container));
                add(new ValueStackContextReferenceELResolver(container));
                add(new XWorkBeanELResolver(container));
                add(new XWorkListELResolver(container));
                add(new XWorkMapELResolver(container));
                add(new XWorkArrayELResolver(container));
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
