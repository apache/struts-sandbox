package com.googlecode.struts2juel.elresolvers;

import javax.el.BeanELResolver;
import javax.el.ELContext;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;

public class XWorkBeanELResolver extends BeanELResolver {
	public XWorkBeanELResolver() {
	}

	public XWorkBeanELResolver(boolean isReadOnly) {
		super(isReadOnly);
	}

	@Override
	public void setValue(ELContext context, Object base, Object property,
			Object value) {
		XWorkConverter converter = (XWorkConverter) context
				.getContext(XWorkConverter.class);
		if (converter != null && base != null) {
			Class propType = getType(context, base, property);
			value = converter.convertValue(value, propType);
		}
		super.setValue(context, base, property, value);
	}
}
