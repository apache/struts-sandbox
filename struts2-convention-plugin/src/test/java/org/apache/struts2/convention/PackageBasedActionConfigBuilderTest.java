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

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.texturemedia.smarturls.actions.BaseResultLocationAnnotationAction;
import org.texturemedia.smarturls.actions.MultipleAnnotationAction;
import org.texturemedia.smarturls.actions.NoAnnotationAction;
import org.texturemedia.smarturls.actions.SingleAnnotationAction;
import org.texturemedia.smarturls.actions.Skip;
import org.texturemedia.smarturls.actions.namespace.DefaultNamespaceAction;
import org.texturemedia.smarturls.actions.namespace.ClassLevelAction;
import org.texturemedia.smarturls.actions.namespace.ActionLevelAction;
import org.texturemedia.smarturls.actions.parentpackage.DefaultAction;
import org.texturemedia.smarturls.actions.parentpackage.DifferentParentPackageAction;
import org.texturemedia.smarturls.actions.nested.ActionNameAction;
import org.texturemedia.smarturls.actions.nested.ActionNamesAction;
import org.texturemedia.smarturls.actions.nested.NamespaceAction;
import org.texturemedia.smarturls.actions.nested.ParentPackageAction;
import org.texturemedia.smarturls.actions.nested.TestAction;

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
public class PackageBasedActionConfigBuilderTest {
    @Test
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

//        Configuration configuration = createNiceMock(Configuration.class);
//        expect(configuration.getPackageConfig("struts-default")).andReturn(strutsDefault);
//        expectLastCall().times(8);
//        expect(configuration.getPackageConfig("foo-package")).andReturn(fooPackagePkg);
//
//        configuration.addPackageConfig(isA(String.class), isA(PackageConfig.class));
//        configuration.rebuildRuntimeConfiguration();
//        replay(configuration);

        ActionNameBuilder actionNameBuilder = new SEOActionNameBuilder("true", "_");

        MyPackageConfig rootPkg = new MyPackageConfig();
        rootPkg.setName("org.texturemedia.smarturls.actions#struts-default#");
        rootPkg.setNamespace("");
        rootPkg.addParent(strutsDefault);

        MyPackageConfig subPkg = new MyPackageConfig();
        subPkg.setName("org.texturemedia.smarturls.actions.nested#struts-default#/nested");
        subPkg.setNamespace("/nested");
        subPkg.addParent(strutsDefault);

        MyPackageConfig subParentPkg = new MyPackageConfig();
        subParentPkg.setName("org.texturemedia.smarturls.actions.nested#foo-package#/nested");
        subParentPkg.setNamespace("/nested");
        subParentPkg.addParent(fooPackagePkg);

        MyPackageConfig fooPkg = new MyPackageConfig();
        fooPkg.setName("org.texturemedia.smarturls.actions.nested#struts-default#/foo");
        fooPkg.setNamespace("/foo");
        fooPkg.addParent(strutsDefault);

        MyPackageConfig nsPkg = new MyPackageConfig();
        nsPkg.setName("org.texturemedia.smarturls.actions.ns#struts-default#/ns");
        nsPkg.setNamespace("/ns");
        nsPkg.addParent(strutsDefault);

        MyPackageConfig ns2Pkg = new MyPackageConfig();
        ns2Pkg.setName("org.texturemedia.smarturls.actions.ns.ns2#struts-default#/ns/ns2");
        ns2Pkg.setNamespace("/ns/ns2");
        ns2Pkg.addParent(strutsDefault);

        MyPackageConfig packageLevelPkg = new MyPackageConfig();
        packageLevelPkg.setName("org.texturemedia.smarturls.actions.parentpackage#package-level#/parentpackage");
        packageLevelPkg.setNamespace("/parentpackage");
        packageLevelPkg.addParent(packageLevelPackagePkg);

        MyPackageConfig differentPkg = new MyPackageConfig();
        differentPkg.setName("org.texturemedia.smarturls.actions.parentpackage#different-package#/parentpackage");
        differentPkg.setNamespace("/parentpackage");
        differentPkg.addParent(differentPackagePkg);

        MyPackageConfig skipPkg = new MyPackageConfig();
        skipPkg.setName("org.texturemedia.smarturls.actions.skip#struts-default#/skip");
        skipPkg.setNamespace("/skip");
        skipPkg.addParent(strutsDefault);

        MyPackageConfig pkgLevelNamespacePkg = new MyPackageConfig();
        pkgLevelNamespacePkg.setName("org.texturemedia.smarturls.actions.namespace#struts-default#/package-level");
        pkgLevelNamespacePkg.setNamespace("/package-level");
        pkgLevelNamespacePkg.addParent(strutsDefault);

