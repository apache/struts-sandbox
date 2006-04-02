package org.apache.struts.flow.ibatis;

import junit.framework.TestCase;

import java.io.*;
import java.util.*;
import com.ibatis.sqlmap.engine.mapping.statement.*;
import com.ibatis.sqlmap.engine.impl.*;
import com.ibatis.sqlmap.client.*;
import com.ibatis.common.resources.*; 
import org.mozilla.javascript.Scriptable;
import org.apache.struts.flow.*;

import org.apache.struts.flow.sugar.*;

/* JUnitTest case for class: org.apache.struts.chain.commands.servlet.SqlMap */
public class TestSqlMap extends TestCase {

    public TestSqlMap(String _name) {
        super(_name);
    }
    
    public void setUp() throws Exception {
        FlowConfiguration.getInstance().setProperty("flow.ibatis.config", "org/apache/struts/flow/ibatis/sqlmap.conf");
        SqlMap.reloadConfig();
    }

    private SqlMap getMap(String name) {
        SqlMap fmap = new SqlMap();
        return (SqlMap) fmap.construct(null, null, new Object[] {name});
    }

    public void testConstructor() throws Exception {
        
        SqlMap nmap = getMap("Foo");
        assertNotNull(nmap);
        
        assertNotNull(nmap.statements);
        assertTrue(nmap.statements.size() == 1);
        
        SqlMap map2 = (SqlMap) nmap.construct(null, null, new Object[0]);
        assertNotNull(map2.statements);
        assertTrue(map2.statements.size() == 1);
        
    }
    
    public void testGetIbatisFunctions() throws Exception {
        
        SqlMap nmap = getMap("Foo");
        
        Object obj = nmap.get("getAll", null);
        assertTrue(obj instanceof ExtensionFunction);
        
        obj = nmap.get("asdfgetAll", null);
        assertTrue(obj == Scriptable.NOT_FOUND);
        
    }
    
    
    public void testBuildPatterns() throws Exception {
        Map props = new HashMap();
        props.put("flow.ibatis.dynamic.foo.pattern", "foo(.*)");
        props.put("flow.ibatis.dynamic.foo.query", "getAll");
        props.put("flow.ibatis.dynamic.foo.type", "all");
        
        props.put("flow.ibatis.dynamic.bar.pattern", "bar(.*)");
        props.put("flow.ibatis.dynamic.bar.query", "getAll");
        props.put("flow.ibatis.dynamic.bar.type", "scalar");
        
        SqlMap map = getMap("Foo");
        List queries = map.buildPatterns(props);
        assertNotNull(queries);
        assertTrue(queries.size() == 2);
        
    }
    
    public void testDynamicFunctions() throws Exception {
        Map props = new HashMap();
        props.put("flow.ibatis.dynamic.foo.pattern", "foo(.*)");
        props.put("flow.ibatis.dynamic.foo.query", "getAll");
        props.put("flow.ibatis.dynamic.foo.type", "all");
        
        props.put("flow.ibatis.dynamic.bar.pattern", "bar(.*)");
        props.put("flow.ibatis.dynamic.bar.query", "getAll");
        props.put("flow.ibatis.dynamic.bar.type", "scalar");
        
        SqlMap map = getMap("Foo");
        map.dynamicQueries = map.buildPatterns(props);
        Object obj = map.get("fooBar", null);
        assertTrue(obj instanceof ExtensionFunction);
        
        obj = map.get("barBar", null);
        assertTrue(obj instanceof ExtensionFunction);
        
        obj = map.get("fosoBar", null);
        assertTrue(obj == Scriptable.NOT_FOUND);
        
    }

    /* Executes the test case */
    public static void main(String[] argv) {
        String[] testCaseList = {TestSqlMap.class.getName()};
        junit.textui.TestRunner.main(testCaseList);
    }
}
