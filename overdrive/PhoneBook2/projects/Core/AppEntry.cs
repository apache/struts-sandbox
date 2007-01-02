/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use _Store file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System;
using System.Collections;

namespace PhoneBook.Core
{
    /// <summary>
    /// Expose field attributes as public properties.
    /// </summary>
    /// 
    [Serializable]
    public class AppEntry
    {
        /// <summary>
        /// Internal storage.
        /// </summary>
        /// 
        private IDictionary _Value = new Hashtable(5);

        /// <summary>
        /// Add each source entry to our internal store. 
        /// </summary>
        /// <remarks><p>
        /// Entries with keys that match the property names will be exposed. 
        /// Other entries may be added, but can only be retrieved via Get.
        /// </p></remarks>
        /// <param name="sources">Entries to add</param>
        /// 
        public void AddAll(IDictionary sources)
        {
            ICollection keys = sources.Keys;
            foreach (string key in keys)
            {
                Add(key, sources[key] as string);
            }
        }

        /// <summary>
        /// Add a single entry to our internal store.
        /// </summary>
        /// <remarks><p>
        /// Entries with keys that match the property names will be exposed. 
        /// Other entries may be added, but can only be retrieved via Get.
        /// </p></remarks>
        /// <param name="key">ID for entry</param>
        /// <param name="value">Content for entry</param>
        /// 
        public void Add(string key, string value)
        {
            _Value.Add(key, value);
        }

        /// <summary>
        /// Provide the value corresponding to key from the internal store.
        /// </summary>
        /// <param name="key">ID for entry</param>
        /// <returns>Content for entry</returns>
        /// 
        public string Get(string key)
        {
            return _Value[key] as string;
        }

        /// <summary>
        /// Set an entry to the internal store, overwriting any existing entry.
        /// </summary>
        /// <remarks><p>
        /// This is a protected method used by the Properties. 
        /// Use an existing Property to set values, 
        /// or extend the class to include other Properties. 
        /// </p></remarks>
        /// <param name="key"></param>
        /// <param name="value"></param>
        protected void Set(string key, string value)
        {
            _Value[key] = value;
        }


        /*
        public string Property
        {
            get { return Get(App.PROPERTY); }
            set { Set(App.PROPERTY, value); }
        }
        */

        public string entry_key
        {
            get { return Get(App.ENTRY_KEY); }
            set { Set(App.ENTRY_KEY, value); }
        }

        public string first_name
        {
            get { return Get(App.FIRST_NAME); }
            set { Set(App.FIRST_NAME, value); }
        }

        public string last_name
        {
            get { return Get(App.LAST_NAME); }
            set { Set(App.LAST_NAME, value); }
        }

        public string extension
        {
            get { return Get(App.EXTENSION); }
            set { Set(App.EXTENSION, value); }
        }

        public string user_name
        {
            get { return Get(App.USER_NAME); }
            set { Set(App.USER_NAME, value); }
        }

        public string hired
        {
            get { return Get(App.HIRED); }
            set { Set(App.HIRED, value); }
        }

        public string hours
        {
            get { return Get(App.HOURS); }
            set { Set(App.HOURS, value); }
        }

        public string editor
        {
            get { return Get(App.EDITOR); }
            set { Set(App.EDITOR, value); }
        }
    }
}