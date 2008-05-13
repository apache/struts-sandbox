/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.convention;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import static org.apache.struts2.convention.ReflectionTools.*;
import org.apache.struts2.convention.actions.DefaultResultPathAction;
import org.apache.struts2.convention.actions.NoAnnotationAction;
import org.apache.struts2.convention.actions.Skip;
import org.apache.struts2.convention.actions.action.ActionNameAction;
import org.apache.struts2.convention.actions.action.ActionNamesAction;
import org.apache.struts2.convention.actions.action.SingleActionNameAction;
import org.apache.struts2.convention.actions.action.TestAction;
import org.apache.struts2.convention.actions.namespace.ActionLevelNamespaceAction;
import org.apache.struts2.convention.actions.namespace.ClassLevelNamespaceAction;
import org.apache.struts2.convention.actions.namespace.PackageLevelNamespaceAction;
import org.apache.struts2.convention.actions.namespace2.DefaultNamespaceAction;
import org.apache.struts2.convention.actions.parentpackage.ClassLevelParentPackageAction;
import org.apache.struts2.convention.actions.parentpackage.PackageLevelParentPackageAction;
import org.apache.struts2.convention.actions.result.ActionLevelResultAction;
import org.apache.struts2.convention.actions.result.ActionLevelResultsAction;
import org.apache.struts2.convention.actions.result.ClassLevelResultAction;
import org.apache.struts2.convention.actions.result.ClassLevelResultsAction;
import org.apache.struts2.convention.actions.resultpath.ClassLevelResultPathAction;
import org.apache.struts2.convention.actions.resultpath.PackageLevelResultPathAction;
import org.apache.struts2.convention.actions.skip.Index;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

/**
 * <p>
 * This is a test for the package based name builder.
 * </p>
 */
public class PackageBasedActionConfigBuilderTest extends TestCase {
    public void testActionPackages() {
        run("org.apache.struts2.convention.actions", null, null);
    }

    public void testPackageLocators() {
        run(null, "actions", null);
    }

