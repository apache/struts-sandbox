package org.apache.struts2;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.struts.EmbeddedJSPResult;
import org.apache.struts.JSPLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import com.opensymphony.xwork2.ActionContext;

import java.util.HashMap;

public class EmbeddedJSPResultTest extends TestCase {


    public void testSimple() throws Exception {
        //mock objects
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext context = new MockServletContext();

        ActionContext actionContext = new ActionContext(new HashMap<String, Object>());
        ActionContext.setContext(actionContext);
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ServletActionContext.setServletContext(context);

        EmbeddedJSPResult result = new EmbeddedJSPResult();

        result.setLocation("org/apache/struts2/simple0.jsp");
        result.execute(null);

        assertEquals("hello", response.getContentAsString());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (JSPLoader.JSP_DIR.exists())
            FileUtils.forceDelete(JSPLoader.JSP_DIR);
    }
}
