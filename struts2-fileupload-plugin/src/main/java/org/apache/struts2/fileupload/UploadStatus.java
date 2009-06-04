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

import java.util.Calendar;

/**
 * POJO for holding the status of a running upload. Instance access
 * time is recorded to allow for simple cleanup.
 *
 * @author Wes W
 */
public class UploadStatus {

    private long lastAccess ;
    private long bytesRead ;
    private long contentLength ;
    private int itemId;

    /**
     *
     */
    public UploadStatus() {
        updateLastAccess() ;
        bytesRead = 0;
        contentLength = 0;
        itemId = 0;
    }

    /**
     *
     */
    public long getLastAccess() {
        return lastAccess;
    }

    /**
     *
     */
    public long getBytesRead() {
        updateLastAccess();
        return bytesRead;
    }

    /**
     *
     */
    public void setBytesRead(long bytesRead) {
        updateLastAccess();
        this.bytesRead = bytesRead;
    }

    /**
     *
     */
    public long getContentLength() {
        updateLastAccess();
        return contentLength;
    }

    /**
     *
     */
    public void setContentLength(long contentLength) {
        updateLastAccess();
        this.contentLength = contentLength;
    }

    /**
     *
     */
    public int getItemId() {
        updateLastAccess();
        return itemId;
    }

    /**
     *
     */
    public void setItemId(int itemId) {
        updateLastAccess();
        this.itemId = itemId;
    }

    /**
     *
     */
    private void updateLastAccess() {
        lastAccess = Calendar.getInstance().getTimeInMillis() / 1000 ;
    }

}
