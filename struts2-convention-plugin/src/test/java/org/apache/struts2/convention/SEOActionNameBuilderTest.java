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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * <p>
 * This class tests the SEO name builder.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SEOActionNameBuilderTest {
    @Test
    public void testBuild() throws Exception {
        SEOActionNameBuilder builder = new SEOActionNameBuilder("true", "_");
        assertEquals("foo", builder.build("Foo"));
        assertEquals("foo", builder.build("FooAction"));
        assertEquals("foo_bar", builder.build("FooBarAction"));
        assertEquals("foo_bar_baz", builder.build("FooBarBazAction"));
    }

    @Test
    public void testDash() throws Exception {
        SEOActionNameBuilder builder = new SEOActionNameBuilder("true", "-");
        assertEquals("foo", builder.build("Foo"));
        assertEquals("foo", builder.build("FooAction"));
        assertEquals("foo-bar", builder.build("FooBarAction"));
        assertEquals("foo-bar-baz", builder.build("FooBarBazAction"));
    }
}