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
package org.apache.ti.util.iterator;

import org.apache.ti.util.Bundle;
import org.apache.ti.util.logging.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p/>
 * {@link Iterator} implementation for {@link ResultSet}.
 * </p>
 */
public class ResultSetIterator
        implements Iterator {

    private static final Logger LOGGER = Logger.getInstance(ResultSetIterator.class);

    private boolean _primed = false;
    private String[] _columnNames = null;
    private ResultSet _rs = null;

    /**
     * Create a ResultSetIterator for the <code>resultSet</code>.
     *
     * @param resultSet the ResultSet to iterate over
     * @throws IllegalStateException when a {@link SQLException} occurs manipulating the ResultSet
     */
    public ResultSetIterator(ResultSet resultSet) {
        if (resultSet == null)
            return;

        _rs = resultSet;

        try {
            // handle RSMD here to build a template map that can contain the data for each row
            ResultSetMetaData rsmd = _rs.getMetaData();
            assert rsmd != null;

            int cols = rsmd.getColumnCount();
            _columnNames = new String[cols];
            for (int i = 1; i <= cols; i++) {
                _columnNames[i - 1] = rsmd.getColumnName(i);
                LOGGER.trace("column[" + i + "]: " + _columnNames[i - 1]);
            }
        } catch (SQLException sql) {
            String msg = "An exception occurred reading ResultSetMetaData from a ResultSet.  Cause: " + sql;
            LOGGER.error(msg, sql);
            IllegalStateException e = new IllegalStateException(msg);
            e.initCause(sql);
            throw e;
        }
    }

    /**
     * Check for a subsequent item in the ResultSet.
     *
     * @return <code>true</code> if there is another element; <code>false</code> otherwise
     * @throws IllegalStateException when a {@link SQLException} occurs advancing the ResultSet
     */
    public boolean hasNext() {
        if (_rs == null)
            return false;

        if (_primed)
            return true;

        try {
            _primed = _rs.next();
            return _primed;
        } catch (SQLException sql) {
            String msg = "An exception occurred reading from the Iterator.  Cause: " + sql;
            LOGGER.error(msg, sql);
            IllegalStateException e = new IllegalStateException(msg);
            e.initCause(sql);
            throw e;
        }
    }

    /**
     * Advance to the next row in the ResultSet.
     *
     * @return a {@link java.util.Map} containing the data in the next row.  The keys in the map
     *         correspond to the ResultSet's column names and are case insensitive when checking a key.
     * @throws NoSuchElementException if the ResultSet is null or the end of the ResultSet has been reached
     */
    public Object next() {
        if (_rs == null)
            throw new NoSuchElementException(Bundle.getErrorString("IteratorFactory_Iterator_noSuchElement"));

        try {
            if (!_primed) {
                _primed = _rs.next();
                if (!_primed) {
                    throw new NoSuchElementException(Bundle.getErrorString("IteratorFactory_Iterator_noSuchElement"));
                }
            }

            _primed = false;
            return convertRow(_rs, _columnNames);
        } catch (SQLException sql) {
            String msg = "An exception occurred reading from the Iterator.  Cause: " + sql;
            LOGGER.error(msg, sql);
            IllegalStateException e = new IllegalStateException(msg);
            e.initCause(sql);
            throw e;
        }
    }

    /**
     * The remove operation is unsupported on the ResultSetIterator.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException(Bundle.getErrorString("IteratorFactory_Iterator_removeUnsupported",
                new Object[]{this.getClass().getName()}));
    }

    private static final Object convertRow(final ResultSet resultSet, final String[] columnNames)
            throws SQLException {
        SortedMap map = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < columnNames.length; i++) {
            Object value = resultSet.getObject(i + 1);
            if (resultSet.wasNull())
                value = null;
            map.put(columnNames[i], value);
        }
        return map;
    }
}
