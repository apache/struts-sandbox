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
using Nexus.Core.Helpers;
using NUnit.Framework;

namespace PhoneBook.Core.Commands
{

    /// <summary>
    /// Exercise formatting processors.
    /// </summary>
    /// 
    [TestFixture]
    public class FormatEntry : BaseTest
    {

        public const string ENTRY_KEY_VALUE_FORMAT = "c5b6bbb1-66d6-49cb-9db6-743af6627828";        

        private AppEntry getAppEntry()
        {
            IViewHelper helper = catalog.GetHelperFor(App.ENTRY);
            helper.Criteria[App.ENTRY_KEY] = ENTRY_KEY_VALUE_FORMAT;
            helper.Execute();
            AssertNominal(helper);
            AppEntry entry = new AppEntry(helper.Criteria);
            return entry;
        }
        
        [Test]
        public void Hired()
        {
            AppEntry entry = getAppEntry();
            string hired = entry.hired;
            Assert.IsNotNull(hired, "Expected row to have a hired date in string format.");
            Assert.IsTrue(hired.Length < "##/##/#### ".Length, hired + ": Expected short date format.");
        }

        [Test]
        public void extension()
        {
            AppEntry entry = getAppEntry();
            string extension = entry.extension;
            Assert.IsNotNull(extension, "Expected each row to have an extension.");
            Assert.IsTrue(extension.Length > "1234567890".Length, extension + ": Expected formatted extension.");
        }

    }
}