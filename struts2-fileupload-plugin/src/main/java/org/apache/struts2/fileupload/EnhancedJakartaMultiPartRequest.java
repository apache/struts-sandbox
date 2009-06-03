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

import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Using Jakarta Commons File Upload, handle multipart requests. This class
 * acts mostly the same as the original JakartaMultiPartRequest.
 *
 * @author WesW
 */
public class EnhancedJakartaMultiPartRequest
        extends JakartaMultiPartRequest
        implements MultiPartRequest {

    static final Logger LOG = LoggerFactory.getLogger(MultiPartRequest.class);

    protected ProgressListener progressListener;
    protected FileItemFactory fileItemFactory;
    protected boolean portletUpload;

    /**
     *
     * @param progressListener
     */
    @Inject(FileUploadConstants.PROGRESS_LISTENER)
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     *
     * @param fileItemFactory
     */
    @Inject(FileUploadConstants.FILE_ITEM_FACTORY)
    public void setFileItemFactory(FileItemFactory fileItemFactory) {
        this.fileItemFactory = fileItemFactory;
    }

    /**
     *
     * @param portletUpload
     */
    @Inject(FileUploadConstants.IS_PORTLET)
    public void setPortletUpload(String portletUpload) {
        this.portletUpload = "true".equalsIgnoreCase(portletUpload);
    }

    /**
     *
     * @param request
     * @param saveDir
     * @throws IOException
     */
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        // Parse the request mostly copy/paste from JakartaMuliPartRequest
        try {

            FileUpload upload ;
            if (portletUpload) {
                upload = new PortletFileUpload(fileItemFactory);
            }
            else {
                upload = new ServletFileUpload(fileItemFactory);
            }

            upload.setSizeMax(maxSize);
            upload.setProgressListener(progressListener);

            List items = upload.parseRequest(createRequestContext(request));

            for (Object item1 : items) {
                FileItem item = (FileItem) item1;
                if (LOG.isDebugEnabled()) LOG.debug("Found item " + item.getFieldName());
                if (item.isFormField()) {
                    LOG.debug("Item is a normal form field");
                    List<String> values;
                    if (params.get(item.getFieldName()) != null) {
                        values = params.get(item.getFieldName());
                    } else {
                        values = new ArrayList<String>();
                    }

                    // note: see http://jira.opensymphony.com/browse/WW-633
                    // basically, in some cases the charset may be null, so
                    // we're just going to try to "other" method (no idea if this
                    // will work)
                    String charset = request.getCharacterEncoding();
                    if (charset != null) {
                        values.add(item.getString(charset));
                    } else {
                        values.add(item.getString());
                    }
                    params.put(item.getFieldName(), values);
                } else {
                    LOG.debug("Item is a file upload");

                    // Skip file uploads that don't have a file name - meaning that no file was selected.
                    if (item.getName() == null || item.getName().trim().length() < 1) {
                        LOG.debug("No file has been uploaded for the field: " + item.getFieldName());
                        continue;
                    }

                    List<FileItem> values;
                    if (files.get(item.getFieldName()) != null) {
                        values = files.get(item.getFieldName());
                    } else {
                        values = new ArrayList<FileItem>();
                    }

                    values.add(item);
                    files.put(item.getFieldName(), values);
                }
            }
        } catch (FileUploadException e) {
            LOG.warn("Unable to parse request", e);
            errors.add(e.getMessage());
        }
    }

    /**
     * Creates a RequestContext needed by Jakarta Commons Upload.
     * (copy/pasta from JakartaMultiPartRequest)
     *
     * @param req  the request.
     * @return a new request context.
     */
    private RequestContext createRequestContext(final HttpServletRequest req) {
        return new RequestContext() {
            public String getCharacterEncoding() {
                return req.getCharacterEncoding();
            }

            public String getContentType() {
                return req.getContentType();
            }

            public int getContentLength() {
                return req.getContentLength();
            }

            public InputStream getInputStream() throws IOException {
                InputStream in = req.getInputStream();
                if (in == null) {
                    throw new IOException("Missing content in the request");
                }
                return req.getInputStream();
            }
        };
    }
}
