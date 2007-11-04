package com.googlecode.struts2juel;

import javax.el.VariableMapper;

import com.opensymphony.xwork2.util.CompoundRoot;

import de.odysseus.el.util.SimpleContext;

/**
 * An implementation of SimpleContext that knows about the CompoundRoot.
 */
public class CompoundRootELContext extends SimpleContext {
    private VariableMapper variableMapper;

    public CompoundRootELContext(CompoundRoot root) {
        variableMapper = new CompoundRootVariableMapper(root);
    }

    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }
}
