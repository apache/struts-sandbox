package org.apache.struts2.uelplugin.elresolvers;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import javax.el.ELContext;
import javax.el.ELException;
import java.util.Map;

/**
 * Will throw an exception if invoke is called and method invocation is not allowed
 */
public class MethodInvocationGuardELResolver extends AbstractELResolver {
    public MethodInvocationGuardELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    @Override
    public Object invoke(ELContext elContext, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        Map<String, Object> context = (Map) elContext.getContext(XWorkValueStackContext.class);
        if (ReflectionContextState.isDenyMethodExecution(context)) {
            //you aint invoking this
            throw new ELException("Method ivocations are disabled");
        } else
            return null;
    }
}
