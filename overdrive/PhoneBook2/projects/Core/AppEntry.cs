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
using Nexus.Core;

namespace PhoneBook.Core
{
    /// <summary>
    /// Expose field attributes as public properties.
    /// </summary>
    /// 
    [Serializable]
    public class AppEntry : EntryDictionary
    {

        public AppEntry()
        {
            // Default contstructor	
        }

        public AppEntry(IDictionary sources)
        {
            AddAll(sources);
        }

        public AppEntry(AppEntry row)
        {
            AddAll(row);
        }

        /// <summary>
        /// Add each source entry to our internal store. 
        /// </summary>
        /// <remarks><p>
        /// Entries with keys that match the property names will be exposed. 
        /// Other entries may be added, but can only be retrieved via Get.
        /// </p></remarks>
        /// <param name="row">Entries to add</param>
        /// 
        public void AddAll(AppEntry row)
        {
            ICollection keys = row.Keys;
            foreach (string key in keys)
            {
                Add(key, row.Get(key));
            }
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