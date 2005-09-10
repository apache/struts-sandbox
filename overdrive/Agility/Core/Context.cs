/*
 * Copyright 1999-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System.Collections;

namespace Agility.Core
{
	/// <summary>
	/// Concrete {@link IContext} implementation.
	/// </summary>
	public class Context : Hashtable, IContext
	{
		/// <summary>
		/// Default constructor.
		/// </summary>
		public Context()
		{
		}

		/// <summary>
		/// Convenience constructor to create new Context 
		/// and add a new item.
		/// </summary>
		/// <param name="key">Index for entry</param>
		/// <param name="_value">Value for entry</param>
		public Context(string key, object _value)
		{
			this.Add(key, _value);
		}
	}
}