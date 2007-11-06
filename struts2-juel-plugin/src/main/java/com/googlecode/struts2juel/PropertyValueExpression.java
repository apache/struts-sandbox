package com.googlecode.struts2juel;

import java.lang.reflect.InvocationTargetException;

import javax.el.ELContext;
import javax.el.ValueExpression;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * A value expression that uses a javabean as the root of the value expression.
 */
public class PropertyValueExpression extends ValueExpression {
	private Object object;
	private String property;

	public PropertyValueExpression(Object object, String property) {
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
	public Class<?> getType(ELContext arg0) {
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
	public Object getValue(ELContext arg0) {
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
	public boolean isReadOnly(ELContext arg0) {
		return !PropertyUtils.isWriteable(object, property);
	}

	@Override
	public void setValue(ELContext arg0, Object obj) {
		try {
			PropertyUtils.setSimpleProperty(object, property, obj);
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
