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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.FilterConfig;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class tests the smart URLs filter.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SmartURLsFilterTest {
    @Test
    public void testProxyGetInitParameterWithExisting() {
        FilterConfig config = EasyMock.createStrictMock(FilterConfig.class);
        SmartURLsFilter.SmartURLsFilterConfigProxy proxy = new SmartURLsFilter.SmartURLsFilterConfigProxy(config);
        EasyMock.expect(config.getInitParameter("configProviders")).andReturn("foo,bar");
        EasyMock.expect(config.getInitParameter("packages")).andReturn("foo");
        EasyMock.replay(config);
        Assert.assertEquals("org.texturemedia.smarturls.SmartURLsConfigurationProvider,foo,bar", proxy.getInitParameter("configProviders"));
        Assert.assertEquals("component foo", proxy.getInitParameter("packages"));
        EasyMock.verify(config);
    }

    @Test
    public void testProxyGetInitParameter() {
        FilterConfig config = EasyMock.createStrictMock(FilterConfig.class);
        SmartURLsFilter.SmartURLsFilterConfigProxy proxy = new SmartURLsFilter.SmartURLsFilterConfigProxy(config);
        EasyMock.expect(config.getInitParameter("configProviders")).andReturn(null);
        EasyMock.expect(config.getInitParameter("packages")).andReturn(null);
        EasyMock.replay(config);
        Assert.assertEquals("org.texturemedia.smarturls.SmartURLsConfigurationProvider", proxy.getInitParameter("configProviders"));
        Assert.assertEquals("component", proxy.getInitParameter("packages"));
        EasyMock.verify(config);
    }

    @Test
    public void testProxyGetInitParameterDup() {
        FilterConfig config = EasyMock.createStrictMock(FilterConfig.class);
        SmartURLsFilter.SmartURLsFilterConfigProxy proxy = new SmartURLsFilter.SmartURLsFilterConfigProxy(config);
        EasyMock.expect(config.getInitParameter("configProviders")).andReturn("org.texturemedia.smarturls.SmartURLsConfigurationProvider");
        EasyMock.expect(config.getInitParameter("packages")).andReturn("component");
        EasyMock.replay(config);
        Assert.assertEquals("org.texturemedia.smarturls.SmartURLsConfigurationProvider", proxy.getInitParameter("configProviders"));
        Assert.assertEquals("component", proxy.getInitParameter("packages"));
        EasyMock.verify(config);
    }

    @Test
    public void testProxyGetInitParameterOther() {
        FilterConfig config = EasyMock.createStrictMock(FilterConfig.class);
        SmartURLsFilter.SmartURLsFilterConfigProxy proxy = new SmartURLsFilter.SmartURLsFilterConfigProxy(config);
        EasyMock.expect(config.getInitParameter("foo")).andReturn("bar");
        EasyMock.replay(config);
        Assert.assertEquals("bar", proxy.getInitParameter("foo"));
        EasyMock.verify(config);
    }

    @Test
    public void testProxyGetInitParameterNames() {
        FilterConfig config = EasyMock.createStrictMock(FilterConfig.class);
        SmartURLsFilter.SmartURLsFilterConfigProxy proxy = new SmartURLsFilter.SmartURLsFilterConfigProxy(config);
        Vector<String> names = new Vector<String>(Arrays.asList("foo", "bar"));

        EasyMock.expect(config.getInitParameterNames()).andReturn(names.elements());
        EasyMock.replay(config);

        Enumeration e = proxy.getInitParameterNames();
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals("foo", e.nextElement());
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals("bar", e.nextElement());
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals("configProviders", e.nextElement());
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals("packages", e.nextElement());

        EasyMock.verify(config);
    }
}