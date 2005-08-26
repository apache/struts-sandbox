/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.script.common;

import org.apache.ti.script.common.bundle.BundleNode;

/**
 * Provide a {@link java.util.Map} of {@link org.apache.ti.script.common.BundleMap.BundleNodeMap} objects that can
 * expose various implementations of {@link BundleNode} to
 * expression languages. <p/> This {@link java.util.Map} implementation is
 * optimized for read as the entrySet() is created lazily. In addition, the
 * entrySet does not contain all possible BundleNodeMap objects as named
 * "message-resources" bundles are discovered at runtime and requested by name.
 * <p/>
 */
public class BundleMap {

//    extends AbstractScriptableMap {
//
//    public static final String DEFAULT_STRUTS_BUNDLE_NAME = "default";
//
//    private static final Logger LOGGER = Logger.getInstance(BundleMap.class);
//
//    private HashMap _registeredBundles = null;
//
//    private HttpServletRequest _servletRequest = null;
//    private HttpSession _httpSession = null;
//    private ServletContext _servletContext = null;
//
//    /**
//     * Create a BundleMap object that is used for data binding to resource
//     * bundles.
//     * 
//     * @param servletRequest the current {@link javax.servlet.http.HttpServletRequest} object
//     * @param servletContext a {@link javax.servlet.ServletContext} object that facilitates binding to resource bundles 
//     *                       declared in Struts modules 
//     */
//    public BundleMap(HttpServletRequest servletRequest, ServletContext servletContext) {
//        assert servletRequest != null;
//        assert servletContext != null;
//
//        _servletRequest = servletRequest;
//        _httpSession = servletRequest.getSession(false);
//        _servletContext = servletContext;
//
//        _registeredBundles = new HashMap();
//    }
//
//    public void registerResourceBundle(String name, String resourcePath, Locale forcedLocale) {
//        if(_registeredBundles == null)
//            _registeredBundles = new HashMap();
//
//        if(LOGGER.isInfoEnabled() && _registeredBundles.containsKey(name))
//            LOGGER.info("The bundle map already contains a key \"" + name + "\" overwriting the previous value.");
//
//        Locale locale = forcedLocale != null ? forcedLocale : InternalUtils.lookupLocale(_servletRequest);
//        ResourceBundle resourceBundle = ResourceBundle.getBundle(resourcePath, locale);
//        BundleNode bundle = BundleNodeFactory.getInstance().getResourceBundleNode(name, resourceBundle, locale);
//        _registeredBundles.put(name, bundle);
//    }
//
//    public Object get(Object key) {
//        if(key == null)
//            throw new NullPointerException("Binding to a resource bundle does not accept a null key");
//
//        BundleNodeMap map = lookupScriptableBundle(key.toString());
//        if(map == null) {
//            /* handleBundleNotFound will throw an exception when the message key isn't found */
//            handleBundleNotFound(key.toString());
//            return null;
//        }
//        else return map;
//    }
//
//    /**
//     * Implementation of Map.containsKey for the bundle implicit object.
//     * 
//     * This method is required by JSP 2.0 EL and performs the lookups of the
//     * various available bundles which have been registered either explicitly or
//     * implicitly.
//     * 
//     * @param key The name of a bundle to lookup
//     * @return <code>true</code> if the bundle is available; <code>false</code> otherwise
//     */
//    public boolean containsKey(Object key) {
//        if(key == null)
//            throw new NullPointerException("Binding to a resource bundle does not accept a null key");
//
//        BundleNodeMap map = lookupScriptableBundle(key.toString());
//        return map != null;
//    }
//
//    public Set entrySet() {
//        ArrayList entries = new ArrayList();
//
//    	/* add BundleNode objects that have been accessed */
//        if(_registeredBundles != null) {
//            Iterator iterator = _registeredBundles.keySet().iterator();
//            while(iterator.hasNext()) {
//                Object key = iterator.next();
//                entries.add(new BundleNodeEntry(key));
//            }
//        }
//
//        MessageResources resources = null;
//
//        resources = lookupDefaultStrutsBundle();
//        if(resources != null)
//            entries.add(new BundleNodeEntry(DEFAULT_STRUTS_BUNDLE_NAME));
//
//        ModuleConfig moduleConfig = lookupCurrentModuleConfig();
//        if(moduleConfig != null) {
//            MessageResourcesConfig[] mrs = moduleConfig.findMessageResourcesConfigs();
//            for(int i = 0; i < mrs.length; i++) {
//                String resourceKey = mrs[i].getKey() + moduleConfig.getPrefix();
//                resources = lookupStrutsBundle(resourceKey);
//                entries.add(new BundleNodeEntry(mrs[i].getKey()));
//            }
//        }
//
//        return new EntrySet((Entry[])entries.toArray(new Entry[] {}));
//    }
//
//    /*
//     */
//    private BundleNodeMap lookupScriptableBundle(String name) {
//        BundleNodeMap map = null;
//
//        /* check to see if the bundle was explicitly registered */
//        if(_registeredBundles != null && _registeredBundles.containsKey(name)) {
//            map = new BundleNodeMap(name, (BundleNode)_registeredBundles.get(name));
//        }
//        else if(name.equals(DEFAULT_STRUTS_BUNDLE_NAME)) {
//            MessageResources resources = lookupDefaultStrutsBundle();
//            if(resources != null) {
//                BundleNode bundleNode = BundleNodeFactory.getInstance().getStrutsBundleNode(name, resources, retrieveUserLocale());
//                map = new BundleNodeMap(name, bundleNode);
//            }
//        }
//        else if(_servletContext.getAttribute(name) != null) {
//            MessageResources resources = lookupStrutsBundle(name);
//            if(resources != null) {
//                BundleNode bundleNode = BundleNodeFactory.getInstance().getStrutsBundleNode(name, resources, retrieveUserLocale());
//                map = new BundleNodeMap(name, bundleNode);
//            }
//        }
//        else {
//            ModuleConfig moduleConfig = lookupCurrentModuleConfig();
//            if(moduleConfig != null) {
//                MessageResourcesConfig[] mrs = moduleConfig.findMessageResourcesConfigs();
//                if(mrs != null) {
//                    for(int i = 0; i < mrs.length; i++) {
//                        /* skip the default bundle */
//                        if(mrs[i].getKey().equals(Globals.MESSAGES_KEY))
//                            continue;
//                        else if(mrs[i].getKey().equals(name)) {
//                            String resourceKey = mrs[i].getKey() + moduleConfig.getPrefix();
//                            MessageResources resources = lookupStrutsBundle(resourceKey);
//                            BundleNode bundleNode = BundleNodeFactory.getInstance().getStrutsBundleNode(name, resources, retrieveUserLocale());
//                            map = new BundleNodeMap(name, bundleNode);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//
//        return map;
//    }
//
//    /**
//     * Lookup the "default" resource bundle for the current Struts module.
//     *
//     * @return a MessageResources object if a "default" bundle exists.
//     *         <code>null</code> otherwise
//     */
//    private MessageResources lookupDefaultStrutsBundle() {
//        Object value = _servletRequest.getAttribute(Globals.MESSAGES_KEY);
//        if(value instanceof MessageResources)
//            return (MessageResources)value;
//        else {
//            if(value != null)
//                LOGGER.warn("Can not resolve the default module bundle."
//                                + "  The object resolved from the request is of type "
//                                +(value != null ? value.getClass().toString() : "null"));
//            return null;
//        }
//    }
//
//    /**
//     * Lookup a specific resource bundle for the current Struts module.
//     * 
//     * @param name
//     *            the name of the resource bundle to lookup
//     * @return a MessageResources object if a bundle matching the given name
//     *         exists. <code>null</code> otherwise.
//     */
//    private MessageResources lookupStrutsBundle(String name) {
//        Object value = _servletContext.getAttribute(name);
//        if(value instanceof MessageResources)
//            return (MessageResources)value;
//        else {
//            if(value != null)
//                LOGGER.warn("Can not resolve module bundle with name \"" + name
//                                + "\".  The object resolved from ServletContext is of type "
//                                +(value != null ? value.getClass().toString() : "null"));
//            return null;
//        }
//    }
//
//    private final ModuleConfig lookupCurrentModuleConfig() {
//        return (ModuleConfig)_servletRequest.getAttribute(Globals.MODULE_KEY);
//    }
//
//    private void handleBundleNotFound(String name) {
//
//        /* At this point, no message bundle could be found.  Throw an error that contains a
//           descriptive message about the bundles that are available
//          */
//        String registeredBundles = formatBundleNames(createBundleList());
//        String strutsBundles = formatBundleNames(createStrutsBundleList());
//
//        String msg = "The bundle named \"" + name + "\" was not found in the list of registered bundles with names "
//                     + registeredBundles + " or implicit bundle names " + strutsBundles + ".";
//
//        LOGGER.error(msg);
//        throw new RuntimeException(msg);
//    }
//
//    private final String formatBundleNames(String[] names) {
//        InternalStringBuilder sb = new InternalStringBuilder(128);
//        sb.append("[");
//        for(int i = 0; i < names.length; i++) {
//            if(i > 0)
//                sb.append(", ");
//            sb.append(names[i]);
//        }
//        sb.append("]");
//
//        return sb.toString();
//    }
//
//    private final String[] createBundleList() {
//        String[] names = null;
//        if(_registeredBundles != null) {
//            names = new String[_registeredBundles.size()];
//            Iterator iterator = _registeredBundles.keySet().iterator();
//            for(int i = 0; iterator.hasNext(); i++) {
//                names[i] = iterator.next().toString();
//            }
//        }
//
//        return names;
//    }
//
//    private final String[] createStrutsBundleList() {
//        String[] names = null;
//        ModuleConfig config = lookupCurrentModuleConfig();
//        if(config != null) {
//            MessageResourcesConfig[] mrs = config.findMessageResourcesConfigs();
//            names = new String[mrs.length];
//            if(mrs != null) {
//                for(int i = 0; i < mrs.length; i++) {
//                    if(mrs[i].getKey().equals(Globals.MESSAGES_KEY))
//                        names[i] = DEFAULT_STRUTS_BUNDLE_NAME;
//                    else names[i] = mrs[i].getKey() + config.getPrefix();
//                }
//            }
//        }
//        return names;
//    }
//
//    /**
//     * Utility method that discovers the {@link java.util.Locale} for the
//     * current request.
//     *
//     * @return the {@link java.util.Locale} to use when looking-up strings while data binding to resource bundles
//     */
//    private final Locale retrieveUserLocale() {
//        return InternalUtils.lookupLocale(_servletRequest);
//    }
//
//    final class BundleNodeEntry
//        extends Entry
//    {
//        BundleNodeEntry(Object key) {
//            super(key, null);
//        }
//
//        public Object getValue() {
//            assert getKey() instanceof String;
//
//            String key = (String)getKey();
//            return lookupScriptableBundle(key);
//        }
//    }
//
//    /**
//     * Provide a {@link java.util.Map} implementation that exposes a
//     * {@link org.apache.ti.script.common.bundle.BundleNode}
//     * object to an expression language as a Map. Access to the values in the
//     * map is by key and depends on the implementation of the BundleNode. <p/>
//     * Access is read optimized and the complete entrySet() is only constructed
//     * when needed.
//     */
//    final class BundleNodeMap
//        extends AbstractScriptableMap {
//
//        private String _propertiesName = null;
//        private BundleNode _bundle = null;
//        private Set _entrySet = null;
//
//        BundleNodeMap(String propertiesName, BundleNode bundle) {
//            assert bundle != null;
//            assert propertiesName != null;
//
//            _bundle = bundle;
//            _propertiesName = propertiesName;
//        }
//
//        public Set entrySet() {
//            if(_entrySet == null) {
//                ArrayList list = new ArrayList();
//                Enumeration enumeration = _bundle.getKeys();
//                while(enumeration.hasMoreElements()) {
//                    String key =(String)enumeration.nextElement();
//                    String msg = _bundle.getString(key);
//                    list.add(new Entry(key, msg));
//                }
//                _entrySet = new EntrySet((Entry[])list.toArray(new Entry[] {}));
//            }
//
//            return _entrySet;
//        }
//
//        public Object get(Object key) {
//            if(key == null)
//                throw new NullPointerException("Bundle data binding does not accept a null key");
//
//            String result = _bundle.getString(key.toString());
//            if(result == null) {
//                String msg = "The bundle property name \"" + key + "\" could not be found in the properties bundle \"" + _propertiesName + "\".";
//                LOGGER.error(msg);
//                throw new IllegalArgumentException(msg);
//            } 
//            else return result;
//        }
//
//        public boolean containsKey(Object key) {
//            if(key == null)
//                return false;
//            else return _bundle.getString(key.toString()) != null;
//        }
//
//        public String toString() {
//            return _bundle != null ? _bundle.toString() : "BundleMap contains an empty BundleNode";
//        }
//    }
}
     