        MyPackageConfig classLevelNamespacePkg = new MyPackageConfig();
        classLevelNamespacePkg.setName("org.texturemedia.smarturls.actions.namespace#struts-default#/class-level");
        classLevelNamespacePkg.setNamespace("/class-level");
        classLevelNamespacePkg.addParent(strutsDefault);

        MyPackageConfig actionLevelNamespacePkg = new MyPackageConfig();
        actionLevelNamespacePkg.setName("org.texturemedia.smarturls.actions.namespace#struts-default#/action-level");
        actionLevelNamespacePkg.setNamespace("/action-level");
        actionLevelNamespacePkg.addParent(strutsDefault);

        ResultMapBuilder resultMapBuilder = createNiceMock(ResultMapBuilder.class);
        Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();
        expect(resultMapBuilder.build(NoAnnotationAction.class, "no_annotation", rootPkg)).
            andReturn(results);
        expect(resultMapBuilder.build(MultipleAnnotationAction.class, "multiple_annotation", rootPkg)).
            andReturn(results);
        expect(resultMapBuilder.build(BaseResultLocationAnnotationAction.class, "base_result_location_annotation", rootPkg)).
            andReturn(results);
        expect(resultMapBuilder.build(SingleAnnotationAction.class, "single_annotation", rootPkg)).
            andReturn(results);
        expect(resultMapBuilder.build(Skip.class, "skip", rootPkg)).andReturn(results);

        expect(resultMapBuilder.build(TestAction.class, "test", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNameAction.class, "foo", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNamesAction.class, "action1", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionNamesAction.class, "action2", subPkg)).andReturn(results);
        expect(resultMapBuilder.build(NamespaceAction.class, "namespace", fooPkg)).andReturn(results);
        expect(resultMapBuilder.build(ParentPackageAction.class, "parent_package", subParentPkg)).andReturn(results);

        expect(resultMapBuilder.build(org.texturemedia.smarturls.actions.ns.Index.class, "index", nsPkg)).andReturn(results);
        expect(resultMapBuilder.build(org.texturemedia.smarturls.actions.ns.ns2.Index.class, "index", ns2Pkg)).andReturn(results);

        expect(resultMapBuilder.build(DefaultAction.class, "default", packageLevelPkg)).andReturn(results);
        expect(resultMapBuilder.build(DifferentParentPackageAction.class, "different_parent_package", differentPkg)).andReturn(results);

        expect(resultMapBuilder.build(org.texturemedia.smarturls.actions.skip.Index.class, "index", skipPkg)).andReturn(results);

        expect(resultMapBuilder.build(DefaultNamespaceAction.class, "default_namespace", pkgLevelNamespacePkg)).andReturn(results);
        expect(resultMapBuilder.build(ClassLevelAction.class, "class_level", classLevelNamespacePkg)).andReturn(results);
        expect(resultMapBuilder.build(ActionLevelAction.class, "action_level", actionLevelNamespacePkg)).andReturn(results);

        EasyMock.replay(resultMapBuilder);

        ObjectFactory of = new ObjectFactory();
        PackageBasedActionConfigBuilder builder = new PackageBasedActionConfigBuilder(configuration,
            actionNameBuilder, resultMapBuilder, of, "false");
        builder.setBaseResultLocation("/test-base-result-location");
        builder.buildActionConfigs(null, "org.texturemedia.smarturls.actions");
        verify(resultMapBuilder);

        // Check the package config
        PackageConfig pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions#struts-default#");
        assertNotNull(pkgConfig);
        assertEquals(6, pkgConfig.getActionConfigs().size());

