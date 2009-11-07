package org.apache.struts2.portlet.servlet;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.portlet.ResourceResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;

/**
 * PortletServletResponseJSR286.
 *
 * @author Rene Gielen
 */
public class PortletServletResponseJSR286 extends PortletServletResponse {

    public PortletServletResponseJSR286( PortletResponse portletResponse ) {
        super(portletResponse);
    }


    public void flushBuffer() throws IOException {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).flushBuffer();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public int getBufferSize() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getBufferSize();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public String getCharacterEncoding() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getCharacterEncoding();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public String getContentType() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getContentType();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public Locale getLocale() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getLocale();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if(portletResponse instanceof MimeResponse) {
            return new PortletServletOutputStream(((MimeResponse)portletResponse).getPortletOutputStream());
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public PrintWriter getWriter() throws IOException {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).getWriter();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public boolean isCommitted() {
        if(portletResponse instanceof MimeResponse) {
            return ((MimeResponse)portletResponse).isCommitted();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void reset() {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).reset();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void resetBuffer() {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).resetBuffer();
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void setBufferSize(int size) {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).setBufferSize(size);
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void setCharacterEncoding(String charset) {
        if(portletResponse instanceof ResourceResponse) {
            ((ResourceResponse)portletResponse).setCharacterEncoding(charset);
        }
        throw new IllegalStateException("Only allowed in resource phase");
    }

    public void setContentLength(int len) {
        if(portletResponse instanceof ResourceResponse) {
            ((ResourceResponse)portletResponse).setContentLength(len);
        }
        throw new IllegalStateException("Only allowed in resource phase");
    }

    public void setContentType(String type) {
        if(portletResponse instanceof MimeResponse) {
            ((MimeResponse)portletResponse).setContentType(type);
        }
        else {
            throw new IllegalStateException("Only allowed in render or resource phase");
        }
    }

    public void setLocale(Locale loc) {
        if(portletResponse instanceof ResourceResponse) {
            ((ResourceResponse)portletResponse).setLocale(loc);
        }
        throw new IllegalStateException("Only allowed in resource phase");
    }

}
