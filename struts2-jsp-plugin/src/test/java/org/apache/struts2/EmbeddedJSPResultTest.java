package org.apache.struts2;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.EmbeddedJSPResult;
import org.apache.struts2.JSPLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import com.opensymphony.xwork2.ActionContext;

import java.util.HashMap;

public class EmbeddedJSPResultTest extends TestCase {
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockServletContext context;

    public void testSimple() throws Exception {
        //mock objects
        EmbeddedJSPResult result = new EmbeddedJSPResult();

        result.setLocation("org/apache/struts2/simple0.jsp");
        result.execute(null);

        assertEquals("hello", response.getContentAsString());
    }

     public void testInclude() throws Exception {
        //mock objects
        EmbeddedJSPResult result = new EmbeddedJSPResult();

        result.setLocation("org/apache/struts2/includes0.jsp");
        result.execute(null);

        assertEquals("Test", response.getContentAsString());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        context = new MockServletContext();

        ActionContext actionContext = new ActionContext(new HashMap<String, Object>());
        ActionContext.setContext(actionContext);
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ServletActionContext.setServletContext(context);
        
        if (JSPLoader.JSP_DIR.exists())
            FileUtils.forceDelete(JSPLoader.JSP_DIR);
    }
}
