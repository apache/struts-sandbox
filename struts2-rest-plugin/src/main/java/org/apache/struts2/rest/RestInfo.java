package org.apache.struts2.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RestInfo {

    String apply(HttpServletRequest request,
            HttpServletResponse response, Object target);
}