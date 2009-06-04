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

/**
 * This class just holds the identifying fields for a file
 * that is in the process of being uploaded.
 *
 * @author $Author$
 * <p/>
 */
public class UploadFile {

    private String sessionId;
    private int fileItemId;

    /**
     *
     * @param sessionId
     * @param fileItemId
     */
    public UploadFile(String sessionId, int fileItemId) {
        this.sessionId = sessionId;
        this.fileItemId = fileItemId;
    }

    /**
     *
     * @return
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     *
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 
     * @return
     */
    public int getFileItemId() {
        return fileItemId;
    }

    /**
     *
     * @param fileItemId
     */
    public void setFileItemId(int fileItemId) {
        this.fileItemId = fileItemId;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadFile)) return false;

        UploadFile that = (UploadFile) o;

        if (fileItemId != that.fileItemId) return false;
        if (!sessionId.equals(that.sessionId)) return false;

        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = sessionId.hashCode();
        result = 31 * result + fileItemId;
        return result;
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString() {
        return "sessionId - " + sessionId + ", fileItemId - " + fileItemId;
    }
}
