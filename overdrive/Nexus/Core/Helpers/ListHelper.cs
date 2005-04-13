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

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// Standard implementation of IListHelper.
	/// </summary>
	public class ListHelper : ViewHelper, IListHelper
	{
		private string _TitleText = null;
		public virtual string TitleText
		{
			get { return _TitleText; }
			set { _TitleText = value; }
		}

		private string _PromptText = null;
		public virtual string PromptText
		{
			get { return _PromptText; }
			set { _PromptText = value; }
		}

		private string _FindCommand = null;
		public virtual string FindCommand
		{
			get { return _FindCommand; }
			set { _FindCommand = value; }
		}

		private string _ListCommand = null;
		public virtual string ListCommand
		{
			get { return _ListCommand as string; }
			set { _ListCommand = value; }
		}

		private string _DataKeyField = null;
		public virtual string DataKeyField
		{
			get { return _DataKeyField; }
			set { _DataKeyField = value; }
		}

		private IList _DataFields = null;
		public virtual IList DataFields
		{
			get { return _DataFields; }
			set { _DataFields = value; }
		}

		private IList _DataLabels = null;
		public virtual IList DataLabels
		{
			get { return _DataLabels; }
			set { _DataLabels = value; }
		}
	}
}