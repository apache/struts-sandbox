package org.apache.struts2.jquery;

import org.apache.struts2.views.TagLibrary;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created primarily to provide <@sjx.tag support in Freemarker
 *
 * @author $Author$
 *         <p/>
 *         $Id$
 */
public class JQueryTaglib implements TagLibrary {

    /**
     * Gets a Java object that contains getters for the tag library's Freemarker models.
     * Called once per Freemarker template processing.
     *
     * @param stack The current value stack
     * @param req   The HTTP request
     * @param res   The HTTP response
     * @return The Java object containing the Freemarker model getter methods
     */
    public Object getFreemarkerModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new JQueryStrutsModels(stack, req, res);
    }

    /**
     * Gets a list of Velocity directive classes for the tag library.  Called once on framework
     * startup when initializing Velocity.
     *
     * @return A list of Velocity directive classes
     */
    public List<Class> getVelocityDirectiveClasses() {
        throw new UnsupportedOperationException("no velocity support currently, sorry");
    }
}
