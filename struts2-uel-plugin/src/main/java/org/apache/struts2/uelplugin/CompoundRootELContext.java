package org.apache.struts2.uelplugin;

import javax.el.ArrayELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.VariableMapper;

import org.apache.struts2.uelplugin.elresolvers.CompoundRootELResolver;
import org.apache.struts2.uelplugin.elresolvers.XWorkBeanELResolver;


/**
 * An implementation of SimpleContext that knows about the ValueStack's
 * CompoundRoot.
 */
public class CompoundRootELContext extends ELContext {
	private ELResolver DEFAULT_RESOLVER_READ_WRITE;

	public CompoundRootELContext() {
		DEFAULT_RESOLVER_READ_WRITE = new CompositeELResolver() {
			{
				add(new CompoundRootELResolver());
				add(new ArrayELResolver(false));
				add(new ListELResolver(false));
				add(new MapELResolver(false));
				add(new ResourceBundleELResolver());
				add(new XWorkBeanELResolver());
			}
		};
	}

	@Override
	public VariableMapper getVariableMapper() {
		return null;
	}

	@Override
	public ELResolver getELResolver() {
		return DEFAULT_RESOLVER_READ_WRITE;
	}

	@Override
	public FunctionMapper getFunctionMapper() {
		return null;
	}
}
