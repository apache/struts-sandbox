/*
 * $Id: CreateJavaBeanForm.java 230535 2005-08-06 07:56:40Z mrdon $
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

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Tries to instantiate the form class's no-arg constructor.
 */
public class CreateJavaBeanForm implements Command {

    private static final Log log = LogFactory.getLog(CreateJavaBeanForm.class);

    public boolean execute(Context origctx) throws Exception {
        log.debug("Creating JavaBean form");
        
        Class cls = (Class) origctx.get(CreateFormChain.FORM_CLASS);
        try {
            Object o = cls.newInstance();
            origctx.put(CreateFormChain.FORM_OBJECT, o);
            return true;
        } catch (Exception ex) {
            log.warn("Unable to create object from class "+cls, ex);
        }
        return false;
    }
}
