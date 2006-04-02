/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.flow.core.source.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;

import org.apache.struts.flow.core.source.ModifiableSource;
import org.apache.struts.flow.core.source.ModifiableTraversableSource;
import org.apache.struts.flow.core.source.MoveableSource;
import org.apache.struts.flow.core.source.Source;
import org.apache.struts.flow.core.source.SourceException;
import org.apache.struts.flow.core.source.SourceNotFoundException;
import org.apache.struts.flow.core.source.SourceUtil;
import org.apache.struts.flow.core.source.SourceValidity;
import org.apache.struts.flow.core.source.impl.validity.FileTimeStampValidity;

/**
 * A {@link ModifiableTraversableSource} for filesystem objects.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: FileSource.java,v 1.5 2004/02/28 11:47:24 cziegeler Exp $
 */

public class FileSource implements ModifiableTraversableSource, MoveableSource
{

    /** The file */
    private File m_file;

    /** The scheme */
    private String m_scheme;

    /** The URI of this source */
    private String m_uri;

    /**
     * Builds a FileSource given an URI, which doesn't necessarily have to start with "file:"
     * @param uri
     * @throws SourceException
     * @throws MalformedURLException
     */
    public FileSource(String uri) throws SourceException, MalformedURLException
    {
        int pos = SourceUtil.indexOfSchemeColon(uri);
        if (pos == -1)
        {
            throw new MalformedURLException("Invalid URI : " + uri);
        }

        String scheme = uri.substring(0, pos);
        String fileName = uri.substring(pos + 1);
        fileName = SourceUtil.decodePath(fileName);
        init(scheme, new File(fileName));
    }

    /**
     * Builds a FileSource, given an URI scheme and a File.
     * 
     * @param scheme
     * @param file
     * @throws SourceException
     */
    public FileSource(String scheme, File file) throws SourceException
    {
        init(scheme, file);
    }

    private void init(String scheme, File file) throws SourceException
    {
        m_scheme = scheme;

        String uri;
        try
        {
            uri = file.toURL().toExternalForm();
        }
        catch (MalformedURLException mue)
        {
            // Can this really happen ?
            throw new SourceException("Failed to get URL for file " + file, mue);
        }

        if (!uri.startsWith(scheme))
        {
            // Scheme is not "file:"
            uri = scheme + ':' + uri.substring(uri.indexOf(':') + 1);
        }

        m_uri = uri;

        m_file = file;
    }

    /**
     * Get the associated file
     */
    public File getFile()
    {
        return m_file;
    }

    //----------------------------------------------------------------------------------
    //  Source interface methods
    //----------------------------------------------------------------------------------

    /**
     * @see org.apache.struts.flow.core.source.Source#getContentLength()
     */
    public long getContentLength()
    {
        return m_file.length();
    }

    /**
     * @see org.apache.struts.flow.core.source.Source#getInputStream()
     */
    public InputStream getInputStream() throws IOException, SourceNotFoundException
    {
        try
        {
            return new FileInputStream(m_file);
        }
        catch (FileNotFoundException fnfe)
        {
            throw new SourceNotFoundException(m_uri + " doesn't exist.", fnfe);
        }
    }

    /**
     * @see org.apache.struts.flow.core.source.Source#getLastModified()
     */
    public long getLastModified()
    {
        return m_file.lastModified();
    }

    /**
     * @see org.apache.struts.flow.core.source.Source#getMimeType()
     */
    public String getMimeType()
    {
        return URLConnection.getFileNameMap().getContentTypeFor(m_file.getName());
    }

    /* (non-Javadoc)
     * @see org.apache.struts.flow.core.source.Source#getScheme()
     */
    public String getScheme()
    {
        return m_scheme;

    }

    /* (non-Javadoc)
     * @see org.apache.struts.flow.core.source.Source#getURI()
     */
    public String getURI()
    {
        return m_uri;
    }

    /**
     * Return a validity object based on the file's modification date.
     * 
     * @see org.apache.struts.flow.core.source.Source#getValidity()
     */
    public SourceValidity getValidity()
    {
        if (m_file.exists())
        {
            return new FileTimeStampValidity(m_file);
        }
        else
        {
            return null;
        }
    }

