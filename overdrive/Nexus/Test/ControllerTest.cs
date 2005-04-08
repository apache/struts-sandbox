using NUnit.Framework;

namespace Nexus.Core
{
	/// <summary>
	/// Exercise IController per [OVR-8].
	/// </summary>
	[TestFixture]
	public class ControllerTest : BaseNexusTest
	{

		/// <summary>
		/// A simple "list all" command should return nominal with an outcome. 
		/// </summary>
		[Test]
		public void ExecuteContext()
		{
			IRequestContext context = controller.ExecuteContext(App.LIST_ALL);
			AssertNominal(context);
			Assert.IsTrue (context.IsNominal,"Expected nominal result.");
			Assert.IsTrue(context.HasOutcome,"Expected outcome from command.");
		}
	}
}
