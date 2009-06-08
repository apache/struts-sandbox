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

import java.util.HashMap;
import java.util.Map;

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
    private UploadStatusTracker tracker;
    private static Map<UploadFile,Long> lastUpdates = new HashMap<UploadFile,Long>(); //

    /**
     *
     * @param tracker
     */
    @Inject(FileUploadConstants.UPLOAD_STATUS_HOLDER)
    public void setTracker(UploadStatusTracker tracker) {
        this.tracker = tracker;
    }

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
        String sessionId = ServletActionContext.getRequest().getSession(true).getId();
        UploadFile curKey = new UploadFile(sessionId,item);
        Long lastUpdateValue = lastUpdates.get(curKey);
        long lastUpdate = 0;
        if (lastUpdateValue != null ) {
            lastUpdate = lastUpdateValue.longValue();
        }
        if ( bytesRead / updateFrequency >= lastUpdate) {
            lastUpdate = bytesRead / updateFrequency;
            if (lastUpdate == 0 ) {
                lastUpdate = 1;
            }
            lastUpdates.remove(curKey);
            lastUpdates.put(curKey, lastUpdate);
            UploadStatus status = new UploadStatus();
            status.setBytesRead(bytesRead);
            status.setContentLength(contentLength);
            status.setItemId(item);
            tracker.addUploadStatus(sessionId, status);
        }
    }
}
