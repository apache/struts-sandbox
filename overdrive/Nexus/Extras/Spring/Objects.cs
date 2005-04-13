/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System;
using Spring.Context;
using Spring.Context.Support;
using Spring.Objects.Factory.Xml;

namespace Nexus.Extras.Spring
{
	/// <summary>
	/// A singleton configuration loader for Spring. 
	/// </summary>
	public class Objects
	{
		// Controller: Obviously, this should be configurable, like iBATIS. 

		private static string[] files = {
			"/Resources/Command/AppConfig.xml",
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