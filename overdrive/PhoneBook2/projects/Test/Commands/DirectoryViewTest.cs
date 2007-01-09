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
	public class EntryFindTest : BaseTest
	{
		/// <summary>
		/// Confirm that Context contains the expected attributes for the list filter-0ps.
		/// </summary>
		/// 
		[Test]
		public void ContainsFilters()
		{
			IRequestContext context = catalog.ExecuteRequest(App.ENTRY_FIND);
			AssertNominal(context);
			string[] FILTERS = {App.LAST_NAME_LIST, App.FIRST_NAME_LIST, App.EXTENSION_LIST, App.USER_NAME_LIST, App.HIRED_LIST, App.HOURS_LIST};
			foreach (string filter in FILTERS)
			{
				Assert.IsTrue(context.Contains(filter), filter + ": Expected context to contain key.");
			}
		}

		/// <summary>
		/// Confirm that Helper contains the expected command.
		/// </summary>
		/// 
		[Test]
		public void HelperContains()
		{
			IViewHelper helper = catalog.GetHelperFor(App.ENTRY_FIND);
			IRequestCommand command = helper.Command;
			Assert.IsNotNull(command, "Expected Helper to have a Command");
			Assert.AreEqual(App.ENTRY_FIND, command.ID, "Expected Helper to have View Command.");
		}
	}
}