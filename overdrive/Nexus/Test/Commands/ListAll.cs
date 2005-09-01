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
using System.Collections;

namespace Nexus.Core.Commands
{
	/// <summary>
	/// Return a list as the outcome.
	/// </summary>
	/// 
	public class ListAll : RequestCommand
	{
		/// <summary>
		/// Test ID for Command.
		/// </summary>
		/// 
		public const string LIST_ALL = "ListAll";

		/// <summary>
		/// Fake name for test data.
		/// </summary>
		/// 
		private const string DATA = "data";

		public override bool RequestExecute(IRequestContext context)
		{
			// IList list = Mapper.Get ().QueryForList (ID, context);
			// Fake it:
			IList list = new ArrayList();
			list.Add(DATA);
			context.Outcome = list;
			return CONTINUE;
		}
	}
}