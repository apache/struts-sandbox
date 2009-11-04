package org.apache.struts2.uelplugin;

import javax.el.*;

import org.apache.struts2.uelplugin.elresolvers.CompoundRootELResolver;
import org.apache.struts2.uelplugin.elresolvers.XWorkBeanELResolver;
import org.apache.struts2.uelplugin.elresolvers.PublicMethodResolver;
import de.odysseus.el.util.SimpleContext;


/**
 * An implementation of SimpleContext that knows about the ValueStack's
 * CompoundRoot.
 */
public class CompoundRootELContext extends SimpleContext {
    public CompoundRootELContext() {
        super(new CompositeELResolver() {
            {
                add(new BeanELResolver());
                add(new PublicMethodResolver());
                add(new CompoundRootELResolver());
                add(new ArrayELResolver(false));
                add(new ListELResolver(false));
                add(new MapELResolver(false));
                add(new ResourceBundleELResolver());
                add(new XWorkBeanELResolver());
            }
        });
    }

    @Override
    public VariableMapper getVariableMapper() {
        return null;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return null;
    }
}
