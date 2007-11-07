package com.googlecode.struts2juel;

import java.lang.reflect.InvocationTargetException;

import javax.el.ELContext;
import javax.el.ValueExpression;

import org.apache.commons.beanutils.PropertyUtils;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;

/**
 * A value expression that uses a javabean as the root of the value expression.
 */
public class PropertyValueExpression extends ValueExpression {
	private Object object;
	private String property;
    private XWorkConverter xworkConverter;

	public PropertyValueExpression(Object object, String property) {
    	this.xworkConverter = xworkConverter;
		this.object = object;
		this.property = property;
	}

	@Override
	public Class<?> getExpectedType() {
		try {
			return PropertyUtils.getPropertyType(object, property);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> getType(ELContext context) {
		try {
			return PropertyUtils.getPropertyType(object, property);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object getValue(ELContext context) {
		try {
			return PropertyUtils.getSimpleProperty(object, property);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isReadOnly(ELContext context) {
		return !PropertyUtils.isWriteable(object, property);
	}

	@Override
	public void setValue(ELContext context, Object value) {
		try {
			Class propType = PropertyUtils.getPropertyType(object, property);
			XWorkConverter xworkConverter = ((CompoundRootELContext) context).getXworkConverter();
			Object convertedValue = xworkConverter.convertValue(value, propType);
			PropertyUtils.setSimpleProperty(object, property, convertedValue);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean equals(Object otherObject) {
		if (otherObject != null && otherObject.getClass() == getClass()) {
			PropertyValueExpression other = (PropertyValueExpression) otherObject;
			if (property != other.property) {
				return false;
			}
			return object == other.object || object != null
					&& object.equals(other.object);
		}
		return false;
	}

	@Override
	public String getExpressionString() {
		return property;
	}

	@Override
	public int hashCode() {
		return property.hashCode();
	}

	@Override
	public boolean isLiteralText() {
		return false;
	}
}
