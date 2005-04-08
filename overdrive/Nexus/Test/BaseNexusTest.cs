using System.Collections;
using System.Text;
using Agility.Core;
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

		public void AssertNoFault (IRequestContext context)
		{
			bool hasFault = context.HasFault;
			if (hasFault)
				Assert.Fail (context.Fault.Message);
		}

		public void AssertNominal (IRequestContext context)
		{
			AssertNoFault (context);
			bool hasErrors = context.HasErrors;
			if (hasErrors)
			{
				StringBuilder outer = new StringBuilder ();
				IContext store = context.Errors;
				ICollection keys = store.Keys;
				foreach (string key in keys)
				{
					StringBuilder inner = new StringBuilder ();
					inner.Append (key);
					inner.Append (": ");
					IList messages = store [key] as IList;
					foreach (string message in messages)
					{
						inner.Append (message);
						inner.Append (";");
					}
					outer.Append (inner.ToString ());
					outer.Append ("/n");
				}
				Assert.Fail (outer.ToString ());
			}
		}
	}
}
