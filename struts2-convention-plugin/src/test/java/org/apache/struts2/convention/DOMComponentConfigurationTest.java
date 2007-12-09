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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * <p>
 * This tests the component configuration.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DOMComponentConfigurationTest {
    @Test
    public void testGetComponents() throws Exception {
        DOMComponentConfiguration config = new DOMComponentConfiguration();
        List<Component> components = config.getComponents();
        assertEquals(1, components.size());
        assertEquals("/WEB-INF/component", components.get(0).getDefaultBaseResultLocation());
        assertEquals("foo-bar", components.get(0).getDefaultParentPackage());
        assertEquals(2, components.get(0).getActionPackages().size());
        assertEquals("org.texturemedia.component.test", components.get(0).getActionPackages().get(0).getActionPackage());
        assertNull(components.get(0).getActionPackages().get(0).getNamespacePrefix());
        assertEquals("org.texturemedia.component.test2", components.get(0).getActionPackages().get(1).getActionPackage());
        assertEquals("/test2", components.get(0).getActionPackages().get(1).getNamespacePrefix());
    }
}