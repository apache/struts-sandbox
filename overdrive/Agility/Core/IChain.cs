using System.Collections;
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

namespace Agility.Core
{
	/// <summary>
	/// A {@link IChain} represents a configured list of
	/// {@link ICommand}s that will be executed in order to perform processing
	/// on a specified {@link IContext}.  
	/// </summary>
	/// <remarks>
	/// <p>Each included {@link ICommand} will be
	/// executed in turn, until either one of them returns <code>true</code>,
	/// one of the executed {@link ICommand}s throws an Exception,
	/// or the end of the chain has been reached.  The {@link IChain} itself will
	/// return the return value of the last {@link ICommand} that was executed
	/// (if no exception was thrown), or rethrow the thrown Exception.</p>
	///
	/// <p>Note that {@link IChain} extends {@link ICommand}, so that the two can
	/// be used interchangeably when a {@link ICommand} is expected.  This makes it
	/// easy to assemble workflows in a hierarchical manner by combining subchains
	/// into an overall processing chain.</p>
	///
	/// <p>To protect applications from evolution of this interface, specialized
	/// implementations of {@link IChain} should generally be created by extending
	/// the provided base class {@link Agility.Core.Chain})
	/// rather than directly implementing this interface.</p>
	///
	/// <p>{@link IChain} implementations should be designed in a thread-safe
	/// manner, suitable for execution on multiple threads simultaneously.  In
	/// general, this implies that the state information identifying which
	/// {@link ICommand} is currently being executed should be maintained in a
	/// local variable inside the <code>Execute</code> method, rather than
	/// in an instance variable.  The {@link ICommand}s in a {@link IChain} may be
	/// configured (via calls to <code>AddCommand</code>) at any time before
	/// the <code>Execute()</code> method of the {@link IChain} is first called.
	/// After that, the configuration of the {@link IChain} is frozen.</p>
	/// </remarks>
	public interface IChain : ICommand
	{
		/// <summary>
		/// Add a {@link ICommand} to the list of {@link ICommand}s that will
		/// be called in turn when this {@link IChain}'s <code>Execute()</code>
		/// method is called.  
		///</summary>
		///<remarks>
		/// <p>Once <code>Execute</code> has been called
		/// at least once, it is no longer possible to add additional
		/// {@link ICommand}s; instead, an Exception will be thrown.</p>
		/// </remarks>
		void AddCommand(ICommand command);

		/// <summary>
		/// Add a IList of {@link ICommand}s to the list of {@link ICommand}s.
		///</summary>
		///<remarks>
		/// Although rendered as a property, this member does add a Command to the list, 
		/// without overwritign any existing commands, unless keys conflicts. 
		/// If key conflict, the last one added wins.
		///</remarks>
		IList AddCommands { set; }

		/// <summary>
		/// Execute the processing represented by this {@link IChain}.
		/// </summary>
		/// <remarks>
		/// <p>Processing uses the following algorithm:</p>
		/// <ul>
		/// <li>If there are no configured {@link ICommand}s in the {@link IChain},
		///     return <code>false</code>.</li>
		/// <li>Call the <code>Execute</code> method of each {@link ICommand}
		///     configured on this chain, in the order they were added via calls
		///     to the <code>AddCommand</code> method, until the end of the
		///     configured {@link ICommand}s is encountered, or until one of
		///     the executed {@link ICommand}s returns <code>true</code>
		///     or throws an Exception.</li>
		/// <li>Walk backwards through the {@link ICommand}s whose
		///     <code>Execute</code> methods, starting with the last one that
		///     was executed.  If this {@link ICommand} instance is also a
		///     {@link IFilter}, call its <code>PostProcess</code> method.
		///     <b>If <code>PostProcess</code> throws an Exception, it is ignored.</b></li>
		/// <li>If the last {@link ICommand} whose <code>Execute</code> method
		///     was called throws an Exception, rethrow that Exception.</li>
		/// <li>Otherwise, return the value returned by the <code>Execute</code>
		///     method of the last {@link ICommand} that was executed.  This will be
		///     <code>true</code> if the last {@link ICommand} indicated that
		///     processing of this {@link IContext} has been completed, or
		///     <code>false</code> if none of the called {@link ICommand}s
		///     returned <code>true</code>.</li>
		/// </ul>
		/// </remarks>
		/// <param name="context">The {@link IContext} to be processed by this
		///  {@link IChain}</param>
		/// <returns><code>true</code> if the processing of this {@link IContext}
		///  is complete, or <code>false</code> if further processing
		///  of this {@link IContext} can be delegated to a subsequent
		///  {@link ICommand} in an enclosing {@link IChain}</returns>
		new bool Execute(IContext context);

	}
}