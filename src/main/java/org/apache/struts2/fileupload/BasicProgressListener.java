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

import org.apache.commons.fileupload.ProgressListener;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Just a simple ProgressListener for Jakarta FileUpload. It will store
 * file progress in a HashMap that gets cleaned out once in a while.
 * The HashMap will key the upload status off of the HttpSession.getId(),
 * which means that you can't have multiple uploads going to the same
 * server from different browser windows or tabs.
 *
 * @author Wes W
 */
public class BasicProgressListener implements ProgressListener {

    private int updateFrequency = 2048; // magic number for a default
    private UploadStatusHolder holder = new UploadStatusHolder();
    private long lastUpdate = -1L; //

    /**
     * 
     * @param updateFrequency
     */
    @Inject(FileUploadConstants.LISTENER_UPDATE_FREQUENCY)
    public void setUpdateFrequency(String updateFrequency) {
        try {
            this.updateFrequency = Integer.parseInt(updateFrequency);
        }
        catch (NumberFormatException nfe) {
            // come on people, how hard is it to specify an integer
            // if you flubbed it up, you can get the default
        }
    }

    /**
     * 
     * @param bytesRead
     * @param contentLength
     * @param item
     */
    public void update(long bytesRead, long contentLength, int item) {
        if ( bytesRead / updateFrequency > lastUpdate) {
            lastUpdate = bytesRead / updateFrequency;
            if (lastUpdate == 0 ) {
                // we could get stuck here if we leave it
                lastUpdate = 1L;
            }
            // I wonder if this will ever NPE
            String sessionId = ServletActionContext.getRequest().getSession(true).getId();
            UploadStatus status = new UploadStatus();
            status.setBytesRead(bytesRead);
            status.setContentLength(contentLength);
            status.setItem(item);
            holder.addUploadStatus(sessionId, status);
        }
    }
}
