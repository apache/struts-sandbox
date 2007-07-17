package org.apache.struts2.rest.handler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class XStreamJsonHandler extends XStreamHandler {

    @Override
    protected XStream createXStream() {
        return new XStream(new JettisonMappedXmlDriver());
    }

    @Override
    public String getContentType() {
        return "text/javascript";
    }
    
    
}
