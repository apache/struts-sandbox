package org.apache.struts2.fileupload;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Describe your class here
 *
 * @author Your Name
 *         <p/>
 *         $Id$
 */
public class UploadStatusActionTest implements StrutsStatics {

    ActionContext actionContext;

    private MockServletContext servletContext;
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     *
     */
    @Before
    public void setUp() {
        // the basic progress listener keys off of the session id
        // and the file item id. To find the session id, it will
        // try to get a session from the servletActionContext
        // so, we setup a mock context to let it work. I'm not sure
        // if all of the following is necessary, but it appears to
        // work and ids are properly generating as of $Date: 2009-06-04 11:46:32 -0400 (Thu, 04 Jun 2009) $

        Map extraContext = new HashMap();

        servletContext = new MockServletContext();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        extraContext.put(HTTP_REQUEST, request);
        extraContext.put(HTTP_RESPONSE, response);
        extraContext.put(SERVLET_CONTEXT, servletContext);

        actionContext = new ActionContext(extraContext);
        ServletActionContext.setContext(actionContext);
    }

    /**
     *
     */
    @Test
    public void testUploadStatusActionGetUploadStatus() {
        BasicProgressListener listener = new BasicProgressListener();
        UploadStatusTracker tracker = new UploadStatusHolder();
        listener.setTracker(tracker);
        listener.setUpdateFrequency("10");
        listener.update(10L, 10L, 1);
        listener.update(100L, 100L, 2);

        UploadStatusAction action = new UploadStatusAction();
        List<UploadStatus> statuses = action.getUploadStatus();

        for (UploadStatus status : statuses) {
            if (status.getItemId() == 1) {
                assertTrue(status.getBytesRead() == 10L);
                assertTrue(status.getContentLength() == 10L);
            }
            else {
                assertTrue(status.getBytesRead() == 100L);
                assertTrue(status.getContentLength() == 100L);
            }
        }
    }

}
