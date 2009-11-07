package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

import javax.el.ExpressionFactory;

/**
 * Creates UelValueStacks.
 */
public class UELValueStackFactory implements ValueStackFactory {
    private ExpressionFactory factory;

    private XWorkConverter xworkConverter;

    private Container container;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }

    @Inject
    public void setContainer(Container container) throws ClassNotFoundException {
        this.container = container;
    }


    public ValueStack createValueStack() {
        ValueStack results = new UELValueStack(container);
        results.getContext().put(ActionContext.CONTAINER, container);
        return results;
    }

    public ValueStack createValueStack(ValueStack stack) {
        ValueStack results = new UELValueStack(container, stack);
        container.inject(results);
        results.getContext().put(ActionContext.CONTAINER, container);
        return results;
    }
}
