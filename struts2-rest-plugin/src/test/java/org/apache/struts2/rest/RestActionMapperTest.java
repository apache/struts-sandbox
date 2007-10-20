package org.apache.struts2.rest;

import org.apache.struts2.dispatcher.mapper.ActionMapping;

import junit.framework.TestCase;

public class RestActionMapperTest extends TestCase {

    private RestActionMapper mapper;
    
    public void setUp() throws Exception {
        mapper = new RestActionMapper();
    }

    public void testParseNameAndNamespace() {
        tryUri("/foo/23", "/", "foo/23");
        tryUri("/foo/", "/", "foo/");
        tryUri("foo", "", "foo");
        tryUri("/", "/", "");
    }
    
    public void testParseNameAndNamespaceWithNamespaces() {
        tryUri("/ns/foo/23", "/ns", "foo/23");
        tryUri("/ns/foo/", "/ns", "foo/");
    }
    
    public void testParseNameAndNamespaceWithEdit() {
        tryUri("/ns/foo/23;edit", "/ns", "foo/23;edit");
    }
    
    private void tryUri(String uri, String expectedNamespace, String expectedName) {
        ActionMapping mapping = new ActionMapping();
        mapper.parseNameAndNamespace(uri, mapping, null);
        assertEquals(expectedName, mapping.getName());
        assertEquals(expectedNamespace, mapping.getNamespace());
    }

}
