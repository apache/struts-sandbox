package org.apache.struts2.showcase.source;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.util.ServletContextAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.xwork.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.uwyn.jhighlight.renderer.XhtmlRendererFactory;
import com.uwyn.jhighlight.renderer.Renderer;

@Namespace("/source")
@Results(
        @Result(name = com.opensymphony.xwork2.Action.SUCCESS, location = "source.jsp")
)
public class SourceAction extends ActionSupport implements ServletContextAware {
    private ServletContext servletContext;

    private String className;
    private String output;
    private boolean fragment = true;
    private String path;

    @Action("get-class-source")
    public String getClassSource() throws IOException {
        String filePath = "/" + className.replace('.', '/') + ".java";

        InputStream is = ClassLoaderUtil.getResourceAsStream(filePath, getClass());

        Renderer renderer = XhtmlRendererFactory.getRenderer(XhtmlRendererFactory.JAVA);
        String fileName = StringUtils.substringAfterLast(filePath, "/");
        output = renderer.highlight(fileName, IOUtils.toString(is), "UTF-8", fragment);

        return SUCCESS;
    }

    @Action("get-xml-source")
    public String getXmlSource() throws IOException {
        if (path.endsWith(".xml")) {
            File root = FileUtils.toFile(ClassLoaderUtil.getResource("/", getClass()));
            File fileUrl = FileUtils.toFile(ClassLoaderUtil.getResource(path, getClass()));

            //make sure file is under the right directory
            if (fileUrl.getAbsolutePath().startsWith(root.getAbsolutePath())) {
                Renderer renderer = XhtmlRendererFactory.getRenderer(XhtmlRendererFactory.XML);
                String content = IOUtils.toString(FileUtils.openInputStream(fileUrl));
                output = renderer.highlight(fileUrl.getName(), content, "UTF-8", fragment);
            }
        }
        return SUCCESS;
    }


    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getOutput() {
        return output;
    }

    public void setFragment(boolean fragment) {
        this.fragment = fragment;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
