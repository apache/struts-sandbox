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

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;
import org.apache.ti.config.mapper.ActionMapper;
import org.apache.ti.config.mapper.ActionMapping;

/**
 *  Creates an ActionMapping and stores it in the context.
 */
public class CreateActionMapping implements Command {

    protected ActionMapper actionMapper;

    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    public boolean execute(Context origctx) {
        WebContext ctx = (WebContext) origctx;

        ActionMapping mapping = actionMapper.getMapping(ctx);
        ctx.put("actionMapping", mapping);
        return false;
    }


}
