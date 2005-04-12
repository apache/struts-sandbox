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

namespace Nexus.Core
{
	/// <summary>
	/// Tokens representing context keys.
	/// </summary>
	public class Tokens
	{
		private Tokens ()
		{
			// No need to construct static helper class
		}

		/// <summary>
		/// Token for Command property.
		/// </summary>
		public const string COMMAND = "__COMMAND";

		/// <summary>
		/// Token for CommandBin property.
		/// </summary>
		public const string COMMAND_BIN = "__COMMAND_BIN";

		/// <summary>
		/// Token for Errors property.
		/// </summary>
		public const string ERRORS = "__ERRORS";

		/// <summary>
		/// Token for a generic message.
		/// </summary>
		public const string GENERIC_MESSAGE = "__GENERIC_MESSAGE";

		/// <summary>
		/// Token for Fault property.
		/// </summary>
		public const string FAULT = "__FAULT";


	}
}