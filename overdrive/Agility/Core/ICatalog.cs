/*
 * Copyright 2003-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System;
using System.Collections;

namespace Agility.Core
{
	/// <summary>
	/// A {@link ICatalog} is a collection of named {@link ICommand}s (or
	/// {@link IChain}s) that can be used retrieve the set of commands that
	/// should be performed based on a symbolic identifier.  
	/// </summary>
	/// <remarks>
	/// <p>Use of catalogs is optional, but convenient when there are multiple possible 
	/// chains that can be selected and executed based on environmental conditions.</p>
	/// </remarks>
	public interface ICatalog
	{
		/// <summary>
		/// Add a new name and associated {@link ICommand} or {@link IChain}
		/// to the set of named commands known to this {@link ICatalog},
		/// replacing any previous command for that name.
		/// </summary>
		/// <param name="name">Name of the new command</param>
		/// <param name="command">{@link ICommand} or {@link IChain} to be returned</param>
		void AddCommand(String name, ICommand command);

		/// <summary>
		/// Return the {@link ICommand} or {@link IChain} associated with the
		/// specified name, if any; otherwise, return <code>null</code>.
		/// </summary>
		/// <param name="name">Name for which a {@link ICommand} or {@link IChain}
		///  should be retrieved</param>
		ICommand GetCommand(String name);

		/// <summary>
		/// Return an <code>IEnumerator</code> over the set of named commands
		/// known to this {@link ICatalog}.  
		/// </summary>
		/// <remarks>
		/// <p>If there are no known commands, an empty IEnumerator is returned.</p>
		/// </remarks>
		IEnumerator GetNames();

	}
}