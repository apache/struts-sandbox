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

namespace Nexus.Core
{
	/// <summary>
	/// Implement IKeyValue.
	/// </summary>
	[Serializable]
	public class KeyValue : IKeyValue
	{

		/// <summary>
		/// Expose name of "Key" field.
		/// </summary>
		/// <remarks><p>
		/// Use this field for the DataValueField (sic).
		/// </p></remarks>
		public const string KEY = "Key";

		/// <summary>
		/// Expose name of "Value" field.
		/// </summary>
		/// <remarks><p>
		/// Use this field for the DataTextField (sic).
		/// </p></remarks>
		public const string VALUE= "Value";

		public KeyValue()
		{
			;
		}

		public KeyValue(string aKey, object aValue)
		{
			_Key = aKey;
			_Value = aValue;
		}

		protected string _Key;

		public virtual string Key
		{
			get { return _Key; }
			set { _Key = value; }
		}

		protected object _Value;

		public virtual object Value
		{
			get { return _Value; }
			set { _Value = value; }
		}

		public virtual string Text
		{
			get { return _Value as string; }
			set { _Value = Text; }
		}

		public override string ToString()
		{
			return Text;
		}

	}
}