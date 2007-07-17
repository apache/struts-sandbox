package org.apache.struts2.rest.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.thoughtworks.xstream.XStream;

public class XStreamHandler implements MimeTypeHandler {

    public String fromObject(Object obj, ActionInvocation inv) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        XStream xstream = createXStream();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        xstream.toXML(obj, bout);
        response.getOutputStream().write(bout.toByteArray());
        response.setContentLength(bout.size());
        response.setContentType(getContentType());
        return null;
    }

    public Object toObject(InputStream in) {
        XStream xstream = createXStream();
        return xstream.fromXML(in);
    }
    
    protected XStream createXStream() {
        return new XStream();
    }

    public String getContentType() {
        return "text/xml";
    }

}
