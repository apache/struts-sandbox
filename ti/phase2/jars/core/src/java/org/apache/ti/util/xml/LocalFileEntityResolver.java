/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.util.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Entity resolver that tries to find the resource locally (from classloader, under a given path prefix) before trying
 * to resolve via the network.
 */
public class LocalFileEntityResolver
        implements EntityResolver {
    private String _resourcePathPrefix;

    /**
     * Constructor.
     * @param resourcePathPrefix The path prefix to use when trying to resolve a file locally.
     */
    public LocalFileEntityResolver(String resourcePathPrefix) {
        _resourcePathPrefix = resourcePathPrefix;
    }

    /**
     * Resolve the entity.  First try to find it locally, then fallback to the network.
     */
    public InputSource resolveEntity(String publicID, String systemID)
            throws SAXException, IOException {
        InputSource localFileInput = resolveLocalEntity(systemID);

        return (localFileInput != null) ? localFileInput : new InputSource(systemID);
    }

    /**
     * Resolve the given entity locally.
     */
    public InputSource resolveLocalEntity(String systemID)
            throws SAXException, IOException {
        String localFileName = systemID;
        int fileNameStart = localFileName.lastIndexOf('/') + 1;

        if (fileNameStart < localFileName.length()) {
            localFileName = systemID.substring(fileNameStart);
        }

        ClassLoader cl = LocalFileEntityResolver.class.getClassLoader();
        InputStream stream = cl.getResourceAsStream(_resourcePathPrefix + localFileName);

        if (stream != null) {
            return new InputSource(stream);
        } else if (_resourcePathPrefix.endsWith(localFileName)) {
            // If the resource path prefix itself resolves to the right file, use that.
            return new InputSource(_resourcePathPrefix);
        }

        return null;
    }
}
