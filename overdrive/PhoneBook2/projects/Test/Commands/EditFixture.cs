using System.Collections;
using NUnit.Framework;

namespace PhoneBook.Core.Commands
{
    /// <summary>
    /// Exercise editing commands.
    /// </summary>
    /// 
    [TestFixture]
    public class EditFixture : BaseTest
    {

        public const string ENTRY_KEY_VALUE = "12345678-1234-123456789-1234567890AB";
        public const string FIRST_NAME_VALUE = App.FIRST_NAME;
        public const string LAST_NAME_VALUE = App.LAST_NAME;
        public const string USER_NAME_VALUE = App.USER_NAME;
        public const string EXTENSION_VALUE = "1234567890";
        public const string HIRED_VALUE = "2002-07-31";
        public const string HOURS_VALUE = "20";
        public const string EDITOR_VALUE = "1";

        protected override void Populate(IDictionary context)
        {
            context[App.ENTRY_KEY] = ENTRY_KEY_VALUE;
            context[App.FIRST_NAME] = FIRST_NAME_VALUE;
            context[App.LAST_NAME] = LAST_NAME_VALUE;
            context[App.USER_NAME] = USER_NAME_VALUE;
            context[App.EXTENSION] = EXTENSION_VALUE;
            context[App.HIRED] = HIRED_VALUE;
            context[App.HOURS] = HOURS_VALUE;
            context[App.EDITOR] = EDITOR_VALUE;
        }
        
                        
        [Test]
        public void InsertDelete()
        {
            AssertInsertDelete(App.ENTRY_SAVE, App.ENTRY_KEY, ENTRY_KEY_VALUE, App.ENTRY_DELETE);
        }
        
   }
}