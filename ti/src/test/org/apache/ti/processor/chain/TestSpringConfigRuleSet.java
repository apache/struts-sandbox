/*
 * $Id: TestSpringConfigRuleSet.java 171041 2005-05-20 03:50:06Z jmitchell $ 
 *
 * Copyright 2002-2004 The Apache Software Foundation.
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.config.ConfigParser;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Unit tests for the <code>org.apache.ti.config.SpringConfigRuleSet</code> class.
 *
 * @version $Rev: 171041 $ $Date: 2005-05-19 20:50:06 -0700 (Thu, 19 May 2005) $
 */
public class TestSpringConfigRuleSet extends TestCase {
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestSpringConfigRuleSet(String theName) {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(
            new String[] { TestSpringConfigRuleSet.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSpringConfigRuleSet.class);
    }
    

    public void testLoadConfig() throws Exception {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerBeanDefinition("springBean", new RootBeanDefinition(Bean.class));
        assertNotNull(factory.getBean("springBean"));
        
        ConfigParser parser = new ConfigParser();
        SpringConfigRuleSet ruleset = new SpringConfigRuleSet();
        ruleset.setBeanFactory(factory);
        parser.setRuleSet(ruleset);
        
        parser.parse(getClass().getResource("/org/apache/ti/processor/chain/chain-config.xml"));
        Catalog cat = CatalogFactory.getInstance().getCatalog("test");
        assertNotNull(cat);
        assertNotNull(cat.getCommand("normalBean"));
        assertNotNull(cat.getCommand("springBean"));
        
        assertNull(cat.getCommand("beandoesntexist"));
    }

}
