package org.apache.struts2.jquery.freemarker.models;

import org.apache.struts2.views.freemarker.tags.TagModel;
import org.apache.struts2.components.Component;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Describe your class here
 *
 * @author $Author$
 *         <p/>
 *         $Id$
 */
public class JQueryFreemarkerModel extends TagModel {

    private Class clazz;
    private static final Logger LOG = LoggerFactory.getLogger(JQueryFreemarkerModel.class);

    public JQueryFreemarkerModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res, Class clazz) {
        super(stack, req, res);
        this.clazz = clazz;
    }

    protected Component getBean() {

        Constructor con;
        try {
           con = clazz.getConstructor(ValueStack.class,
                                      HttpServletRequest.class,
                                      HttpServletResponse.class);
        }
        catch(NoSuchMethodException nsme) {
            LOG.error("class specified does not appear to have an appropriate constructor");
            return null;
        }

        Object o = null;
        try {
            o = con.newInstance(new Object[]{stack,req,res});
        }
        catch (InvocationTargetException e) {
            LOG.error("InvocationTargetException caught instantiating component - " + clazz.getName());
        }
        catch (IllegalAccessException e) {
            LOG.error("IllegalAccessException caught instantiating component - " + clazz.getName());
        }
        catch (InstantiationException e) {
            LOG.error("InstantiationException caught instantiating component - " + clazz.getName());
        }

        if (o instanceof Component) {
            return (Component)o;
        }

        return null;

    }
}