    /**
     * @see org.apache.struts.flow.core.source.Source#refresh()
     */
    public void refresh()
    {
        // Nothing to do...
    }

    /**
     * Does this source actually exist ?
     *
     * @return true if the resource exists.
     */
    public boolean exists()
    {
        return getFile().exists();
    }

    //----------------------------------------------------------------------------------
    //  TraversableSource interface methods
    //----------------------------------------------------------------------------------

    /**
     * @see org.apache.struts.flow.core.source.TraversableSource#getChild(java.lang.String)
     */
    public Source getChild(String name) throws SourceException
    {
        if (!m_file.isDirectory())
        {
            throw new SourceException(getURI() + " is not a directory");
        }

        return new FileSource(this.getScheme(), new File(m_file, name));

    }

    /**
     * @see org.apache.struts.flow.core.source.TraversableSource#getChildren()
     */
    public Collection getChildren() throws SourceException
    {

        if (!m_file.isDirectory())
        {
            throw new SourceException(getURI() + " is not a directory");
        }

        // Build a FileSource object for each of the children
        File[] files = m_file.listFiles();

        FileSource[] children = new FileSource[files.length];
        for (int i = 0; i < files.length; i++)
        {
            children[i] = new FileSource(this.getScheme(), files[i]);
        }

        // Return it as a list
        return Arrays.asList(children);
    }

    /**
     * @see org.apache.struts.flow.core.source.TraversableSource#getName()
     */
    public String getName()
    {
        return m_file.getName();
    }

    /**
     * @see org.apache.struts.flow.core.source.TraversableSource#getParent()
     */
    public Source getParent() throws SourceException
    {
        return new FileSource(getScheme(), m_file.getParentFile());
    }

    /**
     * @see org.apache.struts.flow.core.source.TraversableSource#isCollection()
     */
    public boolean isCollection()
    {
        return m_file.isDirectory();
    }

    //----------------------------------------------------------------------------------
    //  ModifiableSource interface methods
    //----------------------------------------------------------------------------------

    /**
     * Get an <code>InputStream</code> where raw bytes can be written to.
     * The signification of these bytes is implementation-dependent and
     * is not restricted to a serialized XML document.
     *
     * The output stream returned actually writes to a temp file that replaces
     * the real one on close. This temp file is used as lock to forbid multiple
     * simultaneous writes. The real file is updated atomically when the output
     * stream is closed.
     *
     * The returned stream must be closed or cancelled by the calling code.
     *
     * @return a stream to write to
     * @throws ConcurrentModificationException if another thread is currently
     *         writing to this file.
     */
    public OutputStream getOutputStream() throws IOException
    {
        // Create a temp file. It will replace the right one when writing terminates,
        // and serve as a lock to prevent concurrent writes.
        File tmpFile = new File(getFile().getPath() + ".tmp");

        // Ensure the directory exists
        tmpFile.getParentFile().mkdirs();

        // Can we write the file ?
        if (getFile().exists() && !getFile().canWrite())
        {
            throw new IOException("Cannot write to file " + getFile().getPath());
        }

        // Check if it temp file already exists, meaning someone else currently writing
        if (!tmpFile.createNewFile())
        {
            throw new ConcurrentModificationException(
                "File " + getFile().getPath() + " is already being written by another thread");
        }

        // Return a stream that will rename the temp file on close.
        return new FileSourceOutputStream(tmpFile, this);
    }

    /**
     * Can the data sent to an <code>OutputStream</code> returned by
     * {@link #getOutputStream()} be cancelled ?
     *
     * @return true if the stream can be cancelled
     */
    public boolean canCancel(OutputStream stream)
    {
        if (stream instanceof FileSourceOutputStream)
        {
            FileSourceOutputStream fsos = (FileSourceOutputStream) stream;
            if (fsos.getSource() == this)
            {
                return fsos.canCancel();
            }
        }

        // Not a valid stream for this source
        throw new IllegalArgumentException("The stream is not associated to this source");
    }

    /**
     * Cancel the data sent to an <code>OutputStream</code> returned by
     * {@link #getOutputStream()}.
     * <p>
     * After cancel, the stream should no more be used.
     */
    public void cancel(OutputStream stream) throws SourceException
    {
        if (stream instanceof FileSourceOutputStream)
        {
            FileSourceOutputStream fsos = (FileSourceOutputStream) stream;
            if (fsos.getSource() == this)
            {
                try
                {
                    fsos.cancel();
                }
                catch (Exception e)
                {
                    throw new SourceException("Exception during cancel.", e);
                }
                return;
            }
        }

        // Not a valid stream for this source
        throw new IllegalArgumentException("The stream is not associated to this source");
    }

