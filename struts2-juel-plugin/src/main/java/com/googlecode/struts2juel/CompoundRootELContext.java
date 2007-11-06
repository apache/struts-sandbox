package com.googlecode.struts2juel;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.VariableMapper;

import com.opensymphony.xwork2.util.CompoundRoot;

/**
 * An implementation of SimpleContext that knows about the ValueStack's CompoundRoot.
 */
public class CompoundRootELContext extends ELContext {
    private VariableMapper variableMapper;
    private FunctionMapper functionMapper = new NullFunctionMapper();

    private static final ELResolver DEFAULT_RESOLVER_READ_ONLY = new CompositeELResolver() {
		{
			add(new ArrayELResolver(false));
			add(new ListELResolver(false));
			add(new MapELResolver(false));
			add(new ResourceBundleELResolver());
			add(new BeanELResolver(false));
		}
	};
    
    public CompoundRootELContext(CompoundRoot root) {
        variableMapper = new CompoundRootVariableMapper(root);
    }

    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }

	@Override
	public ELResolver getELResolver() {
		return DEFAULT_RESOLVER_READ_ONLY;
	}

	@Override
	public FunctionMapper getFunctionMapper() {
		return functionMapper;
	}
}
