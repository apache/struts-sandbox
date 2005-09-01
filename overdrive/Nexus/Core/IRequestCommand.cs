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
using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Extend ICommand to utilize an IRequestContext [OVR-9]
	/// </summary>
	/// <remarks><p>
	/// Rather than have each command cast its context to an 
	/// IRequestContext, 
	/// provide a IRequestCommand with an alternative signature. 
	/// </p></remarks>
	/// 
	public interface IRequestCommand : ICommand
	{
		/// <summary>
		/// An identifier for this Command. 
		/// </summary>
		/// <remarks>
		/// Corresponds to the Command property of IHelperContext. 
		/// </remarks>
		/// <returns>An identifier for this Command.</returns>
		/// 
		string ID { get; set; }

		/// <summary>
		/// An identifier for a Query associated with this Command (if any).
		/// </summary>
		/// <remarks>
		/// If Query is not set, then ID is returned instead. 
		/// (The default QueryID is the command ID.) 
		/// </remarks>
		/// <returns>An identifier for this Command.</returns>
		string QueryID { get; set; }

		/// <summary>
		/// Factory method to provide an empty context that can be used 
		/// with the Command instance.
		/// </summary>
		/// <returns>Context instance with Command ID set.</returns>
		/// 
		IRequestContext NewContext();

		/// <summary>
		/// Field IDs required by this Command.
		/// </summary>
		/// <remarks><p>
		/// If requisite fields are not present in the main Context, 
		/// appropriate errors should be posted to the Errors property, 
		/// so that the client can correct the oversight and resubmit the request.
		/// </p><p>
		/// The RelatedIDs property may be used by the command itself, 
		/// or by a collaborating "conversion" command, 
		/// to confirm that related fields, when present, are in the expected format. 
		/// </p><p>
		/// The RelatedIDs property may be used by the command itself, 
		/// or by a collaborating "validation" command, 
		/// to confirm that related fields, when present, are in the expected format. 
		/// </p></remarks>
		IList RequiredIDs { get; set; }

		/// <summary>
		/// Add a IList of IDs to the list of {@link RequiredIds}.
		///</summary>
		IList AddRequiredIDs { set; }

		/// <summary>
		/// Record FieldContext IDs related to this Command, including any RequiredIDs.
		/// </summary>
		/// <remarks><p>
		/// If a Field ID is not specified as a  RelatedID or a RequiredID, 
		/// than it may not be passed from the Criteria to the main Context, 
		/// and so will not be available to the Command. 
		/// </p><p>
		/// As the field is passed from the Fieldstate to the main Context, 
		/// it may also be converted to the appropriate DataType or string format. 
		/// A collaborating Command may reference the FieldTable in a INexusContext 
		/// to ascertain the expected type or format for a value and 
		/// to obtain the appropriate error messages for each field. 
		/// </p><p>
		/// The RelatedIDs property may be used by the command itself, 
		/// or by a collaborating "conversion" command, 
		/// to confirm that related fields, when present, are in the expected format. 
		/// </p><p>
		/// Since posting errors and messages is a specialized concern, 
		/// it is recommended that collaborating Commands handle validation and confirmation.
		/// </p><p>
		/// A collaborating Command may reference the FieldTable in a INexusContext 
		/// to ascertain the expected format for a value and 
		/// to obtain the appropriate error messages for each field. 
		/// </p></remarks>
		IList RelatedIDs { get; set; }

		/// <summary>
		/// Add a IList of IDs to the list of {@link RelatedIds}.
		///</summary>
		IList AddRelatedIDs { set; }

		/// <summary>
		/// Record Field IDs provided during the processing of a Chain. 
		/// </summary>
		/// <remarks><p>
		/// When Commands are chained, the output from one Command may be  used 
		/// as input for another Command. If a collaborating Command is 
		/// validating the Criteria for required input, prior to processing, 
		/// then Runtime FieldIDs may be excluded from the set of RequiredIDs. 
		/// </p><p>
		/// The RuntimeIDs are expected to be set on a Chain rather than an individual 
		/// Command. The property is a member of the INexusCommand interface so that 
		/// Command and Chains can observe the substitution principle.
		/// </p></remarks>
		IList RuntimeIDs { get; set; }

		/// <summary>
		/// Add a IList of IDs to the list of {@link RuntimeIds}.
		///</summary>
		IList AddRuntimeIDs { set; }

		/// <summary>
		/// Invoke the business operation.
		/// </summary>
		/// <remarks><p>
		/// Expected to be called from Execute as a casting convenience.
		/// </p></remarks>
		/// <param name="context">Context to process.</param>
		/// 
		bool RequestExecute(IRequestContext context);

	}
}