/*
* Copyright 2005 The Apache Software Foundation.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
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

namespace PhoneBook.Core
{
    /// <summary>
    /// Tokens representing context keys.
    /// </summary>
    /// 
    public class App
    {
        private App()
        {
            // No need to construct static helper class
        }

        #region Properties

        /// <summary>
        /// Token for Catalog property.
        /// </summary>
        /// 
        public const string CATALOG_KEY = "Catalog";

        /// <summary>
        /// Token for entry_key property.
        /// </summary>
        /// 
        public const string ENTRY_KEY = "entry_key";

        /// <summary>
        /// Token for first_name property.
        /// </summary>
        /// 
        public const string FIRST_NAME = "first_name";

        /// <summary>
        /// Token for last_name property.
        /// </summary>
        /// 
        public const string LAST_NAME = "last_name";

        /// <summary>
        /// Token for user_name property.
        /// </summary>
        /// 
        public const string USER_NAME = "user_name";

        /// <summary>
        /// Token for extension property.
        /// </summary>
        /// 
        public const string EXTENSION = "extension";

        /// <summary>
        /// Token for hired property.
        /// </summary>
        /// 
        public const string HIRED = "hired";

        /// <summary>
        /// Token for hours property.
        /// </summary>
        /// 
        public const string HOURS = "hours";

        /// <summary>
        /// Token for editor property.
        /// </summary>
        /// 
        public const string EDITOR = "editor";

        #endregion

        #region Commands

        public const string FROM_DATE = "from_date";

        public const string THRU_DATE = "thru_date";

        /// <summary>
        /// Token for SQL LIMIT clause.
        /// </summary>
        /// 
        public const string ITEM_LIMIT = "item_limit";

        /// <summary>
        /// Token for SQL OFFSET clause.
        /// </summary>
        /// 
        public const string ITEM_OFFSET = "item_offset";

        /// <summary>
        /// Token for SQL COUNT clause.
        /// </summary>
        /// 
        public const string ITEM_COUNT = "item_count";

        /// <summary>
        /// Token for select one command.
        /// </summary>
        /// 
        public const string ENTRY = "entry";

        /// <summary>
        /// Token for list all command.
        /// </summary>
        /// 
        public const string ENTRY_LIST = "entry_list";

        /// <summary>
        /// Token for Entry Initial command.
        /// </summary>
        /// 
        public const string ENTRY_INITIAL = "entry_initial";

        /// <summary>
        /// Token for List Last Names command.
        /// </summary>
        /// 
        public const string LAST_NAME_LIST = "last_name_list";

        /// <summary>
        /// Token for List Last Names command.
        /// </summary>
        /// 
        public const string FIRST_NAME_LIST = "first_name_list";

        /// <summary>
        /// Token for List Extensions command.
        /// </summary>
        /// 
        public const string EXTENSION_LIST = "extension_list";

        /// <summary>
        /// Token for List UserNames command.
        /// </summary>
        /// 
        public const string USER_NAME_LIST = "user_name_list";

        /// <summary>
        /// Token for List Hire Dates command.
        /// </summary>
        /// 
        public const string HIRED_LIST = "hired_list";

        /// <summary>
        /// Token for List Hours command.
        /// </summary>
        /// 
        public const string HOURS_LIST = "hours_list";

        /// <summary>
        /// Token for Entry Find command.
        /// </summary>
        /// 
        public const string ENTRY_FIND = "entry_find";

        /// <summary>
        /// Token for Entry Save command.
        /// </summary>
        /// 
        public const string ENTRY_SAVE = "entry_save";

        /// <summary>
        /// Token for Entry Delete command.
        /// </summary>
        /// 
        public const string ENTRY_DELETE = "entry_delete";

        #endregion

        #region Messages

        /// <summary>
        /// Token for Directory page title.
        /// </summary>
        public const string DIRECTORY_TITLE = "directory_title";

        /// <summary>
        /// Token for Directory directory page heading.
        /// </summary>
        public const string DIRECTORY_HEADING = "directory_heading";

        /// <summary>
        /// Token for Directory page prompt.
        /// </summary>
        public const string DIRECTORY_PROMPT = "directory_prompt";

        #endregion
    }
}