package com.googlecode.struts2juel;

import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.apache.commons.beanutils.PropertyUtils;

import com.opensymphony.xwork2.util.CompoundRoot;

/**
 * Implementation of VariableMapper based on a CompoundRoot.
 */
public class CompoundRootVariableMapper extends VariableMapper {
    private CompoundRoot root;
    
    public CompoundRootVariableMapper(CompoundRoot root) {
        this.root = root;
    }

    @Override
    public ValueExpression resolveVariable(String variable) {
        if ("top".equals(variable) && root.size() > 0) {
            return new PropertyValueExpression(root.get(0), variable);
        }
        for (int i = 0; i < root.size(); i++) {
            if (PropertyUtils.isReadable(root.get(i), variable) ||
                PropertyUtils.isWriteable(root.get(i), variable)) {
                return new PropertyValueExpression(root.get(i), variable);
            }
        }
        return null;
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression valueExpression) {
        throw new RuntimeException("Method not implemented!");
    }

}
