package com.googlecode.struts2juel.elresolvers;

import javax.el.BeanELResolver;
import javax.el.ELContext;

import com.googlecode.struts2juel.CompoundRootELContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;

public class XWorkBeanELResolver extends BeanELResolver {

	public XWorkBeanELResolver() {
		super();
	}

	public XWorkBeanELResolver(boolean isReadOnly) {
		super(isReadOnly);
	}

	@Override
	public void setValue(ELContext context, Object base, Object property,
			Object value) {
		Class expectedType = getType(context, base, property);
		if (expectedType != null) {
			XWorkConverter converter = ((CompoundRootELContext) context)
					.getXworkConverter();
			value = converter.convertValue(null, value, expectedType);
		}
		super.setValue(context, base, property, value);
	}
}
