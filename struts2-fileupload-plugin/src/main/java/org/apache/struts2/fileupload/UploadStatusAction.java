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

import com.opensymphony.xwork2.ActionSupport;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.XStream;

import java.util.List;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.apache.struts2.ServletActionContext;

/**
 * Class that provides the ability to retrieve the
 * status of the session's running uploads.
 *
 * @author $Author$
 * <p/>
 */
public class UploadStatusAction extends ActionSupport {


    public InputStream getJsonStream() {
        UploadStatusHolder holder = new UploadStatusHolder();
        List<UploadStatus> statuses = holder.getAllStatusesInSession(
                ServletActionContext.getRequest().getSession(true).getId() );

        XStream xstream = new XStream(new JettisonMappedXmlDriver());
        // xstream.omitField(UploadStatus.class, "lastAccess");
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("status", UploadStatus.class);
        ByteArrayInputStream stream = new ByteArrayInputStream(xstream.toXML(statuses).getBytes());
        return stream;
    }
}