    private void run(String actionPackages, String packageLocators, String excludePackages) {
        PackageConfig strutsDefault = makePackageConfig("struts-default", null, null, "dispatcher",
            new ResultTypeConfig.Builder("dispatcher", ServletDispatcherResult.class.getName()).
                defaultResultParam("location").build());
        PackageConfig packageLevelParentPkg = makePackageConfig("package-level", null, null, null);
        PackageConfig classLevelParentPkg = makePackageConfig("class-level", null, null, null);

        PackageConfig rootPkg = makePackageConfig("org.apache.struts2.convention.actions#struts-default#",
            "", strutsDefault, null);
        PackageConfig actionPkg = makePackageConfig("org.apache.struts2.convention.actions.action#struts-default#/action",
            "/action", strutsDefault, null);
        PackageConfig idxPkg = makePackageConfig("org.apache.struts2.convention.actions.idx#struts-default#/idx",
            "/idx", strutsDefault, null);
        PackageConfig idx2Pkg = makePackageConfig("org.apache.struts2.convention.actions.idx.idx2#struts-default#/idx/idx2",
            "/idx/idx2", strutsDefault, null);
        PackageConfig packageLevelPkg = makePackageConfig("org.apache.struts2.convention.actions.parentpackage#package-level#/parentpackage",
            "/parentpackage", packageLevelParentPkg, null);
        PackageConfig differentPkg = makePackageConfig("org.apache.struts2.convention.actions.parentpackage#class-level#/parentpackage",
            "/parentpackage", classLevelParentPkg, null);
        PackageConfig pkgLevelNamespacePkg = makePackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/package-level",
            "/package-level", strutsDefault, null);
        PackageConfig classLevelNamespacePkg = makePackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/class-level",
            "/class-level", strutsDefault, null);
        PackageConfig actionLevelNamespacePkg = makePackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/action-level",
            "/action-level", strutsDefault, null);
        PackageConfig defaultNamespacePkg = makePackageConfig("org.apache.struts2.convention.actions.namespace2#struts-default#/namespace2",
            "/namespace2", strutsDefault, null);
        PackageConfig resultPkg = makePackageConfig("org.apache.struts2.convention.actions.result#struts-default#/result",
            "/result", strutsDefault, null);
        PackageConfig resultPathPkg = makePackageConfig("org.apache.struts2.convention.actions.resultpath#struts-default#/resultpath",
            "/resultpath", strutsDefault, null);
        PackageConfig skipPkg = makePackageConfig("org.apache.struts2.convention.actions.skip#struts-default#/skip",
            "/skip", strutsDefault, null);

        ResultMapBuilder resultMapBuilder = createStrictMock(ResultMapBuilder.class);
        checkOrder(resultMapBuilder, false);
        Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();

        /* org.apache.struts2.convention.actions.action */
        expect(resultMapBuilder.build(ActionNameAction.class, getAnnotation(ActionNameAction.class, "run1", Action.class), "action1", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNameAction.class, getAnnotation(ActionNameAction.class, "run2", Action.class), "action2", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNamesAction.class, getAnnotation(ActionNamesAction.class, "run", Actions.class).value()[0], "actions1", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNamesAction.class, getAnnotation(ActionNamesAction.class, "run", Actions.class).value()[1], "actions2", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(SingleActionNameAction.class, getAnnotation(SingleActionNameAction.class, "run", Action.class), "action", actionPkg)).andReturn(results);
        expect(resultMapBuilder.build(TestAction.class, null, "test", actionPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.idx */
        /* org.apache.struts2.convention.actions.idx.idx2 */
        expect(resultMapBuilder.build(org.apache.struts2.convention.actions.idx.Index.class, null, "index", idxPkg)).andReturn(results);
        expect(resultMapBuilder.build(org.apache.struts2.convention.actions.idx.idx2.Index.class, null, "index", idx2Pkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.namespace */
        expect(resultMapBuilder.build(ActionLevelNamespaceAction.class, getAnnotation(ActionLevelNamespaceAction.class, "execute", Action.class), "action", actionLevelNamespacePkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelNamespaceAction.class, null, "class-level-namespace", classLevelNamespacePkg)).andReturn(results);
        expect(resultMapBuilder.build(PackageLevelNamespaceAction.class, null, "package-level-namespace", pkgLevelNamespacePkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.namespace2 */
        expect(resultMapBuilder.build(DefaultNamespaceAction.class, null, "default-namespace", defaultNamespacePkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.parentpackage */
        expect(resultMapBuilder.build(PackageLevelParentPackageAction.class, null, "package-level-parent-package", packageLevelPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelParentPackageAction.class, null, "class-level-parent-package", differentPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.result */
        expect(resultMapBuilder.build(ClassLevelResultAction.class, null, "class-level-result", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelResultsAction.class, null, "class-level-results", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelResultAction.class, getAnnotation(ActionLevelResultAction.class, "execute", Action.class), "action-level-result", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelResultsAction.class, getAnnotation(ActionLevelResultsAction.class, "execute", Action.class), "action-level-results", resultPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.resultpath */
        expect(resultMapBuilder.build(ClassLevelResultPathAction.class, null, "class-level-result-path", resultPathPkg)).andReturn(results);
        expect(resultMapBuilder.build(PackageLevelResultPathAction.class, null, "package-level-result-path", resultPathPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions */
        expect(resultMapBuilder.build(NoAnnotationAction.class, null, "no-annotation", rootPkg)).andReturn(results);
        expect(resultMapBuilder.build(DefaultResultPathAction.class, null, "default-result-path", rootPkg)).andReturn(results);
        expect(resultMapBuilder.build(Skip.class, null, "skip", rootPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.skip */
        expect(resultMapBuilder.build(Index.class, null, "index", skipPkg)).andReturn(results);

        EasyMock.replay(resultMapBuilder);

        Configuration configuration = new DefaultConfiguration();
        configuration.addPackageConfig("struts-default", strutsDefault);
        configuration.addPackageConfig("package-level", packageLevelParentPkg);
        configuration.addPackageConfig("class-level", classLevelParentPkg);

        ActionNameBuilder actionNameBuilder = new SEOActionNameBuilder("true", "-");
        ObjectFactory of = new ObjectFactory();
        PackageBasedActionConfigBuilder builder = new PackageBasedActionConfigBuilder(configuration,
            actionNameBuilder, resultMapBuilder, of, "false", "struts-default");
        if (actionPackages != null) {
            builder.setActionPackages(actionPackages);
        }
        if (packageLocators != null) {
            builder.setPackageLocators(packageLocators);
        }
        if (excludePackages != null) {
            builder.setExcludePackages(excludePackages);
        }
        builder.buildActionConfigs();
        verify(resultMapBuilder);

        /* org.apache.struts2.convention.actions.action */
        PackageConfig pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.action#struts-default#/action");
        assertNotNull(pkgConfig);
        assertEquals(6, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action1", ActionNameAction.class, "run1", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action2", ActionNameAction.class, "run2", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "actions1", ActionNamesAction.class, "run", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "actions2", ActionNamesAction.class, "run", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action", SingleActionNameAction.class, "run", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "test", TestAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.idx */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.idx#struts-default#/idx");
        assertNotNull(pkgConfig);
        assertEquals(3, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "", org.apache.struts2.convention.actions.idx.Index.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "index", org.apache.struts2.convention.actions.idx.Index.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "idx2", org.apache.struts2.convention.actions.idx.idx2.Index.class, "execute",
            "org.apache.struts2.convention.actions.idx.idx2#struts-default#/idx/idx2");

        /* org.apache.struts2.convention.actions.idx.idx2 */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.idx.idx2#struts-default#/idx/idx2");
        assertNotNull(pkgConfig);
        assertEquals(2, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "", org.apache.struts2.convention.actions.idx.idx2.Index.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "index", org.apache.struts2.convention.actions.idx.idx2.Index.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace action level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/action-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action", ActionLevelNamespaceAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace class level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/class-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-namespace", ClassLevelNamespaceAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace package level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/package-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "package-level-namespace", PackageLevelNamespaceAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.namespace2 */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace2#struts-default#/namespace2");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "default-namespace", DefaultNamespaceAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.parentpackage class level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.parentpackage#class-level#/parentpackage");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-parent-package", ClassLevelParentPackageAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.parentpackage package level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.parentpackage#package-level#/parentpackage");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "package-level-parent-package", PackageLevelParentPackageAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.result */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.result#struts-default#/result");
        assertNotNull(pkgConfig);
        assertEquals(4, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-result", ClassLevelResultAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "class-level-results", ClassLevelResultsAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action-level-result", ActionLevelResultAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "action-level-results", ActionLevelResultsAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions.resultpath */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.resultpath#struts-default#/resultpath");
        assertNotNull(pkgConfig);
        assertEquals(2, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-result-path", ClassLevelResultPathAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "package-level-result-path", PackageLevelResultPathAction.class, "execute", pkgConfig.getName());

        /* org.apache.struts2.convention.actions */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions#struts-default#");
        assertNotNull(pkgConfig);
        System.out.println("actions " + pkgConfig.getActionConfigs());
        assertEquals(4, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "no-annotation", NoAnnotationAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "default-result-path", DefaultResultPathAction.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "skip", Skip.class, "execute", pkgConfig.getName());
        verifyActionConfig(pkgConfig, "idx", org.apache.struts2.convention.actions.idx.Index.class, "execute",
            "org.apache.struts2.convention.actions.idx#struts-default#/idx");
    }

    private void verifyActionConfig(PackageConfig pkgConfig, String actionName, Class<?> actionClass,
            String methodName, String packageName) {
        ActionConfig ac = pkgConfig.getAllActionConfigs().get(actionName);
        assertNotNull(ac);
        assertEquals(actionClass.getName(), ac.getClassName());
        assertEquals(methodName, ac.getMethodName());
        assertEquals(packageName, ac.getPackageName());
    }

    private PackageConfig makePackageConfig(String name, String namespace, PackageConfig parent,
            String defaultResultType, ResultTypeConfig... results) {
        PackageConfig.Builder builder = new PackageConfig.Builder(name);
        if (namespace != null) {
            builder.namespace(namespace);
        }
        if (parent != null) {
            builder.addParent(parent);
        }
        if (defaultResultType != null) {
            builder.defaultResultType(defaultResultType);
        }
        for (ResultTypeConfig result : results) {
            builder.addResultTypeConfig(result);
        }

        return new MyPackageConfig(builder.build());
    }

    public class MyPackageConfig extends PackageConfig {
        protected MyPackageConfig(PackageConfig packageConfig) {
            super(packageConfig);
        }

        public boolean equals(Object obj) {
            PackageConfig other = (PackageConfig) obj;
            return getName().equals(other.getName()) && getNamespace().equals(other.getNamespace()) &&
                getParents().get(0) == other.getParents().get(0) && getParents().size() == other.getParents().size();
        }
    }
}