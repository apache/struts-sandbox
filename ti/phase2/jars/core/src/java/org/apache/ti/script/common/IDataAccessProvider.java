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

// java imports

// internal imports

// external imports

/**
 * The IDataAccessProvider interface is implemented by objects
 * that need to provide their children with data and wish
 * to make it available to them with the <code>container</code>
 * binding context.  Expression evaluation will process
 * all <code>container</code> context references against this interface;
 * several read-only properties are exposed:
 * <table cellpadding="2" cellspacing="0" border="1">
 * <tr><th>Method</th><th>NetUI Data Binding Expression</th><th>Required</th></tr>
 * <tr><td>getCurrentIndex()</td><td><code>container.index</code></td><td>Yes</td></tr>
 * <tr><td>getCurrentItem()</td><td><code>container.item</code></td><td>Yes</td></tr>
 * <tr><td>getCurrentMetadata()</td><td><code>container.metadata</code></td><td>No</td></tr>
 * <tr><td>getDataSource()</td><td><code>container.dataSource</code></td><td>Yes</td></tr>
 * <tr><td>getProviderParent()</td><td><code>container.container</code></td><td>Yes</td></tr>
 * </table>
 * <p/>
 * In cases where a IDataAccessProvider contains another IDataAccessProvider, the
 * grandparent IDataAccessProvider may be referenced with the binding expression
 * <code>container.container</code>.  For example, the item, with the property firstName,
 * may be accessed with the expression <code>container.container.item.firstName</code>.
 * </p>
 * <p/>
 * The general use of the IDataAccessProvider is as an interface that is implemented
 * by repeating databound tags that iterate over a data set and render each item
 * in that data set.  The item and iteration index are exposed through this
 * interface and can be bound to by tags inside of the repeating tag
 * that implements the IDataAccessProvider interface.  This binding expression
 * should start with <code>container</code> and reference one of the properties above.
 * </p>
 */
public interface IDataAccessProvider {

    /**
     * Get the current index in this iteration.  This should be a
     * zero based integer that increments after each iteration.
     *
     * @return the current index of iteration or 0
     */
    public int getCurrentIndex();

    /**
     * Get the current data item in this IDataAccessProvider.
     *
     * @return the current data item or <code>null</code>
     */
    public Object getCurrentItem();

    /**
     * Get the expression that references the data item to which the
     * IDataAccessProvider is bound.
     *
     * @return the expression referencing the data source or <code>null</code> if no
     *         dataSource is set
     */
    public String getDataSource();

    /**
     * Get a metadata object for the current item.  This interface
     * is optional, and implementations of this interface are
     * provided by the IDataAccessProvider interface.  See these
     * implementations for information about their support for
     * current item metadata.
     *
     * @return the current metadata or <code>null</code> if no metadata can be
     *         found or metadata is not supported by a IDataAccessProvider implementation
     */
    public Object getCurrentMetadata();

    /**
     * Get the parent IDataAccessProvider of a DataAccessProvider.  A DataAccessProvider
     * implementation may be able to nest DataAccessProviders.  In this case,
     * it can be useful to be able to also nest access to data from parent
     * providers.  Implementations of this interface are left with having
     * to discover and export parents.  The return value from this call
     * on an implementing Object can be <code>null</code>.
     *
     * @return the parent DataAccessProvider or <code>null</code> if this method
     *         is not supported or the parent can not be found.
     */
    public IDataAccessProvider getProviderParent();
}
