/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shale.faces;

import java.io.IOException;
import java.net.URL;
import javax.faces.FactoryFinder;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shale.view.DefaultViewControllerMapper;

/**
 * <p>{@link ShaleApplicationFilter} is a <code>Filter</code> implementation
 * that invokes the required <em>Application Controller</em> functionality on
 * every request.
 * In addition, it performs overall application startup and shutdown
 * operations on behalf of the framework.</p>
 *
 * <p>The detailed processing to be performed for each request is configured
 * by a <code>Command</code> or <code>Chain</code> defined using the "Chain of
 * Resposibility" design pattern, as implemented by the Commons Chain package.
 * There must exist a <code>Catalog</code> named <code>shale</code>, which
 * contains a <code>Command</code> named <code>standard</code>, that defines
 * the processing to be performed.</p>
 *
 * <p>At any point, one of the <code>Command</code>s being executed may choose
 * to complete the response itself (such as to perform an HTTP redirect),
 * instead of allowing processing to continue.  To indicate this choice, the
 * <code>Command</code> should follow the standard Commons Chain convention of
 * returning <code>true</code>.  If you want processing to continue, return
 * <code>false</code> instead.</p>
 *
 * <p>The default implementation of the standard command processing chain
 * performs the following tasks:</p>
 * <ul>
 * <li>Invoke a command named <code>preprocess</code> (in the <code>shale</code>
 *     catalog), if it exists.  This is where you should insert commands to be
 *     executed <strong>before</code> {@link ShaleApplicationFilter} passes the
 *     request on to the next filter or servlet.</li>
 * <li>Execute the remainder of the filter chain for this request.</li>
 * <li>Invokes a command named <code>postprocess</code> (in the <code>shale</code>
 *     catalog), if it exists.  This is where you should insert commands to be
 *     executed <strong>after</code> control returns from the invoked filter or
 *     servlet.  Note that it is no longer possible, at this point, to replace
 *     the response content produced by the filter or servlet -- that should
 *     be done in a preprocess step.</li>
 * </ul>
 *
 * <p><strong>NOTE</strong> - Configuration of the <code>shale</code> catalog,
 * and the commands it contains, may be performed in any manner you desire.
 * One convenient mechanism is to use the <code>ChainListener</code> class
 * that is included in the Commons Chain package.  If you do not reconfigure it
 * differently, the <code>standard</code> command (in the <code>shale</code>
 * catalog) will be configured according to the embedded resource
 * <code>org/apache/shale/faces/shale-config.xml</code> in the JAR file
 * containing the core Shale runtime environment, which executes the default
 * request processing described above.</p>
 *
 * $Id$
 */

public class ShaleApplicationFilter implements Filter {


    // -------------------------------------------------------- Static Variables


    /**
     * <p>The name of the Commons Chain <code>Catalog</code> to use.</p>
     */
    public static final String CATALOG_NAME = "shale";


    /**
     * <p>The name of the <code>Command</code> (in the <code>Catalog</code>
     * named by <code>CATALOG_NAME</code>) that performs the standard
     * application scope request processing.</p>
     */
    public static final String COMMAND_NAME = "standard";


    /**
     * <p>The request scope attribute key under which the <code>Context</code>
     * object used for this chain of command request to be stored, in addition
     * to it being passed in to the command chains.</p>
     */
    public static final String CONTEXT_ATTR =
      "org.apache.shale.CONTEXT_ATTR";


    /**
     * <p>The name of the internal resource containing our default
     * configuration of the default command.</p>
     */
    public static final String RESOURCE_NAME =
      "org/apache/shale/faces/shale-config.xml";


    /**
     * <p>The <code>Log</code> instance for this class.</p>
     */
    private static final Log log =
      LogFactory.getLog(ShaleApplicationFilter.class);


    // ------------------------------------------------------ Instance Variables


    /**
     * <p>Chain of Responsibility <code>Catalog</code> we will be using.</p>
     */
    private Catalog catalog = null;


    /**
     * <p>Filter configuration object for this <code>Filter</code>.</p>
     */
    private FilterConfig config = null;


