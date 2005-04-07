using System;
using Spring.Context;
using Spring.Context.Support;
using Spring.Objects.Factory.Xml;

namespace Agility.Extras.Spring
{
	/// <summary>
	/// A singleton configuration loader for Spring. 
	/// </summary>
	public class Objects
	{

		// Controller: Obviously, this should be configurable, like iBATIS. 

		private static string[] files = {
			"/Resources/Command/Catalog.xml"
		};

		private Objects ()
		{
			// private constructor prevents instantiation. 
		}

		private static string _rootDirectory =
			AppDomain.CurrentDomain.BaseDirectory.Replace (@"\bin", "").Replace (@"\Debug", "").Replace (@"\Release", "");

		private static volatile IApplicationContext _Factory = null;

		public static IApplicationContext Factory ()
		{
			if (_Factory == null)
			{
				lock (typeof (XmlObjectFactory))
				{
					int i = 0;
					string[] foo = new string[files.Length];
					foreach (string f in files)
					{
						foo [i] = "file://" + _rootDirectory + f;
						i++;
					}
					if (_Factory == null) // double-check 
						_Factory = new XmlApplicationContext (foo);
				}
			}
			return _Factory;
		}

	}
}