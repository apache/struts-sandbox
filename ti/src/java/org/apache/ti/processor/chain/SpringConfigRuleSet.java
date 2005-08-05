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


import java.util.List;

import org.apache.commons.chain.config.ConfigRuleSet;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.Rule;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.xml.sax.Attributes;


/**
 *  Builds on commons-chain rule set to first try to locate the command name in
 *  a bean factory before processing the command class name.
 */
public class SpringConfigRuleSet extends ConfigRuleSet implements BeanFactoryAware {

    protected BeanFactory factory;

    public void setBeanFactory(BeanFactory factory) {
        this.factory = factory;
    }

    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with
     * our namespace URI (if any).  This method should only be called
     * by a Digester instance.</p>
     *
     * @param digester Digester instance to which the new Rule instances
     *  should be added.
     */
    public void addRuleInstances(Digester digester) {

        super.addRuleInstances(digester);
        String pattern = "*/" + getCommandElement();

        // Add rules for a command element
        Rule rule = new ObjectCreateRule(digester, null, getClassAttribute()) {
            public void begin(Attributes attrs) throws Exception {
                String name = attrs.getValue(getNameAttribute());
                System.out.println("looking up "+name);
                if (name != null) {
                    if (factory.containsBean(name)) {
                        getDigester().push(factory.getBean(name));
                    } else {
                        super.begin(attrs);
                    }
                } else {
                    throw new IllegalArgumentException("Unable to locate class name");
                }
            }
        };
        rule.setDigester(digester);

        List list = digester.getRules().match(null, pattern);
        list.set(0, rule);
    }


}
