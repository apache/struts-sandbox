package org.apache.struts2.rest.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.opensymphony.xwork2.ActionInvocation;

public interface MimeTypeHandler {
    Object toObject(InputStream in);
    
    String fromObject(Object obj, ActionInvocation inv) throws IOException;
}
