package org.apache.struts2.rest.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;

public class HtmlHandler implements MimeTypeHandler {

    public String fromObject(Object obj, ActionInvocation inv) throws IOException {
        inv.getStack().push(Collections.singletonMap("body", obj));
        return Action.SUCCESS;
    }

    public Object toObject(InputStream in) {
        return null;
    }

}
