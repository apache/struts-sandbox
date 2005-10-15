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
	/// Extend RequestCommand with data access methods.
	/// </summary>
	/// 
	public abstract class BaseMapper : RequestCommand
	{
		/// <summary>
		/// Provide a field for Mapper property.
		/// </summary>
		/// 
		private SqlMapper _Mapper;

		/// <summary>
		/// Expose a preconfigured SqlMapper instance that Commands can use to run statements.
		/// </summary>
		/// <remarks><p>
		/// Commands use Mapper to invoke SqlMap statements, such as 
		/// <code>
		/// object row = Mapper.QueryForObject (QueryID, context);
		/// </code>.
		/// </p><p>
		/// Any SqlMapper API method may be called. 
		/// </p><p>
		/// The default behavior of BAseNexusCommand is to use the 
		/// command ID if the QueryID is null.
		/// </p></remarks>
		/// <returns>Preconfigured Mapper instance</returns>
		/// 
		public SqlMapper Mapper
		{
			get { return _Mapper; }
			set { _Mapper = value; }

		}

		/// <summary>
		/// Indicate whether string is null or zero length.
		/// </summary>
		/// <param name="input">Input to validate</param>
		/// <returns>True if string is nyull or zero length</returns>
		/// 
		public bool IsEmpty(string input)
		{
			return ((input == null) || (input.Equals(String.Empty)));
		}

		/// <summary>
		/// Create new Global Universal Identifer as a formatted string.
		/// </summary>
		/// <returns>String representing a new GUID</returns>
		/// <remarks><p>
		/// No two calls to this method will ever return duplicate strings.
		/// </p></remarks>
		/// 
		public string GuidString()
		{
			Guid guid = Guid.NewGuid();
			string gs = guid.ToString();
			return gs;
		}

	}
}