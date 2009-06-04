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

import com.opensymphony.xwork2.inject.Inject;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

/**
 * The UploadStatusHolder simply holds UploadStatuses. It will
 * clean them up if an UploadStatus has not been accessed within
 * the configured amount of time (in seconds).
 *
 * @author Wes W
 */
public class UploadStatusHolder implements UploadStatusTracker {

    private int secondsToKeep = 600 ; //default to ten minutes
    public static Map<UploadFile,UploadStatus> statuses = new HashMap<UploadFile,UploadStatus>();

    private long lastRun = 0; // keep track so we aren't cleaning up all the time

    /**
     *
     * @param secondsToKeep
     */
    @Inject(FileUploadConstants.TIME_TO_KEEP_STATUS)
    public void setSecondsToKeep(String secondsToKeep) {
        try {
            this.secondsToKeep = Integer.parseInt(secondsToKeep);
        }
        catch (NumberFormatException nfe) {
            // come on people, how hard is it to specify an integer
            // if you flubbed it up, you can get the default
        }
    }

    /**
     *
     * @param key
     * @param status
     */
    public void addUploadStatus(String key, UploadStatus status ) {
        statuses.put(new UploadFile(key, status.getItemId()),status);
    }

    /**
     * 
     * @param key
     * @return
     */
    public UploadStatus getUploadStatus(String key, int fileItemId) {
        cleanUp();
        UploadFile mapKey = new UploadFile(key, fileItemId);
        return statuses.get(mapKey);
    }

    /**
     *
     * @param sessionId
     * @return
     */
    public List<UploadStatus> getAllStatusesInSession(String sessionId) {
        List<UploadStatus> statusesInSession = new ArrayList<UploadStatus>();
        for (UploadFile candidate : statuses.keySet()) {
            if (candidate.getSessionId() != null && candidate.getSessionId().equals(sessionId)) {
                statusesInSession.add(statuses.get(candidate));
            }
        }
        return statusesInSession;
    }

    private void cleanUp() {
        long now = Calendar.getInstance().getTimeInMillis() / 1000 ;
        if ( now - lastRun > secondsToKeep) {
            // time to clean up
            List<UploadFile> keys2del = new ArrayList<UploadFile>();
            for (UploadFile cleanUpKey : statuses.keySet()) {
                if ( now - statuses.get(cleanUpKey).getLastAccess() > secondsToKeep ) {
                    keys2del.add(cleanUpKey);
                }
            }
            for (UploadFile key2del : keys2del) {
                statuses.remove(key2del);
            }
        }
    }
}
