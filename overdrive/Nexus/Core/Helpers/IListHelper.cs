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
	/// Helper for controls that display a list [OVR-11].
	/// </summary>
	public interface IListHelper : IViewHelper
	{
		/// <summary>
		/// Text to display as the title for a display. 
		/// </summary>
		string TitleText { get; set; }

		/// <summary>
		/// Text to display as a prompt to the user as to the next step.
		/// </summary>
		string PromptText { get; set; }

		/// <summary>
		/// ID of the command to execute to setup a Find dialog. 
		/// </summary>
		string FindCommand { get; set; }

		/// <summary>
		/// ID of the command to execute to list entries matching search critera.
		/// </summary>
		string ListCommand { get; set; }

		/// <summary>
		/// The name of the key field for a data set.
		/// </summary>
		string DataKeyField { get; set; }

		/// <summary>
		/// The list of field IDs to display as a row.
		/// </summary>
		IList DataFields { get; set; }

		/// <summary>
		/// The list of field labels to display with each column in a row (in the same order).
		/// </summary>
		/// <remarks>
		/// If ommitted, the DataField ID is used instead.
		/// </remarks>
		IList DataLabels { get; set; }
	}
}