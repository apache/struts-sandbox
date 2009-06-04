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
 * Unit Test for UploadStatus
 *
 * @author Wes W
 */
public class UploadStatusTest {

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLastAccessChange() throws Exception {
        UploadStatus stat = new UploadStatus() ;
        long firstAccess = stat.getLastAccess();
        stat.setBytesRead(1L);
        stat.setContentLength(1L);
        stat.setItemId(1);
        Thread.sleep(5000L);
        assertTrue(1 == stat.getBytesRead());
        assertTrue(1 == stat.getContentLength());
        assertTrue(1 == stat.getItemId());
        assertTrue(firstAccess < stat.getLastAccess());
    }
}
