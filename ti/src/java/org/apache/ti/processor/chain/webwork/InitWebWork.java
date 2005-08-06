/*
 * $Id: Init.java 230535 2005-08-06 07:56:40Z mrdon $
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
package org.apache.ti.processor.chain.webwork;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;

import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.util.FileManager;
import com.opensymphony.xwork.util.LocalizedTextUtil;
import com.opensymphony.webwork.config.Configuration;

/**
 *  Initializes  by replacing default factories
 */
public class InitWebWork implements Command {

    private static final Log log = LogFactory.getLog(InitWebWork.class);

    public boolean execute(Context origctx) {
        log.debug("Initializing webwork");
        
        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/webwork/webwork-messages");

        //check for configuration reloading
        if ("true".equalsIgnoreCase(Configuration.getString("webwork.configuration.xml.reload"))) {
            FileManager.setReloadingConfigs(true);
        }

        return false;
    }


}
