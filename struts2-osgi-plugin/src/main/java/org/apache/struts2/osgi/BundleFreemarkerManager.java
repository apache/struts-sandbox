package org.apache.struts2.osgi;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.StrutsClassTemplateLoader;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;

public class BundleFreemarkerManager extends FreemarkerManager{
    private static final Logger LOG = LoggerFactory.getLogger(BundleFreemarkerManager.class);
    
    @Override
    protected TemplateLoader getTemplateLoader(ServletContext servletContext) {
        // construct a FileTemplateLoader for the init-param 'TemplatePath'
        FileTemplateLoader templatePathLoader = null;

        String templatePath = servletContext.getInitParameter("TemplatePath");
        if (templatePath == null) {
            templatePath = servletContext.getInitParameter("templatePath");
        }

        if (templatePath != null) {
            try {
                templatePathLoader = new FileTemplateLoader(new File(templatePath));
            } catch (IOException e) {
                LOG.error("Invalid template path specified: " + e.getMessage(), e);
            }
        }

        // presume that most apps will require the class and webapp template loader
        // if people wish to
        return templatePathLoader != null ?
                new MultiTemplateLoader(new TemplateLoader[]{
                        templatePathLoader,
                        new WebappTemplateLoader(servletContext),
                        new StrutsClassTemplateLoader(),
                        new BundleTemplateLoader()
                })
                : new MultiTemplateLoader(new TemplateLoader[]{
                    new WebappTemplateLoader(servletContext),
                    new StrutsClassTemplateLoader(),
                    new BundleTemplateLoader()
                });
    }
    
}
