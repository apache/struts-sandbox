package org.apache.struts2;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.struts2.dispatcher.StrutsResultSupport;

/**
 * Can render jsps from the classpath. "includes" in the jsps must not use relative paths
 */
public class EmbeddedJSPResult extends StrutsResultSupport {
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        JSPRuntime.handle(StringUtils.removeStart(finalLocation, "/"));
    }
}
