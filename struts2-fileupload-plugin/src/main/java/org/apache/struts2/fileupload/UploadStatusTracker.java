package org.apache.struts2.fileupload;

import java.util.List;

/**
 * Describe your class here
 *
 * @author Your Name
 *         <p/>
 *         $Id$
 */
public interface UploadStatusTracker {
    /**
     *
     * @param key
     * @param status
     */
    void addUploadStatus(String key, UploadStatus status );

    /**
     *
     * @param key
     * @return
     */
    UploadStatus getUploadStatus(String key, int fileItemId);

    /**
     *
     * @param sessionId
     * @return
     */
    List<UploadStatus> getAllStatusesInSession(String sessionId);
}
