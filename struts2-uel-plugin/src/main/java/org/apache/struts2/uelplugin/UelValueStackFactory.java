package org.apache.struts2.uelplugin;

import javax.el.ExpressionFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * Creates UelValueStacks.
 */
public class UelValueStackFactory implements ValueStackFactory {
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
    	
    public void initExpressionFactory() {
		if (factory == null) {
			factory = ExpressionFactoryHolder.getExpressionFactory();
		}
	}

	public ValueStack createValueStack() {
		initExpressionFactory();
		ValueStack results = new UelValueStack(factory, xworkConverter);
		results.getContext().put(ActionContext.CONTAINER, container);
		return results;
	}

	public ValueStack createValueStack(ValueStack stack) {
		initExpressionFactory();
		ValueStack results = new UelValueStack(factory, xworkConverter, stack);
		results.getContext().put(ActionContext.CONTAINER, container);
		return results;
	}
}
