using System;
using Spring.Context;
using Spring.Context.Support;
using Spring.Objects.Factory.Xml;

namespace Agility.Extras.Spring
{
	/// <summary>
	/// A singleton implementation of the IHelper protocol, 
	/// using static methods and the Spring object factory.
	/// </summary>
	/// <remarks><p>
	/// Since static methods are used here, as a convenience, 
	/// it is not possible to implement IHelper. 
	/// However, the same method signatures are otherwise used.
	/// </p><p>
	/// Of course, an alternative is to provide the Factory
	/// method a singlton, and then obtain the Helpers 
	/// class from the Factory, but implementing the 
	/// IHelpers interface as a singleton seems simpler.
	/// </p></remarks>
	public class Objects
	{
		private static string FILE = "/Objects.xml";

		private Objects()
		{
			// private constructor prevents instantiation. 
		}

		// ISSUE: Remove this kludge and adopt latest iBATIS approach.
		private static string _rootDirectory =
			AppDomain.CurrentDomain.BaseDirectory.Replace(@"\bin", "").Replace(@"\Debug", "").Replace(@"\Release", "");

		private static volatile IApplicationContext _Factory = null;

		public static IApplicationContext Factory()
		{
			if (_Factory == null)
			{
				lock (typeof (XmlObjectFactory))
				{
					string foo = "file://" + _rootDirectory + FILE;
					if (_Factory == null) // double-check 
						_Factory = new XmlApplicationContext(foo);
				}
			}
			return _Factory;
		}
	}
}