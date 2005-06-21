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
		/// Confirm that context contains the expected attributes for the list filters.
		/// </summary>
		/// 
		[Test]
		public void ContainsFilters ()
		{
			IRequestContext context = catalog.ExecuteRequest (App.DIRECTORY_VIEW);
			string[] keys = {App.LIST_LAST_NAMES};
			foreach (string key in keys)
			{
				Assert.IsTrue (context.Contains (key), key + ": Expected context to contain key.");
			}
		}

		// TODO: [Test]
		public void HelperContains ()
		{
			IViewHelper helper = catalog.GetHelper (App.DIRECTORY_VIEW_HELPER);
			IRequestCommand command = helper.Command;
			Assert.IsNotNull (command, "Expected Helper to have a Command");
			Assert.AreEqual (App.DIRECTORY_VIEW, command.ID, "Expected Helper to have View Command.");
		}
	}
}