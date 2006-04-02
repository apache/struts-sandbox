/*
 *  Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.struts.flow.ibatis;

import org.mozilla.javascript.*;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.engine.mapping.statement.*;
import com.ibatis.sqlmap.engine.impl.*;

import com.ibatis.sqlmap.client.*;

import org.apache.struts.flow.*;
import org.apache.struts.flow.core.*;
import org.apache.struts.flow.core.javascript.*;
import org.apache.struts.flow.sugar.*;

import java.util.*;
import java.io.*;
import java.sql.SQLException;

import java.util.regex.*;


import org.apache.commons.logging.*;

/**
 *  JavaScript interface to the log facility.
 *
 *@jsname log
 */
public class SqlMap extends ScriptableObject implements Function {
    
    private static final long serialVersionUID = 1L;
    protected static SqlMapClient client;
    protected String namespace;
    protected Function initFunction;
    protected Map statements = new HashMap();
    protected List dynamicQueries = new ArrayList();
    
    public static final String FIRST_TYPE = "first";
    public static final String ALL_TYPE = "all";
    public static final String SCALAR_TYPE = "scalar";
    
    protected static final Log log = LogFactory.getLog(SqlMap.class);

    static {
        reloadConfig();
    }

    /**  Constructor for the JSLog object */
    public SqlMap() { 
        FlowConfiguration config = FlowConfiguration.getInstance();
        dynamicQueries = buildPatterns(config);
    }
    
    public synchronized static void reloadConfig() {
        String configPath = FlowConfiguration.getInstance().getProperty("flow.ibatis.config");
        if (configPath != null) {
            log.debug("Reloading SqlMap config");
            try {
                Reader reader = Resources.getResourceAsReader(configPath);
                client = SqlMapClientBuilder.buildSqlMapClient(reader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            log.debug("Cannot reload SqlMap config - no configuration path identified.");
        }
    }

    protected List buildPatterns(Map patterns) { 
        List queries = new ArrayList();
        String prefix = "flow.ibatis.dynamic.";
        String key, pattern, query, type, name;
        HashSet found = new HashSet();
        for (Iterator i = patterns.keySet().iterator(); i.hasNext(); ) {
            key = (String)i.next();
            
            if (key.startsWith(prefix)) {
                name = key.substring(prefix.length(), key.lastIndexOf('.'));
                if (!found.contains(name)) {
                    pattern = (String) patterns.get(prefix + name + ".pattern");
                    query = (String) patterns.get(prefix + name + ".query");
                    type = (String) patterns.get(prefix + name + ".type");
                    queries.add(new DynamicQuery(type, pattern, query));
                    found.add(name);
                }
            }
        }
        return queries;
    }

    
    public Scriptable construct(Context cx, Scriptable scope, java.lang.Object[] args) {
        
        if (args.length == 0 && this.namespace == null) {
            throw new FlowException("The namespace parameter is required");
        }
        
        if (this.namespace == null) {
            setNamespace(args[0].toString());
        }
        
        if (args.length == 2 && this.initFunction == null) {
            this.initFunction = (Function)args[1];
        }
        
        SqlMap self = new SqlMap();
        self.setPrototype(getPrototype());
        self.setParentScope(getParentScope());
        self.setNamespace(this.namespace);
        
        
        if (this.initFunction != null) {
            try {
                this.initFunction.call(cx, scope, self, new Object[0]);
            } catch (Exception ex) {
                throw Context.throwAsScriptRuntimeEx(ex);
            }
        }
        
        return self;
        
    }
    
    protected void setInitFunction(Function func) {
        this.initFunction = func;
    }
    
    protected void setNamespace(String ns) {
        this.namespace = ns;
        log.debug("setting namespace: "+namespace);
        synchronized (SqlMap.class) {
            if (client != null) {
                SqlMapClientImpl cimpl = (SqlMapClientImpl)client;
                SqlMapExecutorDelegate del = cimpl.getDelegate();
                String name;
                for (Iterator i = del.getMappedStatementNames(); i.hasNext(); ) {
                    name = (String) i.next();
                    log.debug("looking at statement "+name);
                    if (name.startsWith(namespace+".")) {
                        log.debug("adding statement "+name);
                        statements.put(name.substring(namespace.length() + 1), del.getMappedStatement(name));
                    }
                }
            } else {
                log.warn("ibatis client is missing");
            }
        }
    }
    
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) {
        throw new FlowException("This function cannot not be called without the 'new' operator");
    }

    /**
     *  Gets the class name
     *
     *@return    The className value
     */
    public String getClassName() {
        return (this.namespace == null ? "SqlMap" : this.namespace);
    }

    public Object get(final String name, Scriptable start) {

        log.debug("getting property "+name);
        final MappedStatement ms = (MappedStatement) statements.get(name);
        if (ms != null) {
            return buildStatementFunction(ms, null, null);
        } else {
            DynamicQuery query;
            Matcher m;
            for (Iterator i = dynamicQueries.iterator(); i.hasNext(); ) {
                query = (DynamicQuery)i.next();
                System.out.println("testing: "+query.pattern.pattern()+" against "+name);
                m = query.getPattern().matcher(name);
                if (m.matches()) {
                    Map params = new HashMap();
                    for (int x = 1; x<= m.groupCount(); x++) {
                        String paramname = m.group(x);
                        params.put("name"+x, paramname);
                    }
                    return buildStatementFunction(ms, query.getType(), params);
                }
            }
            
            return super.get(name, start);
        }
    }

    private Object buildStatementFunction(final MappedStatement ms, final String resultType, final Map coreParams) {
        log.debug("found as a function");
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) 
                throws IOException {
                
                Map params = null;
                if (args.length == 1 && args[0] instanceof Scriptable) {
                    params = ConversionHelper.jsobjectToMap((Scriptable)args[0]);
                } else {
                    params = ConversionHelper.jsobjectToMap(thisObj);
                }
                
                if (coreParams != null) {
                    params.putAll(coreParams);
                }
                String stmName = ms.getId();
                Object result = null;
                StatementType type = ms.getStatementType();
                try {  
                    synchronized (SqlMap.class) {
                        if (type == StatementType.INSERT) {
                            result = client.insert(stmName, params);
                        } else if (type == StatementType.DELETE) {
                            result = new Integer(client.delete(stmName, params));
                        } else if (type == StatementType.UPDATE) {
                            result = new Integer(client.update(stmName, params));
                        } else {
                            if (SCALAR_TYPE.equals(resultType)) {
                                result = client.queryForObject(stmName, params);
                            } else if (FIRST_TYPE.equals(resultType)) {
                                List list = client.queryForList(stmName, params);
                                if (list.size() > 0) {
                                    result = list.get(0);
                                } 
                            } else {
                                result = client.queryForList(stmName, params);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return result;
            }
        };
    }
    
    protected class DynamicQuery {
        
        private String type;
        private Pattern pattern;
        private MappedStatement query;
        
        public DynamicQuery(String type, String pattern, String query) {
            this.type = type;
            this.pattern = Pattern.compile(pattern);
            this.query = (MappedStatement)statements.get(query);
        }
        public Pattern getPattern() {
            return pattern;
        }
        public MappedStatement getQuery() {
            return query;
        }
        public String getType() {
            return type;
        }
    }
 
    
}

