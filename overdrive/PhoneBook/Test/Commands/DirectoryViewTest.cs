using Nexus.Core;
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
		public void ContainsFilters()
		{

			IRequestContext context = catalog.ExecuteRequest (App.DIRECTORY_VIEW);
			string[] keys = {App.LIST_LAST_NAMES};
			foreach (string key in keys)
			{
				Assert.IsTrue (context.Contains (key),key + ": Expected context to contain key.");
			}

			
		}
	}
}
