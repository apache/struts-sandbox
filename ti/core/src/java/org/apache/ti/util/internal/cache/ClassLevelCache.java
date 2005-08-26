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
package org.apache.ti.util.internal.cache;

import org.apache.ti.util.internal.concurrent.InternalConcurrentHashMap;

import java.util.Map;


/**
 * Thread-safe cache that is stored statically per-Class.
 */
public final class ClassLevelCache {

    private static InternalConcurrentHashMap _classCaches = new InternalConcurrentHashMap();
    private InternalConcurrentHashMap _caches = new InternalConcurrentHashMap();


    public static ClassLevelCache getCache(Class c) {
        String className = c.getName();
        ClassLevelCache cache = (ClassLevelCache) _classCaches.get(className);

        if (cache == null) {
            cache = new ClassLevelCache();
            _classCaches.put(className, cache);
        }

        return cache;
    }

    protected ClassLevelCache() {
    }

    public Object get(String majorKey, String minorKey) {
        InternalConcurrentHashMap cache = (InternalConcurrentHashMap) _caches.get(majorKey);
        return cache != null ? cache.get(minorKey) : null;
    }

    public Object getCacheObject(String cacheID) {
        return _caches.get(cacheID);
    }

    public void setCacheObject(String cacheID, Object object) {
        _caches.put(cacheID, object);
    }

    public Map getCacheMap(String cacheID) {
        return getCacheMap(cacheID, true);
    }

    public Map getCacheMap(String cacheID, boolean createIfMissing) {
        InternalConcurrentHashMap cache = (InternalConcurrentHashMap) _caches.get(cacheID);

        if (cache == null && createIfMissing) {
            cache = new InternalConcurrentHashMap();
            _caches.put(cacheID, cache);
        }

        return cache;
    }

    public void put(String cacheID, String minorKey, Object value) {
        //
        // ConcurrentHashMap can't accept null.  For now we'll just assert; if it becomes necessary to add null,
        // then we can use a marker value.
        //
        assert value != null;
        getCacheMap(cacheID).put(minorKey, value);
    }

    public static void clearAll() {
        _classCaches.clear();
    }
}
