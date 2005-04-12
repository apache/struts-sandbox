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
using Nexus.Core;
using Spring.Context;

namespace Nexus.Extras.Spring
{
	/// <summary>
	/// Concrete IController implementation using Spring as an IOC container.
	/// </summary>
	public class Controller : IController, IApplicationContextAware
	{
		private IApplicationContext _Factory = null;

		public IApplicationContext ApplicationContext
		{
			get { return _Factory; }
			set { _Factory = value; }
		}

		public Controller ()
		{
		}

		public Controller (IApplicationContext value)
		{
			ApplicationContext = value;
		}

		public IApplicationContext Factory ()
		{
			return _Factory;
		}

		public object GetObject (string name)
		{
			if (null == name)
				throw new ArgumentNullException ("name", "IController.GetObject");
			return Factory ().GetObject (name);
		}

		public IRequestCommand GetCommand (string name)
		{
			if (null == name)
			{
				Exception e = new Exception ("Command name cannot be null.");
				throw(e);
			}
			object o = GetObject (name);
			if (o == null)
			{
				Exception e = new Exception ("Object is not found in Factory.");
				throw(e);
			}
			IRequestCommand command = o as IRequestCommand;
			if (command == null)
			{
				Exception e = new Exception ("Command is not a IRequestCommand or IRequestChain.");
				throw(e);
			}
			return command;
		}

		/// <summary>
		/// Token for Command object.
		/// </summary>
		private string COMMAND_BIN = Tokens.COMMAND_BIN;

		public IRequestContext GetContext (string name)
		{
			IRequestContext context = null;
			try
			{
				IRequestCommand command = GetCommand (name);
				context = command.NewContext ();
				context [COMMAND_BIN] = command;
			}
			catch (Exception e)
			{
				context = new RequestContext ();
				context.Fault = e;
				// TODO: Log exception(faults) (Log all errors in verbose mode?)
				// TODO: Provide an alternate location on fault? -- Declarative exception handing
			}
			return context;
		}

		public void Execute (IRequestContext context)
		{
			if (null == context)
			{
				context = new RequestContext ();
				// TODO: Add a message about null context
			}

			IRequestCommand command = context [COMMAND_BIN] as IRequestCommand;

			if (null == command)
			{
				// TODO: Add a message about null command.
				// (A null context with then have two messages.)
			}
			else
			{
				try
				{
					command.Execute (context);
				}
				catch (Exception e)
				{
					context.Fault = e;
				}
			}
			// TODO: Log exception(faults) (Log all errors in verbose mode?)
			// TODO: Provide an alternate location on fault? -- Declarative exception handing?
		}

		public IRequestContext ExecuteContext (string command)
		{
			IRequestContext context = GetContext (command);
			Execute (context);
			return context;
		}

	}
}