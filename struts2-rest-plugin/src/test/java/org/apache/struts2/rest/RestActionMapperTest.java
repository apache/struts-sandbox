package org.apache.struts2.rest;

import java.util.HashMap;

import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

import junit.framework.TestCase;

public class RestActionMapperTest extends TestCase {

    private RestActionMapper mapper;
    private ConfigurationManager configManager;
    private Configuration config;

    protected void setUp() throws Exception {
        super.setUp();
        mapper = new RestActionMapper();

        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig("myns", "/my/namespace", false, null);
        PackageConfig pkg2 = new PackageConfig("my", "/my", false, null);
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        configManager = new ConfigurationManager() {
            public Configuration getConfiguration() {
                return config;
            }
        };
    }

    public void testParseNameAndNamespace() {
        tryUri("/foo/23", "", "foo/23");
        tryUri("/foo/", "", "foo/");
        tryUri("foo", "", "foo");
        tryUri("/", "/", "");
    }
    
    public void testParseNameAndNamespaceWithNamespaces() {
        tryUri("/my/foo/23", "/my", "foo/23");
        tryUri("/my/foo/", "/my", "foo/");
    }
    
    public void testParseNameAndNamespaceWithEdit() {
        tryUri("/my/foo/23;edit", "/my", "foo/23;edit");
    }
    
    private void tryUri(String uri, String expectedNamespace, String expectedName) {
        ActionMapping mapping = new ActionMapping();
        mapper.parseNameAndNamespace(uri, mapping, configManager);
        assertEquals(expectedName, mapping.getName());
        assertEquals(expectedNamespace, mapping.getNamespace());
    }

}
