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

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItemFactory;
import com.opensymphony.xwork2.inject.Inject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class basically wraps the DiskFileItemFactory which is the
 * default FileItemFactory for Jakarta FileUpload. The threshold
 * setting specifies how large a file can get before it gets flushed
 * from memory onto disk.
 *
 * TODO: Someone mentioned that google app engine won't let you
 * write to the filesystem. It should be documented that you could
 * potentially set the threshold to match your max upload size and then
 * keep all files in memory. I don't know if it will work, since the
 * MultiPartRequest interface insists that Files be used and I don't
 * know what will happen, but it is worth trying.
 *
 * @author Wes W
 */
public class DiskFileItemFactoryWrapper implements FileItemFactoryWrapper {

    protected DiskFileItemFactory factory;

    /**
     * 
     * @param sizeThreshold
     * @param repositoryPath
     */
    public DiskFileItemFactoryWrapper(
            @Inject(FileUploadConstants.SIZE_THRESHOLD)
                String sizeThreshold,
            @Inject(FileUploadConstants.FILE_REPOSITORY_PATH)
                String repositoryPath ) {

        int threshold ;
        try {
            threshold = Integer.parseInt(sizeThreshold);
        }
        catch (NumberFormatException nfe) {
            // how hard is it to specify a valid integer?
        }
        finally {
            threshold = 256; // I guess this is a magic number
        }

        File repoPath = convertStringToFile(repositoryPath);

        factory = new DiskFileItemFactory(threshold, repoPath);
    }

    /**
     *
     * @return
     */
    public FileItemFactory getFileItemFactory() {
        return factory;
    }

    /**
     *
     * @param threshold
     */
    public void setSizeThreshold(int threshold) {
        this.factory.setSizeThreshold(threshold);
    }

    /**
     *
     * @param repo
     */
    public void setRepositoryPath(String repo) {
        this.factory.setRepository(convertStringToFile(repo));
    }

    private File convertStringToFile(String path) {
        File repoPath = new File(path);
        if (repoPath == null || !repoPath.isDirectory()) {
            File tempFile = null;
            try {
                tempFile = File.createTempFile("","");
            }
            catch (IOException ioe) {
                // I guess we're just sort of screwed, punt
                repoPath = null;
            }
            if (tempFile != null ) {
                repoPath = tempFile.getParentFile();
                tempFile.delete();
            }
        }
        return repoPath;
    }

}
