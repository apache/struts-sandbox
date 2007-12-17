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
 *
 * @author Brian Pontarelli
 */
public class PackageBasedActionConfigBuilderTest extends TestCase {
    public void testBuild() {
        MyPackageConfig strutsDefault = new MyPackageConfig();
        strutsDefault.setName("struts-default");

        MyPackageConfig fooPackagePkg = new MyPackageConfig();
        fooPackagePkg.setName("foo-package");

        MyPackageConfig packageLevelPackagePkg = new MyPackageConfig();
        packageLevelPackagePkg.setName("package-level");

        MyPackageConfig differentPackagePkg = new MyPackageConfig();
        differentPackagePkg.setName("different-package");

        Configuration configuration = new DefaultConfiguration();
        strutsDefault.addResultTypeConfig(new ResultTypeConfig("dispatcher", ServletDispatcherResult.class.getName(), "location"));
        strutsDefault.setDefaultResultType("dispatcher");
        configuration.addPackageConfig("struts-default", strutsDefault);
        configuration.addPackageConfig("foo-package", fooPackagePkg);
        configuration.addPackageConfig("package-level", packageLevelPackagePkg);
        configuration.addPackageConfig("different-package", differentPackagePkg);

        ActionNameBuilder actionNameBuilder = new SEOActionNameBuilder("true", "_");

        MyPackageConfig rootPkg = new MyPackageConfig();
        rootPkg.setName("org.apache.struts2.convention.actions#struts-default#");
        rootPkg.setNamespace("");
        rootPkg.addParent(strutsDefault);

        MyPackageConfig subPkg = new MyPackageConfig();
        subPkg.setName("org.apache.struts2.convention.actions.action#struts-default#/action");
        subPkg.setNamespace("/action");
        subPkg.addParent(strutsDefault);

        MyPackageConfig subParentPkg = new MyPackageConfig();
        subParentPkg.setName("org.apache.struts2.convention.actions.action#foo-package#/action");
        subParentPkg.setNamespace("/action");
        subParentPkg.addParent(fooPackagePkg);

        MyPackageConfig fooPkg = new MyPackageConfig();
        fooPkg.setName("org.apache.struts2.convention.actions.action#struts-default#/foo");
        fooPkg.setNamespace("/foo");
        fooPkg.addParent(strutsDefault);

        MyPackageConfig idxPkg = new MyPackageConfig();
        idxPkg.setName("org.apache.struts2.convention.actions.idx#struts-default#/idx");
        idxPkg.setNamespace("/idx");
        idxPkg.addParent(strutsDefault);

        MyPackageConfig idx2Pkg = new MyPackageConfig();
        idx2Pkg.setName("org.apache.struts2.convention.actions.idx.idx2#struts-default#/idx/idx2");
        idx2Pkg.setNamespace("/idx/idx2");
        idx2Pkg.addParent(strutsDefault);

        MyPackageConfig packageLevelPkg = new MyPackageConfig();
        packageLevelPkg.setName("org.apache.struts2.convention.actions.parentpackage#package-level#/parentpackage");
        packageLevelPkg.setNamespace("/parentpackage");
        packageLevelPkg.addParent(packageLevelPackagePkg);

        MyPackageConfig differentPkg = new MyPackageConfig();
        differentPkg.setName("org.apache.struts2.convention.actions.parentpackage#different-package#/parentpackage");
        differentPkg.setNamespace("/parentpackage");
        differentPkg.addParent(differentPackagePkg);

        MyPackageConfig skipPkg = new MyPackageConfig();
        skipPkg.setName("org.apache.struts2.convention.actions.skip#struts-default#/skip");
        skipPkg.setNamespace("/skip");
        skipPkg.addParent(strutsDefault);

        MyPackageConfig pkgLevelNamespacePkg = new MyPackageConfig();
        pkgLevelNamespacePkg.setName("org.apache.struts2.convention.actions.namespace#struts-default#/package-level");
        pkgLevelNamespacePkg.setNamespace("/package-level");
        pkgLevelNamespacePkg.addParent(strutsDefault);

        MyPackageConfig classLevelNamespacePkg = new MyPackageConfig();
        classLevelNamespacePkg.setName("org.apache.struts2.convention.actions.namespace#struts-default#/class-level");
        classLevelNamespacePkg.setNamespace("/class-level");
        classLevelNamespacePkg.addParent(strutsDefault);

        MyPackageConfig actionLevelNamespacePkg = new MyPackageConfig();
        actionLevelNamespacePkg.setName("org.apache.struts2.convention.actions.namespace#struts-default#/action-level");
        actionLevelNamespacePkg.setNamespace("/action-level");
        actionLevelNamespacePkg.addParent(strutsDefault);

        MyPackageConfig defaultNamespacePkg = new MyPackageConfig();
        defaultNamespacePkg.setName("org.apache.struts2.convention.actions.namespace2#struts-default#/namespace2");
        defaultNamespacePkg.setNamespace("/namespace2");
        defaultNamespacePkg.addParent(strutsDefault);

        MyPackageConfig resultPkg = new MyPackageConfig();
        resultPkg.setName("org.apache.struts2.convention.actions.result#struts-default#/result");
        resultPkg.setNamespace("/result");
        resultPkg.addParent(strutsDefault);

        MyPackageConfig resultPathPkg = new MyPackageConfig();
        resultPathPkg.setName("org.apache.struts2.convention.actions.resultpath#struts-default#/resultpath");
        resultPathPkg.setNamespace("/resultpath");
        resultPathPkg.addParent(strutsDefault);

        ResultMapBuilder resultMapBuilder = createNiceMock(ResultMapBuilder.class);
        Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();

        /* org.apache.struts2.convention.actions.action */
        expect(resultMapBuilder.build(ActionNameAction.class, getAnnotation(ActionNameAction.class, "run1", Action.class), "action1", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNameAction.class, getAnnotation(ActionNameAction.class, "run2", Action.class), "action2", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNamesAction.class, getAnnotation(ActionNamesAction.class, "run", Actions.class).value()[0], "actions1", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNamesAction.class, getAnnotation(ActionNamesAction.class, "run", Actions.class).value()[1], "actions2", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(SingleActionNameAction.class, getAnnotation(SingleActionNameAction.class, "run", Action.class), "action", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(TestAction.class, null, "test", subPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.idx */
        /* org.apache.struts2.convention.actions.idx.idx2 */
        expect(resultMapBuilder.build(org.apache.struts2.convention.actions.idx.Index.class, null, "index", idxPkg)).andReturn(results);
        expect(resultMapBuilder.build(org.apache.struts2.convention.actions.idx.idx2.Index.class, null, "index", idx2Pkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.namespace */
        expect(resultMapBuilder.build(ActionLevelNamespaceAction.class, getAnnotation(ActionLevelNamespaceAction.class, "execute", Action.class), "action", actionLevelNamespacePkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelNamespaceAction.class, null, "class-level", classLevelNamespacePkg)).andReturn(results);
        expect(resultMapBuilder.build(PackageLevelNamespaceAction.class, null, "package-level", pkgLevelNamespacePkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.namespace2 */
        expect(resultMapBuilder.build(DefaultNamespaceAction.class, null, "default-namespace", defaultNamespacePkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.parentpackage */
        expect(resultMapBuilder.build(PackageLevelParentPackageAction.class, null, "package-level-parent-package", packageLevelPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelParentPackageAction.class, null, "class-level-parent-package", differentPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.result */
        expect(resultMapBuilder.build(ClassLevelResultAction.class, null, "class-level-result", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelResultsAction.class, null, "class-level-results", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelResultAction.class, null, "action-level-result", resultPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelResultsAction.class, null, "action-level-results", resultPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions.resultpath */
        expect(resultMapBuilder.build(ClassLevelResultPathAction.class, null, "class-level-result-path", resultPathPkg)).andReturn(results);
        expect(resultMapBuilder.build(PackageLevelResultPathAction.class, null, "package-level-result-path", resultPathPkg)).andReturn(results);

        /* org.apache.struts2.convention.actions */
        expect(resultMapBuilder.build(NoAnnotationAction.class, null, "no-annotation", rootPkg)).andReturn(results);
        expect(resultMapBuilder.build(DefaultResultPathAction.class, null, "default-result-path", rootPkg)).andReturn(results);
        expect(resultMapBuilder.build(Skip.class, null, "skip", rootPkg)).andReturn(results);

        EasyMock.replay(resultMapBuilder);

        ObjectFactory of = new ObjectFactory();
        PackageBasedActionConfigBuilder builder = new PackageBasedActionConfigBuilder(configuration,
            actionNameBuilder, resultMapBuilder, of, "false", "org.apache.struts2.convention.actions",
            null, null, "struts-default", "/default-result-path");
        builder.buildActionConfigs();
        verify(resultMapBuilder);

        /* org.apache.struts2.convention.actions.action */
        PackageConfig pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.action#struts-default#/action");
        assertNotNull(pkgConfig);
        assertEquals(6, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action1", ActionNameAction.class, "run1", "/default-result-path");
        verifyActionConfig(pkgConfig, "action2", ActionNameAction.class, "run2", "/default-result-path");
        verifyActionConfig(pkgConfig, "actions1", ActionNamesAction.class, "run", "/default-result-path");
        verifyActionConfig(pkgConfig, "actions2", ActionNamesAction.class, "run", "/default-result-path");
        verifyActionConfig(pkgConfig, "action", SingleActionNameAction.class, "run", "/default-result-path");
        verifyActionConfig(pkgConfig, "test", TestAction.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.idx */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.idx#struts-default#/idx");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "", org.apache.struts2.convention.actions.idx.Index.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.idx.idx2 */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.idx.idx2#struts-default#/idx/idx2");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "", org.apache.struts2.convention.actions.idx.idx2.Index.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.namespace action level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/action-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "action", ActionLevelNamespaceAction.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.namespace class level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/class-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-namespace", ClassLevelNamespaceAction.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.namespace package level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace#struts-default#/package-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "package-level-namespace", PackageLevelNamespaceAction.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.namespace2 */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.namespace2#struts-default#/namespace2");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "default-namespace", DefaultNamespaceAction.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.parentpackage class level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.parentpackage#class-level#/parentpackage");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-parent-package", ClassLevelParentPackageAction.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.parentpackage package level */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.parentpackage#package-level#/parentpackage");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "package-level-parent-package", PackageLevelParentPackageAction.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.result */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.result#struts-default#/result");
        assertNotNull(pkgConfig);
        assertEquals(4, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-result", ClassLevelResultAction.class, "execute", "/default-result-path");
        verifyActionConfig(pkgConfig, "class-level-results", ClassLevelResultsAction.class, "execute", "/default-result-path");
        verifyActionConfig(pkgConfig, "action-level-result", ActionLevelResultAction.class, "execute", "/default-result-path");
        verifyActionConfig(pkgConfig, "action-level-results", ActionLevelResultsAction.class, "execute", "/default-result-path");

        /* org.apache.struts2.convention.actions.resultpath */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions.resultpath#struts-default#/resultpath");
        assertNotNull(pkgConfig);
        assertEquals(2, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "class-level-result-path", ClassLevelResultPathAction.class, "execute", "/class-level");
        verifyActionConfig(pkgConfig, "package-level-result-path", PackageLevelResultPathAction.class, "execute", "/package-level");

        /* org.apache.struts2.convention.actions */
        pkgConfig = configuration.getPackageConfig("org.apache.struts2.convention.actions#struts-default#/");
        assertNotNull(pkgConfig);
        assertEquals(3, pkgConfig.getActionConfigs().size());
        verifyActionConfig(pkgConfig, "no-annotation", NoAnnotationAction.class, "execute", "/default-result-path");
        verifyActionConfig(pkgConfig, "default-result-path", DefaultResultPathAction.class, "execute", "/default-result-path");
        verifyActionConfig(pkgConfig, "skip", DefaultResultPathAction.class, "execute", "/default-result-path");
    }

    private void verifyActionConfig(PackageConfig pkgConfig, String actionName, Class<?> actionClass,
            String methodName, String resultPath) {
        ActionConfig ac = pkgConfig.getAllActionConfigs().get(actionName);
        assertNotNull(ac);
        assertEquals(actionClass.getName(), ac.getClassName());
        assertEquals(methodName, ac.getMethodName());
        assertTrue(ac instanceof ConventionActionConfig);
        assertEquals(resultPath, ((ConventionActionConfig) ac).getBaseResultLocation());
        assertEquals(pkgConfig.getName(), ac.getPackageName());
    }

    /**
     * I need thi because I couldn't fucking figure out why the
     * PackageConfig from XWork wasn't being equal.
     */
    public class MyPackageConfig extends PackageConfig {
        public boolean equals(Object o) {
            PackageConfig config = (PackageConfig) o;
            boolean ret = config.getName().equals(getName()) && config.getNamespace().equals(getNamespace()) &&
                config.getParents().get(0) == getParents().get(0) && config.getParents().size() == getParents().size();
            System.out.println("ret is " + ret);
            return ret;
        }
    }
}