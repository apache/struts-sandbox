/*
 * $Id$
 *
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.processor.chain;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ti.processor.ProcessorException;
import org.apache.ti.processor.RequestProcessor;
import org.apache.ti.util.SourceResolver;


/**
 *  Request processor that uses commons-chain.
 */
public class ChainRequestProcessor implements RequestProcessor {

    /**
     * <p>Comma-separated list of context or classloader-relative path(s) that
     * contain the configuration for the default commons-chain catalog(s).</p>
     */
    protected String chainConfig = "org/apache/ti/processor/chain/chain-config-servlet.xml";

    /**
     * <p>Commons Logging instance.</p>
     */
    protected static Log log = LogFactory.getLog(ChainRequestProcessor.class);

    /**
     * <p>The {@link CatalogFactory} from which catalog containing the the
     * base request-processing {@link Command} will be retrieved.</p>
     */
    protected CatalogFactory catalogFactory = null;


    /**
     * <p>The {@link Catalog} containing all of the available command chains
     * for this module.
     */
    protected Catalog catalog = null;


    /**
     * <p>The {@link Command} to be executed for each request.</p>
     */
    protected Command startCmd = null;

    protected String catalogName = "struts-ti";
    protected String startCmdName = "start";
    protected String initCmdName = "init";

    protected SourceResolver resolver = null;

    protected RuleSet ruleSet = null;

    protected Map initParameters = null;

    public void setSourceResolver(SourceResolver resolver) {
        this.resolver = resolver;
    }

    public void setStartCommandName(String name) {
        this.startCmdName = name;
    }

    public void setInitCommandName(String name) {
        this.initCmdName = name;
    }

    public void setCatalogName(String name) {
        this.catalogName = name;
    }

    public void setChainConfig(String name) {
        this.chainConfig = name;
    }

    public void setChainRuleSet(RuleSet ruleset) {
        this.ruleSet = ruleset;
    }


    public void init(Map initParameters, WebContext webContext) {
        this.initParameters = initParameters;

        String chain = (String) initParameters.get("chainConfig");
        try {
            initChain(chain, webContext);

            initCatalogFactory();

            catalog = this.catalogFactory.getCatalog(catalogName);
            if (catalog == null) {
                throw new ProcessorException("Cannot find catalog '"
                        + catalogName + "'");
            }

            Command initCmd = catalog.getCommand(initCmdName);
            if (initCmd == null) {
                throw new ProcessorException("Cannot find init command '"
                        + initCmdName + "'");
            }
            initCmd.execute(webContext);

            startCmd = catalog.getCommand(startCmdName);
            if (startCmd == null) {
                throw new ProcessorException("Cannot find command '"
                        + startCmdName + "'");
            }

        } catch (Throwable t) {

            // The follow error message is not retrieved from internal message
            // resources as they may not have been able to have been
            // initialized
            log.error("Unable to initialize Struts ServletRequestHandler due to an "
                + "unexpected exception or error thrown, so marking the "
                + "servlet as unavailable.  Most likely, this is due to an "
                + "incorrect or missing library dependency.", t);
            throw new ProcessorException(t);
        }
    }


    public void process(WebContext ctx) {
        // Create and execute the command.
        try {
            if (log.isDebugEnabled()) {
                log.debug("Using the processing chain for this request");
            }

            // Add initialization parameters directly to context.
            ctx.putAll(initParameters);
            startCmd.execute(ctx);
        } catch (Exception e) {
            // Execute the exception processing chain??
            throw new ProcessorException(e);
        }
    }

    public void destroy() {
        // Release our LogFactory and Log instances (if any)
        ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ChainRequestProcessor.class.getClassLoader();
        }
        try {
            LogFactory.release(classLoader);
        } catch (Throwable t) {
            ; // Servlet container doesn't have the latest version
              // of commons-logging-api.jar installed

            // :FIXME: Why is this dependent on the container's version of
            // commons-logging? Shouldn't this depend on the version packaged
            // with Struts?
            /*
              Reason: LogFactory.release(classLoader); was added as
              an attempt to investigate the OutOfMemory error reported on
              Bugzilla #14042. It was committed for version 1.136 by craigmcc
            */
        }

        CatalogFactory.clear();
        catalogFactory = null;
        catalog = null;
        startCmd = null;
    }

    /**
     * <p>Parse the configuration documents specified by the
     * <code>chainConfig</code> init-param to configure the default
     * {@link Catalog} that is registered in the {@link CatalogFactory}
     * instance for this application.</p>
     *
     * @throws ServletException if an error occurs.
     */
    protected void initChain(String paths, WebContext ctx) {

        // Parse the configuration file specified by path or resource
        try {
            if (paths != null) {
                chainConfig = paths;
            }

            ConfigParser parser = new ConfigParser();
            parser.setRuleSet(ruleSet);
            List urls = resolver.resolveList(chainConfig, ctx);
            URL resource = null;
            for (Iterator i = urls.iterator(); i.hasNext();) {
                resource = (URL) i.next();
                log.info("Loading chain catalog from " + resource);
                parser.parse(resource);
            }
        } catch (Throwable t) {
            log.error("Exception loading resources", t);
            throw new ProcessorException(t);
        }
    }

    protected void initCatalogFactory() {
        if (this.catalogFactory != null) {
            return;
        }
        this.catalogFactory = CatalogFactory.getInstance();

    }

}
