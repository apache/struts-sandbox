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
import static org.junit.Assert.assertTrue;

/**
 * Unit Test for UploadStatusHolder
 *
 * @author Wes W
 */
public class UploadStatusHolderTest {

    /**
     *
     */
    @Test
    public void testStatusHolding() {
        UploadStatusHolder holder1 = new UploadStatusHolder();
        holder1.setSecondsToKeep("600");
        UploadStatus status1 = new UploadStatus();
        status1.setBytesRead(1L);
        status1.setContentLength(1L);
        status1.setItemId(1);
        holder1.addUploadStatus("status1",status1);

        UploadStatusTracker holder2 = new UploadStatusHolder();
        UploadStatus status2 = holder2.getUploadStatus("status1", 1);

        assertTrue(status1.getBytesRead() == status2.getBytesRead());
        assertTrue(status1.getContentLength() == status2.getContentLength());
        assertTrue(status1.getItemId() == status2.getItemId());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testStatusHoldingGetsRemoved() throws Exception {
        UploadStatusHolder holder1 = new UploadStatusHolder();
        holder1.setSecondsToKeep("1");

        UploadStatus status1 = new UploadStatus();
        status1.setBytesRead(1L);
        status1.setContentLength(1L);
        status1.setItemId(1);

        holder1.addUploadStatus("status1",status1);
        Thread.sleep(5000L);

        UploadStatus status2 = holder1.getUploadStatus("status1", 1);
        assertTrue(status2 != null);
        // cleanup happens after retrieval, so it will most likely take at least one retrieval for objects to disappear
        UploadStatus status3 = holder1.getUploadStatus("status1", 1);
        assertTrue(status3 == null);
    }
}