        ActionConfig ac = pkgConfig.getActionConfigs().get("no_annotation");
        assertNotNull(ac);
        assertEquals(NoAnnotationAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions#struts-default#", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("multiple_annotation");
        assertNotNull(ac);
        assertEquals(MultipleAnnotationAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions#struts-default#", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("single_annotation");
        assertNotNull(ac);
        assertEquals(SingleAnnotationAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions#struts-default#", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("base_result_location_annotation");
        assertNotNull(ac);
        assertEquals(BaseResultLocationAnnotationAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions#struts-default#", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("skip");
        assertNotNull(ac);
        assertEquals(Skip.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions#struts-default#", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("ns");
        assertNotNull(ac);
        assertEquals(org.texturemedia.smarturls.actions.ns.Index.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.ns#struts-default#/ns", ac.getPackageName());

        // Check the package config
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.nested#struts-default#/nested");
        assertNotNull(pkgConfig);
        assertEquals(4, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("test");
        assertNotNull(ac);
        assertEquals(TestAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.nested#struts-default#/nested", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("foo");
        assertNotNull(ac);
        assertEquals(ActionNameAction.class.getName(), ac.getClassName());
        assertEquals("run", ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.nested#struts-default#/nested", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("action1");
        assertNotNull(ac);
        assertEquals(ActionNamesAction.class.getName(), ac.getClassName());
        assertEquals("run1", ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.nested#struts-default#/nested", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("action2");
        assertNotNull(ac);
        assertEquals(ActionNamesAction.class.getName(), ac.getClassName());
        assertEquals("run2", ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.nested#struts-default#/nested", ac.getPackageName());

        // Check the package config
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.nested#foo-package#/nested");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("parent_package");
        assertNotNull(ac);
        assertEquals(ParentPackageAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.nested#foo-package#/nested", ac.getPackageName());

        // Check the package config
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.nested#struts-default#/foo");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("namespace");
        assertNotNull(ac);
        assertEquals(NamespaceAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.nested#struts-default#/foo", ac.getPackageName());

        // Check the ns config
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.ns#struts-default#/ns");
        assertNotNull(pkgConfig);
        System.out.println("Keys are " + pkgConfig.getActionConfigs().keySet());
        assertEquals(3, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("index");
        assertNotNull(ac);
        assertEquals(org.texturemedia.smarturls.actions.ns.Index.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.ns#struts-default#/ns", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("ns2");
        assertNotNull(ac);
        assertEquals(org.texturemedia.smarturls.actions.ns.ns2.Index.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.ns.ns2#struts-default#/ns/ns2", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("");
        assertNotNull(ac);
        assertEquals(org.texturemedia.smarturls.actions.ns.Index.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.ns#struts-default#/ns", ac.getPackageName());

        // Check the ns2 config
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.ns.ns2#struts-default#/ns/ns2");
        assertNotNull(pkgConfig);
        assertEquals(2, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("index");
        assertNotNull(ac);
        assertEquals(org.texturemedia.smarturls.actions.ns.ns2.Index.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.ns.ns2#struts-default#/ns/ns2", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("");
        assertNotNull(ac);
        assertEquals(org.texturemedia.smarturls.actions.ns.ns2.Index.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.ns.ns2#struts-default#/ns/ns2", ac.getPackageName());

        // Check the skip config
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.skip#struts-default#/skip");
        assertNotNull(pkgConfig);
        assertEquals(2, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("index");
        assertNotNull(ac);
        assertEquals(org.texturemedia.smarturls.actions.skip.Index.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.skip#struts-default#/skip", ac.getPackageName());

        ac = pkgConfig.getActionConfigs().get("");
        assertNotNull(ac);
        assertEquals(org.texturemedia.smarturls.actions.skip.Index.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.skip#struts-default#/skip", ac.getPackageName());

        // Check the package level annotation config
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.parentpackage#package-level#/parentpackage");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("default");
        assertNotNull(ac);
        assertEquals(DefaultAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.parentpackage#package-level#/parentpackage", ac.getPackageName());

        // Check the package level override annotation config
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.parentpackage#different-package#/parentpackage");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("different_parent_package");
        assertNotNull(ac);
        assertEquals(DifferentParentPackageAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.parentpackage#different-package#/parentpackage", ac.getPackageName());

        // Check the namespace at the package level
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.namespace#struts-default#/package-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("default_namespace");
        assertNotNull(ac);
        assertEquals(DefaultNamespaceAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.namespace#struts-default#/package-level", ac.getPackageName());

        // Check the namespace at the class level in a package that has a package level
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.namespace#struts-default#/class-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("class_level");
        assertNotNull(ac);
        assertEquals(ClassLevelAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.namespace#struts-default#/class-level", ac.getPackageName());

        // Check the namespace at the action level in a package that has a package level
        pkgConfig = configuration.getPackageConfig("org.texturemedia.smarturls.actions.namespace#struts-default#/action-level");
        assertNotNull(pkgConfig);
        assertEquals(1, pkgConfig.getActionConfigs().size());

        ac = pkgConfig.getActionConfigs().get("action_level");
        assertNotNull(ac);
        assertEquals(ActionLevelAction.class.getName(), ac.getClassName());
        assertNull(ac.getMethodName());
        assertTrue(ac instanceof SmartURLsActionConfig);
        assertEquals("/test-base-result-location", ((SmartURLsActionConfig) ac).getBaseResultLocation());
        assertEquals("org.texturemedia.smarturls.actions.namespace#struts-default#/action-level", ac.getPackageName());
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