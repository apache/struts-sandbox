using NUnit.Framework;

namespace Nexus.Core
{
	/// <summary>
	/// Summary description for BaseNexusTest.
	/// </summary>
	[TestFixture]
	public class BaseNexusTest
	{

		protected IController controller;

		[SetUp]
		public virtual void SetUp ()
		{
			// TODO: Implement Objects.Facotory [OVR-8]
			// IApplicationContext factory = Objects.Factory ();
			// controller = new Controller (factory);
		}
	}
}
