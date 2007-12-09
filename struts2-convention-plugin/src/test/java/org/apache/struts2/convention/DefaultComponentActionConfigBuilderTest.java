/*
 * Copyright (c) 2007, Inversoft and Texturemedia, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.texturemedia.smarturls;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;

import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.easymock.EasyMock;
import static org.junit.Assert.*;
import org.junit.Test;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

/**
 * <p>
 * This class tests the XMLComponentActionConfigBuilder.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultComponentActionConfigBuilderTest {
    @Test
    public void testBuildActionConfigs() throws Exception {
        PackageConfig pkgConfig = new PackageConfig("foo-bar", "/foo", false);
        pkgConfig.addResultTypeConfig(new ResultTypeConfig("dispatcher",
            ServletDispatcherResult.class.getName(), "location"));
        pkgConfig.setDefaultResultType("dispatcher");
        Configuration config = new DefaultConfiguration();
        config.addPackageConfig("foo-bar", pkgConfig);

        Set<String> resources = new HashSet<String>();
        resources.add("/WEB-INF/component/test-success.jsp");
        resources.add("/WEB-INF/component/test-failure.jsp");

        Set<String> resources2 = new HashSet<String>();
        resources2.add("/WEB-INF/component/test2/test2-success.jsp");
        resources2.add("/WEB-INF/component/test2/test2-failure.jsp");

        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(context.getResourcePaths("/WEB-INF/component/")).andReturn(resources);
        EasyMock.expect(context.getResourcePaths("/WEB-INF/component/test2/")).andReturn(resources2);
        EasyMock.replay(context);

        ObjectFactory of = new ObjectFactory();
        ResultMapBuilder rmb = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        rmb.setBaseResultLocation("/should-be-overridden");
        ActionConfigBuilder acb = new PackageBasedActionConfigBuilder(config, new SEOActionNameBuilder("true", "_"),
            rmb, of, "false");
        DefaultComponentActionConfigBuilder build = new DefaultComponentActionConfigBuilder(acb,
            new DOMComponentConfiguration());
        build.buildActionConfigs();

        pkgConfig = config.getPackageConfig("org.texturemedia.component.test#foo-bar#");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getAllActionConfigs().size());

        ActionConfig action = pkgConfig.getAllActionConfigs().get("test");
        assertEquals("org.texturemedia.component.test.TestAction", action.getClassName());
        assertNull(action.getMethodName());
        assertTrue(action instanceof SmartURLsActionConfig);
        assertEquals("/WEB-INF/component", ((SmartURLsActionConfig) action).getBaseResultLocation());
        assertEquals("org.texturemedia.component.test#foo-bar#", action.getPackageName());

        Map<String, ResultConfig> results = action.getResults();
        assertEquals(2, results.size());
        assertEquals("/WEB-INF/component/test-success.jsp", results.get("success").getParams().get("location"));
        assertEquals("/WEB-INF/component/test-failure.jsp", results.get("failure").getParams().get("location"));

        // -------------------------------------------------------------------------------------- //
        pkgConfig = config.getPackageConfig("org.texturemedia.component.test2#foo-bar#/test2");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getAllActionConfigs().size());

        action = pkgConfig.getAllActionConfigs().get("test2");
        assertEquals("org.texturemedia.component.test2.Test2Action", action.getClassName());
        assertNull(action.getMethodName());
        assertTrue(action instanceof SmartURLsActionConfig);
        assertEquals("/WEB-INF/component", ((SmartURLsActionConfig) action).getBaseResultLocation());
        assertEquals("org.texturemedia.component.test2#foo-bar#/test2", action.getPackageName());

        results = action.getResults();
        assertEquals(2, results.size());
        assertEquals("/WEB-INF/component/test2/test2-success.jsp", results.get("success").getParams().get("location"));
        assertEquals("/WEB-INF/component/test2/test2-failure.jsp", results.get("failure").getParams().get("location"));

        EasyMock.verify(context);
    }
}