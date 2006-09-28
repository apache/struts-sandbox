/*
 * Copyright 1999-2005 The Apache Software Foundation
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
	/// Concrete {@link IIChain} implementation
	/// </summary>
	/// <remarks><p>@version $Revision$ $Date$</p></remarks>
	/// 
	public class Chain : IChain
	{
		#region Constructors

		/// <summary>
		/// Construct a {@link IChain} with no configured {@link ICommand}s.
		/// </summary>
		public Chain()
		{
		}

		/// <summary>
		/// Construct a {@link IChain} configured with the specified {@link ICommand}.
		/// </summary>
		/// <param name="command">The {@link ICommand} to be configured.</param>
		public Chain(ICommand command)
		{
			AddCommand(command);
		}

		/// <summary>
		/// Construct a {@link IChain} configured with the specified {@link ICommand}s.
		/// </summary>
		/// <param name="commands">The {@link ICommand}s to be configured.</param>
		public Chain(ICommand[] commands)
		{
			if (commands == null) // FIXME: Illegal Argument
				throw new Exception();
			for (int i = 0; i < commands.Length; i++) AddCommand(commands[i]);

		}


		/// <summary>
		/// Construct a {@link IChain} configured with the specified {@link ICommand}s.
		/// </summary>
		/// <param name="commands">The {@link ICommand}s to be configured</param>
		public Chain(IList commands) : base()
		{
			AddCommands = commands;
		}

		#endregion

		#region Instance Variables

		/// <summary>
		/// The list of {@link ICommand}s configured for this {@link IChain}, in the order in which they may delegate processing to the remainder of the {@link IChain}.
		/// </summary>
		protected ICommand[] commands = new ICommand[0];

		/// <summary>
		/// Flag indicating whether the configuration of our commands list has been frozen by a call to the <code>execute()</code> method.
		/// </summary>
		protected bool frozen = false;

		#endregion

		#region Chain Methods

		// See interface
		public void AddCommand(ICommand command)
		{
			if (command == null)
				throw new ArgumentNullException("command==null", "Chain.AddCommand");
			if (frozen)
				throw new ApplicationException("Chain.AddCommand: frozen==true");
			ICommand[] results = new ICommand[commands.Length + 1];
			Array.Copy(commands, 0, results, 0, commands.Length);
			results[commands.Length] = command;
			commands = results;

		}

		public IList AddCommands
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException("value==null", "Chain.AddCommands");
				IEnumerator elements = value.GetEnumerator();
				while (elements.MoveNext()) AddCommand(elements.Current as ICommand);
			}
		}


		// See interface
		public bool Execute(IContext context)
		{
			// Verify our parameters
			if (context == null)
				throw new ArgumentNullException("context==null", "Chain.Execute");

			// Freeze the configuration of the command list
			frozen = true;

			// Execute the commands in this list until one returns true
			// or throws an exception
			bool saveResult = false;
			Exception saveException = null;
			int i;
			int n = commands.Length;
			;
			for (i = 0; i < n; i++)
			{
				try
				{
					saveResult = commands[i].Execute(context);
					if (saveResult) break;
				}
				catch (Exception e)
				{
					saveException = e;
					break;
				}
			}

			// Call postprocess methods on Filters in reverse order
			if (i >= n)
			{ // Fell off the end of the chain
				i--;
			}
			bool handled = false;
			bool result;
			for (int j = i; j >= 0; j--)
			{
				if (commands[j] is IFilter)
				{
					try
					{
						result =
							((IFilter) commands[j]).PostProcess(context, saveException);
						if (result)
							handled = true;
					}
					catch (Exception e)
					{
						if (e == null) throw (e); // Silently ignore
					}
				}
			}
			// Return the exception or result state from the last execute()
			if ((saveException != null) && !handled) throw saveException;
			else return (saveResult);
		}

		#endregion

		#region Internal Methods

		/// <summary>
		/// Return an array of the configured {@link ICommand}s for this {@link IChain}.  This method is internal to the assembly and is used only for the unit tests.
		/// </summary>
		/// <returns>An array of the configured {@link ICommand}s for this {@link IChain}</returns>
		public ICommand[] GetCommands()
		{
			return (commands);

		}

		#endregion
	}
}