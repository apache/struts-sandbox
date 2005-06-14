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
	/// <remarks><p>
	/// The common convention of using CAPITALS for constants 
	/// is not followed for properties because using the same 
	/// case as the Property simplifies the use of a simple 
	/// template to create new members.
	/// </p></remarks>
	/// 
	public class Tokens
	{
		private Tokens ()
		{
			// No need to construct static helper class
		}

		#region Properties

		/*
		get { return Context[Tokens.Property] as string; }
		set { Context[Tokens.Property] = value; }

		/// <summary>
		/// Token for Property property.
		/// </summary>
		/// 
		public const string Property = "_Property";
		*/
		
		/// <summary>
		/// Token for a generic message property.
		/// </summary>
		/// <remarks><p>
		/// A dot is used to communicate the idea 
		/// that the message catagory has no name
		/// and to avoid using a language constant 
		/// in a language-neutral content.
		/// </p></remarks>
		/// 
		public const string GenericMessage = ".";

		/// <summary>
		/// Token for Command property.
		/// </summary>
		/// 
		public const string Command = "_Command";

		/// <summary>
		/// Token for CommandBin property.
		/// </summary>
		/// 
		public const string CommandBin = "_CommandBin";

		/// <summary>
		/// Token for Alerts property.
		/// </summary>
		/// 
		public const string Alerts = "_Alerts";

		/// <summary>
		/// Token for Hints property.
		/// </summary>
		public const string Hints = "_Hints";

		/// <summary>
		/// Token for FieldTable property.
		/// </summary>
		/// 
		public const string FieldTable = "_FieldTable";

		/// <summary>
		/// Token for FieldSet property.
		/// </summary>
		/// 
		public const string FieldSet = "_FieldSet";

		/// <summary>
		/// Token for Fault property.
		/// </summary>
		/// 
		public const string Fault = "_Fault";

		/// <summary>
		/// Token for Prefix property.
		/// </summary>
		/// 
		public const string Prefix = "_Prefix";

		/// <summary>
		/// Token for ListSuffix property.
		/// </summary>
		/// 
		public const string ListSuffix = "_ListSuffix";

		/// <summary>
		/// Token for NullIfEmpty property.
		/// </summary>
		/// 
		public const string NullIfEmpty = "_NullIfEmpty";

		/// <summary>
		/// Token for SelectItemPrompt property.
		/// </summary>
		/// 
		public const string SelectItemPrompt = "_SelectItemPrompt";

		/// <summary>
		/// Token for Catalog property.
		/// </summary>
		/// 
		public const string Catalog = "_Catalog";

		#endregion

		#region Command elements

		/// <summary>
		/// Token for the pre-op command element.
		/// </summary>
		/// 
		public const string PRE_OP = "pre-op";

		/// <summary>
		/// Token for the post-op command element.
		/// </summary>
		/// 
		public const string POST_OP = "post-op";

		#endregion

	}
}