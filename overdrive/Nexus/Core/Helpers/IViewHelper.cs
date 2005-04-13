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
using Agility.Core;

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// An encapsulation of the IRequestContext that hides some methods and exposes others in a more convenient way. 
	/// </summary>
	/// <remarks>
	/// An IViewHelper implementation may also act as a "front controller" to ensure routine tasks are carried out.
	/// </remarks>
	public interface IViewHelper
	{
		/// <summary>
		/// The IRequestContext we are processing.
		/// </summary>
		IRequestContext Context { get; set; }

		/// <summary>
		/// 
		/// </summary>
		IContext Errors { get; }

		/// <summary>
		/// Return true if errors are queued.
		/// </summary>
		/// <returns>True if errors are queued.</returns>
		bool HasErrors { get; }

		/// <summary>
		/// 
		/// </summary>
		Exception Fault { get; }

		/// <summary>
		/// Return true if an exception is caught.
		/// </summary>
		/// <returns>True if an exception is caught.</returns>
		bool HasFault { get; }

		/// <summary>
		/// Return true if there are no errors or exception pending.
		/// </summary>
		/// <returns>True if all is well.</returns>
		bool IsNominal { get; }
	}

}