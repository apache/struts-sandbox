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
	/// Provide tokens representing context keys.
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
		/// Token for Alert property.
		/// </summary>
		/// 
		public const string Alert = "_Alert";

		/// <summary>
		/// Token for Alerts property.
		/// </summary>
		/// 
		public const string Alerts = "_Alerts";

		/// <summary>
		/// Token for Catalog property.
		/// </summary>
		/// 
		public const string Catalog = "_Catalog";

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
		/// Token for ControlTypeName ControlTypeName.
		/// </summary>
		/// 
		public const string ControlTypeName = "_ControlTypeName";

		/// <summary>
		/// Token for Criteria property.
		/// </summary>
		/// 
		public const string Criteria = "_Criteria";

		/// <summary>
		/// Token for DataFormat DataFormat.
		/// </summary>
		/// 
		public const string DataFormat = "_DataFormat";

		/// <summary>
		/// Token for DataType DataType.
		/// </summary>
		/// 
		public const string DataType = "_DataType";

		/// <summary>
		/// Token for DataTypeName DataTypeName.
		/// </summary>
		/// 
		public const string DataTypeName = "_DataTypeName";

		/// <summary>
		/// Token for Fault property.
		/// </summary>
		/// 
		public const string Fault = "_Fault";

		/// <summary>
		/// Token for FieldKey property.
		/// </summary>
		/// 
		public const string FieldKey = "_FieldKey";

		/// <summary>
		/// Token for FieldSet property.
		/// </summary>
		/// 
		public const string FieldSet = "_FieldSet";

		/// <summary>
		/// Token for FieldTable property.
		/// </summary>
		/// 
		public const string FieldTable = "_FieldTable";

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
		/// Token for Hint property.
		/// </summary>
		public const string Hint = "_Hint";

		/// <summary>
		/// Token for Hints property.
		/// </summary>
		public const string Hints = "_Hints";

		/// <summary>
		/// Token for Help property.
		/// </summary>
		public const string Help = "_Help";

		/// <summary>
		/// Token for ID ID.
		/// </summary>
		/// 
		public const string ID = "_ID";

		/// <summary>
		/// Token for Label Label.
		/// </summary>
		/// 
		public const string Label = "_Label";

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
		/// Token for Prefix property.
		/// </summary>
		/// 
		public const string Prefix = "_Prefix";

		/// <summary>
		/// Token for SelectItemPrompt property.
		/// </summary>
		/// 
		public const string SelectItemPrompt = "_SelectItemPrompt";

		/// <summary>
		/// Token for Source property.
		/// </summary>
		/// 
		public const string Source = "_Source";

		/// <summary>
		/// Token for Target property.
		/// </summary>
		/// 
		public const string Target = "_Target";

		#endregion

		#region Command elements

		/// <summary>
		/// Token for the pre-op command element.
		/// </summary>
		/// 
		public const string PRE_OP_ID = "pre-op";

		/// <summary>
		/// Token for the post-op command element.
		/// </summary>
		/// 
		public const string POST_OP_ID = "post-op";

		/// <summary>
		/// Token for FieldTable command element.
		/// </summary>
		/// 
		public const string FIELD_TABLE_ID = "FieldTable";

		#endregion

		#region Control Type Names

		/// <summary>
		/// Token for input Control Type Name.
		/// </summary>
		public const string INPUT_CONTROL = "input";

		/// <summary>
		/// Token for select Control Type Name.
		/// </summary>
		public const string SELECT_CONTROL = "select";

		#endregion
	}
}