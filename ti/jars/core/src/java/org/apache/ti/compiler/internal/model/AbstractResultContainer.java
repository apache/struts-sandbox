/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.compiler.internal.model;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


abstract class AbstractResultContainer
        extends XWorkElementSupport
        implements XWorkResultContainer {

    private LinkedHashMap _forwards = new LinkedHashMap();


    public AbstractResultContainer(XWorkModuleConfigModel parentApp) {
        super(parentApp);
    }

    public AbstractResultContainer(AbstractResultContainer src) {
        super(src.getParentApp());
        _forwards = (LinkedHashMap) src._forwards.clone();
    }

    /**
     * Implemented for {@link XWorkResultContainer}.
     */
    public void addForward(XWorkResultModel newActionForward) {
        if (_forwards.containsKey(newActionForward.getName())) {
            // TODO: logging
//                if ( ! fwd.getPath().equals( newActionForward.getPath() ) )
//                {
//                    logger.warn( "Could not add forward \"" + newActionForward.getName() + "\", path=\""
//                                 + newActionForward.getPath() + "\" because there is already a forward with"
//                                 + " the same name (path=\"" + fwd.getPath() + "\")." );
//                }

            return;
        }

        _forwards.put(newActionForward.getName(), newActionForward);
    }

    public XWorkResultModel findForward(String forwardName) {
        return (XWorkResultModel) _forwards.get(forwardName);
    }

    public void writeForwards(XmlModelWriter xw, Element parentElement) {
        for (Iterator i = _forwards.values().iterator(); i.hasNext();) {
            XWorkResultModel fwd = (XWorkResultModel) i.next();
            fwd.writeXML(xw, parentElement);
        }
    }

    public XWorkResultModel[] getForwards() {
        return (XWorkResultModel[]) _forwards.values().toArray(new XWorkResultModel[ _forwards.size() ]);
    }

    public List getForwardsAsList() {
        List ret = new ArrayList();
        ret.addAll(_forwards.values());
        return ret;
    }

    protected XWorkResultModel getForward(String forwardName) {
        return (XWorkResultModel) _forwards.get(forwardName);
    }

    public void deleteForward(XWorkResultModel forward) {
        _forwards.remove(forward.getName());
    }
}
