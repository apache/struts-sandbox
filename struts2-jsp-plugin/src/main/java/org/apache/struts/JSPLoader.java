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
package org.apache.struts;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.ClassLoaderUtil;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;
import java.io.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspC;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

/**
 * Uses jasper to extract a JSP from the classpath to a file and compile it
 */
public class JSPLoader implements BuildListener {
    private static final Logger LOG = LoggerFactory.getLogger(JSPLoader.class);
    public static final File JSP_DIR = new File(System.getProperty("java.io.tmpdir"), "struts_jsps");

    private static ClassLoader classLoader;
    private static final String DEFAULT_NAMESPACE = "org.apache.jsp";
    private static final String DEFAULT_PATH = "org/apache/jsp";

    static {
        try {
            classLoader = new URLClassLoader(new URL[]{JSP_DIR.toURI().toURL()});
        } catch (MalformedURLException e) {
            //this is kinda dumb
            throw new RuntimeException(e);
        }
    }

    public Servlet load(String location, ServletContext servletContext) throws Exception {
        File jspFile = extractFile(location);
        File classFile = compileJSP(jspFile);
        Class clazz = loadClass(classFile);
        return createServlet(clazz, servletContext);
    }

    /**
     * Creates and inits a servlet
     */
    private Servlet createServlet(Class clazz, ServletContext servletContext) throws IllegalAccessException, InstantiationException, ServletException {
        Servlet servlet = (Servlet) clazz.newInstance();
        JSPServletConfig config = new JSPServletConfig(servletContext);
        servlet.init(config);

        return servlet;
    }

    private Class loadClass(File file) throws ClassNotFoundException {
        StringBuilder sb = new StringBuilder(DEFAULT_NAMESPACE);
        sb.append(".");
        sb.append(StringUtils.substringBefore(file.getName(), "."));
        return classLoader.loadClass(sb.toString());
    }

    private File compileJSP(File file) throws JasperException {
        //ant setup
        Project project = new Project();
        project.addBuildListener(this);

        File parentFile = file.getParentFile();
        String parentPath = parentFile.getAbsolutePath();

        JspC jspC = new JspC();
        jspC.setProject(project);
        jspC.setOutputDir(parentPath);
        jspC.setCompile(true);
        jspC.setJspFiles(file.getName());
        jspC.setUriroot(parentPath);
        jspC.execute();

        String classFileName = file.getName().replace(".jsp", "_jsp.class");
        return new File(parentFile, DEFAULT_PATH + "/" + classFileName);
    }

    /**
     * Looks up a file with path finalLocation from the classpath and extracts it to a temporal file
     */
    public File extractFile(String finalLocation) throws IOException {
        InputStream inputStream = ClassLoaderUtil.getResourceAsStream(finalLocation, EmbeddedJSPResult.class);

        if (inputStream == null)
            throw new FileNotFoundException("Unable to find file [" +
                    finalLocation
                    + "] in the classpath");

        FileOutputStream jspOutputStream = null;

        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);

            //file name and path
            String[] splitted = finalLocation.split("\\.|/");
            String path = StringUtils.substringBeforeLast(finalLocation, "/");
            String fileName = StringUtils.substringAfterLast(finalLocation, "/");


            //create output dir
            if (!JSP_DIR.exists())
                JSP_DIR.mkdirs();

            //temp file
            File jspFile = new File(JSP_DIR, fileName);

            //output channel
            jspOutputStream = new FileOutputStream(jspFile);
            FileChannel outChannel = jspOutputStream.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            //copy
            while (true) {
                int read = readableByteChannel.read(buffer);

                if (read == -1)
                    break;

                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }

            return jspFile;
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (jspOutputStream != null)
                jspOutputStream.close();
        }
    }

    public void buildStarted(BuildEvent buildEvent) {
    }

    public void buildFinished(BuildEvent buildEvent) {
    }

    public void targetStarted(BuildEvent buildEvent) {
    }

    public void targetFinished(BuildEvent buildEvent) {
    }

    public void taskStarted(BuildEvent buildEvent) {
    }

    public void taskFinished(BuildEvent buildEvent) {
    }

    public void messageLogged(BuildEvent buildEvent) {
        LOG.debug(buildEvent.getMessage());
    }
}
