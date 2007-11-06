package com.googlecode.struts2juel;

import java.lang.reflect.Method;

import javax.el.FunctionMapper;

public class NullFunctionMapper extends FunctionMapper {

	@Override
	public Method resolveFunction(String arg0, String arg1) {
		return null;
	}

}
