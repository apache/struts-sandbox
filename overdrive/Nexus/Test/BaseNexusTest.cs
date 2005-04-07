using Agility.Extras.Spring;
using NUnit.Framework;
using Spring.Context;

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
			// TODO: Implement Objects.Factory [OVR-8]
			IApplicationContext factory = Objects.Factory ();
			controller = new Controller (factory);
		}
	}
}
