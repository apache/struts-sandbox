using Nexus.Core;
using Nexus.Core.Helpers;
using NUnit.Framework;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Exercise Directory View Command.
	/// </summary>
	/// 
	[TestFixture]
	public class DirectoryViewTest : BaseTest
	{
		/// <summary>
		/// Confirm that Context contains the expected attributes for the list filters.
		/// </summary>
		/// 
		[Test]
		public void ContainsFilters ()
		{
			IRequestContext context = catalog.ExecuteRequest (App.DIRECTORY_VIEW);
			string[] FILTERS = {App.LAST_NAME_LIST, App.FIRST_NAME_LIST, App.EXTENSION_LIST, App.USER_NAME_LIST, App.HIRED_LIST, App.HOURS_LIST};
			foreach (string filter in FILTERS)
			{
				Assert.IsTrue (context.Contains (filter), filter + ": Expected context to contain key.");
			}
		}

		/// <summary>
		/// Confirm that Helper contains the expected command.
		/// </summary>
		[Test]
		public void HelperContains ()
		{
			IViewHelper helper = catalog.GetHelper (App.DIRECTORY_FIND_HELPER);
			IRequestCommand command = helper.Command;
			Assert.IsNotNull (command, "Expected Helper to have a Command");
			Assert.AreEqual (App.DIRECTORY_VIEW, command.ID, "Expected Helper to have View Command.");
		}
	}
}