    /**
     * Delete the source.
     */
    public void delete() throws SourceException
    {
        if (!m_file.exists())
        {
            throw new SourceNotFoundException("Cannot delete non-existing file " + m_file.toString());
        }
        
        if (!m_file.delete())
        {
            throw new SourceException("Could not delete " + m_file.toString() + " (unknown reason)");
        } 
    }

    //----------------------------------------------------------------------------------
    //  ModifiableTraversableSource interface methods
    //----------------------------------------------------------------------------------

    /**
     * @see org.apache.struts.flow.core.source.ModifiableTraversableSource#makeCollection()
     */
    public void makeCollection() throws SourceException
    {
        m_file.mkdirs();
    }

    //----------------------------------------------------------------------------------
    //  MoveableSource interface methods
    //----------------------------------------------------------------------------------

    /**
     * @see org.apache.struts.flow.core.source.MoveableSource#copyTo(org.apache.struts.flow.core.source.Source)
     */
    public void copyTo(Source destination) throws SourceException
    {
        try
        {
            SourceUtil.copy(this.getInputStream(), ((ModifiableSource) destination).getOutputStream());
        }
        catch (IOException ioe)
        {
            throw new SourceException("Couldn't copy " + getURI() + " to " + destination.getURI(), ioe);
        }
    }

    /**
     * @see org.apache.struts.flow.core.source.MoveableSource#moveTo(org.apache.struts.flow.core.source.Source)
     */
    public void moveTo(Source destination) throws SourceException
    {
        if (destination instanceof FileSource)
        {
            final File dest = ((FileSource) destination).getFile();
            final File parent = dest.getParentFile();

            if (parent != null)
            {
                parent.mkdirs(); // ensure parent directories exist
            }

            if (!m_file.renameTo(dest))
            {
                throw new SourceException("Couldn't move " + getURI() + " to " + destination.getURI());
            }
        }
        else
        {
            SourceUtil.move(this, destination);
        }

    }

    //----------------------------------------------------------------------------------
    //  Private helper class for ModifiableSource implementation
    //----------------------------------------------------------------------------------

    /**
     * A file outputStream that will rename the temp file to the destination file upon close()
     * and discard the temp file upon cancel().
     */
    private static class FileSourceOutputStream extends FileOutputStream
    {

        private File m_tmpFile;
        private boolean m_isClosed = false;
        private FileSource m_source;

        public FileSourceOutputStream(File tmpFile, FileSource source) throws IOException
        {
            super(tmpFile);
            m_tmpFile = tmpFile;
            m_source = source;
        }

        public void close() throws IOException
        {
            if (!m_isClosed)
            {
                super.close();
                try
                {
                    // Delete destination file
                    if (m_source.getFile().exists())
                    {
                        m_source.getFile().delete();
                    }
                    // Rename temp file to destination file
                    if (!m_tmpFile.renameTo(m_source.getFile())) 
                    {
                       throw new IOException("Could not rename " + 
                         m_tmpFile.getAbsolutePath() + 
                         " to " + m_source.getFile().getAbsolutePath());
                    }

                }
                finally
                {
                    // Ensure temp file is deleted, ie lock is released.
                    // If there was a failure above, written data is lost.
                    if (m_tmpFile.exists())
                    {
                        m_tmpFile.delete();
                    }
                    m_isClosed = true;
                }
            }

        }

        public boolean canCancel()
        {
            return !m_isClosed;
        }

        public void cancel() throws Exception
        {
            if (m_isClosed)
            {
                throw new IllegalStateException("Cannot cancel : outputstrem is already closed");
            }

            m_isClosed = true;
            super.close();
            m_tmpFile.delete();
        }

        public void finalize()
        {
            if (!m_isClosed && m_tmpFile.exists())
            {
                // Something wrong happened while writing : delete temp file
                m_tmpFile.delete();
            }
        }

        public FileSource getSource()
        {
            return m_source;
        }
    }
}
