package org.apache.struts2.osgi;

import junit.framework.TestCase;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 25/05/2008
 * Time: 1:02:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class OsgiConfigurationProviderTest extends TestCase {

    public void testGetScannedPackages() {
        OsgiConfigurationProvider prov = new OsgiConfigurationProvider();
        Properties props = new Properties();
        props.setProperty("scanning.jar.includes", "*.jar");
        props.setProperty("scanning.package.includes", "com.opensymphony.xwork2.*, org.apache.struts2.*,ognl.*,freemarker.*, org.apache.velocity.*");
        String export = prov.getScannedPackages(props);
        assertNotNull(export);
        assertTrue(export.length() > 20);

    }
    public void testToArray() {
        OsgiConfigurationProvider prov = new OsgiConfigurationProvider();

        areEqual(new String[]{}, prov.toArray(null));
        areEqual(new String[]{"foo", "bar"}, prov.toArray("foo,bar"));
        areEqual(new String[]{"foo", "bar"}, prov.toArray("foo, bar"));
        areEqual(new String[]{"foo", "bar"}, prov.toArray("foo, \nbar"));

    }

    private void areEqual(String[] expected, String[] actual) {
        for (int x=0; x<expected.length; x++) {
            assertEquals(expected[x], actual[x]);
        }
    }
}
