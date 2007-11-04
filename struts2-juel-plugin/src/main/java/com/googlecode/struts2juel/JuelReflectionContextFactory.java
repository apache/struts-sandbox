package com.googlecode.struts2juel;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;

public class JuelReflectionContextFactory implements ReflectionContextFactory {

    public Map createDefaultContext(Object root) {
        return new HashMap();
    }
}