    /**
     * <p>The <code>ServletContext</code> instance for our web application.</p>
     */
    private ServletContext context = null;


    /**
     * <p>The JSF <code>PhaseListener</code> that we have registered.</p>
     */
    private PhaseListener phaseListener = null;


    // ---------------------------------------------------------- Filter Methods


    /**
     * <p>Perform application shutdown finalization as necessary.</p>
     */
    public void destroy() {

        log.info("Finalizing Shale application filter");

        if (phaseListener != null) {
            getLifecycle().removePhaseListener(phaseListener);
        }
        phaseListener = null;
        context = null;
        config = null;
        catalog = null;

    }
    

    /**
     * <p>Perform per-request application controler functionality.</p>
     *
     * @param request The request we are processing
     * @param response The response we are creating
     * @param chain The filter chain for this request
     */
    public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

      // Construct and store a new Context for this request
      ShaleWebContext context =
        new ShaleWebContext(this.context,
          (HttpServletRequest) request,
          (HttpServletResponse) response, chain);
      request.setAttribute(CONTEXT_ATTR, context);

      // Invoke the standard processing command on this context
      Command standard = catalog.getCommand(COMMAND_NAME);
      try {
          standard.execute(context);
      } catch (IOException e) {
          throw e;
      } catch (ServletException e) {
          throw e;
      } catch (Exception e) {
          throw new ServletException(e);
      }

      // Clean up the stored request attribute
      request.removeAttribute(CONTEXT_ATTR);

    }


    /**
     * <p>Perform application startup intiialization as necessary.</p>
     *
     * @param config <code>FilterConfig</code> for this filter
     */
    public void init(FilterConfig config) throws ServletException {

        log.info("Initializing Shale application filter");

        this.config = config;
        context = config.getServletContext();
        phaseListener = new ShalePhaseListener();
        getLifecycle().addPhaseListener(phaseListener);
        // FIXME - make the mapper pluggable
        context.setAttribute(ShaleConstants.VIEW_MAPPER,
          new DefaultViewControllerMapper());

        // Look up the "shale" catalog and ensure "standard" is defined
        try {
            this.catalog = getCatalog();
        } catch (ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }


    // --------------------------------------------------------- Private Methods


    /**
     * <p>Return the "shale" catalog with a "standard" command, configuring the
     * default version of this command if necessary.</p>
     *
     * @exception Exception if a resource parsing exception occurs
     */
    private Catalog getCatalog() throws Exception {

        // Look up the "shale" catalog, creating one if necessary
        Catalog catalog = CatalogFactory.getInstance().getCatalog(CATALOG_NAME);
        if (catalog == null) {
            if (log.isDebugEnabled()) {
                log.debug("Creating catalog '" + CATALOG_NAME + "'");
            }
            catalog = new CatalogBase();
            CatalogFactory.getInstance().addCatalog(CATALOG_NAME, catalog);
        }

        // If the required command exists, just return the catalog
        if (catalog.getCommand(COMMAND_NAME) != null) {
            return catalog;
        }

        // Configure based on our default resource
        if (log.isDebugEnabled()) {
            log.debug("Parsing default resource '" + RESOURCE_NAME + "'");
        }
        ConfigParser parser = new ConfigParser();
        URL url = this.getClass().getClassLoader().getResource(RESOURCE_NAME);
        if (url == null) {
            throw new IllegalArgumentException(RESOURCE_NAME);
        }
        parser.parse(url);

        // Ensure that the required command has been configured
        if (catalog.getCommand(COMMAND_NAME) == null) {
            throw new IllegalArgumentException(COMMAND_NAME);
        }
        return catalog;

    }


    /**
     * <p>Return the JSF <code>Lifecycle</code> instance for this
     * web application.</p>
     */
    private Lifecycle getLifecycle() {

        String lifecycleId =
          context.getInitParameter("javax.faces.LIFECYCLE_ID");
        if (lifecycleId == null) {
            lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
        }
        LifecycleFactory factory = (LifecycleFactory)
          FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        return factory.getLifecycle(lifecycleId);

    }


}
