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
using System.Collections;

namespace Agility.Core
{
	/// <summary>
	/// A {@link Context} represents the state information that is
	/// accessed and manipulated by the execution of a {@link ICommand} or a
	/// {@link IChain}.  
	/// </summary>
	/// <remarks>
	/// <p>Specialized implementations of {@link IContext} may
	/// add properties that contain typesafe accessors to information that 
	/// is relevant to a particular use case for this context, and/or add 
	/// operations that affect the state information that is saved in the 
	/// context.</p>
	/// 
	/// <p>Implementations of {@link IContext} must also implement all of the
	/// required and optional contracts of the <code>IDictionary</code>
	/// interface.</p>
	///
	/// <p>To protect applications from evolution of this interface, specialized
	/// implementations of {@link Context} should generally be created by extending
	/// the provided base class ({@link Agility.Core.Context})
	/// rather than directly implementing this interface.</p>
	///
	/// <p>Applications should <strong>NOT</strong> assume that
	/// {@link Context} implementations, or the values stored in its
	/// attributes, may be accessed from multiple threads
	/// simultaneously, unless this is explicitly documented for a particular
	/// implementation.</p>
	/// </remarks>
	public interface IContext : IDictionary
	{
	}
}