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

package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.uelplugin.reflection.GenericReflectionProvider;
import org.apache.struts2.util.StrutsTypeConverter;
import org.springframework.mock.web.MockServletContext;

import javax.el.ExpressionFactory;
import javax.servlet.ServletContextEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Map;


public abstract class AbstractUELTest extends XWorkTestCase {
    private ExpressionFactory factory = ExpressionFactory.newInstance();
    protected XWorkConverter converter;
    protected CompoundRoot root;
    protected UELValueStack stack;
    protected DateFormat format = DateFormat.getDateInstance();
    protected ReflectionProvider reflectionProvider;

    private class DateConverter extends StrutsTypeConverter {

        @Override
        public Object convertFromString(Map context, String[] values, Class toClass) {
            try {
                return format.parseObject(values[0]);
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        public String convertToString(Map context, Object o) {
            return format.format(o);
        }

    }


    protected void setUp() throws Exception {
        super.setUp();

        loadConfigurationProviders(new StubConfigurationProvider() {
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                builder.factory(ValueStack.class, UELValueStack.class);
                builder.factory(ValueStackFactory.class, UELValueStackFactory.class);
                builder.factory(ReflectionProvider.class, GenericReflectionProvider.class);
                //builder.factory(StrutsTypeConverter)
            }
        });

        converter = container.getInstance(XWorkConverter.class);
        reflectionProvider = container.getInstance(ReflectionProvider.class);
        converter.registerConverter("java.util.Date", new DateConverter());
        this.root = new CompoundRoot();
        this.stack = new UELValueStack(container);
        stack.setRoot(root);
        stack.getContext().put(ActionContext.CONTAINER, container);

        MockServletContext servletContext = new MockServletContext();
        ActionContext context = new ActionContext(stack.getContext());
        ActionContext.setContext(context);
        ServletActionContext.setServletContext(servletContext);

        //simulate start up
        UELServletContextListener listener = new UELServletContextListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));
    }
}
