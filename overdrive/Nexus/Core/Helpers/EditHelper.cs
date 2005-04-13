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

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// Standard implementation of IEditHelper.
	/// </summary>
	public class EditHelper : ListHelper, IEditHelper
	{
		public const string msg_EDIT_COMMAND = "EDIT";
		public const string msg_QUIT_COMMAND = "CANCEL";
		public const string msg_SAVE_COMMAND = "SAVE";

		public EditHelper () : base ()
		{
			EditText = msg_EDIT_COMMAND;
			QuitText = msg_QUIT_COMMAND;
			SaveText = msg_SAVE_COMMAND;
		}

		private string _EditText = null;
		public virtual string EditText
		{
			get { return _EditText; }
			set { _EditText = value; }
		}

		private string _QuitText = null;
		public virtual string QuitText
		{
			get { return _QuitText; }
			set { _QuitText = value; }
		}

		private string _SaveText = null;
		public virtual string SaveText
		{
			get { return _SaveText; }
			set { _SaveText = value; }
		}

		private string _SaveCommand = null;
		public virtual string SaveCommand
		{
			get { return _SaveCommand; }
			set { _SaveCommand = value; }
		}
	}
}