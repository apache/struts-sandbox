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

import org.easymock.EasyMock;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.apache.struts2.convention.actions.ResultLocationAnnotationAction;
import org.apache.struts2.convention.actions.result.ResultsAction;
import org.apache.struts2.convention.actions.NoAnnotationAction;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;

/**
 * <p>
 * This class tests the simple result map builder.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultResultMapBuilderTest {
    @Test
    public void testBuild() throws Exception {
        ServletContext context = mockServletContext();

        // Test with a slash
        PackageConfig packageConfig = createPackageConfig("/namespace");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/location");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, "action", packageConfig);
        verify(context, results, false);

        // Test without a slash
        context = mockServletContext();
        packageConfig = createPackageConfig("namespace");
        builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/location");
        results = builder.build(NoAnnotationAction.class, "action", packageConfig);
        verify(context, results, false);
    }

    @Test
    public void testNull() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(null);
        EasyMock.replay(context);

        // Test with a slash
        PackageConfig packageConfig = createPackageConfig("/namespace");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/location");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, "action", packageConfig);
        assertEquals(0, results.size());
        EasyMock.verify(context);
    }

    @Test
    public void testBuildBaseResultLocation() throws Exception {
        ServletContext context = mockServletContext();

        // Test with a a base result location
        PackageConfig packageConfig = createPackageConfig("/namespace");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/notused");
        Map<String, ResultConfig> results = builder.build(ResultLocationAnnotationAction.class,
            "action", packageConfig);
        verify(context, results, false);
    }

    @Test
    public void testBuildSingleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfig("/namespace");

        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/location");
        Map<String, ResultConfig> results = builder.build(SingleAnnotationAction.class, "action", packageConfig);
        assertEquals(1, results.size());
        assertEquals("success", results.get("success").getName());
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("/WEB-INF/location/namespace/foo.jsp", results.get("success").getParams().get("location"));
        assertEquals("value", results.get("success").getParams().get("key"));
        assertEquals("value1", results.get("success").getParams().get("key1"));
        EasyMock.verify(context);
    }

    @Test
    public void testResourceExtensionType() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        resources.add("/WEB-INF/location/namespace/action.ftl");
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfig("/namespace");

        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/location");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, "action", packageConfig);
        assertEquals(3, results.size());
        assertEquals("success", results.get("success").getName());
        assertEquals(1, results.get("success").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action.ftl", results.get("success").getParams().get("location"));
        assertEquals("org.apache.struts2.views.freemarker.FreemarkerResult", results.get("success").getClassName());
        EasyMock.verify(context);
    }

    @Test
    public void testBuildMultipleResultAnnotation() throws Exception {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);

        PackageConfig packageConfig = createPackageConfig("/namespace");

        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/location");
        Map<String, ResultConfig> results = builder.build(ResultsAction.class, "action", packageConfig);
        verify(context, results, true);
    }

    @Test
    public void testNotDefaultParams() throws Exception {
        ServletContext context = mockServletContext();

        ResultTypeConfig resultType = new ResultTypeConfig("dispatcher",
            "org.apache.struts2.dispatcher.ServletDispatcherResult", "huh?");
        PackageConfig packageConfig = new PackageConfig("package", "/namespace", false);
        packageConfig.setDefaultResultType("dispatcher");
        packageConfig.addResultTypeConfig(resultType);

        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/location");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, "action", packageConfig);
        assertEquals(4, results.size());
        assertEquals("input", results.get("input").getName());
        assertEquals("error", results.get("error").getName());
        assertEquals("success", results.get("success").getName());
        assertEquals("failure", results.get("failure").getName());
        assertEquals(1, results.get("success").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-success.jsp", results.get("success").getParams().get("location"));
        assertEquals(1, results.get("failure").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-failure.jsp", results.get("failure").getParams().get("location"));
        assertEquals(1, results.get("input").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action.jsp", results.get("input").getParams().get("location"));
        assertEquals(1, results.get("error").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action.jsp", results.get("error").getParams().get("location"));
        EasyMock.verify(context);
    }

    @Test
    public void testClassPath() throws Exception {
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);

        ResultTypeConfig resultType = new ResultTypeConfig("freemarker",
            "org.apache.struts2.dispatcher.ServletDispatcherResult", "huh?");
        PackageConfig packageConfig = new PackageConfig("package", "", false);
        packageConfig.setDefaultResultType("dispatcher");
        packageConfig.addResultTypeConfig(resultType);

        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, "dispatcher,velocity,freemarker");
        builder.setBaseResultLocation("/WEB-INF/component");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, "list", packageConfig);
        assertEquals(4, results.size());
        assertEquals("input", results.get("input").getName());
        assertEquals("error", results.get("error").getName());
        assertEquals("success", results.get("success").getName());
        assertEquals("foo", results.get("foo").getName());
        assertEquals(1, results.get("success").getParams().size());
        assertEquals("/WEB-INF/component/list.ftl", results.get("success").getParams().get("location"));
        assertEquals(1, results.get("input").getParams().size());
        assertEquals("/WEB-INF/component/list.ftl", results.get("input").getParams().get("location"));
        assertEquals(1, results.get("error").getParams().size());
        assertEquals("/WEB-INF/component/list.ftl", results.get("error").getParams().get("location"));
        assertEquals(1, results.get("foo").getParams().size());
        assertEquals("/WEB-INF/component/list-foo.ftl", results.get("foo").getParams().get("location"));
    }

    private PackageConfig createPackageConfig(String namespace) {
        ResultTypeConfig resultType = new ResultTypeConfig("dispatcher",
            "org.apache.struts2.dispatcher.ServletDispatcherResult", "huh?");
        resultType.addParam("key", "value");
        resultType.addParam("key1", "value1");

        ResultTypeConfig redirect  = new ResultTypeConfig("redirect-action",
            "org.apache.struts2.dispatcher.ServletActionRedirectResult", "huh?");

        ResultTypeConfig ftlResultType = new ResultTypeConfig("freemarker",
            "org.apache.struts2.views.freemarker.FreemarkerResult", "huh?");

        PackageConfig packageConfig = new PackageConfig("package", namespace, false);
        packageConfig.setDefaultResultType("dispatcher");
        packageConfig.addResultTypeConfig(resultType);
        packageConfig.addResultTypeConfig(redirect);
        packageConfig.addResultTypeConfig(ftlResultType);
        return packageConfig;
    }

    private ServletContext mockServletContext() {
        ServletContext context = EasyMock.createStrictMock(ServletContext.class);

        // Setup some mock jsps
        Set<String> resources = new HashSet<String>();
        resources.add("/WEB-INF/location/namespace/action.jsp");
        resources.add("/WEB-INF/location/namespace/action-success.jsp");
        resources.add("/WEB-INF/location/namespace/action-failure.jsp");
        EasyMock.expect(context.getResourcePaths("/WEB-INF/location/namespace/")).andReturn(resources);
        EasyMock.replay(context);
        return context;
    }

    private void verify(ServletContext context, Map<String, ResultConfig> results, boolean redirect) {
        assertEquals(4, results.size());
        assertEquals("success", results.get("success").getName());
        assertEquals("input", results.get("input").getName());
        assertEquals("error", results.get("error").getName());
        assertEquals("failure", results.get("failure").getName());
        assertEquals(3, results.get("success").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-success.jsp", results.get("success").getParams().get("location"));
        assertEquals("value", results.get("success").getParams().get("key"));
        assertEquals("value1", results.get("success").getParams().get("key1"));
        assertEquals(3, results.get("failure").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action-failure.jsp", results.get("failure").getParams().get("location"));
        assertEquals("value", results.get("failure").getParams().get("key"));
        assertEquals("value1", results.get("failure").getParams().get("key1"));

        if (redirect) {
            assertEquals(1, results.get("input").getParams().size());
            assertEquals("foo.action", results.get("input").getParams().get("actionName"));
        } else {
            assertEquals(3, results.get("input").getParams().size());
            assertEquals("/WEB-INF/location/namespace/action.jsp", results.get("input").getParams().get("location"));
            assertEquals("value", results.get("input").getParams().get("key"));
            assertEquals("value1", results.get("input").getParams().get("key1"));
        }

        assertEquals(3, results.get("error").getParams().size());
        assertEquals("/WEB-INF/location/namespace/action.jsp", results.get("error").getParams().get("location"));
        assertEquals("value", results.get("error").getParams().get("key"));
        assertEquals("value1", results.get("error").getParams().get("key1"));
        EasyMock.verify(context);
    }
}