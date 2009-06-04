/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.fileupload;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.opensymphony.xwork2.ActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

/**
 * Unit test for BasicProgressListener
 *
 * @author Wes W
 */
public class BasicProgressListenerTest implements StrutsStatics {

    ActionContext actionContext;
    // ServletActionContext servletActionContext;
    private MockServletContext servletContext;
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     *
     */
    @Before
    public void setUp() {
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
    public void testUpdate() {
        BasicProgressListener listener = new BasicProgressListener();
        listener.setUpdateFrequency("1");
        UploadStatusTracker tracker = new UploadStatusHolder();
        listener.setTracker(tracker);
        listener.update(10L,10L,1);

        UploadStatusTracker tracker2 = new UploadStatusHolder();

        String key = request.getSession().getId();
        System.err.println("key - " + key);

        UploadStatus status = tracker2.getUploadStatus(key , 1);

        assertTrue(status.getBytesRead() == 10L);
        assertTrue(status.getContentLength() == 10L);
        assertTrue(status.getItemId() == 1);
    }

    /**
     *
     */
    @Test
    public void testDontUpdate() {
        BasicProgressListener listener = new BasicProgressListener();
        // listener.setUpdateFrequency("1000"); // let default of 2048 take over
        UploadStatusTracker tracker = new UploadStatusHolder();
        listener.setTracker(tracker);
        listener.update(10L,10L,1);
        listener.update(100L,100L,1);

        UploadStatusTracker tracker2 = new UploadStatusHolder();

        String key = request.getSession().getId();
        System.err.println("key - " + key);

        UploadStatus status = tracker2.getUploadStatus(key, 1 );

        assertTrue(status.getBytesRead() == 10L);
        assertTrue(status.getContentLength() == 10L);
        assertTrue(status.getItemId() == 1);

    }
}