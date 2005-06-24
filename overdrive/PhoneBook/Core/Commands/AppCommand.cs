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
using IBatisNet.DataMapper;
using Nexus.Core;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Add data access methods to RequestCommand.
	/// </summary>
	/// 
	public abstract class AppCommand : RequestCommand
	{
		public SqlMapper Mapper ()
		{
			// return IBatisNet.DataMapper.Mapper.Instance();
			return IBatisNet.DataMapper.Mapper.Instance ();
		}

		public bool IsEmpty (string input)
		{
			return ((input == null) || (input.Equals (String.Empty)));
		}

		public string GuidString ()
		{
			Guid guid = Guid.NewGuid ();
			string gs = guid.ToString ();
			return gs;
		}

	}
}